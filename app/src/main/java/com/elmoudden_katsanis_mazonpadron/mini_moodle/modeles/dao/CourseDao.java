package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class CourseDao {
    public static List<Cours> getCourses() throws IOException, JSONException {
        return new HttpJsonService().getCourses();
    }
}
