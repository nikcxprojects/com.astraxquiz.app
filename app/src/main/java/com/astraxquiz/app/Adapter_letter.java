package com.astraxquiz.app;

import static android.content.Context.VIBRATOR_SERVICE;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter_letter extends RecyclerView.Adapter<Adapter_letter.ViewHolder> {

    Context activity;
    ClickInterface clickInterface;
    EndLevel endLevel;
    List<Alphabet> alphabetList;
    String word;
    String mode;
    int level;
    SoundPool sound_Level;
    int sound_items;
    final int MAX_STREAMS = 5;
    Vibrator vibrator;
    boolean sound;
    boolean music;
    boolean vibrate;

    public Adapter_letter(Context activity, List<Alphabet> alphabetList, int level, String word, String mode,
                          ClickInterface clickInterface, EndLevel endLevel, boolean sound, boolean music, boolean vibrate) {
        this.activity = activity;
        this.alphabetList = alphabetList;
        this.clickInterface = clickInterface;
        this.endLevel = endLevel;
        this.level = level;
        this.mode = mode;
        this.word = word;
        this.sound = sound;
        this.music = music;
        this.vibrate = vibrate;

        sound_Level = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        sound_items = sound_Level.load(activity,R.raw.click5,1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.letter, viewGroup, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (!alphabetList.get(i).getActive()) {

            viewHolder.text_level.setText("" + alphabetList.get(i).getLetter());

            viewHolder.text_level.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    char[] myCharArray = word.toCharArray();
                    if (word.contains(alphabetList.get(i).getLetter())) {
                        if (sound) {
                            sound_Level.play(sound_items, 0.8f, 0.8f, 0, 0, 1);
                        }
                        for (int j = 0; j < myCharArray.length; j++) {
                            if (alphabetList.get(i).getLetter().equals(String.valueOf(myCharArray[j]))) {
                                clickInterface.LetterClick(alphabetList.get(i).getLetter(), j);
                                viewHolder.text_level.setEnabled(false);
                                viewHolder.text_level.setVisibility(View.INVISIBLE);
                                GameActivity.count++;
                                if (GameActivity.count == word.length()){
                                    endLevel.EndLevel(level, "win");
                                }
                            }
                        }
                    } else {
                        if (vibrate){ vibrate();}
                        if (mode.equals("Elimination_mode")){
                            GameActivity.mistakes--;
                            GameActivity.textInfoMode.setText(GameActivity.mistakes+" mistakes left");
                            if (GameActivity.mistakes==0){
                                endLevel.EndLevel(level,"Lose_mistakes");
                            }
                        }
                        viewHolder.text_level.setEnabled(false);
                        viewHolder.text_level.setBackgroundResource(R.drawable.settings_off);
                    }
                }
            });
        }
        else {
            viewHolder.text_level.setText("" + alphabetList.get(i).getLetter());

            viewHolder.text_level.setEnabled(false);
            viewHolder.text_level.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return 26;
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_level;
        ConstraintLayout constraint;

        public ViewHolder(View view) {
            super(view);
            this.text_level = view.findViewById(R.id.text_level);
            this.constraint = view.findViewById(R.id.constraint);
        }
    }

    private void vibrate() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            vibrator = (Vibrator) activity.getSystemService(VIBRATOR_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else {
            vibrator.vibrate(200);
        }
    }
}

