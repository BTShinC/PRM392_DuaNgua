package com.example.group1_duangua;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class RaceActivity extends AppCompatActivity {

    private MediaPlayer raceBeepPlayer;    // race_beep.mp3
    private MediaPlayer idlePlayer;        // race_car_idle.mp3 (loop while waiting)
    private MediaPlayer racingPlayer;      // racing_f1.mp3 (loop during race)
    private MediaPlayer crowdPlayer;       // crowd_cheers.mp3 (on win)

    private SeekBar seekCar1, seekCar2, seekCar3;
    private final Handler handler = new Handler();
    private final Random random = new Random();
    private boolean raceFinished = false;

    // Runnable để cập nhật tiến trình xe; lưu tham chiếu để remove khi cần
    private final Runnable runCarsRunnable = new Runnable() {
        @Override
        public void run() {
            if (raceFinished) return;

            // tăng progress ngẫu nhiên (tùy chỉnh mạnh yếu ở đây)
            seekCar1.setProgress(Math.min(100, seekCar1.getProgress() + (random.nextInt(5) )));
            seekCar2.setProgress(Math.min(100, seekCar2.getProgress() + (random.nextInt(5))));
            seekCar3.setProgress(Math.min(100, seekCar3.getProgress() + (random.nextInt(5))));

            // kiểm tra winner
            if (seekCar1.getProgress() >= 100) {
                finishRace(1);
            } else if (seekCar2.getProgress() >= 100) {
                finishRace(2);
            } else if (seekCar3.getProgress() >= 100) {
                finishRace(3);
            } else {
                // tiếp tục cập nhật
                handler.postDelayed(this, 500); // 80ms interval (tùy chỉnh)
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        seekCar1 = findViewById(R.id.seekCar1);
        seekCar2 = findViewById(R.id.seekCar2);
        seekCar3 = findViewById(R.id.seekCar3);

        // disable seekbars để người dùng không kéo
        seekCar1.setEnabled(false);
        seekCar2.setEnabled(false);
        seekCar3.setEnabled(false);

        // reset progress
        seekCar1.setProgress(0);
        seekCar2.setProgress(0);
        seekCar3.setProgress(0);

        // Start audio sequence: race_beep + race_car_idle (together)
        startBeepAndIdle();
    }

    // Start race_beep and idle simultaneously.
    private void startBeepAndIdle() {
        try {
            // init idle sound (looping) first
            idlePlayer = MediaPlayer.create(this, R.raw.race_car_idle);
            if (idlePlayer != null) {
                idlePlayer.setLooping(true);
                idlePlayer.start();
            }

            // then start beep (one-shot)
            raceBeepPlayer = MediaPlayer.create(this, R.raw.race_beep);
            if (raceBeepPlayer != null) {
                raceBeepPlayer.setOnCompletionListener(mp -> {
                    // When beep finishes: stop idle and start the actual race (with racing sound)
                    stopAndRelease(idlePlayer);
                    idlePlayer = null;
                    startRacingSoundAndRun();
                });
                raceBeepPlayer.start();
            } else {
                // fallback if beep missing: directly start race
                stopAndRelease(idlePlayer);
                idlePlayer = null;
                startRacingSoundAndRun();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // đảm bảo không bị treo nếu audio lỗi
            stopAndRelease(idlePlayer);
            stopAndRelease(raceBeepPlayer);
            startRacingSoundAndRun();
        }
    }

    // Start racing loop sound and begin cars running.
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

        // Reset progress again (safety) and start runnable
        runOnUiThread(() -> {
            seekCar1.setProgress(0);
            seekCar2.setProgress(0);
            seekCar3.setProgress(0);
            raceFinished = false;
            handler.postDelayed(runCarsRunnable, 100);
            Toast.makeText(RaceActivity.this, "Cuộc đua bắt đầu!", Toast.LENGTH_SHORT).show();
        });
    }

    // Called when a car wins (1,2,3)
    private synchronized void finishRace(int carIndex) {
        if (raceFinished) return; // bảo vệ multi-calls
        raceFinished = true;

        // dừng runnable
        handler.removeCallbacks(runCarsRunnable);

        // dừng racing sound
        stopAndRelease(racingPlayer);

        // phát crowd cheers
        try {
            crowdPlayer = MediaPlayer.create(this, R.raw.crowd_cheers);
            if (crowdPlayer != null) {
                crowdPlayer.start();
                // release sau khi hoàn thành
                crowdPlayer.setOnCompletionListener(mp -> stopAndRelease(crowdPlayer));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // thông báo winner
        final String message = "Xe " + carIndex + " về đích!";
        runOnUiThread(() -> Toast.makeText(RaceActivity.this, message, Toast.LENGTH_LONG).show());
    }

    // helper: stop + release MediaPlayer if non-null
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
        // nếu activity bị tắt/ẩn, dừng nhạc và stop race
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
        handler.removeCallbacksAndMessages(null);
        stopAndRelease(raceBeepPlayer);
        stopAndRelease(idlePlayer);
        stopAndRelease(racingPlayer);
        stopAndRelease(crowdPlayer);
    }
}
