package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptateur RecyclerView pour afficher la liste des travaux (assignments).
 * Affiche le titre, la date limite et le statut coloré de chaque travail.
 */
public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private List<Assignment> assignmentList = new ArrayList<>();
    private OnAssignmentClickListener listener;

    /**
     * Interface de callback pour le clic sur un travail.
     */
    public interface OnAssignmentClickListener {
        void onAssignmentClick(Assignment assignment);
    }

    public AssignmentAdapter(OnAssignmentClickListener listener) {
        this.listener = listener;
    }

    /**
     * Met à jour la liste des travaux et notifie le RecyclerView.
     */
    public void setAssignmentList(List<Assignment> newList) {
        this.assignmentList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.liste_devoirs, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);

        // Affiche le titre du travail
        holder.tvTitreDevoir.setText(assignment.getTitle());

        // Affiche la date limite formatée
        holder.tvDateLimite.setText("Expire dans : " + assignment.getDueDate());

        // Affiche le statut avec un code couleur pour une identification rapide
        String statut = assignment.getStatus();
        holder.tvStatut.setText(statut);

        // Couleur conditionnelle selon le statut du travail
        // Aide l'utilisateur à identifier rapidement l'état de chaque travail
        if (statut != null) {
            switch (statut.toLowerCase()) {
                case "à faire":
                    // Orange pour les travaux à faire (action requise)
                    holder.tvStatut.setTextColor(
                            ContextCompat.getColor(holder.itemView.getContext(), R.color.orange));
                    break;
                case "remis":
                    // Vert pour les travaux remis (complétés)
                    holder.tvStatut.setTextColor(0xFF4CAF50);
                    break;
                case "en retard":
                    // Rouge pour les travaux en retard (urgents)
                    holder.tvStatut.setTextColor(0xFFF44336);
                    break;
                case "corrigé":
                    // Bleu pour les travaux corrigés
                    holder.tvStatut.setTextColor(0xFF2196F3);
                    break;
                default:
                    // Gris par défaut
                    holder.tvStatut.setTextColor(
                            ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
                    break;
            }
        }

        // Navigation vers les détails du travail au clic
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAssignmentClick(assignment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    /**
     * ViewHolder pour un item de travail.
     */
    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitreDevoir;   // Titre du travail
        TextView tvDateLimite;    // Date limite
        TextView tvStatut;        // Statut (à faire, remis, etc.)
        ImageView ivArrow;        // Flèche de navigation

        AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitreDevoir = itemView.findViewById(R.id.tvTitreDevoir);
            tvDateLimite = itemView.findViewById(R.id.tvDateLimite);
            tvStatut = itemView.findViewById(R.id.tvStatutDevoir);
            ivArrow = itemView.findViewById(R.id.ivArrowDevoir);
        }
    }
}
