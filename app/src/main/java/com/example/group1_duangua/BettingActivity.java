package com.example.group1_duangua;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BettingActivity extends AppCompatActivity {

    private EditText edtBetCar1, edtBetCar2, edtBetCar3;
    private TextView tvBalance;
    private Button btnStartRace;

    private int balance = 1000; // default nếu chưa có dữ liệu
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_betting_seekbar);

        // Ánh xạ
        edtBetCar1 = findViewById(R.id.edtBetCar1);
        edtBetCar2 = findViewById(R.id.edtBetCar2);
        edtBetCar3 = findViewById(R.id.edtBetCar3);
        tvBalance = findViewById(R.id.tvBalance);
        btnStartRace = findViewById(R.id.btnStartRace);

        // Lấy số dư từ SharedPreferences
        prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        balance = prefs.getInt("balance", 1000);
        tvBalance.setText("Số dư: " + balance + "$");

        // Sự kiện nút bắt đầu đua
        btnStartRace.setOnClickListener(v -> validateBets());
    }

    private void validateBets() {
        int bet1 = parseBet(edtBetCar1.getText().toString());
        int bet2 = parseBet(edtBetCar2.getText().toString());
        int bet3 = parseBet(edtBetCar3.getText().toString());

        int totalBet = bet1 + bet2 + bet3;

        if (totalBet == 0) {
            Toast.makeText(this, "Vui lòng nhập tiền cược cho ít nhất 1 xe!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (totalBet > balance) {
            Toast.makeText(this, "Tổng tiền cược vượt quá số dư!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Trừ tiền và lưu lại
        balance -= totalBet;
        prefs.edit().putInt("balance", balance).apply();
        tvBalance.setText("Số dư: " + balance + "$");

        // TODO: Bắt đầu đua xe ở đây
        Toast.makeText(this, "Đua bắt đầu! Tổng cược: " + totalBet + "$", Toast.LENGTH_SHORT).show();
    }

    private int parseBet(String betStr) {
        if (TextUtils.isEmpty(betStr)) return 0;
        try {
            int bet = Integer.parseInt(betStr);
            if (bet < 0) return 0;
            return bet;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
