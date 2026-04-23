package com.example.gsb_visite.data.repository;

import com.example.gsb_visite.data.api.ApiService;
import com.example.gsb_visite.data.model.LoginRequest;
import com.example.gsb_visite.data.model.LoginResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.gsb_visite.data.model.LoginCredentials;

public class LoginRepository {
    private final ApiService apiService;

    @Inject
    public LoginRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void login(String email, String password, RepositoryCallback<LoginResponse> callback) {
        LoginCredentials credentials = new LoginCredentials(email, password);
        apiService.login(credentials).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Erreur " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " : " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}
