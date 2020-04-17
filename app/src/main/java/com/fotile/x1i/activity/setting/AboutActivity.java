package com.fotile.x1i.activity.setting;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.fragment.AboutInstructionsFragment;
import com.fotile.x1i.activity.setting.fragment.AboutLegalFragment;
import com.fotile.x1i.activity.setting.fragment.AboutProductFragment;
import com.fotile.x1i.adapter.AboutViewpagerAdapter;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.base.BaseFragment;
import com.fotile.x1i.widget.HorScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 文件名称：AboutActivity
 * 创建时间：2019/4/28 14:35
 * 文件作者：yaohx
 * 功能描述：关于本机
 */
public class AboutActivity extends BaseActivity implements ViewPager.OnPageChangeListener, HorScrollView.OnHorItemClickListener {

    @BindView(R.id.tabLayout_category)
    HorScrollView tabLayoutCategory;

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    AboutViewpagerAdapter aboutViewpagerAdapter;

    List<String> titles = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initViewpager();
    }

    private void initView() {
        titles.add("产品信息");
        // titles.add("电子说明书");
        titles.add("法律信息");
        int index = 0;
        tabLayoutCategory.addItemView(titles.get(0), index++);
        tabLayoutCategory.addItemView(titles.get(1), index++);
        //  tabLayoutCategory.addItemView(titles.get(2), index++);

        tabLayoutCategory.setSelect(0);
        tabLayoutCategory.setOnHorItemClickListener(this);
    }

    private void initViewpager() {
        aboutViewpagerAdapter = new AboutViewpagerAdapter(getSupportFragmentManager());
        List<BaseFragment> fragments = new ArrayList<BaseFragment>();

        //产品信息 fragment
        AboutProductFragment fragment_product = new AboutProductFragment();
        fragments.add(fragment_product);
        //电子说明书 fragment
        //  AboutInstructionsFragment fragment_ins = new AboutInstructionsFragment();
        //  fragments.add(fragment_ins);
        //法律信息 fragment
        AboutLegalFragment fragment_legal = new AboutLegalFragment();
        fragments.add(fragment_legal);

        aboutViewpagerAdapter.setData(fragments, titles);
        viewPager.setAdapter(aboutViewpagerAdapter);
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_about;
    }

    @Override
    public boolean showBottom() {
        return true;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tabLayoutCategory.setSelect(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //HorScrollView 单个元素的点击事件
    @Override
    public void onHorItemClick(int index) {
        viewPager.setCurrentItem(index);
    }
}
