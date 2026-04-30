package com.example.gsb_visite.data.repository;

import com.example.gsb_visite.data.api.ApiService;
import com.example.gsb_visite.data.model.Praticien;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PraticienRepository {
    private final ApiService apiService;
    private final Gson gson = new Gson();

    @Inject
    public PraticienRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void getPraticien(String praticienId, RepositoryCallback<Praticien> callback) {
        apiService.getPraticien(praticienId).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(buildErrorMessage(response));
                    return;
                }

                Praticien praticien = parsePraticien(response.body());
                if (praticien == null) {
                    callback.onError("Impossible de lire les informations du praticien.");
                    return;
                }
                if (isBlank(praticien.getId())) {
                    praticien.setId(praticienId);
                }
                callback.onSuccess(praticien);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }

    private Praticien parsePraticien(JsonElement element) {
        JsonObject object = unwrapObject(element, "praticien", "medecin", "data");
        return object == null ? null : gson.fromJson(object, Praticien.class);
    }

    private JsonObject unwrapObject(JsonElement element, String... keys) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }

        JsonObject object = element.getAsJsonObject();
        boolean foundNestedObject;
        do {
            foundNestedObject = false;
            for (String key : keys) {
                JsonElement nested = object.get(key);
                if (nested != null && nested.isJsonObject()) {
                    object = nested.getAsJsonObject();
                    foundNestedObject = true;
                    break;
                }
            }
        } while (foundNestedObject);
        return object;
    }

    private String buildErrorMessage(Response<?> response) {
        String errorMsg = "Erreur " + response.code();
        try {
            if (response.errorBody() != null) {
                errorMsg += " : " + response.errorBody().string();
            }
        } catch (Exception e) {
            // Le code HTTP suffit si le corps d'erreur n'est pas lisible.
        }
        return errorMsg;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}
