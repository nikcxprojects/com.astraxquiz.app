package com.astraxquiz.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ClickInterface {

    LinearLayout Solo_mode,Time_Attack_mode,Elimination_mode;

    RecyclerView recyclerView;
    Adapter_level adapter;

    TextView textInfoMode;
    ImageView imageSettings;

    int level;

    MediaPlayer mPlayer;

    SharedPreferences.Editor editor;
    boolean sounds;
    boolean music;
    boolean vibration;

    SoundPool sound_Level;
    int sound_btn;
    final int MAX_STREAMS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        music = sharedPreferences.getBoolean("music",true);
        vibration = sharedPreferences.getBoolean("vibration",true);
        sounds = sharedPreferences.getBoolean("sounds",true);

        initViews();
        GetData("Solo_mode");
    }

    private void initViews(){

        textInfoMode = findViewById(R.id.textInfoMode);
        imageSettings = findViewById(R.id.imageSettings);

        recyclerView = findViewById(R.id.recyclerView);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        Solo_mode = findViewById(R.id.Solo_mode);
        Time_Attack_mode = findViewById(R.id.Time_Attack_mode);
        Elimination_mode = findViewById(R.id.Elimination_mode);

        Solo_mode.setBackgroundResource(R.drawable.mode_stroke);

        imageSettings.setOnClickListener(this);
        Solo_mode.setOnClickListener(this);
        Time_Attack_mode.setOnClickListener(this);
        Elimination_mode.setOnClickListener(this);

        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.media1); //загрузка пеера
        mPlayer.setLooping(true);//зацикливание
        mPlayer.setVolume(0.5f, 0.5f);

        sound_Level = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        sound_btn = sound_Level.load(this,R.raw.click1,1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Solo_mode:
                if (sounds) {
                    sound_Level.play(sound_btn, 0.8f, 0.8f, 0, 0, 1);
                }
                GetData("Solo_mode");
                Solo_mode.setBackgroundResource(R.drawable.mode_stroke);
                Time_Attack_mode.setBackgroundResource(0);
                Elimination_mode.setBackgroundResource(0);
                break;
            case R.id.Time_Attack_mode:
                if (sounds) {
                    sound_Level.play(sound_btn, 0.8f, 0.8f, 0, 0, 1);
                }
                GetData("Time_Attack_mode");
                Solo_mode.setBackgroundResource(0);
                Time_Attack_mode.setBackgroundResource(R.drawable.mode_stroke);
                Elimination_mode.setBackgroundResource(0);
                break;
            case R.id.Elimination_mode:
                if (sounds) {
                    sound_Level.play(sound_btn, 0.8f, 0.8f, 0, 0, 1);
                }
                GetData("Elimination_mode");
                Solo_mode.setBackgroundResource(0);
                Time_Attack_mode.setBackgroundResource(0);
                Elimination_mode.setBackgroundResource(R.drawable.mode_stroke);
                break;
            case R.id.imageSettings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent); finish();
                break;
        }
    }

    private void GetData(String mode) {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        level = sharedPreferences.getInt(""+mode,0);
        if (level<20) {
            textInfoMode.setText("Level " + (level + 1));
        }else{
            textInfoMode.setText("Level " + level);
        }

        adapter = new Adapter_level(getApplicationContext(), mode,level, MainActivity.this,sounds);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        final Window w = getWindow();
        if (!mPlayer.isPlaying()&&music){
            mPlayer.start();//старт
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onPause(){
        super.onPause();
        if (mPlayer != null&&music) {
            mPlayer.pause();//пауза плеера
        }
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        if (mPlayer.isPlaying()&&music)
            mPlayer.stop();//стоп плеера
    }

    @Override
    public void LevelClick(int i, String mode) {
        Log.d("LevelClick",""+i+" "+mode);
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("mode",mode);
        intent.putExtra("level",i);
        startActivity(intent); finish();
    }

    @Override
    public void LetterClick(String letter, int pos) {
        //.....
    }
}