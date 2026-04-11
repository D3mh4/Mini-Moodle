package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

public class ResultatQuiz {
    private String idQuiz;
    private int score;
    private int total;

    public String getIdQuiz() { return idQuiz; }
    public void setIdQuiz(String idQuiz) { this.idQuiz = idQuiz; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}