package com.pancake.setonline;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.*;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageButton;
import java.util.ArrayList;

public class Jeu_view extends ActionBarActivity implements IJeu_receiver{
    // gestionnaire des panneaux
    private SlidingMenu menuS;

    private TextView tvTimer;                       // affichage chronomètre
    private TextView tvNbSetsATrouver;              // affichage du nombre de sets restants
    private ImageButton ib[];                       // cartes de la partie en cours
    private String ibValue[];                       // valeurs des cartes

    private ListView lvSetsFound;                   // liste graphique des sets trouvés pour la partie en cours
    private GameSetsListAdapter lv_foundSets;       // adapter lié à la liste graphique des sets trouvés pour la partie en cours
    private ArrayList<String> validSetsFound;       // la liste des sets trouvés

    private ImageView ivDifficultyStar1;            // 1ere étoile de difficulté
    private ImageView ivDifficultyStar2;            // seconde étoile de difficulté
    private ImageView ivDifficultyStar3;            // 3e étoile de difficulté

    private int idSelectedCard1 = -1;               // index de la 1ere carte sélectionnée
    private int idSelectedCard2 = -1;               // index de la seconde carte sélectionnée
    private int idSelectedCard3 = -1;               // index de la 3e carte sélectionnée

    private boolean isPlaying = false;              // gestion de la 1ere partie non jouée
    private JeuType jeu;

    /**
     * Fonction appellée automatiquement pour chaque activité
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // permet de mettre en mode immersif (KIT KAT + uniquement !!!)
        /*if(android.os.Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.INVISIBLE);
        }*/

        // mise en place des panneaux
        menuS = new SlidingMenu(this);
        menuS.setMode(SlidingMenu.LEFT_RIGHT);
        menuS.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        menuS.setFadeDegree(0.35f);
        menuS.setBehindOffset((int)(((float)100/(float)720)*(float)size.x)); // dans l'idéal à adapter selon la taille de l'écran
        menuS.setShadowWidth(100);
        menuS.setShadowDrawable(R.drawable.shadow_left);
        menuS.setSecondaryShadowDrawable(R.drawable.shadow_right);
        menuS.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menuS.setMenu(R.layout.classement_partie_en_cours);
        menuS.setSecondaryMenu(R.layout.sets_trouves_partie_en_cours);

        //Enable home button
        //getSupportActionBar().setHomeButtonEnabled(true);

        //Home as up display
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        // récupération de la liste des sets trouvés situés dans le panneau de droite
        lvSetsFound = (ListView)menuS.getSecondaryMenu().findViewById(R.id.list);

        // initialisation des composants
        validSetsFound = new ArrayList<String>();

        tvTimer = (TextView)findViewById(R.id.timer);
        tvNbSetsATrouver = (TextView)findViewById(R.id.tvSetsATrouver);

        ib = new ImageButton[12];
        ibValue = new String[12];

        ib[0] = (ImageButton)findViewById(R.id.imageButton);
        ib[1] = (ImageButton)findViewById(R.id.imageButton2);
        ib[2] = (ImageButton)findViewById(R.id.imageButton3);
        ib[3] = (ImageButton)findViewById(R.id.imageButton4);
        ib[4] = (ImageButton)findViewById(R.id.imageButton5);
        ib[5] = (ImageButton)findViewById(R.id.imageButton6);
        ib[6] = (ImageButton)findViewById(R.id.imageButton7);
        ib[7] = (ImageButton)findViewById(R.id.imageButton8);
        ib[8] = (ImageButton)findViewById(R.id.imageButton9);
        ib[9] = (ImageButton)findViewById(R.id.imageButton10);
        ib[10] = (ImageButton)findViewById(R.id.imageButton11);
        ib[11] = (ImageButton)findViewById(R.id.imageButton12);

        for(int i = 0; i != 12; ++i){
            ib[i].setOnClickListener(new cardButton(i));
            ib[i].setBackgroundResource(0);
        }

        ivDifficultyStar1 = (ImageView)findViewById(R.id.ivDifficulty1);
        ivDifficultyStar2 = (ImageView)findViewById(R.id.ivDifficulty2);
        ivDifficultyStar3 = (ImageView)findViewById(R.id.ivDifficulty3);

        ivDifficultyStar1.setImageResource(0);
        ivDifficultyStar2.setImageResource(0);
        ivDifficultyStar3.setImageResource(0);

        if(SocketManager.isNetGame) {
            jeu = new JeuTypeVitesseOnline();
        } else {
            jeu = new JeuTypeVitesseOffline();
        }
        if(!jeu.init(this, this)) Toast.makeText(Jeu_view.this, getString(R.string.error_server_offline), Toast.LENGTH_LONG).show();

        //enableBroadcastReceiver(); // active la détection de perte/récupération de connexion date/wifi
    }

    /*
    public void enableBroadcastReceiver(){
        ComponentName receiver = new ComponentName(Jeu_view.this, NetworkStateMonitor.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void disableBroadcastNetworkReceiver(){
        ComponentName receiver = new ComponentName(Jeu_view.this, NetworkStateMonitor.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    */

    /**
     * récupération de l'image correspondant à la carte passée en param
     * @param name nom du fichier sans l'extension
     * @return l'id de la ressource associée à l'image
     */
    public int getCardIdFromName(String name){
        return this.getResources().getIdentifier(name, "drawable", this.getPackageName());
    }

    // permet d'annuler la sélection des 3 cartes
    private void undoSelection() {
        if(idSelectedCard1 > -1) ib[idSelectedCard1].setAlpha(1.0f);
        if(idSelectedCard2 > -1) ib[idSelectedCard2].setAlpha(1.0f);
        if(idSelectedCard3 > -1) ib[idSelectedCard3].setAlpha(1.0f);

        idSelectedCard1 = -1;
        idSelectedCard2 = -1;
        idSelectedCard3 = -1;
    }

    /**
     * gestion de la sélection des cartes
     * @param i id du bouton (de la carte sélectionnée)
     */
    private void selectCard(int i){
        if(idSelectedCard1 == i) {
            // désélection 1
            ib[i].setAlpha(1.0f);
            idSelectedCard1 = -1;
        } else if(idSelectedCard2 == i){
            // désélection 2
            ib[i].setAlpha(1.0f);
            idSelectedCard2 = -1;
        } else if(idSelectedCard3 == i){
            // désélection 3
            ib[i].setAlpha(1.0f);
            idSelectedCard3 = -1;
        } else if(idSelectedCard1 == -1){
            // sélection 1
            ib[i].setAlpha(0.4f);
            idSelectedCard1 = i;
        } else if(idSelectedCard2 == -1){
            // sélection 2
            ib[i].setAlpha(0.4f);
            idSelectedCard2 = i;
        } else if(idSelectedCard3 == -1){
            // sélection 3
            ib[i].setAlpha(0.4f);
            idSelectedCard3 = i;
            jeu.sendSet(ibValue[idSelectedCard1] + ibValue[idSelectedCard2] + ibValue[idSelectedCard3]);
            undoSelection();
        }
    }

    // gestion du menu . . .
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jeu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                menuS.toggle(true);
                return true;
            case R.id.action_rightPanel:
                if(menuS.isSecondaryMenuShowing())
                    menuS.toggle(true);
                else
                    menuS.showSecondaryMenu(true);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * fonction appellée automatiquement lors de la fermeture de l'activité
     */
    public void onDestroy() {
        super.onDestroy();

        jeu.shutDown();

        // disableBroadcastNetworkReceiver(); // arrêt de la détection de la perte/récupération de la connexion data/wifi
    }

    /**
     * évènement synchronisation du timer
     */
    public void onGameTimerUpdate(int newTime) {
        // conversion du nombre de secondes en minute : seconde
        int seconds = 0;
        int minutes = 0;

        seconds = newTime;
        minutes = seconds / 60;
        seconds = seconds % 60;

        String finalSeconds = (seconds < 10) ? "0" + seconds: seconds + "";
        String finalMinutes = (minutes < 10) ? "0" + minutes: minutes + "";

        if(isPlaying){
            tvTimer.setText(finalMinutes + ":" + finalSeconds);
        } else {
            tvTimer.setText("Une partie est déjà en cours... (" +finalMinutes + ":" + finalSeconds + ")");
        }
    }

    /**
     * évènement nouvelle partie
     * Données au format Json:
     * name: carte0 value:1232
     * ...
     * name: carte11 value:1232
     * name:nbSets value:5
     */
    public void onNewGame(String cartes) {
        validSetsFound.clear(); // remise à zéro des sets trouvés pour la partie en cours
        lvSetsFound.setAdapter(null);
        undoSelection(); // remise à zéro de la sélection des cartes

        try {
            JSONArray data = new JSONArray(cartes); // remise en format Json du Json stringifié

            for(int i = 0; i != 12; ++i){
                try { // récupération des 12 cartes
                    ib[i].setBackgroundResource(getCardIdFromName("i" + data.getJSONObject(i).getString("value")));
                    ibValue[i] = data.getJSONObject(i).getString("value");
                } catch(JSONException e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    return;
                }
            }
            try{
                tvNbSetsATrouver.setText(data.getJSONObject(12).getString("value") + " sets trouvables !"); // affichage du nombre de sets trouvables pour la partie en cours

                // affichage de la difficulé
                int nbFoundableSets =Integer.parseInt(data.getJSONObject(12).getString("value"));
                if(nbFoundableSets < 3){ // partie difficile : 3 étoiles
                    ivDifficultyStar1.setImageResource(R.drawable.star);
                    ivDifficultyStar2.setImageResource(R.drawable.star);
                    ivDifficultyStar3.setImageResource(R.drawable.star);
                } else if(nbFoundableSets < 6){ // partie intermédiaire : 2 étoiles
                    ivDifficultyStar1.setImageResource(R.drawable.star);
                    ivDifficultyStar2.setImageResource(R.drawable.star);
                    ivDifficultyStar3.setImageResource(0);
                } else { // partie facile : 1 étoile
                    ivDifficultyStar1.setImageResource(R.drawable.star);
                    ivDifficultyStar2.setImageResource(0);
                    ivDifficultyStar3.setImageResource(0);
                }
            } catch(JSONException e){
                e.printStackTrace();
                System.out.println(e.getMessage());
                return;
            }

            isPlaying = true; // on participe à la partie en cours (débloque l'interface, en effet, celle ci est bloquée lorsqu'on
            // arrive sur le jeu car pour la première partie, on ne récupère pas les cartes
        } catch (JSONException e) {
            return;
        }
    }

    /**
     * évènement récupération d'un set valide
     * Données au format Json:
     * name:carte0 value:1231
     * name:carte1 value:1222
     * name:carte2 value:2131
     * name: value:
     */
    public void onSetCorrect(String setCorrect) {
        String newValidSet = "";
        try {
            String s = setCorrect; // récupération du Json stringifié contenant le set
            JSONArray data = new JSONArray(s);
            for(int i = 0; i != 3; ++i){
                // récupération des 3 cartes formant le set
                try {
                    newValidSet += data.getJSONObject(i).getString("value");
                } catch(JSONException e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    return;
                }
            }

            //tvNbSetsATrouver.setText(data.getJSONObject(3).getString("value") + " sets restants !");
        } catch (JSONException e) {
            return;
        }
        // ajout de ce set à la liste des sets trouvés de la partie en cours
        validSetsFound.add(newValidSet);
        //lv_foundSets.add(newValidSet);
        // on recrée l'adapteur des sets trouvés et on le remplit avec les sets précédemment trouvés
        String lst[] = new String[validSetsFound.size()];
        for(int i = 0; i != validSetsFound.size(); ++i){
            lst[i] = validSetsFound.get(i);
        }
        lv_foundSets = new GameSetsListAdapter(getBaseContext(), lst);
        lvSetsFound.setAdapter(lv_foundSets);

        Toast.makeText(Jeu_view.this, "Set valide \\o/", Toast.LENGTH_LONG).show();
    }

    /**
     * évènement récupération d'un set invalide
     */
    public void onSetIncorrect(String setIncorrect) {
        Toast.makeText(Jeu_view.this, "Set invalide :(", Toast.LENGTH_LONG).show();
    }

    /**
     * Listener des cartes ( des 12 boutons )
     */
    private class cardButton implements View.OnClickListener {
        private int idx;
        public cardButton(int index) {
            super();
            // keep references for your onClick logic
            idx = index;
        }

        public void onClick(View v) {
            //System.out.println("Select card " + idx);
            if(isPlaying)
                selectCard(idx);
        }
    }

    /**
     * Adapter : représente une ligne dans la liste des sets trouvés
     */
    private class GameSetsListAdapter extends ArrayAdapter<String> {

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.set_row_layout, parent, false);

            // récupération des images correspondant aux cartes du ième set trouvé
            ImageView c1 = (ImageView) rowView.findViewById(R.id.card1);
            ImageView c2 = (ImageView) rowView.findViewById(R.id.card2);
            ImageView c3 = (ImageView) rowView.findViewById(R.id.card3);

            // récupération des numéros des cartes du ième set trouvé
            int res_c1 = getCardIdFromName("i" + getItem(position).substring(0, 4));
            int res_c2 = getCardIdFromName("i" + getItem(position).substring(4, 8));
            int res_c3 = getCardIdFromName("i" + getItem(position).substring(8, 12));

            if(convertView == null ) {
                c1.setImageResource(res_c1);
                c2.setImageResource(res_c2);
                c3.setImageResource(res_c3);
            }else
                rowView = (View)convertView;

            return rowView;
        }

        public GameSetsListAdapter(Context context, String[] values) {
            super(context, R.layout.set_row_layout, values);
        }
    }
}
