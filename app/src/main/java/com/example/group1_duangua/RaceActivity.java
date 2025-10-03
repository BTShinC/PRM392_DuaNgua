package com.example.group1_duangua;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RaceActivity extends AppCompatActivity {

    private MediaPlayer raceBeepPlayer, idlePlayer, racingPlayer, crowdPlayer;
    private SeekBar seekCar1, seekCar2, seekCar3;
    private VideoView videoViewTrack;
    private final Handler handler = new Handler();
    private final Random random = new Random();
    private boolean raceFinished = false;
    private int currentVideoPosition = 0;

    private ArrayList<Bet> userBets;

    private TextView tvRaceBalance, tvCountdown;
    private SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS_NAME = "MyPrefs";
    public static final String BALANCE_KEY = "balance";

    private final Runnable runCarsRunnable = new Runnable() {
        @Override
        public void run() {
            if (raceFinished) return;

            seekCar1.setProgress(Math.min(100, seekCar1.getProgress() + random.nextInt(4)));
            seekCar2.setProgress(Math.min(100, seekCar2.getProgress() + random.nextInt(4)));
            seekCar3.setProgress(Math.min(100, seekCar3.getProgress() + random.nextInt(4)));

            if (seekCar1.getProgress() >= 100) finishRace(1);
            else if (seekCar2.getProgress() >= 100) finishRace(2);
            else if (seekCar3.getProgress() >= 100) finishRace(3);
            else handler.postDelayed(this, 170);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        tvRaceBalance = findViewById(R.id.tvBalance);
        tvCountdown = findViewById(R.id.tvCountdown);

        Intent intent = getIntent();
        userBets = intent.getParcelableArrayListExtra("userBets");

        seekCar1 = findViewById(R.id.seekCar1);
        seekCar2 = findViewById(R.id.seekCar2);
        seekCar3 = findViewById(R.id.seekCar3);

        seekCar1.setEnabled(false);
        seekCar2.setEnabled(false);
        seekCar3.setEnabled(false);

        seekCar1.setProgress(0);
        seekCar2.setProgress(0);
        seekCar3.setProgress(0);

        videoViewTrack = findViewById(R.id.videoViewTrack);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.racetrack;
        Uri videoUri = Uri.parse(videoPath);
        videoViewTrack.setVideoURI(videoUri);
        videoViewTrack.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            // Do NOT start video here. Video starts when cars move.
            // Call startBeepAndIdle directly after video is prepared.
            if (!isFinishing()) {
                 startBeepAndIdle();
            }
        });
        videoViewTrack.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(RaceActivity.this, "Không thể tải video đường đua", Toast.LENGTH_SHORT).show();
            if (!isFinishing()) {
                // Fallback to start sound sequence immediately if video fails
                startBeepAndIdle();
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayBalance();
        if (videoViewTrack != null) {
            if (currentVideoPosition > 0 && !videoViewTrack.isPlaying()) {
                videoViewTrack.seekTo(currentVideoPosition);
                videoViewTrack.start();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoViewTrack != null && videoViewTrack.isPlaying()) {
            currentVideoPosition = videoViewTrack.getCurrentPosition();
            videoViewTrack.pause();
        }
        handler.removeCallbacksAndMessages(null);
        raceFinished = true;

        stopAndRelease(raceBeepPlayer); raceBeepPlayer = null;
        stopAndRelease(idlePlayer); idlePlayer = null;
        stopAndRelease(racingPlayer); racingPlayer = null;
        stopAndRelease(crowdPlayer); crowdPlayer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoViewTrack != null) {
            videoViewTrack.stopPlayback();
            videoViewTrack = null;
        }
        handler.removeCallbacksAndMessages(null);
        stopAndRelease(raceBeepPlayer);
        stopAndRelease(idlePlayer);
        stopAndRelease(racingPlayer);
        stopAndRelease(crowdPlayer);
    }

    private void loadAndDisplayBalance() {
        if (sharedPreferences != null && tvRaceBalance != null) {
            float currentBalance = sharedPreferences.getFloat(BALANCE_KEY, 0f);
            tvRaceBalance.setText(String.format(Locale.getDefault(), "Số dư: %,.0fđ", currentBalance));
        }
    }

    private void startBeepAndIdle() {
        if (isFinishing() || raceFinished || (racingPlayer != null && racingPlayer.isPlaying()) || (raceBeepPlayer != null && raceBeepPlayer.isPlaying())) {
            return;
        }

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
                startCountdown();
            } else {
                stopAndRelease(idlePlayer);
                idlePlayer = null;
                startRacingSoundAndRun();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopAndRelease(idlePlayer);
            idlePlayer = null;
            stopAndRelease(raceBeepPlayer);
            raceBeepPlayer = null;
            startRacingSoundAndRun();
        }
    }

    private void startCountdown() {
        tvCountdown.setVisibility(View.VISIBLE);
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText(String.valueOf(millisUntilFinished / 1000 + 1));
            }

            public void onFinish() {
                tvCountdown.setText("Bắt đầu");
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    tvCountdown.setVisibility(View.GONE);
                }, 1000);
            }
        }.start();
    }

    private void startRacingSoundAndRun() {
        if (isFinishing() || raceFinished) {
            return;
        }

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
            if (isFinishing() || raceFinished) return;

            // Start video when cars are about to move
            if (videoViewTrack != null && !videoViewTrack.isPlaying()) {
                videoViewTrack.start();
            }

            seekCar1.setProgress(0);
            seekCar2.setProgress(0);
            seekCar3.setProgress(0);
            raceFinished = false;
            handler.postDelayed(runCarsRunnable, 10);
            Toast.makeText(RaceActivity.this, "Cuộc đua bắt đầu!", Toast.LENGTH_SHORT).show();
        });
    }

    private synchronized void finishRace(int winnerCar) {
        if (raceFinished) return;
        raceFinished = true;

        handler.removeCallbacks(runCarsRunnable);
        stopAndRelease(racingPlayer); racingPlayer = null;

        try {
            crowdPlayer = MediaPlayer.create(this, R.raw.crowd_cheers);
            if (crowdPlayer != null) {
                crowdPlayer.start();
                crowdPlayer.setOnCompletionListener(MediaPlayer::release);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isFinishing()) {
            updateBetHistoryJson(winnerCar);
            Intent intent = new Intent(RaceActivity.this, ResultActivity.class);
            intent.putExtra("winnerCar", winnerCar);
            intent.putParcelableArrayListExtra("userBets", userBets);
            startActivity(intent);
            finish();
        }
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

    private void updateBetHistoryJson(int winnerCar) {
        Gson gson = new Gson();
        String expectedCarName = "Xe số " + winnerCar;
        ArrayList<BetHistory> userBets = getIntent().getParcelableArrayListExtra("betHistories");

        try {
            // Cập nhật trạng thái thắng
            if (userBets != null) {
                for (BetHistory userBet : userBets) {
                    if (userBet.getCarName().equals(expectedCarName)) {
                        userBet.setWin(true);
                    }
                }
            }


            File file = new File(getFilesDir(), "bet_history.json");

            // Đọc dữ liệu cũ từ internal storage (KHÔNG phải assets)
            List<BetHistory> historyList;
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                Type type = new TypeToken<List<BetHistory>>() {}.getType();
                historyList = gson.fromJson(isr, type);
                isr.close();
                fis.close();
            } else {
                historyList = new ArrayList<>();
            }

            // Gộp dữ liệu mới
            if (userBets != null) {
                historyList.addAll(userBets);
            }

            // Lưu lại file JSON
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            gson.toJson(historyList, osw);
            osw.flush();
            osw.close();
            fos.close();

            Log.d("DEBUG_JSON", "Updated bet_history.json successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
