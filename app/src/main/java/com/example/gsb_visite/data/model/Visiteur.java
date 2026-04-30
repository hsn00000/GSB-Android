package com.example.gsb_visite.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Visiteur {
    private static String currentToken;

    @SerializedName(
            value = "id",
            alternate = {"_id", "idVisiteur", "visiteurId", "id_visiteur", "visiteur_id", "userId", "user_id", "sub", "matricule"}
    )
    private String id;
    @SerializedName("nom")
    private String nom;
    @SerializedName("prenom")
    private String prenom;
    @SerializedName("email")
    private String email;
    @SerializedName("telephone")
    private String telephone;
    @SerializedName("phone")
    private String phone;
    @SerializedName("username")
    private String username;
    @SerializedName("token")
    private String token;
    @SerializedName("adresse")
    private String adresse;
    @SerializedName("ville")
    private String ville;
    @SerializedName("cp")
    private String codePostal;
    private List<Portefeuille> portefeuille = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return firstNotBlank(telephone, phone);
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getVille() {
        return ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public String getDisplayName() {
        String fullName = join(prenom, nom);
        return isBlank(fullName) ? firstNotBlank(username, email, "Visiteur") : fullName;
    }

    public List<Portefeuille> getPortefeuille() {
        return portefeuille;
    }

    public void setPortefeuille(List<Portefeuille> portefeuille) {
        this.portefeuille = portefeuille == null ? new ArrayList<>() : portefeuille;
    }

    public static void saveToken(String token) {
        currentToken = token;
    }

    public static String getCurrentToken() {
        return currentToken;
    }

    public static void clearToken() {
        currentToken = null;
    }

    private static String join(String first, String second) {
        StringBuilder builder = new StringBuilder();
        if (!isBlank(first)) {
            builder.append(first.trim());
        }
        if (!isBlank(second)) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(second.trim());
        }
        return builder.toString();
    }

    private static String firstNotBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
