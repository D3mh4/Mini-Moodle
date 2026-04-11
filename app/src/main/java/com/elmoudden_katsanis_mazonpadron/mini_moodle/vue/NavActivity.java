package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.databinding.NavActivityBinding;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments.*;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.databinding.NavActivityBinding;

public class NavActivity extends AppCompatActivity implements View.OnClickListener{

    private NavActivityBinding binding;
    ViewModelUser viewModel;
    ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = NavActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ViewModelUser.class);

        String userId = getIntent().getStringExtra("USER_ID");

        logout = findViewById(R.id.img_logout);
        logout.setOnClickListener(this);

        viewModel.chargerUserParId(userId);

        replaceFragment(new DashboardFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                fragment = new DashboardFragment();
            } else if (itemId == R.id.courses) {
                fragment = new CoursesFragment();
            } else if (itemId == R.id.homeworks) {
                fragment = new HomeworksFragments();
            } else if (itemId == R.id.quiz) {
                fragment = new QuizFragment();
            } else if (itemId == R.id.profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                replaceFragment(fragment);
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        if (v==logout){
            Intent intent = new Intent();
            intent.putExtra("TYPE", "DISCONNECT");
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}