package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;

import java.util.ArrayList;
import java.util.List;

import adaptateurs.QuizAdapter;
import modele.Quiz;

public class QuizFragment extends Fragment {

    private ListView listView;
    private QuizAdapter adapter;
    private List<Quiz> lesQuizzes;

    public QuizFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quiz_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listViewQuiz);

        lesQuizzes = new ArrayList<>();

        lesQuizzes.add(new Quiz("Quiz Réseautique", 20, "Terminé"));
        lesQuizzes.add(new Quiz("Quiz Applications mobiles", 12, "Terminé"));
        lesQuizzes.add(new Quiz("Quiz Bases de données", 8, "Terminé"));
        lesQuizzes.add(new Quiz("Quiz Projet intégrateur", 8, "Non commencé"));

        adapter = new QuizAdapter(getContext(), R.layout.liste_quiz, lesQuizzes);

        listView.setAdapter(adapter);
    }
}