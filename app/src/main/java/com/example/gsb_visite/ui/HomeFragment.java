package com.example.gsb_visite.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    @Inject
    VisiteurRepository visiteurRepository;

    private MaterialButton transferPortfolioButton;

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

        transferPortfolioButton.setVisibility(View.GONE);
        transferPortfolioButton.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Transfert de portefeuille à venir", Toast.LENGTH_SHORT).show()
        );

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
                if (!isAdded() || transferPortfolioButton == null) {
                    return;
                }

                transferPortfolioButton.setVisibility(result.isResponsable() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String message) {
                if (!isAdded() || transferPortfolioButton == null) {
                    return;
                }

                transferPortfolioButton.setVisibility(View.GONE);
            }
        });
    }
}
