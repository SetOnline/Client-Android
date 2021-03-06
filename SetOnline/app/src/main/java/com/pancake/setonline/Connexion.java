package com.pancake.setonline;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.transports.Polling;
/*
import com.github.nkzawa.engineio.client.transports.WebSocket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;*/

import com.github.nkzawa.engineio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.SSLContext;

public class Connexion extends ActionBarActivity {
    // NodeJS
    //private Socket mSocket;
    //com.github.nkzawa.socketio.client.Socket m_io_socket;

    private String nickname = null;

    private EditText etNickname;
    private EditText etPassword;

    private Button btRegistration;
    private Button btAnonymousGame;

    //private ImageView loading;

    // Gestion de l'�v�nement de r�ception du r�sultat de la connexion. Appel� � partir d'un thread
    private Emitter.Listener onConnexionResult = new Emitter.Listener() {
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Integer res = (Integer)args[0];
                    if(res == 0){
                        Toast.makeText(Connexion.this, getString(R.string.error_info_connection), Toast.LENGTH_SHORT).show();
                        nickname = null;
                        Profil_model.pseudo = null;
                    } else {
                        Profil_model.pseudo = nickname;
                        SocketManager.isNetGame = true;
                        Intent intent = new Intent(getApplicationContext(), menuJeu_view.class);
                        startActivity(intent);
                    }
                }
            });
        }
    };

    /**
     * Initialisation de la vue
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        etNickname = (EditText)findViewById(R.id.login);
        etPassword = (EditText)findViewById(R.id.mdp);

        /*loading = (ImageView)findViewById(R.id.ivLoading);
        loading.setBackgroundResource(R.drawable.load_animation);
        AnimationDrawable animLoad = (AnimationDrawable)loading.getBackground();
        animLoad.start();*/

        // destruction et r�initialisation des cookies
        SocketManager.destroyCookies();
        SocketManager.initServerConnexion();
        SocketManager.connectToServer();

        // TEST
        /*LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
        dialog.setView(dialoglayout);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(true);
        dialog.show();*/

        //
        SocketManager.mSocketIO.on("Resultat connexion", onConnexionResult);

        // bouton de connexion
        ImageView btnSeConnecter = (ImageView)findViewById(R.id.ivConnect);
        btnSeConnecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray inscription_packet = new JSONArray();
                JSONObject json_pseudo = new JSONObject();
                JSONObject json_psswd = new JSONObject();

                try {
                    json_pseudo.put("name", "pseudo");
                    json_pseudo.put("value", etNickname.getText().toString());
                    nickname = etNickname.getText().toString();

                    json_psswd.put("name", "mdp");
                    json_psswd.put("value", etPassword.getText().toString());

                    inscription_packet.put(json_pseudo);
                    inscription_packet.put(json_psswd);

                    //Toast.makeText(Connexion.this, "connexion...", Toast.LENGTH_SHORT).show();
                    SocketManager.mSocketIO.emit("Connexion", inscription_packet.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // bouton d'inscription
        btRegistration = (Button)findViewById(R.id.btRegistration);
        btRegistration.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), Inscription_view.class);
                startActivity(intent);
            }
        });

        // chargement et application de la police d'�criture
        Typeface font = Typeface.createFromAsset(getAssets(), Profil_model.defaultFontName);

        TextView tvAppName = (TextView)findViewById(R.id.tvAppName);
        tvAppName.setTypeface(font);
        etNickname.setTypeface(font);
        etPassword.setTypeface(font);
        btRegistration.setTypeface(font);

        // jouer sans connexion
        btAnonymousGame = (Button)findViewById(R.id.btAnonymousGame);
        btAnonymousGame.setTypeface(font);
        btAnonymousGame.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                SocketManager.isNetGame = false;
                Intent intent = new Intent(getApplicationContext(), menuJeu_view.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_connexion, menu);
        return true;
    }

    @Override
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

    public void onDestroy() {
        SocketManager.mSocketIO.off("Resultat connexion");
        SocketManager.disconnectFromServer();
        SocketManager.destroyCookies();

        super.onDestroy();
    }
}
