package com.example.tomas.telesna;

import com.example.tomas.common.Person;

import java.util.Calendar;

/**
 * Created by tomas on 3.2.2017.
 *       Point tables
 *       indexes of pointTable -- hrazda, skok, lah-sed, 100,12min, plavanie
 */

public class TableWrapper {

    Table[] menTables;
    Table[] womenTables;
    Float menLimitI = 26f;
    Float menLimitII = 24f;
    Float womenLimitI = 19f;
    Float womenLimitII = 18f;

    public TableWrapper(){
        createMenTables();
        createWomenTables();
    }
    /* ******************************************** TABLES ************************************************* */
    private void createMenTables(){
        this.menTables = new Table[5];

        this.menTables[0]  = new Table();
        this.menTables[0].minAge = 0;
        this.menTables[0].maxAge = 29;
        this.menTables[0].pointTable =  new Float[][]{
                /*0*/   {3f,  170f, 32f, 18f,    1950f, 5f},
                /*1*/   {4f,  180f, 36f, 17f,    2100f, 2.5f},
                /*2*/   {5f,  190f, 40f, 16.5f,  2200f, 2.3f},
                /*3*/   {6f,  202f, 45f, 16f,    2300f, 2.2f},
                /*4*/   {7f,  214f, 50f, 15.5f,  2400f, 2.1f},
                /*5*/   {8f,  226f, 56f, 15f,    2500f, 2f},
                /*6*/   {9f,  238f, 62f, 14.5f,  2600f, 1.5f},
                /*7*/   {10f, 250f, 68f, 14f,    2700f, 1.4f},
                /*8*/   {11f, 260f, 74f, 13.5f,  2800f, 1.35f},
                /*9*/   {12f, 270f, 80f, 13f,   2900f,  1.3f},
        };

        this.menTables[1]  = new Table();
        this.menTables[1].minAge = 30;
        this.menTables[1].maxAge = 34;
        this.menTables[1].pointTable =  new Float[][]{
                /*0*/   {2f,  165f, 30f, 18.5f,  1900f, 5f},
                /*1*/   {3f,  175f, 34f, 17.5f,  2050f, 3f},
                /*2*/   {4f,  185f, 38f, 17f,    2150f, 2.4f},
                /*3*/   {5f,  196f, 43f, 16.5f,  2250f, 2.27f},
                /*4*/   {6f,  207f, 48f, 16f,    2350f, 2.15f},
                /*5*/   {7f,  218f, 53f, 15.5f,  2450f, 2.05f},
                /*6*/   {8f,  228f, 58f, 15f,    2550f, 1.55f},
                /*7*/   {9f,  240f, 64f, 14.4f,  2650f, 1.46f},
                /*8*/   {10f, 250f, 70f, 13.9f,  2750f, 1.37f},
                /*9*/   {11f, 260f, 75f, 13.4f,  2850f, 1.32f},
        };

        this.menTables[2]  = new Table();
        this.menTables[2].minAge = 35;
        this.menTables[2].maxAge = 39;
        this.menTables[2].pointTable =  new Float[][]{
                /*0*/   {2f,  160f, 28f, 19f,   1850f, 5f},
                /*1*/   {3f,  170f, 32f, 18f,   2000f, 3.15f},
                /*2*/   {4f,  180f, 36f, 17.5f, 2100f, 2.5f},
                /*3*/   {5f,  190f, 41f, 17f,   2200f, 2.35f},
                /*4*/   {6f,  200f, 45f, 16.5f, 2300f, 2.2f},
                /*5*/   {7f,  210f, 50f, 16f,   2400f, 2.1f},
                /*6*/   {8f,  220f, 55f, 15.5f, 2500f, 2f},
                /*7*/   {9f,  230f, 60f, 14.9f, 2600f, 1.5f},
                /*8*/   {10f, 240f, 65f, 14.3f, 2700f, 1.4f},
                /*9*/   {11f, 250f, 70f, 13.8f, 2800f, 1.35f},
        };
        this.menTables[3]  = new Table();
        this.menTables[3].minAge = 40;
        this.menTables[3].maxAge = 44;
        this.menTables[3].pointTable =  new Float[][]{
                /*0*/   {1f,  155f, 25f, 20f,   1800f, 5f},
                /*1*/   {2f,  165f, 30f, 19f,   1900f, 3.35f},
                /*2*/   {3f,  175f, 34f, 18.4f, 2000f, 3.1f},
                /*3*/   {4f,  185f, 38f, 17.8f, 2100f, 2.5f},
                /*4*/   {5f,  195f, 42f, 17.2f, 2200f, 2.35f},
                /*5*/   {6f,  205f, 46f, 16.6f, 2300f, 2.2f},
                /*6*/   {7f,  215f, 50f, 16f,   2400f, 2.07f},
                /*7*/   {8f,  225f, 55f, 15.4f, 2520f, 1.55f},
                /*8*/   {9f,  235f, 60f, 14.8f, 2630f, 1.45f},
                /*9*/   {10f, 245f, 65f, 14.3f, 2750f, 1.4f},
        };
        this.menTables[4]  = new Table();
        this.menTables[4].minAge = 45;
        this.menTables[4].maxAge = Integer.MAX_VALUE;
        this.menTables[4].pointTable =  new Float[][]{
                /*0*/   {1f,  150f, 23f, 21f,   1700f, 5f},
                /*1*/   {2f,  160f, 27f, 20f,   1800f, 4f},
                /*2*/   {3f,  170f, 31f, 19.3f, 1900f, 3.35f},
                /*3*/   {4f,  180f, 35f, 18.6f, 2000f, 3.1f},
                /*4*/   {5f,  190f, 39f, 17.9f, 2100f, 2.5f},
                /*5*/   {6f,  200f, 43f, 17.2f, 2200f, 2.3f},
                /*6*/   {7f,  210f, 47f, 16.5f, 2300f, 2.15f},
                /*7*/   {8f,  220f, 51f, 15.9f, 2420f, 2f},
                /*8*/   {9f,  230f, 55f, 15.3f, 2550f, 1.5f},
                /*9*/   {10f, 240f, 60f, 14.8f, 2700f, 1.45f},
        };

    }

    private void createWomenTables(){
        this.womenTables = new Table[5];


        this.womenTables[0]  = new Table();
        this.womenTables[0].minAge = 0;
        this.womenTables[0].maxAge = 29;
        this.womenTables[0].pointTable =  new Float[][]{
                /*0*/   {10f,  0f, 32f, 9.6f,   1600f, 250f},
                /*1*/   {12f,  0f, 37f, 9.3f,   1700f, 270f},
                /*2*/   {15f,  0f, 42f, 9f,     1780f, 320f},
                /*3*/   {17f,  0f, 46f, 8.7f,   1880f, 370f},
                /*4*/   {20f,  0f, 50f, 8.5f,   1960f, 420f},
                /*5*/   {22f,  0f, 54f, 8.2f,   2050f, 480f},
                /*6*/   {26f,  0f, 58f, 7.9f,   2150f, 510f},
                /*7*/   {33f,  0f, 62f, 7.5f,   2220f, 540f},
                /*8*/   {37f,  0f, 66f, 7.2f,   2350f, 570f},
                /*9*/   {40f,  0f, 70f, 7f,     2400f, 600f},
        };

        this.womenTables[1]  = new Table();
        this.womenTables[1].minAge = 30;
        this.womenTables[1].maxAge = 34;
        this.womenTables[1].pointTable =  new Float[][]{
                /*0*/   {8f,   0f, 29f, 10f,    1580f, 220f},
                /*1*/   {10f,  0f, 32f, 9.8f,   1650f, 240f},
                /*2*/   {13f,  0f, 35f, 9.5f,   1750f, 290f},
                /*3*/   {15f,  0f, 39f, 9.2f,   1820f, 340f},
                /*4*/   {17f,  0f, 44f, 8.9f,   1920f, 390f},
                /*5*/   {21f,  0f, 49f, 8.6f,   2000f, 440f},
                /*6*/   {24f,  0f, 54f, 8.3f,   2120f, 490f},
                /*7*/   {27f,  0f, 58f, 7.9f,   2200f, 520f},
                /*8*/   {32f,  0f, 62f, 7.6f,   2280f, 550f},
                /*9*/   {36f,  0f, 66f, 7.3f,   2350f, 580f},
        };
        this.womenTables[2]  = new Table();
        this.womenTables[2].minAge = 35;
        this.womenTables[2].maxAge = 39;
        this.womenTables[2].pointTable =  new Float[][]{
                /*0*/   {6f,   0f, 26f, 10.6f,  1550f, 210f},
                /*1*/   {7f,   0f, 29f, 10.3f,  1620f, 230f},
                /*2*/   {9f,   0f, 32f, 10f,    1680f, 260f},
                /*3*/   {11f,  0f, 36f, 9.7f,   1750f, 310f},
                /*4*/   {14f,  0f, 40f, 9.3f,   1860f, 350f},
                /*5*/   {17f,  0f, 44f, 9f,     1950f, 390f},
                /*6*/   {21f,  0f, 48f, 8.7f,   2050f, 440f},
                /*7*/   {25f,  0f, 52f, 8.3f,   2150f, 480f},
                /*8*/   {28f,  0f, 56f, 8f,     2220f, 530f},
                /*9*/   {32f,  0f, 60f, 7.7f,   2300f, 560f},
        };
        this.womenTables[3]  = new Table();
        this.womenTables[3].minAge = 40;
        this.womenTables[3].maxAge = 44;
        this.womenTables[3].pointTable =  new Float[][]{
                /*0*/   {5f,  0f, 19f, 11.9f,   1350f, 170f},
                /*1*/   {6f,  0f, 23f, 11.4f,   1400f, 180f},
                /*2*/   {8f,  0f, 26f, 11f,     1460f, 200f},
                /*3*/   {9f,  0f, 30f, 10.6f,   1520f, 230f},
                /*4*/   {11f, 0f, 34f, 10.2f,   1550f, 260f},
                /*5*/   {13f, 0f, 38f, 9.8f,    1620f, 290f},
                /*6*/   {17f, 0f, 43f, 9.4f,    1750f, 330f},
                /*7*/   {21f, 0f, 47f, 8.9f,    1900f, 380f},
                /*8*/   {24f, 0f, 51f, 8.5f,    2020f, 430f},
                /*9*/   {28f, 0f, 56f, 8.2f,    2100f, 500f},
        };
        this.womenTables[4]  = new Table();
        this.womenTables[4].minAge = 45;
        this.womenTables[4].maxAge = Integer.MAX_VALUE;
        this.womenTables[4].pointTable =  new Float[][]{
                /*0*/   {4f,  0f, 18f, 12.2f,   1200f, 130f},
                /*1*/   {5f,  0f, 19f, 11.8f,   1250f, 140f},
                /*2*/   {6f,  0f, 20f, 11.4f,   1320f, 150f},
                /*3*/   {7f,  0f, 23f, 11f,     1400f, 170f},
                /*4*/   {8f,  0f, 28f, 10.6f,   1480f, 220f},
                /*5*/   {11f, 0f, 32f, 10.2f,   1550f, 270f},
                /*6*/   {14f, 0f, 36f, 9.7f,    1650f, 310f},
                /*7*/   {17f, 0f, 40f, 9.3f,    1800f, 350f},
                /*8*/   {21f, 0f, 46f, 8.9f,    1900f, 410f},
                /*9*/   {25f, 0f, 50f, 8.5f,    2000f, 460f},
        };

    /* ******************************************** END OF TABLES ************************************************* */
    }

    Points calculateResult(PhysicalResult result){
        Table table = chooseTable(result);
        Points points = new Points();
        points.summary = 0;
        points.disciplines[Indexes.PULL_UP] = calculatePoints(table,Indexes.PULL_UP,Float.valueOf(result.disciplines.get("pull_up")));
        points.summary+= points.disciplines[Indexes.PULL_UP];
        points.disciplines[Indexes.JUMP] = calculatePoints(table,Indexes.JUMP,Float.valueOf(result.disciplines.get("jump")));
        points.summary+= points.disciplines[Indexes.JUMP];
        points.disciplines[Indexes.CRUNCH] = calculatePoints(table,Indexes.CRUNCH,Float.valueOf(result.disciplines.get("crunch")));
        points.summary+= points.disciplines[Indexes.CRUNCH];
        points.disciplines[Indexes.SPRINT] = calculatePoints(table,Indexes.SPRINT,Float.valueOf(result.disciplines.get("sprint")));
        points.summary+= points.disciplines[Indexes.SPRINT];
        points.disciplines[Indexes.RUN_12min] = calculatePoints(table,Indexes.RUN_12min,Float.valueOf(result.disciplines.get("12min")));
        points.summary+= points.disciplines[Indexes.RUN_12min];
        points.disciplines[Indexes.SWIMMING] = calculatePoints(table,Indexes.SWIMMING,Float.valueOf(result.disciplines.get("swimming")));
        points.summary+= points.disciplines[Indexes.SWIMMING];

        points.passed = checkResults(result.person,points);
        return points;
    }

    private  boolean checkResults(Person person, Points points){

        Integer score = points.summary;
        if (person.getGender().equals("m")){
            for( Integer i = 0; i < Indexes.NUM_OF_DISCIPLINES; ++i){
                if( i != Indexes.SWIMMING && points.disciplines[i] ==0)
                    return false;
            }
            if (person.getGroup().equals("I")){
                if (score >= menLimitI){
                    return true;
                }
            }else if (person.getGroup().equals("II")){
                if(score >= menLimitII){
                    return true;
                }
            }
        } else if (person.getGender().equals("f")){
            if (points.disciplines[Indexes.RUN_12min] == 0 && points.disciplines[Indexes.SWIMMING] == 0 ) return false;
            for( Integer i = 0; i < Indexes.NUM_OF_DISCIPLINES; ++i){
                if( i != Indexes.SWIMMING && i!= Indexes.RUN_12min && i!= Indexes.JUMP && points.disciplines[i] ==0)
                    return false;
            }
            if (person.getGroup().equals("I")){
                if (score >= womenLimitI){
                    return true;
                }
            }else if (person.getGroup().equals("II")){
                if(score >= womenLimitII){
                    return true;
                }
            }
        }
        return false;
    }

    private Integer calculatePoints( Table table, Integer discipline, Float res){
        /* check if bigger number == more points */
        if(table.pointTable[0][discipline] < table.pointTable[1][discipline]){
            for(Integer i = 0; i < table.getPointsMax(); ++i){
                if (table.pointTable[i][discipline] > res){
                    return i;
                }
            }
        }else if (table.pointTable[0][discipline] >= table.pointTable[1][discipline]){ /* equal because women have 0 jump */
            /* if person gets 0 do not get 10 points */
            if (res == 0){ return 0; }
            for(Integer i = 0; i < table.getPointsMax(); ++i){
                if (table.pointTable[i][discipline] < res){
                    return i;
                }
            }
        }

        return table.getPointsMax(); /* in case the person reaches the best possible result */
    }

    private Table chooseTable(PhysicalResult result){
        Table [] tables = null;
        Integer age =  Calendar.getInstance().get(Calendar.YEAR) - result.person.getDateOfBirth();

        if (result.person.getGender().equals("m")){
            tables = this.menTables;
        }else if (result.person.getGender().equals("f")){
            tables = this.womenTables;
        }

        if ( tables != null ) {
            for (Table table : tables)
                if (table.maxAge >= age)
                    return table;
        }
        return null;
    }

}
