package com.endolife.elfit.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.endolife.elfit.R;

//import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.endolife.elfit.adapters.SListAdapter;

import java.util.List;


public class CheckSensorsActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    //private Sensor mSensor;
    //ListView mSensorListView;
    SListAdapter mListAdapter;
    List<Sensor> mSensorsList;
    Context context;

    private RecyclerView mSensorListView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView mSensorsCountView;

    private static final String TAG = "CheckSensorsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sensors);
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

        //mSensorsCountView = (TextView)findViewById(R.id.sensorcount);

        mSensorListView = (RecyclerView) findViewById(R.id.sensors_list);
        Log.d(TAG, "We are in oncreate of check sensors");

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mSensorListView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mSensorListView.setLayoutManager(mLayoutManager);



        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensorsList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor element : mSensorsList) {
            // 1 - can call methods of element
            Log.d(TAG, "list of avaialble sensors :" + element.toString());
            // ...
        };

        mAdapter = new SListAdapter(this, mSensorsList);
        mSensorListView.setAdapter(mAdapter);
        //mSensorsCountView.setText(String.valueOf(mSensorsList.size()));
    }

}
