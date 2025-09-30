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
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private TextView tvWinner, tvResult, tvBalance;
    private ImageView ivResultIcon;
    private Button btnBack;
    private MediaPlayer mediaPlayer;

    // SharedPreferences constants (ensure these match with other activities if used elsewhere)
    public static final String SHARED_PREFS_NAME = "MyPrefs";
    public static final String BALANCE_KEY = "balance";

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
        int winnerCar = intent.getIntExtra("winnerCar", -1); // -1 if no winner/error
        ArrayList<Bet> userBets = intent.getParcelableArrayListExtra("userBets");

        // --- Display general winner information ---
        if (winnerCar != -1) {
            tvWinner.setText(String.format(Locale.getDefault(), "Xe %d về đích!", winnerCar));
        } else {
            tvWinner.setText("Không có thông tin người thắng cuộc.");
        }

        // --- SharedPreferences for balance ---
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        float currentBalance = prefs.getFloat(BALANCE_KEY, 0f);
        float newBalance = currentBalance; // Initialize newBalance with currentBalance

        float totalProfitOrLoss = 0f;

        if (userBets != null && !userBets.isEmpty()) {
            for (Bet bet : userBets) {
                if (bet.getCarNumber() == winnerCar) {
                    totalProfitOrLoss += bet.getBetAmount(); // Profit is 1x the bet amount
                } else {
                    totalProfitOrLoss -= bet.getBetAmount(); // Loss is 1x the bet amount
                }
            }

            newBalance += totalProfitOrLoss;

            if (totalProfitOrLoss > 0) {
                tvResult.setText(String.format(Locale.getDefault(), "Chúc mừng! Bạn thắng %,.0fđ", totalProfitOrLoss));
                // Assuming R.color.green_success is defined in your colors.xml
                tvResult.setTextColor(ContextCompat.getColor(this, R.color.green_success)); 
                ivResultIcon.setImageResource(R.drawable.ic_trophy);
                playSound(R.raw.win_sound); // Assuming R.raw.win_sound exists
            } else if (totalProfitOrLoss < 0) {
                tvResult.setText(String.format(Locale.getDefault(), "Rất tiếc! Bạn thua %,.0fđ", Math.abs(totalProfitOrLoss)));
                tvResult.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                ivResultIcon.setImageResource(R.drawable.ic_sad_face); // Assuming R.drawable.ic_sad_face exists
                playSound(R.raw.lose_sound); // Assuming R.raw.lose_sound exists
            } else { // totalProfitOrLoss == 0
                tvResult.setText("Bạn hòa vốn!");
                tvResult.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                ivResultIcon.setImageResource(R.drawable.ic_trophy); // Or a neutral icon
                // No specific sound for breakeven, or a neutral one
            }
        } else {
            // No bets were made
            tvResult.setText("Bạn không tham gia cược.");
            tvResult.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            ivResultIcon.setImageResource(R.drawable.ic_trophy); // Or a neutral/default icon
            // No sound needed
        }

        // --- Save updated balance ---
        prefs.edit().putFloat(BALANCE_KEY, newBalance).apply();

        // --- Show updated balance ---
        // Placeholder in XML is: @string/placeholder_balance_text
        // Update to show the actual balance.
        tvBalance.setText(String.format(Locale.getDefault(), "Số dư: %,.0fđ", newBalance));

        // --- Back button to finish this activity ---
        // XML text is @string/play_again_button
        btnBack.setOnClickListener(v -> {
            // Optionally, navigate to a specific activity like MainActivity
            // Intent mainActivityIntent = new Intent(ResultActivity.this, MainActivity.class);
            // mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(mainActivityIntent);
            finish(); // Finishes ResultActivity, returning to previous in stack (or as defined by launch modes)
        });
    }

    private void playSound(int resId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, resId);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                if (mp != null) {
                    mp.release();
                }
                if (mediaPlayer == mp) { // Ensure we are clearing the correct instance
                    mediaPlayer = null;
                }
            });
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
