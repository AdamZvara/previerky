package com.example.tomas.common;

/*
    EMPTY(""),
    ODI("ODI"),
    OKAP("OKaP"),
    OKP("OKP"),
    OOPZ_Bidovce("OOPZ-Bidovce"),
    OOPZ_Bohdanovce("OOPZ-Bohdanovce"),
    OOPZ_Cana("OOPZ-Čaňa"),
    OOPZ_Jasov("OOPZ-Jasov"),
    OOPZ_Kysak("OOPZ-Kysak"),
    OOPZ_Moldava("OOPZ-Moldava n/B"),
    OOPZ_Turna("OOPZ-Turňa n/B"),
    OOPZ_Ida("OOPZ-Veľká Ida"),
    OPP("OPP"),
    VO("VO"),
    OSTATNE("OSTATNÉ"),;
  */

/*
    EMPTY(""),
    CPKE("CPKE"),
    ODI("ODI"),
    OKP("OKP"),
    OOPZ_DH("OOPZ-DG. HRDINOV"),
    OOPZ_JAZ("OOPZ-JAZERO"),
    OOPZ_JUH("OOPZ-JUH"),
    OOPZ_KVP("OOPZ-KVP"),
    OOPZ_SAC("OOPZ-ŠACA"),
    OOPZ_SEV("OOPZ-SEVER"),
    OOPZ_SM("OOPZ-STARÉ MESTO"),
    OOPZ_TAH("OOPZ-ŤAHANOVCE"),
    OOPZ_ZAP("OOPZ-ZÁPAD"),
    OPP("OPP"),
    OSTATNE("OSTATNÉ"),;
  */

/*
    EMPTY(""),
    CPKE("CPKE"),
    HIPOLOGIA("HIPOLÓGIA"),
    KDI("KDI"),
    KEU("KEU"),
    KRPZKE("KRPZKE"),
    NAKA_NJFP("NAKA-NJFP"),
    NAKA_NPDJ("NAKA-NPDJ"),
    NAKA_NPKJ("NAKA-NPKJ"),
    NAKA_NPTJ("NAKA-NPTJ"),
    NAKA_NPZJ("NAKA-NPZJ"),
    NEMOCNICA("NEMOCNICA"),
    OCP("OCP"),
    ODSV("ODSV"),
    OFO("OFO"),
    OHK_LETISKO("OHK-LETISKO"),
    OK("OK"),
    OKAP("OKaP"),
    OKP("OKP"),
    OOO("OOO"),
    OPO("OPO"),
    OPP("OPP"),
    OSBS("OSBS"),
    OSE("OŠE"),
    OSK("OSK"),
    OZP("OŽP"),
    OZPC_ITP("OZPČ-ITP"),
    OZPC_SOV("OZPČ-SOV"),
    PMJ("PMJ"),
    UIS("ÚIS"),
    SKP("SKP"),
    UOUCADM("UoUČaDM"),
    VO("VO"),;
  */

public enum CentrumENUM {

    EMPTY(""),
    CPKE("CPKE"),
    KDI("KDI"),
    KEU("KEU"),
    KRPZKE("KRPZKE"),
    NAKA("NAKA"),
    NEMOCNICA("NEMOCNICA"),
    OCP("OCP"),
    ODSV("ODSV"),
    OFO("OFO"),
    OHK_LETISKO("OHK-LETISKO"),
    OJP("OJP"),
    OK("OK"),
    OKAP("OKaP"),
    OKP("OKP"),
    OOO("OOO"),
    OPO("OPO"),
    OPP("OPP"),
    OSBS("OSBS"),
    OSE("OŠE"),
    OSK("OSK"),
    OZP("OŽP"),
    OZPCV("OZPČV"),
    PMJ("PMJ"),
    SKP("SKP"),
    UIS("ÚIS"),
    UOUCADM("UoUČaDM"),
    VO("VO"),;


    private String name;

    CentrumENUM(String name){
        this.name = name;
    }

    @Override public String toString(){
        return name;
    }

    public static boolean contains(String str) {
        for (CentrumENUM c : CentrumENUM.values()) {
            if (c.name().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static CentrumENUM validate(String str) {
        return CentrumENUM.contains(str) ?
                CentrumENUM.valueOf(str) : CentrumENUM.EMPTY;
    }
}
