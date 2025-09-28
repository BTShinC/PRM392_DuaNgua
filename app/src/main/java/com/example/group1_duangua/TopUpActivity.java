package com.example.group1_duangua;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
// import android.widget.RadioGroup; // Not used currently
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;
import com.google.android.material.button.MaterialButton;

public class TopUpActivity extends AppCompatActivity {

    private TextView textViewWalletBalanceValue;
    private EditText editTextCustomAmount;
    private MaterialButton buttonConfirmTopUp;
    private MaterialButton buttonAmount20k, buttonAmount50k, buttonAmount100k, buttonAmount200k, buttonAmount500k, buttonCustomAmount;
    private ImageView imageViewBackButton;

    private SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS_NAME = "MyPrefs";
    public static final String BALANCE_KEY = "balance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

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

        loadAndDisplayBalance();

        imageViewBackButton.setOnClickListener(v -> finish());

        View.OnClickListener amountButtonListener = v -> {
            Button b = (Button) v;
            String amountText = b.getText().toString().replaceAll("[^\\d]", "");
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
                playTopUpSound();
                showSuccessDialog(); // Show custom success dialog

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
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.top_up_success_sound);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        } else {
            // Toast.makeText(this, "Sound file not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_payment_success, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        Button buttonDialogClose = dialogView.findViewById(R.id.buttonDialogClose);
        buttonDialogClose.setOnClickListener(v -> dialog.dismiss());
        
        // Optional: Make dialog not cancelable by back press or touch outside
        // dialog.setCancelable(false);
        // dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }
}
