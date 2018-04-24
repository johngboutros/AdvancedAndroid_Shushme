package com.example.android.shushme.geofence;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 24/04/18.
 */

public class Geofencing {

    private static final long GEOFENCE_TIMEOUT = 24*60*60*1000;
    private static final float GEOFENCE_RADIUS = 50;

    private Context context;
    private GoogleApiClient googleApiClient;

    private List<Geofence> geofences = new ArrayList<>();

    public Geofencing(@NonNull Context context, @NonNull GoogleApiClient googlApiClient) {
        this.context= context;
        this.googleApiClient = googlApiClient;
    }

    public void updateGeofences(PlaceBuffer places) {
        if (places == null || places.getCount() == 0) return;

        geofences.clear();
        for (Place place: places) {
            String id = place.getId();
            double latitude = place.getLatLng().latitude;
            double longitude = place.getLatLng().longitude;

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(id)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            geofences.add(geofence);
        }
    }

}
