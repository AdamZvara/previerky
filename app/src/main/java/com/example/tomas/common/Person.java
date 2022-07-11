package com.example.tomas.common;

/*Created by tomas on 17.8.2016.
 * Person - store values of person
 * id - coz number
 * name - name of person
 * group - I or II
 * gender - m or f
 * gun - L or M
 */
public class Person {

    public String id;
    private String title;
    private RankENUM rank;
    public String name;
    private CentrumENUM centrum;
    private Integer dateOfBirth;
    private String group;
    private String gender;
    private String gun;
    private Integer attempt;

    public Person(String id,RankENUM rank, String name,CentrumENUM centrum, Integer dateOfBirth, String group, String gender, String gun){
        this.id = id;
        setTitle(title);
        setRank(rank);
        this.name = name;
        setCentrum(centrum);
        setDateOfBirth(dateOfBirth);
        setGroup(group);
        setGender(gender);
        setGun(gun);
    }

    public void setTitle(String title){this.title = title;}
    public String getTitle(){return title == null ? "" : title;}

    public void setRank(RankENUM rank){
        this.rank = rank;}
    public RankENUM getRank(){return rank;}

    public void setCentrum(CentrumENUM centrum){
        this.centrum = centrum;}
    public CentrumENUM getCentrum(){return centrum;}

    public void setDateOfBirth(Integer date){
        this.dateOfBirth = date;
    }
    public Integer getDateOfBirth(){return this.dateOfBirth;}

    public void setGroup(String group){
        this.group =group;
    }
    public String getGroup(){
        return this.group;
    }

    public void setGender(String gender){
        this.gender = gender;
    }
    public String getGender(){
        return this.gender;
    }


    public void setGun(String gun){ this.gun = gun;}
    public String getGun(){return gun == null ? "" : gun ;}

    public void setAttempt(Integer attempt){this.attempt = attempt;}
    public Integer getAttempt(){return attempt;}
}
