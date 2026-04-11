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
    private String username;
    private String email;
    private String password;
    private String nom;
    private String prenom;
    private String telephone;
    private String photoUrl;

    private List<String> enrolledCourseIds;
    private List<QuizResult> quizResults;
    private List<String> completedAssignmentIds;

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

    public List<QuizResult> getQuizResults() { return quizResults; }
    public void setQuizResults(List<QuizResult> quizResults) { this.quizResults = quizResults; }

    public List<String> getCompletedAssignmentIds() { return completedAssignmentIds; }
    public void setCompletedAssignmentIds(List<String> completedAssignmentIds) { this.completedAssignmentIds = completedAssignmentIds; }

    public static class QuizResult {
        private String quizId;
        private int score;
        private int total;

        public String getQuizId() { return quizId; }
        public void setQuizId(String quizId) { this.quizId = quizId; }

        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
    }
}