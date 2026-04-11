package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao;

import android.util.Log;

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

/**
 * Service pour les appels HTTP vers l'API JSON.
 */
public class HttpJsonService {

    private static String URL_POINT_ENTREE = "http://10.0.2.2:3000";

    /**
     * Récupère la liste des comptes depuis l'API.
     */
    public List<User> getUsers()  throws IOException, JSONException {

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

    /**
     * Envoie un compte mis à jour vers l'API.
     */
    public boolean enregistrerUser(User user) throws IOException, JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        if (user.getEnrolledCourseIds() == null) {
            user.setEnrolledCourseIds(new ArrayList<>());
        }
        if (user.getQuizResults() == null) {
            user.setQuizResults(new ArrayList<>());
        }
        if (user.getCompletedAssignmentIds() == null) {
            user.setCompletedAssignmentIds(new ArrayList<>());
        }

        JSONObject obj = new JSONObject();
        obj.put("id", user.getId());
        obj.put("username", user.getUsername());
        obj.put("email", user.getEmail());
        obj.put("password", user.getPassword());
        obj.put("nom", user.getNom());
        obj.put("prenom", user.getPrenom());
        obj.put("telephone", user.getTelephone());
        obj.put("photoUrl", user.getPhotoUrl());
        obj.put("enrolledCourseIds", user.getEnrolledCourseIds());
        obj.put("quizResults", user.getQuizResults());
        obj.put("completedAssignmentIds", user.getCompletedAssignmentIds());

        RequestBody corpsRequete = RequestBody.create(obj.toString(), JSON);
        String url = URL_POINT_ENTREE + "/user/" + user.getId();

        Request request = new Request.Builder()
                .url(url)
                .put(corpsRequete)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response.code() == 200;
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
}