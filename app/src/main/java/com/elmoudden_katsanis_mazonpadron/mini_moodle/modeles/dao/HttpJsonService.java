package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao;

import android.util.Log;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpJsonService {

    private static String URL_POINT_ENTREE = "http://10.0.2.2:3000";

    public List<User> getUsers() throws IOException, JSONException {

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL_POINT_ENTREE + "/users")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String jsonStr = responseBody.string();
        List<User> users = null;

        Log.d("HttpJsonService", jsonStr);

        if (jsonStr.length() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                users = Arrays.asList(mapper.readValue(jsonStr, User[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return users;
        }
        return null;
    }

    public List<Cours> getCourses() throws IOException, JSONException {

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL_POINT_ENTREE + "/courses")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String jsonStr = responseBody.string();
        List<Cours> cours = null;

        Log.d("HttpJsonService", jsonStr);

        if (jsonStr.length() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                cours = Arrays.asList(mapper.readValue(jsonStr, Cours[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return cours;
        }
        return null;
    }

    public List<Assignment> getAssignments() throws IOException, JSONException {

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL_POINT_ENTREE + "/assignments")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String jsonStr = responseBody.string();
        List<Assignment> assignments = null;

        Log.d("HttpJsonService", jsonStr);

        if (jsonStr.length() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                assignments = Arrays.asList(mapper.readValue(jsonStr, Assignment[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return assignments;
        }
        return null;
    }

    public List<Quiz> getQuizzes() throws IOException, JSONException {

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL_POINT_ENTREE + "/quizzes")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String jsonStr = responseBody.string();
        List<Quiz> quizzes = null;

        Log.d("HttpJsonService", jsonStr);

        if (jsonStr.length() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                quizzes = Arrays.asList(mapper.readValue(jsonStr, Quiz[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return quizzes;
        }
        return null;
    }

    public boolean enregistrerUser(User user) throws IOException, JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        if (user.getEnrolledCourseIds() == null) user.setEnrolledCourseIds(new ArrayList<>());
        if (user.getQuizResults() == null) user.setQuizResults(new ArrayList<>());
        if (user.getCompletedAssignmentIds() == null) user.setCompletedAssignmentIds(new ArrayList<>());

        JSONObject obj = new JSONObject();
        obj.put("id", user.getId());
        obj.put("username", user.getUsername());
        obj.put("email", user.getEmail());
        obj.put("password", user.getPassword());
        obj.put("nom", user.getNom());
        obj.put("prenom", user.getPrenom());
        obj.put("telephone", user.getTelephone());
        obj.put("photoUrl", user.getPhotoUrl());

        obj.put("enrolledCourseIds", new org.json.JSONArray(user.getEnrolledCourseIds()));
        obj.put("quizResults", new org.json.JSONArray(user.getQuizResults()));
        obj.put("completedAssignmentIds", new org.json.JSONArray(user.getCompletedAssignmentIds()));

        RequestBody corpsRequete = RequestBody.create(obj.toString(), JSON);

        String url = URL_POINT_ENTREE + "/users/" + user.getId();

        Request request = new Request.Builder()
                .url(url)
                .put(corpsRequete)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                Log.e("HttpJsonService", "Erreur Save: " + response.code() + " " + response.message());
            }
            return response.code() == 200;
        }
    }

    public boolean inscrireUser(User user) throws IOException {

        if (user.getEnrolledCourseIds() == null) {
            user.setEnrolledCourseIds(new ArrayList<>());
        }
        if (user.getQuizResults() == null) {
            user.setQuizResults(new ArrayList<>());
        }
        if (user.getCompletedAssignmentIds() == null) {
            user.setCompletedAssignmentIds(new ArrayList<>());
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);

        RequestBody corpsRequete = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(URL_POINT_ENTREE + "/users/")
                .post(corpsRequete)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * Met à jour le statut d'un travail sur le serveur JSON.
     * Utilise PATCH pour ne modifier que le champ "status"
     * sans écraser les autres champs de l'objet.
     *
     * @param assignmentId L'ID du travail à modifier
     * @param newStatus    Le nouveau statut (ex: "Remis")
     * @return true si la mise à jour a réussi (code 200)
     */
    public boolean updateAssignmentStatus(String assignmentId, String newStatus) throws IOException, JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // Construit le corps JSON avec seulement le champ à modifier
        JSONObject obj = new JSONObject();
        obj.put("status", newStatus);

        RequestBody corpsRequete = RequestBody.create(obj.toString(), JSON);

        // PATCH sur /assignments/{id} — ne modifie que les champs envoyés
        String url = URL_POINT_ENTREE + "/assignments/" + assignmentId;

        Request request = new Request.Builder()
                .url(url)
                .patch(corpsRequete)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                Log.e("HttpJsonService", "Erreur PATCH assignment: " + response.code() + " " + response.message());
            }
            return response.code() == 200;
        }
    }
}
