package com.fotile.x1i.activity.recipe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fotile.common.util.log.LogUtil;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.bean.recipe.RecipeCategory;
import com.fotile.recipe.net.presenter.LocalRecipePresenter;
import com.fotile.recipe.net.view.LocalRecipeView;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.recipe.RecipesDetailsActivity;
import com.fotile.x1i.activity.recipe.RecipesCategoryActivity;
import com.fotile.x1i.adapter.common.CommonRecyclerAdapter;
import com.fotile.x1i.adapter.recipe.RecipesCategoryRecyclerAdapter;
import com.fotile.x1i.base.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * 文件名称：RecipesCategoryFragment
 * 创建时间：17-9-13 上午9:20
 * 文件作者：zhaoqingjing
 * 功能描述：根据菜谱分类显示的菜谱列表fragment
 */
public class RecipesCategoryFragment extends BaseFragment implements CommonRecyclerAdapter.OnRecipeItemClickListener{

    /**
     * 分类标签id
     */
    private String categoryId;
    /**
     * 分类标签name
     */
    private String tagName;

    private RecipesCategoryRecyclerAdapter recyclerAdapter;

    /**
     * 菜谱列表
     */
    @BindView(R.id.recyclerView_recipes)
    RecyclerView recyclerViewRecipes;

    private LocalRecipePresenter recipesPresenter;
    private List<Recipe> recipeList = new ArrayList<>();
    private ArrayList<Integer> mAttachPositions;

    public RecipesCategoryFragment() {

    }

    /**
     * 构造方法
     *
     * @param categoryId
     * @param tagName    菜谱标签名称
     */
    public RecipesCategoryFragment(String categoryId, String tagName) {
        this.categoryId = categoryId;
        this.tagName = tagName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initView();
        initData();
        return view;
    }

    /**
     * 初始化recyclerview的item的点击事件
     */
    private void initView() {
        recyclerAdapter = new RecipesCategoryRecyclerAdapter(context);
        recyclerAdapter.setOnRecipeItemClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewRecipes.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        recyclerViewRecipes.setAdapter(recyclerAdapter);
        mAttachPositions = new ArrayList<>();
        recyclerViewRecipes.addOnChildAttachStateChangeListener(mListener);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recipes_category;
    }

    /**
     * 请求数据
     */
    private void initData() {
        recipesPresenter = new LocalRecipePresenter(context);
        recipesPresenter.onCreate(compositeSubscription);
        recipesPresenter.attachLocalRecipeView(recipesView);

        //本地菜谱从缓存中获取数据
        recipesPresenter.getRecipeListByCategoryId(categoryId, tagName);
    }

    LocalRecipeView recipesView = new LocalRecipeView() {
        @Override
        public void onRecipeCategoryListSuccess(List<RecipeCategory> categoryList) {

        }

        @Override
        public void onRecideCategoryListError(String e) {

        }

        //返回请求的菜谱列表
        @Override
        public void onRecipeListSuccess(List<Recipe> list) {
            recipeList.clear();
            sortByLevel(list);
            recipeList.addAll(list);
            recyclerAdapter.addItems(recipeList);

            LogUtil.LOG_RECIPE("onRecipeListSuccess--" + tagName, null == list ? 0 : list.size());
        }

        @Override
        public void onRecipeListError(String e) {
            LogUtil.LOG_RECIPE("onRecipeListError--" + tagName, e);
        }
    };

    /**
     * 对菜谱列表按权重大小排序
     */
    private void sortByLevel(List<Recipe> list) {

        Collections.sort(list, new Comparator<Recipe>() {

            @Override
            public int compare(Recipe beforeRecipe, Recipe afterRecipe) {
                return beforeRecipe.getSerialNumber() - afterRecipe.getSerialNumber();
            }
        });
    }

    //点击列表中某一道菜谱
    @Override
    public void onRecipeItemClick(int position) {
        LogUtil.LOGE("============onRecipeItemClick", position);
        Recipe recipe = recipeList.get(position);
        Intent intent = new Intent(context , RecipesDetailsActivity.class);
        intent.putExtra("recipe",recipe);
        startActivity(intent);
    }

    RecyclerView.OnChildAttachStateChangeListener mListener = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            int childAdapterPosition = recyclerViewRecipes.getChildAdapterPosition(view);
            mAttachPositions.add(childAdapterPosition);
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            //            int childAdapterPosition = recyclerViewRecipes.getChildAdapterPosition(view);
            //            mAttachPositions.remove(childAdapterPosition - mAttachPositions.get(0));
            int childAdapterPosition = recyclerViewRecipes.getChildAdapterPosition(view);
            if (mAttachPositions.contains(childAdapterPosition)) {
                mAttachPositions.remove(Integer.valueOf(childAdapterPosition));
            }
        }
    };

    public String selectItem(int index) {
        if (mAttachPositions != null && !mAttachPositions.isEmpty()) {
            if (index >= mAttachPositions.size()) {
                return "当前页面并没有第" + (index + 1) + "道菜哦";
            }
            LogUtil.LOGE("============onRecipeItemClick", index);
            Integer itemIndex = mAttachPositions.get(index);
            Recipe recipe = recipeList.get(itemIndex);
            //进入菜谱基本信息界面
//            ((RecipesCategoryActivity) getActivity()).launchActivity(
//                    RecipesDetailsActivity.class, itemIndex, recipes);
            Intent intent = new Intent(context , RecipesDetailsActivity.class);
            intent.putExtra("recipe",recipe);
            startActivity(intent);
            return "好的，已为您选择" + recipe.getName();
        }
        return null;
    }
}
