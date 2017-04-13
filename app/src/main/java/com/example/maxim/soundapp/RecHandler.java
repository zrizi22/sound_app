package com.example.maxim.soundapp;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/**
 * This class is responsible for initializing the AudioRecord class
 * and handling all audio recording activities
 */

class RecHandler {
    // Constants
    private static final int MIC_SAMPLE_PERIOD = 100; // ms between each Mic sampling
    // TODO: add constant for desired sampling time + calculate the buffer size for this

    // Self handler, a single instance all over the app
    private static RecHandler recHandler;

    // Members
    private AudioRecord recorder;
    private short[] audioBuf;
    private int audioRecMinBuf;
    private float audioRecAvg; // average amplitude of the recorded buffer

    // Constructor
    private RecHandler() {
        try {
            audioRecMinBuf = AudioRecord.getMinBufferSize(44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            Log.d("RecHandler", "Buffer size is "+Integer.toString(audioRecMinBuf));

            recorder  = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    44100, // sample rate in Hz
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    audioRecMinBuf
            );

            // Check that could be successfully initialized
            if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                recorder.release();
                throw new IOException("AudioRecord failed to initialize");
            }

            // Initialize samples buffer
            audioBuf = new short[audioRecMinBuf];

            // Initialize buffer average
            audioRecAvg = (float) 0;

        } catch (Exception ex) {
            Log.e("RecHandler", "Got exception when trying to initialize RecHandler");
            ex.printStackTrace();
        }
    }

    // Getter
    static RecHandler getRecHandler() {
        if (recHandler == null) {
            recHandler = new RecHandler();
            Log.d("RecHandler", "Created new RecHandler");
        }
        return recHandler;
    }

    // Start recording
    void startMicRec() {
        recorder.startRecording();

        // Record audio in separate thread, so we have no busy-waiting
        //  between recordings
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    try {
                        Log.d("RecHandler","Recording loop");

                        // Record a full buffer
                        recorder.read(audioBuf, 0, audioRecMinBuf);

                        // Calculate the average recorded amplitude
                        audioRecAvg = calcBufAvg();

                        // Wait until next sample
                        this.wait(MIC_SAMPLE_PERIOD);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // Stop recording
    public void stopMicRec() {
        recorder.stop();
    }

    // Calculate buffer average
    // TODO: convert to db value
    private float calcBufAvg() {
        int sum = 0;

        for (short sample : audioBuf) {
            sum += sample;
        }

        Log.d("RecHandler","Buffer average is" + Float.toString(sum / (float) audioRecMinBuf));
        return (sum / (float) audioRecMinBuf);
    }

    // Getter for average amplitude
    public float getAudioRecAvg() {
        return audioRecAvg;
    }
}
