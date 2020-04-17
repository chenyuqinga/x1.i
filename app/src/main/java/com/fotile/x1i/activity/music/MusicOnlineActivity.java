package com.fotile.x1i.activity.music;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotile.common.util.Tool;
import com.fotile.music.adapter.MusicAlbumViewpagerAdapter;
import com.fotile.music.model.view.MusicOnlineView;
import com.fotile.music.presenter.MusicOnlinePresenter;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.WifiListActivity;
import com.fotile.x1i.bean.event.WifiConnection;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.metadata.Attributes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MusicOnlineActivity extends BaseMusicActivity implements View.OnClickListener {
    public static final String TAG = MusicOnlineActivity.class.getSimpleName();
    /**
     * 音乐Icon
     */
    //    ImageView icon_music;
    /**
     * 音乐文字
     */
    //    TextView text_music;

    /**
     * 网络状态改变时，更新音乐
     */
    private static final int MESSAGE_UPDATE_MUSIC = 1;
    /**
     * 专辑分类tab按键
     */
    TabLayout tabLayoutCategory;
    /**
     * 专辑的viewpager
     */
    ViewPager viewPagerMusicContent;
    /**
     * 显示音乐专辑viewpager区域
     */
    LinearLayout currentActivityContent;
    /**
     * 搜索框
     */
    FrameLayout searchLayout;
    /**
     * Head Title
     */
    RelativeLayout headTitle;
    /**
     * 提示语信息
     */
    TextView tvTip;
    /**
     * 连接网络按钮
     */
    TextView tvConnectNetwork;
    /**
     * 请求错误重连
     */
    TextView tvRefreshMusic;
    /**
     * tabview
     */
    TextView tabview;
    /**
     * 提示区域
     */
    LinearLayout lLayoutTipArea;
    /**
     * 请求数据之后的回调
     */
    private MusicOnlinePresenter musicOnlinePresenter = new MusicOnlinePresenter();

    private boolean isSlide;

    /**
     * 音乐专辑界面tablayout与viewpager的适配器
     */
    private MusicAlbumViewpagerAdapter musicAlbumViewpagerAdapter;

    /**
     * 音乐类型
     */
    private List<Attributes> attributesList = new ArrayList<>();
    /**
     * 分类的数据的fragment
     */
    private List<Fragment> fragments = new ArrayList<>();

    final int DIALOG_LOADING = 1001;

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_online;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        //        registerWifiReceiver();
        initData();
    }

    @Nullable
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        return super.onCreateDialog(id, args);
    }

    private void initData() {
        Log.e(TAG, "init data");
        //请求专辑列表
        musicOnlinePresenter.onCreate(compositeSubscription);
        musicOnlinePresenter.attachView(musicOnlineView);
        if (Tool.isNetworkAvailable(this)) {
            getOnlineData();
        } else {
            updateTip();
            searchLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 获取专辑列表数据
     */
    private void getOnlineData() {
        Log.e(TAG, "getOnlineData");
        //获取专辑列表
        musicOnlinePresenter.getMetadataList();
        //显示加载框
        //        showDialog(DIALOG_LOADING);
    }

    private void initView() {
        tabLayoutCategory = (TabLayout) findViewById(R.id.tabs_category);
        viewPagerMusicContent = (ViewPager) findViewById(R.id.viewPager_music_content);
        currentActivityContent = (LinearLayout) findViewById(R.id.current_activity_content);
        searchLayout = (FrameLayout) findViewById(R.id.search_column);
        //        icon_music = (ImageView) findViewById(R.id.icon_music);
        //        text_music = (TextView) findViewById(R.id.text_music);
        headTitle = (RelativeLayout) findViewById(R.id.rl_music_online_top);
        tabview = (TextView) findViewById(R.id.tab_item_textview);
        lLayoutTipArea = (LinearLayout) findViewById(R.id.lLayout_music_tip_area);
        tvConnectNetwork = (TextView) findViewById(R.id.tv_music_connect_network);
        tvRefreshMusic = (TextView) findViewById(R.id.tv_music_refresh);
        tvTip = (TextView) findViewById(R.id.tv_music_tip);

        tvConnectNetwork.setOnClickListener(this);
        tvRefreshMusic.setOnClickListener(this);

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(MusicSearchActivity.class);
            }
        });
        tabLayoutCategory.setTabMode(TabLayout.MODE_SCROLLABLE);
        //切换页面发现无网时显示提示信息
        viewPagerMusicContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!Tool.isNetworkAvailable(MusicOnlineActivity.this)) {
                    currentActivityContent.setVisibility(View.GONE);
                    attributesList.clear();
                    updateTip();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayoutCategory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerMusicContent.setCurrentItem(tab.getPosition());
                View view = tab.getCustomView();
                if (view instanceof TextView) {
                    ((TextView) view).setTextSize(getResources().getDimension(R.dimen.tab_select));
                    ((TextView) view).setTextColor(getResources().getColor(R.color.select_txt));
                    reflex(tabLayoutCategory);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view instanceof TextView) {
                    ((TextView) view).setTextSize(
                            getResources().getDimension(R.dimen.tab_unselect));
                    ((TextView) view).setTextColor(getResources().getColor(R.color.un_select_txt));
                    reflex(tabLayoutCategory);
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        reflex(tabLayoutCategory);
    }

    /**
     * 进入网络连接页
     *
     * @param
     */
    private void enterSettingPage() {
        launchActivity(WifiListActivity.class);
    }

    /**
     * 自定义Tab的View
     */
    private View getTabView(int currentPosition) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        TextView textView = (TextView) view.findViewById(R.id.tab_item_textview);
        textView.setText(attributesList.get(currentPosition).getDisplayName());
        if (currentPosition == 0) {
            textView.setTextSize(getResources().getDimension(R.dimen.tab_select));
            textView.setTextColor(getResources().getColor(R.color.select_txt));
        }
        return view;
    }

    @Override
    protected void onDestroy() {
        //        unregisterReceiver(wifiReceiver);
        musicHandler.removeMessages(MESSAGE_UPDATE_MUSIC);
        removeDialog(DIALOG_LOADING);
        musicOnlinePresenter.disAttachView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                viewPagerMusicContent.requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_CANCEL:
                viewPagerMusicContent.requestDisallowInterceptTouchEvent(false);
                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 请求回调处理
     */
    MusicOnlineView musicOnlineView = new MusicOnlineView() {
        @Override
        public void onGetMetadataAlbumListSuccess(AlbumList albumList) {
            Log.e(TAG, "onGetMetadataAlbumListSuccess");
        }

        @Override
        public void onGetMetadataAlbumListError(String errorMsg) {
            Log.e(TAG, "onGetMetadataAlbumListError");
        }

        @Override
        public void onGetMetadataListSuccess(List<Attributes> list) {
            Log.e(TAG, "onGetMetadataListSuccess");
            hideTip();
            //            dismissDialog(DIALOG_LOADING);
            //set音乐类型列表
            attributesList.clear();
            attributesList.addAll(list);
            currentActivityContent.setVisibility(View.VISIBLE);
            initFragment();
        }

        @Override
        public void onGetMetadataListError(String errorMsg) {
            Log.e(TAG, "onGetMetadataListError: " + errorMsg);
            updateTip();
            //            dismissDialog(DIALOG_LOADING);
        }
    };

    private void initFragment() {
        fragments.clear();
        //请求成功后创建Fragment
        for (int i = 0; i < attributesList.size(); i++) {
            tabLayoutCategory.addTab(
                    tabLayoutCategory.newTab().setText(attributesList.get(i).getDisplayName()));
            String metadataAttributes = attributesList.get(i).getAttrKey() + ":" +
                                        attributesList.get(i).getAttrValue();
            MusicAlbumFragment musicAlbumFragment = new MusicAlbumFragment(metadataAttributes);
            fragments.add(musicAlbumFragment);
        }

        musicAlbumViewpagerAdapter = new MusicAlbumViewpagerAdapter(getSupportFragmentManager(),
                viewPagerMusicContent, fragments, attributesList);
        viewPagerMusicContent.setAdapter(musicAlbumViewpagerAdapter);
        searchLayout.setVisibility(View.VISIBLE);
        tabLayoutCategory.setupWithViewPager(viewPagerMusicContent);
        //设置tabview
        for (int i = 0; i < tabLayoutCategory.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayoutCategory.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
        }
        reflex(tabLayoutCategory);
    }


    /**
     * 控制viewpager是否可以滑动
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!isSlide) {
                    viewPagerMusicContent.requestDisallowInterceptTouchEvent(true);
                } else {
                    viewPagerMusicContent.requestDisallowInterceptTouchEvent(false);
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isSlide) {
                    viewPagerMusicContent.requestDisallowInterceptTouchEvent(false);
                } else {
                    viewPagerMusicContent.requestDisallowInterceptTouchEvent(true);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setSlide(boolean isSlide) {
        this.isSlide = isSlide;
    }

    public void reflex(final TabLayout tabLayout) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    Field mTabStripField = tabLayout.getClass().getDeclaredField("mTabStrip");
                    mTabStripField.setAccessible(true);

                    LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);

                    int dp10 = dip2px(tabLayout.getContext(), 22);

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //字多宽线就多宽，测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView
                                .getLayoutParams();
                        //乘以3解决点击变两行的问题
                        params.width = width * 3;
                        params.leftMargin = dp10;
                        params.rightMargin = dp10;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 注册网络监听
     */
    private void registerWifiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, intentFilter);
    }

    /**
     * 监听wifi是否链接上
     */
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != info && info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    musicHandler.removeMessages(MESSAGE_UPDATE_MUSIC);
                    musicHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_MUSIC, 1600);
                }
            }
        }
    };

    private Handler musicHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //网络状态改变时，更新音乐
                case MESSAGE_UPDATE_MUSIC:
                    if (attributesList.size() == 0) {
                        getOnlineData();
                    }
                    break;
            }
            return false;
        }
    });

    /**
     * 更新提示
     */
    private void updateTip() {
        if (attributesList.size() != 0) {
            return;
        }
        //网络未连接
        if (!Tool.isNetworkAvailable(this)) {
            headTitle.setVisibility(View.VISIBLE);
            currentActivityContent.setVisibility(View.GONE);
            lLayoutTipArea.setVisibility(View.VISIBLE);
            tvTip.setVisibility(View.VISIBLE);
            tvTip.setText(getString(R.string.str_network_unavailable_tip));
            tvConnectNetwork.setVisibility(View.VISIBLE);
            tvRefreshMusic.setVisibility(View.GONE);
        }
        //请求超时或者网络请求不到数据
        else {
            currentActivityContent.setVisibility(View.GONE);
            headTitle.setVisibility(View.VISIBLE);
            lLayoutTipArea.setVisibility(View.VISIBLE);
            tvConnectNetwork.setVisibility(View.GONE);
            tvTip.setVisibility(View.VISIBLE);
            tvTip.setText(getString(R.string.str_network_disabled));
            tvRefreshMusic.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏提示
     */
    private void hideTip() {
                lLayoutTipArea.setVisibility(View.GONE);
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Subscribe
    public void onWifiStateChange(WifiConnection connection) {
        if (connection != null) {
            if (connection.isConnected()) {//若连接上wifi
                musicHandler.removeMessages(MESSAGE_UPDATE_MUSIC);
                musicHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_MUSIC, 1600);
            } else {

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_music_connect_network:
                enterSettingPage();
                break;
            case R.id.tv_music_refresh:
                getOnlineData();
                break;
            default:
                break;
        }
    }
}
