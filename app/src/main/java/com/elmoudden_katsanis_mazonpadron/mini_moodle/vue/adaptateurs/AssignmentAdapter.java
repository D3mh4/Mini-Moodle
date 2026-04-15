package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;

import java.util.ArrayList;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private List<Assignment> assignmentList = new ArrayList<>();
    private OnAssignmentClickListener listener;

    public interface OnAssignmentClickListener {
        void onAssignmentClick(Assignment assignment);
    }

    public AssignmentAdapter(OnAssignmentClickListener listener) {
        this.listener = listener;
    }

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

        holder.tvTitreDevoir.setText(assignment.getTitle());
        holder.tvDateLimite.setText("Expire le: " + assignment.getDueDate());

        String statut = assignment.getStatus();
        holder.tvStatut.setText(statut != null ? statut : "---");

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

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitreDevoir;
        TextView tvDateLimite;
        TextView tvStatut;

        AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitreDevoir = itemView.findViewById(R.id.tvTitreDevoir);
            tvDateLimite = itemView.findViewById(R.id.tvDateLimite);
            tvStatut = itemView.findViewById(R.id.tvStatutDevoir);
        }
    }
}
