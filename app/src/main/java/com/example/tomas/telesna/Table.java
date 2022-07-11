package com.example.tomas.telesna;

/**
 * Created by tomas on 3.2.2017.
 *       Template for point tables
 */

public class Table {

    Integer minAge;
    Integer maxAge;
    private Integer pointsMax = 10;
    Float[][] pointTable;

    public Integer getPointsMax() {
        return this.pointsMax;
    }
}
