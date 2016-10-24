package com.endolife.elfit.database;

/**
 * Created by eprabhakar on 25/9/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.endolife.elfit.models.BarChartTimeEntry;
import com.endolife.elfit.models.DateStepsModel;

import java.util.ArrayList;
import java.util.Calendar;
public class StepsTrackerDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StepsTrackerDatabase";
    private static final String TABLE_STEPS_SUMMARY = "StepsTrackerSummary";
    private static final String ID = "id";
    private static final String STEP_TYPE = "steptype";
    private static final String STEP_TIME = "steptime";//time is in milliseconds Epoch Time
    private static final String STEP_DATE = "stepdate";//Date format is mm/dd/yyyy
    private static final String SESSION_ID = "sessionid";
    private static final String T0DAY_NO = "todayNumber";

    private static final String STEPS_COUNT = "stepscount";
    private static final String MIN_STEP_TIME = "minsteptime";
    private static final String MAX_STEP_TIME = "maxsteptime";

    private static final String TAG = "StepsDBTrackerActivity";


    private static final String CREATE_TABLE_STEPS_SUMMARY = "CREATE TABLE "
            + TABLE_STEPS_SUMMARY + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + STEP_DATE + " TEXT,"+ T0DAY_NO+ " INTEGER," + SESSION_ID + " TEXT,"+ STEP_TIME + " INTEGER," +  STEP_TYPE + " TEXT"+")";

    public boolean createStepsEntry(long timeStamp, int stepType, String sessionId)
    {

        Log.d(TAG, "session in create: " + sessionId);

        boolean createSuccessful = false;
        Calendar mCalendar = Calendar.getInstance();
        String todayDate = String.valueOf(mCalendar.get(Calendar.MONTH)+1)+"/" + String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(mCalendar.get(Calendar.YEAR));
        int todayNumber =mCalendar.get(mCalendar.DAY_OF_YEAR);
        Log.d(TAG, "Today number: " + todayNumber);
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(STEP_TIME, timeStamp);
            values.put(STEP_DATE, todayDate);
            values.put(STEP_TYPE, stepType);
            values.put(SESSION_ID, sessionId);
            values.put(T0DAY_NO,todayNumber);
            long row = db.insert(TABLE_STEPS_SUMMARY, null, values);
            if(row!=-1)
            {
                createSuccessful = true;
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createSuccessful;
    }

    public int [] getStepsByDate(String date)
    {
        int stepType[] = new int[3];
        String selectQuery = "SELECT " + STEP_TYPE + " FROM " + TABLE_STEPS_SUMMARY +" WHERE " + STEP_DATE +" = '"+ date + "'";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    switch(c.getInt((c.getColumnIndex(STEP_TYPE))))
                    {
                        case WALKING: ++stepType[0];
                            break;
                        case JOGGING: ++stepType[1];
                            break;
                        case RUNNING: ++stepType[2];
                            break;
                    }
                } while (c.moveToNext());
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stepType;
    }

    //For Debug Purposes
    public long getTotalStepsDuration()
    {
        long totalDuration = 0;
        ArrayList<String> sessionNameList = new ArrayList<String>();
        ArrayList<Integer> stepTimeSessionList = new ArrayList<Integer>();
        try {
            String selectQuery = "SELECT DISTINCT"+ SESSION_ID +" FROM " + TABLE_STEPS_SUMMARY;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    sessionNameList.add(c.getString((c.getColumnIndex(SESSION_ID))));
                } while (c.moveToNext());
            }

            int sizeSessionNameList = sessionNameList.size();
            for (int i = 0; i < sizeSessionNameList; i++) {

                String selectTimeQuery = "SELECT "+ STEP_TIME +" FROM " + TABLE_STEPS_SUMMARY +" WHERE " + SESSION_ID +" = '"+ sessionNameList.get(i) + "'";
                Cursor cTime = db.rawQuery(selectTimeQuery, null);
                if (cTime.moveToFirst()) {
                    do {
                        stepTimeSessionList.add(cTime.getInt((cTime.getColumnIndex(STEP_TIME))));
                    } while (cTime.moveToNext());
                }
                int sizeStepTimeSessionList = stepTimeSessionList.size();
                for (int j = sizeStepTimeSessionList-1; j == 1; j--) {
                    totalDuration = totalDuration + stepTimeSessionList.get(j) - stepTimeSessionList.get(j-1);
                }
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalDuration;
    }

    //Get sessions and their time durations for each step type on a given day

    public ArrayList<BarChartTimeEntry> getChartTimeEntriesByDate(String date){

        Log.d(TAG, "inside getCharEntries: " + date);

        ArrayList<String> sessionNameList = new ArrayList<String>();
        ArrayList<BarChartTimeEntry> barChartTimeEntries = new ArrayList<BarChartTimeEntry>();
        try {
            String selectQuery = "SELECT DISTINCT "+ SESSION_ID +" FROM " + TABLE_STEPS_SUMMARY + " WHERE " + STEP_DATE + " = \"" + date + "\"" + " ORDER BY " + SESSION_ID;
            Log.d(TAG, "query: " + selectQuery);
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            Log.d(TAG, "count of sessions: " + c.getCount());
            if (c.moveToFirst()) {
                do {
                    Log.d(TAG, "session: " + c.getString((c.getColumnIndex(SESSION_ID))));
                    sessionNameList.add(c.getString((c.getColumnIndex(SESSION_ID))));
                } while (c.moveToNext());
            }

            int sizeSessionNameList = sessionNameList.size();
            Log.d(TAG, "count of sessions xx: " + sessionNameList.get(0));
            int walkingCount = 0;
            int joggingCount = 0;
            int runningCount = 0;

            for (int i = 0; i < sizeSessionNameList; i++) {

                String selectTimeQueryWalking = "SELECT " + SESSION_ID + "," + "count(" + STEP_TYPE + ") " + STEPS_COUNT + " FROM " + TABLE_STEPS_SUMMARY + " WHERE " + SESSION_ID +" = '"+ sessionNameList.get(i) + "'" + "AND " +  STEP_TYPE+" = " +  WALKING + " AND " + STEP_DATE + " = \"" + date + "\"" ;
                String selectTimeQueryJogging = "SELECT " + SESSION_ID + "," + "count(" + STEP_TYPE + ") " + STEPS_COUNT + " FROM " + TABLE_STEPS_SUMMARY + " WHERE " + SESSION_ID +" = '"+ sessionNameList.get(i) + "'" + "AND " +  STEP_TYPE+" = " + JOGGING + " AND " + STEP_DATE + " = \"" + date + "\"" ;
                String selectTimeQueryRunning = "SELECT " + SESSION_ID + "," + "count(" + STEP_TYPE + ") " + STEPS_COUNT + " FROM " + TABLE_STEPS_SUMMARY + " WHERE " + SESSION_ID +" = '"+ sessionNameList.get(i) + "'" + "AND " +  STEP_TYPE+" = " + RUNNING + " AND " + STEP_DATE + " = \"" + date + "\"" ;

               // String selectTimeQueryRunning = "SELECT MIN("+ STEP_TIME + ") " + MIN_STEP_TIME + " , MAX(" + STEP_TIME +") "+ MAX_STEP_TIME+ " FROM " + TABLE_STEPS_SUMMARY +" WHERE " + SESSION_ID +" = '"+ sessionNameList.get(i) + "'" + "AND " +  STEP_TYPE+" = " + RUNNING;

                Cursor cTime = db.rawQuery(selectTimeQueryWalking, null);
                if(cTime.getCount() == 0){
                    //we need to initialize the list with empty data
                    BarChartTimeEntry barChartTimeEntry = new BarChartTimeEntry();
                    barChartTimeEntry.sessionId = sessionNameList.get(i);
                    barChartTimeEntry.timeDuration = 0;
                    barChartTimeEntry.type = WALKING;
                    barChartTimeEntries.add(barChartTimeEntry);
                } else if (cTime.moveToFirst()) {
                    do {
                        BarChartTimeEntry barChartTimeEntry = new BarChartTimeEntry();
                        barChartTimeEntry.sessionId = sessionNameList.get(i);
                        walkingCount = cTime.getInt((cTime.getColumnIndex(STEPS_COUNT)));
                        Log.d(TAG, "Walking count: " + walkingCount + "- Session Id : " + barChartTimeEntry.sessionId);
                        //timeDuration in minutes
                        barChartTimeEntry.timeDuration = (walkingCount*1.0f)  / 60;
                        Log.d(TAG, "Time duration: " + barChartTimeEntry.timeDuration);
                        barChartTimeEntry.type = WALKING;
                        barChartTimeEntries.add(barChartTimeEntry);
                        break;
                    } while (cTime.moveToNext());
                }
                walkingCount = 0;
                cTime.close();

                cTime = db.rawQuery(selectTimeQueryJogging, null);
                if(cTime.getCount() == 0){
                    //we need to initialize the list with empty data
                    BarChartTimeEntry barChartTimeEntry = new BarChartTimeEntry();
                    barChartTimeEntry.sessionId = sessionNameList.get(i);
                    barChartTimeEntry.timeDuration = 0;
                    barChartTimeEntry.type = JOGGING;
                    barChartTimeEntries.add(barChartTimeEntry);
                } else if (cTime.moveToFirst()) {
                    do {
                        BarChartTimeEntry barChartTimeEntry = new BarChartTimeEntry();
                        barChartTimeEntry.sessionId = sessionNameList.get(i);
                        joggingCount = cTime.getInt((cTime.getColumnIndex(STEPS_COUNT)));
                        Log.d(TAG, "Jogging count: " + joggingCount + "- Session Id : " + barChartTimeEntry.sessionId);
                        barChartTimeEntry.timeDuration = (joggingCount*0.7f) / 60;
                        barChartTimeEntry.type = JOGGING;
                        barChartTimeEntries.add(barChartTimeEntry);
                        break;
                    } while (cTime.moveToNext());
                }
                joggingCount=0;
                cTime.close();

                cTime = db.rawQuery(selectTimeQueryRunning, null);
                if(cTime.getCount() == 0){
                    //we need to initialize the list with empty data
                    BarChartTimeEntry barChartTimeEntry = new BarChartTimeEntry();
                    barChartTimeEntry.sessionId = sessionNameList.get(i);
                    barChartTimeEntry.timeDuration = 0;
                    barChartTimeEntry.type = RUNNING;
                    barChartTimeEntries.add(barChartTimeEntry);
                }else if (cTime.moveToFirst()) {
                    do {
                        BarChartTimeEntry barChartTimeEntry = new BarChartTimeEntry();
                        barChartTimeEntry.sessionId = sessionNameList.get(i);
                        runningCount = cTime.getInt((cTime.getColumnIndex(STEPS_COUNT)));
                        Log.d(TAG, "Running count: " + runningCount + "- Session Id : " + barChartTimeEntry.sessionId);
                        barChartTimeEntry.timeDuration = (runningCount*0.4f) / 60;
                        barChartTimeEntry.type = RUNNING;
                        barChartTimeEntries.add(barChartTimeEntry);
                        break;
                    } while (cTime.moveToNext());
                }
                runningCount=0;
                cTime.close();

            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return barChartTimeEntries;
    }

    //For Debug Purposes
    public ArrayList<String> getAvailableDates()
    {
        ArrayList<String> dateList = new ArrayList<String>();
        try {
            String selectQuery = "SELECT DISTINCT"+ STEP_DATE +" FROM " + TABLE_STEPS_SUMMARY;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    dateList.add(c.getString((c.getColumnIndex(STEP_DATE))));
                } while (c.moveToNext());
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateList;
    }

    private static final int DATABASE_VERSION = 1;
    private static final int RUNNING = 3;
    private static final int JOGGING = 2;
    private static final int WALKING = 1;

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_STEPS_SUMMARY);
        this.onCreate(db);
    }

    public StepsTrackerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate called:");
        db.execSQL(CREATE_TABLE_STEPS_SUMMARY);

    }

    public ArrayList<DateStepsModel> readStepsEntries() {

        ArrayList<DateStepsModel> mStepCountList = new ArrayList<DateStepsModel>();
        String selectQuery = "SELECT " +STEP_DATE + "," + "count(" + STEP_TYPE + ") " + STEPS_COUNT + " FROM " + TABLE_STEPS_SUMMARY + " GROUP BY " + STEP_DATE;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    DateStepsModel mDateStepsModel = new DateStepsModel();
                    mDateStepsModel.mDate = c.getString((c.getColumnIndex(STEP_DATE)));
                    mDateStepsModel.mStepCount = c.getInt((c.getColumnIndex(STEPS_COUNT)));
                    mStepCountList.add(mDateStepsModel);
                } while (c.moveToNext());
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mStepCountList;
    }

}
