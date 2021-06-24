package com.igordutrasanches.perfectscan.manager;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class StorageFile {
    private static File file;

    public StorageFile(){

    }

    public static File getInfoData(){
        try {
            File directory = checkDirectoryData();
            if(directory != null){
                File dataText = new File(directory.getAbsolutePath() + "/data");
                if(!dataText.exists()) dataText.mkdirs();
                file = new File(dataText + "/Info.txt");
                if(!file.exists()) {
                    file.createNewFile();

                }

                return file;
            }else return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri getFileShare(Context c, File f){
        Uri uri = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(c, c.getApplicationContext().getPackageName() + ".com.igordutrasanches.perfectscan.provider", f);
        }else{
            uri = Uri.parse("file://" + f.getAbsolutePath());
        } return uri;
    }

    public static File getZipData(){
        try {
            File directory = checkDirectoryData();
            if(directory != null){
                file = new File(directory + "/Translate.zip");
                if(!file.exists()) {
                    file.createNewFile();

                }else{
                    file.delete();
                    file.createNewFile();
                }

                return file;
            }else return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getXmlData(){
        try {
            File directory = checkDirectoryData();
            if(directory != null){
                File dataXml = new File(directory.getAbsolutePath() + "/data/values");
                if(!dataXml.exists()) dataXml.mkdirs();
                file = new File(dataXml + "/string.xml");
                if(!file.exists()) {
                    file.createNewFile();
                }

                return file;
            }else return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static File getFileSend(String name){
        try {
            File file = new File(getData(), name);

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getData(){
        try {
            File directory = checkDirectoryData();
            if(directory != null){
                File data = new File(directory.getAbsolutePath() + "/data");
                if(!data.exists()) data.mkdirs();


                return data;
            }else return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File checkDirectoryData() {
        try{
            File data = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.igordutrasanches.perfectscan/files");
            if(!data.exists()){
                data.mkdirs();
            }

            return data;
        }catch (Exception x){
            return null;
        }
    }

    public static File getFileSharing() {
        try{
            File path = checkDirectoryData();
            if(path != null){
                File sharePath = new File(path.getAbsolutePath() + "/.share");
                if(!sharePath.exists()){
                    sharePath.mkdirs();
                }

                file = new File(sharePath.getAbsolutePath() + "/code.png");
                if(!file.exists()){
                    file.createNewFile();
                }else {
                    file.delete();
                    file.createNewFile();
                }
                return file;
            } return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

