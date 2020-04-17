package com.fotile.x1i.activity.recipe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.net.presenter.FavoriteRecipePresenter;
import com.fotile.recipe.net.view.FavoriteRecipeView;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.recipe.RecipesCategoryActivity;
import com.fotile.x1i.activity.recipe.RecipesDetailsActivity;
import com.fotile.x1i.adapter.common.CommonRecyclerAdapter;
import com.fotile.x1i.adapter.recipe.FavoriteRecyclerAdapter;
import com.fotile.x1i.base.BaseFragment;
import com.fotile.x1i.bean.event.BackToFirstTabMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 文件名称： FavoriteFragment
 * 创建时间： 2019/7/29
 * 文件作者： chenyqi
 * 功能描述：
 */


public class FavoriteFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = "FavoriteFragment";
    @BindView(R.id.recyclerView_mycollect)
    RecyclerView recyclerViewMyCollect;
    /**
     * 没有收藏显示
     */
    @BindView(R.id.lLayout_no_collect)
    LinearLayout lLayoutNoCollect;
    @BindView(R.id.tv_next)
    TextView tvNext;


    private FavoriteRecyclerAdapter favoriteRecyclerAdapter;
    private FavoriteRecipePresenter favoriteRecipePresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initView();
        return view;
    }

    private void initView() {
        recyclerViewMyCollect.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        favoriteRecyclerAdapter = new FavoriteRecyclerAdapter(context);
        favoriteRecyclerAdapter.setOnItemClickListener(itemClickListener);
        recyclerViewMyCollect.setAdapter(favoriteRecyclerAdapter);
        favoriteRecipePresenter = new FavoriteRecipePresenter(context);
        favoriteRecipePresenter.onCreate(compositeSubscription);
        favoriteRecipePresenter.attachFavoriteRecipeView(favoriteRecipeView);
        tvNext.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        favoriteRecipePresenter.getFavoriteRecipeList();
    }

    FavoriteRecipeView favoriteRecipeView = new FavoriteRecipeView() {
        List<Recipe> recipes = new ArrayList<>();

        @Override
        public void onFavoriteListSuccess(List<Recipe> list) {
            recipes.clear();
            if (list != null && list.size() > 0) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    recipes.add(list.get(i));
                    recyclerViewMyCollect.setVisibility(View.VISIBLE);
                    lLayoutNoCollect.setVisibility(View.GONE);
                }
            } else {
                recyclerViewMyCollect.setVisibility(View.GONE);
                lLayoutNoCollect.setVisibility(View.VISIBLE);
            }
            favoriteRecyclerAdapter.addItems(recipes);
            favoriteRecyclerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onUploadRecipeListSuccess(List<Recipe> list) {

        }

        @Override
        public void onUploadRecipeListError(String e) {

        }

        @Override
        public void onUploadRecipeDeleteSuccess() {

        }

        @Override
        public void onUploadRecipeDeleteError(String e) {

        }
    };

    //点击列表中某一道菜谱
    CommonRecyclerAdapter.OnItemClickListener itemClickListener = new CommonRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Recipe recipe = favoriteRecipePresenter.getRecipeById(favoriteRecyclerAdapter.getContentList().get(position).getId());
            if (null != recipe) {
                Intent intent = new Intent(context , RecipesDetailsActivity.class);
                intent.putExtra("recipe",recipe);
                startActivity(intent);
            }
        }

        @Override
        public void onItemLongClick(View view, int position) {
            favoriteRecyclerAdapter.updateDeleteStatus(true);
        }

        @Override
        public void onItemDelete(View view, int position) {
            favoriteRecipePresenter.deleteByRecipeId(favoriteRecyclerAdapter.getContentList().get(position).getId());
            favoriteRecipePresenter.getFavoriteRecipeList();
        }
    };

    /**
     * 更新删除按钮的显示
     */
    public void updateDeleteIcon(float touchX, float touchY) {
        if (!isTouchRecyclerView(touchX, touchY)) {
            favoriteRecyclerAdapter.updateDeleteStatus(false);
        }
    }

    /**
     * 判断触控位置是否为RecyclerView
     *
     * @param touchX
     * @param touchY
     * @return
     */
    private boolean isTouchRecyclerView(float touchX, float touchY) {

        int[] location = new int[2];
        recyclerViewMyCollect.getLocationOnScreen(location);
        if (touchX >= location[0]
                && touchX <= location[0] + recyclerViewMyCollect.getWidth()
                && touchY >= location[1]
                && touchY <= location[1] + recyclerViewMyCollect.getHeight()) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                EventBus.getDefault().post(new BackToFirstTabMessage(RecipesCategoryActivity.class));
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_favorite;
    }
}
