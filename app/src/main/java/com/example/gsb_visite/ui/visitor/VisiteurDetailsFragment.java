package com.example.gsb_visite.ui.visitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.gsb_visite.R;
import com.example.gsb_visite.data.model.Portefeuille;
import com.example.gsb_visite.data.model.Visiteur;
import com.example.gsb_visite.ui.praticien.PraticienDetailsFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class VisiteurDetailsFragment extends Fragment {
    private VisiteurDetailsViewModel viewModel;
    private ProgressBar progressBar;
    private TextView errorText;
    private LinearLayout contentContainer;
    private LinearLayout profileContainer;
    private LinearLayout portfolioContainer;
    private TextView portfolioEmptyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visiteur_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(VisiteurDetailsViewModel.class);
        progressBar = view.findViewById(R.id.visiteur_progress);
        errorText = view.findViewById(R.id.visiteur_error_text);
        contentContainer = view.findViewById(R.id.visiteur_content_container);
        profileContainer = view.findViewById(R.id.visiteur_profile_container);
        portfolioContainer = view.findViewById(R.id.visiteur_portfolio_container);
        portfolioEmptyText = view.findViewById(R.id.visiteur_portfolio_empty_text);

        MaterialButton backButton = view.findViewById(R.id.visiteur_back_button);
        backButton.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        viewModel.getState().observe(getViewLifecycleOwner(), this::renderState);
        viewModel.load();
    }

    private void renderState(VisiteurDetailsState state) {
        if (state == null) {
            return;
        }

        progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
        errorText.setVisibility(state.getError() == null ? View.GONE : View.VISIBLE);
        contentContainer.setVisibility(state.getVisiteur() == null ? View.GONE : View.VISIBLE);

        if (state.getError() != null) {
            errorText.setText(state.getError());
        }
        if (state.getVisiteur() != null) {
            renderDetails(state.getVisiteur());
        }
    }

    private void renderDetails(Visiteur visiteur) {
        profileContainer.removeAllViews();
        portfolioContainer.removeAllViews();

        addInfoRow(profileContainer, "Nom", visiteur.getDisplayName());
        addInfoRow(profileContainer, "Email", visiteur.getEmail());
        addInfoRow(profileContainer, "Téléphone", visiteur.getTelephone());
        addInfoRow(profileContainer, "Adresse", visiteur.getAdresse());
        addInfoRow(profileContainer, "Ville", join(visiteur.getCodePostal(), visiteur.getVille()));

        List<Portefeuille> portefeuille = visiteur.getPortefeuille();
        portfolioEmptyText.setVisibility(portefeuille == null || portefeuille.isEmpty() ? View.VISIBLE : View.GONE);
        if (portefeuille == null) {
            return;
        }

        for (Portefeuille item : portefeuille) {
            portfolioContainer.addView(createPortfolioCard(item));
        }
    }

    private View createPortfolioCard(Portefeuille item) {
        MaterialCardView card = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, dp(8), 0, 0);
        card.setLayoutParams(cardParams);
        card.setRadius(dp(8));
        card.setCardElevation(dp(1));
        card.setStrokeWidth(1);
        card.setStrokeColor(getResources().getColor(R.color.gsb_accent));
        if (!isBlank(item.getId())) {
            card.setClickable(true);
            card.setFocusable(true);
            card.setOnClickListener(v -> openPraticienDetails(item.getId()));
        }

        LinearLayout content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.addView(content);

        TextView title = new TextView(requireContext());
        title.setText(item.getDisplayName());
        title.setTextColor(getResources().getColor(R.color.gsb_text_primary));
        title.setTextSize(17);
        title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);
        content.addView(title);

        addInfoRow(content, "Spécialité", item.getSpecialite());
        addInfoRow(content, "Email", item.getEmail());
        addInfoRow(content, "Téléphone", item.getTelephone());
        addInfoRow(content, "Adresse", item.getAdresse());
        addInfoRow(content, "Ville", join(item.getCodePostal(), item.getVille()));

        return card;
    }

    private void openPraticienDetails(String praticienId) {
        Bundle args = new Bundle();
        args.putString(PraticienDetailsFragment.ARG_PRATICIEN_ID, praticienId);
        Navigation.findNavController(requireView()).navigate(
                R.id.action_visiteurDetailsFragment_to_praticienDetailsFragment,
                args
        );
    }

    private void addInfoRow(LinearLayout parent, String label, String value) {
        if (isBlank(value)) {
            return;
        }

        TextView row = new TextView(requireContext());
        row.setText(label + " : " + value);
        row.setTextColor(getResources().getColor(R.color.gsb_text_secondary));
        row.setTextSize(15);
        row.setPadding(0, dp(6), 0, 0);
        parent.addView(row);
    }

    private String join(String first, String second) {
        if (isBlank(first)) {
            return second;
        }
        if (isBlank(second)) {
            return first;
        }
        return first.trim() + " " + second.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
