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
    private final MutableLiveData<List<Cours>> coursList = new MutableLiveData<>();

    private final MutableLiveData<List<Cours>> enrolledCourses = new MutableLiveData<>();

    private final MutableLiveData<Cours> selectedCours = new MutableLiveData<>();

    private final MutableLiveData<String> message = new MutableLiveData<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    public LiveData<List<Cours>> getEnrolledCourses() {
        return enrolledCourses;
    }

    public LiveData<Cours> getSelectedCours() {
        return selectedCours;
    }
    public void setSelectedCours(Cours cours) {
        selectedCours.setValue(cours);
    }
    public void chargerCoursInscrits(List<String> enrolledIds) {
        executorService.execute(() -> {
            try {
                List<Cours> allCourses = CourseDao.getCourses();
                List<Cours> filtered = new ArrayList<>();

                if (allCourses != null && enrolledIds != null) {
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
}
