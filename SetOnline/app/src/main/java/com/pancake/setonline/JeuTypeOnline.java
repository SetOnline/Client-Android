package com.pancake.setonline;

import android.app.Activity;

//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;

import com.github.nkzawa.engineio.client.Socket;

import java.net.URI;

public abstract class JeuTypeOnline extends JeuType{
    //protected Socket mSocket_s;
    //protected com.github.nkzawa.socketio.client.Socket mSocket;

    /**
     *
     * @param fj classe r�ceptrice des �v�nements du jeu
     * @param a une activity, utilis�e pour "multithreader" les calculs
     * @return VRAI si l'initialisation s'est pass�e correctement, FAUX sinon.
     */
    public boolean init(IJeu_receiver fj, Activity a){
        fenetreJeu = fj;
        act = a;

        // nodeJS, gestion de la communication client/serveur
        SocketManager.initServerConnexion();
        SocketManager.connectToServer();
        return true;
    }

    /**
     * Arr�t du mode
     */
    public void shutDown(){
        //
    }
}
