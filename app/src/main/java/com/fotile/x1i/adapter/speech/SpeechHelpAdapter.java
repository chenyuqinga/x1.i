/*
 * ************************************************************
 * 文件：SpeechHelpAdapter.java  模块：app  项目：Z1.5
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.x1i.adapter.speech;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fotile.x1i.R;
import com.fotile.x1i.viewholder.SpeechHelpViewHolder;

import java.util.List;

public class SpeechHelpAdapter extends RecyclerView.Adapter<SpeechHelpViewHolder> {

    private static final String TAG = SpeechHelpAdapter.class.getSimpleName();
    private Context mContext;
    private List<String> mContent;

    public SpeechHelpAdapter(Context context, List<String> content) {
        Log.e(TAG, "constructor");
        mContext = context;
        mContent = content;
    }

    @Override
    public SpeechHelpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_speech_help, parent, false);
        return new SpeechHelpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SpeechHelpViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder");
        if (mContent != null && !mContent.isEmpty()) {
            Log.e(TAG, mContent.get(position));
            holder.mTvHelp.setText(mContent.get(position));
        }
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "getItemCount");
        return mContent == null ? 0 : mContent.size();
    }

    /**
     * 更新数据源
     *
     * @param list
     */
    public void updateHelpList(List<String> list) {
        this.mContent.clear();
        this.mContent.addAll(list);
        Log.e(TAG, "update list: " + (list == null ? null : list.size()));
    }
}
