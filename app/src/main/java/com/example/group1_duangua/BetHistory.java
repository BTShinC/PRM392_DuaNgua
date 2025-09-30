package com.example.group1_duangua;

public class BetHistory {
    private String carName;
    private int amount;
    private boolean isWin;
    private String time;

    public BetHistory(String carName, int amount, boolean isWin, String time) {
        this.carName = carName;
        this.amount = amount;
        this.isWin = isWin;
        this.time = time;
    }

    public String getCarName() { return carName; }
    public int getAmount() { return amount; }
    public boolean isWin() { return isWin; }
    public String getTime() { return time; }
}
