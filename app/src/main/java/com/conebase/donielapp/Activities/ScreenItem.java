package com.conebase.donielapp.Activities;

public class ScreenItem {

    String titre, txt_description;
    int screenImg;

    public ScreenItem(String titre, String txt_description, int screenImg) {
        this.titre = titre;
        this.txt_description = txt_description;
        this.screenImg = screenImg;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setTxt_description(String txt_description) {
        this.txt_description = txt_description;
    }

    public void setScreenImg(int screenImg) {
        this.screenImg = screenImg;
    }

    public String getTitre() {
        return titre;
    }

    public String getTxt_description() {
        return txt_description;
    }

    public int getScreenImg() {
        return screenImg;
    }
}
