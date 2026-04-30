package com.example.gsb_visite.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.gsb_visite.R;
import com.example.gsb_visite.data.model.Visiteur;
import com.google.android.material.button.MaterialButton;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
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

        visitorInfoButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_visiteurDetailsFragment)
        );

        logoutButton.setOnClickListener(v -> {
            Visiteur.clearToken();
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_loginFragment);
        });
    }
}
