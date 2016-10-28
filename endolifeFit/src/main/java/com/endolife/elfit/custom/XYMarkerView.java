package com.endolife.elfit.custom;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.endolife.elfit.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;

/**
 * Created by eprabhakar on 20/10/16.
 */

public class XYMarkerView extends MarkerView implements IMarker {

    private TextView tvContent;

    public XYMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        // find your layout components
        Log.d("XYMarkerView", "Constructor called");
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        Log.d("XYMarkerView", "refreshContent: " + e.toString());

        tvContent.setText("" + e.getY());

        // this will perform necessary layouting
        //super.refreshContent(e, highlight);
    }

    @Override
    public int getXOffset(float xpos) {
        return 0;
    }

    @Override
    public int getYOffset(float ypos) {
        return 0;
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }

    @Override
    public MPPointF getOffsetForDrawingAtPos(float posX, float posY) {
        return null;
    }
}
