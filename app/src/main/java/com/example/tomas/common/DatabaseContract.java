package com.example.tomas.common;

import android.provider.BaseColumns;

import com.example.tomas.common.DatabaseContract;



public class DatabaseContract {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "database.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String NUM_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String DATE_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DatabaseContract() {
    }

    public static abstract class People implements BaseColumns {
        public static final String TABLE_NAME = "People";
        public static final String COLUMN_COZ = "coz";
        public static final String COLUMN_NAME = "meno";
        public static final String COLUMN_RANK = "hodnost";
        public static final String COLUMN_TITLE = "titul";
        public static final String COLUMN_CENTRUM = "odbor";
        public static final String COLUMN_BORN = "narodeny";
        public static final String COLUMN_GENDER = "pohlavie";
        public static final String COLUMN_GROUP = "skupina";
        public static final String COLUMN_PHYSICAL_ATTEMPT = "telesnaAtt";
        public static final String COLUMN_SHOOTS_ATTEMPT = "strelbyAtt";
        public static final String COLUMN_GUN = "zbran";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_COZ + TEXT_TYPE + " PRIMARY KEY," +
                COLUMN_RANK + TEXT_TYPE + COMMA_SEP +
                COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_CENTRUM + TEXT_TYPE + COMMA_SEP +
                COLUMN_BORN + NUM_TYPE + COMMA_SEP +
                COLUMN_GENDER + TEXT_TYPE + COMMA_SEP +
                COLUMN_GROUP + TEXT_TYPE + COMMA_SEP +
                COLUMN_PHYSICAL_ATTEMPT + NUM_TYPE + "  DEFAULT 1" + COMMA_SEP +
                COLUMN_SHOOTS_ATTEMPT + NUM_TYPE + "  DEFAULT 1" + COMMA_SEP +
                COLUMN_GUN + TEXT_TYPE + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class PhysicalSessions implements BaseColumns {
        public static final String TABLE_NAME = "PhysicalSessions";
        public static final String COLUMN_ACTUAL_NUM = "dressNumber";
        public static final String COLUMN_TIMESTAMP = "cas";
        public static final String COLUMN_START_CAS = "start_cas_stadion";
        public static final String COLUMN_STOP_CAS = "stop_cas_stadion";


        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_ACTUAL_NUM + NUM_TYPE + COMMA_SEP +
                COLUMN_TIMESTAMP + DATE_TYPE + "NOT NULL DEFAULT CURRENT_TIMESTAMP" + COMMA_SEP +
                COLUMN_START_CAS + NUM_TYPE + " DEFAULT 0" + COMMA_SEP +
                COLUMN_STOP_CAS + NUM_TYPE + " DEFAULT 0" + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class ShootsSessions implements BaseColumns {
        public static final String TABLE_NAME = "ShootsSessions";
        public static final String COLUMN_ACTUAL_NUM = "dressNumber";
        public static final String COLUMN_TIMESTAMP = "cas";
        public static final String COLUMN_BULLET_COUNT = "bulletCount";
        public static final String COLUMN_PARENT_SESSION = "parent_session";
        //TODO there cannot be more than one corrective session for one session
        public static final String COLUMN_CHILD_ORDER_NUM = "child_order";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_ACTUAL_NUM + NUM_TYPE + COMMA_SEP +
                COLUMN_TIMESTAMP + DATE_TYPE + "NOT NULL DEFAULT CURRENT_TIMESTAMP" + COMMA_SEP +
                COLUMN_BULLET_COUNT + NUM_TYPE + COMMA_SEP +
                COLUMN_PARENT_SESSION + NUM_TYPE + " DEFAULT NULL" + COMMA_SEP +
                COLUMN_CHILD_ORDER_NUM + NUM_TYPE + " DEFAULT 0" + COMMA_SEP +
                " FOREIGN KEY (" + COLUMN_PARENT_SESSION + ") REFERENCES " + TABLE_NAME + "(" + _ID + "))";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class TelesnaResults implements BaseColumns {
        /*
        unique Key - SesionId and DressNumber - one dressNum in one session
        be aware of adding 1 personID to more results in one Session - not set as unique but it is mistake
         */
        public static final String TABLE_NAME = "TelesnaResults";
        public static final String COLUMN_SESSSION_ID = "sessionID";/* int */
        public static final String COLUMN_PERSON_ID = "coz";        /* int */
        public static final String COLUMN_DRESS_NUMBER = "dress";   /* 1-MaxInt*/
        public static final String COLUMN_PULL_UP = "zhyby";        /* int */
        public static final String COLUMN_JUMP = "skok";            /* int */
        public static final String COLUMN_CRUNCH = "lahsed";        /* int */
        public static final String COLUMN_SPRINT = "sprint";          /* int */
        public static final String COLUMN_12min = "beh";          /* int */
        public static final String COLUMN_SWIMMING = "plavanie";    /* int */
        public static final String COLUMN_PASSED = "hodnotenie";    /* 0-1 */

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_SESSSION_ID + NUM_TYPE + COMMA_SEP +
                COLUMN_PERSON_ID + TEXT_TYPE + COMMA_SEP +
                COLUMN_DRESS_NUMBER + NUM_TYPE + COMMA_SEP +
                COLUMN_PULL_UP + NUM_TYPE + "  DEFAULT 0" + COMMA_SEP +
                COLUMN_JUMP + NUM_TYPE + "  DEFAULT 0" + COMMA_SEP +
                COLUMN_CRUNCH + NUM_TYPE + "  DEFAULT 0" + COMMA_SEP +
                COLUMN_SPRINT + REAL_TYPE + "  DEFAULT 0" + COMMA_SEP +
                COLUMN_12min + NUM_TYPE + "  DEFAULT 0" + COMMA_SEP +
                COLUMN_SWIMMING + REAL_TYPE + "  DEFAULT 0" + COMMA_SEP +
                COLUMN_PASSED + NUM_TYPE + "  DEFAULT 0" + COMMA_SEP +
                " UNIQUE(" + COLUMN_SESSSION_ID + "," + COLUMN_DRESS_NUMBER + ")" +
                ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class StrelbyResults implements BaseColumns {
        /*
        table for n-n relationship
        unique Key - SesionId and DressNumber - one dressNum in one session
        be aware of adding 1 personID to more results in one Session - not set as unique but it is mistake
         */
        public static final String TABLE_NAME = "StrelbyResults";
        public static final String COLUMN_SESSSION_ID = "sessionID";
        public static final String COLUMN_PERSON_ID = "coz";
        public static final String COLUMN_DRESS_NUMBER = "dress";
        public static final String COLUMN_POINTS = "body";
        public static final String COLUMN_TIME = "cas";
        public static final String COLUMN_RATIO = "koeficient";   // points / time
        public static final String COLUMN_NULLTARGET = "nulovy_terc"; // OK - alright, N - null target, D - disqalified
        public static final String COLUMN_STATUS = "status";    // 0,1

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_SESSSION_ID + NUM_TYPE + COMMA_SEP +
                COLUMN_PERSON_ID + TEXT_TYPE + COMMA_SEP +
                COLUMN_DRESS_NUMBER + NUM_TYPE + COMMA_SEP +
                COLUMN_POINTS + NUM_TYPE + "  DEFAULT 0" + COMMA_SEP +
                COLUMN_TIME + REAL_TYPE + "  DEFAULT 0.0" + COMMA_SEP +
                COLUMN_RATIO + REAL_TYPE + "  DEFAULT 0.0" + COMMA_SEP +
                COLUMN_NULLTARGET + TEXT_TYPE + " DEFAULT \"OK\"" + COMMA_SEP +
                COLUMN_STATUS + NUM_TYPE + "  DEFAULT 0" + COMMA_SEP +
                " UNIQUE(" + COLUMN_SESSSION_ID + "," + COLUMN_DRESS_NUMBER + ")" +
                ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


}
