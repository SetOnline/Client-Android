package com.pancake.setonline;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Socket;
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URI;


public class Classement extends ActionBarActivity {
    private ListView lv_classement;
    private GameClassementListAdapter lv_adapter_classement;

        private Emitter.Listener onClassementResult = new Emitter.Listener() {
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        // get JSON
                        lv_classement.setAdapter(null);

                        System.out.println("RECEIVED : " + (String)args[0]);

                        JSONArray data = null; // remise en format Json du Json stringifié
                        try {
                            data = new JSONArray((String)args[0]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }

                        String newList[] = new String[data.length()];

                        for(int i = 0; i != data.length(); ++i){
                            try {
                                newList[i] = data.getJSONObject(i).getString("name") + '\n' + data.getJSONObject(i).getString("value");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return;
                            }
                        }

                        for(String s : newList){
                            System.out.println("add : " + s);
                        }

                        lv_adapter_classement = new GameClassementListAdapter(getBaseContext(), newList);
                        lv_classement.setAdapter(lv_adapter_classement);
                    }
                });
            }
        };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classement);

        lv_classement = (ListView)findViewById(R.id.lvClassement);

        // nodeJS, gestion de la communication client/serveur
        SocketManager.initServerConnexion();
        SocketManager.connectToServer();

        SocketManager.mSocketIO.on("Reponse classement", onClassementResult);

        SocketManager.mSocketIO.emit("Demande classement");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_classement, menu);
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

    private class GameClassementListAdapter extends ArrayAdapter<String> {

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.classement_row_layout, parent, false);

            // récupération de la ligne (pseudo + score)
            TextView tvPseudo = (TextView) rowView.findViewById(R.id.tv_crl_pseudo);
            TextView tvScore = (TextView) rowView.findViewById(R.id.tv_crl_score);

            // récupération des numéros des cartes du ième set trouvé
            String data[] = getItem(position).split("\n");
            String pseudo = data[0];
            String score = data[1];

            if(convertView == null ) {
                tvPseudo.setText(pseudo);
                tvScore.setText(score);
            }else
                rowView = (View)convertView;

            return rowView;
        }

        public GameClassementListAdapter(Context context, String[] values) {
            super(context, R.layout.classement_row_layout, values);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        // déconnexion du socket
        SocketManager.mSocketIO.off("Reponse classement");
    }
}
