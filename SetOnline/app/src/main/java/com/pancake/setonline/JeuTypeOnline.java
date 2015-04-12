package com.pancake.setonline;

import android.app.Activity;

//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;

import com.github.nkzawa.engineio.client.Socket;

import java.net.URI;

/**
 * Created by Matthieu on 25/03/2015.
 */
public abstract class JeuTypeOnline extends JeuType{
    //protected Socket mSocket_s;
    //protected com.github.nkzawa.socketio.client.Socket mSocket;

    public boolean init(IJeu_receiver fj, Activity a){
        fenetreJeu = fj;
        act = a;

        // nodeJS, gestion de la communication client/serveur
        SocketManager.initServerConnexion();
        SocketManager.connectToServer();
        return true;
    }

    public void shutDown(){
        // keep socket connection
    }
}
