package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.AssignmentAdapter;

import java.util.List;

/**
 * Fragment affichant les détails d'un cours sélectionné.
 */
public class CourseDetailFragment extends Fragment {

    private ViewModelCours viewModelCours;
    private ViewModelAssignment viewModelAssignment;
    private AssignmentAdapter assignmentAdapter;

    // Vues de l'en-tête
    private TextView tvCodeCours, tvTitreCours, tvEnseignant, tvSession;

    // Vue de la description
    private TextView tvDescription;

    // Container pour les annonces
    private LinearLayout llAnnonces;
    private TextView tvAucuneAnnonce;

    // RecyclerView pour les travaux du cours
    private RecyclerView rvTravaux;
    private TextView tvAucunTravail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.course_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Initialisation des ViewModels ---
        viewModelCours = new ViewModelProvider(requireActivity()).get(ViewModelCours.class);
        viewModelAssignment = new ViewModelProvider(requireActivity()).get(ViewModelAssignment.class);

        // --- Récupération de toutes les vues ---
        tvCodeCours = view.findViewById(R.id.tvDetailCodeCours);
        tvTitreCours = view.findViewById(R.id.tvDetailTitreCours);
        tvEnseignant = view.findViewById(R.id.tvDetailEnseignant);
        tvSession = view.findViewById(R.id.tvDetailSession);
        tvDescription = view.findViewById(R.id.tvDetailDescription);
        llAnnonces = view.findViewById(R.id.llAnnonces);
        tvAucuneAnnonce = view.findViewById(R.id.tvAucuneAnnonce);
        rvTravaux = view.findViewById(R.id.rvDetailTravaux);
        tvAucunTravail = view.findViewById(R.id.tvAucunTravail);

        // --- Bouton retour ---
        view.findViewById(R.id.btnRetour).setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        // --- Configuration du RecyclerView des travaux ---
        rvTravaux.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentAdapter = new AssignmentAdapter(assignment -> {
            viewModelAssignment.setSelectedAssignment(assignment);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AssignmentDetailFragment())
                    .addToBackStack(null)
                    .commit();
        });
        rvTravaux.setAdapter(assignmentAdapter);

        // --- Observation du cours sélectionné ---
        viewModelCours.getSelectedCours().observe(getViewLifecycleOwner(), cours -> {
            if (cours != null) {
                afficherDetailsCours(cours);
                viewModelAssignment.chargerAssignmentsParCours(cours.getId());
            }
        });

        // --- Observation des travaux du cours ---
        viewModelAssignment.getAssignmentsByCourse().observe(getViewLifecycleOwner(), assignments -> {
            if (assignments != null) {
                assignmentAdapter.setAssignmentList(assignments);
                tvAucunTravail.setVisibility(assignments.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Remplit toutes les vues avec les données du cours sélectionné.
     *
     * @param cours L'objet Cours contenant les données à afficher
     */
    private void afficherDetailsCours(Cours cours) {
        tvCodeCours.setText(cours.getCodeCours());
        tvTitreCours.setText(cours.getTitre());
        tvEnseignant.setText("Enseignant : " + (cours.getEnseignant() != null ? cours.getEnseignant() : "---"));

        // Formatage de la session (ex: "Hiver 2024" -> "H24")
        String rawSession = cours.getSession();
        if (rawSession != null && !rawSession.isEmpty()) {
            String formatted = rawSession.substring(0, 1).toUpperCase();
            if (rawSession.length() >= 2) {
                formatted += rawSession.substring(rawSession.length() - 2);
            }
            tvSession.setText("Session : " + formatted);
        } else {
            tvSession.setText("Session : ---");
        }

        tvDescription.setText(cours.getDescription() != null ? cours.getDescription() : "Aucune description disponible.");

        // --- Annonces ---
        llAnnonces.removeAllViews();
        List<String> annonces = cours.getAnnonces();
        if (annonces != null && !annonces.isEmpty()) {
            tvAucuneAnnonce.setVisibility(View.GONE);
            for (String annonce : annonces) {
                TextView tvAnnonce = new TextView(getContext());
                tvAnnonce.setText("• " + annonce);
                tvAnnonce.setTextSize(14);
                tvAnnonce.setPadding(0, 4, 0, 4);
                llAnnonces.addView(tvAnnonce);
            }
        } else {
            tvAucuneAnnonce.setVisibility(View.VISIBLE);
        }
    }
}
