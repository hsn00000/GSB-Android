package com.example.gsb_visite.ui.login;

public class LoginResult {
    private final Integer error;
    private final String displayName;

    public LoginResult(Integer error) {
        this.error = error;
        this.displayName = null;
    }

    public LoginResult(String displayName) {
        this.error = null;
        this.displayName = displayName;
    }

    public Integer getError() {
        return error;
    }

    public String getDisplayName() {
        return displayName;
    }
}
