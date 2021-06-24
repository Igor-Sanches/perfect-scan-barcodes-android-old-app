package com.igordutrasanches.perfectscan.compoments;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.igordutrasanches.perfectscan.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GoName {

    private static String root() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Perfect Scan/" + ResourceLoader.getString(mContext, R.string.arquivos) + "/";
    }

    private static Context mContext;
    public static final String PATH_TXT = "";
    public static final String PATH_PDF = "PDF";

    public GoName(Context mContext) {
        this.mContext = mContext;
    }

    public static GoName by(Context mContext) {
        return new GoName(mContext);
    }

    public static Pattern NOT = Pattern.compile("[^A-Za-z0-9]");
    public File getFile(String name, String format, int type) {
        String caminho = "";
        File file;
        String ex = "";
        try{

            if(type == 1){
                caminho = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + ResourceLoader.getString(mContext, R.string.app_name) + "/" + ResourceLoader.getString(mContext, R.string.arquivos) + "/" + ResourceLoader.getString(mContext, R.string.arquivos_of_png) + "/" + getCodeNameFolder(format);
                ex = ".png";
            }else{
                caminho = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + ResourceLoader.getString(mContext, R.string.app_name) + "/" + ResourceLoader.getString(mContext, R.string.arquivos) + "/" + ResourceLoader.getString(mContext, R.string.savetxt) + "/" + getCodeNameFolder(format);
                ex = ".txt";
            }

            File pasta = new File(caminho);
            if (!pasta.exists()) pasta.mkdirs();

            String nameCode = NOT.matcher(name).replaceAll("_");
            if(nameCode.length() >= 24){
                nameCode = nameCode.substring(0, 24);
            }

            String filePath = caminho + "/" + getNameFile(pasta.getAbsolutePath(), nameCode, ex);
            file = new File(filePath);
            return file;
        }catch (Exception X){
            Toast.makeText(mContext, X.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    private String getCodeNameFolder(String format) {
        String name = "";
        switch (format){
            case "AZTEC":{
                name = getRes(R.string.aztec);
            }break;
            case "CODABAR":{
                name = getRes(R.string.barcode);
            }break;
            case "CODE_128":{
                name = getRes(R.string.code128);
            }break;
            case "CODE_39":{
                name = getRes(R.string.code39);
            }break;
            case "CODE_93":{
                name = getRes(R.string.code93);
            }break;
            case "EAN_13":{
                name = getRes(R.string.ean_13);
            }break;
            case "EAN_8":{
                name = getRes(R.string.ean_8);
            }break;
            case "ITF":{
                name = getRes(R.string.itf);
            }break;
            case "PDF_417":{
                name = getRes(R.string.pdf);
            }break;
            case "QR_CODE":{
                name = getRes(R.string.qr);
            }break;
            case "UPC_E":{
                name = getRes(R.string.upc_e);
            }break;
            case "UPC_A":{
                name = getRes(R.string.upc_a);
            }break;
        }
        return name;
    }

    String getRes(int index){
        return ResourceLoader.getString(mContext, index);
    }
    private String getNameFile(String absolutePath, String name, String ex) {
        try{
            //Primeira
            File file = new File(absolutePath+"/"+ name + ex);
            if(file.exists()){

                //Segunda
                File file2 = new File(absolutePath + "/" + name + " (" + lenght(absolutePath, name, ex) + ")" + ex);
                if(file2.exists()){

                    //Terceiro
                    File file3 = new File(absolutePath + "/" + name + " (" + String.valueOf(Long.valueOf(lenght(absolutePath, name, ex)) + 1) + ")" + ex);
                    if(file3.exists()){

                        //Quarta
                        File file4 = new File(absolutePath + "/" + name + " (" + lenght2(absolutePath, name, ex) + ")" + ex);
                        if(file4.exists()){

                            //Quinta
                            File file5 = new File(absolutePath + "/" + name + " (" + new File(absolutePath).listFiles().length + ")" + ex);
                            if(file5.exists()){

                                //Serta
                                File file6 = new File(absolutePath + "/" + name + " (" + DateTime.Now(mContext).toString("dd_MM_yyyy HH_mm_ss") + ")" + ex);
                                return file6.getName();

                            }else return file5.getName();


                        }else return file4.getName();

                    }else return file3.getName();

                }else return file2.getName();

            }else return file.getName();

        }catch (Exception X){
            Toast.makeText(mContext, X.getMessage(), Toast.LENGTH_SHORT).show();
            return "null";
        }
    }


    private String lenght(String absolutePath, String name, String ex) {
        try{
            List<String> names = new ArrayList<>();
            File files = new File(absolutePath);
            for (File file : files.listFiles()){
                String i = file.getName().replace(ex, "").replace(" (", "").replace(")", "").replace("1", "").replace("2","").replace("3","").replace("4", "").replace("5", "").replace("6","").replace("7", "").replace("8","").replace("9","").replace("0","");
                if(i.equals(name)){
                    names.add(file.getName());
                }
            }
            return names.size() + "";

        }catch (Exception X){
            Toast.makeText(mContext, X.getMessage(), Toast.LENGTH_SHORT).show();
            return "null";
        }

    }

    private String lenght2(String absolutePath, String name, String ex) {
        try{
            List<String> names = new ArrayList<>();
            File files = new File(absolutePath);
            for (File file : files.listFiles()){
                String i = file.getName().replace(ex, "").replace(" (", "").replace(")", "").replace("1", "").replace("2","").replace("3","").replace("4", "").replace("5", "").replace("6","").replace("7", "").replace("8","").replace("9","").replace("0","");
                if(i.equals(name)){
                    names.add(file.getName());
                }
            }

            return names.get(names.size()).replace(" (", "").replace(ex, "").replace(")", "").replace(name, "");

        }catch (Exception X){
            Toast.makeText(mContext, X.getMessage(), Toast.LENGTH_SHORT).show();
            return "null";
        }

    }


}