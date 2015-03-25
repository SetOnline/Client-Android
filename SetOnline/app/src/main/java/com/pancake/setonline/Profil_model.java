package com.pancake.setonline;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Matthieu on 13/03/2015.
 */
public class Profil_model {
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static Pattern pattern = null;

    public static String pseudo = null;
    public static String mail = null;

    public static String getTimeStamp(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static boolean isValidEmailAddress(String email) {
        if(pattern == null){
            pattern = Pattern.compile(EMAIL_PATTERN);
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void createAppFolderIfNeeded(){
        File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"SetOnline");
        if(!directory.exists())
            directory.mkdirs();
    }

    public static String getAppFolder(){
        return Environment.getExternalStorageDirectory()+File.separator+"SetOnline";
    }

    public static String getCookieFilename(){
        return getAppFolder()+File.separator+"pancake.cookie";
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }


}
