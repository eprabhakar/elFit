package com.endolife.elfit.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import com.endolife.elfit.database.StepsTrackerDBHelper;
import com.endolife.elfit.services.StepsTrackerService;

import java.util.Calendar;



import com.endolife.elfit.R;

public class CustomAlgoResultsActivity extends AppCompatActivity {
    private TextView mTotalStepsTextView;
    private TextView mTotalDistanceTextView;
    private TextView mTotalDurationTextView;
    private TextView mAverageSpeedTextView;
    private TextView mAveragFrequencyTextView;
    private TextView mTotalCalorieBurnedTextView;
    private TextView mPhysicalActivityTypeTextView;
    private Intent mStepsAnalysisIntent;
    StepsTrackerDBHelper mStepsTrackerDBHelper;

    private static final String TAG = "CustomAlgoResActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_algo_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mStepsTrackerDBHelper = new StepsTrackerDBHelper(this);

        mTotalStepsTextView = (TextView)findViewById(R.id.total_steps);
        mTotalDistanceTextView = (TextView)findViewById(R.id.total_distance);
        mTotalDurationTextView = (TextView)findViewById(R.id.total_duration);
        mAverageSpeedTextView = (TextView)findViewById(R.id.average_speed);
        mAveragFrequencyTextView = (TextView)findViewById(R.id.average_frequency);
        mTotalCalorieBurnedTextView = (TextView)findViewById(R.id.calories_burned);
        mPhysicalActivityTypeTextView = (TextView)findViewById(R.id.physical_activitytype);

        mStepsAnalysisIntent = new Intent(getApplicationContext(), StepsTrackerService.class);
        Log.d(TAG, "On create starting the service");
        startService(mStepsAnalysisIntent);

        calculateDataMatrix();


    }

    public void calculateDataMatrix() {

        Calendar calendar = Calendar.getInstance();
        String todayDate = String.valueOf(calendar.get(Calendar.MONTH)+1)+"/" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.YEAR));
        int stepType[] = mStepsTrackerDBHelper.getStepsByDate(todayDate);
        int walkingSteps = stepType[0];
        int joggingSteps = stepType[1];
        int runningSteps = stepType[2];

        //Calculating total steps
        int totalStepTaken = walkingSteps + joggingSteps + runningSteps;
        mTotalStepsTextView.setText(String.valueOf(totalStepTaken)+ " Steps");

        //Calculating total distance traveled
        float totalDistance = walkingSteps*0.5f + joggingSteps * 1.0f + runningSteps * 1.5f;
        mTotalDistanceTextView.setText(String.valueOf(totalDistance)+" meters");

        //Calculating total duration
        float totalDuration = walkingSteps*1.0f + joggingSteps * 0.7f + runningSteps * 0.4f;
        float hours = totalDuration / 3600;
        float minutes = (totalDuration % 3600) / 60;
        float seconds = totalDuration % 60;
        mTotalDurationTextView.setText(String.format("%.0f",hours) + " hrs " +  String.format("%.0f",minutes) + " mins " +  String.format("%.0f",seconds)+ " secs");

        //Calculating average speed
        if(totalDistance>0) {
            mAverageSpeedTextView.setText(String.format("%.2f", totalDistance/totalDuration)+" meter per seconds");
        } else {
            mAverageSpeedTextView.setText("0 meter per seconds");
        }

        //Calculating average step frequency
        if(totalStepTaken>0) {
            mAveragFrequencyTextView.setText(String.format("%.0f",totalStepTaken/minutes)+" steps per minute");
        } else {
            mAveragFrequencyTextView.setText("0 steps per minute");
        }

        //Calculating total calories burned
        float totalCaloriesBurned = walkingSteps * 0.05f + joggingSteps * 0.1f + runningSteps * 0.2f;
        mTotalCalorieBurnedTextView.setText(String.format("%.0f",totalCaloriesBurned)+" Calories");

        //Calculating type of physical activity
        mPhysicalActivityTypeTextView.setText(String.valueOf(walkingSteps) + " Walking Steps " +  "\n"+String.valueOf(joggingSteps) + " Jogging Steps " +
                "\n"+String.valueOf(runningSteps)+ " Running Steps");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "we are in on back pressed event");
        //stopService(mStepsAnalysisIntent);
        super.onBackPressed();
    }

    @Override
    public void onPause(){
        Log.d(TAG, "On pause called");
        //stopService(mStepsAnalysisIntent);
        super.onPause();
    }

    @Override
    public void onResume(){
        Log.d(TAG, "On resume called");
        //startService(mStepsAnalysisIntent);
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.d(TAG, "On destroy called");
        //stopService(mStepsAnalysisIntent);
        //finish();

    }

}
