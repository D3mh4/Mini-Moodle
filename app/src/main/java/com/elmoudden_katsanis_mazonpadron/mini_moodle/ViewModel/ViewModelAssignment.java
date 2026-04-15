package com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao.AssignmentDao;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao.UserDao;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewModelAssignment extends ViewModel {

    private final MutableLiveData<List<Assignment>> assignmentList = new MutableLiveData<>();
    private final MutableLiveData<List<Assignment>> assignmentsByCourse = new MutableLiveData<>();
    private final MutableLiveData<Assignment> selectedAssignment = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> submissionSuccess = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Référence vers l'utilisateur courant pour accéder à ses completedAssignmentIds
    private User currentUser;

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

    public void setSelectedAssignment(Assignment assignment) {
        selectedAssignment.setValue(assignment);
    }

    /**
     * Stocke la référence vers l'utilisateur courant.
     * Appelé depuis les fragments quand l'utilisateur est chargé.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Applique le statut personnalisé par utilisateur :
     * - Si l'ID du travail est dans completedAssignmentIds → "Remis"
     * - Sinon, vérifie si la date est dépassée → "En retard"
     * - Sinon, garde le statut par défaut du serveur
     */
    private void appliquerStatutUtilisateur(List<Assignment> assignments) {
        List<String> completedIds = (currentUser != null) ? currentUser.getCompletedAssignmentIds() : null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date today = new Date();

            for (Assignment a : assignments) {
                // Si l'utilisateur a soumis ce travail → Remis
                if (completedIds != null && completedIds.contains(a.getId())) {
                    a.setStatus("Remis");
                }
                // Sinon, si la date est dépassée et toujours "À faire" → En retard
                else if (a.getDueDate() != null && a.getStatus() != null
                        && a.getStatus().equalsIgnoreCase("à faire")) {
                    Date dueDate = sdf.parse(a.getDueDate());
                    if (dueDate != null && dueDate.before(today)) {
                        a.setStatus("En retard");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chargerTousLesAssignments() {
        executorService.execute(() -> {
            try {
                List<Assignment> assignments = AssignmentDao.getAssignments();
                if (assignments != null) {
                    appliquerStatutUtilisateur(assignments);
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

    public void chargerAssignmentsParCours(String courseId) {
        executorService.execute(() -> {
            try {
                List<Assignment> allAssignments = AssignmentDao.getAssignments();
                List<Assignment> filtered = new ArrayList<>();

                if (allAssignments != null) {
                    for (Assignment a : allAssignments) {
                        if (a.getCourseId() != null && a.getCourseId().equals(courseId)) {
                            filtered.add(a);
                        }
                    }
                }

                appliquerStatutUtilisateur(filtered);
                assignmentsByCourse.postValue(filtered);
            } catch (IOException | JSONException e) {
                message.postValue("Erreur lors du chargement des travaux du cours");
                assignmentsByCourse.postValue(new ArrayList<>());
            }
        });
    }

    public void chargerTravauxProchains(List<String> enrolledCourseIds) {
        executorService.execute(() -> {
            try {
                List<Assignment> allAssignments = AssignmentDao.getAssignments();
                List<Assignment> upcoming = new ArrayList<>();

                if (allAssignments != null && enrolledCourseIds != null) {
                    // Applique les statuts utilisateur d'abord
                    appliquerStatutUtilisateur(new ArrayList<>(allAssignments));

                    for (Assignment a : allAssignments) {
                        // Applique le statut per-user avant de filtrer
                        List<String> completedIds = (currentUser != null) ? currentUser.getCompletedAssignmentIds() : null;
                        if (completedIds != null && completedIds.contains(a.getId())) {
                            a.setStatus("Remis");
                        }

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
     * Soumet un travail en ajoutant son ID à la liste completedAssignmentIds
     * de l'utilisateur courant, puis sauvegarde l'utilisateur sur le serveur.
     *
     * Le statut global du travail sur le serveur ne change PAS —
     * chaque utilisateur a sa propre liste de travaux complétés.
     */
    public void simulerSoumission(Assignment assignment) {
        if (assignment == null || assignment.getId() == null || currentUser == null) return;

        executorService.execute(() -> {
            try {
                // Ajoute l'ID du travail à la liste des travaux complétés de l'utilisateur
                List<String> completedIds = currentUser.getCompletedAssignmentIds();
                if (completedIds == null) {
                    completedIds = new ArrayList<>();
                    currentUser.setCompletedAssignmentIds(completedIds);
                }

                // Vérifie qu'on ne l'ajoute pas en double
                if (!completedIds.contains(assignment.getId())) {
                    completedIds.add(assignment.getId());
                }

                // Sauvegarde l'utilisateur mis à jour sur le serveur (PUT /users/{id})
                boolean success = UserDao.enregistrer(currentUser);

                if (success) {
                    assignment.setStatus("Remis");
                    selectedAssignment.postValue(assignment);
                    message.postValue("Travail marqué comme remis !");
                    submissionSuccess.postValue(true);
                } else {
                    message.postValue("Erreur lors de la soumission.");
                    submissionSuccess.postValue(false);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                message.postValue("Erreur réseau : " + e.getMessage());
                submissionSuccess.postValue(false);
            }
        });
    }
}
