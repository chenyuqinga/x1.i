package com.fotile.x1i.activity.screensaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fotile.recipe.bean.screensaver.DigitalTime;
import com.fotile.recipe.bean.screensaver.Weather;
import com.fotile.x1i.R;
import com.fotile.x1i.mvp.view.DigitalClockView;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.util.DateUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称：DigitalClockActivity
 * 创建时间：2019/3/14 13:32
 * 文件作者：chenyqi
 * 功能描述：电子时钟待机画面
 */


public class DigitalClockActivity extends BaseClockActivity  {
    private static final String HTTP_PREFIX = "http://";
    private static final String TEMPERATURE_SYMBOL = "°";
    /**
     * 整个时钟布局
     */
    @BindView(R.id.rLayout_digital_clock)
    RelativeLayout rLayoutClock;
    /**
     * 时间
     */
    @BindView(R.id.tv_digital_time)
    TextView tvTime;
    /**
     * 时间标识:上午或下午
     */
    @BindView(R.id.tv_digital_time_symbol)
    TextView tvTimeSymbol;
    /**
     * 天气图标
     */
    @BindView(R.id.img_weather_icon)
    ImageView imgWeatherIcon;
    /**
     * 温度
     */
    @BindView(R.id.tv_weather_temperature)
    TextView tvTemperature;

    private DigitalClockPresenter clockPresenter;
    private CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initView();
        initData();

    }

    private void initView() {
        rLayoutClock.setOnClickListener(this);
        tvTemperature.setText(getResources().getString(R.string.idle_no_net_digital_clock));
        updateTime();
        registerTimeReceiver();
    }

    private void initData() {
        compositeSubscription = new CompositeSubscription();
        clockPresenter = new DigitalClockPresenter(this);
        clockPresenter.onCreate(compositeSubscription);
        clockPresenter.attachView(clockView);
        clockPresenter.getCityInfo(AppUtil.getIpAddress(this));
    }

    DigitalClockView clockView = new DigitalClockView() {

        @Override
        public void onCitySuccess(String city) {
            if (!TextUtils.isEmpty(city)) {
                clockPresenter.getWeatherInfo(city);
            }
        }

        @Override
        public void onWeatherSuccess(Weather weather) {
            if (null != weather) {
                imgWeatherIcon.setVisibility(View.VISIBLE);
                tvTemperature.setText(weather.getTemperature() + TEMPERATURE_SYMBOL);
                Glide.with(DigitalClockActivity.this).load(HTTP_PREFIX + weather.getIconUrl()).into(imgWeatherIcon);
            }
        }

        @Override
        public void onError() {
            tvTemperature.setText(getApplicationContext().getResources().getString(R.string.idle_no_net_digital_clock));
        }
    };

    /**
     * 注册时间变化的广播
     */
    private void registerTimeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeChangedReceiver, filter);
    }

    BroadcastReceiver timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTime();
        }
    };

    /**
     * 更新时间的显示
     */
    private void updateTime() {
        DigitalTime time = DateUtil.getDigitalTime();
        tvTime.setText(DateUtil.getCurrentTime());
        tvTimeSymbol.setText(time.getAmOrPm());
    }

    @Override
    protected void onDestroy() {
        if (null != compositeSubscription && compositeSubscription.hasSubscriptions()) {
            //解除订阅关系，释放网络资源
            compositeSubscription.unsubscribe();
        }
        unregisterReceiver(timeChangedReceiver);
        super.onDestroy();
    }

    public int getLayoutId() {
        return R.layout.activity_digital_clock;
    }

}
