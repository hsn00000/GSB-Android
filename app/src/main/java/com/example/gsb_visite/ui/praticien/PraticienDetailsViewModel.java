package com.example.gsb_visite.ui.praticien;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gsb_visite.data.model.Praticien;
import com.example.gsb_visite.data.repository.PraticienRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PraticienDetailsViewModel extends ViewModel {
    private final PraticienRepository praticienRepository;
    private final MutableLiveData<PraticienDetailsState> state = new MutableLiveData<>();

    @Inject
    public PraticienDetailsViewModel(PraticienRepository praticienRepository) {
        this.praticienRepository = praticienRepository;
    }

    public LiveData<PraticienDetailsState> getState() {
        return state;
    }

    public void load(String praticienId) {
        state.setValue(PraticienDetailsState.loading());
        praticienRepository.getPraticien(praticienId, new PraticienRepository.RepositoryCallback<Praticien>() {
            @Override
            public void onSuccess(Praticien result) {
                state.setValue(PraticienDetailsState.success(result));
            }

            @Override
            public void onError(String message) {
                state.setValue(PraticienDetailsState.error(message));
            }
        });
    }
}
