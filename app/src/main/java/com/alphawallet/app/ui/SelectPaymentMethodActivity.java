package com.alphawallet.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alphawallet.app.C;
import com.alphawallet.app.R;
import com.alphawallet.app.entity.PaymentMethodItem;
import com.alphawallet.app.ui.widget.divider.ListDivider;

import java.util.ArrayList;

public class SelectPaymentMethodActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private String currentPaymentMethod;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        toolbar();
        setTitle(getString(R.string.title_select_payment_method));

        currentPaymentMethod = getIntent().getStringExtra(C.EXTRA_CURRENT_PAYMENT_METHOD);

        ArrayList<PaymentMethodItem> paymentMethodItems = getIntent().getParcelableArrayListExtra(C.EXTRA_STATE);

        if (paymentMethodItems != null) {
            recyclerView = findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CustomAdapter(paymentMethodItems);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new ListDivider(this));
        }
    }

    private void setPaymentMethod(PaymentMethodItem paymentMethodItem) {
        Intent intent = new Intent();
        intent.putExtra(C.EXTRA_PAYMENT_METHOD, paymentMethodItem);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
        private ArrayList<PaymentMethodItem> dataSet;
        private String selectedItemId;

        private CustomAdapter(ArrayList<PaymentMethodItem> data) {
            this.dataSet = data;
        }

        private String getSelectedItemId() {
            return this.selectedItemId;
        }

        private void setSelectedItemId(String selectedItemId) {
            this.selectedItemId = selectedItemId;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_payment_method, parent, false);

            return new CustomViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            PaymentMethodItem item = dataSet.get(position);
            holder.title.setText(item.getName());
            holder.subtitle.setText(item.getDescription());

            if (item.isEnabled()) {
                holder.layout.setEnabled(true);
                holder.layout.setAlpha(1.0f);
                holder.layout.setOnClickListener(v -> {
                    setPaymentMethod(item);
                });
            } else {
                holder.layout.setEnabled(false);
                holder.layout.setAlpha(0.3f);
            }
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView subtitle;
            View layout;

            CustomViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.title);
                subtitle = view.findViewById(R.id.subtitle);
                layout = view.findViewById(R.id.layout);
            }
        }
    }
}
