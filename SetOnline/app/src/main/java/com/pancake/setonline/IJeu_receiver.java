package com.pancake.setonline;

public interface IJeu_receiver{
    public void onGameTimerUpdate(int newTime);
    public void onNewGame(String cartes);
    public void onSetCorrect(String setCorrect);
    public void onSetIncorrect(String setIncorrect);
    public void onSetDejaDonne(String setIncorrect);
    public void updateClassement(String[] cls);
    public void unlockTrophy(String picName, String name, String desc);
}