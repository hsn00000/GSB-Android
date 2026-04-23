package com.example.gsb_visite.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.gsb_visite.R;
import com.example.gsb_visite.data.api.ApiService;
import com.example.gsb_visite.data.model.LoginRequest;
import com.example.gsb_visite.data.model.LoginResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class SignupFragment extends Fragment {

    @Inject
    ApiService apiService;

    private TextInputEditText nomEdit, prenomEdit, emailEdit, phoneEdit, passwordEdit;
    private MaterialButton signupButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nomEdit = view.findViewById(R.id.signup_nom_edit);
        prenomEdit = view.findViewById(R.id.signup_prenom_edit);
        emailEdit = view.findViewById(R.id.signup_email_edit);
        phoneEdit = view.findViewById(R.id.signup_phone_edit);
        passwordEdit = view.findViewById(R.id.signup_password_edit);
        signupButton = view.findViewById(R.id.signup_confirm_button);
        MaterialButton backButton = view.findViewById(R.id.signup_back_button);

        backButton.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        signupButton.setOnClickListener(v -> {
            String nom = nomEdit.getText().toString();
            String prenom = prenomEdit.getText().toString();
            String email = emailEdit.getText().toString();
            String phone = phoneEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest request = new LoginRequest(email, password, nom, prenom, phone);
            apiService.signup(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Compte créé ! Connectez-vous.", Toast.LENGTH_LONG).show();
                        Navigation.findNavController(view).navigateUp(); // Retour au login
                    } else {
                        Toast.makeText(requireContext(), "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(requireContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
