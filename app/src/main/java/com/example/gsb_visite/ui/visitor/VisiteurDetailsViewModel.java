package com.example.gsb_visite.ui.visitor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gsb_visite.data.model.Visiteur;
import com.example.gsb_visite.data.repository.VisiteurRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class VisiteurDetailsViewModel extends ViewModel {
    private final VisiteurRepository visiteurRepository;
    private final MutableLiveData<VisiteurDetailsState> state = new MutableLiveData<>();

    @Inject
    public VisiteurDetailsViewModel(VisiteurRepository visiteurRepository) {
        this.visiteurRepository = visiteurRepository;
    }

    public LiveData<VisiteurDetailsState> getState() {
        return state;
    }

    public void load() {
        state.setValue(VisiteurDetailsState.loading());
        visiteurRepository.getCurrentVisiteurWithPortefeuille(new VisiteurRepository.RepositoryCallback<Visiteur>() {
            @Override
            public void onSuccess(Visiteur result) {
                state.setValue(VisiteurDetailsState.success(result));
            }

            @Override
            public void onError(String message) {
                state.setValue(VisiteurDetailsState.error(message));
            }
        });
    }
}
