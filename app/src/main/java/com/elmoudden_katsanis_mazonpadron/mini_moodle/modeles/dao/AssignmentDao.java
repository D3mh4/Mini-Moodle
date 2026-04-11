package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class AssignmentDao {
    public static List<Assignment> getAssignments() throws IOException, JSONException {
        return new HttpJsonService().getAssignments();
    }
}
