package com.alphawallet.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alphawallet.app.R;
import com.alphawallet.app.widget.FunctionButtonBar;

public class WyreSplashActivity extends BaseActivity {
    FunctionButtonBar functionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wyre_splash);
        toolbar();
        setTitle(getString(R.string.title_wyre));

        initViews();

        setupFunctionBar();
    }

    private void initViews() {
        functionBar = findViewById(R.id.layoutButtons);
    }

    private void setupFunctionBar() {
        functionBar.setPrimaryButtonEnabled(true);
        functionBar.setPrimaryButtonText(R.string.action_continue);
        functionBar.setPrimaryButtonClickListener(v -> {
            // TODO: Show Wyre Widget
        });
        functionBar.revealButtons();
    }
}
