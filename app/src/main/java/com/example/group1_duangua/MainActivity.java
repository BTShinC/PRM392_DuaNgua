package com.example.group1_duangua;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textViewMainBalanceValue;
    private SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS_NAME = "MyPrefs";
    public static final String BALANCE_KEY = "balance";

    // EditText fields for betting
    private EditText edtBetCar1;
    private EditText edtBetCar2;
    // Add more EditTexts if you have more cars, e.g., edtBetCar3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        textViewMainBalanceValue = findViewById(R.id.tvBalance);

        // Initialize EditText fields
        // Make sure these IDs exist in your activity_main.xml
        edtBetCar1 = findViewById(R.id.edtBetCar1); 
        edtBetCar2 = findViewById(R.id.edtBetCar2);
        // if (findViewById(R.id.edtBetCar3) != null) { edtBetCar3 = findViewById(R.id.edtBetCar3); }

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
            ArrayList<Bet> userBets = new ArrayList<>();
            float totalBetAmount = 0f;

            // Process bet for Car 1
            float betAmountCar1 = getBetAmountFromEditText(edtBetCar1);
            if (betAmountCar1 > 0) {
                userBets.add(new Bet(1, betAmountCar1));
                totalBetAmount += betAmountCar1;
            }

            // Process bet for Car 2
            float betAmountCar2 = getBetAmountFromEditText(edtBetCar2);
            if (betAmountCar2 > 0) {
                userBets.add(new Bet(2, betAmountCar2));
                totalBetAmount += betAmountCar2;
            }

            // Process bet for Car 3 (if it exists)
            // float betAmountCar3 = getBetAmountFromEditText(edtBetCar3); 
            // if (betAmountCar3 > 0) {
            //     userBets.add(new Bet(3, betAmountCar3));
            //     totalBetAmount += betAmountCar3;
            // }

            if (userBets.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng đặt cược ít nhất một xe.", Toast.LENGTH_SHORT).show();
                return;
            }

            float currentBalance = sharedPreferences.getFloat(BALANCE_KEY, 0f);
            if (totalBetAmount > currentBalance) {
                Toast.makeText(MainActivity.this, "Số dư không đủ để đặt cược!", Toast.LENGTH_SHORT).show();
                return;
            }

            // If all checks pass, proceed to RaceActivity
            Intent intent = new Intent(MainActivity.this, RaceActivity.class);
            intent.putParcelableArrayListExtra("userBets", userBets);
            startActivity(intent);

            // Optionally clear EditText fields after starting race
            // if (edtBetCar1 != null) edtBetCar1.setText("");
            // if (edtBetCar2 != null) edtBetCar2.setText("");
            // if (edtBetCar3 != null) edtBetCar3.setText("");
        });

        Button btnRecharge = findViewById(R.id.btnRecharge);
        btnRecharge.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TopUpActivity.class);
            startActivity(intent);
        });

        loadAndDisplayBalance(); // Initial balance load
    }

    private float getBetAmountFromEditText(EditText editText) {
        if (editText == null) return 0f;
        String betText = editText.getText().toString();
        if (TextUtils.isEmpty(betText)) {
            return 0f;
        }
        try {
            float amount = Float.parseFloat(betText);
            return Math.max(0, amount); // Ensure non-negative bet
        } catch (NumberFormatException e) {
            // Optionally show a toast if input is invalid
            // Toast.makeText(this, "Số tiền cược không hợp lệ cho " + editText.getHint(), Toast.LENGTH_SHORT).show();
            return 0f; // Treat invalid input as 0 bet
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayBalance(); // Update balance when returning to this activity
    }

    private void loadAndDisplayBalance() {
        if (sharedPreferences != null && textViewMainBalanceValue != null) {
            float currentBalance = sharedPreferences.getFloat(BALANCE_KEY, 0f);
            // Ensure textViewMainBalanceValue is the correct TextView from activity_main.xml (R.id.tvBalance)
            textViewMainBalanceValue.setText(String.format(Locale.getDefault(), "Số dư: %,.0fđ", currentBalance));
        }
    }
}
