package com.fotile.x1i.adapter.recipe;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.uitl.RecipeConstant;
import com.fotile.x1i.R;
import com.fotile.x1i.adapter.common.CommonRecyclerAdapter;

import java.util.List;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 文件名称：RecipesCategoryRecyclerAdapter
 * 创建时间：2019/5/29 14:42
 * 文件作者：yaohx
 * 功能描述：菜谱分类列表adapter
 */
public class RecipesCategoryRecyclerAdapter extends CommonRecyclerAdapter<Recipe> {

    /**
     * 图片大小参数
     */
    private static final String PARAM_IMAGE = "?x-oss-process=image/resize,w_250,h_250";

    public RecipesCategoryRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_recipe_info_recycler, parent, false);
        return new RecipeRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecipeRecyclerViewHolder viewHolder = (RecipeRecyclerViewHolder) holder;
        Recipe recipes = getItem(position);
        if (viewHolder != null && recipes != null) {
            List list = recipes.getImages();
            if (list != null && !list.isEmpty()) {
                Glide.with(context)
                        .load(list.get(0) + PARAM_IMAGE)
                        .thumbnail(RecipeConstant.GLIDE_THUMBNAIL_RECIPE_LIST)
                        .bitmapTransform(new MultiTransformation(
                                new CenterCrop(context),new RoundedCornersTransformation(context, 10, 0,
                                RoundedCornersTransformation.CornerType.ALL)))
                        .into(viewHolder.img);
            }
            viewHolder.txt.setText(recipes.getName());
        }

    }


    class RecipeRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txt;

        public RecipeRecyclerViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            txt = (TextView) itemView.findViewById(R.id.txt);

            img.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(null !=onRecipeItemClickListener ){
                        onRecipeItemClickListener.onRecipeItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
