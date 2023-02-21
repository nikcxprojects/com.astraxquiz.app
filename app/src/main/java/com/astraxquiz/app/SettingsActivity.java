package com.astraxquiz.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    ImageView imageBack, imageMusic, imageVibration, imageSounds;
    TextView text_cleanProgress;

    boolean music=true, vibration=true, sounds=true;

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        music = sharedPreferences.getBoolean("music",true);
        vibration = sharedPreferences.getBoolean("vibration",true);
        sounds = sharedPreferences.getBoolean("sounds",true);

        text_cleanProgress = findViewById(R.id.text_cleanProgress);
        imageBack = findViewById(R.id.imageBack);
        imageMusic = findViewById(R.id.imageMusic);
        imageVibration = findViewById(R.id.imageVibration);
        imageSounds = findViewById(R.id.imageSounds);

        if (!music) {
            imageMusic.setImageResource(R.drawable.settings_off); }
        else {
            imageMusic.setImageResource(R.drawable.settings_on); }

        if (!vibration) {
            imageVibration.setImageResource(R.drawable.settings_off); }
        else {
            imageVibration.setImageResource(R.drawable.settings_on); }

        if (!sounds) {
            imageSounds.setImageResource(R.drawable.settings_off); }
        else {
            imageSounds.setImageResource(R.drawable.settings_on); }

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent); finish();
            }
        });

        imageMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music = !music;
                if (music) {
                    editor.putBoolean("music", true).apply();
                    imageMusic.setImageResource(R.drawable.settings_on);
                }
                else {
                    editor.putBoolean("music", false).apply();
                    imageMusic.setImageResource(R.drawable.settings_off);
                }
            }
        });

        imageVibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibration = !vibration;
                if (vibration) {
                    editor.putBoolean("vibration", true).apply();
                    imageVibration.setImageResource(R.drawable.settings_on);
                }
                else {
                    editor.putBoolean("vibration", false).apply();
                    imageVibration.setImageResource(R.drawable.settings_off);
                }
            }
        });

        imageSounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sounds = !sounds;
                if (sounds) {
                    editor.putBoolean("sounds", true).apply();
                    imageSounds.setImageResource(R.drawable.settings_on);
                }
                else {
                    editor.putBoolean("sounds", false).apply();
                    imageSounds.setImageResource(R.drawable.settings_off);
                }
            }
        });

        text_cleanProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().remove("Solo_mode").apply();
                sharedPreferences.edit().remove("Time_Attack_mode").apply();
                sharedPreferences.edit().remove("Elimination_mode").apply();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Window w = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
