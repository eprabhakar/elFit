package com.endolife.elfit.adapters;

/**
 * Created by eprabhakar on 25/9/16.
 */

import android.content.Context;
import android.hardware.Sensor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.endolife.elfit.R;
import com.endolife.elfit.models.DateStepsModel;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        TextView mDateStepCountText;
        ArrayList<DateStepsModel> mStepCountList;
        Context mContext;
        LayoutInflater mLayoutInflater;
        private static final String TAG = "ListAdapter";

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
                // each data item is just a string in this case
                public TextView sensor_name;
                public ViewHolder(View v) {
                        super(v);
                        sensor_name = (TextView)v.findViewById(R.id.sensor_name);
                }
        }



        public ListAdapter(ArrayList<DateStepsModel> mStepCountList, Context mContext) {
                this.mStepCountList = mStepCountList;
                this.mContext = mContext;
                //this.mLayoutInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
                // create a new view
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_rows, parent, false);
                // set the view's size, margins, paddings and layout parameters

                ViewHolder vh = new ViewHolder(v);
                return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
                // - get element from your dataset at this position
                // - replace the contents of the view with that element
                holder.sensor_name.setText(mStepCountList.get(position).mDate + " - Total Steps: " + String.valueOf(mStepCountList.get(position).mStepCount) + "  ");
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
                return mStepCountList.size();
        }

        /*
        @Override
        public int getCount() {
                return mStepCountList.size();
        }

        @Override
        public Object getItem(int position) {
                return mStepCountList.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView==null){
                        convertView = mLayoutInflater.inflate(R.layout.list_rows, parent, false);
                }

                mDateStepCountText = (TextView)convertView.findViewById(R.id.sensor_name);
                mDateStepCountText.setText(mStepCountList.get(position).mDate + " - Total Steps: " + String.valueOf(mStepCountList.get(position).mStepCount));
                Log.d(TAG, "row : " + mStepCountList.get(position).mDate + " - Total Steps: " + String.valueOf(mStepCountList.get(position).mStepCount));
                return convertView;
        }
        */


}
