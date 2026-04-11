package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class QuizDao {

    public static List<Quiz> getQuizzes() throws IOException, JSONException {
        return new HttpJsonService().getQuizzes();
    }

}
