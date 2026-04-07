package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments.*;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.databinding.NavFragmentBinding;

public class NavActivity extends AppCompatActivity {

    private NavFragmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = NavFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new DashboardFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(new DashboardFragment());
            } else if (itemId == R.id.courses) {
                replaceFragment(new CoursesFragment());
            } else if (itemId == R.id.homeworks) {
                replaceFragment(new HomeworksFragments());
            } else if (itemId == R.id.quiz) {
                replaceFragment(new QuizFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
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