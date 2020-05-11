package com.alphawallet.app.repository;

import com.alphawallet.app.entity.PaymentMethodItem;

import java.util.ArrayList;

public interface PaymentMethodRepositoryType {
    String getDefaultPaymentMethod();

    void setDefaultPaymentMethod(PaymentMethodItem paymentMethod);

    ArrayList<PaymentMethodItem> getPaymentMethods();
}
