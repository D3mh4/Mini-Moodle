package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;

import java.util.ArrayList;
import java.util.List;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Question;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Quiz;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvTitreQuiz = findViewById(R.id.tvTitreQuiz);
        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroup = findViewById(R.id.radioGroupChoix);
        btnValider = findViewById(R.id.btnValider);

        quiz = (Quiz) getIntent().getSerializableExtra("quiz");
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
        ((RadioButton) findViewById(R.id.rb1)).setText(options[0]);
        ((RadioButton) findViewById(R.id.rb2)).setText(options[1]);
        ((RadioButton) findViewById(R.id.rb3)).setText(options[2]);
    }

    private void afficherResultat() {
        Toast.makeText(this,"Score: " + score + "/" + questions.size(),
                       Toast.LENGTH_LONG).show();


        Intent intent = new Intent();
        intent.putExtra("score", score);

        setResult(RESULT_OK, intent);

        finish();
    }
}