package com.fotile.x1i.activity.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import com.fotile.common.util.log.LogUtil;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.bean.recipe.RecipeCategory;
import com.fotile.recipe.net.presenter.LocalRecipePresenter;
import com.fotile.recipe.net.view.LocalRecipeView;
import com.fotile.recipe.uitl.RecipeConstant;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.recipe.fragment.FavoriteFragment;
import com.fotile.x1i.activity.recipe.fragment.RecipesCategoryFragment;
import com.fotile.x1i.adapter.common.CommonViewpagerFragmentAdapter;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.base.BaseFragment;
import com.fotile.x1i.bean.event.BackToFirstTabMessage;
import com.fotile.x1i.widget.HorScrollView;
import com.fotile.x1i.widget.StyleTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 文件名称：RecipesCategoryActivity
 * 创建时间：2019/5/28 16:59
 * 文件作者：yaohx
 * 功能描述：菜谱分类
 */
public class RecipesCategoryActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View
        .OnClickListener {
    LocalRecipePresenter recipePresenter;

    @BindView(R.id.tabLayout_category)
    HorScrollView horScrollView;
    /**
     * tab layout的fragment集合
     */
    private List<BaseFragment> fragments = new ArrayList<BaseFragment>();
    /**
     * view page adapter
     */
    private CommonViewpagerFragmentAdapter commonViewpagerFragmentAdapter;
    /**
     * 菜谱分类集合
     */
    private List<RecipeCategory> recipeCategoryList = new ArrayList<>();
    /**
     * 显示菜谱
     */
    @BindView(R.id.viewpager_category)
    ViewPager viewpagerCategory;
    /**
     * 菜谱搜索
     */
    @BindView(R.id.txt_recipe_search)
    StyleTextView txtRecipeSearch;


    /**
     * 传值list<recipe>的key
     */
    public static final String RECIPE_BEAN = "Recipe_bean";

    int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void onBackToFirstTab(BackToFirstTabMessage message) {
        if (message.to_class.getSimpleName().contains("RecipesCategoryActivity")) {
            LogUtil.LOGE("1111",1);
            viewpagerCategory.setCurrentItem(0);
        }
    }
    private void initView() {
        horScrollView.setItemWidth(158);
        horScrollView.setTextSize(36, 26);

        viewpagerCategory.setOnPageChangeListener(this);
        horScrollView.setOnHorItemClickListener(new HorScrollView.OnHorItemClickListener() {
            @Override
            public void onHorItemClick(int index) {
                viewpagerCategory.setCurrentItem(index);
            }
        });

        txtRecipeSearch.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击菜谱搜索
            case R.id.txt_recipe_search:
                launchActivity(RecipeSearchActivity.class);
                break;
        }
    }

    private void initData() {
        commonViewpagerFragmentAdapter = new CommonViewpagerFragmentAdapter(getSupportFragmentManager());

        //获取菜谱分类数据
        recipePresenter = new LocalRecipePresenter(this);
        recipePresenter.onCreate(compositeSubscription);
        recipePresenter.attachLocalRecipeView(recipeView);
        recipePresenter.getRecipeCategoryList(RecipeConstant.RECIPE_CATEGORY);
    }

    /**
     * 初始化tab切换栏
     *
     * @param categoryList
     */
    private void initTab(List<RecipeCategory> categoryList) {
        if (null != categoryList && categoryList.size() > 0) {
            for (int k = 0; k <= categoryList.size() - 1; k++) {
                RecipeCategory recipeCategory = categoryList.get(k);
                horScrollView.addItemView(recipeCategory.getName(), k);
            }
            horScrollView.addItemView("我的菜谱", categoryList.size());
            horScrollView.setSelect(0);
        }
    }

    private void initFragment(List<RecipeCategory> categoryList) {
        if (null != categoryList && categoryList.size() > 0) {
            //本地菜谱标签
            for (int k = 0; k <= categoryList.size() - 1; k++) {
                RecipeCategory recipeCategory = categoryList.get(k);
                String id = recipeCategory.getId();
                String name = recipeCategory.getName();

                //                LogUtil.LOG_RECIPE("菜谱分类标签", "id:" + id + "  name:" + name);
                RecipesCategoryFragment fragment = new RecipesCategoryFragment(id, name);
                fragments.add(fragment);
            }
            FavoriteFragment favoriteFragment = new FavoriteFragment();
            fragments.add(favoriteFragment);
            size=categoryList.size();
        }

        commonViewpagerFragmentAdapter.setList(fragments);
        viewpagerCategory.setAdapter(commonViewpagerFragmentAdapter);
        commonViewpagerFragmentAdapter.notifyDataSetChanged();
    }

    /**
     * 菜谱分类和parent_id返回相关数据
     */
    LocalRecipeView recipeView = new LocalRecipeView() {
        @Override
        public void onRecipeCategoryListSuccess(List<RecipeCategory> categoryList) {
            recipeCategoryList.clear();
            recipeCategoryList.addAll(categoryList);

            initTab(categoryList);
            initFragment(categoryList);
            //            recipePresenter.getRecipeListByCategoryId("5cecc700baa3dc435767f2fd", "中式特色");
        }

        @Override
        public void onRecideCategoryListError(String e) {

        }

        @Override
        public void onRecipeListSuccess(List<Recipe> recipeList) {
            for (Recipe recipe : recipeList) {
                LogUtil.LOG_RECIPE("菜谱分类下的recipe", recipe.getName());
            }
        }

        @Override
        public void onRecipeListError(String e) {

        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(viewpagerCategory.getCurrentItem()==size){
            ((FavoriteFragment)commonViewpagerFragmentAdapter.getItem(size)).updateDeleteIcon(ev.getX(), ev.getY());
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = ev.getX();
                int[] location = new int[2];
                viewpagerCategory.getLocationOnScreen(location);
                float viewPagerEndX = location[0] + viewpagerCategory.getWidth();
                if ((x >= location[0] && x <= location[0] + 25) || (x >= viewPagerEndX - 25 && x <= viewPagerEndX)) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_recipe_main;
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
        horScrollView.setSelect(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public Fragment getCurrentShownFragment() {
        if (commonViewpagerFragmentAdapter != null && viewpagerCategory != null) {
            return commonViewpagerFragmentAdapter.getItem(viewpagerCategory.getCurrentItem());
        }
        return null;
    }

}
