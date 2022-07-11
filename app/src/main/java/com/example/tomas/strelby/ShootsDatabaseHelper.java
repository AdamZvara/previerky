package com.example.tomas.strelby;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.example.tomas.common.CentrumENUM;
import com.example.tomas.common.ExternalStorage;
import com.example.tomas.common.RankENUM;
import com.example.tomas.common.DatabaseContract;
import com.example.tomas.common.Person;
import com.example.tomas.common.Session;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ShootsDatabaseHelper extends SQLiteOpenHelper {
    public ShootsDatabaseHelper(Context context) {
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
        db.execSQL(DatabaseContract.TelesnaResults.DELETE_TABLE);
        db.execSQL(DatabaseContract.PhysicalSessions.DELETE_TABLE);
        db.execSQL(DatabaseContract.ShootsSessions.DELETE_TABLE);
        db.execSQL(DatabaseContract.StrelbyResults.DELETE_TABLE);
        onCreate(db);
    }

    public void createOrUpdatePerson(Person person) {
        /* add Person to table , if id exist replace the person info /* if person was created and have some additional info add it*/
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.People.COLUMN_COZ, person.id);
        values.put(DatabaseContract.People.COLUMN_RANK, person.getRank().name());
        values.put(DatabaseContract.People.COLUMN_CENTRUM, person.getCentrum().name());
        values.put(DatabaseContract.People.COLUMN_NAME, person.name);
        values.put(DatabaseContract.People.COLUMN_BORN, person.getDateOfBirth());
        values.put(DatabaseContract.People.COLUMN_GROUP, person.getGroup());
        values.put(DatabaseContract.People.COLUMN_GENDER, person.getGender());
        values.put(DatabaseContract.People.COLUMN_GUN, person.getGun());
        if (findPerson(person.id) != null) {
            Person additionalInfo = findPerson(person.id);

            values.put(DatabaseContract.People.COLUMN_TITLE, additionalInfo.getTitle());

            values.put(DatabaseContract.People.COLUMN_SHOOTS_ATTEMPT, additionalInfo.getAttempt());
        }

        SQLiteDatabase db = this.getWritableDatabase();

        db.insertWithOnConflict(DatabaseContract.People.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public Person findPerson(String coz) {
        String query = "Select * FROM " + DatabaseContract.People.TABLE_NAME + " WHERE " + DatabaseContract.People.COLUMN_COZ + " =  \"" + coz + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Person person;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_COZ));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_NAME));
            Integer dateOfBirth = cursor.getInt(cursor.getColumnIndex(DatabaseContract.People.COLUMN_BORN));
            String group = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_GROUP));
            String gender = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_GENDER));
            String rank = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_RANK));
            String title = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_TITLE));
            String centrum = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_CENTRUM));
            String gun = cursor.getString(cursor.getColumnIndex(DatabaseContract.People.COLUMN_GUN));
            Integer attempt = cursor.getInt(cursor.getColumnIndex(DatabaseContract.People.COLUMN_SHOOTS_ATTEMPT));
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
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.People.COLUMN_SHOOTS_ATTEMPT, attempt);
        SQLiteDatabase db = this.getWritableDatabase();

        db.update(DatabaseContract.People.TABLE_NAME, values, DatabaseContract.People.COLUMN_COZ + "=" + person.id.toString(), null);
        db.close();
    }
    // << ------------------------------------------ SESSIONS------------------------------------------------->>

    public Session createSession(Integer actualDressNum, Long parentSessionId) {
        /* add new session to the db table and return as a Session object*/
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ShootsSessions.COLUMN_ACTUAL_NUM, actualDressNum);

        if (parentSessionId != null) {
            Session parentSession = findSession(parentSessionId);
            values.put(DatabaseContract.ShootsSessions.COLUMN_PARENT_SESSION, parentSession.id);
            values.put(DatabaseContract.ShootsSessions.COLUMN_CHILD_ORDER_NUM, parentSession.getChildOrderNumber() + 1);
        }
        SQLiteDatabase db = this.getWritableDatabase();

        Long id = db.insert(DatabaseContract.ShootsSessions.TABLE_NAME, null, values);
        db.close();

        // TODO: 24. 5. 2018 return just ID
        return findSession(id);
    }

    public void updateSessionDress(Session session, Integer actualDressNum) {
        /* add new session to the db table and return as a Session object*/
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ShootsSessions.COLUMN_ACTUAL_NUM, actualDressNum);
        SQLiteDatabase db = this.getWritableDatabase();

        db.update(DatabaseContract.ShootsSessions.TABLE_NAME, values, DatabaseContract.ShootsSessions._ID + "=" + session.id.toString(), null);
        db.close();

    }

    /**
     * set number of bullets
     *
     * @param session session id
     * @param bullets number of bullets
     */
    void updateSessionNumOfBullets(Session session, Integer bullets) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ShootsSessions.COLUMN_BULLET_COUNT, bullets);
        SQLiteDatabase db = this.getWritableDatabase();

        db.update(DatabaseContract.ShootsSessions.TABLE_NAME, values, DatabaseContract.ShootsSessions._ID + "=" + session.id.toString(), null);
        db.close();
    }

    public Session findSession(Long id) {
        String query = "Select * FROM " + DatabaseContract.ShootsSessions.TABLE_NAME + " WHERE " + DatabaseContract.ShootsSessions._ID + " =  \"" + id + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Session session;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.ShootsSessions._ID));
            Integer actualNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ShootsSessions.COLUMN_ACTUAL_NUM));
            Integer numOfBullets = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ShootsSessions.COLUMN_BULLET_COUNT));
            String timeStamp = cursor.getString(cursor.getColumnIndex(DatabaseContract.ShootsSessions.COLUMN_TIMESTAMP));
            Long parentSessionId = null;
            if (!cursor.isNull(cursor.getColumnIndex(DatabaseContract.ShootsSessions.COLUMN_PARENT_SESSION))) {
                parentSessionId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.ShootsSessions.COLUMN_PARENT_SESSION));
            }
            Integer childOrderNumber = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ShootsSessions.COLUMN_CHILD_ORDER_NUM));
            cursor.close();
            session = new Session(id, actualNum);
            session.setNumOfBullets(numOfBullets);
            session.setTimeStamp(Timestamp.valueOf(timeStamp));
            session.setParentSessionId(parentSessionId);
            session.setChildOrderNumber(childOrderNumber);
        } else {
            session = null;
        }
        db.close();
        return session;
    }

    public List<Session> getSessions() {
        String query = "Select * FROM " + DatabaseContract.ShootsSessions.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        List<Session> sessions = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                Long sessionId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.ShootsSessions._ID));
                Integer dressNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ShootsSessions.COLUMN_ACTUAL_NUM));
                String timeStamp = cursor.getString(cursor.getColumnIndex(DatabaseContract.ShootsSessions.COLUMN_TIMESTAMP));
                Long parentSessionId = null;
                if (!cursor.isNull(cursor.getColumnIndex(DatabaseContract.ShootsSessions.COLUMN_PARENT_SESSION))) {
                    parentSessionId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.ShootsSessions.COLUMN_PARENT_SESSION));
                }
                Integer childOrderNumber = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ShootsSessions.COLUMN_CHILD_ORDER_NUM));
                Session ses = new Session(sessionId, dressNum);
                ses.setTimeStamp(Timestamp.valueOf(timeStamp));
                ses.setParentSessionId(parentSessionId);
                ses.setChildOrderNumber(childOrderNumber);
                sessions.add(ses);
            }
            return sessions;
        } finally {
            cursor.close();
        }
    }

    public void addOrReplacePersonToStrelbyResults(Session session, Person person, Integer dressNum) {
        /* add person to session, if dressNUm already exist in Session replace PERSON_ID with new ... for person who has second group add 120 to timer */
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.StrelbyResults.COLUMN_SESSSION_ID, session.id);
        values.put(DatabaseContract.StrelbyResults.COLUMN_PERSON_ID, person.id);
        values.put(DatabaseContract.StrelbyResults.COLUMN_DRESS_NUMBER, dressNum);
        if (person.getGroup().equals("II")) {
            values.put(DatabaseContract.StrelbyResults.COLUMN_TIME, ShootResult.secondGroupTime);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        db.insertWithOnConflict(DatabaseContract.StrelbyResults.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public ShootResult findPersonInStrelbyResultsByDress(Session session, Integer dressNum) {
        String query = "Select * FROM " + DatabaseContract.StrelbyResults.TABLE_NAME + " WHERE " + DatabaseContract.StrelbyResults.COLUMN_SESSSION_ID + " =  \"" + session.id + "\" AND " + DatabaseContract.StrelbyResults.COLUMN_DRESS_NUMBER + " =  \"" + dressNum + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        ShootResult result;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            Long sessionId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_SESSSION_ID));
            String personId = cursor.getString(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_PERSON_ID));
            dressNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_DRESS_NUMBER));
            Integer points = cursor.getInt(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_POINTS));
            Float time = cursor.getFloat((cursor.getColumnIndex((DatabaseContract.StrelbyResults.COLUMN_TIME))));
            Float ratio = cursor.getFloat(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_RATIO));
            String nullTarget = cursor.getString(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_NULLTARGET));
            Integer status = cursor.getInt(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_STATUS));
            cursor.close();
            result = new ShootResult(findSession(sessionId), findPerson(personId), dressNum);
            result.setPoints(points);
            result.setTime(time);
            result.setRatio(ratio);
            result.setStatus(status);
            result.setNullTarget(nullTarget);
        } else {
            result = null;
        }
        db.close();
        return result;
    }

    public ShootResult findPersonInStrelbyResultsById(Session session, String personId) {
        String query = "Select * FROM " + DatabaseContract.StrelbyResults.TABLE_NAME + " WHERE " + DatabaseContract.StrelbyResults.COLUMN_SESSSION_ID + " =  \"" + session.id + "\" AND " + DatabaseContract.StrelbyResults.COLUMN_PERSON_ID + " =  \"" + personId + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        ShootResult result;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            Long sessionId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_SESSSION_ID));
            personId = cursor.getString(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_PERSON_ID));
            Integer dressNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_DRESS_NUMBER));
            Integer points = cursor.getInt(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_POINTS));
            Float time = cursor.getFloat((cursor.getColumnIndex((DatabaseContract.StrelbyResults.COLUMN_TIME))));
            Float ratio = cursor.getFloat(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_RATIO));
            String nullTarget = cursor.getString(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_NULLTARGET));
            Integer status = cursor.getInt(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_STATUS));
            cursor.close();
            result = new ShootResult(findSession(sessionId), findPerson(personId), dressNum);
            result.setPoints(points);
            result.setTime(time);
            result.setRatio(ratio);
            result.setStatus(status);
            result.setNullTarget(nullTarget);
        } else {
            result = null;
        }
        db.close();
        return result;
    }

    public void updatePersonStrelbyResults(Session session, Integer actualDressNum, Integer points, Float time, Float ratio, String nullTarget, boolean status) {
        /* add new session to the db table and return as a Session object
         * if person has second category set target to OK */
        Person person = findPersonInStrelbyResultsByDress(session, actualDressNum).person;
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.StrelbyResults.COLUMN_POINTS, points);
        values.put(DatabaseContract.StrelbyResults.COLUMN_TIME, time);
        values.put(DatabaseContract.StrelbyResults.COLUMN_RATIO, ratio);
        /*if ( person.getGroup().equals("II")){
            values.put(DatabaseContract.StrelbyResults.COLUMN_NULLTARGET,"OK");
        }else {*/
        values.put(DatabaseContract.StrelbyResults.COLUMN_NULLTARGET, nullTarget);
        /*}*/
        values.put(DatabaseContract.StrelbyResults.COLUMN_STATUS, status ? 1 : 0);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(DatabaseContract.StrelbyResults.TABLE_NAME, values, DatabaseContract.StrelbyResults.COLUMN_SESSSION_ID + "=" + session.id.toString() + " AND " + DatabaseContract.StrelbyResults.COLUMN_DRESS_NUMBER + "=" + actualDressNum, null);
        db.close();

    }

    public List<ShootResult> getAllTelesnaResultsinStrelbyResults(Session session) {
        String query = "Select * FROM " + DatabaseContract.StrelbyResults.TABLE_NAME + " WHERE " + DatabaseContract.StrelbyResults.COLUMN_SESSSION_ID + " =  \"" + session.id + "\"";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        int length = cursor.getCount();
        List<ShootResult> results = new ArrayList<ShootResult>();

        try {
            while (cursor.moveToNext()) {
                Long sessionId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_SESSSION_ID));
                String personId = cursor.getString(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_PERSON_ID));
                Integer dressNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_DRESS_NUMBER));
                Integer points = cursor.getInt(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_POINTS));
                Float time = cursor.getFloat(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_TIME));
                Float ratio = cursor.getFloat(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_RATIO));
                String nullTarget = cursor.getString(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_NULLTARGET));
                Integer status = cursor.getInt(cursor.getColumnIndex(DatabaseContract.StrelbyResults.COLUMN_STATUS));
                ShootResult result = new ShootResult(findSession(sessionId), findPerson(personId), dressNum);
                result.setPoints(points);
                result.setTime(time);
                result.setRatio(ratio);
                result.setStatus(status);
                result.setNullTarget(nullTarget);
                results.add(result);
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

    public Integer getMaxDressInSession(Session session) {
        String query = "Select MAX(" + DatabaseContract.StrelbyResults.COLUMN_DRESS_NUMBER + ")FROM " + DatabaseContract.StrelbyResults.TABLE_NAME +
                " WHERE " + DatabaseContract.StrelbyResults.COLUMN_SESSSION_ID + " = " + session.id.toString();
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
        /* return minimal dress number from session, if no dress exist returns 0 */
        String query = "Select min(" + DatabaseContract.StrelbyResults.COLUMN_DRESS_NUMBER + ")FROM " + DatabaseContract.StrelbyResults.TABLE_NAME +
                " WHERE " + DatabaseContract.StrelbyResults.COLUMN_SESSSION_ID + " = " + session.id.toString();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        Integer dressNum;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            dressNum = cursor.getInt(0);
            cursor.close();
        } else {
            dressNum = 0;
        }
        db.close();
        return dressNum;
    }

    /*public Session getLatestSession() {
        String query = "Select * FROM " + DatabaseContract.ShootsSessions.TABLE_NAME +
                " WHERE " + DatabaseContract.ShootsSessions.COLUMN_TIMESTAMP + "= ( SELECT MAX(" + DatabaseContract.ShootsSessions.COLUMN_TIMESTAMP +
                ") FROM " + DatabaseContract.ShootsSessions.TABLE_NAME + ")";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Session session;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            Long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.ShootsSessions._ID));
            Integer actualNum = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ShootsSessions.COLUMN_ACTUAL_NUM));
            cursor.close();
            session = new Session(id, actualNum);
        } else {
            session = null;
        }
        db.close();
        return session;
    }*/
}
