package com.endolife.elfit.views;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.endolife.elfit.R;
import com.endolife.elfit.database.StepsTrackerDBHelper;
import com.endolife.elfit.models.DateStepsModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;


import java.util.ArrayList;
import java.util.List;

public class TimeLine extends AppCompatActivity {

    ArrayList<DateStepsModel> mStepCountList;
    StepsTrackerDBHelper mStepsTrackerDBHelper;
    private static final String TAG = "TimeLineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(TAG, "In the oncreate:");

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LineChart chart = (LineChart) findViewById(R.id.line_chart);
        mStepsTrackerDBHelper = new StepsTrackerDBHelper(this);
        mStepCountList = mStepsTrackerDBHelper.readStepsEntries();

        String[] values = new String[mStepCountList.size()+1];

        List<Entry> entries = new ArrayList<Entry>();
        int step = 0;
        Log.d(TAG, "length of step count" + mStepCountList.size());
        for (DateStepsModel data : mStepCountList) {
            // turn your data into Entry objects
            //Log.d(TAG, "step date: " + data.mDate);
            values[++step] = data.mDate;
            entries.add(new Entry((float)step, (float)data.mStepCount));
        }

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
        //XAxis.XAxisPosition pos = XAxis.XAxisPosition.BOTTOM;
        xAxis.setLabelRotationAngle(90);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        if(entries.size() > 1) {

            LineDataSet dataSet = new LineDataSet(entries, "Steps Date wise");
            // int fillColor = FF4081;
            dataSet.setFillColor(Color.parseColor("#ffadb4"));
            //dataSet.setFillAlpha(60);
            dataSet.setDrawFilled(true);
            LineData lineData = new LineData(dataSet);

            chart.setData(lineData);
            chart.invalidate(); // refresh
        }

    }

    public class MyXAxisValueFormatter implements AxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            Log.d(TAG, "inside formatter constructer:");
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
//            Log.d(TAG, "Formatter : " + mValues[(int) value]);
            return mValues[(int) value];
        }

        /** this is only needed if numbers are returned, else return 0 */
        @Override
        public int getDecimalDigits() { return 0; }
    }


}
