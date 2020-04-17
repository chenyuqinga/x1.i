package com.fotile.x1i.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 文件名称：NotificationViewpagerAdapter
 * 创建时间：2017/8/15
 * 文件作者：wanghouyu
 * 功能描述：通知中心左右界面适配器
 */

public class NotificationViewpagerAdapter extends PagerAdapter {
    private ArrayList<View> mViewList;

    public NotificationViewpagerAdapter(ArrayList<View> list) {
        mViewList = list;
    }

    @Override
    public int getCount() {
        if (mViewList != null) {
            return mViewList.size();
        } else {
            return 0;
        }

    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //删除页卡
        container.removeView(mViewList.get(position));
    }

    /**
     * 这个方法用来实例化页卡
     *
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //container.addView(mViewList.get(position), 0);//添加页卡
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

}
