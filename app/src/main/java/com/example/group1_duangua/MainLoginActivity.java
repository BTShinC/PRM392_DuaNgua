package com.example.group1_duangua;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainLoginActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword;
    Button btnLogin, btnRegister;
    SharedPreferences sharedPreferences;
    MediaPlayer mediaPlayer; // nhạc nền

    // Volume control
    SeekBar seekBarVolume;
    ImageView ivVolumeDown, ivVolumeUp;
    AudioManager audioManager;

    private String defaultUser = "player1";
    private String defaultPass = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        seekBarVolume = findViewById(R.id.seekBarVolume);
        ivVolumeDown = findViewById(R.id.ivVolumeDown);
        ivVolumeUp = findViewById(R.id.ivVolumeUp);

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // 🎵 Phát nhạc nền
        mediaPlayer = MediaPlayer.create(this, R.raw.music_header);
        mediaPlayer.setLooping(true);  // lặp lại vô hạn
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.start();

        // (Tuỳ chọn) Tăng gain bằng LoudnessEnhancer
        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != AudioManager.ERROR) {
            LoudnessEnhancer enhancer = new LoudnessEnhancer(audioSessionId);
            enhancer.setTargetGain(1500);
            enhancer.setEnabled(true);
        }

        // 🔊 Setup volume control
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        // Ép volume hệ thống max ngay khi mở app
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

        seekBarVolume.setMax(maxVolume);
        seekBarVolume.setProgress(maxVolume);

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ivVolumeDown.setOnClickListener(v -> {
            int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (current > 0) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current - 1, 0);
                seekBarVolume.setProgress(current - 1);
            }
        });

        ivVolumeUp.setOnClickListener(v -> {
            int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (current < maxVolume) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current + 1, 0);
                seekBarVolume.setProgress(current + 1);
            }
        });

        // 🟢 Sự kiện Login
        btnLogin.setOnClickListener(v -> {
            String user = edtUsername.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

            String savedUser = sharedPreferences.getString("username", defaultUser);
            String savedPass = sharedPreferences.getString("password", defaultPass);

            if (user.equals(savedUser) && pass.equals(savedPass)) {
                Toast.makeText(MainLoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                // TODO: chuyển sang màn hình game chính
            } else {
                Toast.makeText(MainLoginActivity.this, "Sai Tên đăng nhập hoặc Mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });

        // 🟢 Sự kiện Register
        btnRegister.setOnClickListener(v -> {
            String newUser = edtUsername.getText().toString().trim();
            String newPass = edtPassword.getText().toString().trim();

            if (newUser.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(MainLoginActivity.this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", newUser);
                editor.putString("password", newPass);
                editor.apply();
                Toast.makeText(MainLoginActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
