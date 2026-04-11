package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

import java.util.List;

public class Quiz {

    private String id;
    private String idCours;
    private String titre;
    private int nbrQuestions;
    private String statut; // non commence, termine
    private int score;
    private List<Question> questions;

    public Quiz() {
    }

    public Quiz(String titre, int nbrQuestions, String statut) {
        this.titre = titre;
        this.nbrQuestions = nbrQuestions;
        this.statut = statut;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCours() {
        return idCours;
    }

    public void setIdCours(String idCours) {
        this.idCours = idCours;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public int getNbrQuestions() {
        return nbrQuestions;
    }

    public void setNbrQuestions(int nbrQuestions) {
        this.nbrQuestions = nbrQuestions;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}