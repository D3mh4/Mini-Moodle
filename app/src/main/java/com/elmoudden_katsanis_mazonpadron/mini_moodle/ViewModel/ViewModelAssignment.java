package com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao.AssignmentDao;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao.UserDao;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Annonce;
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

    public LiveData<List<Assignment>> getAssignmentList() { return assignmentList; }
    public LiveData<List<Assignment>> getAssignmentsByCourse() { return assignmentsByCourse; }
    public LiveData<Assignment> getSelectedAssignment() { return selectedAssignment; }
    public LiveData<String> getMessage() { return message; }
    public LiveData<Boolean> getSubmissionSuccess() { return submissionSuccess; }

    public void setSelectedAssignment(Assignment assignment) {
        selectedAssignment.setValue(assignment);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private List<String> rafraichirUserEtRecupererCompleted() {
        if (currentUser == null) return null;
        try {
            List<User> users = UserDao.getUsers();
            if (users != null) {
                for (User u : users) {
                    if (u.getId().equals(currentUser.getId())) {
                        currentUser.setCompletedAssignmentIds(u.getCompletedAssignmentIds());
                        currentUser.setUserAnnonces(u.getUserAnnonces());
                        return u.getCompletedAssignmentIds();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentUser.getCompletedAssignmentIds();
    }
    private void appliquerStatutUtilisateur(List<Assignment> assignments) {
        List<String> completedIds = rafraichirUserEtRecupererCompleted();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date today = new Date();

            for (Assignment a : assignments) {
                if (a.getStatus() != null && a.getStatus().equalsIgnoreCase("corrigé")) {
                    continue;
                }
                if (completedIds != null && completedIds.contains(a.getId())) {
                    a.setStatus("Remis");
                }
                else if (a.getDueDate() != null && a.getStatus() != null
                        && (a.getStatus().equalsIgnoreCase("non soumis")
                            || a.getStatus().equalsIgnoreCase("à faire"))) {
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
                List<Assignment> filtered = new ArrayList<>();

                if (assignments != null) {
                    List<String> enrolled = currentUser != null ? currentUser.getEnrolledCourseIds() : null;
                    for (Assignment a : assignments) {
                        // Si pas d'utilisateur ou pas de liste inscrite, on affiche tout (sécurité)
                        // Sinon on filtre par courseId
                        if (enrolled == null || enrolled.contains(a.getCourseId())) {
                            filtered.add(a);
                        }
                    }
                    appliquerStatutUtilisateur(filtered);
                }

                assignmentList.postValue(filtered);
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

    public void simulerSoumission(Assignment assignment) {
        if (assignment == null || assignment.getId() == null || currentUser == null) return;

        executorService.execute(() -> {
            try {
                // Rafraîchir d'abord depuis le serveur
                rafraichirUserEtRecupererCompleted();

                // Ajout à completedAssignmentIds
                List<String> completedIds = currentUser.getCompletedAssignmentIds();
                if (completedIds == null) completedIds = new ArrayList<>();
                completedIds = new ArrayList<>(completedIds);
                if (!completedIds.contains(assignment.getId())) {
                    completedIds.add(assignment.getId());
                }
                currentUser.setCompletedAssignmentIds(completedIds);

                // Ajout de l'annonce en tête (top / newest first)
                List<Annonce> annonces = currentUser.getUserAnnonces();
                if (annonces == null) annonces = new ArrayList<>();
                annonces = new ArrayList<>(annonces);
                String text = (assignment.getTitle() != null ? assignment.getTitle() : "") + " soumis";
                annonces.add(0, new Annonce(assignment.getCourseId(), text));
                currentUser.setUserAnnonces(annonces);
                boolean ok1 = UserDao.updateCompletedAssignments(currentUser.getId(), completedIds);
                boolean ok2 = UserDao.updateUserAnnonces(currentUser.getId(), annonces);

                if (ok1 && ok2) {
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
