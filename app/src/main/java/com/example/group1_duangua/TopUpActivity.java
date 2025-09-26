package com.example.group1_duangua;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer; // For sound effect
import com.google.android.material.button.MaterialButton;

public class TopUpActivity extends AppCompatActivity {

    private TextView textViewWalletBalanceValue;
    private EditText editTextCustomAmount;
    private Button buttonConfirmTopUp;
    private MaterialButton buttonAmount20k, buttonAmount50k, buttonAmount100k, buttonAmount200k, buttonAmount500k, buttonCustomAmount;
    private ImageView imageViewBackButton;
    // private RadioGroup radioGroupPaymentMethods; // Can be used later if needed

    private SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS_NAME = "MyPrefs";
    public static final String BALANCE_KEY = "balance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        // Find UI elements
        imageViewBackButton = findViewById(R.id.imageViewBackButton);
        textViewWalletBalanceValue = findViewById(R.id.textViewWalletBalanceValue);
        editTextCustomAmount = findViewById(R.id.editTextCustomAmount);
        buttonConfirmTopUp = findViewById(R.id.buttonConfirmTopUp);

        buttonAmount20k = findViewById(R.id.buttonAmount20k);
        buttonAmount50k = findViewById(R.id.buttonAmount50k);
        buttonAmount100k = findViewById(R.id.buttonAmount100k);
        buttonAmount200k = findViewById(R.id.buttonAmount200k);
        buttonAmount500k = findViewById(R.id.buttonAmount500k);
        buttonCustomAmount = findViewById(R.id.buttonCustomAmount);
        // radioGroupPaymentMethods = findViewById(R.id.radioGroupPaymentMethods);

        // Load and display current balance
        loadAndDisplayBalance();

        // Back button listener
        imageViewBackButton.setOnClickListener(v -> finish());

        // Predefined amount buttons listeners
        View.OnClickListener amountButtonListener = v -> {
            Button b = (Button) v;
            String amountText = b.getText().toString().replaceAll("[^\\d]", ""); // Remove non-digits
            editTextCustomAmount.setText(amountText);
            editTextCustomAmount.setVisibility(View.VISIBLE);
            editTextCustomAmount.requestFocus();
        };

        buttonAmount20k.setOnClickListener(amountButtonListener);
        buttonAmount50k.setOnClickListener(amountButtonListener);
        buttonAmount100k.setOnClickListener(amountButtonListener);
        buttonAmount200k.setOnClickListener(amountButtonListener);
        buttonAmount500k.setOnClickListener(amountButtonListener);

        buttonCustomAmount.setOnClickListener(v -> {
            editTextCustomAmount.setText("");
            editTextCustomAmount.setVisibility(View.VISIBLE);
            editTextCustomAmount.requestFocus();
        });

        // Confirm top-up button listener
        buttonConfirmTopUp.setOnClickListener(v -> {
            String amountString = editTextCustomAmount.getText().toString();
            if (TextUtils.isEmpty(amountString)) {
                Toast.makeText(TopUpActivity.this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amountToTopUp = Double.parseDouble(amountString);
                if (amountToTopUp <= 0) {
                    Toast.makeText(TopUpActivity.this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                float currentBalance = sharedPreferences.getFloat(BALANCE_KEY, 0f);
                float newBalance = currentBalance + (float) amountToTopUp;

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat(BALANCE_KEY, newBalance);
                editor.apply();

                updateBalanceDisplay(newBalance);
                Toast.makeText(TopUpActivity.this, "Nạp tiền thành công: " + amountString + "đ", Toast.LENGTH_LONG).show();

                playTopUpSound(); // Play sound

                editTextCustomAmount.setText("");
                editTextCustomAmount.setVisibility(View.GONE);

            } catch (NumberFormatException e) {
                Toast.makeText(TopUpActivity.this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAndDisplayBalance() {
        float currentBalance = sharedPreferences.getFloat(BALANCE_KEY, 0f);
        updateBalanceDisplay(currentBalance);
    }

    private void updateBalanceDisplay(float balance) {
        textViewWalletBalanceValue.setText(String.format("%,.0fđ", balance));
    }

    private void playTopUpSound() {
        // Assuming you have a sound file named top_up_success_sound.mp3 in res/raw
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.top_up_success_sound); 
        if (mediaPlayer != null) {
            mediaPlayer.start();
            // Release the MediaPlayer resources once playback is complete
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        } else {
            // Optional: Log an error or show a toast if the sound file is missing
            // Toast.makeText(this, "Sound file not found", Toast.LENGTH_SHORT).show();
        }
    }
}
