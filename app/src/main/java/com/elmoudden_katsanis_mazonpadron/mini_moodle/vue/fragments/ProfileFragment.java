package com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.R;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private TextView tvNomPrenom, tvUsername;
    private ImageView ivProfile;
    private Button btnEditProfile;

    private ViewModelUser viewModel;


    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);

        tvNomPrenom = view.findViewById(R.id.tv_nom_prenom);
        tvUsername = view.findViewById(R.id.tv_username);
        ivProfile = view.findViewById(R.id.imageView_profile);
        btnEditProfile = view.findViewById(R.id.btn_editProfile);
        btnEditProfile.setOnClickListener(this);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvNomPrenom.setText(user.getNom() + " " + user.getPrenom());
                tvUsername.setText("@" + user.getUsername());

                ivProfile.setImageResource(R.drawable.profile_placeholder);

                String imageUrl = user.getPhotoUrl();

                if (imageUrl != null && !imageUrl.isEmpty() && imageUrl.startsWith("http")) {
                    new Thread(() -> {
                        try {
                            java.net.URL url = new java.net.URL(imageUrl);
                            java.io.InputStream inputStream = url.openStream();
                            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(inputStream);

                            if (bitmap != null && isAdded()) {
                                ivProfile.post(() -> ivProfile.setImageBitmap(bitmap));
                            }
                            inputStream.close();
                        } catch (Exception e) {
                            if (isAdded()) {
                                ivProfile.post(() -> ivProfile.setImageResource(R.drawable.profile_placeholder));
                            }
                        }
                    }).start();
                }
            }
        });



    }

    @Override
    public void onClick(View v) {
        if (v == btnEditProfile) {
            viewModel.resetSaveStatus();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new editProfileFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

}
