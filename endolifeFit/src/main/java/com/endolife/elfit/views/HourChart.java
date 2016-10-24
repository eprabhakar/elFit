package com.endolife.elfit.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.endolife.elfit.R;
import com.endolife.elfit.custom.XYMarkerView;
import com.endolife.elfit.database.StepsTrackerDBHelper;
import com.endolife.elfit.models.BarChartTimeEntry;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;


import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HourChart.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HourChart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HourChart extends Fragment implements OnChartValueSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "todayDate";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mtodayDate;
    private String mParam2;

    private static final String TAG = "HourChartActivity";
    private static final int RUNNING = 3;
    private static final int JOGGING = 2;
    private static final int WALKING = 1;

    private OnFragmentInteractionListener mListener;
    StepsTrackerDBHelper mStepsTrackerDBHelper;
    ArrayList<BarChartTimeEntry> mTimeEntriesList;
    protected BarChart mBarChart;

    public HourChart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HourChart.
     */
    // TODO: Rename and change types and number of parameters
    public static HourChart newInstance(String param1, String param2) {
        HourChart fragment = new HourChart();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mtodayDate = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hour_chart, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mBarChart = (BarChart) view.findViewById(R.id.fbar_chart);
        mBarChart.setOnChartValueSelectedListener(this);
        Context currentContext = this.getContext();
        mStepsTrackerDBHelper = new StepsTrackerDBHelper(currentContext);
        mTimeEntriesList = mStepsTrackerDBHelper.getChartTimeEntriesByDate(mtodayDate);

        String[] values = new String[mTimeEntriesList.size()+1];
        Log.d(TAG, "size of mTimeEntriesList: " + values.length);

        List<BarEntry> entriesGroup1 = new ArrayList<>();
        List<BarEntry> entriesGroup2 = new ArrayList<>();
        List<BarEntry> entriesGroup3 = new ArrayList<>();

        // fill the lists
        int step = 0;
        float sessionStart = 0f;
        for(BarChartTimeEntry data:mTimeEntriesList) {

            Log.d(TAG, "Session id :" + data.sessionId + " : " + data.timeDuration + " : " + data.type);
            // if((step == 1) && (Integer.parseInt(data.sessionId)>0)) sessionStart = Integer.parseInt(data.sessionId);
            if(data.type == WALKING) {
                values[++step] = data.sessionId;
                entriesGroup1.add(new BarEntry(step, data.timeDuration));
            } else if (data.type == JOGGING){
                values[++step] = data.sessionId;
                entriesGroup2.add(new BarEntry(step, data.timeDuration));
            } else if (data.type == RUNNING) {
                values[++step] = data.sessionId;
                entriesGroup3.add(new BarEntry(step, data.timeDuration));
            }
        }

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setValueFormatter(new HourChart.MyXAxisValueFormatter(values));
        //XAxis.XAxisPosition pos = XAxis.XAxisPosition.BOTTOM;
        xAxis.setLabelRotationAngle(90);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        IAxisValueFormatter xAxisFormatter = xAxis.getValueFormatter();

        XYMarkerView mv = new XYMarkerView(currentContext, xAxisFormatter);
        mv.setChartView(mBarChart); // For bounds control
        mBarChart.setMarker(mv); // Set the marker to the chart

        YAxis leftAxis = mBarChart.getAxisLeft();

        LimitLine ll = new LimitLine(1f, "1 minute");
        ll.setLineColor(Color.MAGENTA);
        ll.setLineWidth(1f);
        ll.enableDashedLine(10f, 10f, 0f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(8f);

        LimitLine ll5 = new LimitLine(5f, "1 minute");
        ll5.setLineColor(Color.MAGENTA);
        ll5.setLineWidth(1f);
        ll5.enableDashedLine(10f, 10f, 0f);
        ll5.setTextColor(Color.BLACK);
        ll5.setTextSize(8f);
// .. and more styling options

        leftAxis.addLimitLine(ll);

        //enable dragging
        mBarChart.setDragEnabled(true);


        // enable touch gestures
        mBarChart.setTouchEnabled(true);

        // enable scaling and dragging
        // mChart.setDragEnabled(true);
        mBarChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mBarChart.setPinchZoom(true);

        BarDataSet set1 = new BarDataSet(entriesGroup1, "Walking");
        BarDataSet set2 = new BarDataSet(entriesGroup2, "Jogging");
        BarDataSet set3 = new BarDataSet(entriesGroup3, "Running");


        //remove highlighting on running and jogging
        set1.setDrawValues(false); // disable highlighting for DataSet
        set2.setDrawValues(false);
        set3.setDrawValues(false);


        //set1.setHighlightEnabled(true);

        //set2.setHighlightEnabled(true); // disable highlighting for DataSet
        //set3.setHighlightEnabled(true);

        //barChart.highlightValues(null);
        //barChart.setHighlightPerTapEnabled(true);

        //set colors
        set1.setColors(new int[]{R.color.walkingColor}, currentContext);

        set2.setColors(new int[]{R.color.joggingColor}, currentContext);

        set3.setColors(new int[]{R.color.runningColor}, currentContext);

        float groupSpace = 0.04f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.30f; // x2 dataset
        // (0.02 + 0.30) * 3 + 0.04 = 1.00 -> interval per "group"

        BarData data = new BarData(set1, set2, set3);
        data.setBarWidth(barWidth); // set the width of each bar
        mBarChart.setData(data);
       // mBarChart.highlightValue(maxXSet1Entry,walkIndex,false);
        //mBarChart.groupBars(sessionStart, groupSpace, barSpace); // perform the "explicit" grouping
        mBarChart.invalidate(); // refresh


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected RectF mOnValueSelectedRectF = new RectF();

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        mBarChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = mBarChart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + mBarChart.getLowestVisibleX() + ", high: "
                        + mBarChart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            Log.d(TAG, "inside formatter constructer:");
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            Log.d(TAG, "Formatter : " + mValues[(int) value]);
            return mValues[(int) value];
        }

        /** this is only needed if numbers are returned, else return 0 */
        @Override
        public int getDecimalDigits() { return 0; }
    }
}
