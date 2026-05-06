package com.example.gsb_visite.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.gsb_visite.R;
import com.example.gsb_visite.data.api.ApiService;
import com.example.gsb_visite.data.model.Visiteur;
import com.example.gsb_visite.data.repository.VisiteurRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    @Inject
    VisiteurRepository visiteurRepository;

    private MaterialButton transferPortfolioButton;
    private TextView roleText;
    private TextInputEditText destinationEditText;
    private View destinationLayout;
    private Visiteur currentVisiteur;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton logoutButton = view.findViewById(R.id.home_logout_button);
        MaterialButton visitorInfoButton = view.findViewById(R.id.home_visitor_info_button);
        transferPortfolioButton = view.findViewById(R.id.home_transfer_portfolio_button);
        roleText = view.findViewById(R.id.home_role_text);
        destinationLayout = view.findViewById(R.id.home_destination_layout);
        destinationEditText = view.findViewById(R.id.home_destination_edit);

        roleText.setVisibility(View.GONE);
        destinationLayout.setVisibility(View.GONE);
        transferPortfolioButton.setVisibility(View.GONE);
        transferPortfolioButton.setOnClickListener(v -> transferPortefeuille());

        visitorInfoButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_visiteurDetailsFragment)
        );

        logoutButton.setOnClickListener(v -> {
            ApiService.clearToken();
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_loginFragment);
        });

        loadResponsableButton();
    }

    private void loadResponsableButton() {
        visiteurRepository.getCurrentVisiteur(new VisiteurRepository.RepositoryCallback<Visiteur>() {
            @Override
            public void onSuccess(Visiteur result) {
                if (!isAdded() || roleText == null || destinationLayout == null || transferPortfolioButton == null) {
                    return;
                }

                currentVisiteur = result;
                int visibility = result.isResponsable() ? View.VISIBLE : View.GONE;
                roleText.setVisibility(visibility);
                destinationLayout.setVisibility(visibility);
                transferPortfolioButton.setVisibility(visibility);
            }

            @Override
            public void onError(String message) {
                if (!isAdded() || roleText == null || destinationLayout == null || transferPortfolioButton == null) {
                    return;
                }

                currentVisiteur = null;
                roleText.setVisibility(View.GONE);
                destinationLayout.setVisibility(View.GONE);
                transferPortfolioButton.setVisibility(View.GONE);
            }
        });
    }

    private void transferPortefeuille() {
        if (currentVisiteur == null || isBlank(currentVisiteur.getId())) {
            Toast.makeText(requireContext(), "Visiteur connecté introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        String visiteurDestinataireId = getTextValue(destinationEditText);
        if (isBlank(visiteurDestinataireId)) {
            Toast.makeText(requireContext(), "Saisissez l'id du visiteur destinataire", Toast.LENGTH_SHORT).show();
            return;
        }

        transferPortfolioButton.setEnabled(false);
        visiteurRepository.transferPortefeuille(
                currentVisiteur.getId(),
                visiteurDestinataireId,
                new VisiteurRepository.RepositoryCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (!isAdded() || transferPortfolioButton == null) {
                            return;
                        }

                        transferPortfolioButton.setEnabled(true);
                        Toast.makeText(requireContext(), "Portefeuille transféré avec succès", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String message) {
                        if (!isAdded() || transferPortfolioButton == null) {
                            return;
                        }

                        transferPortfolioButton.setEnabled(true);
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private String getTextValue(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
