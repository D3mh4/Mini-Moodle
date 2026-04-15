package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.QuizActivity;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.AssignmentAdapter;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.CoursAdapter;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.DashboardQuizAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fragment du Tableau de Bord (Dashboard).
 *
 * C'est l'écran principal après la connexion. Il affiche un résumé des
 * informations importantes de l'étudiant :
 * - Les cours auxquels il est inscrit
 * - Résumé des travaux par statut (À faire, Remis, En retard, Corrigé)
 * - Les travaux à remettre bientôt (statut "à faire")
 * - Les quiz disponibles (statut "non commencé")
 *
 * Ce fragment utilise 3 ViewModels (ViewModelUser, ViewModelCours, ViewModelAssignment)
 * pour récupérer les données de manière réactive via LiveData.
 */
public class DashboardFragment extends Fragment {

    // ViewModels partagés avec l'Activity parente (NavActivity)
    private ViewModelUser viewModelUser;
    private ViewModelCours viewModelCours;
    private ViewModelAssignment viewModelAssignment;

    // Adaptateurs pour les 3 RecyclerViews du tableau de bord
    private CoursAdapter coursAdapter;
    private AssignmentAdapter assignmentAdapter;
    private DashboardQuizAdapter quizAdapter;

    // Compteurs de statut pour le résumé des travaux
    private TextView tvCountAFaire, tvCountRemis, tvCountRetard, tvCountCorrige;

    public DashboardFragment() {
        // Constructeur vide requis par le framework Android
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate() convertit le XML du layout en objets View Java
        return inflater.inflate(R.layout.dashboard_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Initialisation des ViewModels ---
        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        viewModelCours = new ViewModelProvider(requireActivity()).get(ViewModelCours.class);
        viewModelAssignment = new ViewModelProvider(requireActivity()).get(ViewModelAssignment.class);

        // --- Récupération des vues du résumé ---
        tvCountAFaire = view.findViewById(R.id.tvCountAFaire);
        tvCountRemis = view.findViewById(R.id.tvCountRemis);
        tvCountRetard = view.findViewById(R.id.tvCountRetard);
        tvCountCorrige = view.findViewById(R.id.tvCountCorrige);

        // --- Configuration du RecyclerView des cours ---
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

        // --- Configuration du RecyclerView des travaux ---
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

        // --- Configuration du RecyclerView des quiz ---
        RecyclerView rvQuiz = view.findViewById(R.id.rvQuizDisponibles);
        rvQuiz.setLayoutManager(new LinearLayoutManager(getContext()));
        quizAdapter = new DashboardQuizAdapter(quiz -> {
            Intent intent = new Intent(requireContext(), QuizActivity.class);
            intent.putExtra("titre", quiz.getTitre());
            startActivity(intent);
        });
        rvQuiz.setAdapter(quizAdapter);

        // --- Observation des données via LiveData ---

        viewModelUser.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                List<String> enrolledIds = user.getEnrolledCourseIds();
                if (enrolledIds != null && !enrolledIds.isEmpty()) {
                    viewModelCours.chargerCoursInscrits(enrolledIds);
                    // On charge tous les travaux pour calculer le résumé (comme sur la page Travaux)
                    viewModelAssignment.chargerTousLesAssignments();
                }

                // Charge les quiz disponibles
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        List<Quiz> allQuizzes = QuizDao.getQuizzes();
                        List<Quiz> available = new ArrayList<>();
                        if (allQuizzes != null) {
                            for (Quiz q : allQuizzes) {
                                if (q.getStatut() != null
                                        && q.getStatut().equalsIgnoreCase("non commencé")) {
                                    available.add(q);
                                }
                            }
                        }
                        List<Quiz> finalAvailable = available;
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                quizAdapter.setQuizList(finalAvailable);
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        viewModelCours.getEnrolledCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                coursAdapter.setCoursList(courses);
            }
        });

        // Observation de la liste des travaux
        viewModelAssignment.getAssignmentList().observe(getViewLifecycleOwner(), assignments -> {
            if (assignments != null) {
                // 1. Met à jour les compteurs du résumé (tous les travaux)
                mettreAJourCompteurs(assignments);

                // 2. Filtre pour la liste "Travaux à remettre bientôt" 
                // (uniquement statut "à faire" et cours inscrits)
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
     * Calcule et affiche le nombre de travaux par statut.
     * Copie de la logique présente dans HomeworksFragments.
     *
     * @param assignments La liste complète des travaux
     */
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
