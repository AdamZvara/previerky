package com.example.tomas.strelby;

import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by tomas on 3.9.2016.
 */
public class MyTimer {
    Button btnStart;
    TextView timeText;
    ShootsMainActivity context;
    boolean isRunning = false;

    long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    int secs = 0;
    int mins = 0;
    int milliseconds = 0;
    Handler handler = new Handler();

    public MyTimer(ShootsMainActivity context, Button btnStart, TextView timeText){
        this.btnStart = btnStart;
        this.timeText = timeText;
        this.context =context;
        setListeners();
    }

    private void setListeners(){
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isRunning == false) {
                    startTimer();
                } else if(isRunning ==true){
                    context.editTime.setText(String.format(Locale.US,"%.2f",updatedTime/1000f));
                    stopTimer();

                }
                context.clickSound.start();
            }
        });

    }
    public Runnable updateTimer = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            milliseconds = (int) (updatedTime % 1000);
            timeText.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            timeText.setTextColor(Color.RED);
            handler.postDelayed(this, 0);
        }};
    public void stopTimer(){
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        isRunning = false;
        secs = 0;
        mins = 0;
        milliseconds = 0;
        btnStart.setText("Štart");
        timeText.setTextColor(Color.BLUE);
        handler.removeCallbacks(updateTimer);
        timeText.setText("00:00:00");
    }

    public void startTimer(){
        btnStart.setText("Zapíš");
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTimer, 0);
        isRunning = true;
    }

    public void pauseTimer(){
        timeSwapBuff += timeInMilliseconds;
        handler.removeCallbacks(updateTimer);
    }
}
