package com.pancake.setonline;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.content.Intent;
import android.widget.RelativeLayout;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class menuJeu_view extends ActionBarActivity {

    private Emitter.Listener onGetNewFriendDemands = new Emitter.Listener() {
        public void call(final Object... args) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    JSONArray ja = new JSONArray((String)args[0]);

                    for(int i = 0; i != ja.length(); ++i){
                        final JSONObject jo = ja.getJSONObject(i);
                        final String ps = jo.getString("name");

                        AlertDialog.Builder dlgAddFriend = new AlertDialog.Builder(menuJeu_view.this);
                        dlgAddFriend.setMessage(ps + " aimerait être votre ami. Accepter ?");
                        dlgAddFriend.setTitle("Set!");
                        dlgAddFriend.setPositiveButton("Oui", new DialogInterface.OnClickListener(){

                            public void onClick(DialogInterface dialog, int which) {
                                SocketManager.mSocketIO.emit("Accepter ami", ps);
                            }
                        });
                        dlgAddFriend.setNegativeButton("Non", new DialogInterface.OnClickListener(){

                            public void onClick(DialogInterface dialog, int which) {
                                SocketManager.mSocketIO.emit("Refuser ami", ps);
                            }
                        });
                        dlgAddFriend.setCancelable(false);
                        dlgAddFriend.create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
        }
    };

    /**
     * Fonction appellée automatiquement pour chaque activité
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        // vérification de la présence d'une connexion data/wifi, dans le cas où il n'y a aucune connexion, redirection vers le menu wifi
        /*if(!NetworkStateMonitor.haveNetworkConnection(this.getBaseContext())){
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }*/

        // définition des trois boutons
        Button btnJouer = (Button)findViewById(R.id.buttonJouer);
        Button btnProfil = (Button)findViewById(R.id.buttonMonProfil);
        Button btnClassement = (Button)findViewById(R.id.buttonClassement);
        Button btnDeconnexion = (Button)findViewById(R.id.buttonDisconnect);

        //définition de l'action des trois boutons
        btnJouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Jeu_view.class);
                startActivity(intent);
            }
        });

        btnClassement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Classement.class);
                startActivity(intent);
            }
        });
        if(SocketManager.isNetGame) {
            btnDeconnexion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            btnProfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Profil.class);
                    startActivity(intent);
                }
            });

            SocketManager.mSocketIO.on("Reponse liste demandes amis", onGetNewFriendDemands);
            SocketManager.mSocketIO.emit("Demande liste demandes amis");

        } else {
            btnDeconnexion.setVisibility(View.INVISIBLE);

            btnProfil.setBackgroundResource(R.drawable.button_grise);
            btnProfil.setClickable(false);
        }
    }

    //gestion du menu . . .
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        if(SocketManager.isNetGame) {
            SocketManager.logout();
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    //fonction appellée à la fermeture de l'activité
    public void onDestroy() {
        SocketManager.mSocketIO.off("Reponse liste demandes amis");

        super.onDestroy();
    }
}
