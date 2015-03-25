package com.pancake.setonline;

import android.app.Activity;
import android.os.CountDownTimer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Matthieu on 25/03/2015.
 */
public class JeuTypeVitesseOffline extends JeuType {
    protected int duree_partie = 60000; // 60 secondes
    protected CountDownTimer cdtTIME;
    private int nbSetsRestantsATrouver = 0;
    private String partieEnCours;

    public JeuTypeVitesseOffline(){

    }

    public boolean init(IJeu_receiver fj, Activity a){
        // pas besoin d'initialiser de connexion nodeJS
        fenetreJeu = fj;
        act = a;

        partieEnCours = Jeu_model.generateNewGame();
        try {
            JSONArray jeu = new JSONArray(partieEnCours);
            nbSetsRestantsATrouver = jeu.getJSONObject(12).getInt("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fenetreJeu.onNewGame(partieEnCours);

        // timer
        cdtTIME = new CountDownTimer(duree_partie, 1000) {

            public void onTick(long millisUntilFinished) {
                fenetreJeu.onGameTimerUpdate((int)millisUntilFinished / 1000);
            }


            public void onFinish() {
                partieEnCours = Jeu_model.generateNewGame();
                try {
                    JSONArray jeu = new JSONArray(partieEnCours);
                    nbSetsRestantsATrouver = jeu.getJSONObject(12).getInt("value");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                fenetreJeu.onNewGame(partieEnCours);

                this.start();
            }
        };
        cdtTIME.start();

        return true;
    }

    public void shutDown(){
        // pas de déconnexion

        cdtTIME.cancel();
    }

    public void sendSet(String setTrouve) {
        JSONArray nSet = new JSONArray();

        try {
            // création de 3 objets Json (stockés dans un tableau Json) contenant chacun la valeur d'une carte
            JSONObject ob1 = new JSONObject();
            ob1.put("name", "carte0");
            ob1.put("value", setTrouve.substring(0, 4));

            JSONObject ob2 = new JSONObject();
            ob2.put("name", "carte1");
            ob2.put("value", setTrouve.substring(4, 8));

            JSONObject ob3 = new JSONObject();
            ob3.put("name", "carte2");
            ob3.put("value", setTrouve.substring(8, 12));

            nSet.put(ob1);
            nSet.put(ob2);
            nSet.put(ob3);
        } catch (JSONException e){
            e.printStackTrace();
            return;
        }


        if(!Jeu_model.isAValidSet(setTrouve)) {
            String stringified = nSet.toString(); // Stringification du Json
            fenetreJeu.onSetIncorrect(stringified);
        } else {
            nbSetsRestantsATrouver--;
            JSONObject ob4 = new JSONObject();
            try {
                ob4.put("name", "nbSetsRestants");
                ob4.put("value", nbSetsRestantsATrouver);

                nSet.put(ob4);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            String stringified = nSet.toString(); // Stringification du Json
            fenetreJeu.onSetCorrect(stringified);
        }
    }
}
