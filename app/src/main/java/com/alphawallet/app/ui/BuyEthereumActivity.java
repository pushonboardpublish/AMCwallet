package com.alphawallet.app.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alphawallet.app.C;
import com.alphawallet.app.R;
import com.alphawallet.app.entity.PaymentMethodItem;
import com.alphawallet.app.util.Utils;
import com.alphawallet.app.viewmodel.BuyEthereumViewModel;
import com.alphawallet.app.viewmodel.BuyEthereumViewModelFactory;
import com.alphawallet.app.widget.FunctionButtonBar;
import com.alphawallet.app.widget.SettingsItemView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.alphawallet.app.C.EXTRA_CURRENCY;
import static com.alphawallet.app.C.EXTRA_CURRENT_PAYMENT_METHOD;
import static com.alphawallet.app.C.EXTRA_PAYMENT_METHOD;

public class BuyEthereumActivity extends BaseActivity {
    @Inject
    BuyEthereumViewModelFactory viewModelFactory;
    private BuyEthereumViewModel viewModel;

    private ImageView currencyFlag;
    private TextView currencyText;
    private EditText amountText;
    private SettingsItemView paymentMethodView;
    private FunctionButtonBar functionBar;
    private PaymentMethodItem paymentMethodItem;
    private double amount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BuyEthereumViewModel.class);
        setContentView(R.layout.activity_buy_ethereum);
        toolbar();
        setTitle(getString(R.string.title_buy_ethereum));
        initViews();
    }

    private void initViews() {
        currencyFlag = findViewById(R.id.currency_flag);
        currencyText = findViewById(R.id.currency_text);
        amountText = findViewById(R.id.amount_text);
        paymentMethodView = findViewById(R.id.setting_payment_method);
        functionBar = findViewById(R.id.layoutButtons);
        setupFunctionBar();

        amountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();

                if (input.isEmpty() || input.equals(".")) {
                    amount = 0;
                } else {
                    amount = Double.parseDouble(editable.toString());
                    updateButtonStatus();
                }
            }
        });

        paymentMethodView.highlight();
        currencyText.setOnClickListener(v -> onChangeCurrencyClicked());
        paymentMethodView.setListener(this::onPaymentMethodClicked);
    }

    private void setupFunctionBar() {
        functionBar.setPrimaryButtonText(R.string.action_continue);
        functionBar.setPrimaryButtonClickListener(v -> {
            continueToPaymentDetails();
        });

        updateButtonStatus();
    }

    private void updateButtonStatus() {
        if (paymentMethodItem == null || amount <= 0) {
            functionBar.setPrimaryButtonEnabled(false);
        } else {
            functionBar.setPrimaryButtonEnabled(true);
        }
    }

    private void continueToPaymentDetails() {
        if (paymentMethodItem != null) {
            switch (paymentMethodItem.getCode()) {
                case C.WYRE_DEBIT_CARD:
                    showWyreSplashScreen();
                    break;
                default:
                    break;
            }
        }
    }

    private void showWyreSplashScreen() {
        Intent intent = new Intent(this, WyreSplashActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return false;
    }

    private void onPaymentMethodClicked() {
        Intent intent = new Intent(this, SelectPaymentMethodActivity.class);
        String currentPaymentMethod = viewModel.getDefaultPaymentMethod();
        intent.putExtra(EXTRA_CURRENT_PAYMENT_METHOD, currentPaymentMethod);
        intent.putParcelableArrayListExtra(C.EXTRA_STATE, viewModel.getPaymentMethods());
        startActivityForResult(intent, C.SELECT_PAYMENT_METHOD);
    }

    private void onChangeCurrencyClicked() {
        Intent intent = new Intent(this, SelectCurrencyActivity.class);
        String currentLocale = viewModel.getDefaultCurrency();
        intent.putExtra(EXTRA_CURRENCY, currentLocale);
        intent.putParcelableArrayListExtra(C.EXTRA_STATE, viewModel.getCurrencyList());
        startActivityForResult(intent, C.UPDATE_CURRENCY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case C.UPDATE_CURRENCY: {
                updateCurrency(data);
                break;
            }
            case C.SELECT_PAYMENT_METHOD: {
                updatePaymentMethod(data);
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
                break;
            }
        }
    }

    private void updatePaymentMethod(Intent data) {
        if (data == null) return;
        PaymentMethodItem paymentMethod = (PaymentMethodItem) data.getExtras().get(EXTRA_PAYMENT_METHOD);
        this.paymentMethodItem = paymentMethod;
        paymentMethodView.setTitle(paymentMethod.getName());
        paymentMethodView.setSubtitle(paymentMethod.getDescription());
        paymentMethodView.unhighlight();
        updateButtonStatus();
    }

    private void updateCurrency(Intent data) {
        if (data == null) return;
        String currencyCode = data.getStringExtra(C.EXTRA_CURRENCY);
        currencyFlag.setImageResource(Utils.getFlagIconRes(currencyCode));
        currencyText.setText(currencyCode);
        // TODO: Update Fee
        // TODO: Update Rate
        // TODO: Update Estimated ETH received
    }
}
