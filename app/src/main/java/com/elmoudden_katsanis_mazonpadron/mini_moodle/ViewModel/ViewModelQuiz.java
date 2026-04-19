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

    /**
     * Charge tous les quiz (non filtré par utilisateur).
     * Peu utilisé — préférer chargerQuizzesInscrits.
     */
    public void chargerQuizzes() {
        executorService.execute(() -> {
            try {
                List<Quiz> list = QuizDao.getQuizzes();
                quizzes.postValue(list != null ? list : new ArrayList<>());
            } catch (IOException | JSONException e) {
                error.postValue("Erreur lors du chargement des quiz : " + e.getMessage());
                quizzes.postValue(new ArrayList<>());
            }
        });
    }

    /**
     * Charge uniquement les quiz des cours auxquels l'utilisateur est inscrit.
     * Quiz.courseId est l'ID numérique du cours, qui correspond à User.enrolledCourseIds.
     */
    public void chargerQuizzesInscrits(List<String> enrolledCourseIds) {
        executorService.execute(() -> {
            try {
                List<Quiz> all = QuizDao.getQuizzes();
                List<Quiz> filtered = new ArrayList<>();
                if (all != null && enrolledCourseIds != null) {
                    for (Quiz q : all) {
                        if (q.getCourseId() != null && enrolledCourseIds.contains(q.getCourseId())) {
                            filtered.add(q);
                        }
                    }
                }
                quizzes.postValue(filtered);
            } catch (IOException | JSONException e) {
                error.postValue("Erreur lors du chargement des quiz : " + e.getMessage());
                quizzes.postValue(new ArrayList<>());
            }
        });
    }

    /**
     * Charge les quiz appartenant à un cours donné (par ID numérique).
     * Utilisé sur l'écran de détails d'un cours.
     */
    public void chargerQuizzesParCours(String courseId) {
        executorService.execute(() -> {
            try {
                List<Quiz> all = QuizDao.getQuizzes();
                List<Quiz> filtered = new ArrayList<>();
                if (all != null && courseId != null) {
                    for (Quiz q : all) {
                        if (courseId.equals(q.getCourseId())) {
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
