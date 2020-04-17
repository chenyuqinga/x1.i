package com.fotile.x1i.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fotile.x1i.R;

public class SpeechHelpViewHolder extends RecyclerView.ViewHolder {
    public TextView mTvHelp;
    public SpeechHelpViewHolder(View itemView) {
        super(itemView);
        mTvHelp = (TextView) itemView.findViewById(R.id.tv_speech_help);
    }
}
