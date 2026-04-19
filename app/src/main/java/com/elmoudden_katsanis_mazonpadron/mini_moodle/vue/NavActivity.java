package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelAssignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelCours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.databinding.NavActivityBinding;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Annonce;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavActivity extends AppCompatActivity implements View.OnClickListener {

    private NavActivityBinding binding;
    ViewModelUser viewModel;
    ViewModelCours viewModelCours;
    ViewModelAssignment viewModelAssignment;
    ImageView logout;
    ImageView notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = NavActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ViewModelUser.class);
        viewModelCours = new ViewModelProvider(this).get(ViewModelCours.class);
        viewModelAssignment = new ViewModelProvider(this).get(ViewModelAssignment.class);

        String userId = getIntent().getStringExtra("USER_ID");

        logout = findViewById(R.id.img_logout);
        logout.setOnClickListener(this);

        notifications = findViewById(R.id.imageView4);
        notifications.setOnClickListener(this);

        viewModel.chargerUserParId(userId);

        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                viewModelAssignment.setCurrentUser(user);
                if (user.getEnrolledCourseIds() != null) {
                    viewModelCours.chargerCoursInscrits(user.getEnrolledCourseIds());
                }
            }
        });

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
                getSupportFragmentManager().popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                replaceFragment(fragment);
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        if (v == logout) {
            Intent intent = new Intent();
            intent.putExtra("TYPE", "DISCONNECT");
            setResult(RESULT_OK, intent);
            finish();
        } else if (v == notifications) {
            afficherNotifications();
        }
    }

    /**
     * Affiche un popup avec toutes les notifications de l'utilisateur :
     *  1. Ses annonces personnelles (travaux soumis, quiz complétés) en premier
     *  2. Les annonces globales de chacun de ses cours inscrits
     */
    private void afficherNotifications() {
        List<Cours> courses = viewModelCours.getEnrolledCourses().getValue();
        User user = viewModel.getUser().getValue();

        if (courses == null || courses.isEmpty()) {
            Toast.makeText(this, "Aucune notification", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lookup courseId → code pour préfixer chaque annonce
        Map<String, String> courseIdToCode = new HashMap<>();
        for (Cours c : courses) {
            if (c.getId() != null) {
                courseIdToCode.put(c.getId(), c.getCode() != null ? c.getCode() : "");
            }
        }

        List<String> toutesLesAnnonces = new ArrayList<>();

        // Annonces personnelles en tête (plus récentes)
        if (user != null && user.getUserAnnonces() != null) {
            for (Annonce a : user.getUserAnnonces()) {
                if (a != null && a.getText() != null) {
                    String code = a.getCourseId() != null ? courseIdToCode.get(a.getCourseId()) : null;
                    String prefix = code != null ? code : "";
                    toutesLesAnnonces.add(prefix + " — " + a.getText());
                }
            }
        }

        // Annonces globales ensuite
        for (Cours cours : courses) {
            List<String> annonces = cours.getAnnonces();
            if (annonces != null) {
                for (String annonce : annonces) {
                    String prefix = cours.getCode() != null ? cours.getCode() : "";
                    toutesLesAnnonces.add(prefix + " — " + annonce);
                }
            }
        }

        if (toutesLesAnnonces.isEmpty()) {
            Toast.makeText(this, "Aucune notification", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] items = toutesLesAnnonces.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Notifications")
                .setItems(items, null)
                .setPositiveButton("Fermer", null)
                .show();
    }
}
