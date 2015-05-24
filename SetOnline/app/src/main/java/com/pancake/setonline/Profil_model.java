package com.pancake.setonline;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Socket;
import com.github.nkzawa.engineio.client.Transport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profil_model {
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static Pattern pattern = null;

    public static String pseudo = null;
    public static String mail = null;

    public static final int AVATAR_PICTURE_SIZE = 256;
    public static final String defaultFontName = "CREABBRG.TTF";

    /**
     *
     * @return le timestamp
     */
    public static String getTimeStamp(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     *
     * @param email l'adresse mail à tester
     * @return VRAI si l'adresse mail est valide, FAUX sinon
     */
    public static boolean isValidEmailAddress(String email) {
        if(pattern == null){
            pattern = Pattern.compile(EMAIL_PATTERN);
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Création du dossier "cache" de l'application si nécessaire
     */
    public static void createAppFolderIfNeeded(){
        File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"SetOnline");
        if(!directory.exists())
            directory.mkdirs();
    }

    /**
     *
     * @return chemin vers le dossier de l'application
     */
    public static String getAppFolder(){
        return Environment.getExternalStorageDirectory()+File.separator+"SetOnline";
    }

    /**
     *
     * @return chemin vers l'image de l'avatar du joueur
     */
    public static String getAvatarFilename(){
        return getAppFolder()+File.separator+"avatar.jpg";
    }

    /**
     * Redimension d'image
     * @param bm l'image à redimensionner
     * @param newWidth nouvelle largeur pour l'image
     * @param newHeight nouvelle hauteur pour l'image
     * @return la nouvelle image, redimensionnée
     */
    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // Création d'une matrice de transformation
        Matrix matrix = new Matrix();
        // redimension...
        matrix.postScale(scaleWidth, scaleHeight);

        // puis "recréation" la bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }


}
