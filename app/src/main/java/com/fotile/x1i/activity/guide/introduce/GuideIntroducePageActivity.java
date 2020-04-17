package com.fotile.x1i.activity.guide.introduce;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fotile.x1i.R;
import com.fotile.x1i.adapter.AboutViewpagerAdapter;
import com.fotile.x1i.adapter.IntroduceViewpagerAdapter;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 文件名称：GuideIntroducePageActivity
 * 创建时间：2019/5/15 16:17
 * 文件作者：yaohx
 * 功能描述：功能简介--滑动页面
 */
public class GuideIntroducePageActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.liner_content)
    LinearLayout linerContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        List<BaseFragment> list = new ArrayList<BaseFragment>();
        BaseFragment fragment = new IntroduceFragment1();
        list.add(fragment);
        fragment = new IntroduceFragment2();
        list.add(fragment);
        fragment = new IntroduceFragment3();
        list.add(fragment);
        fragment = new IntroduceFragment4();
        list.add(fragment);
        fragment = new IntroduceFragment5();
        list.add(fragment);

        IntroduceViewpagerAdapter adapter = new IntroduceViewpagerAdapter(getSupportFragmentManager());
        adapter.setData(list);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);

        addPoint();
    }

    private void addPoint() {
        for (int k = 0; k <= 4; k++) {
            ImageView item = new ImageView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(60, 10);
            lp.rightMargin = 10;
            lp.leftMargin = 10;
            item.setLayoutParams(lp);
            item.setBackgroundResource(R.drawable.shape_introduce_normal);
            linerContent.addView(item);
        }
        setSelect(0);
    }

    private void setSelect(int position) {
        for (int k = 0; k <= 4; k++) {
            ImageView item = (ImageView) linerContent.getChildAt(k);
            item.setBackgroundResource(R.drawable.shape_introduce_normal);
            if (k == position) {
                item.setBackgroundResource(R.drawable.shape_introduce_pressed);
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide_introduce_page;
    }

    @Override
    public boolean showBottom() {
        return false;
    }
    @Override
    public boolean showTopBar() {
        return false;
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setSelect(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
