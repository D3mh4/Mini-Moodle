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
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.AssignmentAdapter;

import java.util.List;

/**
 * Fragment affichant tous les travaux de l'utilisateur.
 *
 * Fonctionnalités :
 * - Résumé en haut avec compteurs par statut (à faire, remis, en retard, corrigé)
 * - Liste complète de tous les travaux dans un RecyclerView
 * - Navigation vers les détails d'un travail au clic
 *
 * Les compteurs de statut permettent à l'étudiant d'avoir une vue
 * d'ensemble rapide de son avancement.
 */
public class HomeworksFragments extends Fragment {

    private ViewModelUser viewModelUser;
    private ViewModelAssignment viewModelAssignment;
    private AssignmentAdapter adapter;

    // Compteurs de statut affichés en haut de l'écran
    private TextView tvCountAFaire, tvCountRemis, tvCountRetard, tvCountCorrige;
    private TextView tvAucunDevoir;
    private RecyclerView rvTravaux;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.devoirs_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialisation des ViewModels
        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        viewModelAssignment = new ViewModelProvider(requireActivity()).get(ViewModelAssignment.class);

        // Récupération des vues
        tvCountAFaire = view.findViewById(R.id.tvCountAFaire);
        tvCountRemis = view.findViewById(R.id.tvCountRemis);
        tvCountRetard = view.findViewById(R.id.tvCountRetard);
        tvCountCorrige = view.findViewById(R.id.tvCountCorrige);
        tvAucunDevoir = view.findViewById(R.id.tvAucunDevoir);
        rvTravaux = view.findViewById(R.id.rvTousLesTravaux);

        // Configuration du RecyclerView
        rvTravaux.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AssignmentAdapter(assignment -> {
            // Au clic : stocke le travail sélectionné et navigue vers les détails
            viewModelAssignment.setSelectedAssignment(assignment);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AssignmentDetailFragment())
                    .addToBackStack(null)
                    .commit();
        });
        rvTravaux.setAdapter(adapter);

        // Charge tous les travaux quand le fragment est affiché
        viewModelAssignment.chargerTousLesAssignments();

        // --- Observation des travaux ---
        viewModelAssignment.getAssignmentList().observe(getViewLifecycleOwner(), assignments -> {
            if (assignments != null) {
                adapter.setAssignmentList(assignments);

                // Met à jour les compteurs de statut
                mettreAJourCompteurs(assignments);

                // Affiche/masque le message "aucun devoir"
                tvAucunDevoir.setVisibility(assignments.isEmpty() ? View.VISIBLE : View.GONE);
                rvTravaux.setVisibility(assignments.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
    }

    /**
     * Calcule et affiche le nombre de travaux par statut.
     * Parcourt la liste une seule fois et incrémente les compteurs appropriés.
     *
     * @param assignments La liste complète des travaux
     */
    private void mettreAJourCompteurs(List<Assignment> assignments) {
        int aFaire = 0, remis = 0, retard = 0, corrige = 0;

        for (Assignment a : assignments) {
            String statut = a.getStatus();
            if (statut != null) {
                // switch sur le statut en minuscules pour être insensible à la casse
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

        // String.valueOf() convertit un int en String pour l'affichage
        tvCountAFaire.setText(String.valueOf(aFaire));
        tvCountRemis.setText(String.valueOf(remis));
        tvCountRetard.setText(String.valueOf(retard));
        tvCountCorrige.setText(String.valueOf(corrige));
    }
}
