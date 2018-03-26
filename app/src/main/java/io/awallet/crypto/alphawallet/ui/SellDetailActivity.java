package io.awallet.crypto.alphawallet.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.awallet.crypto.alphawallet.R;
import io.awallet.crypto.alphawallet.entity.Ticket;
import io.awallet.crypto.alphawallet.entity.Wallet;
import io.awallet.crypto.alphawallet.ui.widget.adapter.TicketAdapter;
import io.awallet.crypto.alphawallet.ui.widget.entity.TicketRange;
import io.awallet.crypto.alphawallet.util.BalanceUtils;
import io.awallet.crypto.alphawallet.util.KeyboardUtils;
import io.awallet.crypto.alphawallet.viewmodel.BaseViewModel;
import io.awallet.crypto.alphawallet.viewmodel.SellDetailModel;
import io.awallet.crypto.alphawallet.viewmodel.SellDetailModelFactory;
import io.awallet.crypto.alphawallet.widget.AWalletConfirmationDialog;
import io.awallet.crypto.alphawallet.widget.ProgressView;
import io.awallet.crypto.alphawallet.widget.SystemView;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static io.awallet.crypto.alphawallet.C.EXTRA_TOKENID_LIST;
import static io.awallet.crypto.alphawallet.C.Key.TICKET;
import static io.awallet.crypto.alphawallet.C.Key.WALLET;

/**
 * Created by James on 21/02/2018.
 */

public class SellDetailActivity extends BaseActivity
{
    @Inject
    protected SellDetailModelFactory viewModelFactory;
    protected SellDetailModel viewModel;
    private SystemView systemView;
    private ProgressView progressView;

    private Ticket ticket;
    private TicketRange ticketRange;
    private TicketAdapter adapter;
    private TextView usdPrice;
    private Button sell;

    private EditText sellPrice;
    private TextView textQuantity;
    private String ticketIds;
    private double ethToUsd;

    private TextView totalCostText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        ticket = getIntent().getParcelableExtra(TICKET);
        Wallet wallet = getIntent().getParcelableExtra(WALLET);
        ticketIds = getIntent().getStringExtra(EXTRA_TOKENID_LIST);

        setContentView(R.layout.activity_set_price); //use token just provides a simple list view.

        //we should import a token and a list of chosen ids
        RecyclerView list = findViewById(R.id.listTickets);
        sell = findViewById(R.id.button_sell);
        usdPrice = findViewById(R.id.fiat_price);

        adapter = new TicketAdapter(this::onTicketIdClick, ticket, ticketIds);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        toolbar();

//        setTitle(getString(R.string.action_sell));
        setTitle(getString(R.string.empty));

        systemView = findViewById(R.id.system_view);
        systemView.hide();

        progressView = findViewById(R.id.progress_view);
        progressView.hide();

        sellPrice = findViewById(R.id.asking_price);

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SellDetailModel.class);

        viewModel.setWallet(wallet);

        viewModel.progress().observe(this, systemView::showProgress);
        viewModel.queueProgress().observe(this, progressView::updateProgress);
        viewModel.pushToast().observe(this, this::displayToast);
        viewModel.ethereumPrice().observe(this, this::onEthereumPrice);

        totalCostText = findViewById(R.id.eth_price);
        textQuantity = findViewById(R.id.text_quantity);

        sellPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int quantity = Integer.parseInt(textQuantity.getText().toString());
                    updateSellPrice(quantity);
                }
                catch (NumberFormatException e)
                {
                    //silent fail, just don't update
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RelativeLayout plusButton = findViewById(R.id.layout_quantity_add);
        plusButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textQuantity.getText().toString());
            if ((quantity+1) <= adapter.getTicketRangeCount()) {
                quantity++;
                textQuantity.setText(String.valueOf(quantity));
                updateSellPrice(quantity);
            }
        });

        RelativeLayout minusButton = findViewById(R.id.layout_quantity_minus);
        minusButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textQuantity.getText().toString());
            if ((quantity-1) >= 0) {
                quantity--;
                textQuantity.setText(String.valueOf(quantity));
                updateSellPrice(quantity);
            }
        });

        sell.setOnClickListener((View v) -> {
            if (Integer.parseInt(textQuantity.getText().toString()) > 0
                    && !sellPrice.getText().toString().isEmpty()
                    && Double.parseDouble(sellPrice.getText().toString()) > 0) {
                AWalletConfirmationDialog dialog = new AWalletConfirmationDialog(this);
                dialog.setTitle(R.string.confirm_sale_title);
                dialog.setSmallText(R.string.confirm_sale_small_text);
                dialog.setBigText(totalCostText.getText().toString());
                dialog.setPrimaryButtonText(R.string.action_sell);
                dialog.setSecondaryButtonText(R.string.dialog_cancel_back);
                dialog.setPrimaryButtonListener(v1 -> sellTicketFinal());
                dialog.setSecondaryButtonListener(v1 -> dialog.dismiss());
                dialog.show();
            }
        });
    }

    private void onEthereumPrice(Double aDouble)
    {
        ethToUsd = aDouble;
        //see if there's a non-zero value in the eth field
        updateUSDBalance();
    }

    private void updateUSDBalance()
    {
        try
        {
            int quantity = Integer.parseInt(textQuantity.getText().toString());
            double ethPrice = Double.parseDouble(sellPrice.getText().toString());
            if (quantity > 0 && ethPrice > 0) {
                double usdValue = ethPrice * ethToUsd * (double) quantity;
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                String usdText = "$" + df.format(usdValue);
                usdPrice.setText(usdText);
            }
        }
        catch (NumberFormatException e)
        {

        }
    }

    private void updateSellPrice(int quantity)
    {
        if (!sellPrice.getText().toString().isEmpty()) {
            try {
                double totalCost = quantity * Double.parseDouble(sellPrice.getText().toString());
                totalCostText.setText(getString(R.string.total_cost, String.valueOf(totalCost)));
                updateUSDBalance();
            }
            catch (NumberFormatException e)
            {
                //silent fail, just don't update
            }
        }
    }

    private void sellTicketFinal()
    {
        if (!isValidAmount(sellPrice.getText().toString())) {
            return;
        }

        //1. validate price
        BigInteger price = getPriceInWei();
        //2. get indicies
        int[] indices = ticket.getTicketIndicies(ticketIds);
        int quantity = Integer.parseInt(textQuantity.getText().toString());

        if (price.doubleValue() > 0.0 && indices != null && quantity > 0)
        {
            //get the specific ID's, pick from the start of the run
            int[] prunedIndices = Arrays.copyOfRange(indices, 0, quantity);
            List<Integer> ticketIdList = ticket.parseIDListInteger(ticketIds);
            BigInteger totalValue = price.multiply(BigInteger.valueOf(quantity));
            viewModel.generateSalesOrders(ticket.getAddress(), totalValue, prunedIndices, ticketIdList.get(0));
            finish();
        }

        KeyboardUtils.hideKeyboard(getCurrentFocus());
        //go back to previous screen
    }

    private BigInteger getPriceInWei()
    {
        String textPrice = sellPrice.getText().toString();

        //convert to a double value
        double value = Double.valueOf(textPrice);

        //now convert to milliWei
        int milliEth = (int)(value*1000.0f);

        //now convert to ETH
        BigInteger weiValue = Convert.toWei(String.valueOf(milliEth), Convert.Unit.FINNEY).toBigInteger();

        return weiValue;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    boolean isValidAmount(String eth) {
        try {
            String wei = BalanceUtils.EthToWei(eth);
            return wei != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.prepare(ticket);
    }

    private void onTicketIdClick(View view, TicketRange range) {
        Context context = view.getContext();
        //TODO: what action should be performed when clicking on a range?
    }
}
