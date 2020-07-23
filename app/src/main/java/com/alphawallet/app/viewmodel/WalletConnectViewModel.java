package com.alphawallet.app.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.alphawallet.app.entity.Wallet;
import com.alphawallet.app.interact.GenericWalletInteract;

import io.reactivex.disposables.Disposable;

public class WalletConnectViewModel extends BaseViewModel {
    private final MutableLiveData<Wallet> defaultWallet = new MutableLiveData<>();
    protected Disposable disposable;
    private GenericWalletInteract genericWalletInteract;

    WalletConnectViewModel(GenericWalletInteract genericWalletInteract) {
        this.genericWalletInteract = genericWalletInteract;
    }

    public void prepare() {
        disposable = genericWalletInteract
                .find()
                .subscribe(this::onDefaultWallet,
                        this::onError);
    }

    private void onDefaultWallet(Wallet wallet) {
        defaultWallet.postValue(wallet);
    }

    public LiveData<Wallet> defaultWallet() {
        return defaultWallet;
    }
}
