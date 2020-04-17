package com.fotile.x1i.activity.music;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.common.util.log.LogUtil;
import com.fotile.music.adapter.MusicSearchRecyclerAdapter;
import com.fotile.music.listener.FavoriteItemClickListener;
import com.fotile.music.model.view.MusicsSearchView;
import com.fotile.music.presenter.MusicSearchPresenter;
import com.fotile.music.widget.LineWrapLayout;
import com.fotile.x1i.R;
import com.fotile.x1i.listener.SoftKeyBoardListener;
import com.fotile.x1i.widget.SnakeBar;
import com.google.gson.Gson;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import java.util.ArrayList;


/**
 * 文件名称：MusicSearchActivity
 * 创建时间：17-8-17 下午2:59
 * 文件作者：zhangqiang
 * 功能描述：Music搜索界面
 */
public class MusicSearchActivity extends BaseMusicActivity implements FavoriteItemClickListener.OnItemClickListener,
        View.OnClickListener {

    private static final String TAG = "MusicSearchActivity";

    /**
     * 缓存最大的搜索历史关键词数量
     */
    private static final int MAX_QUERY_KEY_STRING_COUNT = 5;

    /**
     * 搜索的历史记录保存的文件名
     */
    private static final String SHARE_PREFERENCES_NAME = "searchHistoryList";
    private static final String SHARE_PREFERENCES_KEY = "searchMusicList";

    /**
     * 搜索editText
     */
    EditText edSearch;
    /**
     * 搜索显示GridView
     */
    RecyclerView recyclerViewMusicSearch;
    /**
     * 搜索图标
     */
    ImageView imgSearchIcon;
    /**
     * 清除图标
     */
    ImageView imgCancelIcon;
    /**
     * 历史记录
     */
    LineWrapLayout lineWrapLayout;
    /**
     * 清除历史记录
     */
    //    ImageView imgDeleteHistory;
    /**
     * 显示历史记录的LinearLayout
     */
    LinearLayout lLayoutHistory;
    /**
     * 显示历史记录的textView
     */
    //    TextView tvHistory;
    /**
     * 搜索菜谱结果为空提示布局
     */
    LinearLayout lLayoutSearchEmpty;
    /**
     * 搜索菜谱结果为空提示img
     */
    ImageView imgSearchEmpty;
    /**
     * 搜索菜谱结果为空提示TextView
     */
    TextView tvMusicSearchEmpty;
    /**
     * 搜索菜谱结果为空刷新按钮
     */
    TextView tvMusicSearchEmptyRefresh;
    /**
     * view
     */
    View view;
    View viewKeyboard;
    View viewHistory;
    /**
     * search column
     */
    LinearLayout search_column;
    LinearLayout currentActivityContent;
    private int pageId = 1;

    private ArrayList<String> historySearchList = new ArrayList<>();
    private SearchTrackList searchTrackList;
    private MusicSearchPresenter musicSearchPresenter = new MusicSearchPresenter();

    private MusicSearchRecyclerAdapter adapter;

    private String queryString;
    private Context context;
    /**
     * 输入法管理器
     */
    private InputMethodManager inputMethodManager;

    /**
     * 页面加载提示框
     */
    //    private CommonDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initView();
        initData();
    }


    private void initData() {
        musicSearchPresenter.onCreate(compositeSubscription);
        musicSearchPresenter.attachView(musicSearchView);
        //        loadingDialog = new CommonDialog(MusicSearchActivity.this, CommonDialog.CommonTip.ONE_BTN);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_search;
    }


    private void initView() {
        edSearch = (EditText) findViewById(R.id.ed_search);
        recyclerViewMusicSearch = (RecyclerView) findViewById(R.id.recyclerView_search);
        imgSearchIcon = (ImageView) findViewById(R.id.img_search_icon);
        imgCancelIcon = (ImageView) findViewById(R.id.img_cancel_icon);
        lineWrapLayout = (LineWrapLayout) findViewById(R.id.history_search);
        lLayoutHistory = (LinearLayout) findViewById(R.id.lLayout_history);
        lLayoutSearchEmpty = (LinearLayout) findViewById(R.id.lLayout_search_empty);
        imgSearchEmpty = (ImageView) findViewById(R.id.img_search_empty);
        tvMusicSearchEmpty = (TextView) findViewById(R.id.tv_music_search_empty);
        tvMusicSearchEmptyRefresh = (TextView) findViewById(R.id.tv_music_search_refresh);
        //        imgDeleteHistory = (ImageView) findViewById(R.id.img_btn_deleteHistory);
        search_column = (LinearLayout) findViewById(R.id.search_column);
        currentActivityContent = (LinearLayout) findViewById(R.id.current_activity_content);
        view = findViewById(R.id.view);
        viewKeyboard = findViewById(R.id.view_keyboard);
        viewHistory = findViewById(R.id.view_history);

        findViewById(R.id.btn_music_search).setOnClickListener(this);
        findViewById(R.id.btn_music_search_cancel).setOnClickListener(this);

        //        tvHistory = (TextView) findViewById(R.id.tv_history);

        //控制软键盘
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        readSearchHistory();
        recyclerViewMusicSearch.addOnItemTouchListener(new FavoriteItemClickListener(this, this));
        OnSearchEditorActionListener listener = new OnSearchEditorActionListener();
        edSearch.setOnEditorActionListener(listener);
        OnSearchScrollListener onSearchScrollListener = new OnSearchScrollListener();
        recyclerViewMusicSearch.addOnScrollListener(onSearchScrollListener);
        adapter = (MusicSearchRecyclerAdapter) recyclerViewMusicSearch.getAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMusicSearch.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        adapter = new MusicSearchRecyclerAdapter(this);
        recyclerViewMusicSearch.setAdapter(adapter);
        recyclerViewMusicSearch.setVisibility(View.GONE);
        imgCancelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edSearch.setText("");
                backToHistory();
                inputMethodManager.showSoftInput(edSearch, 0);
            }
        });
        /*imgDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
                readSearchHistory();
            }
        });*/

        SoftKeyBoardListener.setListener(this,
                new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
                    @Override
                    public void keyBoardShow(int height) {
                        LogUtil.LOGE("KeyBoard-SHOW", 1);
                        //                         currentActivityContent.setVisibility(View.GONE);
                        viewKeyboard.setVisibility(View.VISIBLE);
                        //                        viewHistory.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void keyBoardHide(int height) {
                        LogUtil.LOGE("KeyBoard-HIDE", 2);
                        //                          currentActivityContent.setVisibility(View.VISIBLE);
                        viewKeyboard.setVisibility(View.GONE);
                        //                        viewHistory.setVisibility(View.GONE);


                    }
                });

        //刷新按钮
        tvMusicSearchEmptyRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2019/6/11 此处直接取上一次的 queryString 和 pageId，若在刷新前做了什么动作导致该二参数变化可能会影响刷新结果
                //请求数据
                musicSearchPresenter.getMusicSearch(queryString, pageId);
                //                loadingDialog.show();
                lLayoutSearchEmpty.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 读取历史记录
     */
    private void readSearchHistory() {
        SharedPreferences preferences = getSharedPreferences(SHARE_PREFERENCES_NAME, MODE_PRIVATE);
        String json = preferences.getString(SHARE_PREFERENCES_KEY, "[]");
        Gson gson = new Gson();
        lineWrapLayout.removeAllViews();
        historySearchList = (ArrayList<String>) gson.fromJson(json, ArrayList.class);

        for (String key : historySearchList) {
            final Button btn = new Button(this);
            if (key.length() >= 10) {
                String s = key.substring(0, 10) + "...";
                btn.setTag(key);
                btn.setText(s);
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
                    if (null != searchTrackList) {
                        searchTrackList.getTracks().clear();
                    }
                    pageId = 1;
                    queryString = btn.getText().toString();
                    //请求数据
                    musicSearchPresenter.getMusicSearch(queryString, pageId);
                    //                    loadingDialog.show();
                    hideKeyboard();
                    lLayoutHistory.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                    saveSearchHistory();
                }
            });
            lineWrapLayout.addView(btn);
        }
    }

    /**
     * 清除历史记录
     */
    /*private void clearHistory() {
        Gson gson = new Gson();
        historySearchList.clear();
        String json = gson.toJson(this.historySearchList);
        SharedPreferences preferences = getSharedPreferences(SHARE_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SHARE_PREFERENCES_KEY, json);
        editor.commit();
    }*/

    /**
     * 存储历史记录
     */
    private void saveSearchHistory() {
        Gson gson = new Gson();
        String json = gson.toJson(this.historySearchList);
        SharedPreferences preferences = getSharedPreferences(SHARE_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SHARE_PREFERENCES_KEY, json);
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_music_search:
                searchMusic();
                break;
            case R.id.btn_music_search_cancel:
                launchActivity(MusicOnlineActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * 滑到最右端刷新数据
     */
    private class OnSearchScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int lastVisibleItem = manager.findLastVisibleItemPosition();
            int totalItemCount = manager.getItemCount();
            int visibleItemCount = manager.getChildCount();
            if (visibleItemCount > 0 && lastVisibleItem == totalItemCount - 1 &&
                    newState == recyclerView.SCROLL_STATE_IDLE) {
                if (pageId < searchTrackList.getTotalPage()) {
                    musicSearchPresenter.getMusicSearch(queryString, pageId);
                }
            }
        }
    }

    /**
     * 回调的搜索列表
     */
    MusicsSearchView musicSearchView = new MusicsSearchView() {

        @Override
        public void onSuccess(SearchTrackList list) {
            Log.e(TAG, "on search success: " + list);
            recyclerViewMusicSearch.setVisibility(View.VISIBLE);
            lLayoutSearchEmpty.setVisibility(View.GONE);
            //            loadingDialog.dismiss();
            if (searchTrackList == null) {
                searchTrackList = list;
            } else {
                searchTrackList.getTracks().addAll(list.getTracks());
            }
            pageId++;
            adapter.setSearchTrackList(searchTrackList);
            adapter.notifyDataSetChanged();
            recyclerViewMusicSearch.scrollToPosition(0);
            Log.e(TAG, "show list: " + recyclerViewMusicSearch.getAdapter().getItemCount() + ", " +
                    recyclerViewMusicSearch.getVisibility());
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, "on search error: " + error);
            switch (error) {
                //搜索请求数据未搜索到
                case MusicSearchPresenter.DATA_NULL:
                case MusicSearchPresenter.ILLEGAL_ERROR:
                    imgSearchEmpty.setImageResource(R.mipmap.img_empty_search);
                    tvMusicSearchEmpty.setText(getString(R.string.str_search_music_empty));
                    tvMusicSearchEmptyRefresh.setVisibility(View.GONE);
                    break;
                default:
                    //搜索请求数据网络异常
                    //                    tvMusicSearchEmpty.setText(getString(R.string.network_error));
                    imgSearchEmpty.setImageResource(R.mipmap.img_internet_off);
                    tvMusicSearchEmpty.setText(getString(R.string.str_network_disabled));
                    tvMusicSearchEmptyRefresh.setVisibility(View.VISIBLE);
                    break;
            }
            lLayoutSearchEmpty.setVisibility(View.VISIBLE);
            //            loadingDialog.dismiss();
        }
    };


    /**
     * 搜索列表的item点击事件，并且播放音乐
     *
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        XmPlayerManager.getInstance(MusicSearchActivity.this).playList(searchTrackList, position);
        launchActivity(MusicPlayActivity.class);
    }

    /**
     * 监听点击回车键执行搜索
     */
    private class OnSearchEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                return searchMusic();
            }
            return true;
        }
    }

    private boolean searchMusic() {
        queryString = edSearch.getText().toString().trim();
        if (TextUtils.isEmpty(queryString)) {
            SnakeBar.makeMsgSnake(context, "请输入音乐名称").show();
            hideKeyboard();
            return false;
        }

        if (!historySearchList.contains(queryString)) {
            if (historySearchList.size() == MAX_QUERY_KEY_STRING_COUNT) {
                historySearchList.remove(historySearchList.size() - 1);
            }
            historySearchList.add(0, queryString);
        } else {
            historySearchList.remove(queryString);
            historySearchList.add(0, queryString);
        }
        if (null != searchTrackList) {
            searchTrackList.getTracks().clear();
        }
        pageId = 1;
        saveSearchHistory();
        //请求数据
        musicSearchPresenter.getMusicSearch(queryString, pageId);
        //        loadingDialog.show();
        hideKeyboard();
        lLayoutSearchEmpty.setVisibility(View.GONE);
        lLayoutHistory.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        recyclerViewMusicSearch.setVisibility(View.GONE);
        return true;
    }

    @Override
    public void finish() {
        if (recyclerViewMusicSearch.getVisibility() == View.VISIBLE &&
                search_column.getVisibility() == View.VISIBLE) {
            backToHistory();
        } else {
            super.finish();
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (null != getCurrentFocus()) {
            inputMethodManager.hideSoftInputFromWindow(
                    getCurrentFocus().getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 回到历史记录界面
     */
    private void backToHistory() {
        readSearchHistory();
        lLayoutHistory.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        recyclerViewMusicSearch.setVisibility(View.GONE);
        lLayoutSearchEmpty.setVisibility(View.GONE);
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    protected void onDestroy() {
        //        loadingDialog.dismiss();
        super.onDestroy();
    }
}
