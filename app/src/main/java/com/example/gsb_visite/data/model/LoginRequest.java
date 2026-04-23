package com.example.gsb_visite.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("nom")
    private String nom;
    @SerializedName("prenom")
    private String prenom;
    @SerializedName("telephone")
    private String telephone;

    public LoginRequest(String email, String password, String nom, String prenom, String telephone) {
        this.email = email;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }
}
