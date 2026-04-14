package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptateur RecyclerView pour afficher les cours dans une liste.
 * Chaque item affiche le code du cours, le titre et une flèche de navigation.
 *
 * Utilise le patron ViewHolder pour recycler les vues efficacement
 * (évite les appels répétés à findViewById).
 */
public class CoursAdapter extends RecyclerView.Adapter<CoursAdapter.CoursViewHolder> {

    // Liste des cours à afficher
    private List<Cours> coursList = new ArrayList<>();

    // Interface de callback pour gérer le clic sur un cours
    private OnCoursClickListener listener;

    /**
     * Interface fonctionnelle pour le clic sur un cours.
     * Implémentée par le Fragment qui utilise cet adaptateur.
     */
    public interface OnCoursClickListener {
        void onCoursClick(Cours cours);
    }

    /**
     * Constructeur de l'adaptateur.
     * @param listener Le callback à appeler quand un cours est cliqué
     */
    public CoursAdapter(OnCoursClickListener listener) {
        this.listener = listener;
    }

    /**
     * Met à jour la liste des cours et rafraîchit l'affichage.
     * @param newList La nouvelle liste de cours à afficher
     */
    public void setCoursList(List<Cours> newList) {
        this.coursList = newList;
        // notifyDataSetChanged() informe le RecyclerView que les données ont changé
        // et qu'il doit redessiner tous les items
        notifyDataSetChanged();
    }

    /**
     * Appelé par le RecyclerView quand il a besoin d'un nouveau ViewHolder.
     * Inflate (gonfle) le layout XML pour créer la vue d'un item.
     */
    @NonNull
    @Override
    public CoursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater convertit le XML en objets View Java
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.liste_cours, parent, false);
        return new CoursViewHolder(view);
    }

    /**
     * Appelé par le RecyclerView pour afficher les données à une position donnée.
     * Lie les données du cours aux vues du ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull CoursViewHolder holder, int position) {
        Cours cours = coursList.get(position);

        // Affiche le code du cours (ex: "TCH055")
        holder.tvCodeCours.setText(cours.getCodeCours());

        // Affiche le titre du cours (ex: "Base de données")
        holder.tvTitreCours.setText(cours.getTitre());

        // Définit le clic sur tout l'item pour naviguer vers les détails
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCoursClick(cours);
            }
        });
    }

    /**
     * Retourne le nombre total d'items dans la liste.
     * Utilisé par le RecyclerView pour savoir combien d'items afficher.
     */
    @Override
    public int getItemCount() {
        return coursList.size();
    }

    /**
     * ViewHolder : contient les références aux vues d'un item.
     * Patron de conception qui évite les appels répétés à findViewById()
     * en stockant les références une seule fois lors de la création.
     */
    static class CoursViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodeCours;   // Code du cours (ex: TCH057)
        TextView tvTitreCours;  // Titre du cours (ex: Développement mobile)
        ImageView ivArrow;      // Flèche indiquant la navigation

        CoursViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCodeCours = itemView.findViewById(R.id.tvCodeCours);
            tvTitreCours = itemView.findViewById(R.id.tvTitreCours);
            ivArrow = itemView.findViewById(R.id.ivArrowCours);
        }
    }
}
