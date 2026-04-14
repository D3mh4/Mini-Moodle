package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * - Les travaux à remettre bientôt (statut "à faire")
 * - Les quiz disponibles (statut "non commencé")
 *
 * Ce fragment utilise 3 ViewModels (ViewModelUser, ViewModelCours, ViewModelAssignment)
 * pour récupérer les données de manière réactive via LiveData.
 *
 * Architecture MVVM :
 * - Vue (ce Fragment) : observe les LiveData et met à jour l'UI
 * - ViewModel : contient la logique métier et expose les données via LiveData
 * - Modèle (DAO/Entité) : gère l'accès aux données (serveur JSON)
 */
public class DashboardFragment extends Fragment {

    // ViewModels partagés avec l'Activity parente (NavActivity)
    // Utiliser requireActivity() garantit que tous les fragments
    // partagent la même instance de ViewModel
    private ViewModelUser viewModelUser;
    private ViewModelCours viewModelCours;
    private ViewModelAssignment viewModelAssignment;

    // Adaptateurs pour les 3 RecyclerViews du tableau de bord
    private CoursAdapter coursAdapter;
    private AssignmentAdapter assignmentAdapter;
    private DashboardQuizAdapter quizAdapter;

    public DashboardFragment() {
        // Constructeur vide requis par le framework Android
        // Les fragments doivent avoir un constructeur sans argument
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
        // ViewModelProvider avec requireActivity() partage le ViewModel avec NavActivity
        // Cela permet au DashboardFragment d'accéder aux données de l'utilisateur
        // chargées dans NavActivity lors de la connexion
        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        viewModelCours = new ViewModelProvider(requireActivity()).get(ViewModelCours.class);
        viewModelAssignment = new ViewModelProvider(requireActivity()).get(ViewModelAssignment.class);

        // --- Configuration du RecyclerView des cours ---
        // LinearLayoutManager dispose les items en liste verticale
        RecyclerView rvCours = view.findViewById(R.id.rvCoursInscrits);
        rvCours.setLayoutManager(new LinearLayoutManager(getContext()));

        // L'adaptateur reçoit un callback (lambda) qui sera appelé au clic sur un cours
        // -> Navigue vers l'écran de détails du cours
        coursAdapter = new CoursAdapter(cours -> {
            // Stocke le cours sélectionné dans le ViewModel pour le partager
            viewModelCours.setSelectedCours(cours);

            // Remplace le fragment actuel par CourseDetailFragment
            // addToBackStack(null) permet de revenir en arrière avec le bouton retour
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CourseDetailFragment())
                    .addToBackStack(null)
                    .commit();
        });
        rvCours.setAdapter(coursAdapter);

        // --- Configuration du RecyclerView des travaux ---
        RecyclerView rvTravaux = view.findViewById(R.id.rvTravauxProchains);
        rvTravaux.setLayoutManager(new LinearLayoutManager(getContext()));

        // Au clic sur un travail -> affiche les détails
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
            // Ouvre l'activité QuizActivity pour passer le quiz
            Intent intent = new Intent(requireContext(), QuizActivity.class);
            intent.putExtra("titre", quiz.getTitre());
            startActivity(intent);
        });
        rvQuiz.setAdapter(quizAdapter);

        // --- Observation des données via LiveData ---
        // observe() est le cœur du pattern Observer dans MVVM :
        // Quand les données changent dans le ViewModel, le callback est automatiquement appelé
        // getViewLifecycleOwner() lie l'observation au cycle de vie du Fragment
        // (évite les fuites mémoire et les mises à jour sur un Fragment détruit)

        viewModelUser.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // Quand l'utilisateur est chargé, on charge ses cours inscrits
                List<String> enrolledIds = user.getEnrolledCourseIds();
                if (enrolledIds != null && !enrolledIds.isEmpty()) {
                    viewModelCours.chargerCoursInscrits(enrolledIds);
                    viewModelAssignment.chargerTravauxProchains(enrolledIds);
                }

                // Charge les quiz disponibles depuis le serveur en arrière-plan
                // On utilise un ExecutorService car l'appel réseau ne peut pas
                // être fait sur le thread principal (Android lance une NetworkOnMainThreadException)
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        List<Quiz> allQuizzes = QuizDao.getQuizzes();
                        List<Quiz> available = new ArrayList<>();
                        if (allQuizzes != null) {
                            for (Quiz q : allQuizzes) {
                                // Filtre les quiz "non commencé" pour le tableau de bord
                                if (q.getStatut() != null
                                        && q.getStatut().equalsIgnoreCase("non commencé")) {
                                    available.add(q);
                                }
                            }
                        }
                        // post() exécute le code sur le thread principal (UI thread)
                        // Nécessaire car on ne peut modifier les vues que depuis le thread UI
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

        // Quand la liste des cours inscrits est mise à jour dans le ViewModel,
        // on met à jour l'adaptateur du RecyclerView
        viewModelCours.getEnrolledCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                coursAdapter.setCoursList(courses);
            }
        });

        // Quand la liste des travaux prochains est mise à jour
        viewModelAssignment.getAssignmentList().observe(getViewLifecycleOwner(), assignments -> {
            if (assignments != null) {
                assignmentAdapter.setAssignmentList(assignments);
            }
        });
    }
}
