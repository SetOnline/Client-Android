package com.pancake.setonline;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Matthieu on 21/03/2015.
 */
public class JeuTypeVitesseOnline extends JeuTypeOnline {
    protected CountDownTimer cdtUpdateClassement;
    private boolean playing = false;
    private ArrayList<JSONArray> setsTrouves;
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
                    playing = true;
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
                    if(!playing){
                        playing = true;
                        // récupération de la partie en cours
                        if((int) args[0] > 5){
                            SocketManager.mSocketIO.emit("Demande partie en cours");
                            System.out.println("emit Demande partie en cours");
                        }
                    }
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
                    try {
                        JSONArray jsa = new JSONArray((String) args[0]);
                        setsTrouves.add(jsa);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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

    private Emitter.Listener onClassementUpdate = new Emitter.Listener() {
        public void call(final Object... args) {
            act.runOnUiThread(new Runnable() {
                public void run() {
                    //HashMap<String, Integer> c = new HashMap<String, Integer>();
                    String[] c = null;

                    try {
                        System.out.println((String) args[0]);
                        JSONArray data = new JSONArray((String) args[0]);
                        c = new String[data.length()];
                        for(int i = 0; i != data.length(); ++i){
                            JSONObject jso = data.getJSONObject(i);

                            //c.put(jso.getString("name"), jso.getInt("value"));
                            c[i] = jso.getString("rank") + " : " + jso.getString("name") + "\n" + jso.getInt("value");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    fenetreJeu.updateClassement(c);
                }
            });
        }
    };

    private Emitter.Listener onUnlockTrophy = new Emitter.Listener() {
        public void call(final Object... args) {
            act.runOnUiThread(new Runnable() {
                public void run() {
                    //fenetreJeu.onSetIncorrect((String) args[0]);
                    try {
                        JSONObject jso = new JSONObject((String)args[0]);
                        fenetreJeu.unlockTrophy(jso.getString("pic"), jso.getString("name"), jso.getString("desc"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public boolean init(IJeu_receiver fj, Activity a){
        boolean res = super.init(fj, a);
        if(!res) return false;

        setsTrouves = new ArrayList<JSONArray>();

        // écoute des évènements
        SocketManager.mSocketIO.on("Nouvelle partie", onNewGame);
        SocketManager.mSocketIO.on("timer", onTimerUpdate);
        SocketManager.mSocketIO.on("Set valide", onSetValide);
        SocketManager.mSocketIO.on("Set invalide", onSetInvalide);
        SocketManager.mSocketIO.on("Reponse classement partie actuelle", onClassementUpdate);
        SocketManager.mSocketIO.on("Deblocage trophee", onUnlockTrophy);

        // timer
        cdtUpdateClassement = new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {
                //fenetreJeu.onGameTimerUpdate((int)millisUntilFinished / 1000);
                SocketManager.mSocketIO.emit("Demande classement partie actuelle");
                System.out.println("Demande classement partie actuelle");
            }

            public void onFinish() {
                setsTrouves = new ArrayList<JSONArray>();
                this.start();
            }
        };
        cdtUpdateClassement.start();

        return true;
    }

    @Override
    public void shutDown() {
        super.shutDown();

        // arrêt de l'écoute des évenements serveur
        SocketManager.mSocketIO.off("Nouvelle partie");
        SocketManager.mSocketIO.off("timer");
        SocketManager.mSocketIO.off("Set valide");
        SocketManager.mSocketIO.off("Set invalide");
        SocketManager.mSocketIO.off("Reponse classement partie actuelle");
        SocketManager.mSocketIO.off("Deblocage trophee");
    }

    @Override
    public void sendSet(String setTrouve) {
        JSONArray nSet = new JSONArray();

        for(int i = 0; i != setsTrouves.size(); ++i){
            try {
                JSONObject c1 = setsTrouves.get(i).getJSONObject(0);
                JSONObject c2 = setsTrouves.get(i).getJSONObject(1);
                JSONObject c3 = setsTrouves.get(i).getJSONObject(2);

                boolean b1, b2, b3;
                b1 = c1.getString("value").equals(setTrouve.substring(0, 4)) ||
                        c1.getString("value").equals(setTrouve.substring(4, 8)) ||
                        c1.getString("value").equals(setTrouve.substring(8, 12));
                b2 = c2.getString("value").equals(setTrouve.substring(0, 4)) ||
                        c2.getString("value").equals(setTrouve.substring(4, 8)) ||
                        c2.getString("value").equals(setTrouve.substring(8, 12));
                b3 = c3.getString("value").equals(setTrouve.substring(0, 4)) ||
                        c3.getString("value").equals(setTrouve.substring(4, 8)) ||
                        c3.getString("value").equals(setTrouve.substring(8, 12));

                if(b1 && b2 && b3){
                    fenetreJeu.onSetDejaDonne(nSet.toString()); // set déjà donné
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }

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

        SocketManager.mSocketIO.emit("Set", stringified); // émission de l'évènement set
    }
}
