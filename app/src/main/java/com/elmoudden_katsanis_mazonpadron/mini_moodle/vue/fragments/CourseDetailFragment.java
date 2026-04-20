package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelAssignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelCours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelQuiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Annonce;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.QuizActivity;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.AssignmentAdapter;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.DashboardQuizAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseDetailFragment extends Fragment {
    private ViewModelCours viewModelCours;
    private ViewModelAssignment viewModelAssignment;
    private ViewModelQuiz viewModelQuiz;
    private ViewModelUser viewModelUser;
    private AssignmentAdapter assignmentAdapter;
    private DashboardQuizAdapter quizAdapter;
    private TextView tvCodeCours, tvTitreCours, tvEnseignant, tvSession;
    private TextView tvDescription;
    private LinearLayout llAnnonces;
    private TextView tvAucuneAnnonce;
    private RecyclerView rvTravaux;
    private TextView tvAucunTravail;
    private RecyclerView rvQuiz;
    private TextView tvAucunQuiz;
    private ActivityResultLauncher<Intent> quizLauncher;
    private Cours coursCourant;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.course_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModelCours = new ViewModelProvider(requireActivity()).get(ViewModelCours.class);
        viewModelAssignment = new ViewModelProvider(requireActivity()).get(ViewModelAssignment.class);
        viewModelQuiz = new ViewModelProvider(requireActivity()).get(ViewModelQuiz.class);
        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);

        tvCodeCours = view.findViewById(R.id.tvDetailCodeCours);
        tvTitreCours = view.findViewById(R.id.tvDetailTitreCours);
        tvEnseignant = view.findViewById(R.id.tvDetailEnseignant);
        tvSession = view.findViewById(R.id.tvDetailSession);
        tvDescription = view.findViewById(R.id.tvDetailDescription);
        llAnnonces = view.findViewById(R.id.llAnnonces);
        tvAucuneAnnonce = view.findViewById(R.id.tvAucuneAnnonce);
        rvTravaux = view.findViewById(R.id.rvDetailTravaux);
        tvAucunTravail = view.findViewById(R.id.tvAucunTravail);
        rvQuiz = view.findViewById(R.id.rvDetailQuiz);
        tvAucunQuiz = view.findViewById(R.id.tvAucunQuiz);

        quizLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK) {
                        viewModelUser.rechargerUserActuel();
                    }
                }
        );

        view.findViewById(R.id.btnRetour).setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        rvTravaux.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentAdapter = new AssignmentAdapter(assignment -> {
            viewModelAssignment.setSelectedAssignment(assignment);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AssignmentDetailFragment())
                    .addToBackStack(null)
                    .commit();
        });
        rvTravaux.setAdapter(assignmentAdapter);

        rvQuiz.setLayoutManager(new LinearLayoutManager(getContext()));
        quizAdapter = new DashboardQuizAdapter(quiz -> {
            Intent intent = new Intent(requireContext(), QuizActivity.class);
            intent.putExtra("quiz", quiz);
            if (viewModelUser.getUser().getValue() != null) {
                intent.putExtra("userId", viewModelUser.getUser().getValue().getId());
            }
            quizLauncher.launch(intent);
        });
        rvQuiz.setAdapter(quizAdapter);

        viewModelCours.getSelectedCours().observe(getViewLifecycleOwner(), cours -> {
            if (cours != null) {
                coursCourant = cours;
                afficherDetailsCours(cours);
                viewModelAssignment.chargerAssignmentsParCours(cours.getId());
                viewModelQuiz.chargerQuizzesParCours(cours.getId());
            }
        });

        viewModelAssignment.getAssignmentsByCourse().observe(getViewLifecycleOwner(), assignments -> {
            if (assignments != null) {
                assignmentAdapter.setAssignmentList(assignments);
                tvAucunTravail.setVisibility(assignments.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        viewModelQuiz.getQuizzesByCourse().observe(getViewLifecycleOwner(), quizzes -> {
            if (quizzes != null) {
                if (coursCourant != null) {
                    quizAdapter.setCoursesForCodeLookup(Arrays.asList(coursCourant));
                }
                quizAdapter.setQuizList(quizzes);
                tvAucunQuiz.setVisibility(quizzes.isEmpty() ? View.VISIBLE : View.GONE);
                rvQuiz.setVisibility(quizzes.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });

        viewModelUser.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                if (quizAdapter != null) {
                    quizAdapter.setUserResults(user.getQuizResults());
                }
                if (coursCourant != null) {
                    afficherAnnonces(coursCourant, user);
                }
            }
        });
    }

    private void afficherDetailsCours(Cours cours) {
        tvCodeCours.setText(cours.getCode());
        tvTitreCours.setText(cours.getTitle());
        tvEnseignant.setText("Enseignant : " + cours.getTeacher());
        tvDescription.setText(cours.getDescription() != null ? cours.getDescription() : "Aucune description disponible.");
        afficherAnnonces(cours, viewModelUser.getUser().getValue());
    }

    private void afficherAnnonces(Cours cours, User user) {
        llAnnonces.removeAllViews();
        List<String> affichees = new ArrayList<>();

        if (user != null && user.getUserAnnonces() != null) {
            for (Annonce a : user.getUserAnnonces()) {
                if (a != null && cours.getId() != null && cours.getId().equals(a.getCourseId())) {
                    affichees.add(a.getText());
                }
            }
        }
        if (cours.getAnnonces() != null) {
            affichees.addAll(cours.getAnnonces());
        }

        if (affichees.isEmpty()) {
            tvAucuneAnnonce.setVisibility(View.VISIBLE);
        }
        else {
            tvAucuneAnnonce.setVisibility(View.GONE);
            for (String annonce : affichees) {
                TextView tvAnnonce = new TextView(getContext());
                tvAnnonce.setText("• " + annonce);
                tvAnnonce.setTextSize(14);
                tvAnnonce.setPadding(0, 4, 0, 4);
                llAnnonces.addView(tvAnnonce);
            }
        }
    }
}
