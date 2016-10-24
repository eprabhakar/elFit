package com.endolife.elfit.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.endolife.elfit.R;
import com.endolife.elfit.database.StepsTrackerDBHelper;
import com.endolife.elfit.models.BarChartTimeEntry;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class TimeChart extends AppCompatActivity implements OnChartGestureListener{

    ArrayList<BarChartTimeEntry> mTimeEntriesList;
    StepsTrackerDBHelper mStepsTrackerDBHelper;
    private static final String TAG = "TimeChartActivity";
    private static final int RUNNING = 3;
    private static final int JOGGING = 2;
    private static final int WALKING = 1;
    protected String todayDate;
    protected int todayNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_chart);
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

        Log.d(TAG, "In the oncreate:");
        this.createChart(); //Old way of displaying all three walking types


    }

    private void createChartWithType(){
        BarChart barChart = (BarChart) findViewById(R.id.bar_chart);
        Calendar mCalendar = Calendar.getInstance();
        TimeZone timezone = TimeZone.getDefault();
        mCalendar.setTimeZone(timezone);
        todayDate = String.valueOf(mCalendar.get(Calendar.MONTH)+1)+"/" + String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(mCalendar.get(Calendar.YEAR));
        todayNumber = mCalendar.get(mCalendar.DAY_OF_YEAR);
        Log.d(TAG, "today date:" + todayDate);

        mStepsTrackerDBHelper = new StepsTrackerDBHelper(this);
        mTimeEntriesList = mStepsTrackerDBHelper.getChartTimeEntriesByDate(todayDate);

        String[] values = new String[mTimeEntriesList.size()+1];
        Log.d(TAG, "size of mTimeEntriesList: " + values.length);

        List<BarEntry> entriesGroup1 = new ArrayList<>();
        List<BarEntry> entriesGroup2 = new ArrayList<>();
        List<BarEntry> entriesGroup3 = new ArrayList<>();

        // fill the lists
        int step = 0;
        float sessionStart = 0f;
        for(BarChartTimeEntry data:mTimeEntriesList) {
            values[++step] = data.sessionId;
            //Log.d(TAG, "Session id :" + data.sessionId);
            // if((step == 1) && (Integer.parseInt(data.sessionId)>0)) sessionStart = Integer.parseInt(data.sessionId);
            if(data.type == WALKING) {
                entriesGroup1.add(new BarEntry(step, data.timeDuration));
            } else if (data.type == JOGGING){
                entriesGroup2.add(new BarEntry(step, data.timeDuration));
            } else if (data.type == RUNNING) {
                entriesGroup3.add(new BarEntry(step, data.timeDuration));
            }
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new TimeChart.MyXAxisValueFormatter(values));
        //XAxis.XAxisPosition pos = XAxis.XAxisPosition.BOTTOM;
        xAxis.setLabelRotationAngle(90);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //enable dragging
        barChart.setDragEnabled(true);


        // enable touch gestures
        //barChart.setTouchEnabled(true);

        // enable scaling and dragging
        // mChart.setDragEnabled(true);
        //barChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        barChart.setPinchZoom(true);

        BarDataSet set1 = new BarDataSet(entriesGroup1, "Walking");
        BarDataSet set2 = new BarDataSet(entriesGroup2, "Jogging");
        BarDataSet set3 = new BarDataSet(entriesGroup3, "Running");



        //set colors
        set1.setColors(new int[]{R.color.walkingColor}, this);
        set2.setColors(new int[]{R.color.joggingColor}, this);
        set3.setColors(new int[]{R.color.runningColor}, this);

        float groupSpace = 0.04f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.40f; // x2 dataset
        // (0.02 + 0.30) * 3 + 0.04 = 1.00 -> interval per "group"

        BarData data = new BarData(set1, set2, set3);
        data.setBarWidth(barWidth); // set the width of each bar
        //set the listner for gesture
        barChart.setOnChartGestureListener(this);

        barChart.setData(data);
        // barChart.groupBars(sessionStart, groupSpace, barSpace); // perform the "explicit" grouping
        barChart.invalidate(); // refresh
    }

    private void createChart(){

        BarChart barChart = (BarChart) findViewById(R.id.bar_chart);
        Calendar mCalendar = Calendar.getInstance();
        TimeZone timezone = TimeZone.getDefault();
        mCalendar.setTimeZone(timezone);
        todayDate = String.valueOf(mCalendar.get(Calendar.MONTH)+1)+"/" + String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(mCalendar.get(Calendar.YEAR));
        todayNumber = mCalendar.get(mCalendar.DAY_OF_YEAR);
        Log.d(TAG, "today date:" + todayDate);

        mStepsTrackerDBHelper = new StepsTrackerDBHelper(this);
        mTimeEntriesList = mStepsTrackerDBHelper.getChartTimeEntriesByDate(todayDate);

        String[] values = new String[mTimeEntriesList.size()+1];
        Log.d(TAG, "size of mTimeEntriesList: " + values.length);

        List<BarEntry> entriesGroup1 = new ArrayList<>();
        List<BarEntry> entriesGroup2 = new ArrayList<>();
        List<BarEntry> entriesGroup3 = new ArrayList<>();

        // fill the lists
        int step = 0;
        float sessionStart = 0f;
        for(BarChartTimeEntry data:mTimeEntriesList) {
            values[++step] = data.sessionId;
            //Log.d(TAG, "Session id :" + data.sessionId);
            // if((step == 1) && (Integer.parseInt(data.sessionId)>0)) sessionStart = Integer.parseInt(data.sessionId);
            if(data.type == WALKING) {
                entriesGroup1.add(new BarEntry(step, data.timeDuration));
            } else if (data.type == JOGGING){
                entriesGroup2.add(new BarEntry(step, data.timeDuration));
            } else if (data.type == RUNNING) {
                entriesGroup3.add(new BarEntry(step, data.timeDuration));
            }
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new TimeChart.MyXAxisValueFormatter(values));
        //XAxis.XAxisPosition pos = XAxis.XAxisPosition.BOTTOM;
        xAxis.setLabelRotationAngle(90);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //enable dragging
        barChart.setDragEnabled(true);


        // enable touch gestures
        //barChart.setTouchEnabled(true);

        // enable scaling and dragging
        // mChart.setDragEnabled(true);
        //barChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        barChart.setPinchZoom(true);

        BarDataSet set1 = new BarDataSet(entriesGroup1, "Walking");
        BarDataSet set2 = new BarDataSet(entriesGroup2, "Jogging");
        BarDataSet set3 = new BarDataSet(entriesGroup3, "Running");



        //set colors
        set1.setColors(new int[]{R.color.walkingColor}, this);
        set2.setColors(new int[]{R.color.joggingColor}, this);
        set3.setColors(new int[]{R.color.runningColor}, this);

        float groupSpace = 0.04f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.40f; // x2 dataset
        // (0.02 + 0.30) * 3 + 0.04 = 1.00 -> interval per "group"

        BarData data = new BarData(set1, set2, set3);
        data.setBarWidth(barWidth); // set the width of each bar
        //set the listner for gesture
        barChart.setOnChartGestureListener(this);

        barChart.setData(data);
        // barChart.groupBars(sessionStart, groupSpace, barSpace); // perform the "explicit" grouping
        barChart.invalidate(); // refresh

    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
           // Log.d(TAG, "inside formatter constructer:");
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



    /**
     * Callbacks when a touch-gesture has started on the chart (ACTION_DOWN)
     *
     * @param me
     * @param lastPerformedGesture
     */
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture){
        Log.d(TAG, "Gesture start: " + me.toString() + " gesture: " +lastPerformedGesture.toString());
    };

    /**
     * Callbacks when a touch-gesture has ended on the chart (ACTION_UP, ACTION_CANCEL)
     *
     * @param me
     * @param lastPerformedGesture
     */
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture){
        Log.d(TAG, "Guesture end: " + me.toString() + " gesture: " +lastPerformedGesture.toString());
    };

    /**
     * Callbacks when the chart is longpressed.
     *
     * @param me
     */
    public void onChartLongPressed(MotionEvent me){
        Log.d(TAG, "Long Pressed: " + me.toString() );
    };

    /**
     * Callbacks when the chart is double-tapped.
     *
     * @param me
     */
    public void onChartDoubleTapped(MotionEvent me){
        Log.d(TAG, "Double tapped: " + me.toString() );
    };

    /**
     * Callbacks when the chart is single-tapped.
     *
     * @param me
     */
    public void onChartSingleTapped(MotionEvent me){
        Log.d(TAG, "Single Tapped: " + me.toString() );
    };

    /**
     * Callbacks then a fling gesture is made on the chart.
     *
     * @param me1
     * @param me2
     * @param velocityX
     * @param velocityY
     */
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY){};

    /**
     * Callbacks when the chart is scaled / zoomed via pinch zoom gesture.
     *
     * @param me
     * @param scaleX scalefactor on the x-axis
     * @param scaleY scalefactor on the y-axis
     */
    public void onChartScale(MotionEvent me, float scaleX, float scaleY){};

    /**
     * Callbacks when the chart is moved / translated via drag gesture.
     *
     * @param me
     * @param dX translation distance on the x-axis
     * @param dY translation distance on the y-axis
     */
    public void onChartTranslate(MotionEvent me, float dX, float dY){

        Log.d(TAG, "Chart moved: " + me.toString() + " dx: " +dY + " dy: "+ dY);
    };
}
