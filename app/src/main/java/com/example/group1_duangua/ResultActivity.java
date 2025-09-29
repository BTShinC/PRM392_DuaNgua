package com.example.group1_duangua;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private TextView tvWinner, tvResult, tvBalance;
    private ImageView ivResultIcon;
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
        ivResultIcon = findViewById(R.id.ivResultIcon);
        btnBack = findViewById(R.id.btnBack);

        // --- Get data from Intent ---
        Intent intent = getIntent();
        int winnerCar = intent.getIntExtra("winnerCar", -1);
        ArrayList<Bet> userBets = intent.getParcelableArrayListExtra("userBets");

        // --- Show winner text ---
        tvWinner.setText("Xe " + winnerCar + " về đích!");

        // --- SharedPreferences for balance ---
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        float balance = prefs.getFloat("balance", 0f);

        // --- Logic for variable bet amounts ---
        float totalWagered = 0;
        Bet winningBet = null;

        if (userBets != null) {
            for (Bet bet : userBets) {
                totalWagered += bet.getBetAmount();
                if (bet.getCarNumber() == winnerCar) {
                    winningBet = bet;
                }
            }
        }

        if (winningBet != null) {
            // --- WINNING STATE ---
            tvResult.setText("Bạn đã thắng cược!");
            tvResult.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            ivResultIcon.setImageResource(R.drawable.ic_trophy);

            float winnings = winningBet.getBetAmount() * 2;
            balance += (winnings - totalWagered);
            playSound(R.raw.win_sound);
        } else {
            // --- LOSING STATE ---
            tvResult.setText("Bạn đã thua cược!");
            tvResult.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            ivResultIcon.setImageResource(R.drawable.ic_sad_face); // Assuming you have a drawable named ic_sad_face

            balance -= totalWagered;
            playSound(R.raw.lose_sound);
        }

        // --- Save updated balance ---
        prefs.edit().putFloat("balance", balance).apply();

        // --- Show updated balance ---
        tvBalance.setText(String.format("Số dư hiện tại: %,.0fđ", balance));

        // --- Back button ---
        btnBack.setOnClickListener(v -> finish());
    }

    private void playSound(int resId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, resId);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        }
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
