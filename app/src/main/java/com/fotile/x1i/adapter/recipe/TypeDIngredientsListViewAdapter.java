package com.fotile.x1i.adapter.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fotile.recipe.bean.recipe.DIngredient;
import com.fotile.x1i.R;
import com.fotile.x1i.viewholder.RecipeMaterialListViewHolder;


import java.util.List;

/**
 * Created by hoperun on 17-12-27.
 */

public class TypeDIngredientsListViewAdapter extends BaseAdapter {
    private Context context;
    private List<DIngredient> list;

    public TypeDIngredientsListViewAdapter(Context context, List<DIngredient> list) {
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
        DIngredient dIngredient = list.get(position);
        String materialName = dIngredient.getName();
        String materialQuantity = dIngredient.getUnit();
        holder.tvName.setText(materialName);
        holder.tvQuantity.setText(materialQuantity);
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}



