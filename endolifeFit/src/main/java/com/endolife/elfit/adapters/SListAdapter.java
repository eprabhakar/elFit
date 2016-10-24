package com.endolife.elfit.adapters;

/**
 * Created by eprabhakar on 25/9/16.
 */


import android.content.Context;
import android.hardware.Sensor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.endolife.elfit.R;

import java.util.List;

import static com.endolife.elfit.R.layout.list_rows;

public class SListAdapter extends RecyclerView.Adapter<SListAdapter.ViewHolder> {

    TextView mSensorsCountText;
    TextView mSensorName;
    TextView mSensorVendor;
    Context mContext;
    LayoutInflater mLayoutInflater;
    List<Sensor> mSensorsList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView sensorname;
        //public TextView vendor;
        public ViewHolder(View v) {
            super(v);

            sensorname = (TextView)v.findViewById(R.id.sensor_name);
            //vendor =  (TextView)v.findViewById(R.id.vendor);
        }
    }


    public SListAdapter(Context mContext, List<Sensor> mSensorsList) {
       // super();
        this.mSensorsList = mSensorsList;
        this.mContext = mContext;
       // this.mLayoutInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
        holder.sensorname.setText(mSensorsList.get(position).getName() + " - " + mSensorsList.get(position).getVendor() + "  ");
       // holder.sensorname.setText(mSensorsList.get(position).getName());
        //holder.vendor.setText(mSensorsList.get(position).getVendor());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSensorsList.size();
    }

    /*
    @Override
    public int getCount() {

        return mSensorsList.size();
    }

    @Override
    public Object getItem(int position) {

        return mSensorsList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){

            convertView = mLayoutInflater.inflate(list_items, parent, false);
        }
        mSensorName = (TextView)convertView.findViewById(R.id.sensor_name);
        mSensorName.setText(mSensorsList.get(position).getName());
        mSensorVendor =(TextView)convertView.findViewById(R.id.vendor);
        mSensorVendor.setText(mSensorsList.get(position).getVendor());

        return convertView;
    }
    */
}
