package com.example.gsb_visite.data.repository;

import com.example.gsb_visite.data.api.ApiService;
import com.example.gsb_visite.data.model.Portefeuille;
import com.example.gsb_visite.data.model.Visiteur;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisiteurRepository {
    private static final String[] VISITEUR_KEYS = {"visiteur", "user", "data"};
    private static final String[] VISITEUR_ID_KEYS = {
            "id", "_id", "idVisiteur", "visiteurId", "id_visiteur", "visiteur_id",
            "userId", "user_id", "sub", "matricule"
    };
    private static final String[] PORTEFEUILLE_KEYS = {"portefeuille", "data", "items", "medecins", "contacts"};
    private static final String[] PORTEFEUILLE_ITEM_KEYS = {"praticien", "medecin", "contact", "client"};
    private static final String[] PRATICIEN_ID_KEYS = {
            "id", "_id", "idPraticien", "praticienId", "id_praticien", "praticien_id"
    };

    private final ApiService apiService;
    private final Gson gson = new Gson();

    @Inject
    public VisiteurRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void getCurrentVisiteurWithPortefeuille(RepositoryCallback<Visiteur> callback) {
        apiService.getCurrentVisiteur().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(buildErrorMessage(response));
                    return;
                }

                Visiteur visiteur = getVisiteurFrom(response.body());
                if (visiteur == null || isBlank(visiteur.getId())) {
                    callback.onError("L'API a répondu, mais aucun identifiant visiteur n'a été trouvé dans /me.");
                    return;
                }

                loadPortefeuille(visiteur, callback);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }

    private void loadPortefeuille(Visiteur visiteur, RepositoryCallback<Visiteur> callback) {
        apiService.getPortefeuille(visiteur.getId()).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful()) {
                    callback.onError(buildErrorMessage(response));
                    return;
                }

                visiteur.setPortefeuille(getPortefeuilleFrom(response.body()));
                callback.onSuccess(visiteur);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }

    private Visiteur getVisiteurFrom(JsonElement json) {
        JsonObject visiteurJson = findObject(json, VISITEUR_KEYS);
        if (visiteurJson == null) {
            return null;
        }

        Visiteur visiteur = gson.fromJson(visiteurJson, Visiteur.class);
        if (visiteur != null && isBlank(visiteur.getId())) {
            visiteur.setId(findString(json, VISITEUR_ID_KEYS));
        }
        return visiteur;
    }

    private List<Portefeuille> getPortefeuilleFrom(JsonElement json) {
        List<Portefeuille> portefeuille = new ArrayList<>();
        JsonArray portefeuilleJson = findArray(json, PORTEFEUILLE_KEYS);
        if (portefeuilleJson == null) {
            return portefeuille;
        }

        for (JsonElement itemJson : portefeuilleJson) {
            Portefeuille item = getPortefeuilleItemFrom(itemJson);
            if (item != null) {
                portefeuille.add(item);
            }
        }
        return portefeuille;
    }

    private Portefeuille getPortefeuilleItemFrom(JsonElement json) {
        JsonObject itemJson = findObject(json, PORTEFEUILLE_ITEM_KEYS);
        if (itemJson == null) {
            return null;
        }

        Portefeuille item = gson.fromJson(itemJson, Portefeuille.class);
        if (item != null && isBlank(item.getId())) {
            item.setId(findString(json, PRATICIEN_ID_KEYS));
        }
        return item;
    }

    private JsonObject findObject(JsonElement json, String... possibleKeys) {
        if (json == null || !json.isJsonObject()) {
            return null;
        }

        JsonObject object = json.getAsJsonObject();
        for (String key : possibleKeys) {
            JsonElement nested = object.get(key);
            if (nested != null && nested.isJsonObject()) {
                return findObject(nested, possibleKeys);
            }
        }
        return object;
    }

    private JsonArray findArray(JsonElement json, String... possibleKeys) {
        if (json == null) {
            return null;
        }
        if (json.isJsonArray()) {
            return json.getAsJsonArray();
        }
        if (!json.isJsonObject()) {
            return null;
        }

        JsonObject object = json.getAsJsonObject();
        for (String key : possibleKeys) {
            JsonElement value = object.get(key);
            if (value == null) {
                continue;
            }
            if (value.isJsonArray()) {
                return value.getAsJsonArray();
            }
            if (value.isJsonObject()) {
                JsonArray nestedArray = findArray(value, possibleKeys);
                if (nestedArray != null) {
                    return nestedArray;
                }
            }
        }
        return null;
    }

    private String findString(JsonElement json, String... possibleKeys) {
        if (json == null || json.isJsonNull()) {
            return null;
        }

        if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            for (String key : possibleKeys) {
                JsonElement value = object.get(key);
                if (value != null && value.isJsonPrimitive()) {
                    return value.getAsString();
                }
            }

            for (String key : object.keySet()) {
                String value = findString(object.get(key), possibleKeys);
                if (!isBlank(value)) {
                    return value;
                }
            }
        }

        if (json.isJsonArray()) {
            for (JsonElement item : json.getAsJsonArray()) {
                String value = findString(item, possibleKeys);
                if (!isBlank(value)) {
                    return value;
                }
            }
        }
        return null;
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
