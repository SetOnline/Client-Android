package com.pancake.setonline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
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

    private Typeface font;

    // Gestion de l'évènement de réception des données de classement. Appelé à partir d'un thread
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

    // Gestion de l'évènement de réception des données de classement de la semaine. Appelé à partir d'un thread
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

    // Gestion de l'évènement de réception des données de classement du jour. Appelé à partir d'un thread
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

    /**
     * Création de la vue
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classement);

        // initialisation de la police d'écriture
        font = Typeface.createFromAsset(getAssets(), Profil_model.defaultFontName);

        // initialisation des onglets
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


        LinearLayout linearLayout = (LinearLayout) myTabHost.getChildAt(0);
        TabWidget tw = (TabWidget) linearLayout .getChildAt(0);
        // tab 1
        LinearLayout firstTabLayout = (LinearLayout) tw.getChildAt(0);
        TextView tabHeader = (TextView) firstTabLayout.getChildAt(1);
        tabHeader.setTypeface(font);
        // tab 2
        LinearLayout secondTabLayout = (LinearLayout) tw.getChildAt(1);
        TextView tabHeader2 = (TextView) secondTabLayout.getChildAt(1);
        tabHeader2.setTypeface(font);
        // tab 3
        LinearLayout thirdTabLayout = (LinearLayout) tw.getChildAt(2);
        TextView tabHeader3 = (TextView) thirdTabLayout.getChildAt(1);
        tabHeader3.setTypeface(font);

        // Initialisation des listes (classements)
        lv_classement = (ListView)findViewById(R.id.lvClassement);
        lv_classement_sem = (ListView)findViewById(R.id.lvClassementSemaine);
        lv_classement_jour = (ListView)findViewById(R.id.lvClassementJour);

        // Initialisation des boîtes de texte
        TextView tvPseudoTitleCJ = (TextView)findViewById(R.id.PseudoID_cj);
        TextView tvPseudoTitleCS = (TextView)findViewById(R.id.PseudoID_cs);
        TextView tvPseudoTitleCI = (TextView)findViewById(R.id.PseudoID_ci);

        TextView tvScoreTitleCJ = (TextView)findViewById(R.id.ScoreID_cj);
        TextView tvScoreTitleCS = (TextView)findViewById(R.id.ScoreID_cs);
        TextView tvScoreTitleCI = (TextView)findViewById(R.id.ScoreID_ci);

        // Application de la police d'écriture
        tvPseudoTitleCJ.setTypeface(font);
        tvPseudoTitleCS.setTypeface(font);
        tvPseudoTitleCI.setTypeface(font);

        tvScoreTitleCJ.setTypeface(font);
        tvScoreTitleCS.setTypeface(font);
        tvScoreTitleCI.setTypeface(font);


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

            // récupération de la ligne (pseudo + score + rang)
            TextView tvPseudo = (TextView) rowView.findViewById(R.id.tv_crl_pseudo);
            TextView tvScore = (TextView) rowView.findViewById(R.id.tv_crl_score);
            TextView tvRank = (TextView) rowView.findViewById(R.id.tv_crl_rank);

            // récupération des informations
            String data[] = getItem(position).split("\n");
            String pseudo = data[0];
            String score = data[1];

            if(convertView == null ) {
                tvRank.setText(position);
                tvPseudo.setText(pseudo);
                tvScore.setText(score);

                tvPseudo.setTypeface(font);
                tvRank.setTypeface(font);
                tvScore.setTypeface(font);
            }else
                rowView = (View)convertView;

            if (position % 2 == 0) {
                rowView.setBackgroundColor(Color.argb(255, 205, 163, 104));
            } /*else {
                rowView.setBackgroundColor(Color.CYAN);
            }*/

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
