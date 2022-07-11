package com.example.tomas.telesna;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

import com.example.tomas.R;

/**
 * Created by tomas on 3.9.2016.
 */
public class WomanTimer {
    TextView timeText;
    FitnessMainActivity context;
    boolean isRunning = false;


    long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    int secs = 0;
    int mins = 0;
    int milliseconds = 0;
    Handler handler = new Handler();


    public Runnable updateTimer = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            secs = (int) (updatedTime / 1000);
            milliseconds = (int) (updatedTime % 1000);
            timeText.setText("" + String.format("%02d", secs));
            handler.postDelayed(this, 200);
        }};

    public WomanTimer(FitnessMainActivity context, TextView timeText){

        this.timeText = timeText;
        this.context =context;
    }


    public void stopTimer(){
        makeSound();
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        secs = 0;
        mins = 0;
        milliseconds = 0;

        handler.removeCallbacks(updateTimer);
        isRunning = false;
    }


    public void startTimer(){
        makeSound();
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTimer, 0);
        isRunning = true;
    }

    private void makeSound(){
        MediaPlayer mp = MediaPlayer.create(context.getApplicationContext(), R.raw.beep);
        mp.start();
    }

    public Boolean isRunning(){
        return isRunning;
    }
}
