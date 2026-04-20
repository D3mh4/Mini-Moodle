package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao.UserDao;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Annonce;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Question;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.ResultatQuiz;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizActivity extends AppCompatActivity {

    private TextView tvTitreQuiz, tvQuestion;
    private RadioGroup radioGroup;

    private Button btnValider;

    private Quiz quiz;
    private List<Question> questions;

    private int indexQuestion = 0;
    private int score = 0;

    private String userId;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvTitreQuiz = findViewById(R.id.tvTitreQuiz);
        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroup = findViewById(R.id.radioGroupChoix);
        btnValider = findViewById(R.id.btnValider);

        quiz = (Quiz) getIntent().getSerializableExtra("quiz");
        userId = getIntent().getStringExtra("userId");

        if (quiz != null) {
            tvTitreQuiz.setText(quiz.getTitle());
            questions = quiz.getQuestions();
        }

        if (questions == null || questions.isEmpty()) {
            Toast.makeText(this, "Pas de questions dans ce quiz", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        afficherQuestion();

        btnValider.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Choisissez une réponse", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedIndex = -1;
            if (selectedId == R.id.rb1) selectedIndex = 0;
            else if (selectedId == R.id.rb2) selectedIndex = 1;
            else if (selectedId == R.id.rb3) selectedIndex = 2;

            Question current = questions.get(indexQuestion);
            if (selectedIndex == current.getCorrectOption()) {
                score++;
            }

            indexQuestion++;

            if (indexQuestion < questions.size()) {
                afficherQuestion();
            } else {
                afficherResultat();
            }
        });
    }

    private void afficherQuestion() {
        radioGroup.clearCheck();

        Question q = questions.get(indexQuestion);
        tvQuestion.setText(q.getQuestion());

        String[] options = q.getOptions();
        RadioButton rb1 = findViewById(R.id.rb1);
        RadioButton rb2 = findViewById(R.id.rb2);
        RadioButton rb3 = findViewById(R.id.rb3);

        if (options != null && options.length >= 1) {
            rb1.setText(options[0]);
            rb1.setVisibility(View.VISIBLE);
        } else {
            rb1.setVisibility(View.GONE);
        }

        if (options != null && options.length >= 2) {
            rb2.setText(options[1]);
            rb2.setVisibility(View.VISIBLE);
        } else {
            rb2.setVisibility(View.GONE);
        }

        if (options != null && options.length >= 3) {
            rb3.setText(options[2]);
            rb3.setVisibility(View.VISIBLE);
        } else {
            rb3.setVisibility(View.GONE);
        }
    }

    private void afficherResultat() {
        Toast.makeText(this, "Score: " + score + "/" + questions.size(),
                Toast.LENGTH_LONG).show();

        btnValider.setEnabled(false);

        if (userId != null && quiz != null && quiz.getId() != null) {
            executorService.execute(() -> {
                sauvegarderResultatEtAnnonceSync();
                runOnUiThread(this::terminerAvecResultat);
            });
        } else {
            terminerAvecResultat();
        }
    }

    private void terminerAvecResultat() {
        Intent intent = new Intent();
        intent.putExtra("score", score);
        intent.putExtra("total", questions.size());
        intent.putExtra("quizId", quiz != null ? quiz.getId() : null);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Sauvegarde synchrone du résultat ET de l'annonce personnelle.
     * Bloque jusqu'à ce que les PATCH soient terminés.
     */
    private void sauvegarderResultatEtAnnonceSync() {
        final int finalScore = score;
        final int finalTotal = questions.size();
        final String finalQuizId = quiz.getId();
        final String quizTitle = quiz.getTitle();
        final String courseId = quiz.getCourseId();

        try {
            List<User> users = UserDao.getUsers();
            List<ResultatQuiz> results = new ArrayList<>();
            List<Annonce> annonces = new ArrayList<>();

            if (users != null) {
                for (User u : users) {
                    if (u.getId().equals(userId)) {
                        if (u.getQuizResults() != null) {
                            results = new ArrayList<>(u.getQuizResults());
                        }
                        if (u.getUserAnnonces() != null) {
                            annonces = new ArrayList<>(u.getUserAnnonces());
                        }
                        break;
                    }
                }
            }

            results.removeIf(r -> finalQuizId.equals(r.getQuizId()));
            ResultatQuiz nouveau = new ResultatQuiz();
            nouveau.setQuizId(finalQuizId);
            nouveau.setScore(finalScore);
            nouveau.setTotal(finalTotal);
            results.add(nouveau);

            String text = (quizTitle != null ? quizTitle : "") + " complété";
            annonces.add(0, new Annonce(courseId, text));

            UserDao.updateQuizResults(userId, results);
            UserDao.updateUserAnnonces(userId, annonces);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
