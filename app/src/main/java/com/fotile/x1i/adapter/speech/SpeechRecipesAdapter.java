/*
 * ************************************************************
 * 文件：SpeechRecipesAdapter.java  模块：app  项目：Z1.5
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.x1i.adapter.speech;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.fotile.voice.bean.SpeechRecipeBean;
import com.fotile.x1i.R;
import com.fotile.x1i.listener.OnItemClickListener;
import com.fotile.x1i.viewholder.SpeechRecipeViewHolder;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class SpeechRecipesAdapter extends RecyclerView.Adapter<SpeechRecipeViewHolder> {

    private static final String TAG = SpeechRecipesAdapter.class.getSimpleName();
    /**
     * 图片大小参数
     */
    private static final String PARAM_IMAGE = "?x-oss-process=image/resize,w_220,h_160";
    private Context context;
    private OnItemClickListener mClickListener;

    private List<SpeechRecipeBean> mRecipeList;

    public SpeechRecipesAdapter(Context context, List<SpeechRecipeBean> list) {
        this.context = context;
        this.mRecipeList = list;
    }

    @Override
    public SpeechRecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_speech_recipe_info, parent,
                false);
        return new SpeechRecipeViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(SpeechRecipeViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder");
        if (mRecipeList != null && !mRecipeList.isEmpty()) {
            SpeechRecipeBean recipe = mRecipeList.get(position);
            if (recipe != null) {
                String image = recipe.getImage();
                if (!TextUtils.isEmpty(image)) {
                    Glide.with(context).load(image + PARAM_IMAGE).bitmapTransform(
                            new MultiTransformation(new CenterCrop(context),
                                    new RoundedCornersTransformation(context, 8, 0,
                                            RoundedCornersTransformation.CornerType.ALL))).into(
                            holder.imgCover);
                }
                holder.tvName.setText(recipe.getName());
                Log.e(TAG, "text: " + recipe.getName());
            }
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = mRecipeList == null ? 0 : mRecipeList.size();
        Log.e(TAG, "item count: " + itemCount);
        return itemCount;
    }

    /**
     * 更新数据源
     *
     * @param list
     */
    public void updateRecipeList(List<SpeechRecipeBean> list) {
        this.mRecipeList.clear();
        this.mRecipeList.addAll(list);
        Log.e(TAG, "update list: " + list.size());
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }
}
