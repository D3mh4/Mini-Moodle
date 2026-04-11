package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText prenom, nom, email, password;
    Button retour, inscription;

    // Ajoute le ViewModel
    private ViewModelUser viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        prenom = findViewById(R.id.editText_prenom);
        nom = findViewById(R.id.editText_nom);
        email = findViewById(R.id.editText_email);
        password = findViewById(R.id.editText_password);

        retour = findViewById(R.id.btn_signup);
        retour.setOnClickListener(this);
        inscription = findViewById(R.id.btn_login);
        inscription.setOnClickListener(this);

        viewModel = new ViewModelProvider(this).get(ViewModelUser.class);

        viewModel.getInscriptionSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, viewModel.getMessage().getValue(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("TYPE", "REGISTER");
                setResult(RESULT_OK, intent);
                finish();
            }else {
                Toast.makeText(this, viewModel.getMessage().getValue(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == retour) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (v == inscription) {
            String prenomValue = prenom.getText().toString().trim();
            String nomValue = nom.getText().toString().trim();
            String emailValue = email.getText().toString().trim();
            String passwordValue = password.getText().toString().trim();

            if (prenomValue.isEmpty() || nomValue.isEmpty() || emailValue.isEmpty() || passwordValue.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User();
            newUser.setId(java.util.UUID.randomUUID().toString());
            newUser.setPrenom(prenomValue);
            newUser.setNom(nomValue);
            newUser.setUsername("");
            newUser.setEmail(emailValue);
            newUser.setPassword(passwordValue);
            newUser.setTelephone("");
            newUser.setPhotoUrl("");
            newUser.setEnrolledCourseIds(new java.util.ArrayList<>());
            newUser.setQuizResults(new java.util.ArrayList<>());
            newUser.setCompletedAssignmentIds(new java.util.ArrayList<>());

            viewModel.inscrireUser(newUser);
        }
    }
}
