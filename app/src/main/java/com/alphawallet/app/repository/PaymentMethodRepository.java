package com.alphawallet.app.repository;

import com.alphawallet.app.C;
import com.alphawallet.app.entity.PaymentMethodItem;

import java.util.ArrayList;
import java.util.Arrays;

public class PaymentMethodRepository implements PaymentMethodRepositoryType {
    private static final PaymentMethodItem[] PAYMENT_METHODS = {
            new PaymentMethodItem(
                    "Debit Card",
                    C.WYRE_DEBIT_CARD,
                    "Wyre",
                    true),
            new PaymentMethodItem(
                    "Bank Transfer",
                    C.WYRE_BANK_TRANSFER,
                    "Coming soon!",
                    false),
            new PaymentMethodItem(
                    "Google Pay",
                    C.GOOGLE_PAY,
                    "Coming soon!",
                    false),
            new PaymentMethodItem(
                    "Samsung Pay",
                    C.SAMSUNG_PAY,
                    "Coming soon!",
                    false)
    };

    private final PreferenceRepositoryType preferences;

    public PaymentMethodRepository(PreferenceRepositoryType preferenceRepository) {
        this.preferences = preferenceRepository;
    }

    @Override
    public String getDefaultPaymentMethod() {
        return preferences.getDefaultPaymentMethod();
    }

    @Override
    public void setDefaultPaymentMethod(PaymentMethodItem paymentMethod) {
        preferences.setDefaultPaymentMethod(paymentMethod);
    }

    @Override
    public ArrayList<PaymentMethodItem> getPaymentMethods() {
        return new ArrayList<>(Arrays.asList(PAYMENT_METHODS));
    }
}
