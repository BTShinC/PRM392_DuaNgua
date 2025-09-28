package com.example.group1_duangua;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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
    }
}