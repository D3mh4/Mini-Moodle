package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

import java.io.Serializable;

public class Question implements Serializable {
    private String statement;
    private String type;
    private String[] choices;
    private String correctAnswer;

    public Question() {
    }

    public Question(String statement, String type, String[] choices, String correctAnswer) {
        this.statement = statement;
        this.type = type;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getChoices() {
        return choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}