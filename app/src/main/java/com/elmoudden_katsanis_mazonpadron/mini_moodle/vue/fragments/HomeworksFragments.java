package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelAssignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.Assignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.adaptateurs.AssignmentAdapter;

import java.util.List;

public class HomeworksFragments extends Fragment {

    private ViewModelUser viewModelUser;
    private ViewModelAssignment viewModelAssignment;
    private AssignmentAdapter adapter;

    private TextView tvCountAFaire, tvCountRemis, tvCountRetard, tvCountCorrige;
    private TextView tvAucunDevoir;
    private RecyclerView rvTravaux;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.devoirs_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        viewModelAssignment = new ViewModelProvider(requireActivity()).get(ViewModelAssignment.class);

        tvCountAFaire = view.findViewById(R.id.tvCountAFaire);
        tvCountRemis = view.findViewById(R.id.tvCountRemis);
        tvCountRetard = view.findViewById(R.id.tvCountRetard);
        tvCountCorrige = view.findViewById(R.id.tvCountCorrige);
        tvAucunDevoir = view.findViewById(R.id.tvAucunDevoir);
        rvTravaux = view.findViewById(R.id.rvTousLesTravaux);

        rvTravaux.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AssignmentAdapter(assignment -> {
            viewModelAssignment.setSelectedAssignment(assignment);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AssignmentDetailFragment())
                    .addToBackStack(null)
                    .commit();
        });
        rvTravaux.setAdapter(adapter);

        // S'assurer que le ViewModel a bien l'utilisateur courant pour filtrer
        if (viewModelUser.getUser().getValue() != null) {
            viewModelAssignment.setCurrentUser(viewModelUser.getUser().getValue());
        }

        viewModelAssignment.chargerTousLesAssignments();

        viewModelAssignment.getAssignmentList().observe(getViewLifecycleOwner(), assignments -> {
            if (assignments != null) {
                adapter.setAssignmentList(assignments);
                mettreAJourCompteurs(assignments);

                tvAucunDevoir.setVisibility(assignments.isEmpty() ? View.VISIBLE : View.GONE);
                rvTravaux.setVisibility(assignments.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });

        // Recharger quand l'utilisateur change (ex: retour d'une soumission)
        viewModelUser.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                viewModelAssignment.setCurrentUser(user);
                viewModelAssignment.chargerTousLesAssignments();
            }
        });
    }

    private void mettreAJourCompteurs(List<Assignment> assignments) {
        int aFaire = 0, remis = 0, retard = 0, corrige = 0;

        for (Assignment a : assignments) {
            String statut = a.getStatus();
            if (statut != null) {
                switch (statut.toLowerCase()) {
                    case "à faire":
                    case "non soumis":
                        aFaire++;
                        break;
                    case "remis":
                        remis++;
                        break;
                    case "en retard":
                        retard++;
                        break;
                    case "corrigé":
                        corrige++;
                        break;
                }
            }
        }

        tvCountAFaire.setText(String.valueOf(aFaire));
        tvCountRemis.setText(String.valueOf(remis));
        tvCountRetard.setText(String.valueOf(retard));
        tvCountCorrige.setText(String.valueOf(corrige));
    }
}
