package com.endolife.elfit.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.endolife.elfit.R;
import com.endolife.elfit.database.StepsTrackerDBHelper;
import com.endolife.elfit.models.DateStepsModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DayChart.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DayChart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayChart extends Fragment implements OnChartValueSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<DateStepsModel> mStepCountList;
    StepsTrackerDBHelper mStepsTrackerDBHelper;
    private static final String TAG = "DayChartActivity";

    protected BarChart barChart;
    protected String dateSelected;
    protected String[] values;

    private OnFragmentInteractionListener mListener;

    public DayChart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DayChart.
     */
    // TODO: Rename and change types and number of parameters
    public static DayChart newInstance(String param1, String param2) {
        DayChart fragment = new DayChart();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_day_chart2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        barChart = (BarChart) view.findViewById(R.id.dbar_chart);
        Context currentContext = this.getContext();

        mStepsTrackerDBHelper = new StepsTrackerDBHelper(currentContext);
        mStepCountList = mStepsTrackerDBHelper.readStepsEntries();

        values = new String[mStepCountList.size()+1];
        Log.d(TAG, "size of mstepsCounts"+ mStepCountList.size());
        List<BarEntry> entriesGroup1 = new ArrayList<>();

        // fill the lists
        int step = 0;
        float sessionStart = 0f;
        for(DateStepsModel data:mStepCountList) {
            values[++step] = data.mDate;
            entriesGroup1.add(new BarEntry(step,data.mStepCount));
        }

        if(entriesGroup1.size()>1) {

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new DayChart.MyXAxisValueFormatter(values));
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
            //set1.setColors(new int[]{R.color.walkingColor}, currentContext);
            set1.setColors(ColorTemplate.JOYFUL_COLORS);

            float groupSpace = 0.04f;
            float barSpace = 0.02f; // x2 dataset
            float barWidth = 0.4f; // x2 dataset
            // (0.02 + 0.30) * 3 + 0.04 = 1.00 -> interval per "group"

            BarData data = new BarData(set1);
            data.setBarWidth(barWidth); // set the width of each bar
            barChart.setData(data);
            //barChart.groupBars(sessionStart, groupSpace, barSpace); // perform the "explicit" grouping
            barChart.invalidate(); // refresh
        }

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
            //Log.d(TAG, "Formatter : " + mValues[(int) value]);
            return mValues[(int) value];
        }

        /** this is only needed if numbers are returned, else return 0 */
        @Override
        public int getDecimalDigits() { return 0; }
    }


    protected RectF mOnValueSelectedRectF = new RectF();

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.d(TAG, "Came to onValue Selected");

        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        barChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = barChart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.d("bar entry: ", e.toString());

        if ((int)e.getX() == 0) {
            dateSelected = values[(int) e.getX()];

        } else if(!((int)e.getX() ==0)){
            dateSelected = values[(int) e.getX()];
        }

        if(dateSelected != null){

            Activity dvActivity = (DayView)getActivity();

            ((DayView) getActivity()).replaceFragment(dateSelected);

        }

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {

        Log.d(TAG, "nothing selected");
    }

}
