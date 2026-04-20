package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Quiz implements Serializable {

    private String id;
    private String courseId;
    private String title;
    private List<Question> questions;

    public Quiz() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
    public int getNbrQuestions() {
        return questions != null ? questions.size() : 0;
    }
}
