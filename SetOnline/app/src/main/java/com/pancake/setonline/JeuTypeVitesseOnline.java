package com.pancake.setonline;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Matthieu on 21/03/2015.
 */
public class JeuTypeVitesseOnline extends JeuType {
    /**
     * évènement nouvelle partie
     * Données au format Json:
     * name: carte0 value:1232
     * ...
     * name: carte11 value:1232
     * name:nbSets value:5
     */
    private Emitter.Listener onNewGame = new Emitter.Listener() {
        public void call(final Object... args) {
            act.runOnUiThread(new Runnable() {
                public void run() {
                    fenetreJeu.onNewGame((String) args[0]);
                }
            });
        }
    };

    /**
     * évènement synchronisation du timer
     */
    private Emitter.Listener onTimerUpdate = new Emitter.Listener() {
        public void call(final Object... args) {
            act.runOnUiThread(new Runnable() {
                public void run() {
                fenetreJeu.onGameTimerUpdate((int) args[0]);
                }
            });
        }
    };

    /**
     * évènement récupération d'un set valide
     * Données au format Json:
     * name:carte0 value:1231
     * name:carte1 value:1222
     * name:carte2 value:2131
     * name: value:
     */
    private Emitter.Listener onSetValide = new Emitter.Listener() {
        public void call(final Object... args) {
            act.runOnUiThread(new Runnable() {
                public void run() {
                    fenetreJeu.onSetCorrect((String) args[0]);
                }
            });
        }
    };

    /**
     * évènement récupération d'un set invalide
     */
    private Emitter.Listener onSetInvalide = new Emitter.Listener() {
        public void call(final Object... args) {
            act.runOnUiThread(new Runnable() {
                public void run() {
                fenetreJeu.onSetIncorrect((String) args[0]);
                }
            });
        }
    };

    public boolean init(IJeu_receiver fj, Activity a){
        boolean res = super.init(fj, a);
        if(!res) return false;

        // écoute des évènements
        mSocket.on("Nouvelle partie", onNewGame);
        mSocket.on("timer", onTimerUpdate);
        mSocket.on("Set valide", onSetValide);
        mSocket.on("Set invalide", onSetInvalide);

        return true;
    }

    @Override
    public void shutDown() {
        super.shutDown();

        // arrêt de l'écoute des évenements serveur
        mSocket.off("Nouvelle partie", onNewGame);
        mSocket.off("timer", onTimerUpdate);
        mSocket.off("Set valide", onSetValide);
        mSocket.off("Set invalide", onSetInvalide);
    }

    @Override
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

        String stringified = nSet.toString(); // Stringification du Json

        // première vérification en local permettant de tester si le set sélectionné est un set valide
        if(!Jeu_model.isAValidSet(setTrouve)) {
            fenetreJeu.onSetIncorrect(stringified);
            return;
        }

        mSocket.emit("Set", stringified); // émission de l'évènement set
    }
}
