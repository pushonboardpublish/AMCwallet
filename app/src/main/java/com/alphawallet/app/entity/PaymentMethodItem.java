package com.alphawallet.app.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class PaymentMethodItem implements Parcelable {
    public static final Creator<PaymentMethodItem> CREATOR = new Creator<PaymentMethodItem>() {
        @Override
        public PaymentMethodItem createFromParcel(Parcel in) {
            return new PaymentMethodItem(in);
        }

        @Override
        public PaymentMethodItem[] newArray(int size) {
            return new PaymentMethodItem[size];
        }
    };

    private String name;
    private String code;
    private String description;
    private boolean isSelected;
    private boolean isEnabled;

    public PaymentMethodItem(String name, String code, String description, boolean isEnabled) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.isEnabled = isEnabled;
    }

    public PaymentMethodItem(String name, String code, boolean isEnabled) {
        this.name = name;
        this.code = code;
        this.isEnabled = isEnabled;
    }

    public PaymentMethodItem(Parcel in) {
        name = in.readString();
        code = in.readString();
        description = in.readString();
        isEnabled = in.readInt() == 1;
        isSelected = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(description);
        dest.writeInt(isEnabled ? 1: 0);
        dest.writeInt(isSelected ? 1 : 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
