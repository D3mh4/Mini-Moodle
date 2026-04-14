package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelAssignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;
import com.google.android.material.card.MaterialCardView;

/**
 * Fragment affichant les détails complets d'un travail (assignment).
 *
 * Fonctionnalités :
 * - Affichage du titre, statut, date limite, points
 * - Affichage de la description et des consignes
 * - Section note/commentaire (visible uniquement si le travail est corrigé)
 * - Soumission simulée : l'étudiant peut saisir un URL ou texte
 *   et cliquer "Marquer comme remis" pour changer le statut
 *
 * La soumission est simulée localement (le statut change dans le ViewModel)
 * conformément aux exigences de l'énoncé du projet.
 */
public class AssignmentDetailFragment extends Fragment {

    private ViewModelAssignment viewModelAssignment;

    // Vues de l'en-tête
    private TextView tvTitre, tvStatut, tvDateLimite, tvPoints;

    // Vues de description/consignes
    private TextView tvDescription, tvInstructions;

    // Vues de la note (visibles seulement si corrigé)
    private MaterialCardView cardNote;
    private TextView tvNote, tvCommentaire;

    // Vues de la soumission
    private MaterialCardView cardSoumission;
    private EditText etSoumissionUrl;
    private Button btnMarquerRemis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.assignment_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialisation du ViewModel
        viewModelAssignment = new ViewModelProvider(requireActivity()).get(ViewModelAssignment.class);

        // --- Récupération de toutes les vues ---
        tvTitre = view.findViewById(R.id.tvDetailTitreDevoir);
        tvStatut = view.findViewById(R.id.tvDetailStatutDevoir);
        tvDateLimite = view.findViewById(R.id.tvDetailDateLimite);
        tvPoints = view.findViewById(R.id.tvDetailPointsDevoir);
        tvDescription = view.findViewById(R.id.tvDetailDescDevoir);
        tvInstructions = view.findViewById(R.id.tvDetailInstructions);
        cardNote = view.findViewById(R.id.cardNote);
        tvNote = view.findViewById(R.id.tvDetailNote);
        tvCommentaire = view.findViewById(R.id.tvDetailCommentaire);
        cardSoumission = view.findViewById(R.id.cardSoumission);
        etSoumissionUrl = view.findViewById(R.id.etSoumissionUrl);
        btnMarquerRemis = view.findViewById(R.id.btnMarquerRemis);

        // --- Bouton retour ---
        // popBackStack() retire ce fragment de la pile et revient au précédent
        view.findViewById(R.id.btnRetourDevoir).setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        // --- Bouton de soumission simulée ---
        btnMarquerRemis.setOnClickListener(v -> {
            // Récupère le travail actuellement sélectionné depuis le ViewModel
            Assignment current = viewModelAssignment.getSelectedAssignment().getValue();
            if (current != null) {
                // Appelle la méthode de simulation dans le ViewModel
                // Cela change le statut à "Remis" et notifie les observateurs
                viewModelAssignment.simulerSoumission(current);
                Toast.makeText(getContext(), "Travail marqué comme remis !", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Observation du travail sélectionné ---
        // Quand les données changent (ex: après soumission), l'UI se met à jour
        viewModelAssignment.getSelectedAssignment().observe(getViewLifecycleOwner(), assignment -> {
            if (assignment != null) {
                afficherDetails(assignment);
            }
        });
    }

    /**
     * Remplit toutes les vues avec les données du travail.
     * Gère aussi la visibilité conditionnelle des sections :
     * - Section note : visible seulement si statut = "corrigé"
     * - Section soumission : visible seulement si statut = "à faire"
     *
     * @param assignment L'objet Assignment contenant les données
     */
    private void afficherDetails(Assignment assignment) {
        // --- En-tête ---
        tvTitre.setText(assignment.getTitle());
        tvDateLimite.setText("Date limite : " + (assignment.getDueDate() != null ? assignment.getDueDate() : "---"));

        // Affichage du statut avec couleur
        String statut = assignment.getStatus();
        tvStatut.setText("Statut : " + (statut != null ? statut : "---"));

        // Couleur conditionnelle du statut (même logique que dans l'adaptateur)
        if (statut != null) {
            int color;
            switch (statut.toLowerCase()) {
                case "à faire":
                    color = getResources().getColor(R.color.orange);
                    break;
                case "remis":
                    color = 0xFF4CAF50; // Vert
                    break;
                case "en retard":
                    color = 0xFFF44336; // Rouge
                    break;
                case "corrigé":
                    color = 0xFF2196F3; // Bleu
                    break;
                default:
                    color = getResources().getColor(R.color.gray);
                    break;
            }
            tvStatut.setTextColor(color);
        }

        // Points totaux
        tvPoints.setText("Points : " + assignment.getTotalPoints());

        // --- Description et consignes ---
        tvDescription.setText(assignment.getDescription() != null ? assignment.getDescription() : "Aucune description.");
        tvInstructions.setText(assignment.getInstructions() != null ? assignment.getInstructions() : "Aucune consigne.");

        // --- Section Note (conditionnelle) ---
        // La carte de note n'est visible que si le travail a été corrigé
        if (statut != null && statut.equalsIgnoreCase("corrigé")) {
            cardNote.setVisibility(View.VISIBLE);

            // Affiche la note (grade peut être null si pas encore noté)
            Integer grade = assignment.getGrade();
            if (grade != null) {
                tvNote.setText("Note : " + grade + " / " + assignment.getTotalPoints());
            } else {
                tvNote.setText("Note : En attente");
            }

            // Affiche le commentaire du correcteur
            tvCommentaire.setText(assignment.getComment() != null ? assignment.getComment() : "Aucun commentaire.");
        } else {
            cardNote.setVisibility(View.GONE);
        }

        // --- Section Soumission (conditionnelle) ---
        // Le formulaire de soumission n'est visible que si le travail est "à faire"
        if (statut != null && statut.equalsIgnoreCase("à faire")) {
            cardSoumission.setVisibility(View.VISIBLE);
        } else {
            cardSoumission.setVisibility(View.GONE);
        }
    }
}
