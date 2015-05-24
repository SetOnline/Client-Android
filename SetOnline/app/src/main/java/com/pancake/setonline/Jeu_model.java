package com.pancake.setonline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class Jeu_model {
    public enum gameMod{
        timed_online,
        timed_offline;
    }

    public static gameMod actualGameMode;

    /**
     *
     * @param prop1 la ième propriété de la 1ere carte
     * @param prop2 la ième propriété de la seconde carte
     * @param prop3 la ième propriété de la 3e carte
     * @return Vrai si l'ensemble des 3 propriétés sont égales / différentes. Faux sinon.
     */
    public static boolean isPropOK(int prop1, int prop2, int prop3) {
        if ((prop1 == prop2) && (prop2 == prop3) && (prop1 == prop3)) { // toutes égales
            return true;
        } else if ((prop1 != prop2) && (prop2 != prop3) && (prop1 != prop3)) { // toutes différentes
            return true;
        } else {
            return false;
        }

    }

    /**
     * chaque carte doit être donnée sous la forme abcd avec a, b, c, d appartenant à [1;3]
     * @param carte1 la 1ere carte
     * @param carte2 la seconde carte
     * @param carte3 la 3e carte
     * @return Vrai si l'ensemble des 3 cartes forme un set. Faux sinon.
     */
    public static boolean isAValidSet(String carte1, String carte2, String carte3) {
        boolean setCorrect = true;
        for (int i = 0; i < 4; i++) {
            boolean propCorrect = isPropOK(carte1.charAt(i), carte2.charAt(i), carte3.charAt(i));
            if (!propCorrect) {
                setCorrect = false;
            }
        }
        return setCorrect;
    }

    /**
     *
     * @param setTrouve l'ensemble des 3 cartes, concaténées.
     * @return Vrai si l'ensemble des 3 cartes forme un set. Faux sinon.
     */
    public static boolean isAValidSet(String setTrouve){
        return isAValidSet(setTrouve.substring(0, 4), setTrouve.substring(4, 8), setTrouve.substring(8, 12));
    }

    ///////////////////////////
    // abcd
    // avec
    // a : couleur (1 : R, 2 : V, 3 : B)
    // b : remplissage (1 : plein, 2 : rayé, 3 : vide)
    // c : quantité (1, 2, 3)
    // d : forme (1 : éclair, 2 : sphère, 3 : triangle)
    public static String genererCombinaison() {
        String combi = "";
        Random r = new Random();
        for (int i = 0; i != 4; ++i) {
            combi += (1+r.nextInt(3));
        }

        return combi;
    }

    /**
     *
     * @param tab le tableau de cartes tirées
     * @param carte la carte à tester
     * @return VRAI si la carte a déjà été tirée, faux sinon
     */
    public static boolean carteDejaTiree(JSONArray tab, String carte) {
        for (int i = 0; i != tab.length(); ++i) {
            try {
                JSONObject c = tab.getJSONObject(i);

                if (c.getString("value").equals(carte)) {
                    return true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return true;
            }
        }

        return false;
    }

    /**
     * Génère une nouvelle partie
     * @return la nouvelle partie
     */
    public static String generateNewGame(){
        JSONArray tabCartes = new JSONArray();
        int nbSetsTrouvablesPartieEnCours = 0;

        while (nbSetsTrouvablesPartieEnCours == 0) {
            tabCartes = new JSONArray(); // on delete tout
            for (int i = 0; i != 12; ++i) {
                String carteTiree = genererCombinaison();
                if (carteDejaTiree(tabCartes, carteTiree)) {
                    i--;
                } else {
                    //result.push({ name: name, goals: goals[name] });
                    JSONObject carte = new JSONObject();
                    try {
                        carte.put("name", "carte"+i);
                        carte.put("value", carteTiree);
                        tabCartes.put(carte);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            nbSetsTrouvablesPartieEnCours = getNbSolutions(tabCartes.toString());
        }

        JSONObject nbSets = new JSONObject();
        try {
            nbSets.put("name", "nbSets");
            nbSets.put("value", nbSetsTrouvablesPartieEnCours);
            tabCartes.put(nbSets);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return tabCartes.toString();
    }

    /**
     *
     * @param partie JSON stringifié de la partie en cours
     * @return le nombre de solutions dans la partie
     */
    public static int getNbSolutions(String partie){
        JSONArray tabCartes = null;
        try {
            tabCartes = new JSONArray(partie);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        int res = 0;

        for (int i = 0; i != 10; ++i) {
            for (int j = i+1; j != 11; ++j) {
                for (int k = j+1; k != 12; ++k) {
                    try {
                        JSONObject c1 = tabCartes.getJSONObject(i);
                        JSONObject c2 = tabCartes.getJSONObject(j);
                        JSONObject c3 = tabCartes.getJSONObject(k);

                        if (isAValidSet(c1.getString("value"), c2.getString("value"), c3.getString("value"))) {
                            res++;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return -1;
                    }
                }
            }
        }

        return res;
    }
}
