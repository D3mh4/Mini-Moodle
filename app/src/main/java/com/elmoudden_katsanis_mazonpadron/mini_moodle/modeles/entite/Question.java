package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Question implements Serializable {
    private String id;
    private String question;
    private String[] options;
    private int correctOption;

    public Question() {
    }

    public Question(String question, String[] options, int correctOption) {
        this.question = question;
        this.options = options;
        this.correctOption = correctOption;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String[] getOptions() { return options; }
    public void setOptions(String[] options) { this.options = options; }

    public int getCorrectOption() { return correctOption; }
    public void setCorrectOption(int correctOption) { this.correctOption = correctOption; }

    /**
     * Aide : renvoie le texte de la bonne réponse à partir de l'index.
     */
    public String getCorrectAnswerText() {
        if (options != null && correctOption >= 0 && correctOption < options.length) {
            return options[correctOption];
        }
        return null;
    }
}
