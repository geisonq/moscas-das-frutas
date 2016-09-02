package com.geisonquevedo.moscadasfrutas.utils;

/**
 * Created by Geison Quevedo on 07/04/2016.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.geisonquevedo.moscadasfrutas.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public  class SaveBitmap {

    public void save(Bitmap bmp, String name, Boolean saveInSubFolder, String subFolderName ) {
        String path;

        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            Log.d("SaveBitmap", "Erro ao criar Diretório");

        }

        //Image Name
        String fileName = name + ".jpg";

        //Image Path

        if(saveInSubFolder) {
            path = pictureFileDir.getPath() + File.separator + "mosca_" + subFolderName;
        } else {
            path = pictureFileDir.getPath();
        }
        Log.i("SAEVE","SAVE:"+path);
        //Check  folder and create if not exists
        verifyFolder(path);

        File file = new File(path + File.separator + fileName);

        try {
            OutputStream outStream = null;
            outStream = new FileOutputStream(file);
            Log.i("SAEVE","TENTAR SAVE:"+path);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();



            if(!saveInSubFolder){
                 String sdcardBmpPath = path + File.separator + "image.bmp";
                 AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
                 boolean isSaveResult = bmpUtil.save(bmp, sdcardBmpPath);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public File getDir() {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "MoscaDasFrutas");
    }

    private boolean verifyFolder(String path) {
        File folder = new File(path);

        boolean success = true;

        if (!folder.exists()) {
            success = folder.mkdir();
        }

        return success;
    }

}