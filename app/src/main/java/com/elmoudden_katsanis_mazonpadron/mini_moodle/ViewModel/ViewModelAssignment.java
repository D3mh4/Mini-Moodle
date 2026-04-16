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

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Récupère la liste completedAssignmentIds la plus récente
     * directement depuis le serveur pour éviter les données périmées.
     * Appelé depuis les threads secondaires uniquement.
     */
    private List<String> getCompletedIdsFrais() {
        if (currentUser == null) return null;

        try {
            List<User> users = UserDao.getUsers();
            if (users != null) {
                for (User u : users) {
                    if (u.getId().equals(currentUser.getId())) {
                        // Met à jour la référence locale aussi
                        currentUser.setCompletedAssignmentIds(u.getCompletedAssignmentIds());
                        return u.getCompletedAssignmentIds();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fallback : utilise les données locales
        return currentUser.getCompletedAssignmentIds();
    }

    /**
     * Applique le statut personnalisé par utilisateur en récupérant
     * les données fraîches depuis le serveur.
     */
    private void appliquerStatutUtilisateur(List<Assignment> assignments) {
        // Récupère les IDs complétés directement depuis le serveur
        List<String> completedIds = getCompletedIdsFrais();

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
                    // Applique les statuts per-user (récupère les données fraîches du serveur)
                    appliquerStatutUtilisateur(allAssignments);

                    for (Assignment a : allAssignments) {
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
     * Soumet un travail en ajoutant son ID à completedAssignmentIds
     * et PATCHe uniquement ce champ sur le serveur.
     */
    public void simulerSoumission(Assignment assignment) {
        if (assignment == null || assignment.getId() == null || currentUser == null) return;

        executorService.execute(() -> {
            try {
                // Récupère les IDs frais depuis le serveur d'abord
                List<String> completedIds = getCompletedIdsFrais();
                if (completedIds == null) {
                    completedIds = new ArrayList<>();
                }

                // Copie modifiable (au cas où la liste du serveur est immutable)
                completedIds = new ArrayList<>(completedIds);
                currentUser.setCompletedAssignmentIds(completedIds);

                if (!completedIds.contains(assignment.getId())) {
                    completedIds.add(assignment.getId());
                }

                // PATCH uniquement completedAssignmentIds sur le serveur
                boolean success = UserDao.updateCompletedAssignments(
                        currentUser.getId(), currentUser.getCompletedAssignmentIds());

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
