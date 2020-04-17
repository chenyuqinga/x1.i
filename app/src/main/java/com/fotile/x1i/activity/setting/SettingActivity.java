package com.fotile.x1i.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.ota.OtaMainActivity;
import com.fotile.x1i.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;


/**
 * @author： yaohx
 * @data： 2019/4/16 14:34
 * @company： 杭州方太智能科技有限公司
 * @description： 设置界面
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    /**
     * icon
     */
    @BindView(R.id.txt_setting_wifi)
    TextView txtWifi;
    @BindView(R.id.txt_setting_link)
    TextView txtLink;
    @BindView(R.id.txt_setting_delay)
    TextView txtDelay;
    @BindView(R.id.txt_setting_control)
    TextView txtControl;
    @BindView(R.id.txt_setting_time)
    TextView txtTime;
    @BindView(R.id.txt_setting_light)
    TextView txtLight;
    @BindView(R.id.txt_setting_voice)
    TextView txtVoice;
    @BindView(R.id.txt_setting_clear)
    TextView txtClear;
    @BindView(R.id.txt_setting_wait)
    TextView txtWait;
    @BindView(R.id.txt_setting_reset)
    TextView txtReset;
    @BindView(R.id.txt_setting_ota)
    TextView txtOta;
    @BindView(R.id.txt_setting_help)
    TextView txtHelp;
    @BindView(R.id.txt_setting_about)
    TextView txtAbout;
    @BindView(R.id.point_red)
    ImageView point_red;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //控制小红点显示
        boolean ota_available = (boolean) PreferenceUtil.getPreferenceValue(context,PreferenceUtil.OTA_AVAILABLE,
                false);
        if(ota_available){
            point_red.setVisibility(View.VISIBLE);
        }else {
            point_red.setVisibility(View.GONE);
        }
    }

    private void initView() {
        txtWifi.setOnClickListener(this);
        txtLink.setOnClickListener(this);
        txtDelay.setOnClickListener(this);
        txtControl.setOnClickListener(this);
        txtTime.setOnClickListener(this);
        txtLight.setOnClickListener(this);
        txtVoice.setOnClickListener(this);
        txtClear.setOnClickListener(this);
        txtWait.setOnClickListener(this);
        txtReset.setOnClickListener(this);
        txtOta.setOnClickListener(this);
        txtHelp.setOnClickListener(this);
        txtAbout.setOnClickListener(this);

    }

    private void initData() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //wifi
            case R.id.txt_setting_wifi:
                launchActivity(WifiListActivity.class);
                break;
            //设备联动
            case R.id.txt_setting_link:
                launchActivity(DeviceLinkageActivity.class);
                break;
            //延时关机
            case R.id.txt_setting_delay:
                launchActivity(DelayOffActivity.class);
                break;
            //远程控制
            case R.id.txt_setting_control:
                launchActivity(DeviceBindActivity.class);
                break;
            //时间设置
            case R.id.txt_setting_time:
                launchActivity(TimeSettingActivity.class);
                break;
            //亮度设置
            case R.id.txt_setting_light:
                launchActivity(LightActivity.class);
                break;
            //系统音量
            case R.id.txt_setting_voice:
                launchActivity(VolumeActivity.class);
                break;
            //清除缓存
            case R.id.txt_setting_clear:
                launchActivity(ClearCacheActivity.class);
                break;
            //待机换面
            case R.id.txt_setting_wait:
                launchActivity(AwaitActivity.class);
                break;
            //恢复出厂设置
            case R.id.txt_setting_reset:
                launchActivity(ResetFactoryActivity.class);
                break;
            //系统升级
            case R.id.txt_setting_ota:
                launchActivity(OtaMainActivity.class);
                break;
            //帮助
            case R.id.txt_setting_help:
                launchActivity(HelpActivity.class);
                break;
            //关于本机
            case R.id.txt_setting_about:
                launchActivity(AboutActivity.class);
                break;

            default:
                break;
        }
    }
}
