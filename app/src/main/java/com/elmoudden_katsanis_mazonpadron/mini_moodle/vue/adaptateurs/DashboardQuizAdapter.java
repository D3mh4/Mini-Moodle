package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.ResultatQuiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardQuizAdapter extends RecyclerView.Adapter<DashboardQuizAdapter.QuizViewHolder> {

    private List<Quiz> quizList = new ArrayList<>();
    private List<ResultatQuiz> userResults = new ArrayList<>();
    private Map<String, String> courseIdToCode = new HashMap<>();
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

    public void setUserResults(List<ResultatQuiz> results) {
        this.userResults = results != null ? results : new ArrayList<>();
        notifyDataSetChanged();
    }
    public void setCoursesForCodeLookup(List<Cours> courses) {
        courseIdToCode.clear();
        if (courses != null) {
            for (Cours c : courses) {
                if (c.getId() != null) {
                    courseIdToCode.put(c.getId(), c.getCode() != null ? c.getCode() : "");
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.liste_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);

        String code = quiz.getCourseId() != null ? courseIdToCode.get(quiz.getCourseId()) : "";
        holder.tvCoursCode.setText(code != null ? code : "");
        holder.tvTitre.setText(quiz.getTitle());

        ResultatQuiz userResult = trouverResultat(quiz.getId());

        if (userResult != null) {
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
            tvTitre = itemView.findViewById(R.id.tvTitre);
            tvInfo = itemView.findViewById(R.id.tvStatut);
            tvScore = itemView.findViewById(R.id.tvNoteQuiz);
        }
    }
}
