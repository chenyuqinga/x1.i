package com.fotile.x1i.adapter.recipe;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.uitl.RecipeConstant;
import com.fotile.recipe.uitl.db.DataFavoriteUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.adapter.common.CommonRecyclerAdapter;

import java.util.List;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 文件名称： FavoriteRecyclerAdapter
 * 创建时间： 2019/7/29
 * 文件作者： chenyqi
 * 功能描述： 我的菜譜收藏
 */


public class FavoriteRecyclerAdapter extends CommonRecyclerAdapter<Recipe> {
    /**
     * 图片大小参数
     */
    private static final String PARAM_IMAGE = "?x-oss-process=image/resize,w_250,h_250";
    /**
     * 删除按钮是否可见的标识
     */
    private boolean deleteVisible = false;

    public FavoriteRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collect, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FavoriteViewHolder favoriteViewHolder = (FavoriteViewHolder) holder;
        Recipe recipes = getItem(position);
        if (favoriteViewHolder != null && recipes != null) {
            List list = recipes.getImages();
            if (list != null && !list.isEmpty()) {
                Glide.with(context)
                        .load(list.get(0) + PARAM_IMAGE)
                        .thumbnail(RecipeConstant.GLIDE_THUMBNAIL_RECIPE_LIST)
                        .bitmapTransform(new MultiTransformation(
                                new CenterCrop(context), new RoundedCornersTransformation(context, 10, 0,
                                RoundedCornersTransformation.CornerType.ALL)))
                        .into(favoriteViewHolder.imgRecipe);
            }
            favoriteViewHolder.tvRecipeName.setText(recipes.getName());
            if (deleteVisible) {
                favoriteViewHolder.layoutDelete.setVisibility(View.VISIBLE);
            } else {

                favoriteViewHolder.layoutDelete.setVisibility(View.GONE);
            }


        }
    }

    /**
     * 更新删除按钮是否显示的标识值
     *
     * @param visible
     */
    public void updateDeleteStatus(boolean visible) {
        deleteVisible = visible;
        notifyDataSetChanged();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipe;

        TextView tvRecipeName;

        RelativeLayout layoutDelete;
        View viewDelete;


        public FavoriteViewHolder(final View itemView) {
            super(itemView);
            imgRecipe = (ImageView) itemView.findViewById(R.id.img);
            tvRecipeName = (TextView) itemView.findViewById(R.id.txt);
            layoutDelete = (RelativeLayout) itemView.findViewById(R.id.layout_menu_name_delete);
            viewDelete = (View) itemView.findViewById(R.id.view_delete);
            //设置item删除事件
            viewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemDelete(itemView, getAdapterPosition());
                    }
                }
            });
            layoutDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {

                    }
                }
            });

            //设置item点击事件
            imgRecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(itemView, getAdapterPosition());
                    }
                }
            });

            //设置item长按事件
            imgRecipe.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemLongClick(itemView, getAdapterPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
