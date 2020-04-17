package com.fotile.x1i.adapter.recipe;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.uitl.RecipeConstant;
import com.fotile.x1i.R;
import com.fotile.x1i.viewholder.RecipeRecyclerSearchViewHolder;


import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * 文件名称：RecipesCategoryRecyclerAdapter
 * 创建时间：17-9-11 上午9:33
 * 文件作者：zhaoqingjing
 * 功能描述：菜谱的adapter
 */
public class RecipesSearchRecyclerAdapter extends RecyclerView.Adapter<RecipeRecyclerSearchViewHolder> {
    private static final String PARAM_IMAGE = "?x-oss-process=image/resize,w_270,h_270";
    private Context context;

    private List<Recipe> list = new ArrayList<Recipe>();

    public RecipesSearchRecyclerAdapter(Context context, List<Recipe> list) {
        this.context = context;
        this.list.addAll(list);
    }

    @Override
    public RecipeRecyclerSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_search_album, parent, false);
        RecipeRecyclerSearchViewHolder holder = new RecipeRecyclerSearchViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecipeRecyclerSearchViewHolder holder, int position) {

        Recipe recipes = list.get(position);
        List list = recipes.getImages();
        if (list != null && !list.isEmpty()) {
            Glide.with(context)
                    .load(list.get(0) + PARAM_IMAGE)
                    .thumbnail(RecipeConstant.GLIDE_THUMBNAIL_RECIPE_LIST)
                    .bitmapTransform(new MultiTransformation(
                            new CenterCrop(context), new RoundedCornersTransformation(context, 10, 0,
                            RoundedCornersTransformation.CornerType.ALL)))
                    .into(holder.imgCover);

        }
        holder.tvName.setText(recipes.getName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 更新数据源
     *
     * @param list
     */
    public void updateRecipeList(List<Recipe> list) {
        this.list.clear();
        this.list.addAll(list);
    }

}
