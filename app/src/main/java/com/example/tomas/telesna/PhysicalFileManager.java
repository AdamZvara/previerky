package com.example.tomas.telesna;

import android.os.Environment;

import com.example.tomas.common.Session;
import com.example.tomas.strelby.Exceptions.NoResultException;
import com.example.tomas.common.Utilities;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Vytvara subor Txt_telesna a do predlohy telesna_vysledky prida ludi a vykony.
 */
class PhysicalFileManager {


    static private String sourceFilePhysical = "/dab/predlohy/telesna_vysledky.xls"; /// telesna
    static TableWrapper tableWrapper = new TableWrapper();


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
     * Ulozi excelovske subory do prislusneho adresara
     *
     * @param workbook excel wb
     * @param fileName name of the file
     * @param path     where the file should be saved
     * @throws IOException file system IOException.
     */
    static private void saveExcelFile(HSSFWorkbook workbook, String path, String fileName, Timestamp timestamp) throws IOException {
        File file = new File(Utilities.makeDateDir_telesna("dd-MM-yyyy", timestamp), fileName);
        FileOutputStream outFS = new FileOutputStream(file, false);
        workbook.write(outFS);
        outFS.close();
    }

    static private void generateHeaderPeople(HSSFWorkbook workbook, Timestamp timestamp, long aStadiumTime) {
        //Zapise datum previerok do hlavicky suboru "telesna_vysledky"
        HSSFSheet sheet = workbook.getSheetAt(0);
        //Update the value of cell
        HSSFRow row = sheet.getRow(0);
        HSSFCell cell;

        if ((row.getCell(0)) == null) row.createCell(0);
        cell = row.getCell(0);
        cell.setCellValue(cell.getStringCellValue() + " " + (Utilities.timestampToString(timestamp, "dd.MM.yyyy")));

        if (aStadiumTime == 0) {
            return;
        }
        if ((row.getCell(5)) == null) row.createCell(5);
        cell = row.getCell(5);
        cell.setCellValue(cell.getStringCellValue() + " " + Utilities.timeToHoursMinutesSeconds(aStadiumTime));

    }

    /**
     * -----------------------------------------------------------------------------------------------
     * Do predlohy "telesna_vysledky" zapise vysledky
     *
     * @param results people
     * @throws NoResultException thrown when size is zero
     * @throws IOException       when writing/reading from file fails
     */
    static void makeExcel_PhysicalPeople(List<PhysicalResult> results, Timestamp timestamp) throws NoResultException, IOException {

        if (results.size() == 0) {
            throw new NoResultException();
        }

        HSSFWorkbook wb = null;
        wb = openExcelFileForReading(sourceFilePhysical);
        generateHeaderPeople(wb, timestamp, 0);
        addPeople_physical(wb, results, timestamp);

        String name = "telesna_vysledky";
        saveExcelFile(wb, "/previerky/telesna/", name + ".xls", timestamp);
    }

    /**
     * -----------------------------------------------------------------------------------------------
     * Prida ludi do zosita "telesna_vysledky" pre tlacenie zoznamov previerok z telesnej
     *
     * @param workbook excel wb
     * @param results  contestants that should be added
     */
    private static void addPeople_physical(HSSFWorkbook workbook, List<PhysicalResult> results, Timestamp timestamp) {
        HSSFSheet sheet = workbook.getSheetAt(0);

        //Zapise osobne udaje do suboru "telesna_vysledky"
        int rowNum = 2;
        int j = 0;
        for (PhysicalResult res : results) {

            j = j + 1;


            HSSFRow row = sheet.getRow(rowNum++);

            if (row == null) {
                row = sheet.createRow(rowNum - 1);
                row.createCell(0);
                row.createCell(1);
                row.createCell(2);
                row.createCell(3);
                row.createCell(4);
                row.createCell(5);
                row.createCell(6);
                row.createCell(6);
                row.createCell(7);
                row.createCell(8);
                row.createCell(9);
                row.createCell(10);
                row.createCell(11);
                row.createCell(12);
                row.createCell(13);
                row.createCell(14);

            }

            row.getCell(0).setCellValue(j);
            row.getCell(1).setCellValue(res.person.id);
            if (res.person.getRank().toString().isEmpty()) {
                row.getCell(2).setCellValue("");
            } else {
                row.getCell(2).setCellValue(res.person.getRank().toString() + ". ");
            }
            row.getCell(3).setCellValue(res.person.name);
            row.getCell(4).setCellValue(res.person.getCentrum().toString());
            row.getCell(5).setCellValue(res.person.getDateOfBirth());
            row.getCell(6).setCellValue(res.person.getGroup());

        }
    }


    /**
     * -----------------------------------------------------------------------------------------------
     * Prida ludi a ich vykony do zosita "telesna_vysledky" pri ukonceni previerok z telesnej
     *
     * @param workbook excel wb
     * @param results  contestants that should be added
     */
    private static void addPeopleAndResults_physical(HSSFWorkbook workbook, List<PhysicalResult> results, Timestamp timestamp) {
        HSSFSheet sheet = workbook.getSheetAt(0);

        //TODO createCell is wrong because when row exists it does not mean that cell exist - can end with Nullpointer
        //Zapise osobne udaje a vykony do suboru "telesna_vysledky"
        int rowNum = 2;
        int j = 0;
        for (PhysicalResult res : results) {
            Points points = tableWrapper.calculateResult(res);

            j = j + 1;
            String status;
            if (points.passed) status = "Vyhovel";
            else status = "Nevyhovel";


            HSSFRow row = sheet.getRow(rowNum++);

            if (row == null) {
                row = sheet.createRow(rowNum - 1);
                row.createCell(0);
                row.createCell(1);
                row.createCell(2);
                row.createCell(3);
                row.createCell(4);
                row.createCell(5);
                row.createCell(6);
                row.createCell(7);
                row.createCell(8);
                row.createCell(9);
                row.createCell(10);
                row.createCell(11);
                row.createCell(12);
                row.createCell(13);
                row.createCell(14);
            }

            (row.getCell(0) == null ? row.createCell(0) : row.getCell(0)).setCellValue(j);
            row.getCell(1).setCellValue(res.person.id);
            if (res.person.getRank().toString().isEmpty()) {
                row.getCell(2).setCellValue("");
            } else {
                row.getCell(2).setCellValue(res.person.getRank().toString() + ". ");
            }
            row.getCell(3).setCellValue(res.person.name);
            row.getCell(4).setCellValue(res.person.getCentrum().toString());
            row.getCell(5).setCellValue(res.person.getDateOfBirth());
            row.getCell(6).setCellValue(res.person.getGroup());
            row.getCell(7).setCellValue(res.disciplines.get("pull_up"));
            row.getCell(8).setCellValue(res.disciplines.get("jump"));
            row.getCell(9).setCellValue(res.disciplines.get("crunch"));
            row.getCell(10).setCellValue(res.disciplines.get("sprint"));
            row.getCell(11).setCellValue(res.disciplines.get("12min"));
            row.getCell(12).setCellValue(res.disciplines.get("swimming"));
            row.getCell(13).setCellValue(points.summary.toString());
            row.getCell(14).setCellValue(status);
        }
    }


    /**
     * -----------------------------------------------------------------------------------------------
     * Do predlohy "telesna_vysledky" zapise vysledky
     *
     * @param results people
     * @throws NoResultException thrown when size is zero
     * @throws IOException       when writing/reading from file fails
     */
    static void makeExcel_PhysicalPeopleAndResults(List<PhysicalResult> results, Timestamp timestamp,
                                                   long startStadiumTime, long endStadiumTime)
            throws NoResultException, IOException {

        if (results.size() == 0) {
            throw new NoResultException();
        }

        HSSFWorkbook wb = null;
        wb = openExcelFileForReading(sourceFilePhysical);

        //get stadium time
        long overallTime = startStadiumTime < endStadiumTime ? endStadiumTime - startStadiumTime : 0;
        generateHeaderPeople(wb, timestamp, overallTime);
        addPeopleAndResults_physical(wb, results, timestamp);

        String name = "telesna_vysledky";
        saveExcelFile(wb, "/previerky/telesna/", name + ".xls", timestamp);
    }

    /**
     * -----------------------------------------------------------------------------------------------
     * Vygeneruje textovy subor "telesna" pre nahadzovanie do centralnej databazy
     *
     * @param results people to be printed
     * @throws NoResultException when result is zero size
     * @throws IOException       if result is 0 size.
     */
    static void createTXT_telesna (List<PhysicalResult> results, Session session) throws NoResultException, IOException {

        try {
            File gpxfile = new File(Utilities.makeDateDir_telesna("dd-MM-yyyy", session.getTimeStamp()), "telesna" + ".txt");
            FileWriter writer = new FileWriter(gpxfile, false);
            for (PhysicalResult result : results) {
                Points points = tableWrapper.calculateResult(result);
                String TXT_SEPARATOR = "-";
                String text = result.person.id + TXT_SEPARATOR;
                text += result.disciplines.get("pull_up") + TXT_SEPARATOR;
                text += result.person.getGender().equals("f") ? "zena" + TXT_SEPARATOR : result.disciplines.get("jump") + TXT_SEPARATOR;
                text += result.disciplines.get("crunch") + TXT_SEPARATOR;
                text += Float.parseFloat(result.disciplines.get("sprint")) == 0 ? 99 + TXT_SEPARATOR : result.disciplines.get("sprint") + TXT_SEPARATOR;
                text += result.disciplines.get("12min") + TXT_SEPARATOR;
                text += (Float.parseFloat(result.disciplines.get("swimming")) == 0 && result.person.getGender().equals("m")) ? 999 + TXT_SEPARATOR : result.disciplines.get("swimming") + TXT_SEPARATOR;
                text += (points.passed) ? "V" + TXT_SEPARATOR : "N" + TXT_SEPARATOR;
                text += result.disciplines.get("pull_up").equals("0")  && result.disciplines.get("jump").equals("0") && result.disciplines.get("crunch").equals("0") ? "P" + TXT_SEPARATOR : "NP" + TXT_SEPARATOR;
                text += result.person.getGroup();
                writer.append(text + System.getProperty("line.separator"));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }


    /**
     * -----------------------------------------------------------------------------------------------
     * Vygeneruje textovy subor "telesna" pre nahadzovanie do centralnej databazy
     *
     * @param results people to be printed
     * @throws NoResultException when result is zero size
     * @throws IOException       if result is 0 size.
     */
    static void createTXT_telesna_vysledky (List<PhysicalResult> results, Session session) throws NoResultException, IOException {

        try {
            File gpxfile = new File(Utilities.makeDateDir_telesna("dd-MM-yyyy", session.getTimeStamp()),  Utilities.getSessionDateString(session) + ".txt");
            FileWriter writer = new FileWriter(gpxfile, false);
            for (PhysicalResult result : results) {
                Points points = tableWrapper.calculateResult(result);
                String TXT_SEPARATOR = ";";
                String text = result.person.id + TXT_SEPARATOR;
                text += result.person.getRank()+ "." + TXT_SEPARATOR;
                text += result.person.name + TXT_SEPARATOR;
                text += result.person.getCentrum().name()+ TXT_SEPARATOR;
                text += result.person.getDateOfBirth() + TXT_SEPARATOR;
                text += result.person.getGroup() + TXT_SEPARATOR;
                text += result.person.getGender() + TXT_SEPARATOR;
                text += result.disciplines.get("pull_up") + TXT_SEPARATOR;
                text += result.person.getGender().equals("f") ? "zena" + TXT_SEPARATOR : result.disciplines.get("jump") + TXT_SEPARATOR;
                text += result.disciplines.get("crunch") + TXT_SEPARATOR;
                text += Float.parseFloat(result.disciplines.get("sprint")) == 0 ? 99 + TXT_SEPARATOR : result.disciplines.get("sprint") + TXT_SEPARATOR;
                text += result.disciplines.get("12min") + TXT_SEPARATOR;
                text += (Float.parseFloat(result.disciplines.get("swimming")) == 0 && result.person.getGender().equals("m")) ? 999 + TXT_SEPARATOR : result.disciplines.get("swimming") + TXT_SEPARATOR;
                text += points.summary.toString() + TXT_SEPARATOR;
                text += (points.passed) ? "V" : "N";
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


