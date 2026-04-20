package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Annonce {
    private String courseId;
    private String text;

    public Annonce() {}

    public Annonce(String courseId, String text) {
        this.courseId = courseId;
        this.text = text;
    }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
