package com.example.tomas.strelby;

import com.example.tomas.common.Person;
import com.example.tomas.common.Session;

/**
 * Created by tomas on 21.8.2016.
 *
 * nullTarget - OK - ok, D- diqualified, N - nullTarget
 * status - y or n
 *
 */
public class ShootResult {
    Person person;
    Session session;
    Integer dressNum;
    private float ratio;
    private String nullTarget;
    private boolean status;
    private int points;
    private float time;
    public static final Float PERCENTIL_CONSTANT = 1.1f;
    public static final  Float secondGroupTime = 120f;

    public Integer getDressNum() {
        return dressNum;
    }

    public ShootResult(Session session, Person person, Integer dressNum){
        this.session = session;
        this.person = person;
        this.dressNum = dressNum;
        this.nullTarget = "OK";
    }


    public void setPoints(Integer points){
        this.points = points;
    }
    public Integer getPoints(){return points;}
    public void setTime(Float time){this.time = time;}
    public Float getTime(){return time;}
    public void setRatio(Float ratio){this.ratio = ratio;}
    public Float getRatio(){return ratio;}
    public String getNullTarget(){
        return nullTarget;
    }
    public void setNullTarget(String t){ nullTarget = t;}
    public void setStatus(boolean s){ this.status =s;}
    public void setStatus(Integer s){ this.status = s== 0 ? false : true;
    }

    public boolean getStatus(){
        /* get status of person related to Shootresult */
        return ShootResult.calculateStatus(time, points, person.getGroup(),nullTarget);
    }

    static boolean calculateStatus(Float time, Integer points, String group,String nullTarget){
        /* calculate status , based on time, points and group */
        boolean stat = false;

        if (group.equals("II") && nullTarget.equals("OK")) {
            if (points >= 6) {
                stat = true;
            } else {
                stat = false;
            }
        } else if (group.equals("I") && nullTarget.equals("OK")) {
            Float percentil  = points/time;
            if(percentil>=ShootResult.PERCENTIL_CONSTANT){
                stat = true;
            }else{
                stat = false;
            }
        }

        return stat;
    }
    static Float calculateRatio(Float time, Integer points, String group){
        /* calculate status , based on time, points and group */
        Float ratio;
        if(group.equals("I")) {
            if (points != 0 && time != 0) {
                ratio = points / time;
            } else {
                ratio = 0f;
            }
        }else{
            ratio = 0f;
        }
        return ratio;
    }
}
