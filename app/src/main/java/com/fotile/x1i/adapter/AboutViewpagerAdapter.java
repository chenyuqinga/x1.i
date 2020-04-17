package com.fotile.x1i.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;


import com.fotile.x1i.base.BaseFragment;

import java.util.List;

/**
 * 文件名称：AboutViewpagerAdapter
 * 创建时间：17-9-13 下午1:59
 * 文件作者：zhaoqingjing
 * 功能描述：关于本机viewpager适配器
 */
public class AboutViewpagerAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> fragments;
    private List<String> titles;

    public AboutViewpagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<BaseFragment> fragment, List<String> titles) {
        this.fragments = fragment;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


    @Override
    public int getCount() {
        return fragments.size();
    }

//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return ((Fragment) object).getView() == view;
//    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position - 1);
    }
}


