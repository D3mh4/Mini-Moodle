package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;

import java.util.ArrayList;
import java.util.List;

public class CoursAdapter extends RecyclerView.Adapter<CoursAdapter.CoursViewHolder> {

    private List<Cours> coursList = new ArrayList<>();
    private OnCoursClickListener listener;

    public interface OnCoursClickListener {
        void onCoursClick(Cours cours);
    }

    public CoursAdapter(OnCoursClickListener listener) {
        this.listener = listener;
    }

    public void setCoursList(List<Cours> newList) {
        this.coursList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CoursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.liste_cours, parent, false);
        return new CoursViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursViewHolder holder, int position) {
        Cours cours = coursList.get(position);
        
        holder.tvTitreCours.setText(cours.getTitre());
        
        // Formate la session : "Hiver 2024" -> "H24"
        String rawSession = cours.getSession();
        if (rawSession != null && !rawSession.isEmpty()) {
            String firstLetter = rawSession.substring(0, 1).toUpperCase();
            String lastTwo = "";
            if (rawSession.length() >= 2) {
                lastTwo = rawSession.substring(rawSession.length() - 2);
            }
            holder.tvSession.setText(firstLetter + lastTwo);
        } else {
            holder.tvSession.setText("");
        }

        // Affiche l'enseignant et le code du cours sur la troisième ligne
        String details = (cours.getEnseignant() != null ? cours.getEnseignant() : "Enseignant inconnu")
                + (cours.getCodeCours() != null ? " - " + cours.getCodeCours() : "");
        holder.tvEnseignantEtCode.setText(details);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCoursClick(cours);
            }
        });
    }

    @Override
    public int getItemCount() {
        return coursList.size();
    }

    static class CoursViewHolder extends RecyclerView.ViewHolder {
        TextView tvSession;
        TextView tvTitreCours;
        TextView tvEnseignantEtCode;

        CoursViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSession = itemView.findViewById(R.id.tvSession);
            tvTitreCours = itemView.findViewById(R.id.tvTitreCours);
            tvEnseignantEtCode = itemView.findViewById(R.id.tvEnseignantEtCode);
        }
    }
}
