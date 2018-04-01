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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    /***
     * Handles the Broadcast message sent when the Geofence Transition is triggered
     * Careful here though, this is running on the main thread so make sure you start an AsyncTask for
     * anything that takes longer than say 10 second to run
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive called");
        // COMPLETED (4) Use GeofencingEvent.fromIntent to retrieve the GeofencingEvent that caused the transition
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        // COMPLETED (5) Call getGeofenceTransition to get the transition type and use AudioManager to set the
        // phone ringer mode based on the transition type. Feel free to create a helper method (setRingerMode)
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) switch (event.getGeofenceTransition()) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            default:
                Log.e(TAG, String.format("Unexpected GeofenceEvent: %d", event.getGeofenceTransition()));
        }

        // TODO (6) Show a notification to alert the user that the ringer mode has changed.
        // Feel free to create a helper method (sendNotification)

    }
}
