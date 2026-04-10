package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button login;
    Button register;

    ActivityResultLauncher<Intent> activityResultLauncher;
    private ViewModelUser viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.btn_login);
        register = findViewById(R.id.btn_signup);

        login.setOnClickListener(this);
        register.setOnClickListener(this);

        viewModel = new ViewModelProvider(this).get(ViewModelUser.class);

        viewModel.getLoginSuccess().observe(this, success -> {

            if (success != null) {

                if (success) {
                    viewModel.getUser().observe(this, user -> {
                        if (user != null) {
                            Intent intent = new Intent(MainActivity.this, NavActivity.class);
                            intent.putExtra("USER_ID", user.getId());
                            activityResultLauncher.launch(intent);
                        }
                    });

                } else {
                    Toast.makeText(MainActivity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        if ("REGISTER".equals(data.getStringExtra("TYPE"))) {
                            Toast.makeText(MainActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                        } else if ("DISCONNECT".equals(data.getStringExtra("TYPE"))) {
                            Toast.makeText(MainActivity.this, "Déconnexion réussie !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {

        if (v == login) {

            String email = ((EditText) findViewById(R.id.editText_email)).getText().toString();
            String password = ((EditText) findViewById(R.id.editText_password)).getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(this, "Veuillez entrer un courriel valide", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.authentifierUser(email, password);

        } else if (v == register) {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            activityResultLauncher.launch(intent);
        }
    }
}