package com.example.tomas.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by tomas on 18.8.2016.
 * <p>
 * One sesion - store an arrayof persons and actual number
 * Hashmap<integer,person> - can add/get person by dress number .... <dressnum, person object>
 * addPerson - add Person to hahmap - integer is based on dressnumber of person
 */
public class Session {

    public Long id;
    public int actualNumber;
    public int numOfBullets;
    public int num;
    private Timestamp timeStamp;
    private long startStadiumTime;
    private long endStadiumTime;
    private Long parentSessionId;
    private int childOrderNumber;

    public Session() {
        //
    }

    public Session(Long id, Integer actualNumber) {
        this.id = id;
        this.actualNumber = actualNumber;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setActualNumber(Integer number) {
        if (number < 1) {
            number = 1;
        }
        this.actualNumber = number;
    }

    public int getActualNumber() {
        return actualNumber;
    }

    public int getNumOfBullets() {
        return this.numOfBullets;
    }

    public void setNumOfBullets(int num) {
        this.numOfBullets = num;
    }

    /**
     * For ArrayAdapter.
     *
     * @return string representation of Session.
     */
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",Locale.getDefault());
        TimeZone tm = TimeZone.getTimeZone("GMT+04:00");
        dateFormat.setTimeZone(tm);
        return dateFormat.format(timeStamp);
    }

    public long getStartStadiumTime() {
        return startStadiumTime;
    }

    public void setStartStadiumTime(long aStartStadiumTime) {
        startStadiumTime = aStartStadiumTime;
    }

    public long getEndStadiumTime() {
        return endStadiumTime;
    }

    public void setEndStadiumTime(long aEndStadiumTime) {
        endStadiumTime = aEndStadiumTime;
    }

    public Long getParentSessionId() {
        return parentSessionId;
    }

    public void setParentSessionId(Long aParentSessionId) {
        parentSessionId = aParentSessionId;
    }

    public int getChildOrderNumber() {
        return childOrderNumber;
    }

    public void setChildOrderNumber(int aChildOrderNumber) {
        childOrderNumber = aChildOrderNumber;
    }
}

