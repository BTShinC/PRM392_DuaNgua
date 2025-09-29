package com.example.group1_duangua;

import android.os.Parcel;
import android.os.Parcelable;

public class Bet implements Parcelable {

    private final int carNumber;
    private final float betAmount;

    public Bet(int carNumber, float betAmount) {
        this.carNumber = carNumber;
        this.betAmount = betAmount;
    }

    protected Bet(Parcel in) {
        carNumber = in.readInt();
        betAmount = in.readFloat();
    }

    public static final Creator<Bet> CREATOR = new Creator<Bet>() {
        @Override
        public Bet createFromParcel(Parcel in) {
            return new Bet(in);
        }

        @Override
        public Bet[] newArray(int size) {
            return new Bet[size];
        }
    };

    public int getCarNumber() {
        return carNumber;
    }

    public float getBetAmount() {
        return betAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(carNumber);
        dest.writeFloat(betAmount);
    }
}
