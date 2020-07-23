package com.alphawallet.app.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.alphawallet.app.interact.GenericWalletInteract;

import javax.inject.Inject;

public class WalletConnectViewModelFactory implements ViewModelProvider.Factory {
    private final GenericWalletInteract genericWalletInteract;

    @Inject
    public WalletConnectViewModelFactory(
            GenericWalletInteract genericWalletInteract) {
        this.genericWalletInteract = genericWalletInteract;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WalletConnectViewModel(
                genericWalletInteract);
    }
}
