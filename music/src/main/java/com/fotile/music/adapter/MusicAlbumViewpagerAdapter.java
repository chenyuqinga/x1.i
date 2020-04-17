package com.fotile.music.adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.ximalaya.ting.android.opensdk.model.metadata.Attributes;

import java.util.List;

/**
 * 文件名称：MusicAlbumViewpagerAdapter
 * 创建时间：17-9-13 下午1:59
 * 文件作者：zhangqiang
 * 功能描述：音乐专辑界面tablayout与viewpager的适配器
 */
public class MusicAlbumViewpagerAdapter extends CommonStateFragmentViewPagerAdapter {

    private List<Fragment> fragments;
    private List<Attributes> attributesList;
    private Context context;

    public MusicAlbumViewpagerAdapter(FragmentManager fm, ViewPager viewPager, List<Fragment> fragments, List<Attributes>
            attributesList) {
        super(fm,viewPager,fragments);
        this.fragments = fragments;
        this.attributesList = attributesList;
    }



    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return null == fragments ? 0 : fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return attributesList.get(position).getDisplayName();

    }

}


