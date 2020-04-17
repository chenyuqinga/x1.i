package com.fotile.x1i.adapter.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名称： CommonRecyclerAdapter
 * 创建时间： 2019/6/5
 * 文件作者： chenyqi
 * 功能描述： 通用RecyclerView适配器,定义了一些常用方法
 */


public class CommonRecyclerAdapter<T> extends RecyclerView.Adapter {
    public Context context;

    public LayoutInflater layoutInflater;


    /**
     * 数据列表
     */
    public List<T> items = new ArrayList<>();


    public CommonRecyclerAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 填充数据列表(之前的数据会清除)
     *
     * @param list
     */
    public void addItems(List<T> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    /**
     * 获取全部数据
     *
     * @return 数据列表
     */
    public List<T> getContentList() {
        return items;
    }

    public interface OnItemClickListener {
        /**
         * item 点击事件
         *
         * @param view     item视图
         * @param position item在列表中的位置
         */
        void onItemClick(View view, int position);

        /**
         * item 长按事件
         *
         * @param view
         * @param position
         */
        void onItemLongClick(View view, int position);

        /**
         * item 删除事件
         *
         * @param view
         * @param position
         */
        void onItemDelete(View view, int position);
    }

    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return null == items ? 0 : items.size();
    }

    public interface OnRecipeItemClickListener {
        void onRecipeItemClick(int position);
    }

    public OnRecipeItemClickListener onRecipeItemClickListener;

    public void setOnRecipeItemClickListener(OnRecipeItemClickListener onRecipeItemClickListener) {
        this.onRecipeItemClickListener = onRecipeItemClickListener;
    }
}
