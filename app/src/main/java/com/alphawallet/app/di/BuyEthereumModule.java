package com.alphawallet.app.di;

import com.alphawallet.app.repository.CurrencyRepository;
import com.alphawallet.app.repository.CurrencyRepositoryType;
import com.alphawallet.app.repository.PaymentMethodRepository;
import com.alphawallet.app.repository.PaymentMethodRepositoryType;
import com.alphawallet.app.repository.PreferenceRepositoryType;
import com.alphawallet.app.viewmodel.BuyEthereumViewModelFactory;

import dagger.Module;
import dagger.Provides;

@Module
class BuyEthereumModule {
    @Provides
    BuyEthereumViewModelFactory provideBuyEthereumViewModelFactory(
            CurrencyRepositoryType currencyRepository,
            PaymentMethodRepositoryType paymentMethodRepository
    ) {
        return new BuyEthereumViewModelFactory(
                currencyRepository,
                paymentMethodRepository);
    }

    @Provides
    CurrencyRepositoryType provideCurrencyRepository(PreferenceRepositoryType preferenceRepository) {
        return new CurrencyRepository(preferenceRepository);
    }

    @Provides
    PaymentMethodRepositoryType providePaymentMethodRepository(PreferenceRepositoryType preferenceRepository) {
        return new PaymentMethodRepository(preferenceRepository);
    }
}
