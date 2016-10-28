package com.endolife.elfit.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.endolife.elfit.R;
import com.endolife.elfit.custom.XYMarkerView;
import com.endolife.elfit.database.StepsTrackerDBHelper;
import com.endolife.elfit.models.BarChartTimeEntry;
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
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private ViewPortHandler mHandler;
    private int currentDay;
    private int todayDay;
    private BarChart barChart;

    private float motionStartX = 0f;
    private float motionEndX = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_chart);
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

        Log.d(TAG, "In the oncreate:");
        this.initBar();
        try {
            this.loadChartByDay(todayDay); //Old way of displaying all three walking types
        }catch (Exception e){
            Log.d(TAG, "throw parse exception" + e.toString());
        }

    }

    private void initBar(){
        barChart = (BarChart) findViewById(R.id.bar_chart);
        barChart.setDescription("");
        Calendar mCalendar = Calendar.getInstance();
        TimeZone timezone = TimeZone.getDefault();
        mCalendar.setTimeZone(timezone);
        todayDate = String.valueOf(mCalendar.get(Calendar.MONTH)+1)+"/" + String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(mCalendar.get(Calendar.YEAR));
        todayDay = mCalendar.get(mCalendar.DAY_OF_YEAR);
        currentDay = todayDay;

    }

    private void createChartWithType(){
        //BarChart barChart = (BarChart) findViewById(R.id.bar_chart);

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


    private void loadChartByDay(int day) throws ParseException {

        //BarChart barChart = (BarChart) findViewById(R.id.bar_chart);
        //Context currentContext = this.getApplicationContext();
        mStepsTrackerDBHelper = new StepsTrackerDBHelper(this);
        mTimeEntriesList = mStepsTrackerDBHelper.getChartTimeEntriesTotalByDate(day);

        String[] values = new String[mTimeEntriesList.size()+1];
        Log.d(TAG, "size of mTimeEntriesList inside loadChartByDate: " + values.length);

        if(mTimeEntriesList.size() >0) {

            List<BarEntry> entriesGroup1 = new ArrayList<>();

            // fill the lists
            int step = 0;
            float sessionStart = 0f;
            for (BarChartTimeEntry data : mTimeEntriesList) {
                if(data.sessionId != "") {
                    values[++step] = data.sessionId;
                    entriesGroup1.add(new BarEntry(step, data.timeDuration));
                }
            }


            if(entriesGroup1.size()>0){

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new TimeChart.MyXAxisValueFormatter(values));
            //XAxis.XAxisPosition pos = XAxis.XAxisPosition.BOTTOM;
            xAxis.setLabelRotationAngle(90);
            xAxis.setGranularity(1f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setLabelCount(23);
            //xAxis.setXOffset(0.4f);
            //xAxis.setGridColor(Color.MAGENTA);
            //xAxis.setDrawAxisLine(false);
            xAxis.setDrawGridLines(false);

            YAxis yAxisLeft = barChart.getAxisLeft();
            //yAxisLeft.setDrawAxisLine(false);
           // yAxisLeft.setAxisMinValue(0.4f);
              //  yAxisLeft.setAxisMaxValue(5f);

               //yAxisLeft.setGranularityEnabled(true);
               //yAxisLeft.setGranularity(0.2f);
           // yAxisLeft.setDrawGridLines(false);
              //  yAxisLeft.setLabelCount(5);
             //yAxisLeft.setGridLineWidth(1f);


            YAxis yAxisRight = barChart.getAxisRight();
            yAxisRight.setEnabled(false);

            //yAxisRight.setGridColor(Color.MAGENTA);
            //yAxisRight.setDrawAxisLine(false);

            barChart.setGridBackgroundColor(Color.CYAN);

            //enable dragging
            barChart.setDragEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            //barChart.setPinchZoom(true);


                String dateText = this.getFormattedDate(day);

                BarDataSet set1 = new BarDataSet(entriesGroup1, dateText);


                //set colors
                set1.setColors(new int[]{R.color.runningColor}, this);
                //set1.setHighlightEnabled(true);

                //disable draw values and set the marker for better view
                set1.setDrawValues(false);
                XYMarkerView marker = new XYMarkerView(getApplicationContext(), R.layout.custom_marker_view);
                barChart.setMarkerView(marker);

                float groupSpace = 0.04f;
                float barSpace = 0.02f; // x2 dataset
                float barWidth = 0.40f; // x2 dataset
                // (0.02 + 0.30) * 3 + 0.04 = 1.00 -> interval per "group"

                BarData data = new BarData(set1);
                //data.notifyDataChanged();

                data.setBarWidth(barWidth); // set the width of each bar
                //set the listner for gesture

                barChart.setVisibleXRangeMaximum(10); // allow 10 values to be displayed at once on the x-axis, not more
                //barChart.setVisibleXRangeMinimum(5);
                barChart.setOnChartGestureListener(this);

                //barChart.setHighlightPerTapEnabled(false);
                barChart.setTouchEnabled(true);


                barChart.setData(data);
                //barChart.notifyDataSetChanged();

                barChart.invalidate(); // refresh
            }
        }


    }


    private String getFormattedDate(int day) throws ParseException {
        String toParse = day + " " + Calendar.getInstance().get(Calendar.YEAR);

        Date date = new SimpleDateFormat("D yyyy").parse(toParse);
        Log.d(TAG, "before format: " + date);

        long dateToBeDisplayed = date.getTime();
        SimpleDateFormat df2 = new SimpleDateFormat("EEE, d MMM yyyy");
        df2.setTimeZone(TimeZone.getDefault());

        String dateText = df2.format(dateToBeDisplayed);
        Log.d(TAG, "after format: " +dateText);
        return dateText;

    }


    private void createChart() throws ParseException {

        //BarChart barChart = (BarChart) findViewById(R.id.bar_chart);
        //Context currentContext = this.getContext();

        Log.d(TAG, "today date:" + todayDate);


        mStepsTrackerDBHelper = new StepsTrackerDBHelper(this);
        mTimeEntriesList = mStepsTrackerDBHelper.getChartTimeEntriesTotalByDate(todayDay);

        if(mTimeEntriesList.size()>1) {


            String[] values = new String[mTimeEntriesList.size()+1];
            Log.d(TAG, "size of mTimeEntriesList: " + values.length);
            List<BarEntry> entriesGroup1 = new ArrayList<>();

            // fill the lists
            int step = 0;
            float sessionStart = 0f;

            for (BarChartTimeEntry data : mTimeEntriesList) {
                values[++step] = data.sessionId;
                entriesGroup1.add(new BarEntry(step, data.timeDuration));
            }

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new TimeChart.MyXAxisValueFormatter(values));
            //XAxis.XAxisPosition pos = XAxis.XAxisPosition.BOTTOM;
            xAxis.setLabelRotationAngle(90);
            xAxis.setGranularity(1f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //enable dragging
            barChart.setDragEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            //barChart.setPinchZoom(true);

            String dateText = this.getFormattedDate(todayDay);
            BarDataSet set1 = new BarDataSet(entriesGroup1, dateText);

            //set colors
            set1.setColors(new int[]{R.color.runningColor}, this);

            set1.setHighlightEnabled(false);

            //disable draw values and set the marker for better view
            set1.setDrawValues(false);

            barChart.setPinchZoom(false);
            barChart.setScaleEnabled(false);



            float groupSpace = 0.04f;
            float barSpace = 0.02f; // x2 dataset
            float barWidth = 0.40f; // x2 dataset
            // (0.02 + 0.30) * 3 + 0.04 = 1.00 -> interval per "group"

            BarData data = new BarData(set1);
            data.setBarWidth(barWidth); // set the width of each bar
            data.notifyDataChanged();
            //set the listner for gesture
            barChart.setOnChartGestureListener(this);
            barChart.setHighlightPerTapEnabled(false);
            barChart.setTouchEnabled(true);


            barChart.setData(data);
            // barChart.groupBars(sessionStart, groupSpace, barSpace); // perform the "explicit" grouping
            barChart.notifyDataSetChanged();
            barChart.invalidate(); // refresh
        }

    }



    public class MyXAxisValueFormatter implements AxisValueFormatter {

        private String[] mValues = null;

        public MyXAxisValueFormatter(String[] values) {
           // Log.d(TAG, "inside formatter constructer:");
            Log.d(TAG, "length of array: " + values.length);
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
           // Log.d(TAG, "Formatter : " + mValues[(int) value] + " value: " + value);
            if(mValues.length > (int) value) {
                return mValues[(int) value];
            } else return null;
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
        motionStartX = me.getX(0);
    };

    /**
     * Callbacks when a touch-gesture has ended on the chart (ACTION_UP, ACTION_CANCEL)
     *
     * @param me
     * @param lastPerformedGesture
     */
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture){
        Log.d(TAG, "Guesture end: " + me.toString() + " gesture: " +lastPerformedGesture.toString());

        //We have to handle the leaf year case -- later
        motionEndX = me.getX(0);
        if((motionEndX - motionStartX) >100){
            Log.d(TAG, "current Day before transition +ve:" + currentDay);
            if(currentDay > 0) {
                currentDay = currentDay - 1;
            }else if(currentDay ==0){
                currentDay =365;
            }
            Log.d(TAG, "current Day after transition +ve:" + currentDay);
            try {
                loadChartByDay(currentDay);
            }catch(Exception e){
                Log.d(TAG, "exception forward: " + e.toString());
            }


        } else if((motionEndX-motionStartX) < -100){
            Log.d(TAG, "current Day before transition -ve:" + currentDay);
            if(currentDay<= todayDay){
                currentDay = currentDay+1;
                if(currentDay == 366) currentDay=0;
                Log.d(TAG, "current Day after transition +ve:" + currentDay);
                try {
                    loadChartByDay(currentDay);
                }catch (Exception e){
                    Log.d(TAG, "exception backward: " + e.toString());
                }
            }
        }
        //loadPreviousDayChart();
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
