package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;

import java.util.ArrayList;
import java.util.List;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.QuizAdapter;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.QuizActivity;

public class QuizFragment extends Fragment {

    private ListView listView;
    private QuizAdapter adapter;
    private List<Quiz> lesQuizzes;
    private ActivityResultLauncher<Intent> quizLauncher;

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

        quizLauncher = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {

                        Intent data = result.getData();
                        if (data != null) {
                            int score = data.getIntExtra("score", 0);

                            android.widget.Toast.makeText(getContext(),
                                    "Score: " + score,
                                    android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


        lesQuizzes = new ArrayList<>();

        lesQuizzes.add(new Quiz("Quiz Réseautique", 20, "Terminé"));
        lesQuizzes.add(new Quiz("Quiz Applications mobiles", 12, "Terminé"));
        lesQuizzes.add(new Quiz("Quiz Bases de données", 8, "Terminé"));
        lesQuizzes.add(new Quiz("Quiz Projet intégrateur", 8, "Non commencé"));

        adapter = new QuizAdapter(requireContext(), R.layout.liste_quiz, lesQuizzes);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {

            Quiz quiz = lesQuizzes.get(position);

            Intent intent = new Intent(requireContext(), QuizActivity.class);
            intent.putExtra("titre", quiz.getTitre());
            quizLauncher.launch(intent);

        });
    }
}