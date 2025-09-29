package com.example.group1_duangua;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class RaceActivity extends AppCompatActivity {

    private MediaPlayer raceBeepPlayer, idlePlayer, racingPlayer, crowdPlayer;
    private SeekBar seekCar1, seekCar2, seekCar3;
    private final Handler handler = new Handler();
    private final Random random = new Random();
    private boolean raceFinished = false;

    // --- Data from Betting screen ---
    private ArrayList<Bet> userBets;

    // For track animation
    private ImageView trackImage1, trackImage2;
    private ValueAnimator trackAnimator;
    private RelativeLayout raceArea;
    private int trackRenderWidth = 0;

    // For balance display
    private TextView tvRaceBalance; // TextView for displaying balance in RaceActivity
    private SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS_NAME = "MyPrefs"; // Ensure this matches other activities
    public static final String BALANCE_KEY = "balance";     // Ensure this matches other activities

    private final Runnable runCarsRunnable = new Runnable() {
        @Override
        public void run() {
            if (raceFinished) return;

            seekCar1.setProgress(Math.min(100, seekCar1.getProgress() + random.nextInt(5)));
            seekCar2.setProgress(Math.min(100, seekCar2.getProgress() + random.nextInt(5)));
            seekCar3.setProgress(Math.min(100, seekCar3.getProgress() + random.nextInt(5)));

            if (seekCar1.getProgress() >= 100) finishRace(1);
            else if (seekCar2.getProgress() >= 100) finishRace(2);
            else if (seekCar3.getProgress() >= 100) finishRace(3);
            else handler.postDelayed(this, 100); 
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        // Bind balance TextView (ID in activity_race.xml is tvBalance)
        tvRaceBalance = findViewById(R.id.tvBalance);
        // loadAndDisplayBalance(); // Called in onResume which is also called after onCreate

        // --- Get bet data from Intent ---
        Intent intent = getIntent();
        userBets = intent.getParcelableArrayListExtra("userBets");

        // --- Bind UI for cars ---
        seekCar1 = findViewById(R.id.seekCar1);
        seekCar2 = findViewById(R.id.seekCar2);
        seekCar3 = findViewById(R.id.seekCar3);

        seekCar1.setEnabled(false);
        seekCar2.setEnabled(false);
        seekCar3.setEnabled(false);

        seekCar1.setProgress(0);
        seekCar2.setProgress(0);
        seekCar3.setProgress(0);

        // Initialize track views for animation
        trackImage1 = findViewById(R.id.trackImage1);
        trackImage2 = findViewById(R.id.trackImage2);
        raceArea = findViewById(R.id.raceArea);

        raceArea.post(new Runnable() {
            @Override
            public void run() {
                trackRenderWidth = raceArea.getWidth();
                if (trackRenderWidth > 0) {
                    setupTrackAnimator(trackRenderWidth);
                }
            }
        });

        startBeepAndIdle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayBalance(); // Reload balance when activity resumes
    }

    private void loadAndDisplayBalance() {
        if (sharedPreferences != null && tvRaceBalance != null) {
            float currentBalance = sharedPreferences.getFloat(BALANCE_KEY, 0f);
            // Ensure tvBalance is correctly identified and is the one in RaceActivity's layout
            tvRaceBalance.setText(String.format(Locale.getDefault(), "Số dư: %,.0fđ", currentBalance));
        }
    }

    private void setupTrackAnimator(final int trackWidth) {
        if (trackAnimator != null) {
            trackAnimator.cancel();
        }
        trackAnimator = ValueAnimator.ofFloat(0.0f, (float) trackWidth);
        trackAnimator.setRepeatCount(ValueAnimator.INFINITE);
        trackAnimator.setInterpolator(new LinearInterpolator());
        trackAnimator.setDuration(10000); // 10 seconds for one full scroll

        trackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                trackImage1.setTranslationX(-value);
                trackImage2.setTranslationX(-value + trackWidth);
            }
        });
    }

    private void startBeepAndIdle() {
        try {
            idlePlayer = MediaPlayer.create(this, R.raw.race_car_idle);
            if (idlePlayer != null) {
                idlePlayer.setLooping(true);
                idlePlayer.start();
            }

            raceBeepPlayer = MediaPlayer.create(this, R.raw.race_beep);
            if (raceBeepPlayer != null) {
                raceBeepPlayer.setOnCompletionListener(mp -> {
                    stopAndRelease(idlePlayer);
                    idlePlayer = null;
                    startRacingSoundAndRun();
                });
                raceBeepPlayer.start();
            } else {
                stopAndRelease(idlePlayer);
                idlePlayer = null;
                startRacingSoundAndRun();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopAndRelease(idlePlayer);
            stopAndRelease(raceBeepPlayer);
            startRacingSoundAndRun();
        }
    }

    private void startRacingSoundAndRun() {
        try {
            racingPlayer = MediaPlayer.create(this, R.raw.racing_f1);
            if (racingPlayer != null) {
                racingPlayer.setLooping(true);
                racingPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        runOnUiThread(() -> {
            seekCar1.setProgress(0);
            seekCar2.setProgress(0);
            seekCar3.setProgress(0);
            raceFinished = false;
            handler.postDelayed(runCarsRunnable, 100);
            Toast.makeText(RaceActivity.this, "Cuộc đua bắt đầu!", Toast.LENGTH_SHORT).show();

            if (trackAnimator != null && !trackAnimator.isRunning() && trackRenderWidth > 0) {
                trackImage1.setTranslationX(0f); 
                trackImage2.setTranslationX(trackRenderWidth);
                trackAnimator.start();
            }
        });
    }

    private synchronized void finishRace(int winnerCar) {
        if (raceFinished) return;
        raceFinished = true;

        handler.removeCallbacks(runCarsRunnable);

        if (trackAnimator != null && trackAnimator.isRunning()) {
            trackAnimator.cancel();
        }

        stopAndRelease(racingPlayer);

        try {
            crowdPlayer = MediaPlayer.create(this, R.raw.crowd_cheers);
            if (crowdPlayer != null) {
                crowdPlayer.start();
                crowdPlayer.setOnCompletionListener(MediaPlayer::release); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(RaceActivity.this, ResultActivity.class);
        intent.putExtra("winnerCar", winnerCar);
        intent.putParcelableArrayListExtra("userBets", userBets);
        startActivity(intent);
        finish();
    }

    private void stopAndRelease(MediaPlayer mp) {
        try {
            if (mp != null) {
                if (mp.isPlaying()) {
                    mp.stop();
                }
                mp.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (trackAnimator != null && trackAnimator.isRunning()) {
            trackAnimator.cancel();
        }
        handler.removeCallbacks(runCarsRunnable);
        raceFinished = true;
        stopAndRelease(raceBeepPlayer);
        raceBeepPlayer = null;
        stopAndRelease(idlePlayer);
        idlePlayer = null;
        stopAndRelease(racingPlayer);
        racingPlayer = null;
        stopAndRelease(crowdPlayer);
        crowdPlayer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (trackAnimator != null) {
            trackAnimator.cancel();
            trackAnimator = null;
        }
        handler.removeCallbacksAndMessages(null);
        stopAndRelease(raceBeepPlayer);
        stopAndRelease(idlePlayer);
        stopAndRelease(racingPlayer);
        stopAndRelease(crowdPlayer);
    }
}
