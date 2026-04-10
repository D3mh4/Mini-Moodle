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
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public LiveData<User> getUser() {
        return user;
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

    public void authentifierUser(String email, String password){
        executorService.execute(() -> {
            try {
                List<User> users = UserDao.getUsers();

                if (users != null) {
                    for (User u : users) {
                        if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                            user.postValue(u);
                            loginSuccess.postValue(true);
                            return;
                        }
                    }
                }

                message.postValue("Identifiants incorrects");
                loginSuccess.postValue(false);

            } catch (JSONException | IOException e) {
                message.postValue("Erreur lors du chargement");
                loginSuccess.postValue(false);
            }
        });
    }

    public void chargerUserParId(String id){
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
                message.postValue("Erreur lors de la modification");
            }
        });
    }


}