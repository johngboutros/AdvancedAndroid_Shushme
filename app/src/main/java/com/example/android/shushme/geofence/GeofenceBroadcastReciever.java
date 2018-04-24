package com.example.android.shushme.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by john on 24/04/18.
 */

public class GeofenceBroadcastReciever extends BroadcastReceiver {

    private static final String TAG = GeofenceBroadcastReciever.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive invoked - intent: " + intent);
    }
}
