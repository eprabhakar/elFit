package com.endolife.elfit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.endolife.elfit.services.StepsTrackerService;

public class ElfitAccelReceiver extends BroadcastReceiver {
    public ElfitAccelReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("ElfitReceiver", "Received on Destroy event, about to restart the service");
        context.startService(new Intent(context, StepsTrackerService.class));
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
