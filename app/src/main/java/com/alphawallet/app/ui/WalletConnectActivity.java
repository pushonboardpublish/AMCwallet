package com.alphawallet.app.ui;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alphawallet.app.R;
import com.alphawallet.app.entity.Wallet;
import com.alphawallet.app.viewmodel.WalletConnectViewModel;
import com.alphawallet.app.viewmodel.WalletConnectViewModelFactory;
import com.alphawallet.app.widget.FunctionButtonBar;
import com.alphawallet.walletconnect.WCClient;
import com.alphawallet.walletconnect.models.WCPeerMeta;
import com.alphawallet.walletconnect.models.ethereum.WCEthereumSignMessage;
import com.alphawallet.walletconnect.models.ethereum.WCEthereumTransaction;
import com.alphawallet.walletconnect.models.session.WCSession;
import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import kotlin.Unit;
import okhttp3.OkHttpClient;

public class WalletConnectActivity extends BaseActivity {
    private static final String TAG = WalletConnectActivity.class.getSimpleName();

    @Inject
    WalletConnectViewModelFactory viewModelFactory;
    WalletConnectViewModel viewModel;

    private WCClient client;
    private OkHttpClient httpClient;

    private ImageView icon;
    private TextView peerName;
    private TextView peerUrl;
    private TextView address;
    private ProgressBar progressBar;
    private LinearLayout infoLayout;
    private FunctionButtonBar functionBar;

    private Wallet wallet;
    private String qrCode;

    private WCPeerMeta peerMeta;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidInjection.inject(this);

        setContentView(R.layout.activity_wallet_connect);

        toolbar();

        setTitle(getString(R.string.title_wallet_connect));

        initViews();

        initViewModel();

        retrieveQrCode();

        viewModel.prepare();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(WalletConnectViewModel.class);

        viewModel.defaultWallet().observe(this, this::onDefaultWallet);
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress);
        infoLayout = findViewById(R.id.layout_info);
        icon = findViewById(R.id.icon);
        peerName = findViewById(R.id.peer_name);
        peerUrl = findViewById(R.id.peer_url);
        address = findViewById(R.id.address);

        functionBar = findViewById(R.id.layoutButtons);
        functionBar.setPrimaryButtonText(R.string.action_end_session);
        functionBar.setPrimaryButtonClickListener(v -> {
            onBackPressed();
        });
    }

    private void onDefaultWallet(Wallet wallet) {
        this.wallet = wallet;

        address.setText(wallet.address);
        if (!wallet.address.isEmpty()) {
            initWalletConnectPeerMeta();
            initWalletConnectClient();
            initWalletConnectSession();
        }
    }

    private void initWalletConnectSession() {
        WCSession session = WCSession.Companion.from(qrCode);
        if (session != null) {
            client.connect(session, peerMeta, UUID.randomUUID().toString(), UUID.randomUUID().toString());
        } else {
            Log.d(TAG, "Invalid WalletConnect QR data");
            finish();
        }
    }

    private void initWalletConnectPeerMeta() {
        String name = getString(R.string.app_name);
        String url = "https://www.alphawallet.com";
        String description = wallet.address;
        String[] icons = {"https://alphawallet.com/wp-content/uploads/2020/03/favicon.png"};

        peerMeta = new WCPeerMeta(
                name,
                url,
                description,
                Arrays.asList(icons)
        );
    }

    private void retrieveQrCode() {
        Bundle data = getIntent().getExtras();
        if (data != null) {
            this.qrCode = data.getString("qrCode");
        } else {
            Log.d(TAG, "No QR code retrieved");
            finish();
        }
    }

    private void initWalletConnectClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();
        client = new WCClient(new GsonBuilder(), httpClient);

        client.setOnSessionRequest((id, peer) -> {
            runOnUiThread(() -> {
                onSessionRequest(id, peer);
            });
            return Unit.INSTANCE;
        });

        client.setOnFailure(throwable -> {
            Log.d(TAG, "throwable: " + throwable.getMessage());
            runOnUiThread(() -> {
                onFailure(throwable);
            });
            return Unit.INSTANCE;
        });

        client.setOnEthSign((id, message) -> {
            Log.d(TAG, "id: " + id + ", message: " + message);
            runOnUiThread(() -> {
                onEthSign(id, message);
            });
            return Unit.INSTANCE;
        });

        client.setOnEthSignTransaction((id, transaction) -> {
            Log.d(TAG, "id: " + id + ", transaction: " + transaction);
            runOnUiThread(() -> {
                onEthSignTransaction(id, transaction);
            });
            return Unit.INSTANCE;
        });

        client.setOnEthSendTransaction((id, transaction) -> {
            Log.d(TAG, "id: " + id + ", transaction: " + transaction);
            runOnUiThread(() -> {
                onEthSendTransaction(id, transaction);
            });
            return Unit.INSTANCE;
        });
    }

    private void onSessionRequest(Long id, WCPeerMeta peer) {
        String[] accounts = {wallet.address};

        Glide.with(this)
                .load(peer.getIcons().get(0))
                .into(icon);
        peerName.setText(peer.getName());
        peerUrl.setText(peer.getUrl());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder
                .setIcon(icon.getDrawable())
                .setTitle(peer.getName())
                .setMessage(peer.getUrl())
                .setPositiveButton(R.string.dialog_approve, (d, w) -> {
                    client.approveSession(Arrays.asList(accounts), 1);
                    progressBar.setVisibility(View.GONE);
                    infoLayout.setVisibility(View.VISIBLE);
                })
                .setNegativeButton(R.string.dialog_reject, (d, w) -> {
                    client.rejectSession(getString(R.string.message_reject_request));
                    finish();
                })
                .create();
        dialog.show();
    }

    private void onEthSign(Long id, WCEthereumSignMessage message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle(R.string.dialog_title_sign_message)
                .setMessage(message.getData())
                .setPositiveButton(R.string.dialog_ok, (d, w) -> {
                    String signature = signMessage(message);
                    client.approveRequest(id, signature);
                })
                .setNegativeButton(R.string.action_cancel, (d, w) -> {
                    client.rejectRequest(id, getString(R.string.message_reject_request));
                })
                .create();
        dialog.show();
    }

    private void onEthSignTransaction(Long id, WCEthereumTransaction transaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle(R.string.dialog_title_sign_transaction)
                .setMessage(transaction.getData())
                .setPositiveButton(R.string.dialog_ok, (d, w) -> {
                    String signature = signTransaction(transaction);
                    client.approveRequest(id, signature);
                })
                .setNegativeButton(R.string.action_cancel, (d, w) -> {
                    client.rejectRequest(id, getString(R.string.message_reject_request));
                })
                .create();
        dialog.show();
    }

    private void onEthSendTransaction(Long id, WCEthereumTransaction transaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle(R.string.dialog_send_eth_transaction)
                .setMessage(transaction.getData())
                .setPositiveButton(R.string.dialog_ok, (d, w) -> {
                    String signature = sendTransaction(transaction);
                    client.approveRequest(id, signature);
                })
                .setNegativeButton(R.string.action_cancel, (d, w) -> {
                    client.rejectRequest(id, getString(R.string.message_reject_request));
                })
                .create();
        dialog.show();
    }

    private void onFailure(Throwable throwable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle(R.string.title_dialog_error)
                .setMessage(throwable.getMessage())
                .setNeutralButton(R.string.try_again, (d, w) -> {
                    finish();
                })
                .create();
        dialog.show();
    }

    private String sendTransaction(WCEthereumTransaction transaction) {
        String transactionToSign = transaction.getData();
        String signature = "";

        // TODO: Send ETH Transaction

        return signature;
    }

    private String signTransaction(WCEthereumTransaction transaction) {
        String transactionToSign = transaction.getData();
        String signature = "";

        // TODO: Sign ETH Transaction
        return signature;
    }

    private String signMessage(WCEthereumSignMessage message) {
        String messageToSign = message.getData();
        String signature = "";

        // TODO: Sign message

        return signature;
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.killSession();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.killSession();
    }

    @Override
    public void onBackPressed() {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.setTitle(R.string.dialog_title_disconnect_session)
                    .setPositiveButton(R.string.dialog_ok, (d, w) -> {
                        client.killSession();
                        finish();
                    })
                    .setNegativeButton(R.string.action_cancel, (d, w) -> {
                        d.dismiss();
                    })
                    .create();
            dialog.show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }
}
