package com.fotile.x1i.activity.setting;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.fragment.HelpNoviceFragment;
import com.fotile.x1i.activity.setting.fragment.HelpQuestionFragment;
import com.fotile.x1i.activity.setting.fragment.HelpServerFragment;
import com.fotile.x1i.adapter.AboutViewpagerAdapter;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.base.BaseFragment;
import com.fotile.x1i.widget.HorScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 文件名称：HelpActivity
 * 创建时间：2019/4/28 14:35
 * 文件作者：yaohx
 * 功能描述：系统帮助
 */
public class HelpActivity extends BaseActivity implements ViewPager.OnPageChangeListener, HorScrollView.OnHorItemClickListener {

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
        //        titles.add("常见问题");
        titles.add("新手帮助");
        titles.add("服务热线");
        int index = 0;
        tabLayoutCategory.addItemView(titles.get(0), index++);
        tabLayoutCategory.addItemView(titles.get(1), index++);
        //        tabLayoutCategory.addItemView(titles.get(2), index++);

        tabLayoutCategory.setSelect(0);
        tabLayoutCategory.setOnHorItemClickListener(this);
    }

    private void initViewpager() {
        aboutViewpagerAdapter = new AboutViewpagerAdapter(getSupportFragmentManager());
        List<BaseFragment> fragments = new ArrayList<BaseFragment>();
        //常见问题
        //       BaseFragment fragment = new HelpQuestionFragment();
        //        fragments.add(fragment);
        //新手帮助
        BaseFragment fragment = new HelpNoviceFragment();
        fragments.add(fragment);
        //服务热线
        fragment = new HelpServerFragment();
        fragments.add(fragment);

        aboutViewpagerAdapter.setData(fragments, titles);
        viewPager.setAdapter(aboutViewpagerAdapter);
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_help;
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
