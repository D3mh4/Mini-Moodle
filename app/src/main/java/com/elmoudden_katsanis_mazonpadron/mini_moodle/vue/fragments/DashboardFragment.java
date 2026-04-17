package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelAssignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelCours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao.QuizDao;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.ResultatQuiz;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.AssignmentAdapter;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.CoursAdapter;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.DashboardQuizAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fragment du Tableau de Bord (Dashboard).
 *
 * Les quiz disponibles montrent ceux que l'utilisateur n'a PAS encore complétés
 * (basé sur quizResults). Cliquer un quiz ici amène l'utilisateur à l'onglet
 * Quiz où il pourra le démarrer (pas de lancement direct depuis le dashboard).
 */
public class DashboardFragment extends Fragment {

    private ViewModelUser viewModelUser;
    private ViewModelCours viewModelCours;
    private ViewModelAssignment viewModelAssignment;

    private CoursAdapter coursAdapter;
    private AssignmentAdapter assignmentAdapter;
    private DashboardQuizAdapter quizAdapter;

    private TextView tvCountAFaire, tvCountRemis, tvCountRetard, tvCountCorrige;

    // Cache de tous les quiz, pour re-filtrer localement quand quizResults change
    private List<Quiz> cacheTousLesQuizzes = new ArrayList<>();

    public DashboardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        viewModelCours = new ViewModelProvider(requireActivity()).get(ViewModelCours.class);
        viewModelAssignment = new ViewModelProvider(requireActivity()).get(ViewModelAssignment.class);

        tvCountAFaire = view.findViewById(R.id.tvCountAFaire);
        tvCountRemis = view.findViewById(R.id.tvCountRemis);
        tvCountRetard = view.findViewById(R.id.tvCountRetard);
        tvCountCorrige = view.findViewById(R.id.tvCountCorrige);

        RecyclerView rvCours = view.findViewById(R.id.rvCoursInscrits);
        rvCours.setLayoutManager(new LinearLayoutManager(getContext()));
        coursAdapter = new CoursAdapter(cours -> {
            viewModelCours.setSelectedCours(cours);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CourseDetailFragment())
                    .addToBackStack(null)
                    .commit();
        });
        rvCours.setAdapter(coursAdapter);

        RecyclerView rvTravaux = view.findViewById(R.id.rvTravauxProchains);
        rvTravaux.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentAdapter = new AssignmentAdapter(assignment -> {
            viewModelAssignment.setSelectedAssignment(assignment);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AssignmentDetailFragment())
                    .addToBackStack(null)
                    .commit();
        });
        rvTravaux.setAdapter(assignmentAdapter);

        RecyclerView rvQuiz = view.findViewById(R.id.rvQuizDisponibles);
        rvQuiz.setLayoutManager(new LinearLayoutManager(getContext()));
        // Au clic sur un quiz du dashboard : on ne lance plus le quiz directement,
        // on amène l'utilisateur à l'onglet Quiz via la BottomNavigationView.
        quizAdapter = new DashboardQuizAdapter(quiz -> naviguerVersOngletQuiz());
        rvQuiz.setAdapter(quizAdapter);

        viewModelUser.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;

            List<String> enrolledIds = user.getEnrolledCourseIds();
            if (enrolledIds != null && !enrolledIds.isEmpty()) {
                viewModelCours.chargerCoursInscrits(enrolledIds);
                viewModelAssignment.chargerTousLesAssignments();
            }

            if (cacheTousLesQuizzes.isEmpty()) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        List<Quiz> all = QuizDao.getQuizzes();
                        cacheTousLesQuizzes = all != null ? all : new ArrayList<>();
                        if (isAdded()) {
                            requireActivity().runOnUiThread(this::rafraichirQuizzesDisponibles);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                rafraichirQuizzesDisponibles();
            }
        });

        viewModelCours.getEnrolledCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                coursAdapter.setCoursList(courses);
            }
        });

        viewModelAssignment.getAssignmentList().observe(getViewLifecycleOwner(), assignments -> {
            if (assignments != null) {
                mettreAJourCompteurs(assignments);

                List<Assignment> upcoming = new ArrayList<>();
                List<String> enrolledIds = null;
                if (viewModelUser.getUser().getValue() != null) {
                    enrolledIds = viewModelUser.getUser().getValue().getEnrolledCourseIds();
                }

                if (enrolledIds != null) {
                    for (Assignment a : assignments) {
                        if (enrolledIds.contains(a.getCourseId())
                                && a.getStatus() != null
                                && a.getStatus().equalsIgnoreCase("à faire")) {
                            upcoming.add(a);
                        }
                    }
                }
                assignmentAdapter.setAssignmentList(upcoming);
            }
        });
    }

    /**
     * Bascule l'onglet actif de la BottomNavigationView vers "Quiz".
     * setSelectedItemId déclenche le listener de NavActivity qui fait
     * déjà le replaceFragment vers QuizFragment.
     */
    private void naviguerVersOngletQuiz() {
        if (getActivity() == null) return;
        BottomNavigationView nav = requireActivity().findViewById(R.id.bottomNavigationView);
        if (nav != null) {
            nav.setSelectedItemId(R.id.quiz);
        }
    }

    /**
     * Retire de la liste les quiz déjà complétés par l'utilisateur.
     */
    private void rafraichirQuizzesDisponibles() {
        if (quizAdapter == null) return;

        Set<String> doneIds = new HashSet<>();
        if (viewModelUser.getUser().getValue() != null
                && viewModelUser.getUser().getValue().getQuizResults() != null) {
            for (ResultatQuiz r : viewModelUser.getUser().getValue().getQuizResults()) {
                if (r.getQuizId() != null) {
                    doneIds.add(r.getQuizId());
                }
            }
        }

        List<Quiz> disponibles = new ArrayList<>();
        for (Quiz q : cacheTousLesQuizzes) {
            if (q.getId() != null && !doneIds.contains(q.getId())) {
                disponibles.add(q);
            }
        }

        quizAdapter.setQuizList(disponibles);
    }

    private void mettreAJourCompteurs(List<Assignment> assignments) {
        int aFaire = 0, remis = 0, retard = 0, corrige = 0;

        for (Assignment a : assignments) {
            String statut = a.getStatus();
            if (statut != null) {
                switch (statut.toLowerCase()) {
                    case "à faire":
                        aFaire++;
                        break;
                    case "remis":
                        remis++;
                        break;
                    case "en retard":
                        retard++;
                        break;
                    case "corrigé":
                        corrige++;
                        break;
                }
            }
        }

        if (tvCountAFaire != null) tvCountAFaire.setText(String.valueOf(aFaire));
        if (tvCountRemis != null) tvCountRemis.setText(String.valueOf(remis));
        if (tvCountRetard != null) tvCountRetard.setText(String.valueOf(retard));
        if (tvCountCorrige != null) tvCountCorrige.setText(String.valueOf(corrige));
    }
}
