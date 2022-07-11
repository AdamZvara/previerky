package com.example.tomas.strelby;

import android.os.Environment;

import com.example.tomas.common.Session;
import com.example.tomas.common.Utilities;
import com.example.tomas.strelby.Exceptions.NoResultException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Class takes care for creating XLS list of contestants.
 */
class ShootsFileManager {

    static private String sourceFileFirst = "/dab/predlohy/strelby-I.skupina.xls"; /// first group source file
    static private String sourceFileSecond = "/dab/predlohy/strelby-II.skupina.xls"; /// second group source file
    static private String sourceFileMunition = "/dab/predlohy/municia_strelby.xls"; /// munition
    static private String sourceFileResults = "/dab/predlohy/strelby_vysledky.xls"; /// munition
    static int numOfBullets; /// number of bullets in munition ... should be changed

    /**
     * -----------------------------------------------------------------------------------------------
     * Otvori excelovsky subor
     *
     * @param path of the XLS file.
     * @return HSSFWorkbook from Apache-poi.
     * @throws IOException - file system exception
     */
    static private HSSFWorkbook openExcelFileForReading(String path) throws IOException {
        String root = Environment.getExternalStorageDirectory().toString();
        File file = new File(root + path);

        FileInputStream myInput = new FileInputStream(file);
        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(myInput);
        return new HSSFWorkbook(poifsFileSystem);
    }

    /**
     * -----------------------------------------------------------------------------------------------
     * Vyplni hlavicku v zosite "ucast_strelby_I" a hlavicku "municia_strelby"
     * - add date
     *
     * @param workbook excel wb
     */
    static private void generateHeaderParticipation_I(HSSFWorkbook workbook, Timestamp timestamp) {
        HSSFSheet sheet = workbook.getSheetAt(0);
        //Update the value of cell
        HSSFCell cell = sheet.getRow(1).getCell(13);
        cell.setCellValue(cell.getStringCellValue() + " " + (Utilities.timestampToString(timestamp, "dd.MM.yyyy")));
    }

    static private void generateHeaderParticipation_II(HSSFWorkbook workbook, Timestamp timestamp) {
        HSSFSheet sheet = workbook.getSheetAt(0);
        //Update the value of cell
        HSSFRow row = sheet.getRow(2);
        HSSFCell cell;
        if ((row.getCell(4)) == null) sheet.getRow(2).createCell(4);
        cell = sheet.getRow(2).getCell(4);
        cell.setCellValue(Utilities.timestampToString(timestamp, "dd.MM.yyyy"));
    }

    static private void generateHeaderMunition(HSSFWorkbook workbook, Timestamp timestamp) {
        HSSFSheet sheet = workbook.getSheetAt(0);
        //Update the value of cell
        HSSFRow row = sheet.getRow(7);
        HSSFCell cell;
        if ((row.getCell(6)) == null) sheet.getRow(7).createCell(6);
        cell = sheet.getRow(7).getCell(6);
        cell.setCellValue(Utilities.timestampToString(timestamp, "dd.MM.yyyy"));
    }

    /**
     * -----------------------------------------------------------------------------------------------
     * Ulozi excelovske subory do prislusneho adresara
     *
     * @param workbook excel wb
     * @param fileName name of the file
     * @param path     where the file should be saved
     * @throws IOException file system IOException.
     */
    static private void saveExcelFile(HSSFWorkbook workbook, String path, String fileName, Timestamp timestamp) throws IOException {
        File file = new File(Utilities.makeDateDir_strelby("dd-MM-yyyy", timestamp), fileName);
        FileOutputStream outFS = new FileOutputStream(file, false);
        workbook.write(outFS);
        outFS.close();
    }


    /******************************TLACIVO UCAST I alebo II********************************************
     /*************************************************************************************************
     * Do predlohy "ucast_strelby" zapise ucastnikov strelieb(podpisuju na strelnici)
     * @param results people to be printed
     * @throws NoResultException when result is zero size
     * @throws IOException if result is 0 size.
     */
    static void makeExcelParticipation(List<ShootResult> results, Session session) throws NoResultException, IOException {

        if (results.size() == 0) {
            throw new NoResultException();
        }

        HSSFWorkbook wb = null;
        Timestamp timestamp = session.getTimeStamp();

        if (results.get(0).person.getGroup().equals("I")) {
            wb = openExcelFileForReading(sourceFileFirst);
            generateHeaderParticipation_I(wb, timestamp);
            addPeople_I(wb, results);
        } else if (results.get(0).person.getGroup().equals("II")) {
            wb = openExcelFileForReading(sourceFileSecond);
            generateHeaderParticipation_II(wb, timestamp);
            addPeople_II(wb, results);
        }


        String name = "";
        if (session.getParentSessionId() != null) {
            name += "opravne_";
            name += session.getChildOrderNumber() + "_";
        }
        name += "ucast_strelby_" + (results.get(0).person.getGroup().equals("I") ? "I" : "II");
        saveExcelFile(wb, "/previerky/strelby/", name + ".xls", timestamp);
    }

    /**
     * -----------------------------------------------------------------------------------------------
     * Prida ludi z I. skupiny do zosita "ucast_strelby_I"
     *
     * @param workbook excel wb
     * @param results  contestants that should be added
     */
    static private void addPeople_I(HSSFWorkbook workbook, List<ShootResult> results) {
        HSSFSheet sheet = workbook.getSheetAt(0);
        int rowNum = 4;
        for (ShootResult res : results) {

            HSSFRow row = sheet.getRow(rowNum++);
            if (row == null) {
                row = sheet.createRow(rowNum - 1);
                row.createCell(1);
                row.createCell(2);
            }

            //skip every 16. person
            //if ( (i++ % 16) == 0 ) continue;
            /// CREATE NEW CELLS
            row.getCell(1).setCellValue(res.person.name + ", " + res.person.id);
            row.getCell(2).setCellValue(res.person.getCentrum().toString());

        }
    }

    /**
     * -----------------------------------------------------------------------------------------------
     * Prida ludi z I. skupiny do zosita "ucast_strelby_II"
     *
     * @param workbook excel wb
     * @param results  contestants that should be added
     */
    static private void addPeople_II(HSSFWorkbook workbook, List<ShootResult> results) {
        HSSFSheet sheet = workbook.getSheetAt(0);
        //Update the value of cell
        int rowNum = 5;
        for (ShootResult res : results) {
            HSSFRow row = sheet.getRow(rowNum++);

            if (row == null) {
                row = sheet.createRow(rowNum - 1);
                row.createCell(2);
                row.createCell(3);
                row.createCell(4);
                row.createCell(5);
                row.createCell(6);
                row.createCell(7);
            }
            String[] nameSplit = Utilities.splitName(res.person.name);
            if (res.person.getRank().toString().isEmpty()) {
                row.getCell(1).setCellValue("");
            } else {
                row.getCell(1).setCellValue(res.person.getRank().toString() + ". ");
            }
            row.getCell(2).setCellValue(nameSplit[0]);
            row.getCell(3).setCellValue(nameSplit[1]);
            row.getCell(4).setCellValue(res.person.id);
            row.getCell(5).setCellValue(res.person.getCentrum().toString());
            if (res.getPoints() == 0) {
            } else {
                row.getCell(6).setCellValue(res.getStatus() ? "Vyhovel" : "Nevyhovel");
                row.getCell(7).setCellValue(res.getPoints());
            }

        }
    }


    /******************************TLACIVO MUNICIA*****************************************************
     /*************************************************************************************************
     * Do predlohy "municia_strelby" zapise ucastnikov strelieb a pocet nabojov(podpisuju na strelnici)
     * @param results people
     * @throws NoResultException thrown when size is zero
     * @throws IOException when writing/reading from file fails
     */
    static void makeExcelMunition(List<ShootResult> results, Session session) throws NoResultException, IOException {

        if (results.size() == 0) {
            throw new NoResultException();
        }

        HSSFWorkbook wb = openExcelFileForReading(sourceFileMunition);
        generateHeaderMunition(wb, session.getTimeStamp());

        addPeopleMunition(wb, results);

        String name = "";
        if (session.getParentSessionId() != null) {
            name += "opravne_";
            name += session.getChildOrderNumber() + "_";
        }
        name += "municia_strelby_" + (results.get(0).person.getGroup().equals("I") ? "I" : "II");
        saveExcelFile(wb, "/previerky/strelby/", name + ".xls", session.getTimeStamp());
    }

    /**
     * -----------------------------------------------------------------------------------------------
     * Prida ludi z I. skupiny alebo II. skupiny do zosita "municia_strelby"
     *
     * @param workbook excel wb
     * @param results  contestants that should be added
     */

    static private void addPeopleMunition(HSSFWorkbook workbook, List<ShootResult> results) {
        HSSFSheet sheet = workbook.getSheetAt(0);
        //Update the value of cell
        int rowNum = 17;
        for (ShootResult res : results) {
            HSSFRow row = sheet.getRow(rowNum++);

            if (row == null) {
                row = sheet.createRow(rowNum - 1);
                row.createCell(1);
                row.createCell(2);
                row.createCell(3);
                row.createCell(4);
                row.createCell(5);
            }

            if (res.person.getRank().toString().isEmpty()) {
                row.getCell(1).setCellValue(res.person.name);
            } else {
                row.getCell(1).setCellValue(res.person.getRank().toString() + ". " + res.person.name);
            }

            row.getCell(2).setCellValue(res.person.getCentrum().toString());
            row.getCell(3).setCellValue(res.person.id);

            if (res.person.getGun().equals("L")) row.getCell(4).setCellValue(numOfBullets);
            else if (res.person.getGun().equals("M")) row.getCell(5).setCellValue(numOfBullets);
        }
    }


    /******************************TLACIVO VYSLEDKY STRELIEB******************************************
     /*************************************************************************************************
     * Do predlohy "strelby_vysledky" zapise vysledky
     * @param results people
     * @throws NoResultException thrown when size is zero
     * @throws IOException when writing/reading from file fails
     */
    static void makeExcel_ShootsResults(List<ShootResult> results, Session session) throws
            NoResultException, IOException {

        if (results.size() == 0) {
            throw new NoResultException();
        }

        HSSFWorkbook wb;
        wb = openExcelFileForReading(sourceFileResults);
        addShootsResults(wb, results, session.getTimeStamp());


        String name = "";
        if (session.getParentSessionId() != null) {
            name += "opravne_";
            name += session.getChildOrderNumber() + "_";
        }
        name += "strelby_vysledky_" + (results.get(0).person.getGroup().equals("I") ? "I" : "II");
        saveExcelFile(wb, "/previerky/strelby/", name + ".xls", session.getTimeStamp());
    }

    /**
     * -----------------------------------------------------------------------------------------------
     * /** Vyplni vysledky osoby na prislusnom riadku v zosite
     *
     * @param workbook of person.
     * @param results  Shootresult object
     */
    static private void addShootsResults(HSSFWorkbook workbook, List<ShootResult> results, Timestamp timestamp) {
        HSSFSheet sheet = workbook.getSheetAt(0);

        //Zapise datum previerok do hlavicky suboru "telesna_vysledky"
        HSSFCell cell_date = sheet.getRow(0).getCell(0);
        cell_date.setCellValue(cell_date.getStringCellValue() + " " + (Utilities.timestampToString(timestamp, "dd.MM.yyyy")));

        int rowNum = 2;
        for (ShootResult res : results) {
            HSSFRow row = sheet.getRow(rowNum++);

            if (row == null) {
                row = sheet.createRow(rowNum - 1);

                row.createCell(0).setCellValue(res.person.id);
                if (res.person.getRank().toString().isEmpty()) {
                    row.createCell(1).setCellValue("");
                } else {
                    row.createCell(1).setCellValue(res.person.getRank().toString() + ". ");
                }
                row.createCell(2).setCellValue(res.person.name);
                row.createCell(3).setCellValue(res.person.getCentrum().toString());
                row.createCell(4).setCellValue(res.person.getDateOfBirth());
                row.createCell(5).setCellValue(res.person.getGroup());
                row.createCell(6).setCellValue(Utilities.formatDec(res.getTime()));
                row.createCell(7).setCellValue(res.getPoints());
                row.createCell(8).setCellValue(Utilities.formatDec(res.getRatio()));
                row.createCell(9).setCellValue(res.getStatus() ? "Vyhovel" : "Nevyhovel");
            }
            // give them border
            HSSFCellStyle style = row.getSheet().getWorkbook().createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setAlignment(CellStyle.ALIGN_LEFT);

            for (int i = 0; i < 10; ++i) {
                row.getCell(i).setCellStyle(style);
            }

        }
    }

    /******************************TEXTOVY SUBOR*******************************************************
     /*************************************************************************************************
     * Vygeneruje textovy subor "strelby" pre nahadzovanie do centralnej databazy
     * @param results people to be printed
     * @throws NoResultException when result is zero size
     * @throws IOException if result is 0 size.
     */
    static void createTXTList(List<ShootResult> results, Session session) throws NoResultException, IOException {

        try {
            String name = "";
            if (session.getParentSessionId() != null) {
                name += "opravne_";
                name += session.getChildOrderNumber() + "_";
            }
            name += "strelby_" + (results.get(0).person.getGroup().equals("I") ? "I" : "II" + ".txt");
            File gpxfile = new File(Utilities.makeDateDir_strelby("dd-MM-yyyy", session.getTimeStamp()), name);
            FileWriter writer = new FileWriter(gpxfile, false);
            for (ShootResult result : results) {
                String TXT_SEPARATOR = "-";
                String text = result.person.id + TXT_SEPARATOR + result.person.getGroup() + TXT_SEPARATOR;
                text += result.getPoints() + TXT_SEPARATOR + result.getTime() + TXT_SEPARATOR + result.getNullTarget() + TXT_SEPARATOR;
                text += result.getStatus() ? "V" + TXT_SEPARATOR : "N" + TXT_SEPARATOR;
                text += result.person.getAttempt();
                writer.append(text + System.getProperty("line.separator"));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
