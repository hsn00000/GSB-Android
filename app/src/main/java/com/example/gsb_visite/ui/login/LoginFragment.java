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

import com.example.gsb_visite.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

            if (loginResult.getError() != null) {
                Toast.makeText(requireContext(), loginResult.getError(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(
                        requireContext(),
                        getString(R.string.login_success, loginResult.getDisplayName()),
                        Toast.LENGTH_SHORT
                ).show();
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
