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
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;
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

public class DashboardFragment extends Fragment {

    private ViewModelUser viewModelUser;
    private ViewModelCours viewModelCours;
    private ViewModelAssignment viewModelAssignment;

    private CoursAdapter coursAdapter;
    private AssignmentAdapter assignmentAdapter;
    private DashboardQuizAdapter quizAdapter;

    private TextView tvCountAFaire, tvCountRemis, tvCountRetard, tvCountCorrige;

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
        quizAdapter = new DashboardQuizAdapter(quiz -> naviguerVersOngletQuiz());
        rvQuiz.setAdapter(quizAdapter);

        viewModelUser.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;

            List<String> enrolledIds = user.getEnrolledCourseIds();
            if (enrolledIds != null && !enrolledIds.isEmpty()) {
                viewModelCours.chargerCoursInscrits(enrolledIds);
                // ViewModelAssignment filtrera tout seul via currentUser.enrolledCourseIds
                viewModelAssignment.setCurrentUser(user);
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

            // Mettre à jour les scores déjà complétés
            if (quizAdapter != null) {
                quizAdapter.setUserResults(user.getQuizResults());
            }
        });

        viewModelCours.getEnrolledCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                coursAdapter.setCoursList(courses);
                // Alimenter le lookup courseId → code pour l'adaptateur quiz
                if (quizAdapter != null) {
                    quizAdapter.setCoursesForCodeLookup(courses);
                }
                // Ré-appliquer le filtre quiz maintenant qu'on a les cours
                rafraichirQuizzesDisponibles();
            }
        });

        viewModelAssignment.getAssignmentList().observe(getViewLifecycleOwner(), assignments -> {
            if (assignments != null) {
                mettreAJourCompteurs(assignments);

                List<Assignment> upcoming = new ArrayList<>();
                for (Assignment a : assignments) {
                    if (a.getStatus() != null
                            && (a.getStatus().equalsIgnoreCase("à faire")
                                || a.getStatus().equalsIgnoreCase("non soumis"))) {
                        upcoming.add(a);
                    }
                }
                android.util.Log.d("DASH", "assignments received: " + assignments.size() + ", upcoming after filter: " + upcoming.size());
                for (Assignment a : upcoming) {
                    android.util.Log.d("DASH", "  upcoming: " + a.getTitle() + " [" + a.getStatus() + "]");
                }
                assignmentAdapter.setAssignmentList(upcoming);
                View rv = getView() != null ? getView().findViewById(R.id.rvTravauxProchains) : null;
                if (rv != null) {
                    float density = getResources().getDisplayMetrics().density;
                    int heightPx = (int) (100 * density * upcoming.size());
                    ViewGroup.LayoutParams lp = rv.getLayoutParams();
                    lp.height = heightPx;
                    rv.setLayoutParams(lp);
                }
            }
        });
    }

    private void naviguerVersOngletQuiz() {
        if (getActivity() == null) return;
        BottomNavigationView nav = requireActivity().findViewById(R.id.bottomNavigationView);
        if (nav != null) {
            nav.setSelectedItemId(R.id.quiz);
        }
    }

    /**
     * Retire de la liste :
     *  - les quiz des cours non inscrits
     *  - les quiz déjà complétés par l'utilisateur
     */
    private void rafraichirQuizzesDisponibles() {
        if (quizAdapter == null) return;

        Set<String> doneIds = new HashSet<>();
        List<String> enrolledIds = null;

        if (viewModelUser.getUser().getValue() != null) {
            if (viewModelUser.getUser().getValue().getQuizResults() != null) {
                for (ResultatQuiz r : viewModelUser.getUser().getValue().getQuizResults()) {
                    if (r.getQuizId() != null) {
                        doneIds.add(r.getQuizId());
                    }
                }
            }
            enrolledIds = viewModelUser.getUser().getValue().getEnrolledCourseIds();
        }

        List<Quiz> disponibles = new ArrayList<>();
        for (Quiz q : cacheTousLesQuizzes) {
            if (q.getId() == null) continue;
            if (doneIds.contains(q.getId())) continue;
            if (enrolledIds != null && !enrolledIds.contains(q.getCourseId())) continue;
            disponibles.add(q);
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
                    case "non soumis":
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
