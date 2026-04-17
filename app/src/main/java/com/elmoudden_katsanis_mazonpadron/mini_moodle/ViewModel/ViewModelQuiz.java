package com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao.QuizDao;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewModelQuiz extends ViewModel {

    private final MutableLiveData<List<Quiz>> quizzes = new MutableLiveData<>();
    private final MutableLiveData<List<Quiz>> quizzesByCourse = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public LiveData<List<Quiz>> getQuizzes() {
        return quizzes;
    }

    public LiveData<List<Quiz>> getQuizzesByCourse() {
        return quizzesByCourse;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void chargerQuizzes() {
        executorService.execute(() -> {
            try {
                List<Quiz> list = QuizDao.getQuizzes();
                quizzes.postValue(list);
            } catch (IOException | JSONException e) {
                error.postValue("Erreur lors du chargement des quiz : " + e.getMessage());
            }
        });
    }

    /**
     * Charge les quiz appartenant à un cours donné.
     * Note: Quiz.idCours contient le code du cours (ex: "TCH057"),
     * pas l'ID numérique — donc on filtre avec le codeCours du Cours.
     */
    public void chargerQuizzesParCodeCours(String codeCours) {
        executorService.execute(() -> {
            try {
                List<Quiz> all = QuizDao.getQuizzes();
                List<Quiz> filtered = new ArrayList<>();
                if (all != null && codeCours != null) {
                    for (Quiz q : all) {
                        if (codeCours.equals(q.getIdCours())) {
                            filtered.add(q);
                        }
                    }
                }
                quizzesByCourse.postValue(filtered);
            } catch (IOException | JSONException e) {
                error.postValue("Erreur lors du chargement des quiz du cours : " + e.getMessage());
                quizzesByCourse.postValue(new ArrayList<>());
            }
        });
    }
}