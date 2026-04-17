package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.ResultatQuiz;

import java.util.ArrayList;
import java.util.List;

public class QuizAdapter extends ArrayAdapter<Quiz> {

    private Context contexte;
    private int viewResourceId;
    private List<Quiz> lesQuizzes;

    // Résultats de quiz de l'utilisateur connecté, pour afficher le score
    // et le statut "Terminé" sur les quiz complétés.
    private List<ResultatQuiz> userResults = new ArrayList<>();

    public QuizAdapter(@NonNull Context contexte, int viewResourceId, @NonNull List<Quiz> quizzes) {
        super(contexte, viewResourceId, quizzes);
        this.contexte = contexte;
        this.viewResourceId = viewResourceId;
        this.lesQuizzes = quizzes;
    }

    /**
     * Fournit les résultats de l'utilisateur pour que l'adaptateur
     * puisse afficher le statut et le score de chaque quiz.
     */
    public void setUserResults(List<ResultatQuiz> results) {
        this.userResults = results != null ? results : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lesQuizzes.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(contexte);
            view = inflater.inflate(viewResourceId, parent, false);
        }

        Quiz quiz = lesQuizzes.get(position);

        TextView tvCoursCode = view.findViewById(R.id.tvQuizCoursCode);
        TextView tvTitre = view.findViewById(R.id.tvTitre);
        TextView tvStatut = view.findViewById(R.id.tvStatut);
        TextView tvNote = view.findViewById(R.id.tvNoteQuiz);

        tvCoursCode.setText(quiz.getIdCours() != null ? quiz.getIdCours() : "");
        tvTitre.setText(quiz.getTitre());

        ResultatQuiz userResult = trouverResultat(quiz.getId());

        if (userResult != null) {
            // Quiz complété : score en orange à droite, statut "Terminé"
            tvNote.setText(userResult.getScore() + " / " + userResult.getTotal());
            tvNote.setVisibility(View.VISIBLE);
            tvStatut.setText("Terminé");
        } else {
            // Quiz non complété
            tvNote.setVisibility(View.GONE);
            tvStatut.setText(quiz.getNbrQuestions() + " questions");
        }

        return view;
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
}
