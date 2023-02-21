package com.astraxquiz.app;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter_level extends RecyclerView.Adapter<Adapter_level.ViewHolder> {

    Context activity;
    ClickInterface clickInterface;
    String mode;
    int level;
    SoundPool sound_Level;
    boolean sounds;
    int sound_items;
    final int MAX_STREAMS = 5;

    public Adapter_level(Context activity, String mode, int level, ClickInterface clickInterface, boolean sounds) {
        this.activity = activity;
        this.clickInterface = clickInterface;
        this.mode = mode;
        this.level = level;
        this.sounds = sounds;

        sound_Level = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        sound_items = sound_Level.load(activity,R.raw.click4,1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.level, viewGroup, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.text_level.setText(""+(i+1));

        if (i<level){
            viewHolder.text_level.setBackgroundResource(R.drawable.style_level_completed);
            viewHolder.text_level.setTextColor(activity.getResources().getColor(R.color.text_completed_color));
        }
        else if (i==level){
            viewHolder.text_level.setBackgroundResource(R.drawable.style_level_enabled);
            viewHolder.text_level.setTextColor(activity.getResources().getColor(R.color.text_enabled_color));
        }
        else if (i>level){
            viewHolder.text_level.setBackgroundResource(R.drawable.style_level_disabled);
            viewHolder.text_level.setTextColor(activity.getResources().getColor(R.color.text_disabled_color));
        }

        viewHolder.text_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i<=level) {
                    if (sounds) {
                        sound_Level.play(sound_items, 0.8f, 0.8f, 0, 0, 1);
                    }
                    clickInterface.LevelClick(i + 1, mode);
                }
                else{
                    Toast.makeText(activity, "Complete all the previous levels", Toast.LENGTH_SHORT).show();
                }           
            }
        });
    }

    @Override
    public int getItemCount() {
        return 20;
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
}

