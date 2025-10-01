package com.example.group1_duangua;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textViewMainBalanceValue;
    private SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS_NAME = "MyPrefs";
    public static final String BALANCE_KEY = "balance";

    private EditText edtBetCar1;
    private EditText edtBetCar2;
    private EditText edtBetCar3;

    MediaPlayer mediaPlayer;
    private DecimalFormat formatter = new DecimalFormat("###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        textViewMainBalanceValue = findViewById(R.id.tvBalance);

        edtBetCar1 = findViewById(R.id.edtBetCar1);
        edtBetCar2 = findViewById(R.id.edtBetCar2);
        edtBetCar3 = findViewById(R.id.edtBetCar3);

        addThousandSeparatorTextWatcher(edtBetCar1);
        addThousandSeparatorTextWatcher(edtBetCar2);
        addThousandSeparatorTextWatcher(edtBetCar3);

        mediaPlayer = MediaPlayer.create(this, R.raw.music_header);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.start();

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

            float betAmountCar1 = getBetAmountFromEditText(edtBetCar1);
            if (betAmountCar1 > 0) {
                userBets.add(new Bet(1, betAmountCar1));
                totalBetAmount += betAmountCar1;
            }

            float betAmountCar2 = getBetAmountFromEditText(edtBetCar2);
            if (betAmountCar2 > 0) {
                userBets.add(new Bet(2, betAmountCar2));
                totalBetAmount += betAmountCar2;
            }

            float betAmountCar3 = getBetAmountFromEditText(edtBetCar3);
            if (betAmountCar3 > 0) {
                userBets.add(new Bet(3, betAmountCar3));
                totalBetAmount += betAmountCar3;
            }

            if (userBets.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng đặt cược ít nhất một xe.", Toast.LENGTH_SHORT).show();
                return;
            }

            float currentBalance = sharedPreferences.getFloat(BALANCE_KEY, 0f);
            if (totalBetAmount > currentBalance) {
                Toast.makeText(MainActivity.this, "Số dư không đủ để đặt cược!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, RaceActivity.class);
            intent.putParcelableArrayListExtra("userBets", userBets);
            startActivity(intent);

            if (edtBetCar1 != null) edtBetCar1.setText("");
            if (edtBetCar2 != null) edtBetCar2.setText("");
            if (edtBetCar3 != null) edtBetCar3.setText("");
        });

        Button btnRecharge = findViewById(R.id.btnRecharge);
        btnRecharge.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TopUpActivity.class);
            startActivity(intent);
        });

        loadAndDisplayBalance();
    }

    private void addThousandSeparatorTextWatcher(final EditText editText) {
        if (editText == null) return;
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[\\,.đ]", ""); // Loại bỏ dấu phẩy, chấm, ký tự tiền tệ nếu có

                    if (!cleanString.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(cleanString);
                            String formatted = formatter.format(parsed); // formatter là DecimalFormat("###,###")

                            current = formatted;
                            editText.setText(formatted);
                            editText.setSelection(formatted.length());
                        } catch (NumberFormatException e) {
                            // Xử lý lỗi nếu không parse được, ví dụ, không làm gì hoặc xóa input
                        }
                    } else {
                        current = "";
                    }
                    editText.addTextChangedListener(this);
                }
            }
        });
    }


    private float getBetAmountFromEditText(EditText editText) {
        if (editText == null) return 0f;
        String betText = editText.getText().toString();
        if (TextUtils.isEmpty(betText)) {
            return 0f;
        }
        // Loại bỏ dấu phẩy để parse
        String cleanString = betText.replaceAll("[\\,]", ""); 
        try {
            float amount = Float.parseFloat(cleanString);
            return Math.max(0, amount); 
        } catch (NumberFormatException e) {
            return 0f; 
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayBalance();
    }

    private void loadAndDisplayBalance() {
        if (sharedPreferences != null && textViewMainBalanceValue != null) {
            float currentBalance = sharedPreferences.getFloat(BALANCE_KEY, 0f);
            textViewMainBalanceValue.setText(String.format(Locale.getDefault(), "Số dư: %,.0fđ", currentBalance));
        }
    }
}
