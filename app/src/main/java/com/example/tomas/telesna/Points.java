package com.example.tomas.telesna;

/**
 * Created by tomas on 17.8.2016.
 * class for storing people point results
 */
public class Points {

    Integer summary;
    boolean passed;
    Integer[] disciplines; /* array indexes with disciplines */

    public Points(){
        this.disciplines = new Integer[Indexes.NUM_OF_DISCIPLINES];
    }


}
