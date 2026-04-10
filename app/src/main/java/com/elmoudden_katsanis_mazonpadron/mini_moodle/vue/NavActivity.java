package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments.*;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.databinding.NavFragmentBinding;

public class NavActivity extends AppCompatActivity {

    private NavFragmentBinding binding;
    ViewModelUser viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = NavFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ViewModelUser.class);

        String userId = getIntent().getStringExtra("USER_ID");

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
}