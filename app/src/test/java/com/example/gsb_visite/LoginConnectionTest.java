package com.example.gsb_visite;

import static org.junit.Assert.assertNotNull;

import com.example.gsb_visite.data.api.ApiService;
import com.example.gsb_visite.data.model.Visiteur;
import com.google.gson.JsonObject;

import org.junit.Test;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Test de vérification de la configuration Retrofit.
 * Ce test vérifie que l'interface ApiService est correctement définie.
 */
public class LoginConnectionTest {

    @Test
    public void testRetrofitConfiguration() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        
        JsonObject request = new JsonObject();
        request.addProperty("email", "test@example.com");
        request.addProperty("password", "password123");
        Call<Visiteur> call = service.login(request);

        assertNotNull("La requête ne doit pas être nulle", call);
        System.out.println("Configuration Retrofit validée : Prêt pour l'envoi vers /login");
    }
}
