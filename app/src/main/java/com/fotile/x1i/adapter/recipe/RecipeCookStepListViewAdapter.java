package com.fotile.x1i.adapter.recipe;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.fotile.common.util.log.LogUtil;
import com.fotile.recipe.bean.recipe.CookingStep;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.uitl.RecipeConstant;
import com.fotile.x1i.R;
import com.fotile.x1i.viewholder.RecipeCookListViewHolder;
import com.fotile.x1i.viewholder.RecipeReadyListViewHolder;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 文件名称： RecipeCookStepListViewAdapter 
 * 创建时间： 2019/6/11  
 * 文件作者： chenyqi
 * 功能描述： 菜谱烹饪步骤ListView
 */

  

public class RecipeCookStepListViewAdapter extends BaseAdapter {
    private Context context;
    private List<CookingStep> cookingStepList;
    private Recipe recipe;
    /**
     * 图片大小参数
     */
    private static final String PARAM_IMAGE = "?x-oss-process=image/resize,w_590,h_393";

    public RecipeCookStepListViewAdapter(Context context, List<CookingStep> list, Recipe recipe) {
        this.cookingStepList = list;
        this.context = context;
        this.recipe = recipe;
    }

    @Override
    public int getCount() {
        return null == cookingStepList ? 0 : cookingStepList.size();
    }

    @Override
    public Object getItem(int position) {
        return cookingStepList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RecipeCookListViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_recipe_cook, parent, false);
            holder = new RecipeCookListViewHolder();
            holder.currentStep = (TextView) convertView.findViewById(R.id.current_cook_step);
            holder.tvCookStepDescription = (TextView) convertView.findViewById(R.id.tv_cook_step_description);
            holder.imgCookRecipe = (ImageView) convertView.findViewById(R.id.img_cook_recipe);
            convertView.setTag(holder);
        } else {
            holder = (RecipeCookListViewHolder) convertView.getTag();
        }
        cookingStepList = recipe.getCookingSteps();
        int cookStepCount = (null != cookingStepList) ? cookingStepList.size() : 0;
        LogUtil.LOG_RECIPE("cookStep", cookStepCount);
        if (cookingStepList != null && !cookingStepList.isEmpty()) {


            if (cookingStepList.get(position).getImages() != null && !cookingStepList.get(position).getImages().isEmpty()) {
                Glide.with(context).load(cookingStepList.get(position).getImages().get(0) + PARAM_IMAGE).thumbnail(RecipeConstant
                        .GLIDE_THUMBNAIL_RECIPE_DETAIL).bitmapTransform(new MultiTransformation(new CenterCrop(context),
                        new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.ALL)))
                        .into(holder.imgCookRecipe);
            }
            String indexStr = getCookStepTitle(cookingStepList.get(position).getIndex(), cookStepCount);
            SpannableString spannableString = new SpannableString(indexStr);
            int index = indexStr.indexOf(" / ");
            int color = context.getResources().getColor(R.color.black_txt);
            spannableString.setSpan(new ForegroundColorSpan(color), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(48), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.currentStep.setText(spannableString);
            holder.tvCookStepDescription.setText(cookingStepList.get(position).getDescription());
        }


        return convertView;
    }

    private String getCookStepTitle(int index, int totalCount) {
        StringBuilder builder = new StringBuilder();
        builder.append(index + 1);
        builder.append(" / ");
        builder.append(totalCount);
        return builder.toString();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
