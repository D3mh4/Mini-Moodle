package com.elmoudden_katsanis_mazonpadron.mini_moodle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.dao.UserDao;
import com.elmoudden_katsanis_mazonpadron.mini_moodle.modeles.entite.User;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewModelUser extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> inscriptionSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public LiveData<User> getUser() {
        return user;
    }

    public void setUser(User u) {
        user.setValue(u);
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    public LiveData<Boolean> getInscriptionSuccess() {
        return inscriptionSuccess;
    }

    public void authentifierUser(String email, String password) {
        executorService.execute(() -> {
            try {
                List<User> users = UserDao.getUsers();

                if (users != null) {
                    for (User u : users) {
                        if (email.equals(u.getEmail()) && password.equals(u.getPassword())) {
                            user.postValue(u);
                            loginSuccess.postValue(true);
                            return;
                        }
                    }
                }

                loginSuccess.postValue(false);

            } catch (JSONException | IOException e) {
                message.postValue("Erreur de connexion au serveur");
                loginSuccess.postValue(false);
            }
        });
    }

    public void chargerUserParId(String id) {
        executorService.execute(() -> {
            try {
                List<User> users = UserDao.getUsers();

                if (users != null) {
                    for (User u : users) {
                        if (u.getId().equals(id)) {
                            user.postValue(u);
                            return;
                        }
                    }
                }

            } catch (JSONException | IOException e) {
                message.postValue("Erreur lors du chargement");
            }
        });
    }

    public void editUser(User u) {
        executorService.execute(() -> {
            try {
                List<User> users = UserDao.getUsers();
                if (users != null) {
                    for (User existingUser : users) {
                        if (existingUser.getEmail().equalsIgnoreCase(u.getEmail())
                                && !existingUser.getId().equals(u.getId())) {

                            message.postValue("Cet email est déjà utilisé par un autre compte.");
                            saveSuccess.postValue(false);
                            return;
                        }
                    }
                }
                boolean reussite = UserDao.enregistrer(u);
                if (reussite) {
                    user.postValue(u);
                    message.postValue("Modification réussie");
                    saveSuccess.postValue(true);
                } else {
                    message.postValue("Erreur lors de la modification");
                    saveSuccess.postValue(false);
                }

            } catch (JSONException | IOException e) {
                message.postValue("Erreur de connexion au serveur");
            }
        });
    }

    public void inscrireUser(User newUser) {
        executorService.execute(() -> {
            try {
                List<User> existingUsers = UserDao.getUsers();

                if (existingUsers != null) {
                    for (User u : existingUsers) {
                        if (u.getEmail().equalsIgnoreCase(newUser.getEmail())) {
                            message.postValue("Cet email est déjà utilisé.");
                            inscriptionSuccess.postValue(false);
                            return;
                        }
                    }
                }

                boolean reussite = UserDao.inscrire(newUser);
                if (reussite) {
                    message.postValue("Inscription réussie !");
                    inscriptionSuccess.postValue(true);
                } else {
                    message.postValue("Erreur lors de l'inscription.");
                    inscriptionSuccess.postValue(false);
                }

            } catch (Exception e) {
                message.postValue("Erreur réseau ou serveur.");
                inscriptionSuccess.postValue(false);
            }
        });
    }

    public void resetLoginStatus() {
        loginSuccess.setValue(null);
    }
    public void resetSaveStatus() {
        saveSuccess.setValue(null);
        message.setValue(null);
    }
}