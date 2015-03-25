package com.pancake.setonline;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Created by Matthieu on 21/03/2015.
 */
public abstract class JeuType {
    protected IJeu_receiver fenetreJeu;
    protected static Activity act;
    //protected static String cookie = CookieManager.getInstance().getCookie(serverAddress);

    public abstract boolean init(IJeu_receiver fj, Activity a);

    public abstract void shutDown();

    public abstract void sendSet(String s);
}
