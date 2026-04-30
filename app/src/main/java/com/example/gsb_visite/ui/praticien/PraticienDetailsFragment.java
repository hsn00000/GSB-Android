package com.example.gsb_visite.ui.praticien;

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
import com.example.gsb_visite.data.model.Praticien;
import com.google.android.material.button.MaterialButton;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PraticienDetailsFragment extends Fragment {
    public static final String ARG_PRATICIEN_ID = "praticienId";

    private PraticienDetailsViewModel viewModel;
    private ProgressBar progressBar;
    private TextView errorText;
    private LinearLayout contentContainer;
    private LinearLayout detailsContainer;
    private TextView titleText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_praticien_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PraticienDetailsViewModel.class);
        progressBar = view.findViewById(R.id.praticien_progress);
        errorText = view.findViewById(R.id.praticien_error_text);
        contentContainer = view.findViewById(R.id.praticien_content_container);
        detailsContainer = view.findViewById(R.id.praticien_info_container);
        titleText = view.findViewById(R.id.praticien_title_text);

        MaterialButton backButton = view.findViewById(R.id.praticien_back_button);
        backButton.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        viewModel.getState().observe(getViewLifecycleOwner(), this::renderState);

        String praticienId = getArguments() == null ? null : getArguments().getString(ARG_PRATICIEN_ID);
        if (isBlank(praticienId)) {
            renderState(PraticienDetailsState.error("Identifiant du praticien manquant."));
            return;
        }
        viewModel.load(praticienId);
    }

    private void renderState(PraticienDetailsState state) {
        if (state == null) {
            return;
        }

        progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
        errorText.setVisibility(state.getError() == null ? View.GONE : View.VISIBLE);
        contentContainer.setVisibility(state.getPraticien() == null ? View.GONE : View.VISIBLE);

        if (state.getError() != null) {
            errorText.setText(state.getError());
        }
        if (state.getPraticien() != null) {
            renderPraticien(state.getPraticien());
        }
    }

    private void renderPraticien(Praticien praticien) {
        detailsContainer.removeAllViews();
        titleText.setText(praticien.getDisplayName());

        addInfoRow("Spécialité", praticien.getSpecialite());
        addInfoRow("Email", praticien.getEmail());
        addInfoRow("Téléphone", praticien.getTelephone());
        addInfoRow("Adresse", praticien.getAdresse());
        addInfoRow("Ville", join(praticien.getCodePostal(), praticien.getVille()));
    }

    private void addInfoRow(String label, String value) {
        if (isBlank(value)) {
            return;
        }

        TextView row = new TextView(requireContext());
        row.setText(label + " : " + value);
        row.setTextColor(getResources().getColor(R.color.gsb_text_secondary));
        row.setTextSize(15);
        row.setPadding(0, dp(8), 0, 0);
        detailsContainer.addView(row);
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
