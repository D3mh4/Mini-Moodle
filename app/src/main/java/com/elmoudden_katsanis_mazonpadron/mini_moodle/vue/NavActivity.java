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
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelAssignment;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelCours;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel.ViewModelUser;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.databinding.NavActivityBinding;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.vue.fragments.*;

/**
 * Activité principale de navigation après la connexion.
 *
 * Rôle :
 * - Héberge le FrameLayout (fragment_container) dans lequel les fragments s'affichent
 * - Gère la BottomNavigationView (barre de navigation en bas)
 * - Initialise les ViewModels partagés entre tous les fragments
 * - Charge les données de l'utilisateur connecté
 *
 * Les ViewModels sont créés ici avec ViewModelProvider(this), ce qui signifie
 * que tous les fragments enfants qui utilisent ViewModelProvider(requireActivity())
 * accèdent aux MÊMES instances de ViewModel. C'est le mécanisme principal
 * de partage de données entre fragments dans MVVM.
 *
 * ViewBinding (NavActivityBinding) est utilisé pour accéder aux vues du layout
 * sans appeler findViewById(). Le binding est généré automatiquement à partir du
 * nom du fichier XML (nav_activity.xml -> NavActivityBinding).
 */
public class NavActivity extends AppCompatActivity implements View.OnClickListener {

    // ViewBinding généré automatiquement depuis nav_activity.xml
    private NavActivityBinding binding;

    // ViewModels partagés avec tous les fragments
    ViewModelUser viewModel;
    ViewModelCours viewModelCours;
    ViewModelAssignment viewModelAssignment;

    // Bouton de déconnexion dans le header
    ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate() crée le binding et la hiérarchie de vues
        binding = NavActivityBinding.inflate(getLayoutInflater());
        // setContentView avec la racine du binding (au lieu de R.layout.nav_activity)
        setContentView(binding.getRoot());

        // --- Initialisation des ViewModels ---
        // ViewModelProvider(this) crée des ViewModels liés au cycle de vie de cette Activity
        // Ils survivent aux changements de configuration (rotation d'écran)
        viewModel = new ViewModelProvider(this).get(ViewModelUser.class);
        viewModelCours = new ViewModelProvider(this).get(ViewModelCours.class);
        viewModelAssignment = new ViewModelProvider(this).get(ViewModelAssignment.class);

        // Récupère l'ID de l'utilisateur passé par MainActivity lors de la connexion
        String userId = getIntent().getStringExtra("USER_ID");

        // Configuration du bouton de déconnexion
        logout = findViewById(R.id.img_logout);
        logout.setOnClickListener(this);

        // Charge les données de l'utilisateur depuis le serveur JSON
        viewModel.chargerUserParId(userId);

        // Affiche le tableau de bord comme premier écran
        replaceFragment(new DashboardFragment());

        // --- Navigation par la barre du bas ---
        // setOnItemSelectedListener écoute les clics sur les items de la BottomNavigationView
        // Chaque clic remplace le fragment affiché dans fragment_container
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment fragment = null;
            int itemId = item.getItemId();

            // Détermine quel fragment afficher selon l'item cliqué
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
                // Vide la pile de retour (back stack) avant de naviguer
                // Cela évite l'accumulation de fragments quand on navigue
                // entre les onglets principaux
                getSupportFragmentManager().popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                replaceFragment(fragment);
            }

            return true;
        });
    }

    /**
     * Remplace le fragment actuellement affiché dans le container.
     *
     * @param fragment Le nouveau fragment à afficher
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace() retire l'ancien fragment et ajoute le nouveau
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        if (v == logout) {
            // Envoie le résultat "DISCONNECT" à MainActivity
            Intent intent = new Intent();
            intent.putExtra("TYPE", "DISCONNECT");
            setResult(RESULT_OK, intent);
            finish(); // Ferme cette activité et retourne à MainActivity
        }
    }
}
