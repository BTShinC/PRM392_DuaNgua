package com.example.group1_duangua;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class RaceActivity extends AppCompatActivity {

    private MediaPlayer raceBeepPlayer, idlePlayer, racingPlayer, crowdPlayer;
    private SeekBar seekCar1, seekCar2, seekCar3;
    private final Handler handler = new Handler();
    private final Random random = new Random();
    private boolean raceFinished = false;

    // --- Data from Betting screen ---
    private ArrayList<Bet> userBets;

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

        // --- Get bet data from Intent ---
        Intent intent = getIntent();
        userBets = intent.getParcelableArrayListExtra("userBets");

        // --- Bind UI ---
        seekCar1 = findViewById(R.id.seekCar1);
        seekCar2 = findViewById(R.id.seekCar2);
        seekCar3 = findViewById(R.id.seekCar3);

        seekCar1.setEnabled(false);
        seekCar2.setEnabled(false);
        seekCar3.setEnabled(false);

        startBeepAndIdle();
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
                startRacingSoundAndRun(); // Fallback
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        raceFinished = false;
        handler.postDelayed(runCarsRunnable, 100);
    }

    private synchronized void finishRace(int winnerCar) {
        if (raceFinished) return;
        raceFinished = true;

        handler.removeCallbacks(runCarsRunnable);
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

        // --- Start ResultActivity and pass all data ---
        Intent intent = new Intent(RaceActivity.this, ResultActivity.class);
        intent.putExtra("winnerCar", winnerCar);
        intent.putParcelableArrayListExtra("userBets", userBets);
        startActivity(intent);
        finish();
    }

    private void stopAndRelease(MediaPlayer mp) {
        try {
            if (mp != null) {
                if (mp.isPlaying()) mp.stop();
                mp.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        raceFinished = true; // Stop race if activity is paused
        handler.removeCallbacks(runCarsRunnable);
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
        handler.removeCallbacksAndMessages(null);
    }
}
