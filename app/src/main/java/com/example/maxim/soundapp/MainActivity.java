package com.example.maxim.soundapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    // Constants
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    // Self reference
    private MainActivity thisActivity;

    // Members
    private boolean isPermissionsGranted = false;
    private RecHandler recHandler;

    // Recorded db
//    final TextView dbTextView = (TextView) findViewById(R.id.db_text_view);
//    private String dbTextStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisActivity = this;

        try {
            // Check and ask for permissions
            // (starting from 6.0 need manifest permissions are not enough)
            // TODO: move all permission handling to a separate class
            // TODO: this doesn't work, need to wait for the asyncThread that asks for permissions to finish before continue
            checkAppPermissions();
            if (!isPermissionsGranted) {
                throw new Exception("Couldn't get RECORD_AUDIO permission");
            }

            recHandler = RecHandler.getRecHandler();

            // Display recorded db value
//            dbTextStr = Float.toString(recHandler.getAudioRecAvg());
//            dbTextView.setText(dbTextStr);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkAppPermissions() {
        // RECORD_AUDIO (needed for Microphone recording)
        int permissionCheck = ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            Log.d("MainActivity", "RECORD_AUDIO permission is requested during runtime");

            ActivityCompat.requestPermissions(thisActivity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            isPermissionsGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                isPermissionsGranted = grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            }
        }
    }

    // Main screen buttons
    public void startMicRec(View view) {
        Log.i("MainActivity", "Start button press detected");
        recHandler.startMicRec();
    }

    public void stopMicRec(View view) {
        Log.i("MainActivity", "Stop button press detected");
//        recHandler.stopMicRec();
    }

}
