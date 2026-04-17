package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.ResultatQuiz;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptateur RecyclerView pour afficher les quiz (tableau de bord + détails cours).
 * Affiche le score de l'utilisateur si un résultat est présent dans userResults.
 */
public class DashboardQuizAdapter extends RecyclerView.Adapter<DashboardQuizAdapter.QuizViewHolder> {

    private List<Quiz> quizList = new ArrayList<>();
    private List<ResultatQuiz> userResults = new ArrayList<>();
    private OnQuizClickListener listener;

    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz);
    }

    public DashboardQuizAdapter(OnQuizClickListener listener) {
        this.listener = listener;
    }

    public void setQuizList(List<Quiz> newList) {
        this.quizList = newList;
        notifyDataSetChanged();
    }

    /**
     * Fournit les résultats de quiz de l'utilisateur pour afficher
     * le score sur les items qu'il a déjà complétés.
     */
    public void setUserResults(List<ResultatQuiz> results) {
        this.userResults = results != null ? results : new ArrayList<>();
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

        holder.tvCoursCode.setText(quiz.getIdCours() != null ? quiz.getIdCours() : "");
        holder.tvTitre.setText(quiz.getTitre());

        // Cherche un résultat de l'utilisateur pour ce quiz
        ResultatQuiz userResult = trouverResultat(quiz.getId());

        if (userResult != null) {
            // Affiche le score sauvegardé (format similaire aux travaux corrigés)
            holder.tvScore.setText(userResult.getScore() + " / " + userResult.getTotal());
            holder.tvScore.setVisibility(View.VISIBLE);
            holder.tvInfo.setText("Complété");
        } else {
            holder.tvScore.setVisibility(View.GONE);
            holder.tvInfo.setText(quiz.getNbrQuestions() + " questions");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuizClick(quiz);
            }
        });
    }

    private ResultatQuiz trouverResultat(String quizId) {
        if (quizId == null || userResults == null) return null;
        for (ResultatQuiz r : userResults) {
            if (quizId.equals(r.getQuizId())) {
                return r;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvCoursCode;
        TextView tvTitre;
        TextView tvInfo;
        TextView tvScore;

        QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCoursCode = itemView.findViewById(R.id.tvQuizCoursCode);
            tvTitre = itemView.findViewById(R.id.tvQuizTitre);
            tvInfo = itemView.findViewById(R.id.tvQuizInfo);
            tvScore = itemView.findViewById(R.id.tvQuizScore);
        }
    }
}