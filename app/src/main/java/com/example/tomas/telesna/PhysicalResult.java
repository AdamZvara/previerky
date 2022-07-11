package com.example.tomas.telesna;

import com.example.tomas.common.Person;
import com.example.tomas.common.Session;

import java.util.HashMap;

/**
 * Created by tomas on 21.8.2016.
 * PhysicalResult class  - stores person info, points, dressNumber  and evaluation
 * set passed - possible as number ( 0 is false ) or as boolean
 */
public class PhysicalResult {
    Person person;
    Session session;
    Integer dressNum;
    boolean passed;
    HashMap<String,String> disciplines;

    static public final Integer ROUND = 400 ;
    /*
    * pull_up
    * jump
    * crunch
    * 100m
    * 12min
    * swimming
    * number are stored as a string because some are float*/

    public PhysicalResult(Session session, Person person, Integer dresNum){
        this.session = session;
        this.person = person;
        this.dressNum = dresNum;
    }

    public PhysicalResult(Session session, Person person, Integer dresNum, HashMap<String,String> results){
        this.session = session;
        this.person = person;
        this.dressNum = dresNum;
        this.disciplines = results;
    }

    public void setResults(HashMap<String,String> results){
        this.disciplines = results;
    }

    public void setPassed(Integer num){ this.passed = num == 0 ? false : true;}
    public void setPassed(boolean val){ this.passed =val;}
    public boolean getPassed(){return passed;}
 }
