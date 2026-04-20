package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;


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
        "completedAssignmentIds",
        "userAnnonces"
})

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String id;
    private String username;
    private String email;
    private String password;
    private String nom;
    private String prenom;
    private String telephone;
    private String photoUrl;
    private List<String> enrolledCourseIds = new ArrayList<>();
    private List<ResultatQuiz> quizResults = new ArrayList<>();
    private List<String> completedAssignmentIds = new ArrayList<>();
    private List<Annonce> userAnnonces = new ArrayList<>();

    public User() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public List<String> getEnrolledCourseIds() { return enrolledCourseIds; }
    public void setEnrolledCourseIds(List<String> enrolledCourseIds) { this.enrolledCourseIds = enrolledCourseIds; }
    public List<ResultatQuiz> getQuizResults() { return quizResults; }
    public void setQuizResults(List<ResultatQuiz> quizResults) { this.quizResults = quizResults; }
    public List<String> getCompletedAssignmentIds() { return completedAssignmentIds; }
    public void setCompletedAssignmentIds(List<String> completedAssignmentIds) { this.completedAssignmentIds = completedAssignmentIds; }
    public List<Annonce> getUserAnnonces() { return userAnnonces; }
    public void setUserAnnonces(List<Annonce> userAnnonces) { this.userAnnonces = userAnnonces; }
}
