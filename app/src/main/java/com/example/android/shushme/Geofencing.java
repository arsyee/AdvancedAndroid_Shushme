package com.example.android.shushme;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

class Geofencing implements OnCompleteListener<Void>, OnFailureListener {
    private static final String TAG = Geofencing.class.getSimpleName();

    private final Context mContext;
    private final GoogleApiClient mClient;
    private final List<Geofence> mGeofenceList;
    private PendingIntent mIntent;

    // COMPLETED (1) Create a Geofencing class with a Context and GoogleApiClient constructor that
    // initializes a private member ArrayList of Geofences called mGeofenceList
    Geofencing(Context context, GoogleApiClient client) {
        mContext = context.getApplicationContext();
        mClient = client;
        mGeofenceList = new ArrayList<>();
    }

    // COMPLETED (2) Inside Geofencing, implement a public method called updateGeofencesList that
    // given a PlaceBuffer will create a Geofence object for each Place using Geofence.Builder
    // and add that Geofence to mGeofenceList
    void updateGeofencesList(PlaceBuffer places) {
        mGeofenceList.clear();
        if (places == null) return;
        for (Place place : places) {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(place.getId())
                    .setExpirationDuration(24L * 60L * 60L * 1000L)
                    .setCircularRegion(place.getLatLng().latitude, place.getLatLng().longitude, 50.0f)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            mGeofenceList.add(geofence);
        }
    }

    // COMPLETED (3) Inside Geofencing, implement a private helper method called getGeofencingRequest that
    // uses GeofencingRequest.Builder to return a GeofencingRequest object from the Geofence list
    GeofencingRequest getGeofencingRequest() {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(mGeofenceList)
                .build();
    }

    // COMPLETED (5) Inside Geofencing, implement a private helper method called getGeofencePendingIntent that
    // returns a PendingIntent for the GeofenceBroadcastReceiver class
    PendingIntent getGeofencePendingIntent() {
        if (mIntent == null) {
            Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
            mIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return mIntent;
    }

    // COMPLETED (6) Inside Geofencing, implement a public method called registerAllGeofences that
    // registers the GeofencingRequest by calling LocationServices.GeofencingApi.addGeofences
    // using the helper functions getGeofencingRequest() and getGeofencePendingIntent()
    public void registerAllGeofences() {
        if (mClient == null || !mClient.isConnected() || mGeofenceList == null || mGeofenceList.size() == 0) return;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission denied.");
            return;
        }
        Task<Void> task = LocationServices.getGeofencingClient(mContext).addGeofences(getGeofencingRequest(), getGeofencePendingIntent());
        task.addOnCompleteListener(this);
        task.addOnFailureListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        Log.e(TAG, "Geofences were registered: " + task.toString());
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Geofences could not be registered: " + e.getMessage());
    }

    // COMPLETED (7) Inside Geofencing, implement a public method called unRegisterAllGeofences that
    // unregisters all geofences by calling LocationServices.GeofencingApi.removeGeofences
    // using the helper function getGeofencePendingIntent()
    public void unRefisterAllGeofences() {
        if (mClient == null || !mClient.isConnected()) return;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission denied.");
            return;
        }
        LocationServices.getGeofencingClient(mContext).removeGeofences(getGeofencePendingIntent());
    }
}
