package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

import java.util.List;

public class Course {
    private String id;
    private String code;
    private String title;
    private String description;
    private String teacher;
    private String session;
    private String imageUrl;
    private List<String> annonces;


    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getSession() {
        return session;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getAnnonces() {
        return annonces;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAnnonces(List<String> annonces) {
        this.annonces = annonces;
    }
}