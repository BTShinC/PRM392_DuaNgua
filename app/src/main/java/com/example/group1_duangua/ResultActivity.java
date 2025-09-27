package com.example.group1_duangua;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView tvWinner, tvResult, tvBalance;
    private Button btnBack;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // --- Bind UI ---
        tvWinner = findViewById(R.id.tvWinner);
        tvResult = findViewById(R.id.tvResult);
        tvBalance = findViewById(R.id.tvBalance);
        btnBack = findViewById(R.id.btnBack);

        // --- Get data from Intent ---
        Intent intent = getIntent();
        int winnerCar = intent.getIntExtra("winnerCar", -1);
        int userBetCar = intent.getIntExtra("userBetCar", -1);
        float betAmount = intent.getFloatExtra("betAmount", 0f);

        // --- Show winner text ---
        tvWinner.setText("Xe " + winnerCar + " về đích!");

        // --- SharedPreferences for balance ---
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        float balance = prefs.getFloat("balance", 0f);

        // --- Check win/lose ---
        if (winnerCar == userBetCar) {
            tvResult.setText("🎉 Bạn đã thắng cược!");
            balance += betAmount * 2;  // Example: win = double money
            playSound(R.raw.win_sound);
        } else {
            tvResult.setText("😢 Bạn đã thua cược!");
            balance -= betAmount;      // Lose = subtract bet
            playSound(R.raw.lose_sound);
        }

        // --- Save updated balance ---
        prefs.edit().putFloat("balance", balance).apply();

        // --- Show updated balance ---
        tvBalance.setText(String.format("Số dư hiện tại: %,.0fđ", balance));

        // --- Back button ---
        btnBack.setOnClickListener(v -> {
            finish(); // close ResultActivity and go back to RaceActivity/Main
        });
    }

    private void playSound(int resId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, resId);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
