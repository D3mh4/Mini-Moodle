package com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao.QuizDao;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewModelQuiz extends ViewModel {

    private final MutableLiveData<List<Quiz>> quizzes = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public LiveData<List<Quiz>> getQuizzes() {
        return quizzes;
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
}