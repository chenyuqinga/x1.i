package com.fotile.x1i.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 文件名称：WifiListViewHolder
 * 创建时间：2017/8/15
 * 文件作者：wanghouyu
 * 功能描述：wifi列表每项view管理
 */
public class WifiListViewHolder {
    private final SparseArray<View> views;
    private int position;
    private View convertView;
    private Context context;

    private WifiListViewHolder(Context context, ViewGroup parent, int layoutId,
                               int position) {
        this.context = context;
        this.position = position;
        this.views = new SparseArray<View>();
        convertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        convertView.setTag(this);
    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static WifiListViewHolder get(Context context, View convertView,
                                         ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new WifiListViewHolder(context, parent, layoutId, position);
        }
        return (WifiListViewHolder) convertView.getTag();
    }

    public View getConvertView() {
        return convertView;
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public WifiListViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public WifiListViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    public WifiListViewHolder setImageResource(int viewId, int drawableId, int level) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        view.setImageLevel(level);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public WifiListViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public int getPosition() {
        return position;
    }
}
