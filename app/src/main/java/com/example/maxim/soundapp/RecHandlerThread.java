package com.example.maxim.soundapp;

import android.util.Log;
import android.widget.TextView;

/**
 *
 */

class RecHandlerThread extends Thread {

    // Constants
    private static final String TAG = "RecHandlerThread";

    // Members
    private MainActivity mainActivity;
    private RecHandler recHandler;

    @Override
    public void run() {
        // Get references
        mainActivity = MainActivity.getThisActivity();
        recHandler = RecHandler.getRecHandler();

        // Run until interrupted
        while (true) {
            showBufAvg();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.v(TAG, "Interrupted");
                return;
            }
        }
    }

    private void showBufAvg() {
        Log.v(TAG, "Updating buffer average view");
        final String bufAvgStr = Float.toString(recHandler.getAudioRecAvg());
        final TextView dbTextView = (TextView) mainActivity.findViewById(R.id.db_text_view);

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dbTextView.setText(bufAvgStr);
            }
        });
    }
}
