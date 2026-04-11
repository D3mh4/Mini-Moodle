package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

public class Question {
    private String enonce;
    private String type;
    private String[] choix;
    private String bonneReponse;

    public Question(String enonce, String type, String[] choix, String bonneReponse) {
        this.enonce = enonce;
        this.type = type;
        this.choix = choix;
        this.bonneReponse = bonneReponse;
    }

    public String getEnonce() {
        return enonce;
    }

    public String getType() {
        return type;
    }

    public String[] getChoix() {
        return choix;
    }

    public String getBonneReponse() {
        return bonneReponse;
    }
}