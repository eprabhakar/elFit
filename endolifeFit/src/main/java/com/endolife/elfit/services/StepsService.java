package com.endolife.elfit.services;

/**
 * Created by eprabhakar on 25/9/16.
 */


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import com.endolife.elfit.database.StepsDBHelper;
import android.util.Log;

public class StepsService extends Service implements SensorEventListener {

    SensorManager mSensorManager;
    Sensor mStepDetectorSensor;
    StepsDBHelper mStepsDBHelper;
    private static final String TAG = "StepsService";

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
        {
            mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mStepsDBHelper = new StepsDBHelper(getApplicationContext());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.d(TAG, "About add a step entry in the database ");
        mStepsDBHelper.createStepsEntry();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

}
