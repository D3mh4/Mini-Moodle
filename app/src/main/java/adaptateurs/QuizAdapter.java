package adaptateurs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;

import java.util.List;

import modele.Quiz;

public class QuizAdapter extends ArrayAdapter<Quiz> {

    private Context contexte;
    private int viewResourceId;
    private List<Quiz> lesQuizzes;

    public QuizAdapter(@NonNull Context contexte, int viewResourceId, @NonNull List<Quiz> quizzes) {
        super(contexte, viewResourceId, quizzes);
        this.contexte = contexte;
        this.viewResourceId = viewResourceId;
        this.lesQuizzes = quizzes;
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

        TextView tvTitre = view.findViewById(R.id.tvTitre);
        TextView tvNbQuestions = view.findViewById(R.id.tvNbQuestions);
        TextView tvStatut = view.findViewById(R.id.tvStatut);

        tvTitre.setText(quiz.getTitre());
        tvNbQuestions.setText(String.valueOf(quiz.getNbrQuestions()));
        tvStatut.setText(quiz.getStatut());

        return view;
    }
}