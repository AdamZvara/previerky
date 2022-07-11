package com.example.tomas.common;

import android.os.Environment;

import java.io.File;

public class ExternalStorage {
    public static String getSDCardPath(){

        return Environment.getExternalStorageDirectory().getParentFile().getParentFile() + File.separator + "/mnt/sdcard/external_sd";
    }
    public static String getNormalPath(){
        return Environment.getExternalStorageDirectory().toString();
    }
}
