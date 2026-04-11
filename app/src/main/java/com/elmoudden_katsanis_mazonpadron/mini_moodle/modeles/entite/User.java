package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonPropertyOrder({
        "id",
        "username",
        "email",
        "password",
        "nom",
        "prenom",
        "telephone",
        "photoUrl",
        "enrolledCourseIds",
        "quizResults",
        "completedAssignmentIds"
})
public class User {

    private String id;
    private String nomUtilisateur;
    private String courriel;
    private String motDePasse;
    private String nom;
    private String prenom;
    private String telephone;
    private String imageIcone;

    private List<String> coursInscrits;
    private List<ResultatQuiz> resultatsQuiz;
    private List<String> devoirsCompletes;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }

    public String getCourriel() { return courriel; }
    public void setCourriel(String courriel) { this.courriel = courriel; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getImageIcone() { return imageIcone; }
    public void setImageIcone(String imageIcone) { this.imageIcone = imageIcone; }

    public List<String> getCoursInscrits() { return coursInscrits; }
    public void setCoursInscrits(List<String> coursInscrits) { this.coursInscrits = coursInscrits; }

    public List<ResultatQuiz> getResultatsQuiz() { return resultatsQuiz; }
    public void setResultatsQuiz(List<ResultatQuiz> resultatsQuiz) { this.resultatsQuiz = resultatsQuiz; }

    public List<String> getDevoirsCompletes() { return devoirsCompletes; }
    public void setDevoirsCompletes(List<String> devoirsCompletes) { this.devoirsCompletes = devoirsCompletes; }

}