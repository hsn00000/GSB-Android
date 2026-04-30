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
    private final ApiService apiService;
    private final Gson gson = new Gson();

    @Inject
    public VisiteurRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void getCurrentVisiteurDetails(RepositoryCallback<Visiteur> callback) {
        apiService.getCurrentVisiteur().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(buildErrorMessage(response));
                    return;
                }

                Visiteur visiteur = parseVisiteur(response.body());
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

                visiteur.setPortefeuille(parsePortefeuille(response.body()));
                callback.onSuccess(visiteur);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }

    private Visiteur parseVisiteur(JsonElement element) {
        JsonObject object = unwrapObject(element, "visiteur", "user", "data");
        if (object == null) {
            return null;
        }

        Visiteur visiteur = gson.fromJson(object, Visiteur.class);
        if (visiteur != null && isBlank(visiteur.getId())) {
            visiteur.setId(findStringValue(
                    element,
                    "id", "_id", "idVisiteur", "visiteurId", "id_visiteur", "visiteur_id",
                    "userId", "user_id", "sub", "matricule"
            ));
        }
        return visiteur;
    }

    private List<Portefeuille> parsePortefeuille(JsonElement element) {
        List<Portefeuille> items = new ArrayList<>();
        JsonArray array = unwrapArray(element, "portefeuille", "data", "items", "medecins", "contacts");
        if (array == null) {
            return items;
        }

        for (JsonElement itemElement : array) {
            JsonObject itemObject = unwrapObject(itemElement, "praticien", "medecin", "contact", "client");
            if (itemObject != null) {
                Portefeuille item = gson.fromJson(itemObject, Portefeuille.class);
                if (item != null && isBlank(item.getId())) {
                    item.setId(findStringValue(
                            itemElement,
                            "id", "_id", "idPraticien", "praticienId", "id_praticien", "praticien_id"
                    ));
                }
                if (item != null) {
                    items.add(item);
                }
            }
        }
        return items;
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

    private String findStringValue(JsonElement element, String... keys) {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (String key : keys) {
                JsonElement value = object.get(key);
                if (value != null && value.isJsonPrimitive()) {
                    return value.getAsString();
                }
            }
            for (String key : object.keySet()) {
                String value = findStringValue(object.get(key), keys);
                if (!isBlank(value)) {
                    return value;
                }
            }
        }

        if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                String value = findStringValue(item, keys);
                if (!isBlank(value)) {
                    return value;
                }
            }
        }

        return null;
    }

    private JsonArray unwrapArray(JsonElement element, String... keys) {
        if (element == null) {
            return null;
        }
        if (element.isJsonArray()) {
            return element.getAsJsonArray();
        }
        if (!element.isJsonObject()) {
            return null;
        }

        JsonObject object = element.getAsJsonObject();
        for (String key : keys) {
            JsonElement nested = object.get(key);
            if (nested != null && nested.isJsonArray()) {
                return nested.getAsJsonArray();
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
