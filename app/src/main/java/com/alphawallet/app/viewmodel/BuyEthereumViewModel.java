package com.alphawallet.app.viewmodel;

import com.alphawallet.app.entity.CurrencyItem;
import com.alphawallet.app.entity.PaymentMethodItem;
import com.alphawallet.app.repository.CurrencyRepositoryType;
import com.alphawallet.app.repository.PaymentMethodRepositoryType;

import java.util.ArrayList;

public class BuyEthereumViewModel extends BaseViewModel {
    private final CurrencyRepositoryType currencyRepository;
    private final PaymentMethodRepositoryType paymentMethodRepository;

    BuyEthereumViewModel(
            CurrencyRepositoryType currencyRepository,
            PaymentMethodRepositoryType paymentMethodRepository) {
        this.currencyRepository = currencyRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public String getDefaultCurrency(){
        return currencyRepository.getDefaultCurrency();
    }

    public ArrayList<CurrencyItem> getCurrencyList() {
        return currencyRepository.getCurrencyList();
    }

    public String getDefaultPaymentMethod() {
        return paymentMethodRepository.getDefaultPaymentMethod();
    }

    public ArrayList<PaymentMethodItem> getPaymentMethods() {
        return paymentMethodRepository.getPaymentMethods();
    }
}
