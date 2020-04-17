package com.fotile.x1i.activity.recipe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.music.listener.FavoriteItemClickListener;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.bean.recipe.RecipeCategory;
import com.fotile.recipe.net.presenter.LocalRecipePresenter;
import com.fotile.recipe.net.view.LocalRecipeView;
import com.fotile.x1i.R;
import com.fotile.x1i.adapter.common.CommonRecyclerAdapter;
import com.fotile.x1i.adapter.recipe.RecipesSearchRecyclerAdapter;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.dailog.CommonDialog;
import com.fotile.x1i.dailog.LoadingDialog;
import com.fotile.x1i.util.Config;
import com.fotile.x1i.widget.LineWrapLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by chenyqi on 2019/6/13.
 */

public class RecipeSearchActivity extends BaseActivity implements View.OnClickListener, FavoriteItemClickListener.OnItemClickListener {
    private static final String SHARE_PREFERENCES_NAME = "searchHistoryList";
    private static final String SHARE_PREFERENCES_KEY = "searchRecipeList";
    private static final String TAG = "RecipesSearchActivity";
    private static final int MAX_QUERY_KEY_STRING_COUNT = 5;
    /**
     * 自动换行历史记录
     */
    @BindView(R.id.history_search)
    LineWrapLayout lineWrapLayout;
    /**
     * 搜索EditText
     */
    @BindView(R.id.ed_search)
    EditText edSearch;
    /**
     * 搜索菜谱结果recyclerView
     */
    @BindView(R.id.recyclerView_search)
    RecyclerView recyclerViewRecipeResult;
    /**
     * 搜索菜谱结果为空
     */
    @BindView(R.id.lLayout_search_empty)
    LinearLayout lLayoutSearchEmpty;
    /**
     * 取消搜索icon
     */
    @BindView(R.id.img_delete_icon)
    ImageView imgCancel;
    /**
     * 退出当前搜索Activity
     */
    @BindView(R.id.btn_exit)
    TextView tvExit;
    /**
     * 搜索
     */
    @BindView(R.id.btn_search)
    TextView tvSearch;
    /**
     * 输入法管理器
     */
    private InputMethodManager inputMethodManager;
    private ArrayList<String> historySearchList = new ArrayList<>();
    /**
     * 搜索菜谱集合
     */
    private List<Recipe> recipeList = new ArrayList<>();
    /**
     * 菜谱页数
     */
    private int pageId = 0;
    /**
     * 菜谱总数
     */
    private int recipeCount = 0;
    /**
     * 搜索关键字
     */
    private String keyString;
    private LocalRecipePresenter recipesPresenter;

    /**
     * 页面加载提示框
     */
    private LoadingDialog loadingDialog;
    private RecipesSearchRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initPresenter();
    }

    private void initView() {
        //控制软键盘
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        readSearchHistory();
        RecipeSearchActivity.OnSearchEditorActionListener onSearchEditorActionListener = new RecipeSearchActivity.OnSearchEditorActionListener();
        edSearch.setOnEditorActionListener(onSearchEditorActionListener);
        RecipeSearchActivity.OnSearchScrollListener onSearchScrollListener = new RecipeSearchActivity.OnSearchScrollListener();
        recyclerViewRecipeResult.addOnScrollListener(onSearchScrollListener);
        //点击item进入菜谱详情时间
        recyclerViewRecipeResult.addOnItemTouchListener(new FavoriteItemClickListener(this, this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        recyclerViewRecipeResult.setLayoutManager(layoutManager);
        recyclerViewRecipeResult.setVisibility(View.GONE);
        adapter = new RecipesSearchRecyclerAdapter(this, recipeList);
        recyclerViewRecipeResult.setAdapter(adapter);

        imgCancel.setOnClickListener(this);
        tvExit.setOnClickListener(this);
        tvSearch.setOnClickListener(this);


    }

    private void initPresenter() {
        recipesPresenter = new LocalRecipePresenter(this);
        recipesPresenter.onCreate(compositeSubscription);
        recipesPresenter.attachLocalRecipeView(recipeView);
        loadingDialog = new LoadingDialog(RecipeSearchActivity.this, R.style.CommonDialog);
    }

    LocalRecipeView recipeView = new LocalRecipeView() {
        @Override
        public void onRecipeCategoryListSuccess(List<RecipeCategory> categoryList) {
            LogUtil.LOG_RECIPE("RecipeCategoryListSuccess", "获取菜谱分类标签成功");
        }

        @Override
        public void onRecideCategoryListError(String e) {
            LogUtil.LOG_RECIPE("RecideCategoryListError", "获取菜谱分类标签失败");
        }

        @Override
        public void onRecipeListSuccess(List<Recipe> list) {
            LogUtil.LOG_RECIPE("onRecipeListSuccess", "获取菜谱成功");
            loadingDialog.dismiss();
            if (list.size() > 0 && list != null) {
                recyclerViewRecipeResult.setVisibility(View.VISIBLE);
                lineWrapLayout.setVisibility(View.GONE);
                lLayoutSearchEmpty.setVisibility(View.GONE);
                recipeList.addAll(list);
                pageId++;
                adapter.updateRecipeList(recipeList);
                adapter.notifyDataSetChanged();
                recyclerViewRecipeResult.scrollToPosition(0);

            } else {
                lLayoutSearchEmpty.setVisibility(View.VISIBLE);
                recyclerViewRecipeResult.setVisibility(View.GONE);
                lineWrapLayout.setVisibility(View.GONE);
                LogUtil.LOG_RECIPE("RecipeEmpty", "获取菜谱数据为空");
            }
        }

        @Override
        public void onRecipeListError(String e) {
            LogUtil.LOG_RECIPE("onRecipeListError", "获取菜谱失败");
        }
    };


    private void readSearchHistory() {
        SharedPreferences preferences = getSharedPreferences(SHARE_PREFERENCES_NAME, MODE_PRIVATE);
        String json = preferences.getString(SHARE_PREFERENCES_KEY, "[]");
        LogUtil.LOGE("---json",json);
        Gson gson = new Gson();
        lineWrapLayout.removeAllViews();
        historySearchList = (ArrayList<String>) gson.fromJson(json, ArrayList.class);
        for (String key : historySearchList) {
            final Button btn = new Button(this);
            if (key.length() >= 10) {
                String s = key.substring(0, 10) + "...";
                btn.setText(s);
                btn.setTag(key);
            } else {
                btn.setText(key);
            }
            btn.setAllCaps(false);
            btn.setTextSize(26);
            btn.setAlpha((float) 0.7);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn.getTag() != null) {
                        String history = btn.getTag().toString();
                        edSearch.setText(history);
                        edSearch.setSelection(history.length());
                        historySearchList.remove(history);
                        historySearchList.add(0, history);
                    }
                    else{
                        edSearch.setText(btn.getText());
                        edSearch.setSelection(btn.getText().length());
                        historySearchList.remove(btn.getText().toString());
                        historySearchList.add(0, btn.getText().toString());
                    }
                    recipeList.clear();
                    pageId = 0;
                    displaySearchResultHistory(btn.getText().toString());
                    saveSearchHistory();
                }
            });
            lineWrapLayout.addView(btn);
        }
    }

    /**
     * 显示历史记录数据
     *
     * @param queryString
     */
    private void displaySearchResultHistory(String queryString) {
        hideKeyboard();
        lineWrapLayout.setVisibility(View.GONE);
        keyString = queryString;
        if (Tool.isNetworkAvailable(this)) {
            recipesPresenter.getRecipeListBySearchKey(keyString, pageId, Config.COOKER_PRODUCT_ID);
        } else {
            recipesPresenter.getRecipeListBySearchKey(keyString, -1, "");
        }
        loadingDialog.show();
    }

    /**
     * 监听点击回车键执行搜索
     */
    private class OnSearchEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            LogUtil.LOGD(TAG, "OnSearchEditorActionListener:onEditorAction");
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String queryString = edSearch.getText().toString().trim();
                if (TextUtils.isEmpty(queryString)) {
                    Toast toast = Toast.makeText(context, "请输入菜谱名称", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    hideKeyboard();
                } else {
                    if (!historySearchList.contains(queryString)) {
                        if (historySearchList.size() == MAX_QUERY_KEY_STRING_COUNT) {
                            historySearchList.remove(historySearchList.size() - 1);
                        }
                        historySearchList.add(0, queryString);
                    } else {
                        historySearchList.remove(queryString);
                        historySearchList.add(0, queryString);
                    }
                    //请求数据前清空list
                    recipeList.clear();
                    pageId = 0;
                    saveSearchHistory();
                    displaySearchResultEditorAction(queryString);
                }

            }
            return false;
        }
    }

    private void doSearch() {
        String queryString = edSearch.getText().toString().trim();
        if (TextUtils.isEmpty(queryString)) {
            Toast toast = Toast.makeText(context, "请输入菜谱名称", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            hideKeyboard();
        } else {
            if (!historySearchList.contains(queryString)) {
                if (historySearchList.size() == MAX_QUERY_KEY_STRING_COUNT) {
                    historySearchList.remove(historySearchList.size() - 1);
                }
                historySearchList.add(0, queryString);
            } else {
                historySearchList.remove(queryString);
                historySearchList.add(0, queryString);
            }
            //请求数据前清空list
            recipeList.clear();
            pageId = 0;
            saveSearchHistory();
            displaySearchResultEditorAction(queryString);
        }

    }

    /**
     * 滑到最底端
     */
    private class OnSearchScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);


            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            //屏幕中最后一个可见子项的position
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            //当前屏幕所看到的子项个数
            int visibleItemCount = layoutManager.getChildCount();
            //当前RecyclerView的所有子项个数
            int totalItemCount = layoutManager.getItemCount();
            //RecyclerView的滑动状态
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && pageId <= recipeCount /
                        10) {
                    if (Tool.isNetworkAvailable(RecipeSearchActivity.this))
                        recipesPresenter.getRecipeListBySearchKey(keyString, pageId, Config.COOKER_PRODUCT_ID);
                }
            }
        }
    }

    /**
     * 显示请求搜索数据
     *
     * @param queryString
     */
    private void displaySearchResultEditorAction(String queryString) {
        hideKeyboard();
        lLayoutSearchEmpty.setVisibility(View.GONE);
        recyclerViewRecipeResult.setVisibility(View.GONE);
        lineWrapLayout.setVisibility(View.GONE);
        keyString = queryString;
        if (Tool.isNetworkAvailable(this)) {
            recipesPresenter.getRecipeListBySearchKey(keyString, pageId, Config.COOKER_PRODUCT_ID);
        } else {
            recipesPresenter.getRecipeListBySearchKey(keyString, -1, "");
        }
        loadingDialog.show();
    }

    /**
     * 存储搜索记录
     */
    private void saveSearchHistory() {
        Gson gson = new Gson();
        String json = gson.toJson(this.historySearchList);
        SharedPreferences preferences = getSharedPreferences(SHARE_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SHARE_PREFERENCES_KEY, json);
        editor.commit();
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (null != getCurrentFocus()) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        LogUtil.LOGE("--RecipeSearch", position);
        //进去菜谱基本信息页面
        launchActivity(RecipesDetailsActivity.class, position);
    }

    public void launchActivity(Class<?> activityClass, int position) {
        Intent intent = new Intent(this, activityClass);
        Bundle bundle = new Bundle();
        bundle.putParcelable("recipe", recipeList.get(position));
        intent.putExtras(bundle);
        launchActivity(intent);
    }

    /**
     * 回到历史记录界面
     */
    private void backToHistory() {
        readSearchHistory();
        recyclerViewRecipeResult.setVisibility(View.GONE);
        lLayoutSearchEmpty.setVisibility(View.GONE);
        lineWrapLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_recipe_search;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_delete_icon:
                edSearch.setText("");
                backToHistory();
                inputMethodManager.showSoftInput(edSearch, 0);
                break;
            case R.id.btn_search:
                doSearch();
                break;
            case R.id.btn_exit:
                finish();
                break;


        }

    }

    @Override
    public void finish() {
        if (recyclerViewRecipeResult.getVisibility() == View.VISIBLE) {
            backToHistory();
        } else {
            super.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismiss();
    }

    @Override
    public boolean showBottom() {
        return true;
    }
}
