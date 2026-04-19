package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Annonce;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.ResultatQuiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class UserDao {

    public static List<User> getUsers() throws IOException, JSONException {
        return new HttpJsonService().getUsers();
    }

    public static boolean enregistrer(User user) throws IOException, JSONException {
        return new HttpJsonService().enregistrerUser(user);
    }

    public static boolean inscrire(User user) throws IOException, JSONException {
        return new HttpJsonService().inscrireUser(user);
    }

    public static boolean updateCompletedAssignments(String userId, List<String> completedIds) throws IOException, JSONException {
        return new HttpJsonService().updateCompletedAssignments(userId, completedIds);
    }

    public static boolean updateQuizResults(String userId, List<ResultatQuiz> results) throws IOException, JSONException {
        return new HttpJsonService().updateQuizResults(userId, results);
    }

    public static boolean updateUserAnnonces(String userId, List<Annonce> annonces) throws IOException, JSONException {
        return new HttpJsonService().updateUserAnnonces(userId, annonces);
    }
}
