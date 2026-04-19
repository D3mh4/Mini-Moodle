package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelCours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelQuiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.QuizActivity;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.QuizAdapter;

import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment {

    private ListView listView;
    private QuizAdapter adapter;
    private List<Quiz> lesQuizzes;
    private ActivityResultLauncher<Intent> quizLauncher;
    private ViewModelQuiz viewModel;
    private ViewModelUser viewModelUser;
    private ViewModelCours viewModelCours;

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
        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        viewModelCours = new ViewModelProvider(requireActivity()).get(ViewModelCours.class);

        quizLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            int score = data.getIntExtra("score", 0);
                            Toast.makeText(getContext(), "Score: " + score, Toast.LENGTH_SHORT).show();
                        }
                        viewModelUser.rechargerUserActuel();
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

        // Filtrer les quiz par cours inscrits — recharger quand l'utilisateur change
        viewModelUser.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                adapter.setUserResults(user.getQuizResults());
                if (user.getEnrolledCourseIds() != null) {
                    viewModel.chargerQuizzesInscrits(user.getEnrolledCourseIds());
                }
            }
        });

        // Fournir les cours pour que l'adaptateur puisse afficher le code (TCH...)
        viewModelCours.getEnrolledCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                adapter.setCoursesForCodeLookup(courses);
            }
        });

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Quiz quiz = lesQuizzes.get(position);
            Intent intent = new Intent(requireContext(), QuizActivity.class);
            intent.putExtra("quiz", quiz);
            if (viewModelUser.getUser().getValue() != null) {
                intent.putExtra("userId", viewModelUser.getUser().getValue().getId());
            }
            quizLauncher.launch(intent);
        });
    }
}
