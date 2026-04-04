package com.elmoudden_katsanis_mazonpadron.mini_moodle.activites.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnTestProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTestProfil = findViewById(R.id.btnTestProfil);
        btnTestProfil.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnTestProfil) {
            ProfileFragment profileFragment = new ProfileFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, profileFragment);
            transaction.commit();
        }
    }
}