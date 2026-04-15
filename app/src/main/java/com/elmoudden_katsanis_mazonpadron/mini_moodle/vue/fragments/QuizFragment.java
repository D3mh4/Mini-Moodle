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

import androidx.lifecycle.ViewModelProvider;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelQuiz;
import android.widget.Toast;

public class QuizFragment extends Fragment {

    private ListView listView;
    private QuizAdapter adapter;
    private List<Quiz> lesQuizzes;
    private ActivityResultLauncher<Intent> quizLauncher;
    private ViewModelQuiz viewModel;

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
        adapter = new QuizAdapter(requireContext(), R.layout.liste_quiz, lesQuizzes);
        listView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ViewModelQuiz.class);

        quizLauncher = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            int score = data.getIntExtra("score", 0);
                            Toast.makeText(getContext(), "Score: " + score, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        viewModel.getQuizzes().observe(getViewLifecycleOwner(), quizzes -> {
            if (quizzes != null) {
                lesQuizzes.clear();
                lesQuizzes.addAll(quizzes);
                adapter.notifyDataSetChanged();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.chargerQuizzes();

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Quiz quiz = lesQuizzes.get(position);
            Intent intent = new Intent(requireContext(), QuizActivity.class);
            intent.putExtra("quiz", quiz);
            quizLauncher.launch(intent);
        });
    }
}