package com.example.gsb_visite.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginCredentials {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;

    public LoginCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
