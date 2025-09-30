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

import java.io.IOException;
import java.io.InputStream;
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
        String json = loadJSONFromAsset("bet_history.json");

        Gson gson = new Gson();
        Type type = new TypeToken<List<BetHistory>>(){}.getType();
        historyList = gson.fromJson(json, type);

        if (historyList == null) historyList = new ArrayList<>();
    }

    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
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

