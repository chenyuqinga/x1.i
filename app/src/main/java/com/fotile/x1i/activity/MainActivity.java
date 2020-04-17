package com.fotile.x1i.activity;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.music.listener.MusicPlayerStateListener;
import com.fotile.music.manager.MusicManager;
import com.fotile.music.model.CommanConst;
import com.fotile.voice.CommonConst;
import com.fotile.voice.bean.BoxLinkState;
import com.fotile.voice.socket.SocketUtil;
import com.fotile.voice.wifi.WifiAPManager;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.music.DuiMusicPlayActivity;
import com.fotile.x1i.activity.music.MusicOnlineActivity;
import com.fotile.x1i.activity.music.MusicPlayActivity;
import com.fotile.x1i.activity.quicktool.QuickToolActivity;
import com.fotile.x1i.activity.recipe.RecipesCategoryActivity;
import com.fotile.x1i.activity.setting.SettingActivity;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.dailog.RuntimeDialog;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.manager.SpeechManager;
import com.fotile.x1i.server.TimerServer;
import com.fotile.x1i.server.screensaver.ScreenSaverService;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.util.Constant;
import com.fotile.x1i.widget.BottomView;
import com.fotile.x1i.widget.ProgressCircle;
import com.fotile.x1i.widget.SpeechFloatingLayer;
import com.fotile.x1i.widget.TopBar;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fotile.device.cookerprotocollib.helper.bean.DeviceMessage;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.functions.Action1;

import static com.fotile.common.util.PreferenceUtil.OTA_AVAILABLE;

/**
 * @author： yaohx
 * @data： 2019/4/15 16:21
 * @company： 杭州方太智能科技有限公司
 * @description： MainActivity
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    /**
     * 厨房菜谱
     */
    RelativeLayout layoutMainRecipe;
    /**
     * 在线音乐
     */
    RelativeLayout layoutMainMusic;
    /**
     * 无音乐播放时的音乐栏
     */
    private RelativeLayout mMusicNormalRl;
    /**
     * 音乐控制
     */
    private RelativeLayout mMusicControlRl;
    /**
     * 音乐图片
     */
    private ImageView mMusicImgIv;
    /**
     * 下一首
     */
    private ImageView mMusicNextIv;
    /**
     * 上一首
     */
    private ImageView mMusicPreIv;
    /**
     * 状态
     */
    private ImageView mMusicStateIv;
    /**
     * 音乐图片
     */
    private TextView mMusicNameTv;
    /**
     * 播放界面图片旋转动画
     */
    private Animation animationImage;
    /**
     * 在线影音
     */
    RelativeLayout layoutMainVideo;
    /**
     * 快捷工具
     */
    RelativeLayout layoutMainQuick;
    RelativeLayout layoutMainTick;
    ProgressCircle progressCircle;
    ImageView mainQuickRed;
    /**
     * ota可更新小红点
     */
    @BindView(R.id.point_red)
    ImageView point_red;

    /**
     * 系统设置
     */
    RelativeLayout layoutMainSetting;
    /**
     * 倒计时服务
     */
    TimerServer timerServer;
    /**
     * 喜马拉雅控件
     */
    private XmPlayerManager xmPlayerManager;
    /**
     * 喜马拉雅
     */
    private CommonRequest xmCommonRequest;
    /**
     * 更新topbar时间
     */
    final int WHAT_UPDATE_TIME = 1001;
    /**
     * 获取网络时间
     */
    final int WHAT_OBTAIN_NET_TIME = 1002;
    /**
     * 设备上报消息的对象
     */
    public Action1<DeviceMessage> actionDeviceMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initAnimation();
        initXm();
        initDuiMusic();
        initData();
        initSpeech();
        registerTimeAndWifiReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MusicManager.getInstance().connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRedShow();
        //风机2000小时保养提示
        int run_time = (int) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.RUN_TIME, -1);
        if (run_time > 2000) {
            if (PreferenceUtil.getRuntimeTip(context) == PreferenceUtil.RUN_CLEAN_TIP_NORMAL) {
                RuntimeDialog dialog = new RuntimeDialog(context);
                dialog.show();
            }
        }
        //控制小红点显示
        boolean ota_available = (boolean) PreferenceUtil.getPreferenceValue(context, OTA_AVAILABLE, false);
        if (ota_available) {
            point_red.setVisibility(View.VISIBLE);
        } else {
            point_red.setVisibility(View.GONE);
        }
    }

    @Override
    public void createAction() {
        actionDeviceMessage = new Action1<DeviceMessage>() {
            @Override
            public void call(DeviceMessage deviceMessage) {
                if (deviceMessage == null) {
                    return;
                }
                updateRedShow();
            }
        };
    }

    public void updateRedShow() {
        if (PreferenceUtil.hasUnreadMemorandum(this) || PreferenceUtil.hasUnreadNotification(this)) {
            mainQuickRed.setVisibility(View.VISIBLE);
        } else {
            mainQuickRed.setVisibility(View.GONE);
        }
    }

    private void initView() {
        layoutMainRecipe = (RelativeLayout) findViewById(R.id.layout_main_recipe);
        layoutMainMusic = (RelativeLayout) findViewById(R.id.layout_main_music);
        layoutMainVideo = (RelativeLayout) findViewById(R.id.layout_main_video);
        layoutMainQuick = (RelativeLayout) findViewById(R.id.layout_main_quick);
        mainQuickRed = (ImageView) findViewById(R.id.main_quick_red);
        layoutMainSetting = (RelativeLayout) findViewById(R.id.layout_main_setting);
        layoutMainTick = (RelativeLayout) findViewById(R.id.layout_main_tick);
        progressCircle = (ProgressCircle) findViewById(R.id.progress_circle);
        mMusicNormalRl = (RelativeLayout) findViewById(R.id.rl_main_music_normal);
        mMusicControlRl = (RelativeLayout) findViewById(R.id.rl_main_music_control);
        mMusicImgIv = (ImageView) findViewById(R.id.iv_main_music_img);
        mMusicNameTv = (TextView) findViewById(R.id.tv_music_name);
        mMusicPreIv = (ImageView) findViewById(R.id.iv_main_music_pre);
        mMusicNextIv = (ImageView) findViewById(R.id.iv_main_music_next);
        mMusicStateIv = (ImageView) findViewById(R.id.iv_main_music_state);

        layoutMainRecipe.setOnClickListener(this);
        layoutMainMusic.setOnClickListener(this);
        layoutMainVideo.setOnClickListener(this);
        layoutMainQuick.setOnClickListener(this);
        layoutMainTick.setOnClickListener(this);
        layoutMainSetting.setOnClickListener(this);
        mMusicPreIv.setOnClickListener(this);
        mMusicNextIv.setOnClickListener(this);
        mMusicStateIv.setOnClickListener(this);
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        animationImage = AnimationUtils.loadAnimation(context, R.anim.music_img_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();
        animationImage.setInterpolator(interpolator);
    }


    private void initData() {
        //防止断电重启不会进入引导页，只有进入主页后才更新该字段
        PreferenceUtil.setGuide(context, false);
        //设置非首次进入主页
        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.FIRST_ENTER_APP, false);

        //底栏和顶栏（进入首页后才显示底栏顶栏）
        BottomView.getInstance(this).initContext(this);
        TopBar.getInstance(this).initContext(getApplicationContext());

        //启动屏保服务 （放到这里初始化是为了避免在开机引导时执行了屏保逻辑）
        Intent intent = new Intent(this, ScreenSaverService.class);
        startService(intent);
        //        intent = new Intent(this, ScreenService.class);
        //        startService(intent);
        //倒计时服务
        intent = new Intent(context, TimerServer.class);
        context.getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        if (null != actionDeviceMessage) {
            DeviceReportManager.getInstance().addMessageAction(actionDeviceMessage);
        }
    }

    //更新主页定时UI
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerServer.MyBinder myBinder = (TimerServer.MyBinder) service;
            timerServer = myBinder.getServer();
            //设置回调
            timerServer.addOnTimerTickListener(onTimerTickListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //定时服务器回调函数
    TimerServer.OnTimerTickListener onTimerTickListener = new TimerServer.OnTimerTickListener() {
        //定时中
        @Override
        public void onTicking(long left_time, long all_time) {
            layoutMainQuick.setVisibility(View.GONE);
            layoutMainTick.setVisibility(View.VISIBLE);

            int minute = timerServer.getAllTickTimeMinute();
            int maxValue = minute * 60 * 1000;
            int preValue = (int) (maxValue - timerServer.getLeftTickTime());
            progressCircle.setDuration2(preValue, maxValue, minute + "分", null);
        }

        //定时结束
        @Override
        public void onTickFinish() {
            layoutMainQuick.setVisibility(View.VISIBLE);
            layoutMainTick.setVisibility(View.GONE);
        }

        @Override
        public void onTickCanceled() {
            layoutMainQuick.setVisibility(View.VISIBLE);
            layoutMainTick.setVisibility(View.GONE);
        }
    };

    /**
     * 注册时间和网络变化
     */
    private void registerTimeAndWifiReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //filter.addAction(Constant.ACTION_VIDEO_STATUS_CHANGED);
        registerReceiver(timeAndWifiReceiver, filter);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //更新时间
                case WHAT_UPDATE_TIME:
                    TopBar.getInstance(context).updateTime();
                    break;
                //去获取网络时间
                case WHAT_OBTAIN_NET_TIME:
                    new Thread(netTimeRunnable).start();
                    break;
            }
            return false;
        }
    });

    //获取网络时间
    Runnable netTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if(Tool.isNetworkAvailable(context)){
                long time = Tool.getNetTime(context);
                if (time > 0) {
                    //此处需要系统权限
                    try {
                        if (time / 1000 < Integer.MAX_VALUE) {
                            ((AlarmManager) context.getSystemService(context.ALARM_SERVICE)).setTime(time);
                        }
                        //通知topbar更新时间
                        handler.sendEmptyMessage(WHAT_UPDATE_TIME);
                    } catch (SecurityException e) {

                    }
                }
            }
        }
    };

    /**
     * 接收时间变化和网络变化的广播
     */
    private BroadcastReceiver timeAndWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_TIME_TICK.equals(action) || Intent.ACTION_TIME_CHANGED.equals(action)) {
                TopBar.getInstance(MainActivity.this).updateTime();
            }
            //网络变化
            else if ((WifiManager.NETWORK_STATE_CHANGED_ACTION).equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != info) {
                    NetworkInfo.State state = info.getState();
                    WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    if (state == NetworkInfo.State.CONNECTED) {
                        if (null != wifiInfo && null != wifiInfo.getSSID() && wifiInfo.getSSID().startsWith("\"")) {
                            PreferenceUtil.setPreferenceValue(context, PreferenceUtil.LAST_CONNECTED_WIFI_NAME,
                                    wifiInfo.getSSID().replace("\"", ""));
                        }
                        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4);

                        TopBar.getInstance(MainActivity.this).updateWifiState(true, level);

                        //网络变化时去网络更新时间
                        handler.sendEmptyMessageDelayed(WHAT_OBTAIN_NET_TIME, 200);
                    } else if (state == NetworkInfo.State.DISCONNECTED) {

                        TopBar.getInstance(MainActivity.this).updateWifiState(false, -100);

                    }
                }
            } else if ((Intent.ACTION_DATE_CHANGED).equals(action)) {
                AppUtil.deleteOverdayMessage(context);
            }

        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //厨房菜谱
            case R.id.layout_main_recipe:
                launchActivity(RecipesCategoryActivity.class);
                break;
            //厨房电台
            case R.id.layout_main_music:
                if (mMusicControlRl.isShown()) {
                    startMusicPlayActivity();
                } else {
                    launchActivity(MusicOnlineActivity.class);
                }
                break;
            //影视
            case R.id.layout_main_video:
                //                launchActivity(AqyActivity.class);
                AppUtil.setVideoState(1, this);
                TopBar.getInstance(context).hide();
                break;
            //快捷工具
            case R.id.layout_main_tick:
            case R.id.layout_main_quick:
                launchActivity(QuickToolActivity.class);
                break;
            //设置
            case R.id.layout_main_setting:
                launchActivity(SettingActivity.class);
                break;
            case R.id.iv_main_music_pre:
                if (Tool.fastclick()) {
                    if (MusicManager.getInstance().isDuiMode()) {
                        MusicManager.getInstance().skipToNext();
                    } else {
                        LogUtil.LOGE(TAG, "pre");
                        xmPlayerManager.playPre();
                    }
                }
                break;
            case R.id.iv_main_music_next:
                if (Tool.fastclick()) {
                    if (MusicManager.getInstance().isDuiMode()) {
                        MusicManager.getInstance().skipToNext();
                    } else {
                        LogUtil.LOGE(TAG, "next");
                        xmPlayerManager.playNext();
                    }
                }
                break;
            case R.id.iv_main_music_state:
                if (Tool.fastclick()) {
                    if (MusicManager.getInstance().isDuiMode()) {
                        if (MusicManager.getInstance().isPlaying()) {
                            MusicManager.getInstance().pause();
                        } else {
                            MusicManager.getInstance().play();
                        }
                    } else {
                        //喜马拉雅
                        if (xmPlayerManager.isPlaying()) {//音乐在播放
                            LogUtil.LOGE(TAG, "is playing");
                            xmPlayerManager.pause();
                            mMusicStateIv.setImageResource(R.mipmap.btn_music_play);
                            mMusicImgIv.clearAnimation();
                        } else {//音乐没在播放
                            LogUtil.LOGE(TAG, "isNotPlaying");
                            xmPlayerManager.play();
                            mMusicStateIv.setImageResource(R.mipmap.img_btn_pause);
                            mMusicImgIv.startAnimation(animationImage);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != timerServer) {
            timerServer.removeTimerTickListener(onTimerTickListener);
        }
        MusicManager.getInstance().removeAudioStateListener(mAudioStatusChangeListener);
        MusicManager.getInstance().disconnect();
        SpeechManager.getInstance().releaseResource();
    }

    private void initSpeech() {
        SpeechFloatingLayer.get(this).init(this);
        SpeechManager.getInstance().registerApplication(this);
        SocketUtil.getInstance(this).updateContext(this);
        WifiAPManager.getInstance(this).updateContext(this);
        EventBus.getDefault().post(CommonConst.SPEECH_READY);
        if (SpeechManager.getInstance().isConnected()) {
            //            DiffuseView.getInstance(this).show();
            EventBus.getDefault().post(new BoxLinkState(true));
        }
    }

    /**
     * 初始化喜马拉雅
     */
    private void initXm() {
        xmCommonRequest = CommonRequest.getInstanse();
        //喜马拉雅接口签名
        CommonRequest.getInstanse().init(getApplication(), Constant.appSecret);
        xmPlayerManager = XmPlayerManager.getInstance(MainActivity.this);
        xmPlayerManager.init();
        xmPlayerManager.setBreakpointResume(false);
        updateButtonStatus();
        xmPlayerManager.addPlayerStatusListener(playerStatusListener);
        xmPlayerManager.addOnConnectedListerner(new XmPlayerManager.IConnectListener() {
            @Override
            public void onConnected() {
                xmPlayerManager.removeOnConnectedListerner(this);
                xmCommonRequest.setDefaultPagesize(100);
                xmPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
                Log.e(TAG, "播放器初始化成功");
            }
        });

    }


    /**
     * 初始化dui音乐
     */
    private void initDuiMusic() {
        MusicManager.getInstance().addOnAudioStatusListener(mAudioStatusChangeListener);
        Log.e("MusicManager", "add music listener in main activity");
        MusicManager.getInstance().init(this, null, false);
    }

    private MusicManager.OnAudioStatusChangeListener mAudioStatusChangeListener = new MusicManager
            .OnAudioStatusChangeListener() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            Log.e("main music state change", "state: " + state);
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            Log.e("main music state change", "metadata: " + metadata);
            updateMusicTitle(metadata);
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            Log.e("main music state change", "queue: " + queue);

        }

        @Override
        public void onProgressInit(PlaybackStateCompat state) {

        }
    };

    private void updatePlaybackState(PlaybackStateCompat state) {
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mMusicStateIv.setImageResource(R.mipmap.img_btn_pause);
                mMusicImgIv.startAnimation(animationImage);
                setMusicVisible(true);
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                LogUtil.LOGE(TAG, "onPlayPause");
                mMusicImgIv.clearAnimation();
                mMusicStateIv.setImageResource(R.mipmap.btn_music_play);
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                Log.e(TAG, "on play stop");
                setMusicVisible(false);
                mMusicStateIv.setImageResource(R.mipmap.btn_music_play);
                mMusicImgIv.clearAnimation();
                break;
            default:
                Log.d(TAG, "Unhandled state " + state.getState());
                break;
        }
        updateButtonStatus();
        //        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) == 0) {
        //            imgBtnHomeMusicPrevious.setEnabled(false);
        //            imgBtnHomeMusicPrevious.setImageResource(R.mipmap.btn_music_pre_grey);
        //        } else {
        //            imgBtnHomeMusicPrevious.setEnabled(true);
        //            imgBtnHomeMusicPrevious.setImageResource(R.mipmap.img_btn_previous);
        //        }
    }

    private void updateMusicTitle(MediaMetadataCompat metadata) {
        mMusicNameTv.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
    }

    private void updateButtonStatus() {
        mMusicPreIv.setEnabled(MusicManager.getInstance().isDuiMode() || xmPlayerManager.hasPreSound());
        mMusicNextIv.setEnabled(MusicManager.getInstance().isDuiMode() || xmPlayerManager.hasNextSound());
    }

    /**
     * 喜马拉雅内部类
     */
    private IXmPlayerStatusListener playerStatusListener = new MusicPlayerStateListener() {
        /**
         *切歌
         * @param laModel 上一首model,可能为空
         * @param curModel 下一首model
         */
        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            LogUtil.LOGE(TAG, "SoundSwitch");
            String title = null;
            String coverUrl = null;
            PlayableModel model = xmPlayerManager.getCurrSound();
            if (model != null) {
                if (model instanceof Track) {
                    Track info = (Track) model;
                    title = info.getTrackTitle();
                    coverUrl = info.getCoverUrlLarge();
                }
                mMusicNameTv.setText(title);
                Glide.with(context).load(coverUrl).
                        bitmapTransform(new CropCircleTransformation(context)).
                        animate(animationImage).into(mMusicImgIv);
            }
            updateButtonStatus();
            setMusicVisible(true);
        }

        /**
         * 播放进度回调
         * @param currPos
         * @param duration
         */
        @Override
        public void onPlayProgress(int currPos, int duration) {
        }

        //播放停止
        @Override
        public void onPlayStop() {
            Log.e(TAG, "on play stop");
            setMusicVisible(false);
            mMusicStateIv.setImageResource(R.mipmap.btn_music_play);
            mMusicImgIv.clearAnimation();
        }

        //播放开始
        @Override
        public void onPlayStart() {
            mMusicStateIv.setImageResource(R.mipmap.img_btn_pause);
            mMusicImgIv.startAnimation(animationImage);
            setMusicVisible(true);
        }

        //播放暂停
        @Override
        public void onPlayPause() {
            LogUtil.LOGE(TAG, "onPlayPause");
            mMusicImgIv.clearAnimation();
            mMusicStateIv.setImageResource(R.mipmap.btn_music_play);
        }

        /**
         * 播放完成
         */
        @Override
        public void onSoundPlayComplete() {
            mMusicImgIv.clearAnimation();
            mMusicStateIv.setImageResource(R.mipmap.btn_music_play);
        }

        @Override
        public boolean onError(XmPlayerException e) {
            Log.e(TAG, "onError: " + e.toString());
            if (AppUtil.getCurrentActivityName(getApplicationContext()).contains("MainActivity")) {
                mMusicImgIv.clearAnimation();
                mMusicStateIv.setImageResource(R.mipmap.btn_music_play);
            } else {
                setMusicVisible(false);
            }
            return super.onError(e);
        }
    };

    private void setMusicVisible(boolean isVisible) {
        mMusicNormalRl.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        mMusicControlRl.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 启动音乐播放界面
     */
    private void startMusicPlayActivity() {
        if (MusicManager.getInstance().isDuiMode()) {
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(MainActivity.this);
            MediaMetadataCompat metadata = controller.getMetadata();
            Intent intent = new Intent(MainActivity.this, DuiMusicPlayActivity.class);
            intent.putExtra(CommanConst.EXTRA_CURRENT_MEDIA_DESCRIPTION, metadata);
            launchActivity(intent);
        } else {
            Intent intent = new Intent(this, MusicPlayActivity.class);
            intent.putExtra("isFromHome", true);
            startActivity(intent);
        }
    }
}
