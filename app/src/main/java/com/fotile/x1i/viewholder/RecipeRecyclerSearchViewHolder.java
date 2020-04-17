package com.fotile.x1i.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fotile.x1i.R;


/**
 * 文件名称：RecipeRecyclerSearchViewHolder
 * 创建时间：2019/1/21 10:30
 * 文件作者：chenyqi
 * 功能描述：
 */


public class RecipeRecyclerSearchViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgCover;
    public TextView tvName;

    public RecipeRecyclerSearchViewHolder(View itemView) {
        super(itemView);
        imgCover = (ImageView) itemView.findViewById(R.id.img_recipe);
        tvName = (TextView) itemView.findViewById(R.id.tv_recipe_name);

    }

}
