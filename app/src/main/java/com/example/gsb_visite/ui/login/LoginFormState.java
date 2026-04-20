package com.example.gsb_visite.ui.login;

public class LoginFormState {
    private final Integer emailError;
    private final Integer passwordError;
    private final boolean dataValid;

    public LoginFormState(Integer emailError, Integer passwordError) {
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.dataValid = false;
    }

    public LoginFormState(boolean dataValid) {
        this.emailError = null;
        this.passwordError = null;
        this.dataValid = dataValid;
    }

    public Integer getEmailError() {
        return emailError;
    }

    public Integer getPasswordError() {
        return passwordError;
    }

    public boolean isDataValid() {
        return dataValid;
    }
}
