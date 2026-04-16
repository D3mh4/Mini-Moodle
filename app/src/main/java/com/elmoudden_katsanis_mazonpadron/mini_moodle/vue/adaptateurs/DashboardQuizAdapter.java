package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptateur RecyclerView pour afficher les quiz sur le tableau de bord.
 * Utilise le layout liste_quiz_dashboard.xml pour chaque item.
 */
public class DashboardQuizAdapter extends RecyclerView.Adapter<DashboardQuizAdapter.QuizViewHolder> {

    private List<Quiz> quizList = new ArrayList<>();
    private OnQuizClickListener listener;

    /**
     * Interface de callback pour le clic sur un quiz.
     */
    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz);
    }

    public DashboardQuizAdapter(OnQuizClickListener listener) {
        this.listener = listener;
    }

    /**
     * Met à jour la liste des quiz et rafraîchit l'affichage.
     */
    public void setQuizList(List<Quiz> newList) {
        this.quizList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.liste_quiz_dashboard, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);

        // Affiche l'ID du cours associé au quiz
        holder.tvCoursCode.setText(quiz.getIdCours() != null ? quiz.getIdCours() : "");

        // Affiche le titre du quiz
        holder.tvTitre.setText(quiz.getTitre());

        // Affiche le nombre de questions
        holder.tvInfo.setText(quiz.getNbrQuestions() + " questions");

        // Clic pour démarrer le quiz
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuizClick(quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    /**
     * ViewHolder pour un item de quiz du tableau de bord.
     */
    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvCoursCode;  // Code du cours associé
        TextView tvTitre;      // Titre du quiz
        TextView tvInfo;       // Info supplémentaire (nb questions)

        QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCoursCode = itemView.findViewById(R.id.tvQuizCoursCode);
            tvTitre = itemView.findViewById(R.id.tvQuizTitre);
            tvInfo = itemView.findViewById(R.id.tvQuizInfo);
        }
    }
}
