package com.example.tomas.telesna;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.tomas.common.CentrumENUM;
import com.example.tomas.common.ExternalStorage;
import com.example.tomas.common.RankENUM;
import com.example.tomas.common.DatabaseContract;
import com.example.tomas.common.Person;
import com.example.tomas.common.Session;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by tomas on 19.8.2016.
 * DATABASE HELPER
 */

public class FitnessDatabaseHelper extends SQLiteOpenHelper {
    public FitnessDatabaseHelper(Context context) {
        super(context, ExternalStorage.getNormalPath()
                + File.separator + "dab"
                + File.separator + DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.People.CREATE_TABLE);
        db.execSQL(DatabaseContract.PhysicalSessions.CREATE_TABLE);
        db.execSQL(DatabaseContract.ShootsSessions.CREATE_TABLE);
        db.execSQL(DatabaseContract.TelesnaResults.CREATE_TABLE);
        db.execSQL(DatabaseContract.StrelbyResults.CREATE_TABLE);
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.People.DELETE_TABLE);
        db.execSQL(DatabaseContract.PhysicalSessions.DELETE_TABLE);
        db.execSQL(DatabaseContract.ShootsSessions.DELETE_TABLE);
        db.execSQL(DatabaseContract.TelesnaResults.DELETE_TABLE);
        db.execSQL(DatabaseContract.StrelbyResults.DELETE_TABLE);
        onCreate(db);
    }

    public void createOrUpdatePerson(Person person) {
        /* add Person to table , if id exist replace the person info, if person was created and have some additional info add it
         * do not update person attempt here because it would be updated every time to 1 */
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.People.COLUMN_COZ, person.id);
        values.put(DatabaseContract.People.COLUMN_RANK, person.getRank().name());
        values.put(DatabaseContract.People.COLUMN_NAME, person.name);
        values.put(DatabaseContract.People.COLUMN_BORN, person.getDateOfBirth());
        values.put(DatabaseContract.People.COLUMN_CENTRUM, person.getCentrum().name());
        values.put(DatabaseContract.People.COLUMN_GROUP, person.getGroup());
        values.put(DatabaseContract.People.COLUMN_GENDER, person.getGender());

        if (findPerson(person.id) != null) {
            Person additionalInfo = findPerson(person.id);

            values.put(DatabaseContract.People.COLUMN_TITLE, additionalInfo.getTitle());
            values.put(DatabaseContract.People.COLUMN_PHYSICAL_ATTEMPT, additionalInfo.getAttempt());
            values.put(DatabaseContract.People.COLUMN_GUN, additionalInfo.getGun());
        }

        SQLiteDatabase db = this.getWritableDatabase();

        db.insertWithOnConflict(DatabaseContract.People.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    /* Najde osobu v DB podla COZ */

    public Person findPerson(String coz) {
        String query = "Select * FROM " + DatabaseContract.People.TABLE_NAME + " WHERE " + DatabaseContract.People.COLUMN_COZ + " =  \"" + coz + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        Person person;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_COZ));
            String rank = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_RANK));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_NAME));
            String centrum = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_CENTRUM));
            Integer dateOfBirth = cursor.getInt(cursor.getColumnIndex(DatabaseContract.People.COLUMN_BORN));
            String group = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_GROUP));
            String gender = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_GENDER));
            String gun = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_GUN));

            String title = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_TITLE));
            Integer attempt = cursor.getInt(cursor.getColumnIndex(DatabaseContract.People.COLUMN_PHYSICAL_ATTEMPT));
            cursor.close();
            person = new Person(id, RankENUM.validate(rank), name, CentrumENUM.validate(centrum), dateOfBirth, group, gender, gun);


            person.setTitle(title);


            /* when bad imported */
            if (attempt < 1) {
                person.setAttempt(1);
                updatePersonAttempt(person, 1);
            } else {
                person.setAttempt(attempt);
            }
        } else {
            person = null;
        }
        db.close();
        return person;
    }

    public void updatePersonAttempt(Person person, Integer attempt) {
        /* add new session to the db table and return as a Session object*/
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.People.COLUMN_PHYSICAL_ATTEMPT, attempt);
        SQLiteDatabase db = this.getWritableDatabase();

        db.update(DatabaseContract.People.TABLE_NAME, values, DatabaseContract.People.COLUMN_COZ + "=" + person.id, null);
        db.close();
    }

    // << ------------------------------------------ SESSIONS------------------------------------------------->>

    public Session createSession(Integer actualDressNum) {
        /* add new session to the db table and return as a Session object*/
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PhysicalSessions.COLUMN_ACTUAL_NUM, actualDressNum);
        SQLiteDatabase db = this.getWritableDatabase();

        Long id = db.insert(DatabaseContract.PhysicalSessions.TABLE_NAME, null, values);
        db.close();

        return findSession(id);
    }

    public Session createSession(Integer actualDressNum, Date date) {
        /* add new session to the db table and return as a Session object*/
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PhysicalSessions.COLUMN_ACTUAL_NUM, actualDressNum);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        values.put(DatabaseContract.PhysicalSessions.COLUMN_TIMESTAMP, dateFormat.format(date));
        SQLiteDatabase db = this.getWritableDatabase();

        Long id = db.insert(DatabaseContract.PhysicalSessions.TABLE_NAME, null, values);
        db.close();

        return findSession(id);
    }



    public List<Session> getSessions() {
        String query = "Select * FROM " + DatabaseContract.PhysicalSessions.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        List<Session> sessions = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                Long sessionId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.PhysicalSessions._ID));
                Integer dressNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.PhysicalSessions.COLUMN_ACTUAL_NUM));
                String timeStamp = cursor.getString(cursor.getColumnIndex(DatabaseContract.PhysicalSessions.COLUMN_TIMESTAMP));
                long startStadiumTime = cursor.getLong(cursor.getColumnIndex(DatabaseContract.PhysicalSessions.COLUMN_START_CAS));
                long endStadiumTime = cursor.getLong(cursor.getColumnIndex(DatabaseContract.PhysicalSessions.COLUMN_STOP_CAS));
                Session ses = new Session(sessionId, dressNum);
                ses.setTimeStamp(Timestamp.valueOf(timeStamp));
                ses.setStartStadiumTime(startStadiumTime);
                ses.setEndStadiumTime(endStadiumTime);
                sessions.add(ses);
            }
            return sessions;
        } finally {
            cursor.close();
        }
    }

    public void updateSessionDress(Session session, Integer actualDressNum) {
        /* add new session to the db table and return as a Session object*/
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PhysicalSessions.COLUMN_ACTUAL_NUM, actualDressNum);
        SQLiteDatabase db = this.getWritableDatabase();

        db.update(DatabaseContract.PhysicalSessions.TABLE_NAME, values, DatabaseContract.PhysicalSessions._ID + "=" + session.id.toString(), null);
        db.close();
    }

    public void updateSessionStartTime(Session session) {
        /* add new session to the db table and return as a Session object*/
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PhysicalSessions.COLUMN_START_CAS, session.getStartStadiumTime());
        SQLiteDatabase db = this.getWritableDatabase();

        db.update(DatabaseContract.PhysicalSessions.TABLE_NAME, values, DatabaseContract.PhysicalSessions._ID + "=" + session.id.toString(), null);
        db.close();
    }

    public void updateSessionEndTime(Session session) {
        /* add new session to the db table and return as a Session object*/
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PhysicalSessions.COLUMN_STOP_CAS, session.getEndStadiumTime());
        SQLiteDatabase db = this.getWritableDatabase();

        db.update(DatabaseContract.PhysicalSessions.TABLE_NAME, values, DatabaseContract.PhysicalSessions._ID + "=" + session.id.toString(), null);
        db.close();
    }

    public Session findSession(Long sessionId) {
        String query = "Select * FROM " + DatabaseContract.PhysicalSessions.TABLE_NAME + " WHERE " + DatabaseContract.PhysicalSessions._ID + " =  \"" + sessionId + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Session session = null;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            sessionId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.PhysicalSessions._ID));
            Integer dressNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.PhysicalSessions.COLUMN_ACTUAL_NUM));
            String timeStamp = cursor.getString(cursor.getColumnIndex(DatabaseContract.PhysicalSessions.COLUMN_TIMESTAMP));
            long startStadiumTime = cursor.getLong(cursor.getColumnIndex(DatabaseContract.PhysicalSessions.COLUMN_START_CAS));
            long endStadiumTime = cursor.getLong(cursor.getColumnIndex(DatabaseContract.PhysicalSessions.COLUMN_STOP_CAS));
            session = new Session(sessionId, dressNum);
            session.setTimeStamp(Timestamp.valueOf(timeStamp));
            session.setStartStadiumTime(startStadiumTime);
            session.setEndStadiumTime(endStadiumTime);
        }

        cursor.close();
        return session;
    }

    public void addOrReplacePersonToSession(Session session, Person person, Integer dressNum) {
        /* add person to session, if dressNUm already exist in Session replace with new */
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID, session.id);
        values.put(DatabaseContract.TelesnaResults.COLUMN_PERSON_ID, person.id);
        values.put(DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER, dressNum);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insertWithOnConflict(DatabaseContract.TelesnaResults.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public PhysicalResult findPersonInSessionByDress(Session session, Integer dressNum) {
        String query = "Select * FROM " + DatabaseContract.TelesnaResults.TABLE_NAME + " WHERE " + DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID + " =  \"" + session.id + "\" AND " + DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER + " =  \"" + dressNum + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        PhysicalResult result;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            Long sessionId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID));
            String personId = cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_PERSON_ID));
            dressNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER));
            HashMap<String, String> results = new HashMap<String, String>();
            results.put("pull_up", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_PULL_UP)));
            results.put("jump", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_JUMP)));
            results.put("crunch", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_CRUNCH)));
            results.put("sprint", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_SPRINT)));
            results.put("12min", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_12min)));
            results.put("swimming", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_SWIMMING)));
            cursor.close();
            result = new PhysicalResult(findSession(sessionId), findPerson(personId), dressNum);
            result.setResults(results);
        } else {
            result = null;
        }
        db.close();
        return result;
    }

    public PhysicalResult findPersonInSessionById(Session session, String personId) {
        String query = "Select * FROM " + DatabaseContract.TelesnaResults.TABLE_NAME + " WHERE " + DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID + " =  \"" + session.id + "\" AND " + DatabaseContract.TelesnaResults.COLUMN_PERSON_ID + " =  \"" + personId + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        PhysicalResult result;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            Long sessionId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID));
            personId = cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_PERSON_ID));
            Integer dressNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER));
            HashMap<String, String> results = new HashMap<String, String>();
            results.put("pull_up", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_PULL_UP)));
            results.put("jump", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_JUMP)));
            results.put("crunch", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_CRUNCH)));
            results.put("sprint", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_SPRINT)));
            results.put("12min", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_12min)));
            results.put("swimming", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_SWIMMING)));
            cursor.close();
            result = new PhysicalResult(findSession(sessionId), findPerson(personId), dressNum);
            result.setResults(results);
        } else {
            result = null;
        }
        db.close();
        return result;
    }

    public void updatePersonTelesnaResults(Session session, Integer actualDressNum, HashMap<String, String> results) {
        /* add new session to the db table and return as a Session object*/
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.TelesnaResults.COLUMN_PULL_UP, results.get("pull_up").toString());
        values.put(DatabaseContract.TelesnaResults.COLUMN_JUMP, results.get("jump").toString());
        values.put(DatabaseContract.TelesnaResults.COLUMN_CRUNCH, results.get("crunch").toString());
        values.put(DatabaseContract.TelesnaResults.COLUMN_SPRINT, results.get("sprint").toString());
        values.put(DatabaseContract.TelesnaResults.COLUMN_12min, results.get("12min").toString());
        values.put(DatabaseContract.TelesnaResults.COLUMN_SWIMMING, results.get("swimming").toString());
        SQLiteDatabase db = this.getWritableDatabase();

        db.update(
                DatabaseContract.TelesnaResults.TABLE_NAME, values, DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID + "=" + session.id.toString()
                        + " AND " + DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER + "=" + actualDressNum, null
        );
        db.close();
    }

    //TODO delete
    public void addRoundToPerson(Session session, Integer dress) {
        long start2 = SystemClock.uptimeMillis();
        PhysicalResult result = findPersonInSessionByDress(session, dress);
        Log.d("AfterFind", String.valueOf(
                (SystemClock.uptimeMillis() - start2)
        ));
        long start3 = SystemClock.uptimeMillis();
        Integer newValue = Integer.parseInt(result.disciplines.get("12min").toString()) + result.ROUND;
        result.disciplines.put("12min", newValue.toString());
        updatePersonTelesnaResults(session, dress, result.disciplines);
        Log.d("AfterUpdate", String.valueOf(
                (SystemClock.uptimeMillis() - start3)
        ));
    }

    //TODO refactor
    public void addRoundToPerson(Session session, List<Integer> dresses) {
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder stringBuilder = new StringBuilder();
        for (Integer dress : dresses) {
            stringBuilder.append(dress);
            stringBuilder.append(",");
        }

        db.execSQL("UPDATE " + DatabaseContract.TelesnaResults.TABLE_NAME +
                " SET " + DatabaseContract.TelesnaResults.COLUMN_12min + " = " + (DatabaseContract.TelesnaResults.COLUMN_12min + " + " + PhysicalResult.ROUND)
                + " WHERE " + DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID + " = " + session.id.toString()
                + " AND " + DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER + " IN (" + stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1) + ")");
        db.close();
    }

    public void addRoundToPerson(Session session, Integer dress, Integer round) {
        PhysicalResult result = findPersonInSessionByDress(session, dress);
        Integer newValue = Integer.parseInt(result.disciplines.get("12min").toString()) + round;
        result.disciplines.put("12min", newValue.toString());
        updatePersonTelesnaResults(session, dress, result.disciplines);
    }

    public void deleteRoundFromPerson(Session session, List<Integer> dresses) {
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder stringBuilder = new StringBuilder();
        for (Integer dress : dresses) {
            stringBuilder.append(dress);
            stringBuilder.append(",");
        }

        db.execSQL("UPDATE " + DatabaseContract.TelesnaResults.TABLE_NAME +
                " SET " + DatabaseContract.TelesnaResults.COLUMN_12min + " = " + (DatabaseContract.TelesnaResults.COLUMN_12min + " - " + PhysicalResult.ROUND)
                + " WHERE " + DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID + " = " + session.id.toString()
                + " AND " + DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER + " IN (" + stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1) + ")");
        db.close();
    }

    public void deleteRoundFromPerson(Session session, Integer dress) {
        /* delete round from person, if round less then zero zero stays, for stepbacker*/
        PhysicalResult result = findPersonInSessionByDress(session, dress);
        Integer newValue = (Integer.parseInt(result.disciplines.get("12min").toString()) - result.ROUND) >= 0 ? (Integer.parseInt(result.disciplines.get("12min").toString()) - result.ROUND) : 0;
        result.disciplines.put("12min", newValue.toString());
        updatePersonTelesnaResults(session, dress, result.disciplines);
    }

    public List<PhysicalResult> getAllTelesnaResultsinSession(Session session) {
        String query = "Select * FROM " + DatabaseContract.TelesnaResults.TABLE_NAME + " WHERE " + DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID + " =  \"" + session.id + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        int length = cursor.getCount();
        List<PhysicalResult> results = new ArrayList<PhysicalResult>();

        try {
            while (cursor.moveToNext()) {
                Long sessionId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID));
                String personId = cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_PERSON_ID));
                Integer dressNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER));
                HashMap<String, String> disciplines = new HashMap<String, String>();
                disciplines.put("pull_up", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_PULL_UP)));
                disciplines.put("jump", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_JUMP)));
                disciplines.put("crunch", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_CRUNCH)));
                disciplines.put("sprint", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_SPRINT)));
                disciplines.put("12min", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_12min)));
                disciplines.put("swimming", cursor.getString(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_SWIMMING)));
                results.add(new PhysicalResult(findSession(sessionId), findPerson(personId), dressNum, disciplines));
            }
        } finally {
            cursor.close();
        }
        db.close();
        return results;
    }

    public boolean deleteResult(Session session, Integer dressNumber) {
        return getWritableDatabase().delete(DatabaseContract.DATABASE_NAME, DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER + "=" + dressNumber, null) > 0;
    }

    public ArrayList<Integer> getOrderedDressFromSession(Session session) {
        String query = "Select " + DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER + " FROM " + DatabaseContract.TelesnaResults.TABLE_NAME + " WHERE " + DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID + " =  \"" + session.id + "\"" + " ORDER BY " + DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Integer> dresses = new ArrayList<Integer>();

        try {
            while (cursor.moveToNext()) {
                Integer dressNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER));
                dresses.add(dressNum);
            }
        } finally {
            cursor.close();
        }
        db.close();
        return dresses;
    }

    public Integer getMaxDressInSession(Session session) {
        String query = "Select MAX(" + DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER + ") FROM " + DatabaseContract.TelesnaResults.TABLE_NAME +
                " WHERE " + DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID + " = " + session.id.toString();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        Integer dressNum;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            dressNum = cursor.getInt(0);
            cursor.close();
        } else {
            dressNum = null;
        }
        db.close();
        return dressNum;
    }

    public Integer getMinDressInSession(Session session) {
        /* return minimal dress number from session, if no dress exist returns 1 */
        String query = "Select min(" + DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER + ")FROM " + DatabaseContract.TelesnaResults.TABLE_NAME +
                " WHERE " + DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID + " = " + session.id.toString();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        Integer dressNum;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            dressNum = cursor.getInt(0);
            cursor.close();
        } else {
            dressNum = 1;
        }
        db.close();
        return dressNum;
    }


    public Integer getMinDressWithFilled12Min(Session session) {
        /* return minimal dress number from session, if no dress exist returns 1 */
        String query = "Select min(" + DatabaseContract.TelesnaResults.COLUMN_DRESS_NUMBER + ")FROM " + DatabaseContract.TelesnaResults.TABLE_NAME +
                " WHERE " + DatabaseContract.TelesnaResults.COLUMN_SESSSION_ID + " = " + session.id.toString()
                + " AND " + DatabaseContract.TelesnaResults.COLUMN_12min + " = 0 " ;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        Integer dressNum;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            dressNum = cursor.getInt(0);
            cursor.close();
        } else {
            dressNum = 1;
        }
        db.close();
        return dressNum;
    }

    public Session getLatestSession() {
        String query = "Select * FROM " + DatabaseContract.PhysicalSessions.TABLE_NAME +
                " WHERE " + DatabaseContract.PhysicalSessions.COLUMN_TIMESTAMP + "= ( SELECT MAX(" + DatabaseContract.PhysicalSessions.COLUMN_TIMESTAMP +
                ") FROM " + DatabaseContract.PhysicalSessions.TABLE_NAME + ")";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Session session;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            Long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.PhysicalSessions._ID));
            Integer actualNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.PhysicalSessions.COLUMN_ACTUAL_NUM));
            String timeStamp = cursor.getString(cursor.getColumnIndex(DatabaseContract.PhysicalSessions.COLUMN_TIMESTAMP));
            long startStadiumTime = cursor.getLong(cursor.getColumnIndex(DatabaseContract.PhysicalSessions.COLUMN_START_CAS));
            long endStadiumTime = cursor.getLong(cursor.getColumnIndex(DatabaseContract.PhysicalSessions.COLUMN_STOP_CAS));
            cursor.close();
            session = new Session(id, actualNum);
            session.setTimeStamp(Timestamp.valueOf(timeStamp));
            session.setStartStadiumTime(startStadiumTime);
            session.setEndStadiumTime(endStadiumTime);
        } else {
            session = null;
        }
        db.close();
        return session;
    }

}