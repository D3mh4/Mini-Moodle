package com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class AssignmentDao {
    public static List<Assignment> getAssignments() throws IOException, JSONException {
        return new HttpJsonService().getAssignments();
    }

    /**
     * Met à jour le statut d'un travail sur le serveur.
     *
     * @param assignmentId L'ID du travail
     * @param newStatus    Le nouveau statut
     * @return true si réussi
     */
    public static boolean updateStatus(String assignmentId, String newStatus) throws IOException, JSONException {
        return new HttpJsonService().updateAssignmentStatus(assignmentId, newStatus);
    }
}
