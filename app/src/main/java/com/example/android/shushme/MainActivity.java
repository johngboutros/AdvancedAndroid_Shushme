package com.example.android.shushme;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();

    // Member variables
    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Switch permissionCheckBox;
    private GoogleApiClient googleClient;

    private final static int REQUEST_LOCATION_PERMISSION_CODE = 18;

    /**
     * Called when the activity is starting
     *
     * @param savedInstanceState The Bundle that contains the data supplied in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // DONE (4) Create a GoogleApiClient with the LocationServices API and GEO_DATA_API
        googleClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();

        // Setup permission CheckBox
        permissionCheckBox = (Switch) findViewById(R.id.permission_checkbox);
    }

    // (5) Override onConnected, onConnectionSuspended and onConnectionFailed for GoogleApiClient
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Api client connection successful! =)");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Api client connection suspended! =|");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Api client connection failed! =(");
    }

    // DONE (7) Override onResume and inside it initialize the location permissions checkbox
    @Override
    protected void onResume() {
        super.onResume();
        checkLocationPermission();
    }

    // DONE (8) Implement onLocationPermissionClicked to handle the CheckBox click event
    public void onLocationPermissionClicked(View v) {
        boolean checked = permissionCheckBox.isChecked();
        Log.d(TAG, "permissionCheckBox clicked: " + (checked ? "checked" : "unchecked"));
        if (checked) {
            requestLocationPermission();
        }
        checkLocationPermission();
    }

    private boolean checkLocationPermission() {

        boolean granted= false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (PackageManager.PERMISSION_GRANTED !=
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Permission is not granted
                // Show checkBox
                granted = false;
            } else {
                // Permission has already been granted
                granted = true;
            }
        } else {
            granted = true;
        }

        permissionCheckBox.setChecked(granted);
        permissionCheckBox.setEnabled(!granted);

        return granted;
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Permission is not granted
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "Location permission must be granted to continue",
                        Toast.LENGTH_LONG).show();

                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION_CODE);

            } else {

                // No explanation needed; request the permission
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    // DONE (9) Implement the Add Place Button click event to show  a toast message with the permission status
    public void pickLocation(View v) {
        boolean checked = permissionCheckBox.isChecked();
        Toast.makeText(this, "Permission Status: "
                        + (checked ? "granted" : "un-granted"),
                Toast.LENGTH_SHORT).show();
    }
}
