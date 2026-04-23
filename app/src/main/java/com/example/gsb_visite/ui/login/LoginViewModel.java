package com.example.gsb_visite.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gsb_visite.R;
import com.example.gsb_visite.data.model.LoginResponse;
import com.example.gsb_visite.data.repository.LoginRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ViewModel {
    private static final int PASSWORD_MIN_LENGTH = 6;

    private final LoginRepository loginRepository;
    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    @Inject
    public LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        if (!isEmailValid(email) || !isPasswordValid(password)) {
            loginDataChanged(email, password);
            return;
        }

        loginRepository.login(email, password, new LoginRepository.RepositoryCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                loginResult.setValue(new LoginResult(result.getUsername(), false));
            }

            @Override
            public void onError(String message) {
                loginResult.setValue(new LoginResult(message, true));
            }
        });
    }

    public void loginDataChanged(String email, String password) {
        if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(R.string.login_invalid_email, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.login_invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= PASSWORD_MIN_LENGTH;
    }
}
