package com.example.maxim.soundapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_CODE = 123;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO};

    // Self reference (singleton)
    private static MainActivity thisActivity;
    public static synchronized MainActivity getThisActivity() {
        return thisActivity;
    }

    // Members
    private RecHandler recHandler;
    private Boolean isRecordAudioRequested = false;
    private Boolean isRecordAudioGranted = false;

    // UI update worker
//    public Handler mHandler = new Handler(Looper.getMainLooper());

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set references
        thisActivity = this;
    }

    //----------------------------------------------------------------------------------------------
    // Permissions management
    //----------------------------------------------------------------------------------------------
    /*
    * Checks and requests app permissions
    */
    private void checkAppPermissions() throws Exception {

        // Make sure all permissions are granted
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(thisActivity, permission) ==
                    PackageManager.PERMISSION_DENIED) {

                // Request permission from user
                ActivityCompat.requestPermissions(thisActivity, new String[]{permission},
                        PERMISSIONS_REQUEST_CODE);

                Log.i("MainActivity", permission + " permission is requested during runtime");

                // TODO: make a hash-map to track state of each required permission
                isRecordAudioRequested = true;
                return;
            }
        }
        /* If all permissions already granted, the thread will end here and main
         *  activity continues
         */
        recordVoice(PackageManager.PERMISSION_GRANTED);
    }

    /*
    *   Callback, called after user response to permissions request
    */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "Got into onRequestPermissionsResult");

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                try {
                    // If request is denied, the result arrays are empty.

                    // RECORD_AUDIO
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        recordVoice(PackageManager.PERMISSION_GRANTED);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } break;
            default: {
                Log.i(TAG, "Permissions request code didn't match");
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // Main screen buttons
    //----------------------------------------------------------------------------------------------
    /*
     * Called when 'start' button is pressed
     */
    public void pressStartButton(View view) {
        startMicRec();
    }

    /*
     * Called when 'stop' button is pressed
     */
    public void pressStopButton(View view) {
        stopMicRec();
    }

    //----------------------------------------------------------------------------------------------
    // Sound recording control
    //----------------------------------------------------------------------------------------------
    private void startMicRec() {
        Log.i("MainActivity", "Start button press detected");

        if (isRecordAudioGranted) {
            recHandler.startMicRec();
        } else if (!isRecordAudioRequested) {
            recordVoice(PackageManager.GET_PERMISSIONS);
        } else { // requested but not granted
            Log.e(TAG, "RECORD_AUDIO permission wasn't approved by user");
            Toast.makeText(thisActivity, "Can't record audio without RECORD_AUDIO permission", Toast.LENGTH_LONG).show();
        }
    }

    private void stopMicRec() {
        Log.i("MainActivity", "Stop button press detected");

        if (recHandler != null) {
            recHandler.stopMicRec();
        }
    }

    //----------------------------------------------------------------------------------------------
    // Feature #1 (temporal) - record voice and display average amplitude
    //----------------------------------------------------------------------------------------------
    public void recordVoice(int state) {
        try {
            if (state == PackageManager.PERMISSION_GRANTED) {
                recHandler = RecHandler.getRecHandler();
                isRecordAudioGranted = true;
                startMicRec();
            } else if (state == PackageManager.GET_PERMISSIONS ) {
                checkAppPermissions();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
