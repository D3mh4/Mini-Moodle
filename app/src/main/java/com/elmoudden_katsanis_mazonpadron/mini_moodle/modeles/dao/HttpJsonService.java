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

    public boolean enregistrerUser(User user) throws IOException, JSONException {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        if (user.getCoursInscrits() == null) {
            user.setCoursInscrits(new ArrayList<>());
        }
        if (user.getResultatsQuiz() == null) {
            user.setResultatsQuiz(new ArrayList<>());
        }
        if (user.getDevoirsCompletes() == null) {
            user.setDevoirsCompletes(new ArrayList<>());
        }

        JSONObject obj = new JSONObject();
        obj.put("id", user.getId());
        obj.put("username", user.getNomUtilisateur());
        obj.put("email", user.getCourriel());
        obj.put("password", user.getMotDePasse());
        obj.put("nom", user.getNom());
        obj.put("prenom", user.getPrenom());
        obj.put("telephone", user.getTelephone());
        obj.put("photoUrl", user.getImageIcone());
        obj.put("enrolledCourseIds", user.getCoursInscrits());
        obj.put("quizResults", user.getResultatsQuiz());
        obj.put("completedAssignmentIds", user.getDevoirsCompletes());

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

        if (user.getCoursInscrits() == null) {
            user.setCoursInscrits(new ArrayList<>());
        }
        if (user.getResultatsQuiz() == null) {
            user.setResultatsQuiz(new ArrayList<>());
        }
        if (user.getDevoirsCompletes() == null) {
            user.setDevoirsCompletes(new ArrayList<>());
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