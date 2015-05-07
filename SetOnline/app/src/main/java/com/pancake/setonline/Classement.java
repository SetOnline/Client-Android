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
import android.widget.TabHost;
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
    private TabHost myTabHost;

    private ListView lv_classement_sem;
    private GameClassementListAdapter lv_adapter_classement_sem;

    private ListView lv_classement_jour;
    private GameClassementListAdapter lv_adapter_classement_jour;

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

    private Emitter.Listener onClassementSemResult = new Emitter.Listener() {
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                public void run() {
                    // get JSON
                    lv_classement_sem.setAdapter(null);

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

                    lv_adapter_classement_sem = new GameClassementListAdapter(getBaseContext(), newList);
                    lv_classement_sem.setAdapter(lv_adapter_classement_sem);
                }
            });
        }
    };

    private Emitter.Listener onClassementJourResult = new Emitter.Listener() {
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                public void run() {
                    // get JSON
                    lv_classement_jour.setAdapter(null);

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

                    lv_adapter_classement_jour = new GameClassementListAdapter(getBaseContext(), newList);
                    lv_classement_jour.setAdapter(lv_adapter_classement_jour);
                }
            });
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classement);

        myTabHost =(TabHost) findViewById(R.id.tabHost_cl_gen);
        myTabHost.setup();

        TabHost.TabSpec cinf_tab_spec = myTabHost.newTabSpec("classement_infini");

        cinf_tab_spec.setContent(R.id.tab_cinf);
        cinf_tab_spec.setIndicator("Classement global");
        myTabHost.addTab(cinf_tab_spec);

        TabHost.TabSpec csem_tab_spec = myTabHost.newTabSpec("classement_semaine");
        csem_tab_spec.setContent(R.id.tab_csem);
        csem_tab_spec.setIndicator("Classement semaine");
        myTabHost.addTab(csem_tab_spec);

        TabHost.TabSpec ctod_tab_spec = myTabHost.newTabSpec("classement_jour");
        ctod_tab_spec.setContent(R.id.tab_ctod);
        ctod_tab_spec.setIndicator("Classement jour");
        myTabHost.addTab(ctod_tab_spec);

        lv_classement = (ListView)findViewById(R.id.lvClassement);
        lv_classement_sem = (ListView)findViewById(R.id.lvClassementSemaine);
        lv_classement_jour = (ListView)findViewById(R.id.lvClassementJour);

        // nodeJS, gestion de la communication client/serveur
        SocketManager.initServerConnexion();
        SocketManager.connectToServer();

        SocketManager.mSocketIO.on("Reponse classement", onClassementResult);
        SocketManager.mSocketIO.emit("Demande classement");

        SocketManager.mSocketIO.on("Reponse classement jour", onClassementJourResult);
        SocketManager.mSocketIO.emit("Demande classement jour");

        SocketManager.mSocketIO.on("Reponse classement semaine", onClassementSemResult);
        SocketManager.mSocketIO.emit("Demande classement semaine");
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
        SocketManager.mSocketIO.off("Reponse classement");
        SocketManager.mSocketIO.off("Reponse classement jour");
        SocketManager.mSocketIO.off("Reponse classement semaine");

        super.onDestroy();
    }
}
