package com.example.gsb_visite.ui.login;

public class LoginResult {
    private final Integer errorResId;
    private final String errorMessage;
    private final String displayName;

    public LoginResult(Integer errorResId) {
        this.errorResId = errorResId;
        this.errorMessage = null;
        this.displayName = null;
    }

    public LoginResult(String displayNameOrError, boolean isError) {
        if (isError) {
            this.errorMessage = displayNameOrError;
            this.displayName = null;
        } else {
            this.errorMessage = null;
            this.displayName = displayNameOrError;
        }
        this.errorResId = null;
    }

    public Integer getError() { return errorResId; }
    public String getErrorMessage() { return errorMessage; }
    public String getDisplayName() { return displayName; }
}
