package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

import java.util.List;

public class Cours {
    private String id;
    private String codeCours;
    private String titre;
    private String description;
    private String enseignant;
    private String session;
    private String urlImage;
    private List<String> annonces;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodeCours() {
        return codeCours;
    }

    public void setCodeCours(String codeCours) {
        this.codeCours = codeCours;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnseignant() {
        return enseignant;
    }

    public void setEnseignant(String enseignant) {
        this.enseignant = enseignant;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public List<String> getAnnonces() {
        return annonces;
    }

    public void setAnnonces(List<String> annonces) {
        this.annonces = annonces;
    }
}