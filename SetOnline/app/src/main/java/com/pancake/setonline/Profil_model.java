package com.pancake.setonline;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

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

    public static final int AVATAR_PICTURE_SIZE = 256;
    public static final String defaultFontName = "CREABBRG.TTF";

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

    public static String getAvatarFilename(){
        return getAppFolder()+File.separator+"avatar.jpg";
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

    public static String getCookieData(){
        File f = new File(Profil_model.getCookieFilename());
        if(!f.exists()) return "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(Profil_model.getCookieFilename()));
            String line = br.readLine();
            String cookie = "";

            while (line != null) {
                //headers.put("Cookie", line);
                cookie += line;
                line = br.readLine();
            }

            if(cookie.length() == 0){
                br.close();
                return "";
            }

            br.close();

            return cookie;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void activateCookies(Socket mSocket){
        System.out.println("activate cookies");


        mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // Called on a new transport created.
                Transport transport = (Transport)args[0];

                transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        File f = new File(Profil_model.getCookieFilename());
                        if(!f.exists()) return;

                        @SuppressWarnings("unchecked")
                        Map<String, String> headers = (Map<String, String>)args[0];
                        System.out.println("dafuck ?");
                        System.out.println("-----------------------");
                        System.out.println(headers.toString());
                        System.out.println("-----------------------");
                        // send cookies to server.
                        //headers.put("Cookie", "foo=1;");

                        try {
                            BufferedReader br = new BufferedReader(new FileReader(Profil_model.getCookieFilename()));
                            String line = br.readLine();
                            String cookie = "";

                            while (line != null) {
                                //headers.put("Cookie", line);
                                cookie += line;
                                line = br.readLine();
                            }

                            if(cookie.length() == 0){
                                br.close();
                                return;
                            }

                            headers.put("Cookie", cookie);
                            headers.put("Set-Cookie", cookie);
                            headers.put("X-SocketIO", cookie);

                            System.out.println("SEND : " + cookie);

                            br.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                        //System.out.println("ASK COOKIE (" + args.length + ")");
                    }
                }).on(Transport.EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> headers = (Map<String, String>)args[0];
                        System.out.println("dafuck 2 ?");
                        System.out.println("-----------------------");
                        System.out.println(headers.toString());
                        System.out.println("-----------------------");
                        // get cookies from server.
                        String cookie = headers.get("Set-Cookie");
                        if(cookie == null) return;

                        Profil_model.createAppFolderIfNeeded();

                        File file = new File(Profil_model.getCookieFilename());
                        BufferedWriter output = null;
                        try {
                            output = new BufferedWriter(new FileWriter(file));


                            /*for (Map.Entry<String, String> entry : headers.entrySet()){
                                output.write(entry.getKey() + "=" + entry.getValue() + "\n");
                            }*/
                            System.out.println("RECEIVED : " + cookie);
                            output.write(cookie);

                            output.close();
                            //System.out.println("RECEIVED COOKIE (" + args.length + ")");
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }).on(Transport.EVENT_OPEN, new Emitter.Listener(){

                    @Override
                    public void call(Object... objects) {
                        System.out.println("TRANSPORT EVENT OPEN (" + objects.length + ")");
                        for(int i = 0; i != objects.length; ++i){
                            System.out.println(objects[i]);
                        }
                    }
                }).on(Transport.EVENT_DRAIN, new Emitter.Listener() {
                    @Override
                    public void call(Object... objects) {
                        System.out.println("TRANSPORT EVENT_DRAIN (" + objects.length + ")");
                        for(int i = 0; i != objects.length; ++i){
                            System.out.println(objects[i]);
                        }
                    }
                });
            }
        });

    }
}
