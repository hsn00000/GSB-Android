package com.example.gsb_visite;

import static org.junit.Assert.assertNotNull;

import com.example.gsb_visite.data.api.ApiService;
import com.example.gsb_visite.data.model.LoginRequest;
import com.example.gsb_visite.data.model.LoginResponse;

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
        
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        Call<LoginResponse> call = service.login(request);

        assertNotNull("La requête ne doit pas être nulle", call);
        System.out.println("Configuration Retrofit validée : Prêt pour l'envoi vers /login");
    }
}
