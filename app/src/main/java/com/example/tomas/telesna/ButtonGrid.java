package com.example.tomas.telesna;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tomas.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomas on 1.9.2016.
 */
public class ButtonGrid extends BaseAdapter{
    private FitnessMainActivity mContext;
    private List<Integer> numbers;
    public List<Integer> scores;
    private List<Integer> colored = new ArrayList<>();
    private List<Integer> history = new ArrayList<Integer>(); //history for enabling back button_osoba - 0 means all users, other nums means dressnumber
    public ButtonGrid(FitnessMainActivity c, List<Integer> numbers, List<Integer> scores) {
        mContext = c;
        this.numbers = numbers;
        this.scores =scores;
    }



    @Override
    public int getCount() {
        return numbers.size();
    }

    @Override
    public Object getItem(int position) {
        return numbers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            grid = inflater.inflate(R.layout.grid_cell, null);
            final TextView textNumber = (TextView) grid.findViewById(R.id.grid_text_number);
            textNumber.setText(String.valueOf(numbers.get(position)));
            TextView textScore = (TextView) grid.findViewById(R.id.grid_text_score);
            textScore.setText(String.valueOf(scores.get(position)));
            final RelativeLayout relLay = (RelativeLayout) grid.findViewById(R.id.grid_rel_lay);
            if(colored.contains(numbers.get(position))){
                relLay.setBackgroundResource(R.drawable.button_shape_pressed);
            }
            relLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer dress = Integer.parseInt(textNumber.getText().toString());
                    if (mContext.myTimer.isRunning) {
                        mContext.db.addRoundToPerson(mContext.session, dress);
                        PhysicalResult r =mContext.db.findPersonInSessionByDress(mContext.session,dress);
                        updateScores();
                        MediaPlayer mp = MediaPlayer.create(mContext.getApplicationContext(), R.raw.beep1);
                        mp.setVolume(3,3);
                        mp.start();
                        mContext.btnUndo.setEnabled(true);
                    }else if(mContext.is12minOver){
                        Bundle args = new Bundle();
                        Integer value = Integer.parseInt(mContext.db.findPersonInSessionByDress(mContext.session,dress).disciplines.get("12min"));
                        args.putInt("oldValue",value);
                        args.putInt("dress",dress);
                        AfterRunDialog dialog = new AfterRunDialog();
                        dialog.setArguments(args);
                        dialog.show(mContext.getSupportFragmentManager(),"tag");

                    }
                    addToHistory(dress);
                    mContext.moveToPerson(dress);
                    mContext.focusEdit(mContext.editDisciplines.get(mContext.EDIT_12min));

                }
            });






        return grid;
    }

    public void updateScores(){
        this.scores.clear();
        for(Integer n: numbers){
            PhysicalResult r = mContext.db.findPersonInSessionByDress(mContext.session,n);
            this.scores.add(Integer.parseInt(r.disciplines.get("12min")));


        }
        notifyDataSetChanged();
    }



    public List<Integer> getItems(){
        return this.numbers;
    }

    public Integer stepBack(){
        /* delete last element in history and returns history size */
        if(history.size()>0) {
            Integer num = getLastFromHistory();
            long startnow = android.os.SystemClock.uptimeMillis();
            if (num == 0) {
                mContext.db.deleteRoundFromPerson(mContext.session, this.getItems());
            } else {
                mContext.db.deleteRoundFromPerson(mContext.session, num);
            }
            history.remove(history.size() - 1);
            long endnow = android.os.SystemClock.uptimeMillis();
            Log.d("MYTAG", "Execution time: " + (endnow - startnow) + " ms");
            updateScores();
        }

        return history.size();
    }

    public void addToColored(Integer buttonNumber){
        /* add button_osoba which should be colored ,, changes color of button_osoba*/
        colored.add(buttonNumber);
    }

    public void addToHistory(Integer num){
        history.add(num);
    }

    public Integer getLastFromHistory(){
        return history.get(history.size()-1);
    }
}
