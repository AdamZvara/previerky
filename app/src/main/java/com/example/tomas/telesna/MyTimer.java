package com.example.tomas.telesna;

import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tomas.R;

/**
 * Created by tomas on 3.9.2016.
 */
public class MyTimer {
    Button btnStart;
    Button btnUndo;
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

    public MyTimer(FitnessMainActivity context, Button btnStart, TextView timeText){
        this.btnStart = btnStart;
        this.timeText = timeText;
        this.context =context;
        setListeners();
    }

    private void setListeners(){

        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (!isRunning) {
                    startTimer();

                } else if(isRunning){
                    pauseTimer();
                    new AlertDialog.Builder(context)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Stopky")
                            .setMessage("Chcete vynulovať stopky?")
                            .setPositiveButton("Áno", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    stopTimer();
                                }

                            })
                            .setNegativeButton("Nie", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startTimer();
                                }

                            })
                            .show();

                }}
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
            timeText.setText("" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
                    /*+ ":" + String.format("%03d", milliseconds));*/
            timeText.setTextColor(Color.RED);
            handler.postDelayed(this, 0);
            //int time = 12*60000;
            int time = 12*60000;
            if ((updatedTime >= (time))){ //&& (updatedTime <= (time +70))){
                stopTimer();
                context.is12minOver = true;
            }
        }};
    public void stopTimer(){
        makeSound();
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
        //timeText.setText("00:00");

       context.btnRound.setText("Ukonči 12min");
       context.btnUndo.setEnabled(false);
    }


    public void startTimer(){
        makeSound();
        btnStart.setText("Vynuluj");
        timeText.setText("00:00");
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTimer, 0);
        isRunning = true;
        context.btnRound.setText("Kolo všetci");
    }

    public void pauseTimer(){
        timeSwapBuff += timeInMilliseconds;
        handler.removeCallbacks(updateTimer);
    }

    private void makeSound(){
        MediaPlayer mp = MediaPlayer.create(context.getApplicationContext(), R.raw.stop_sound);
        mp.start();
    }

    public long getTimeInMiliseconds(){
        return timeInMilliseconds;
    }
}
