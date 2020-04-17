package com.fotile.x1i.adapter.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fotile.recipe.bean.recipe.MajorIngredient;
import com.fotile.x1i.R;
import com.fotile.x1i.viewholder.RecipeMaterialListViewHolder;


import java.util.List;

/**
 * 文件名称：MajorIngredientsListViewAdapter
 * 创建时间：2017/8/7
 * 文件作者：zhaoqingjing
 * 功能描述：Major Ingredients of recipe list view adapter
 */

public class MajorIngredientsListViewAdapter extends BaseAdapter {
    private Context context;
    private List<MajorIngredient> list;

    public MajorIngredientsListViewAdapter(Context context, List<MajorIngredient> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecipeMaterialListViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_material, parent, false);
            holder = new RecipeMaterialListViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_material_name);
            holder.tvQuantity = (TextView) convertView.findViewById(R.id.tv_material_quantity);
            holder.dotLine=(View) convertView.findViewById(R.id.dot_line) ;
            holder.dotLine.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
            holder.tvQuantity.setSelected(true);
            convertView.setTag(holder);
        } else {
            holder = (RecipeMaterialListViewHolder) convertView.getTag();
        }
        MajorIngredient minorIngredient = list.get(position);
        String materialName = minorIngredient.getName();
        String materialQuantity = minorIngredient.getUnit();
        holder.tvName.setText(materialName);
        holder.tvQuantity.setText(materialQuantity);
        holder.tvQuantity.setSelected(true);
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
