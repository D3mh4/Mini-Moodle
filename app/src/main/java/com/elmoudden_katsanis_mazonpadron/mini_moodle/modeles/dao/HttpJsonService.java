package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao;

import android.util.Log;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Annonce;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
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
        Request request = new Request.Builder().url(URL_POINT_ENTREE + "/users").build();

        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String jsonStr = responseBody.string();
        Log.d("HttpJsonService", jsonStr);

        if (jsonStr.length() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return Arrays.asList(mapper.readValue(jsonStr, User[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public List<Cours> getCourses() throws IOException, JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(URL_POINT_ENTREE + "/courses").build();

        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String jsonStr = responseBody.string();
        Log.d("HttpJsonService", jsonStr);

        if (jsonStr.length() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return Arrays.asList(mapper.readValue(jsonStr, Cours[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public List<Assignment> getAssignments() throws IOException, JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(URL_POINT_ENTREE + "/assignments").build();

        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String jsonStr = responseBody.string();
        Log.d("HttpJsonService", jsonStr);

        if (jsonStr.length() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return Arrays.asList(mapper.readValue(jsonStr, Assignment[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public List<Quiz> getQuizzes() throws IOException, JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(URL_POINT_ENTREE + "/quizzes").build();

        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String jsonStr = responseBody.string();
        Log.d("HttpJsonService", jsonStr);

        if (jsonStr.length() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return Arrays.asList(mapper.readValue(jsonStr, Quiz[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Met à jour les informations de profil de l'utilisateur via PATCH
     * pour ne pas écraser les listes (cours, travaux, annonces).
     */
    public boolean enregistrerUser(User user) throws IOException, JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject obj = new JSONObject();
        obj.put("username", user.getUsername());
        obj.put("email", user.getEmail());
        obj.put("password", user.getPassword());
        obj.put("nom", user.getNom());
        obj.put("prenom", user.getPrenom());
        obj.put("telephone", user.getTelephone());
        obj.put("photoUrl", user.getPhotoUrl());

        RequestBody corpsRequete = RequestBody.create(obj.toString(), JSON);
        String url = URL_POINT_ENTREE + "/users/" + user.getId();

        Request request = new Request.Builder().url(url).patch(corpsRequete).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                Log.e("HttpJsonService", "Erreur Save Profile (PATCH): " + response.code() + " " + response.message());
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
        if (user.getUserAnnonces() == null) {
            user.setUserAnnonces(new ArrayList<>());
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

    public boolean updateAssignmentStatus(String assignmentId, String newStatus) throws IOException, JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject obj = new JSONObject();
        obj.put("status", newStatus);

        RequestBody corpsRequete = RequestBody.create(obj.toString(), JSON);
        String url = URL_POINT_ENTREE + "/assignments/" + assignmentId;

        Request request = new Request.Builder().url(url).patch(corpsRequete).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                Log.e("HttpJsonService", "Erreur PATCH assignment: " + response.code() + " " + response.message());
            }
            return response.code() == 200;
        }
    }

    public boolean updateCompletedAssignments(String userId, List<String> completedIds) throws IOException, JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject obj = new JSONObject();
        obj.put("completedAssignmentIds", new JSONArray(completedIds));

        RequestBody corpsRequete = RequestBody.create(obj.toString(), JSON);
        String url = URL_POINT_ENTREE + "/users/" + userId;

        Request request = new Request.Builder().url(url).patch(corpsRequete).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    public boolean updateQuizResults(String userId, List<com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.ResultatQuiz> results) throws IOException, JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONArray arr = new JSONArray();
        if (results != null) {
            for (com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.ResultatQuiz r : results) {
                JSONObject o = new JSONObject();
                o.put("quizId", r.getQuizId());
                o.put("score", r.getScore());
                o.put("total", r.getTotal());
                arr.put(o);
            }
        }

        JSONObject obj = new JSONObject();
        obj.put("quizResults", arr);

        RequestBody corpsRequete = RequestBody.create(obj.toString(), JSON);
        String url = URL_POINT_ENTREE + "/users/" + userId;

        Request request = new Request.Builder().url(url).patch(corpsRequete).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }

    /**
     * Met à jour uniquement la liste d'annonces personnelles d'un utilisateur.
     * Utilise PATCH pour ne pas écraser les autres champs.
     */
    public boolean updateUserAnnonces(String userId, List<Annonce> annonces) throws IOException, JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONArray arr = new JSONArray();
        if (annonces != null) {
            for (Annonce a : annonces) {
                JSONObject o = new JSONObject();
                o.put("courseId", a.getCourseId());
                o.put("text", a.getText());
                arr.put(o);
            }
        }

        JSONObject obj = new JSONObject();
        obj.put("userAnnonces", arr);

        RequestBody corpsRequete = RequestBody.create(obj.toString(), JSON);
        String url = URL_POINT_ENTREE + "/users/" + userId;

        Request request = new Request.Builder().url(url).patch(corpsRequete).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }
}
