package com.example.gsb_visite.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.gsb_visite.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton loginButton;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        emailLayout = view.findViewById(R.id.email_text_input);
        passwordLayout = view.findViewById(R.id.password_text_input);
        emailEditText = view.findViewById(R.id.email_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        loginButton = view.findViewById(R.id.login_button);
        MaterialButton signupButton = view.findViewById(R.id.signup_button);

        signupButton.setOnClickListener(v -> 
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signupFragment)
        );

        loginButton.setEnabled(false);

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            if (loginFormState == null) {
                return;
            }

            loginButton.setEnabled(loginFormState.isDataValid());
            emailLayout.setError(loginFormState.getEmailError() == null
                    ? null
                    : getString(loginFormState.getEmailError()));
            passwordLayout.setError(loginFormState.getPasswordError() == null
                    ? null
                    : getString(loginFormState.getPasswordError()));
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult == null) {
                return;
            }

            if (loginResult.getError() != null || loginResult.getErrorMessage() != null) {
                String errorMsg = loginResult.getErrorMessage() != null 
                    ? loginResult.getErrorMessage() 
                    : getString(loginResult.getError());
                
                // Affichage d'une Snackbar rouge pour l'erreur
                Snackbar.make(view, errorMsg, Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(android.R.color.holo_red_dark))
                        .setTextColor(getResources().getColor(android.R.color.white))
                        .show();
            } else {
                // Succès
                String welcomeName = loginResult.getDisplayName() != null 
                    ? loginResult.getDisplayName() 
                    : "Visiteur";
                
                Toast.makeText(requireContext(), "Bienvenue " + welcomeName, Toast.LENGTH_SHORT).show();

                // Redirection
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
            }
        });

        View.OnClickListener submitLogin = ignored -> loginViewModel.login(
                getTextValue(emailEditText),
                getTextValue(passwordEditText)
        );

        loginButton.setOnClickListener(submitLogin);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitLogin.onClick(v);
                return true;
            }
            return false;
        });

        View.OnFocusChangeListener validateOnFocusLost = (v, hasFocus) -> {
            if (!hasFocus) {
                notifyDataChanged();
            }
        };
        emailEditText.setOnFocusChangeListener(validateOnFocusLost);
        passwordEditText.setOnFocusChangeListener(validateOnFocusLost);
        emailEditText.addTextChangedListener(new SimpleTextWatcher(this::notifyDataChanged));
        passwordEditText.addTextChangedListener(new SimpleTextWatcher(this::notifyDataChanged));
    }

    private void notifyDataChanged() {
        loginViewModel.loginDataChanged(getTextValue(emailEditText), getTextValue(passwordEditText));
    }

    private String getTextValue(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString();
    }
}
