package com.pancake.setonline;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class menuJeu_view extends ActionBarActivity {
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

        btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profil.class);
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
                    SocketManager.logout();
                    finish();
                }
            });
        } else {
            btnDeconnexion.setVisibility(View.INVISIBLE);
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
        super.onDestroy();
    }
}
