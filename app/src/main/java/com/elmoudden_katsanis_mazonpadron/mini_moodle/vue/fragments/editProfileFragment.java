package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;

public class editProfileFragment extends Fragment implements View.OnClickListener {

    private EditText nom, prenom, username, email, password, telephone, url;
    private Button btnSave, btnCancel;
    private ViewModelUser viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.editprofile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);

        nom = view.findViewById(R.id.editText_nom);
        prenom = view.findViewById(R.id.editText_prenom);
        username = view.findViewById(R.id.editText_username);
        email = view.findViewById(R.id.editText_email);
        password = view.findViewById(R.id.editText_password);
        telephone = view.findViewById(R.id.editText_telephone);
        url = view.findViewById(R.id.editText_imageurl);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        viewModel.getMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                android.widget.Toast.makeText(getContext(), msg, android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                getParentFragmentManager().popBackStack();
                viewModel.resetSaveStatus();
            }
        });

        viewModel.getUser().observe(getViewLifecycleOwner(), u -> {
            if (u != null) {
                nom.setText(u.getNom());
                prenom.setText(u.getPrenom());
                username.setText(u.getUsername());
                email.setText(u.getEmail());
                password.setText(u.getPassword());
                telephone.setText(u.getTelephone());
                url.setText(u.getPhotoUrl());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnSave) {
            User currentUser = viewModel.getUser().getValue();
            if (currentUser != null) {
                User u = new User();
                u.setId(currentUser.getId());
                u.setNom(nom.getText().toString());
                u.setPrenom(prenom.getText().toString());
                u.setUsername(username.getText().toString());
                u.setEmail(email.getText().toString());
                u.setPassword(password.getText().toString());
                u.setTelephone(telephone.getText().toString());
                u.setPhotoUrl(url.getText().toString());

                viewModel.editUser(u);
            }
        } else if (v == btnCancel) {
            getParentFragmentManager().popBackStack();
        }
    }
}