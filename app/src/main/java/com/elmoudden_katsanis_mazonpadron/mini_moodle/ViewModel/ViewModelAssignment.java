package com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao.AssignmentDao;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewModelAssignment extends ViewModel {

    // LiveData contenant tous les travaux
    private final MutableLiveData<List<Assignment>> assignmentList = new MutableLiveData<>();

    // LiveData contenant les travaux filtrés par cours
    private final MutableLiveData<List<Assignment>> assignmentsByCourse = new MutableLiveData<>();

    // LiveData pour un travail sélectionné (écran de détails)
    private final MutableLiveData<Assignment> selectedAssignment = new MutableLiveData<>();

    // LiveData pour les messages d'erreur ou de succès
    private final MutableLiveData<String> message = new MutableLiveData<>();

    // LiveData pour indiquer si la soumission a réussi
    private final MutableLiveData<Boolean> submissionSuccess = new MutableLiveData<>();

    // ExecutorService pour les opérations en arrière-plan
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // --- Getters ---

    public LiveData<List<Assignment>> getAssignmentList() {
        return assignmentList;
    }

    public LiveData<List<Assignment>> getAssignmentsByCourse() {
        return assignmentsByCourse;
    }

    public LiveData<Assignment> getSelectedAssignment() {
        return selectedAssignment;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<Boolean> getSubmissionSuccess() {
        return submissionSuccess;
    }

    // --- Setter ---

    public void setSelectedAssignment(Assignment assignment) {
        selectedAssignment.setValue(assignment);
    }

    /**
     * Charge tous les travaux depuis le serveur JSON.
     * Utilisé par l'écran Travaux pour afficher la liste complète.
     */
    public void chargerTousLesAssignments() {
        executorService.execute(() -> {
            try {
                List<Assignment> assignments = AssignmentDao.getAssignments();
                if (assignments != null) {
                    assignmentList.postValue(assignments);
                } else {
                    assignmentList.postValue(new ArrayList<>());
                }
            } catch (IOException | JSONException e) {
                message.postValue("Erreur lors du chargement des travaux");
                assignmentList.postValue(new ArrayList<>());
            }
        });
    }

    /**
     * Charge les travaux filtrés pour un cours spécifique.
     * Utilisé dans l'écran de détails d'un cours.
     *
     * @param courseId L'ID du cours pour lequel filtrer les travaux
     */
    public void chargerAssignmentsParCours(String courseId) {
        executorService.execute(() -> {
            try {
                List<Assignment> allAssignments = AssignmentDao.getAssignments();
                List<Assignment> filtered = new ArrayList<>();

                if (allAssignments != null) {
                    for (Assignment a : allAssignments) {
                        // On compare le courseId du travail avec celui demandé
                        if (a.getCourseId() != null && a.getCourseId().equals(courseId)) {
                            filtered.add(a);
                        }
                    }
                }

                assignmentsByCourse.postValue(filtered);
            } catch (IOException | JSONException e) {
                message.postValue("Erreur lors du chargement des travaux du cours");
                assignmentsByCourse.postValue(new ArrayList<>());
            }
        });
    }

    /**
     * Charge les travaux dont la date limite approche (statut "à faire").
     * Utilisé sur le tableau de bord pour la section "travaux à remettre bientôt".
     *
     * @param enrolledCourseIds Liste des IDs de cours auxquels l'utilisateur est inscrit
     */
    public void chargerTravauxProchains(List<String> enrolledCourseIds) {
        executorService.execute(() -> {
            try {
                List<Assignment> allAssignments = AssignmentDao.getAssignments();
                List<Assignment> upcoming = new ArrayList<>();

                if (allAssignments != null && enrolledCourseIds != null) {
                    for (Assignment a : allAssignments) {
                        // On filtre : le travail doit appartenir à un cours inscrit
                        // et avoir un statut "à faire" (non encore remis)
                        if (enrolledCourseIds.contains(a.getCourseId())
                                && a.getStatus() != null
                                && a.getStatus().equalsIgnoreCase("à faire")) {
                            upcoming.add(a);
                        }
                    }
                }

                assignmentList.postValue(upcoming);
            } catch (IOException | JSONException e) {
                message.postValue("Erreur lors du chargement des travaux prochains");
                assignmentList.postValue(new ArrayList<>());
            }
        });
    }

    /**
     * Simule la soumission d'un travail.
     * Change le statut du travail à "Remis" localement.
     * Dans une application complète, on enverrait aussi la mise à jour au serveur.
     *
     * @param assignment Le travail à marquer comme remis
     */
    public void simulerSoumission(Assignment assignment) {
        if (assignment != null) {
            assignment.setStatus("Remis");
            selectedAssignment.postValue(assignment);
            message.postValue("Travail marqué comme remis !");
            submissionSuccess.postValue(true);
        }
    }
}
