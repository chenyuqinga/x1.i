package com.fotile.x1i.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.recipe.bean.screensaver.Weather;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.MainActivity;
import com.fotile.x1i.manager.TopBarStatusCallBack;
import com.fotile.x1i.mvp.presenter.TopStateWeatherPresenter;
import com.fotile.x1i.mvp.view.TopStateWeatherView;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.util.DateUtil;

import rx.subscriptions.CompositeSubscription;

/**
 * @author chenyjg
 * @date 2019/4/23
 * @company 杭州方太智能科技有限公司
 * @description 顶部状态栏top bar
 * <p>
 * Copyright (c) 2019, FOTILE GROUP.
 * All rights reserved.
 */
public class TopBar extends LinearLayout implements TopBarStatusCallBack {
    private static final String HTTP_PREFIX = "http://";
    private static final String TEMPERATURE_SYMBOL = "°";

    private Context mContext;

    private LinearLayout layoutLeft;
    private LinearLayout layoutRight;
    /**
     * 天气
     */
    private ImageView imgWeather;
    private TextView tvWeather;
    /**
     * 故障
     */
    private ImageView imgFault;
    /**
     * 绑定
     */
    private ImageView imgUnbind;
    /**
     * 设备联动
     */
    private ImageView imgLink;
    /**
     * 麦克风
     */
    private ImageView imgSpeech;
    /**
     * 音量
     */
    private ImageView imgSilence;
    /**
     * Wifi
     */
    private ImageView imgWifi;

    private TextView tvCurrentTime;

    /**
     * 系统音频管理器
     */
    private AudioManager audioManager;

    private TopStateWeatherPresenter topStateWeatherPresenter;
    private CompositeSubscription compositeSubscription;

    public TopBar(@NonNull Context context) {
        super(context);
    }

    private static TopBar ourInstance;

    public static TopBar getInstance(Context context) {
        if (ourInstance == null) {
            synchronized (TopBar.class) {
                ourInstance = new TopBar(context);
            }
        }
        return ourInstance;
    }

    public void initContext(Context context) {
        this.mContext = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        inflater.inflate(R.layout.layout_top_bar, this, true);

        layoutLeft = (LinearLayout) findViewById(R.id.layout_top_left);
        layoutRight = (LinearLayout) findViewById(R.id.layout_top_right);

        imgWeather = (ImageView) findViewById(R.id.iv_weather);
        tvWeather = (TextView) findViewById(R.id.tv_temp);
        imgFault = (ImageView) findViewById(R.id.iv_fault);
        imgUnbind = (ImageView) findViewById(R.id.iv_bind);
        imgLink = (ImageView) findViewById(R.id.iv_linkage);
        imgSpeech = (ImageView) findViewById(R.id.img_top_speech);
        imgSilence = (ImageView) findViewById(R.id.img_top_silence);
        imgWifi = (ImageView) findViewById(R.id.iv_wifi);
        tvCurrentTime = (TextView) findViewById(R.id.tc_clock);

        //显示系统时间
        updateTime();
        // 更新天气数据
        updateWeather();
        //显示静音状态
        // 设备绑定
        boolean bind_state = (boolean) PreferenceUtil.getPreferenceValue(mContext, PreferenceUtil.DEVICE_BIND_STATE,
                false);
        updateBindState(bind_state);

        registerWifiReceiver();
    }

    /**
     * 注册网络变化
     */
    private void registerWifiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mContext.registerReceiver(wifiReceiver, intentFilter);
    }

    /**
     * 接收网络变化的广播
     */
    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ((WifiManager.NETWORK_STATE_CHANGED_ACTION).equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    NetworkInfo.State state = info.getState();
                    if (state == NetworkInfo.State.CONNECTED) {
                        updateWeather();
                    } else if (state == NetworkInfo.State.DISCONNECTED) {
                        tvWeather.setText(mContext.getResources().getString(R.string.idle_no_net_digital_clock));
                        imgWeather.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

    /**
     * 更新系统时间
     */
    @Override
    public void updateTime() {
        if (null != tvCurrentTime) {
            String time = DateUtil.getCurrentTime();
            tvCurrentTime.setText(time);
        }
    }


    @Override
    public void updateWeather() {
        compositeSubscription = new CompositeSubscription();
        topStateWeatherPresenter = new TopStateWeatherPresenter(mContext);
        topStateWeatherPresenter.onCreate(compositeSubscription);
        topStateWeatherPresenter.attachView(topStateWeatherView);
        //网络畅通时获取，防止anr
        if(Tool.isNetworkAvailable(mContext)){
            topStateWeatherPresenter.getCityInfo(AppUtil.getIpAddress(mContext));
        }
    }

    TopStateWeatherView topStateWeatherView = new TopStateWeatherView() {
        @Override
        public void onCitySuccess(String city) {
            LogUtil.LOGE("---天气--城市", city);
            if (!TextUtils.isEmpty(city)) {
                topStateWeatherPresenter.getWeatherInfo(city);
            }
        }

        @Override
        public void onWeatherSuccess(Weather weather) {
            LogUtil.LOGE("---天气--温度", weather.getTemperature());
            LogUtil.LOGE("---天气--Icon", weather.getIconUrl());
            if (null != weather) {
                imgWeather.setVisibility(View.VISIBLE);
                tvWeather.setText(weather.getWeather() + "     " + weather.getTemperature() + TEMPERATURE_SYMBOL + "C");
                Glide.with(mContext).load(HTTP_PREFIX + weather.getIconUrl()).into(imgWeather);
            }
        }

        @Override
        public void onError() {
            imgWeather.setVisibility(View.GONE);
            tvWeather.setText(mContext.getResources().getString(R.string.idle_no_net_digital_clock));
            LogUtil.LOGE("---天气--错误", "error");
        }
    };

    @Override
    public void updateBindState(boolean isBind) {
        if (mContext == null) {
            return;
        }
        if (isBind) {
            imgUnbind.setVisibility(View.GONE);
        } else {
            imgUnbind.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void updateLinkState(boolean isLinked) {
        if (isLinked) {
            imgLink.setVisibility(View.GONE);
        } else {
            imgLink.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateFaultState(boolean isVisible) {
        if (null != imgFault) {
            imgFault.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void updateWifiState(boolean connected, int level) {
        if (null != imgWifi) {
            LogUtil.LOGE("==================updateWifiIcon", connected + "-" + level);

            int res_id = AppUtil.getResourceById("wifi_" + level + "_default");
            if (res_id > 0) {
                imgWifi.setImageResource(res_id);
            }
            if (connected) {
                imgWifi.setVisibility(View.VISIBLE);
            } else {
                imgWifi.setVisibility(View.GONE);
            }
        }
    }

    /**
     * wifi图标是否显示
     */
    public void IsVisibleWifiIcon(boolean isVisible) {
        if (null != imgWifi) {
            LogUtil.LOGE("==================IsVisibleWifiIcon", isVisible);
            int vis = imgWifi.getVisibility();
            if (vis == View.VISIBLE) {
                imgWifi.setVisibility(View.GONE);
            } else {
                imgWifi.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 系统是否静音
     */
    public void setVolmeState() {
        //静音是否显示
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (volume == 0) {
            setImgSilence(true);
        } else {
            setImgSilence(false);
        }
    }

    /**
     * 是否显示静音图像
     */
    public void setImgSilence(boolean isVisible) {
        if (isVisible) {
            LogUtil.LOGE("DockState-silence", isVisible);
            imgSilence.setVisibility(View.VISIBLE);
        } else {
            imgSilence.setVisibility(View.GONE);
        }
    }

    /**
     * top_bar show
     */
    public void show() {
        setVisibility(View.VISIBLE);
    }

    /**
     * top_bar hide
     */
    public void hide() {
        setVisibility(View.GONE);
    }
}
