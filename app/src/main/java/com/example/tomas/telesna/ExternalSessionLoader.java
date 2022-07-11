package com.example.tomas.telesna;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomas.R;
import com.example.tomas.common.CentrumENUM;
import com.example.tomas.common.Person;
import com.example.tomas.common.RankENUM;
import com.example.tomas.common.Session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ExternalSessionLoader {
    static String EXTERNAL_SESSION_DIR  = "/Previerky/import/";

    private static ArrayList<File> getImportFiles() {
        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + EXTERNAL_SESSION_DIR);
        if (directory.listFiles()!= null){
            return new ArrayList<>(Arrays.asList(directory.listFiles()));
        }
        return null;
    }

    private static void removeFiles(ArrayList<File> files) throws IOException {
        for(File file: files){
            if(!file.delete()) {
                throw new IOException("File could not be deleted");
            }
        }
    }

    public static void addSessionsToDatabase (FitnessDatabaseHelper db)throws ParseException, IOException{

        BufferedReader bufferedReader;
        ArrayList<File> importedFiles = getImportFiles();
        if(importedFiles == null){
            return;
        }
        for(File file: importedFiles){
                try {
                    bufferedReader = new BufferedReader(new FileReader(file));
                    Log.d("FILENAME",file.getName());

                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
                    int dressNum = 1;
                    Session session = db.createSession(dressNum, format.parse(file.getName()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] personAttr = line.split("\\s*;\\s*");
                        Person person = new Person(personAttr[0],RankENUM.validate(personAttr[1]),personAttr[2],CentrumENUM.validate(personAttr[3]), Integer.parseInt(personAttr[4]),personAttr[6],personAttr[5],"");
                        db.createOrUpdatePerson(person);
                        db.addOrReplacePersonToSession(session,person,dressNum++);
                   }
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }

        removeFiles(importedFiles);
    }
}
