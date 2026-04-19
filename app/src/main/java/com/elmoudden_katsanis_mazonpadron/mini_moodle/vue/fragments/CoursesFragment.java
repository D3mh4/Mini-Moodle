package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelCours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Cours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.CoursAdapter;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {

    private ViewModelUser viewModelUser;
    private ViewModelCours viewModelCours;
    private CoursAdapter adapter;
    private RecyclerView rvCours;
    private EditText etRecherche;
    private TextView tvAucunCours;

    private List<Cours> tousLesCours = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cours_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        viewModelCours = new ViewModelProvider(requireActivity()).get(ViewModelCours.class);

        rvCours = view.findViewById(R.id.rvListeCours);
        etRecherche = view.findViewById(R.id.etRechercheCours);
        tvAucunCours = view.findViewById(R.id.tvAucunCours);

        rvCours.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CoursAdapter(cours -> {
            viewModelCours.setSelectedCours(cours);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CourseDetailFragment())
                    .addToBackStack(null)
                    .commit();
        });
        rvCours.setAdapter(adapter);

        etRecherche.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filtrerCours(s.toString());
            }
        });

        viewModelUser.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null && user.getEnrolledCourseIds() != null) {
                viewModelCours.chargerCoursInscrits(user.getEnrolledCourseIds());
            }
        });

        viewModelCours.getEnrolledCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                tousLesCours = courses;
                adapter.setCoursList(courses);

                tvAucunCours.setVisibility(courses.isEmpty() ? View.VISIBLE : View.GONE);
                rvCours.setVisibility(courses.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void filtrerCours(String query) {
        if (query.isEmpty()) {
            adapter.setCoursList(tousLesCours);
            tvAucunCours.setVisibility(tousLesCours.isEmpty() ? View.VISIBLE : View.GONE);
            rvCours.setVisibility(tousLesCours.isEmpty() ? View.GONE : View.VISIBLE);
            return;
        }

        List<Cours> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Cours c : tousLesCours) {
            boolean matchCode = c.getCode() != null
                    && c.getCode().toLowerCase().contains(lowerQuery);
            boolean matchTitre = c.getTitle() != null
                    && c.getTitle().toLowerCase().contains(lowerQuery);

            if (matchCode || matchTitre) {
                filtered.add(c);
            }
        }

        adapter.setCoursList(filtered);
        tvAucunCours.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        rvCours.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
