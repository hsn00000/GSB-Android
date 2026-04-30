package com.example.gsb_visite.data.model;

import com.google.gson.annotations.SerializedName;

public class Portefeuille {
    @SerializedName(
            value = "id",
            alternate = {"_id", "idPraticien", "praticienId", "id_praticien", "praticien_id"}
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
    @SerializedName("specialite")
    private String specialite;
    @SerializedName("speciality")
    private String speciality;
    @SerializedName("adresse")
    private String adresse;
    @SerializedName("ville")
    private String ville;
    @SerializedName("cp")
    private String codePostal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return firstNotBlank(telephone, phone);
    }

    public String getSpecialite() {
        return firstNotBlank(specialite, speciality);
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
        return isBlank(fullName) ? firstNotBlank(email, "Praticien") : fullName;
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
