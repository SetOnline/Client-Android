package com.pancake.setonline;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URI;

/**
 * Created by Matthieu on 21/03/2015.
 */
public abstract class JeuType {
    protected IJeu_receiver fenetreJeu;
    protected Socket mSocket;
    protected static final String serverAddress = "http://37.59.123.190:1337";
    protected static Activity act;

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

        mSocket.connect();
        return true;
    }

    public void shutDown(){
        // déconnexion du socket
        mSocket.disconnect();
    }

    public abstract void sendSet(String s);
}
