package com.example.tomas.common;
import android.os.Environment;
import java.io.File;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Utilities {

    public static String getSessionDateString(Session session) {

        String DATE_FORMAT = "dd-MM-yyyy";
        SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_FORMAT);
        String strDate = sdfDate.format(session.getTimeStamp());
        return strDate;
    }

    public static String[] splitName(String name) {
        int i = name.lastIndexOf(" ");
        String[] a;
        if (i == -1) a = new String[]{name, ""};
        else a = new String[]{name.substring(0, i), name.substring(i + 1)};
        return a;
    }

    public static File makeDateDir_strelby(String format, Timestamp timestamp) {
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/previerky/strelby/" + Utilities.timestampToString(timestamp, format) + "/");
        dir.mkdirs();
        return dir;
    }


    public static File makeDateDir_telesna(String format, Timestamp timestamp) {
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/previerky/telesna/" + Utilities.timestampToString(timestamp, format) + "/");
        dir.mkdirs();
        return dir;
    }

    /**
     * Function formats decimal number using DecimalFormatSymbols and DecimalFormat
     * it changes . to , and rounds to 2 decimal places
     *
     * @param val number to be converted to double
     * @return a formated String
     */
    public static String formatDec(double val) {
        DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
        unusualSymbols.setDecimalSeparator(',');

        String formating = "###.##";
        DecimalFormat weirdFormatter = new DecimalFormat(formating, unusualSymbols);

        return weirdFormatter.format(val);
    }

    /* Unfortunately worse solution */
    public static String formatDec(String val) {
        double d = Double.valueOf(val);
        return formatDec(d);
    }

    /**
     * Formatuj casovy retazec
     *
     * @param format
     * @return
     */
    public static String timestampToString(Timestamp tm, String format) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(format, Locale.getDefault());
        String strDate = sdfDate.format(tm);
        return strDate;
    }

    /**
     * Converts long to String HH:mm:ss
     *
     * @param time long value to convert
     * @return formated string
     */
    public static String timeToHoursMinutesSeconds(long time) {

        long hours = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

}
