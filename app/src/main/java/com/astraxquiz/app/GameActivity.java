package com.astraxquiz.app;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements ClickInterface,EndLevel {

    RecyclerView recyclerView;
    Adapter_letter adapter;

    int hints=0;
    int time=0;
    public static int mistakes=0;
    int level;
    String mode;

    public static String word;
    public static int image;
    public static int count;
    public static TextView textInfoMode;

    TextView textLevel,textMode;
    TextView textHints;
    TextView textHint;
    ImageView imageView;

    TextView letter1,letter2,letter3,letter4,letter5,letter6,letter7,letter8,letter9,letter10;

    int windowwidth,windowheight;

    TextView[] letters = null;

    SharedPreferences.Editor editor;

    CountDownTimer timer;
    boolean isPause=false;
    boolean music;
    boolean vibration;
    boolean sounds;
    MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        music = sharedPreferences.getBoolean("music",true);
        vibration = sharedPreferences.getBoolean("vibration",true);
        sounds = sharedPreferences.getBoolean("sounds",true);

        Bundle extra = getIntent().getExtras();
        mode = extra.getString("mode");
        level = extra.getInt("level");

        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.media1); //загрузка пеера
        mPlayer.setLooping(true);//зацикливание
        mPlayer.setVolume(0.5f, 0.5f);

        initViews(level, mode);

    }

    private void initViews(int level, String mode){

        hints=0;
        count=0;

        letter1 = findViewById(R.id.letter1);
        letter2 = findViewById(R.id.letter2);
        letter3 = findViewById(R.id.letter3);
        letter4 = findViewById(R.id.letter4);
        letter5 = findViewById(R.id.letter5);
        letter6 = findViewById(R.id.letter6);
        letter7 = findViewById(R.id.letter7);
        letter8 = findViewById(R.id.letter8);
        letter9 = findViewById(R.id.letter9);
        letter10 = findViewById(R.id.letter10);

        letters = new TextView[]{
                letter1, letter2, letter3, letter4, letter5,
                letter6, letter7, letter8, letter9, letter10};

        textHints = findViewById(R.id.textHints);
        textHint = findViewById(R.id.textHint);
        textLevel = findViewById(R.id.textLevel);
        textInfoMode = findViewById(R.id.textInfoMode);
        textMode = findViewById(R.id.textMode);
        imageView = findViewById(R.id.imageView);
        recyclerView = findViewById(R.id.recyclerView);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        List<Alphabet> alphabetList = new ArrayList<>();

        if (mode.equals("Solo_mode")){
            textInfoMode.setText("");
            textLevel.setText("Level "+ level);
            textMode.setText("Solo mode");
            word = Constants.WordsSolo[level -1];
            image = Constants.ImagesSolo[level -1];
        }
        else if (mode.equals("Time_Attack_mode")){
            textInfoMode.setText("02:00");
            textLevel.setText("Level "+ level);
            textMode.setText("Time Attack");
            word = Constants.WordsTimeAttack[level -1];
            image = Constants.ImagesTimeAttack[level -1];
            time=30;
            StartTimer();
        }
        else if (mode.equals("Elimination_mode")){
            word = Constants.WordsElimination[level -1];
            image = Constants.ImagesElimination[level -1];
            mistakes=word.length()/2;
            textInfoMode.setText(mistakes+" mistakes left");
            textLevel.setText("Level "+ level);
            textMode.setText("Elimination");
        }

        for (int i = 0; i < Constants.alphabet.length; i++) {
            alphabetList.add(new Alphabet(i,Constants.alphabet[i],false));
        }

        for (int i = 0; i<word.length();i++){
            letters[i].setText("");
            letters[i].setBackgroundResource(R.drawable.empty_cell);
            letters[i].setVisibility(View.VISIBLE);
        }

        textHints.setText(""+hints+"/3");
        imageView.setImageResource(image);
        getMaxSizeLetter(word.length());

        textHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hints<3){
                    hints++;
                    textHints.setText(""+hints+"/3");
                    for (int i = 0; i <word.length();i++){
                        if (letters[i].getText().toString().equals("")){
                            char let = word.charAt(i);
                            for (int j = 0; j <word.length();j++){
                                if (String.valueOf(word.charAt(j)).equals(String.valueOf(let))){
                                    letters[j].setText(""+let);
                                    letters[j].setBackgroundResource(R.drawable.style_level_completed);
                                    count++;
                                }
                            }
                            alphabetList.get(getPosition(String.valueOf(word.charAt(i)))).setActive(true);
                            adapter.notifyItemChanged(getPosition(String.valueOf(word.charAt(i))));
                            if (count == word.length()){
                                EndLevel(level, "win");
                            }
                            break;
                        }
                    }
                }
            }
        });

        adapter = new Adapter_letter(getApplicationContext(),alphabetList, level, word, mode,GameActivity.this,
                GameActivity.this,sounds,music,vibration);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private void StartTimer() {
        timer = new CountDownTimer(time*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time--;
                textInfoMode.setText(""+new SimpleDateFormat("mm:ss").format(time*1000));
            }

            @Override
            public void onFinish() {
                time = 0;
                textInfoMode.setText(""+new SimpleDateFormat("mm:ss").format(time*1000));
                DialogEnd("Lose");
            }
        }.start();
    }

    private void DialogPause() {
        Dialog pauseDialog = new Dialog(GameActivity.this,R.style.FullScreenDialog);
        pauseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pauseDialog.setContentView(R.layout.dialog_pause);
        pauseDialog.setCanceledOnTouchOutside(false);
        pauseDialog.setCancelable(false);
        pauseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pauseDialog.getWindow().setDimAmount(0.5f);
        pauseDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        pauseDialog.show();
        isPause = true;

        LinearLayout linear_dialog = pauseDialog.findViewById(R.id.linear_dialog);
        linear_dialog.setAlpha(0.6f);
        linear_dialog.setScaleX(0.8f);
        linear_dialog.setScaleY(0.8f);
        linear_dialog.animate().alpha(1.0f)
                .setDuration(500)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(300)
                .start();

        TextView resume = pauseDialog.findViewById(R.id.resume);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer!=null) {
                    timer.start(); }
               /* if (sound)
                    sound_Level.play(sound_btn, 0.8f, 0.8f, 0, 0, 1);*/
                isPause = false;
                pauseDialog.dismiss();
            }
        });

        TextView quit = pauseDialog.findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this,MainActivity.class);
                startActivity(intent); finish();
                /*if (sound)
                    sound_Level.play(sound_btn, 0.8f, 0.8f, 0, 0, 1);*/
            }
        });
    }

    private void DialogEnd(String status) {
        Dialog endDialog = new Dialog(GameActivity.this,R.style.FullScreenDialog);
        endDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        endDialog.setContentView(R.layout.dialog_end);
        endDialog.setCanceledOnTouchOutside(false);
        endDialog.setCancelable(false);
        endDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        endDialog.getWindow().setDimAmount(0.5f);
        endDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        endDialog.show();

        LinearLayout linear_dialog = endDialog.findViewById(R.id.linear_dialog);
        linear_dialog.setAlpha(0.6f);
        linear_dialog.setScaleX(0.8f);
        linear_dialog.setScaleY(0.8f);
        linear_dialog.animate().alpha(1.0f)
                .setDuration(500)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(300)
                .start();

        TextView restart = endDialog.findViewById(R.id.restart);

        TextView text_status = endDialog.findViewById(R.id.text_status);
        if (status.equals("Won")) {
            restart.setVisibility(View.GONE);
            text_status.setText("You have Won");
        }
        else if (status.equals("Lose")){
            text_status.setText("You have Lost");
        }
        else if (status.equals("Mistakes")){
            text_status.setText("You made a lot of mistakes");
        }

        TextView quit = endDialog.findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this,MainActivity.class);
                startActivity(intent); finish();
                endDialog.dismiss();
                /*if (sound)
                    sound_Level.play(sound_btn, 0.8f, 0.8f, 0, 0, 1);*/
            }
        });


        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initViews(level, mode);
                endDialog.dismiss();
                /*if (sound)
                    sound_Level.play(sound_btn, 0.8f, 0.8f, 0, 0, 1);*/
            }
        });
    }

    private int getPosition(String c) {
        for (int i = 0; i < Constants.alphabet.length; i++)
        {
            if (Constants.alphabet[i].equals(c)) {
                return i;
            }
        }
        return -1;
    }

    private void getMaxSizeLetter(int length) {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        windowwidth = displaymetrics.widthPixels;
        windowheight = displaymetrics.heightPixels;

        if (length>7) {
           Constants.countLetter = length;
        }
        else{
            Constants.countLetter = 7;
        }

        int size = (windowwidth / Constants.countLetter) - 10;

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) letter1.getLayoutParams();
        lp.width = size;
        lp.height = size;
        letter1.setLayoutParams(lp);
        letter2.setLayoutParams(lp);
        letter3.setLayoutParams(lp);
        letter4.setLayoutParams(lp);
        letter5.setLayoutParams(lp);
        letter6.setLayoutParams(lp);
        letter7.setLayoutParams(lp);
        letter8.setLayoutParams(lp);
        letter9.setLayoutParams(lp);
        letter10.setLayoutParams(lp);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null&&music) {
            mPlayer.pause();//пауза плеера
        }
        if (timer!=null&&mode.equals("Time_Attack_mode")) {
            timer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Window w = getWindow();
        if (!mPlayer.isPlaying()&&music){
            mPlayer.start();//старт
        }

        if (!isPause&&time<30&&mode.equals("Time_Attack_mode")){ DialogPause(); }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void LevelClick(int i, String mode) {
        //.....
    }

    @Override
    public void LetterClick(String letter, int pos) {
        //Toast.makeText(getApplicationContext(), ""+letter, Toast.LENGTH_SHORT).show();
        Log.d("LetterClick",""+letter);
        letters[pos].setText(""+letter);
        letters[pos].setBackgroundResource(R.drawable.style_level_completed);

    }

    @Override
    public void onBackPressed() {
        if (timer!=null&&mode.equals("Time_Attack_mode")) { timer.cancel(); }
        if (!isPause&&time<120&&mode.equals("Time_Attack_mode")){ DialogPause(); }
        else{
            Intent intent = new Intent(GameActivity.this,MainActivity.class);
            startActivity(intent); finish();
        }
    }

    @Override
    public void EndLevel(int level, String status) {
        if (status.equals("Lose_mistakes")){
            DialogEnd("Mistakes");
        }
        else if (status.equals("win")) {
            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
            editor = sharedPreferences.edit();
            this.level = level+1;
            int nextLevel = this.level;
            if (level>sharedPreferences.getInt(""+mode,0))
                editor.putInt(""+mode,level).apply();
            if (level < 20) {
                if (time > 0&&mode.equals("Time_Attack_mode"))
                    timer.cancel();
                initViews(nextLevel, mode);
            } else {
                DialogEnd("Won");
            /*Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent); finish();*/
            }
        }
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        if (mPlayer.isPlaying()&&music)
            mPlayer.stop();//стоп плеера
    }
}
