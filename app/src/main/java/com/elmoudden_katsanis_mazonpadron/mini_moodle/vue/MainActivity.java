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

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button login;
    Button register;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.btn_login);
        login.setOnClickListener(this);

        register = findViewById(R.id.btn_signup);
        register.setOnClickListener(this);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() { // Utilise ActivityResult ici
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            if(data.getStringExtra("TYPE").equals("REGISTER")){
                                Toast.makeText(MainActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                            } else if (data.getStringExtra("TYPE").equals("DISCONNECT")){
                                Toast.makeText(MainActivity.this, "Déconnexion réussie !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == login) {
            String email = ((EditText) findViewById(R.id.editText_email)).getText().toString();
            String password = ((EditText) findViewById(R.id.editText_password)).getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }

            if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(MainActivity.this, "Veuillez entrer un courriel valide", Toast.LENGTH_SHORT).show();
            }



            Intent intent = new Intent(MainActivity.this, NavActivity.class);
            activityResultLauncher.launch(intent);
        } else if (v == register) {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            activityResultLauncher.launch(intent);
        }
    }
}
