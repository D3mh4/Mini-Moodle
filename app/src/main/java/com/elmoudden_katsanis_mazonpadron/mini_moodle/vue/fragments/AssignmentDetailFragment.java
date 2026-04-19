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
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;
import com.google.android.material.card.MaterialCardView;

public class AssignmentDetailFragment extends Fragment {

    private ViewModelAssignment viewModelAssignment;

    private TextView tvTitre, tvStatut, tvDateLimite, tvPoints;
    private TextView tvDescription, tvInstructions;

    private MaterialCardView cardNote;
    private TextView tvNote, tvCommentaire;

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

        viewModelAssignment = new ViewModelProvider(requireActivity()).get(ViewModelAssignment.class);

        ViewModelUser viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        if (viewModelUser.getUser().getValue() != null) {
            viewModelAssignment.setCurrentUser(viewModelUser.getUser().getValue());
        }

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

        view.findViewById(R.id.btnRetourDevoir).setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        btnMarquerRemis.setOnClickListener(v -> {
            Assignment current = viewModelAssignment.getSelectedAssignment().getValue();
            if (current != null) {
                viewModelAssignment.simulerSoumission(current);
                Toast.makeText(getContext(), "Travail marqué comme remis !", Toast.LENGTH_SHORT).show();
            }
        });

        viewModelAssignment.getSelectedAssignment().observe(getViewLifecycleOwner(), assignment -> {
            if (assignment != null) {
                afficherDetails(assignment);
            }
        });
    }

    private void afficherDetails(Assignment assignment) {
        tvTitre.setText(assignment.getTitle());
        tvDateLimite.setText("Date limite : " + (assignment.getDueDate() != null ? assignment.getDueDate() : "---"));

        String statut = assignment.getStatus();
        tvStatut.setText("Statut : " + (statut != null ? statut : "---"));

        if (statut != null) {
            int color;
            switch (statut.toLowerCase()) {
                case "à faire":
                case "non soumis":
                    color = getResources().getColor(R.color.orange);
                    break;
                case "remis":
                    color = 0xFF4CAF50;
                    break;
                case "en retard":
                    color = 0xFFF44336;
                    break;
                case "corrigé":
                    color = 0xFF2196F3;
                    break;
                default:
                    color = getResources().getColor(R.color.gray);
                    break;
            }
            tvStatut.setTextColor(color);
        }

        tvPoints.setText("Points : " + assignment.getTotalPoints());

        tvDescription.setText(assignment.getDescription() != null ? assignment.getDescription() : "Aucune description.");
        tvInstructions.setText(assignment.getInstructions() != null ? assignment.getInstructions() : "Aucune consigne.");

        if (statut != null && statut.equalsIgnoreCase("corrigé")) {
            cardNote.setVisibility(View.VISIBLE);

            Integer grade = assignment.getGrade();
            if (grade != null) {
                tvNote.setText("Note : " + grade + " / " + assignment.getTotalPoints());
            } else {
                tvNote.setText("Note : En attente");
            }

            tvCommentaire.setText(assignment.getComment() != null ? assignment.getComment() : "Aucun commentaire.");
        } else {
            cardNote.setVisibility(View.GONE);
        }

        // Soumission visible si le travail est encore à faire/non soumis
        if (statut != null && (statut.equalsIgnoreCase("à faire") || statut.equalsIgnoreCase("non soumis"))) {
            cardSoumission.setVisibility(View.VISIBLE);
        } else {
            cardSoumission.setVisibility(View.GONE);
        }
    }
}
