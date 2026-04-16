package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelCours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.CoursAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment affichant la liste complète des cours de l'utilisateur.
 *
 * Fonctionnalités :
 * - Affiche tous les cours inscrits dans un RecyclerView
 * - Recherche en temps réel par nom ou code de cours
 * - Navigation vers les détails d'un cours au clic
 *
 * La recherche utilise un TextWatcher qui écoute chaque modification
 * du champ de texte et filtre la liste immédiatement.
 */
public class CoursesFragment extends Fragment {

    private ViewModelUser viewModelUser;
    private ViewModelCours viewModelCours;
    private CoursAdapter adapter;
    private RecyclerView rvCours;
    private EditText etRecherche;
    private TextView tvAucunCours;

    // Liste complète des cours (non filtrée)
    // Gardée en mémoire pour pouvoir filtrer sans recharger depuis le serveur
    private List<Cours> tousLesCours = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cours_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialisation des ViewModels (partagés avec l'Activity)
        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        viewModelCours = new ViewModelProvider(requireActivity()).get(ViewModelCours.class);

        // Récupération des vues depuis le layout
        rvCours = view.findViewById(R.id.rvListeCours);
        etRecherche = view.findViewById(R.id.etRechercheCours);
        tvAucunCours = view.findViewById(R.id.tvAucunCours);

        // Configuration du RecyclerView
        rvCours.setLayoutManager(new LinearLayoutManager(getContext()));

        // Callback au clic : stocke le cours sélectionné et navigue vers les détails
        adapter = new CoursAdapter(cours -> {
            viewModelCours.setSelectedCours(cours);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CourseDetailFragment())
                    .addToBackStack(null)  // Permet le retour avec le bouton back
                    .commit();
        });
        rvCours.setAdapter(adapter);

        // --- Recherche en temps réel ---
        // TextWatcher est un écouteur qui réagit à chaque changement de texte
        // Il y a 3 méthodes, mais seule afterTextChanged nous intéresse ici
        etRecherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Non utilisé mais requis par l'interface
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Non utilisé mais requis par l'interface
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Appelé après chaque modification du texte
                // On filtre la liste des cours selon le texte saisi
                filtrerCours(s.toString());
            }
        });

        // --- Observation des données ---
        // Quand l'utilisateur est chargé, on charge ses cours inscrits
        viewModelUser.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null && user.getEnrolledCourseIds() != null) {
                viewModelCours.chargerCoursInscrits(user.getEnrolledCourseIds());
            }
        });

        // Quand les cours sont chargés, on met à jour l'affichage
        viewModelCours.getEnrolledCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                tousLesCours = courses;
                adapter.setCoursList(courses);

                // Affiche le message "aucun cours" si la liste est vide
                tvAucunCours.setVisibility(courses.isEmpty() ? View.VISIBLE : View.GONE);
                rvCours.setVisibility(courses.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
    }

    /**
     * Filtre la liste des cours selon le texte de recherche.
     * Compare le texte (en minuscules) avec le code et le titre de chaque cours.
     *
     * @param query Le texte de recherche saisi par l'utilisateur
     */
    private void filtrerCours(String query) {
        // Si le champ est vide, on affiche tous les cours
        if (query.isEmpty()) {
            adapter.setCoursList(tousLesCours);
            tvAucunCours.setVisibility(tousLesCours.isEmpty() ? View.VISIBLE : View.GONE);
            rvCours.setVisibility(tousLesCours.isEmpty() ? View.GONE : View.VISIBLE);
            return;
        }

        // Filtre les cours dont le code OU le titre contient le texte recherché
        // toLowerCase() rend la recherche insensible à la casse
        List<Cours> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Cours c : tousLesCours) {
            boolean matchCode = c.getCodeCours() != null
                    && c.getCodeCours().toLowerCase().contains(lowerQuery);
            boolean matchTitre = c.getTitre() != null
                    && c.getTitre().toLowerCase().contains(lowerQuery);

            if (matchCode || matchTitre) {
                filtered.add(c);
            }
        }

        adapter.setCoursList(filtered);
        tvAucunCours.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        rvCours.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
