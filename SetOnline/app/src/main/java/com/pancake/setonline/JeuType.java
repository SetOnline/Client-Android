package com.pancake.setonline;

import android.app.Activity;

public abstract class JeuType {
    protected IJeu_receiver fenetreJeu;
    protected static Activity act;
    //protected static String cookie = CookieManager.getInstance().getCookie(serverAddress);

    public abstract boolean init(IJeu_receiver fj, Activity a);

    public abstract void shutDown();

    public abstract void sendSet(String s);
}
