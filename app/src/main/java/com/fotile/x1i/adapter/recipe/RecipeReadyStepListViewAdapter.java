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
import com.fotile.recipe.bean.recipe.ReadyStep;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.uitl.RecipeConstant;
import com.fotile.x1i.R;
import com.fotile.x1i.viewholder.RecipeReadyListViewHolder;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 文件名称： RecipeReadyStepListViewAdapter 
 * 创建时间： 2019/6/11  
 * 文件作者： chenyqi
 * 功能描述： 菜谱准备步骤ListView
 */

  

public class RecipeReadyStepListViewAdapter extends BaseAdapter {
    private Context context;
    private List<ReadyStep> readyStepList;
    private Recipe recipe;
    /**
     * 图片大小参数
     */
    private static final String PARAM_IMAGE = "?x-oss-process=image/resize,w_590,h_393";

    public RecipeReadyStepListViewAdapter(Context context, List<ReadyStep> list, Recipe recipe) {
        this.readyStepList = list;
        this.context = context;
        this.recipe = recipe;
    }

    @Override
    public int getCount() {
        return null == readyStepList ? 0 : readyStepList.size();
    }

    @Override
    public Object getItem(int position) {
        return readyStepList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RecipeReadyListViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_recipe_ready, parent, false);
            holder = new RecipeReadyListViewHolder();
            holder.currentStep = (TextView) convertView.findViewById(R.id.current_ready_step);
            holder.tvReadyStepDescription = (TextView) convertView.findViewById(R.id.tv_ready_step_description);
            holder.imgReadyRecipe = (ImageView) convertView.findViewById(R.id.img_ready_recipe);
            convertView.setTag(holder);
        } else {
            holder = (RecipeReadyListViewHolder) convertView.getTag();
        }
        readyStepList = recipe.getReadySteps();
        int readyStepCount = (null != readyStepList) ? readyStepList.size() : 0;
        LogUtil.LOG_RECIPE("readyStep", readyStepCount);
        if (readyStepList != null && !readyStepList.isEmpty()) {
                if (readyStepList.get(position).getImages() != null && !readyStepList.get(position).getImages().isEmpty()) {
                    Glide.with(context).load(readyStepList.get(position).getImages().get(0) + PARAM_IMAGE).thumbnail(RecipeConstant
                            .GLIDE_THUMBNAIL_RECIPE_DETAIL).bitmapTransform(new MultiTransformation(new CenterCrop(context),
                            new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.ALL)))
                            .into(holder.imgReadyRecipe);
                }
                String indexStr = getCookStepTitle(readyStepList.get(position).getIndex(), readyStepCount);
                SpannableString spannableString = new SpannableString(indexStr);
                int index = indexStr.indexOf(" / ");
                int color = context.getResources().getColor(R.color.black_txt);
                spannableString.setSpan(new ForegroundColorSpan(color), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new AbsoluteSizeSpan(48), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.currentStep.setText(spannableString);
                holder.tvReadyStepDescription.setText(readyStepList.get(position).getDescription());

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
