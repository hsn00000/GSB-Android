package com.example.gsb_visite.ui.visitor;

import com.example.gsb_visite.data.model.Visiteur;

public class VisiteurDetailsState {
    private final boolean loading;
    private final Visiteur visiteur;
    private final String error;

    private VisiteurDetailsState(boolean loading, Visiteur visiteur, String error) {
        this.loading = loading;
        this.visiteur = visiteur;
        this.error = error;
    }

    public static VisiteurDetailsState loading() {
        return new VisiteurDetailsState(true, null, null);
    }

    public static VisiteurDetailsState success(Visiteur visiteur) {
        return new VisiteurDetailsState(false, visiteur, null);
    }

    public static VisiteurDetailsState error(String error) {
        return new VisiteurDetailsState(false, null, error);
    }

    public boolean isLoading() {
        return loading;
    }

    public Visiteur getVisiteur() {
        return visiteur;
    }

    public String getError() {
        return error;
    }
}
