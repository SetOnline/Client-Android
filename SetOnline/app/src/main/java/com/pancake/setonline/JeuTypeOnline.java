package com.pancake.setonline;

import android.app.Activity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URI;

/**
 * Created by Matthieu on 25/03/2015.
 */
public abstract class JeuTypeOnline extends JeuType{
    protected Socket mSocket;
    protected static final String serverAddress = "http://37.59.123.190:1337";

    public boolean init(IJeu_receiver fj, Activity a){
        fenetreJeu = fj;
        act = a;

        // nodeJS, gestion de la communication client/serveur
        try {
            mSocket = IO.socket(new URI(serverAddress));
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("error initializing mSocket");
            return false;
        }

        /**
         * Fonction appellée automatiquement lors d'une connexion réussie
         */
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            public void call(Object... args) {
                //Log.d("ActivityName: ", "socket connected");
                //Toast.makeText(Jeu.this, "Connection réussie !", Toast.LENGTH_LONG).show();
                // emit anything you want here to the server
                //socket.emit("login", some);
                //socket.disconnect();
            }

            // this is the emit from the server
        });

        //mSocket.addHeader("Cookie", cookie);

        /*mSocket.on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // Called on a new transport created.
                Transport transport = (Transport)args[0];

                transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> headers = (Map<String, String>)args[0];
                        // send cookies to server.
                        //headers.put("Cookie", "foo=1;");

                        try {
                            BufferedReader br = new BufferedReader(new FileReader(Profil_model.getCookieFilename()));
                            String line = br.readLine();

                            while (line != null) {
                                headers.put("Cookie", line);
                                line = br.readLine();
                            }

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }).on(Transport.EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> headers = (Map<String, String>)args[0];
                        // get cookies from server.
                        String cookie = headers.get("Set-Cookie");

                        Profil_model.createAppFolderIfNeeded();

                        File file = new File(Profil_model.getCookieFilename());
                        BufferedWriter output = null;
                        try {
                            output = new BufferedWriter(new FileWriter(file));


                            for (Map.Entry<String, String> entry : headers.entrySet()){
                                output.write(entry.getKey() + "=" + entry.getValue() + "\n");
                            }

                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                });
            }
        });*/

        mSocket.connect();
        return true;
    }

    public void shutDown(){
        // déconnexion du socket
        mSocket.disconnect();
    }
}
