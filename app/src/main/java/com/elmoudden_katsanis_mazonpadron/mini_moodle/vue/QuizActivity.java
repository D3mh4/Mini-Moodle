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

public class QuizActivity extends AppCompatActivity {

    private TextView tvTitreQuiz, tvQuestion;
    private RadioGroup radioGroup;
    private RadioButton selected;

    private Button btnValider;

    private List<String> questions;
    private List<String[]> choix;
    private List<String> bonnesReponses;

    private int indexQuestion = 0;
    private int score = 0;
    private String titre, reponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvTitreQuiz = findViewById(R.id.tvTitreQuiz);
        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroup = findViewById(R.id.radioGroupChoix);
        btnValider = findViewById(R.id.btnValider);

        titre = getIntent().getStringExtra("titre");
        tvTitreQuiz.setText(titre);

        questions = new ArrayList<>();
        choix = new ArrayList<>();
        bonnesReponses = new ArrayList<>();

        questions.add("Quel protocole est fiable mais plus lent ?");
        choix.add(new String[]{"UDP", "TCP", "IP"});
        bonnesReponses.add("TCP");

        questions.add("Quel protocole est rapide mais moins fiable ?");
        choix.add(new String[]{"TCP", "UDP", "HTTP"});
        bonnesReponses.add("UDP");

        afficherQuestion();

        btnValider.setOnClickListener(v -> {

            int selectedId = radioGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Choisissez une réponse", Toast.LENGTH_SHORT).show();
                return;
            }

            selected = findViewById(selectedId);
            reponse = selected.getText().toString();

            if (reponse.equals(bonnesReponses.get(indexQuestion))) {
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

        tvQuestion.setText(questions.get(indexQuestion));

        ((RadioButton) findViewById(R.id.rb1)).setText(choix.get(indexQuestion)[0]);
        ((RadioButton) findViewById(R.id.rb2)).setText(choix.get(indexQuestion)[1]);
        ((RadioButton) findViewById(R.id.rb3)).setText(choix.get(indexQuestion)[2]);
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