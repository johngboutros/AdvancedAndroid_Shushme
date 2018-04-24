package com.example.android.shushme.geofence;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 24/04/18.
 */

public class Geofencing {

    private final static String TAG = Geofencing.class.getSimpleName();

    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000;
    private static final float GEOFENCE_RADIUS = 50;

    private Context context;
    //    private GoogleApiClient googleApiClient;
    private PendingIntent geofencePendingIntent;

    private List<Geofence> geofences = new ArrayList<>();

    public Geofencing(@NonNull Context context) {
        this.context = context;
//        this.googleApiClient = googlApiClient;
    }

    public void registerAllGeofences() {
        if (geofences == null || geofences.isEmpty()) return;

        /*if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // TO-DO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/

        try {
            LocationServices.getGeofencingClient(context).addGeofences(getGeofencingRequest(),
                    getGeofencePendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {

                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "Gefencing request succeeded");
                }

            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "Gefencing request failed");
                }
            });

        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    public void unregisterAllGeofences() {
        if (geofences == null || geofences.isEmpty()) return;

        try {
            LocationServices.getGeofencingClient(context).removeGeofences(
                    getGeofencePendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {

                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "Gefencing removal succeeded");
                }

            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "Gefencing removal failed");
                }
            });

        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    public void updateGeofences(PlaceBuffer places) {
        if (places == null || places.getCount() == 0) return;

        geofences.clear();
        for (Place place : places) {
            String id = place.getId();
            double latitude = place.getLatLng().latitude;
            double longitude = place.getLatLng().longitude;

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(id)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            geofences.add(geofence);
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        if (geofences == null || geofences.isEmpty()) return null;

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);

        return builder.build();
    }

    public PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) return geofencePendingIntent;

        Intent intent = new Intent(context, GeofenceBroadcastReciever.class);
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return geofencePendingIntent;
    }
}
