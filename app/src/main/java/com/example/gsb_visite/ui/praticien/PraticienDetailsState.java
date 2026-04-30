package com.example.gsb_visite.ui.praticien;

import com.example.gsb_visite.data.model.Praticien;

public class PraticienDetailsState {
    private final boolean loading;
    private final Praticien praticien;
    private final String error;

    private PraticienDetailsState(boolean loading, Praticien praticien, String error) {
        this.loading = loading;
        this.praticien = praticien;
        this.error = error;
    }

    public static PraticienDetailsState loading() {
        return new PraticienDetailsState(true, null, null);
    }

    public static PraticienDetailsState success(Praticien praticien) {
        return new PraticienDetailsState(false, praticien, null);
    }

    public static PraticienDetailsState error(String error) {
        return new PraticienDetailsState(false, null, error);
    }

    public boolean isLoading() {
        return loading;
    }

    public Praticien getPraticien() {
        return praticien;
    }

    public String getError() {
        return error;
    }
}
