package com.example.group1_duangua;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
// import androidx.core.graphics.Insets; // Not strictly needed for this change
// import androidx.core.view.ViewCompat; // Not strictly needed for this change
// import androidx.core.view.WindowInsetsCompat; // Not strictly needed for this change

public class MainActivity extends AppCompatActivity {

    private TextView textViewMainBalanceValue; // Variable name can remain the same
    private SharedPreferences sharedPreferences;
    // Use the same constants as in TopUpActivity
    public static final String SHARED_PREFS_NAME = "MyPrefs";
    public static final String BALANCE_KEY = "balance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // Assuming this is fixed or handled separately
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        // Find the TextView for balance display using the correct ID from activity_main.xml
        textViewMainBalanceValue = findViewById(R.id.tvBalance); // Changed ID here

        ConstraintLayout root = findViewById(R.id.mainLayout);
        Drawable bg = root.getBackground();
        if (bg instanceof AnimationDrawable) {
            AnimationDrawable anim = (AnimationDrawable) bg;
            anim.setEnterFadeDuration(2000);
            anim.setExitFadeDuration(2000);
            anim.start();
        }

        Button btnStrart = findViewById(R.id.btnStartRace);
        btnStrart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RaceActivity.class);
                startActivity(intent);
            }
        });

        Button btnRecharge = findViewById(R.id.btnRecharge);
        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TopUpActivity.class);
                startActivity(intent);
            }
        });

        loadAndDisplayBalance(); // Load balance when activity is created
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayBalance(); // Reload balance when activity resumes (e.g., after returning from TopUpActivity)
    }

    private void loadAndDisplayBalance() {
        if (sharedPreferences != null && textViewMainBalanceValue != null) {
            float currentBalance = sharedPreferences.getFloat(BALANCE_KEY, 0f);
            // Format to show as currency, e.g., Số dư: 50,000đ
            // The TextView in activity_main.xml already has "Số dư: " in its text, 
            // so we might only want to set the value, or adjust the text here accordingly.
            // For now, keeping the format consistent with TopUpActivity's balance display, prepending "Số dư: ".
            textViewMainBalanceValue.setText(String.format("Số dư: %,.0fđ", currentBalance));
        }
    }
}
