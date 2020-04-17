package com.fotile.x1i.activity.music;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.music.adapter.MusicTrackRecyclerAdapter;
import com.fotile.music.listener.FavoriteItemClickListener;
import com.fotile.music.model.view.MusicTrackView;
import com.fotile.music.presenter.MusicTrackPresenter;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.WifiListActivity;
import com.fotile.x1i.dailog.CommonDialog;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

/**
 * 文件名称：MusicAlbum
 * 创建时间：17-8-16 下午2:14
 * 文件作者：zhangqiang
 * 功能描述：显示音乐歌曲列表界面
 */
public class MusicTrackActivity extends BaseMusicActivity implements FavoriteItemClickListener.OnItemClickListener {

    private static final String TAG = "MusicTrackActivity";
    /**
     * 断网
     */
    ImageView imgInternetOff;
    /**
     * 网络状态改变时，更新音乐
     */
    private static final int MESSAGE_UPDATE_MUSIC = 1;

    /**
     * 专辑名称
     */
    TextView tvCategoryTitle;

    /**
     * 提示区域
     */
    LinearLayout lLayoutContentArea;
    /**
     * 提示区域-网络连接失败
     */
    LinearLayout lLayoutTipArea;
    /**
     * 提示语信息
     */
    TextView tvTip;
    /**
     * 连接网络按钮
     */
    TextView btnConnectNetwork;
    /**
     * 刷新网络按钮
     */
    TextView btnRefreshNetwork;
    /**
     * 专辑下的音乐列表
     */
    RecyclerView recyclerViewMusicTrack;

    private int pageId = 1;
    private TrackList trackList;
    /**
     * 专辑ID
     */
    private String albumId;
    private MusicTrackPresenter musicTrackPresenter = new MusicTrackPresenter();
    /**
     * 是否是从主页跳转
     */
    private boolean isFromHome;
    /**
     * 音乐列表适配器
     */
    private MusicTrackRecyclerAdapter musicTrackRecyclerAdapter;

    final int DIALOG_LOADING = 1001;

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_track;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        isFromHome = intent.getBooleanExtra("isFromHome", false);
        String albumTitle = intent.getStringExtra(MusicAlbumFragment.TITLE);
        albumId = intent.getStringExtra(MusicAlbumFragment.ALBUMID);

        musicTrackPresenter.onCreate(compositeSubscription);
        musicTrackPresenter.attachView(musicTrackView);
        if (isNetworkAvailable(this)) {
            updateTip(true);
            musicTrackPresenter.getDataTrack(albumId, pageId);
        } else {
            updateTip(false);
        }
        tvCategoryTitle.setText(albumTitle);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_LOADING) {
            CommonDialog loadingDialog = new CommonDialog(MusicTrackActivity.this, CommonDialog.CommonTip.ONE_BTN);
            return loadingDialog;
        }
        return super.onCreateDialog(id);
    }

    private void initView() {
        tvCategoryTitle = (TextView) findViewById(R.id.tv_category_title);
        lLayoutContentArea = (LinearLayout) findViewById(R.id.lLayout_music_track_content);
        lLayoutTipArea = (LinearLayout) findViewById(R.id.lLayout_music_track_tip_area);
        tvTip = (TextView) findViewById(R.id.tv_music_track_tip);
        btnConnectNetwork = (TextView) findViewById(R.id.tv_music_track_connect_network);
        btnRefreshNetwork = (TextView) findViewById(R.id.tv_music_refresh);
        recyclerViewMusicTrack = (RecyclerView) findViewById(R.id.recyclerView_track);
        imgInternetOff = (ImageView) findViewById(R.id.img_internet_off);

        recyclerViewMusicTrack.addOnItemTouchListener(new FavoriteItemClickListener(this, this));
        OnTrackScrollListener listener = new OnTrackScrollListener();
        recyclerViewMusicTrack.addOnScrollListener(listener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        recyclerViewMusicTrack.setLayoutManager(layoutManager);
        musicTrackRecyclerAdapter = new MusicTrackRecyclerAdapter(this);
        recyclerViewMusicTrack.setAdapter(musicTrackRecyclerAdapter);
        lLayoutContentArea.setVisibility(View.GONE);
        registerWifiReceiver();
        btnConnectNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterSettingPage();

            }
        });
        btnRefreshNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnlineData();
                //                showDialog(DIALOG_LOADING);
            }
        });
    }

    /**
     * 滑到最右端
     */
    private class OnTrackScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int lastVisibleItem = manager.findFirstVisibleItemPosition();
            int totalItemCount = manager.getItemCount();

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (lastVisibleItem == (totalItemCount - 10)) {
                    musicTrackPresenter.getDataTrack(albumId, pageId);
                }
            }
        }
    }

    /**
     * 回调的专辑下的音乐列表
     */
    MusicTrackView musicTrackView = new MusicTrackView() {
        @Override
        public void onSuccess(TrackList list) {
            if (trackList == null) {
                trackList = list;
            } else {
                trackList.getTracks().addAll(list.getTracks());
            }
            pageId++;
            musicTrackRecyclerAdapter.updateList(trackList);
            hideTip();
            //            dismissDialog(DIALOG_LOADING);
            lLayoutContentArea.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(String errorMsg) {
            updateTip(false);

        }
    };

    /**
     * 搜索列表的item点击事件，并且跳转到播放界面
     *
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        XmPlayerManager.getInstance(MusicTrackActivity.this).playList(trackList, position);
        launchActivity(MusicPlayActivity.class);
    }

    @Override
    public void finish() {
        super.finish();
        if (isFromHome) {
            launchActivity(MusicOnlineActivity.class);
        }
        musicTrackPresenter.removeView();
        removeDialog(DIALOG_LOADING);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(wifiReceiver);
        musicHandler.removeMessages(MESSAGE_UPDATE_MUSIC);
        super.onDestroy();
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
                    musicHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_MUSIC, 1500);
                }
            }
        }
    };

    private Handler musicHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                // 网络状态改变时，更新音乐
                case MESSAGE_UPDATE_MUSIC:
                    if (null == trackList) {
                        musicTrackPresenter.getDataTrack(albumId, pageId);
                    }
                    break;
            }
            return false;
        }
    });

    /**
     * 更新提示
     *
     * @param hasNet
     */
    private void updateTip(boolean hasNet) {
        if (null != trackList) {
            return;
        }
        lLayoutContentArea.setVisibility(View.GONE);
        if (hasNet) {
            //显示加载框
            //            showDialog(DIALOG_LOADING);
        } else {
            if (!isNetworkAvailable(this)) {
                lLayoutTipArea.setVisibility(View.VISIBLE);
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(getString(R.string.str_network_unavailable_tip));
                btnConnectNetwork.setVisibility(View.VISIBLE);
                //关闭加载框
                //                dismissDialog(DIALOG_LOADING);
            } else {
                lLayoutTipArea.setVisibility(View.VISIBLE);
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(getString(R.string.str_network_disabled));
                btnConnectNetwork.setVisibility(View.GONE);
                btnRefreshNetwork.setVisibility(View.VISIBLE);
                //关闭加载框
                //                dismissDialog(DIALOG_LOADING);
            }
        }
    }

    /**
     * 隐藏提示
     */
    private void hideTip() {
        lLayoutTipArea.setVisibility(View.GONE);
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable()) {
            return true;
        }
        return false;
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
     * 获取专辑列表数据
     */
    private void getOnlineData() {
        //获取专辑列表
        musicTrackPresenter.getDataTrack(albumId, pageId);
        //显示加载框
        //        showDialog(DIALOG_LOADING);
    }

    @Override
    public boolean showBottom() {
        return true;
    }
}
