package com.pancake.setonline;
/**
 * Created by Matthieu on 05/03/2015.
 */
public class Jeu_model {
    /**
     *
     * @param prop1 la ième propriété de la 1ere carte
     * @param prop2 la ième propriété de la seconde carte
     * @param prop3 la ième propriété de la 3e carte
     * @return Vrai si l'ensemble des 3 propriétés sont égales / différentes. Faux sinon.
     */
    public static boolean isPropOK(int prop1,int prop2,int prop3) {
        if ((prop1 == prop2) && (prop2 == prop3) && (prop1 == prop3)) { // toutes égales
            return true;
        }
        else if ((prop1 != prop2) && (prop2 != prop3) && (prop1 != prop3)) { // toutes différentes
            return true;
        }
        else {
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
}
