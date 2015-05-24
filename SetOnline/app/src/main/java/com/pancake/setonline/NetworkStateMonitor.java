package com.pancake.setonline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkStateMonitor /*extends BroadcastReceiver*/ {
    /**
     * Cette fonction est appellée automatiquement lorsque la connexion du téléphone hôte change
     * @param context osef
     * @param intent osef
     */
    /*
    public void onReceive(final Context context, final Intent intent) {
        if(haveNetworkConnection(context)){
            Toast.makeText(context, "Connecté à internet !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Connexion perdue !", Toast.LENGTH_SHORT).show();
        }
    }*/

    /**
     * Test d'accès au réseau
     * @param context context de l'application
     * @return Vrai si le téléphone hôte a un accès data / wifi. Faux sinon.
     */
    public static boolean haveNetworkConnection(final Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
