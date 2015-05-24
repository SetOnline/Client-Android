package com.pancake.setonline;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class SocketManager {
    public static Socket mSocketEngine = null;
    public static com.github.nkzawa.socketio.client.Socket mSocketIO = null;
    protected static final String serverAddress = "SERVER_ADDRESS";
    public static boolean isNetGame = true;

    /**
     * Initialisation de la connexion au serveur
     */
    public static void initServerConnexion(){
        if(mSocketEngine == null && mSocketIO == null){
            System.out.println("INITIALIZATION...");
            try {
                mSocketEngine = new Socket(new URI(serverAddress));
                mSocketIO = com.github.nkzawa.socketio.client.IO.socket(new URI(serverAddress));

                System.out.println("OK");
                //connect();
                // interface

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Connexion au serveur
     */
    public static void connectToServer(){
        if(!mSocketIO.connected()){
            System.out.println("CONNECTION...");
            activateEngineCookies(mSocketEngine);
            activateSocketIOCookies(mSocketIO);

            mSocketEngine.open();
        }
    }

    /**
     * déconnexion
     */
    public static void disconnectFromServer(){
        System.out.println("DISCONNECTION...");
        if(mSocketEngine != null){
            mSocketEngine.close();
        }

        if(mSocketIO != null){
            mSocketIO.disconnect();
        }
    }

    /**
     *
     * @return le chemin vers le fichier cookie Node.js
     */
    public static String getCookieFilename(){
        return Profil_model.getAppFolder()+File.separator+"pancakeEngine.cookie";
    }

    /**
     *
     * @return le chemin vers le cookie Socket.IO
     */
    public static String getCookieFilename2(){
        return Profil_model.getAppFolder()+File.separator+"pancakeIO.cookie";
    }

    /**
     * suppression des cookies
     */
    public static void destroyCookies(){
        File f = new File(getCookieFilename());
        if(f.exists()) f.delete();

        f = new File(getCookieFilename2());
        if(f.exists()) f.delete();
    }

    /**
     * lance la gestion des cookies (pour la communication avec le serveur). Permet le maintient de la session Node.js.
     * @param mSocket socket de connexion
     */
    public static void activateEngineCookies(final Socket mSocket){
        mSocket.on(Socket.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // Called on a new transport created.
                Transport transport = (Transport) args[0];

                transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        File f = new File(getCookieFilename());
                        if (!f.exists()) return;

                        @SuppressWarnings("unchecked")
                        Map<String, String> headers = (Map<String, String>) args[0];
                        // send cookies to server.
                        //headers.put("Cookie", "foo=1;");

                        try {
                            BufferedReader br = new BufferedReader(new FileReader(getCookieFilename()));
                            String line = br.readLine();
                            String cookie = "";

                            while (line != null) {
                                //headers.put("Cookie", line);
                                cookie += line;
                                line = br.readLine();
                            }
                            br.close();

                            f = new File(getCookieFilename());
                            if (f.exists()) {
                                br = new BufferedReader(new FileReader(getCookieFilename2()));
                                line = br.readLine();
                                cookie += ";";
                                while (line != null) {
                                    //headers.put("Cookie", line);
                                    cookie += line;
                                    line = br.readLine();
                                }
                                br.close();
                            }

                            if (cookie.length() == 0) {
                                return;
                            }

                            headers.put("Cookie", cookie);

                            System.out.println("SEND : " + cookie);

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
                        Map<String, String> headers = (Map<String, String>) args[0];
                        // get cookies from server.
                        String cookie = headers.get("Set-Cookie");
                        if (cookie == null) return;

                        Profil_model.createAppFolderIfNeeded();

                        File file = new File(getCookieFilename());
                        BufferedWriter output = null;
                        try {
                            output = new BufferedWriter(new FileWriter(file));

                            System.out.println("RECEIVED : " + cookie);
                            output.write(cookie);

                            output.close();

                            if (!mSocketIO.connected())
                                mSocketIO.connect(); // we have our session; launch Socket.IO

                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                });
            }
        });

    }

    /**
     * lance la gestion des cookies (pour la communication avec le serveur). Permet le maintient de la session Socket.IO.
     * @param mSocket socket de connexion
     */
    public static void activateSocketIOCookies(final com.github.nkzawa.socketio.client.Socket mSocket) {
        mSocket.io().on(Socket.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // Called on a new transport created.
                Transport transport = (Transport) args[0];

                transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> headers = (Map<String, String>) args[0];
                        // send cookies to server.
                        //headers.put("Cookie", "foo=1;");

                        try {
                            File f = new File(getCookieFilename2());
                            String line;
                            String cookie;
                            BufferedReader br;

                            if (f.exists()) {

                                br = new BufferedReader(new FileReader(getCookieFilename2()));
                                line = br.readLine();
                                cookie = "";

                                while (line != null) {
                                    //headers.put("Cookie", line);
                                    cookie += line;
                                    line = br.readLine();
                                }
                                br.close();
                            } else {
                                cookie = "io=0";
                            }

                            f = new File(getCookieFilename());
                            if (f.exists()) {
                                br = new BufferedReader(new FileReader(getCookieFilename()));
                                line = br.readLine();
                                cookie += ";";
                                while (line != null) {
                                    //headers.put("Cookie", line);
                                    cookie += line;
                                    line = br.readLine();
                                }

                                if (cookie.length() == 0) {
                                    br.close();
                                    return;
                                }
                            } else {
                                cookie += ";connect.sid=0;Path=/;HttpOnly";
                            }

                            headers.put("Cookie", cookie);

                            System.out.println("SEND : " + cookie);

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
                        Map<String, String> headers = (Map<String, String>) args[0];
                        // get cookies from server.
                        String cookie = headers.get("Set-Cookie");
                        if (cookie == null) return;

                        Profil_model.createAppFolderIfNeeded();

                        File file = new File(getCookieFilename2());
                        BufferedWriter output = null;
                        try {
                            output = new BufferedWriter(new FileWriter(file));

                            System.out.println("RECEIVED : " + cookie);
                            output.write(cookie);

                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                });
            }
        });
    }

    /**
     * Déconnexion (du compte du joueur, pas du serveur)
     */
    public static void logout(){
        if(mSocketIO != null && mSocketEngine != null && mSocketIO.connected())
            mSocketIO.emit("Deco");
    }
}
