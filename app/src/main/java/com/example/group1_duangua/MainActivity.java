package com.example.group1_duangua;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView textViewMainBalanceValue;
    private SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS_NAME = "MyPrefs";
    public static final String BALANCE_KEY = "balance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        textViewMainBalanceValue = findViewById(R.id.tvBalance);

        ConstraintLayout root = findViewById(R.id.mainLayout);
        Drawable bg = root.getBackground();
        if (bg instanceof AnimationDrawable) {
            AnimationDrawable anim = (AnimationDrawable) bg;
            anim.setEnterFadeDuration(2000);
            anim.setExitFadeDuration(2000);
            anim.start();
        }

        Button btnStartRace = findViewById(R.id.btnStartRace);
        btnStartRace.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RaceActivity.class);

            // --- Create a list of Bet objects ---
            // TODO: Replace with actual user input from the Betting UI
            ArrayList<Bet> userBets = new ArrayList<>();
            userBets.add(new Bet(1, 1000f)); // User bets 1000 on Car 1
            userBets.add(new Bet(2, 2000f)); // User bets 2500 on Car 3

            intent.putParcelableArrayListExtra("userBets", userBets);
            startActivity(intent);
        });

        Button btnRecharge = findViewById(R.id.btnRecharge);
        btnRecharge.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TopUpActivity.class);
            startActivity(intent);
        });

        Button btnStatistic = findViewById(R.id.btnStatistics);
        btnStatistic.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        loadAndDisplayBalance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayBalance();
    }

    private void loadAndDisplayBalance() {
        if (sharedPreferences != null && textViewMainBalanceValue != null) {
            float currentBalance = sharedPreferences.getFloat(BALANCE_KEY, 0f);
            textViewMainBalanceValue.setText(String.format("Số dư: %,.0fđ", currentBalance));
        }
    }
}
