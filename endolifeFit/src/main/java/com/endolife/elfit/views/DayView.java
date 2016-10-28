package com.endolife.elfit.views;

import android.annotation.SuppressLint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.endolife.elfit.R;
import com.endolife.elfit.database.StepsTrackerDBHelper;
import com.endolife.elfit.models.BarChartTimeEntry;
import com.endolife.elfit.models.DateStepsModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;



public class DayView extends AppCompatActivity implements  HourChart.OnFragmentInteractionListener, DayChart.OnFragmentInteractionListener {

    ArrayList<DateStepsModel> mStepCountList;
    StepsTrackerDBHelper mStepsTrackerDBHelper;
    private static final String TAG = "DayViewActivity";

    protected BarChart barChart;
    protected String[] values;
    protected String dateSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        if (findViewById(R.id.content_day_view) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            DayChart firstFragment = new DayChart();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_day_view, firstFragment).commit();
        }



        /*

        barChart = (BarChart) findViewById(R.id.dbar_chart);

        mStepsTrackerDBHelper = new StepsTrackerDBHelper(this);
        mStepCountList = mStepsTrackerDBHelper.readStepsEntries();

        values = new String[mStepCountList.size()+1];
        List<BarEntry> entriesGroup1 = new ArrayList<>();

        // fill the lists
        int step = 0;
        float sessionStart = 0f;
        for(DateStepsModel data:mStepCountList) {
            values[++step] = data.mDate;
            entriesGroup1.add(new BarEntry(step,data.mStepCount));
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new DayView.MyXAxisValueFormatter(values));
        //XAxis.XAxisPosition pos = XAxis.XAxisPosition.BOTTOM;
        xAxis.setLabelRotationAngle(90);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //enable dragging
        barChart.setDragEnabled(true);


        // enable touch gestures
        barChart.setTouchEnabled(true);

        // enable scaling and dragging
        // mChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        barChart.setPinchZoom(true);

        barChart.setOnChartValueSelectedListener(this);

        BarDataSet set1 = new BarDataSet(entriesGroup1, "Total Steps");
        set1.setColors(new int[]{R.color.walkingColor}, this);

        float groupSpace = 0.04f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.94f; // x2 dataset
        // (0.02 + 0.30) * 3 + 0.04 = 1.00 -> interval per "group"

        BarData data = new BarData(set1);
        data.setBarWidth(barWidth); // set the width of each bar
        barChart.setData(data);
//        barChart.groupBars(sessionStart, groupSpace, barSpace); // perform the "explicit" grouping
        barChart.invalidate(); // refresh

        */
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void replaceFragment(String dateSelected){

        //HourChart fragmentHour = (HourChart) getSupportFragmentManager().findFragmentById(R.id.hourChart);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        HourChart hourChart = HourChart.newInstance(dateSelected,"selected");
        ft.replace(R.id.content_day_view, hourChart);
        ft.addToBackStack(null);
        ft.commit();
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
            //Log.d(TAG, "Formatter : " + mValues[(int) value]);
            return mValues[(int) value];
        }

        /** this is only needed if numbers are returned, else return 0 */
        @Override
        public int getDecimalDigits() { return 0; }
    }



}
