package com.example.group1_duangua;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<BetHistory> historyList = new ArrayList<>();
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        recyclerView = findViewById(R.id.recyclerHistory);
        pieChart = findViewById(R.id.pieChart);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load dữ liệu từ SharedPreferences
        loadHistoryFromFile();

        adapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);

        setupPieChart();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // đóng màn hình hiện tại
        });
    }

    private void loadHistoryFromFile() {
        Gson gson = new Gson();
        Type type = new TypeToken<List<BetHistory>>() {}.getType();

        File file = new File(getFilesDir(), "bet_history.json");
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                historyList = gson.fromJson(isr, type);
                isr.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
                historyList = new ArrayList<>();
            }
        } else {
            historyList = new ArrayList<>();
        }
    }

    private void setupPieChart() {
        int winCount = 0, loseCount = 0;
        for (BetHistory bet : historyList) {
            if (bet.isWin()) winCount++;
            else loseCount++;
        }

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(winCount, "Thắng"));
        entries.add(new PieEntry(loseCount, "Thua"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        // Set màu cụ thể cho từng entry (theo thứ tự add vào entries)
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#4CAF50")); // Xanh lá cho Thắng
        colors.add(Color.parseColor("#F44336")); // Đỏ cho Thua
        dataSet.setColors(colors);

        // Chỉnh chữ trong chart
        dataSet.setValueTextSize(18f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // hiển thị số nguyên
            }
        });
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}

