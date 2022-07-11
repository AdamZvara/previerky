package com.example.tomas.common;

public enum RankENUM {

    EMPTY(""),
    stržm("stržm"),
    nstržm("nstržm"),
    ppráp("ppráp"),
    práp("práp"),
    npráp("npráp"),
    ppor("ppor"),
    por("por"),
    npor("npor"),
    kpt("kpt"),
    mjr("mjr"),
    pplk("pplk"),
    plk("plk");
    private String name;

    RankENUM(String name){
        this.name = name;
    }

    @Override public String toString(){
        return name;
    }

    public static boolean contains(String str) {
        for (RankENUM c : RankENUM.values()) {
            if (c.name().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static RankENUM validate(String str) {
        return RankENUM.contains(str) ?
                RankENUM.valueOf(str) : RankENUM.EMPTY;
    }
}
