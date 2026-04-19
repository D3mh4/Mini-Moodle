package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cours {
    private String id;
    private String code;
    private String title;
    private String description;
    private String teacher;
    private String session;
    private String imageUrl;
    private List<String> annonces;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }

    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getAnnonces() { return annonces; }
    public void setAnnonces(List<String> annonces) { this.annonces = annonces; }
}
