package com.fotile.x1i.adapter.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.fotile.x1i.base.BaseFragment;

import java.util.List;

/**
 * 文件名称：CommonViewpagerFragmentAdapter
 * 创建时间：2019/5/29 14:08
 * 文件作者：yaohx
 * 功能描述：菜谱分类viewpager adapter
 */
public class CommonViewpagerFragmentAdapter<T> extends FragmentStatePagerAdapter {

    private List<BaseFragment> list;

    public CommonViewpagerFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setList(List<BaseFragment> list) {
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return null == list ? 0 : list.size();
    }
}
