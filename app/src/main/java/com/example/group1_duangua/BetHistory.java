package com.example.group1_duangua;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class BetHistory implements Parcelable {
    private String carName;
    private float amount;
    private boolean isWin;
    private String time;

    public BetHistory(String carName, float amount, boolean isWin, String time) {
        this.carName = carName;
        this.amount = amount;
        this.isWin = isWin;
        this.time = time;
    }

    protected BetHistory(Parcel in) {
        carName = in.readString();
        amount = in.readFloat();
        isWin = in.readByte() != 0; // true if byte != 0
        time = in.readString();
    }

    public String getCarName() { return carName; }
    public float getAmount() { return amount; }
    public boolean isWin() { return isWin; }
    public String getTime() { return time; }

    public void setWin(boolean isWin) { this.isWin = isWin; }

    public static final Creator<BetHistory> CREATOR = new Creator<BetHistory>() {
        @Override
        public BetHistory createFromParcel(Parcel in) {
            return new BetHistory(in);
        }

        @Override
        public BetHistory[] newArray(int size) {
            return new BetHistory[size];
        }
    };
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(carName);
        dest.writeFloat(amount);
        dest.writeByte((byte) (isWin ? 1 : 0));
        dest.writeString(time);
    }
}
