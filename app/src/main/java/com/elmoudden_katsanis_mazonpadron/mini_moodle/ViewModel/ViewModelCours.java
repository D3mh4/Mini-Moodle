package com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao.CourseDao;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewModelCours extends ViewModel {

    // LiveData contenant la liste complète des cours
    private final MutableLiveData<List<Cours>> coursList = new MutableLiveData<>();

    // LiveData contenant uniquement les cours auxquels l'utilisateur est inscrit
    private final MutableLiveData<List<Cours>> enrolledCourses = new MutableLiveData<>();

    // LiveData pour un cours sélectionné (pour l'écran de détails)
    private final MutableLiveData<Cours> selectedCours = new MutableLiveData<>();

    // LiveData pour les messages d'erreur
    private final MutableLiveData<String> message = new MutableLiveData<>();

    // ExecutorService pour exécuter les appels réseau en arrière-plan
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // --- Getters pour les LiveData ---

    public LiveData<List<Cours>> getCoursList() {
        return coursList;
    }

    public LiveData<List<Cours>> getEnrolledCourses() {
        return enrolledCourses;
    }

    public LiveData<Cours> getSelectedCours() {
        return selectedCours;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    // --- Setter pour le cours sélectionné ---

    public void setSelectedCours(Cours cours) {
        selectedCours.setValue(cours);
    }

    /**
     * Charge tous les cours depuis le serveur JSON.
     * Exécuté dans un thread séparé pour ne pas bloquer le thread principal (UI).
     */
    public void chargerTousLesCours() {
        executorService.execute(() -> {
            try {
                // Appel au DAO qui fait la requête HTTP vers le serveur JSON
                List<Cours> courses = CourseDao.getCourses();
                if (courses != null) {
                    // postValue() est utilisé car on est dans un thread secondaire
                    coursList.postValue(courses);
                } else {
                    coursList.postValue(new ArrayList<>());
                }
            } catch (IOException | JSONException e) {
                message.postValue("Erreur lors du chargement des cours");
                coursList.postValue(new ArrayList<>());
            }
        });
    }

    /**
     * Charge uniquement les cours auxquels l'utilisateur est inscrit.
     * Filtre la liste complète des cours en comparant les IDs avec la liste
     * d'IDs de cours inscrits de l'utilisateur.
     *
     * @param enrolledIds Liste des IDs de cours auxquels l'utilisateur est inscrit
     */
    public void chargerCoursInscrits(List<String> enrolledIds) {
        executorService.execute(() -> {
            try {
                List<Cours> allCourses = CourseDao.getCourses();
                List<Cours> filtered = new ArrayList<>();

                if (allCourses != null && enrolledIds != null) {
                    // Filtrage : on ne garde que les cours dont l'ID est dans enrolledIds
                    for (Cours c : allCourses) {
                        if (enrolledIds.contains(c.getId())) {
                            filtered.add(c);
                        }
                    }
                }

                enrolledCourses.postValue(filtered);
            } catch (IOException | JSONException e) {
                message.postValue("Erreur lors du chargement des cours inscrits");
                enrolledCourses.postValue(new ArrayList<>());
            }
        });
    }

    /**
     * Recherche un cours par son ID dans la liste déjà chargée.
     * Utilisé pour afficher les détails d'un cours.
     *
     * @param courseId L'ID du cours à trouver
     */
    public void chargerCoursParId(String courseId) {
        executorService.execute(() -> {
            try {
                List<Cours> allCourses = CourseDao.getCourses();
                if (allCourses != null) {
                    for (Cours c : allCourses) {
                        if (c.getId().equals(courseId)) {
                            selectedCours.postValue(c);
                            return;
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                message.postValue("Erreur lors du chargement du cours");
            }
        });
    }
}
