package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private Button btnModifierProfil;
    private ImageView imageProfil;
    private TextView txtNomPrenom, txtCourriel, txtTelephone;
    private TextView txtModNom, txtModPrenom, txtModCourriel, txtModTelephone, txtModPasse;
    private EditText editPrenom, editNom, editTelephone, editCourriel, editMotDePasse;
    private boolean modeEdition = false;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtNomPrenom = view.findViewById(R.id.txtNomPrenom);
        txtCourriel = view.findViewById(R.id.txtCourriel);
        txtTelephone = view.findViewById(R.id.txtTelephone);

        txtModNom = view.findViewById(R.id.txtModNom);
        txtModPrenom = view.findViewById(R.id.txtModPrenom);
        txtModCourriel = view.findViewById(R.id.txtModCourriel);
        txtModTelephone = view.findViewById(R.id.txtModTelephone);
        txtModPasse = view.findViewById(R.id.txtModPasse);

        editPrenom = view.findViewById(R.id.editPrenom);
        editNom = view.findViewById(R.id.editNom);
        editTelephone = view.findViewById(R.id.editTelephone);
        editCourriel = view.findViewById(R.id.editCourriel);
        editMotDePasse = view.findViewById(R.id.editMotDePasse);

        imageProfil = view.findViewById(R.id.imageProfil);
        btnModifierProfil = view.findViewById(R.id.btnModifierProfil);

        btnModifierProfil.setOnClickListener(this);
    }

    private void activerEdition() {
        modeEdition = true;

        txtNomPrenom.setVisibility(View.GONE);
        txtCourriel.setVisibility(View.GONE);
        txtTelephone.setVisibility(View.GONE);

        txtModNom.setVisibility(View.VISIBLE);
        txtModPrenom.setVisibility(View.VISIBLE);
        txtModCourriel.setVisibility(View.VISIBLE);
        txtModTelephone.setVisibility(View.VISIBLE);
        txtModPasse.setVisibility(View.VISIBLE);

        editNom.setVisibility(View.VISIBLE);
        editPrenom.setVisibility(View.VISIBLE);
        editCourriel.setVisibility(View.VISIBLE);
        editTelephone.setVisibility(View.VISIBLE);
        editMotDePasse.setVisibility(View.VISIBLE);

        btnModifierProfil.setText("Enregistrer");
    }

    private void enregistrerProfil() {
        modeEdition = false;

        String nom = editNom.getText().toString();
        String prenom = editPrenom.getText().toString();
        String courriel = editCourriel.getText().toString();
        String telephone = editTelephone.getText().toString();

        txtNomPrenom.setText(prenom + " " + nom);
        txtCourriel.setText(courriel);
        txtTelephone.setText(telephone);

        txtNomPrenom.setVisibility(View.VISIBLE);
        txtCourriel.setVisibility(View.VISIBLE);
        txtTelephone.setVisibility(View.VISIBLE);

        txtModNom.setVisibility(View.GONE);
        txtModPrenom.setVisibility(View.GONE);
        txtModCourriel.setVisibility(View.GONE);
        txtModTelephone.setVisibility(View.GONE);
        txtModPasse.setVisibility(View.GONE);

        editNom.setVisibility(View.GONE);
        editPrenom.setVisibility(View.GONE);
        editCourriel.setVisibility(View.GONE);
        editTelephone.setVisibility(View.GONE);
        editMotDePasse.setVisibility(View.GONE);

        btnModifierProfil.setText("Modifier mon profil");
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnModifierProfil) {
            if (!modeEdition) {
                activerEdition();
            } else {
                enregistrerProfil();
            }
        }
    }

}
