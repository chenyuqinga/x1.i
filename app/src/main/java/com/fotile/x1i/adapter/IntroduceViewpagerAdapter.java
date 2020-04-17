package com.fotile.x1i.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.fotile.x1i.base.BaseFragment;

import java.util.List;

public class IntroduceViewpagerAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> fragments;

    public IntroduceViewpagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<BaseFragment> fragment) {
        this.fragments = fragment;
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


}


