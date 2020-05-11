package com.alphawallet.app.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.alphawallet.app.repository.CurrencyRepositoryType;
import com.alphawallet.app.repository.PaymentMethodRepositoryType;

public class BuyEthereumViewModelFactory implements ViewModelProvider.Factory {
    private final CurrencyRepositoryType currencyRepository;
    private final PaymentMethodRepositoryType paymentMethodRepository;

    public BuyEthereumViewModelFactory(
            CurrencyRepositoryType currencyRepository,
            PaymentMethodRepositoryType paymentMethodRepository) {
        this.currencyRepository = currencyRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BuyEthereumViewModel(
                currencyRepository,
                paymentMethodRepository
        );
    }
}
