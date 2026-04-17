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
    private RadioButton selected;

    private Button btnValider;

    private Quiz quiz;
    private List<Question> questions;

    private int indexQuestion = 0;
    private int score = 0;
    private String reponse;

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
        // ID de l'utilisateur connecté, passé par le fragment qui lance le quiz
        userId = getIntent().getStringExtra("userId");

        if (quiz != null) {
            tvTitreQuiz.setText(quiz.getTitre());
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

            selected = findViewById(selectedId);
            reponse = selected.getText().toString();

            if (reponse.equals(questions.get(indexQuestion).getCorrectAnswer())) {
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
        tvQuestion.setText(q.getStatement());

        String[] options = q.getChoices();
        RadioButton rb1 = findViewById(R.id.rb1);
        RadioButton rb2 = findViewById(R.id.rb2);
        RadioButton rb3 = findViewById(R.id.rb3);

        // Affiche seulement le nombre de boutons correspondant aux choix disponibles
        // (vrai/faux = 2 choix, QCM = 3 choix)
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

        // Désactive le bouton pour éviter un double-clic pendant la sauvegarde
        btnValider.setEnabled(false);

        // Sauvegarde persistante du résultat AVANT de rendre le contrôle au fragment.
        // On attend que le PATCH soit terminé pour que rechargerUserActuel() dans le
        // fragment appelant voie les données à jour dès le premier retour.
        if (userId != null && quiz != null && quiz.getId() != null) {
            executorService.execute(() -> {
                sauvegarderResultatSync();
                runOnUiThread(this::terminerAvecResultat);
            });
        } else {
            terminerAvecResultat();
        }
    }

    /**
     * Termine l'activité en renvoyant le score au fragment appelant.
     * Doit être appelé sur le thread UI.
     */
    private void terminerAvecResultat() {
        Intent intent = new Intent();
        intent.putExtra("score", score);
        intent.putExtra("total", questions.size());
        intent.putExtra("quizId", quiz != null ? quiz.getId() : null);

        setResult(RESULT_OK, intent);

        finish();
    }

    /**
     * Sauvegarde synchrone du résultat (bloquante, doit être appelée depuis
     * un thread secondaire). Récupère les quizResults frais du serveur,
     * ajoute/remplace le résultat pour ce quiz, puis PATCH.
     * Bloque jusqu'à ce que le PATCH soit terminé.
     */
    private void sauvegarderResultatSync() {
        final int finalScore = score;
        final int finalTotal = questions.size();
        final String finalQuizId = quiz.getId();

        try {
            List<User> users = UserDao.getUsers();
            List<ResultatQuiz> results = new ArrayList<>();

            if (users != null) {
                for (User u : users) {
                    if (u.getId().equals(userId)) {
                        if (u.getQuizResults() != null) {
                            results = new ArrayList<>(u.getQuizResults());
                        }
                        break;
                    }
                }
            }

            // Retire l'ancien résultat pour ce quiz (si présent), garde le plus récent
            results.removeIf(r -> finalQuizId.equals(r.getQuizId()));

            ResultatQuiz nouveau = new ResultatQuiz();
            nouveau.setQuizId(finalQuizId);
            nouveau.setScore(finalScore);
            nouveau.setTotal(finalTotal);
            results.add(nouveau);

            UserDao.updateQuizResults(userId, results);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
