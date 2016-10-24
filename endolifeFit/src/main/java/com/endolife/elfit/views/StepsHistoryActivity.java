package com.endolife.elfit.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.endolife.elfit.R;

import com.endolife.elfit.adapters.ListAdapter;
import com.endolife.elfit.database.StepsDBHelper;
import com.endolife.elfit.database.StepsTrackerDBHelper;
import com.endolife.elfit.models.DateStepsModel;
import com.endolife.elfit.services.StepsService;

import java.util.ArrayList;
import java.util.Calendar;

public class StepsHistoryActivity extends AppCompatActivity {


    StepsDBHelper mStepsDBHelper;
    ListView mSensorListView;
    ListAdapter mListAdapter;
    Intent mStepsIntent;
    ArrayList<DateStepsModel> mStepCountList;
    private static final String TAG = "StepsHistoryActivity";

    private RecyclerView mStepsListView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_history);
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

        mStepsListView = (RecyclerView)findViewById(R.id.steps_list);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mStepsListView.setLayoutManager(mLayoutManager);


        Log.d(TAG, "we are in oncreate of custom algo history activity");
        getDataForList();

        mAdapter = new ListAdapter(mStepCountList, this);
        mStepsListView.setAdapter(mAdapter);

    }

    public void getDataForList()
    {
        mStepsDBHelper = new StepsDBHelper(this);
        mStepCountList = mStepsDBHelper.readStepsEntries();
    }

}
