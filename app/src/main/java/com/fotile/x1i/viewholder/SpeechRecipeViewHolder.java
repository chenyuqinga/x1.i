package com.fotile.x1i.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.listener.OnItemClickListener;

//public class SpeechRecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
public class SpeechRecipeViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rlRecipeDisplay;
    public ImageView imgCover;
    public TextView tvName;
    private OnItemClickListener mListener;

    public SpeechRecipeViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView);
        mListener = listener;
        rlRecipeDisplay = (RelativeLayout) itemView.findViewById(R.id.rl_speech_recipe_display);
        imgCover = (ImageView) itemView.findViewById(R.id.img_speech_recipe_image);
        tvName = (TextView) itemView.findViewById(R.id.tv_speech_recipe_name);
        rlRecipeDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, getLayoutPosition());
            }
        });
    }
}
