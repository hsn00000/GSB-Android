package com.example.gsb_visite.data.repository;

import com.example.gsb_visite.data.api.ApiService;
import com.example.gsb_visite.data.model.Visiteur;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    private final ApiService apiService;

    @Inject
    public LoginRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void login(String email, String password, RepositoryCallback<Visiteur> callback) {
        JsonObject credentials = new JsonObject();
        credentials.addProperty("email", email);
        credentials.addProperty("password", password);

        apiService.login(credentials).enqueue(new Callback<Visiteur>() {
            @Override
            public void onResponse(Call<Visiteur> call, Response<Visiteur> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Visiteur visiteur = response.body();
                    if (visiteur.getToken() != null && !visiteur.getToken().trim().isEmpty()) {
                        Visiteur.saveToken(visiteur.getToken());
                    }
                    callback.onSuccess(visiteur);
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
            public void onFailure(Call<Visiteur> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}
