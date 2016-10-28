package com.endolife.elfit.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.endolife.elfit.database.StepsTrackerDBHelper;
import com.endolife.elfit.models.AccelerometerData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StepsTrackerService extends Service {

    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private Sensor mAccelerometerSensor;
    private AccelerometerListener mAccelerometerListener;
    private StepDetectorListener mStepDetectorListener;
    private StepsTrackerDBHelper mStepsTrackerDBHelper;
    private SimpleDateFormat formatter;


    private static final int WALKINGPEAK = 16;
    private static final int JOGGINGPEAK = 23;
    private static final int RUNNINGPEAK = 30;
    private static final int RUNNING = 3;
    private static final int JOGGING = 2;
    private static final int WALKING = 1;
    private static final String TAG = "StepsTrackerService";


    private void startAccelerometer(){
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccelerometerListener = new AccelerometerListener();
        mSensorManager.registerListener(mAccelerometerListener, mAccelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);

        isAccelerometerRegistered = true;
    }


    private String getCurrentSessionId(){
        Date date = Calendar.getInstance().getTime();
        String sessionId = formatter.format(date);

        Log.d(TAG, "session ID: " + sessionId);
        return sessionId;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
        {
            mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mStepDetectorListener = new StepDetectorListener();
            mSensorManager.registerListener(mStepDetectorListener, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
        {
            startAccelerometer();

        } else{
            Log.d(TAG, "Unable to get accelerometer sensor ");

        }

        formatter = new SimpleDateFormat("HH");
        String timezoneID = TimeZone.getDefault().getID();
        formatter.setTimeZone(TimeZone.getTimeZone(timezoneID));
        Log.d(TAG, " On create called: " + timezoneID.toString());
        mStepsTrackerDBHelper = new StepsTrackerDBHelper(this);
    }


    ScheduledExecutorService mScheduledExecutorService = Executors.newScheduledThreadPool(2);
    private ScheduledFuture mScheduledUnregisterAccelerometerTask;
    private ScheduledFuture mScheduledProcessDataTask;
    private UnregisterAcceleromterTask mUnregisterAcceleromterTask;
    //private RegisterAccelerometerTask mRegisterAccelerometerTask;
    private ProcessDataTask mProcessDataTask;
    //private SessionTimerTask mSessionTimerTask;
    private boolean isScheduleUnregistered = false;
    private boolean isAccelerometerRegistered = false;
    private String sessionId;


    class StepDetectorListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {

            if(!isAccelerometerRegistered && mAccelerometerSensor!=null)
            {
                Log.d(TAG, "about to register a new Accelerometer sensor listner");
                mAccelerometerListener = new AccelerometerListener();
                mSensorManager.registerListener(mAccelerometerListener, mAccelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
                isAccelerometerRegistered = true;
            }
            if(isScheduleUnregistered)
            {
                mScheduledUnregisterAccelerometerTask.cancel(true);
            }
            mUnregisterAcceleromterTask = new UnregisterAcceleromterTask();
            mScheduledUnregisterAccelerometerTask = mScheduledExecutorService.schedule(mUnregisterAcceleromterTask, 20000, TimeUnit.MILLISECONDS);
            isScheduleUnregistered = true;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }

    // --end of the new funcition

    class RegisterAccelerometerTask implements Runnable {
        @Override
        public void run() {
            startAccelerometer();
        }

    }


    class UnregisterAcceleromterTask implements Runnable {

        @Override
        public void run() {
            isAccelerometerRegistered = false;
            mSensorManager.unregisterListener(mAccelerometerListener);
            isScheduleUnregistered = false;
            mScheduledProcessDataTask.cancel(false);
        }
    }


    private long timeOffsetValue;
    ArrayList<AccelerometerData> mAccelerometerDataList = new ArrayList<AccelerometerData>();
    ArrayList<AccelerometerData> mRawDataList = new ArrayList<AccelerometerData>();
    ArrayList<AccelerometerData> mAboveThresholdValuesList = new ArrayList<AccelerometerData>();
    ArrayList<AccelerometerData> mHighestPeakList = new ArrayList<AccelerometerData>();

    class AccelerometerListener implements SensorEventListener{

        public AccelerometerListener()
        {
            Log.d(TAG, "Finished new Accelerometer sensor listner");
            mProcessDataTask = new ProcessDataTask();
            mScheduledProcessDataTask = mScheduledExecutorService.scheduleWithFixedDelay(mProcessDataTask, 10000, 10000, TimeUnit.MILLISECONDS);
            //mSessionTimerTask = new SessionTimerTask();
            //mScheduledSessionTimerTask = mScheduledExecutorService.scheduleWithFixedDelay(mSessionTimerTask, 3600000, 3600000, TimeUnit.MILLISECONDS);

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            //Log.d(TAG, "we have a sesor changed event on accelerometer");
            AccelerometerData mAccelerometerData = new AccelerometerData();
            mAccelerometerData.x = event.values[0];
            mAccelerometerData.y = event.values[1];
            mAccelerometerData.z = event.values[2];
            mAccelerometerData.time = event.timestamp;
            mAccelerometerDataList.add(mAccelerometerData);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }

    class ProcessDataTask implements Runnable {

        @Override
        public void run() {

            //Copy accelerometer data from main sensor array in separate array for processing
            mRawDataList.addAll(mAccelerometerDataList);
            mAccelerometerDataList.clear();

            //Calculating the magnitude (Square root of sum of squares of x, y, z) & converting time from nano seconds from boot time to epoc time
            timeOffsetValue = System.currentTimeMillis() - SystemClock.elapsedRealtime();
            int dataSize = mRawDataList.size();
            for (int i = 0; i < dataSize; i++) {

                mRawDataList.get(i).value = Math.sqrt(Math.pow(mRawDataList.get(i).x, 2) + Math.pow(mRawDataList.get(i).y, 2) + Math.pow(mRawDataList.get(i).z, 2));
                mRawDataList.get(i).time = (mRawDataList.get(i).time/1000000L) + timeOffsetValue;
            }

            //Calculating the High Peaks
            findHighPeaks();
            //Remove high peaks close to each other which are within range of 0.4 seconds
            removeClosePeaks();
            //Find the type of step (Running, jogging, walking) & store in Database
            findStepTypeAndStoreInDB();

            mRawDataList.clear();
            mAboveThresholdValuesList.clear();
            mHighestPeakList.clear();
        }

        public void findHighPeaks()
        {
            //Calculating the High Peaks
            boolean isAboveMeanLastValueTrue = false;
            int dataSize = mRawDataList.size();
            for (int i = 0; i < dataSize; i++)
            {
                if(mRawDataList.get(i).value > WALKINGPEAK)
                {
                    mAboveThresholdValuesList.add(mRawDataList.get(i));
                    isAboveMeanLastValueTrue = false;
                }
                else
                {
                    if(!isAboveMeanLastValueTrue && mAboveThresholdValuesList.size()>0)
                    {
                        Collections.sort(mAboveThresholdValuesList,new DataSorter());
                        mHighestPeakList.add(mAboveThresholdValuesList.get(mAboveThresholdValuesList.size()-1));
                        mAboveThresholdValuesList.clear();
                    }
                    isAboveMeanLastValueTrue = true;
                }
            }

        }

        public void removeClosePeaks()
        {
            for (int i = 0; i < mHighestPeakList.size()-1; i++) {

                if(mHighestPeakList.get(i).isRealPeak)
                {
                    if(mHighestPeakList.get(i+1).time - mHighestPeakList.get(i).time < 400)
                    {
                        if(mHighestPeakList.get(i+1).value > mHighestPeakList.get(i).value)
                        {
                            mHighestPeakList.get(i).isRealPeak = false;
                        }
                        else
                        {
                            mHighestPeakList.get(i+1).isRealPeak = false;
                        }
                    }
                }
            }
        }

        public void findStepTypeAndStoreInDB()
        {

            int size = mHighestPeakList.size();
            String sessionId = getCurrentSessionId();
            for (int i = 0; i < size; i++)
            {
                if(mHighestPeakList.get(i).isRealPeak)
                {
                    if(mHighestPeakList.get(i).value > RUNNINGPEAK)
                    {
                        mStepsTrackerDBHelper.createStepsEntry(mHighestPeakList.get(i).time, RUNNING,sessionId);
                    }
                    else
                    {
                        if(mHighestPeakList.get(i).value > JOGGINGPEAK)
                        {
                            mStepsTrackerDBHelper.createStepsEntry(mHighestPeakList.get(i).time, JOGGING,sessionId);
                        }
                        else
                        {
                            mStepsTrackerDBHelper.createStepsEntry(mHighestPeakList.get(i).time, WALKING,sessionId);
                        }
                    }
                }
            }
        }

        public class DataSorter implements Comparator<AccelerometerData>{

            public int compare(AccelerometerData obj1, AccelerometerData obj2){
                int returnVal = 0;

                if(obj1.value < obj2.value){
                    returnVal =  -1;
                }else if(obj1.value > obj2.value){
                    returnVal =  1;
                }
                return returnVal;
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        //super.onDestroy();
        Log.d(TAG, "in onDestroy event");
        mUnregisterAcceleromterTask = new UnregisterAcceleromterTask();
        mScheduledUnregisterAccelerometerTask = mScheduledExecutorService.schedule(mUnregisterAcceleromterTask, 1000, TimeUnit.MILLISECONDS);
        isScheduleUnregistered = true;
        //mScheduledExecutorService.shutdown();
        /*
        //If for some reason the service is stopped, wake it up again with a 10 minute delay
        mRegisterAccelerometerTask = new RegisterAccelerometerTask();
        mScheduledAccelerometerTask = mScheduledExecutorService.schedule(mRegisterAccelerometerTask, 60000,TimeUnit.MILLISECONDS);
        Log.d(TAG, "after service start after 10 minutes");
        */
        //We send a broadcast message to start the service again
        Intent intent = new Intent("com.endolife.accelaction");
        //We dont have to pass any values through putExtra now
        // intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);

    }

}
