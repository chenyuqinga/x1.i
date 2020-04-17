package com.fotile.x1i.activity.setting;

import android.os.Bundle;
import android.provider.Settings;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.bean.event.UpdateInfo;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.util.Constant;
import com.fotile.x1i.widget.SeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * 文件名称：LightActivity
 * 创建时间：2019/5/15 19:48
 * 文件作者：yaohx
 * 功能描述：调节系统亮度
 */
public class LightActivity extends BaseActivity {

    @BindView(R.id.seekbar)
    SeekBar seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        seekbar.setMax(255);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(int progress) {
                AppUtil.setSysLight(context, progress);
                //保存更新后的亮度
                PreferenceUtil.setPreferenceValue(context, PreferenceUtil.BRIGHT_LAST, progress);
            }

            @Override
            public void onProgressStop(int progress) {

            }
        });

        //根据亮度显示进度条长度
        try {
            int currentBright = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            seekbar.setProgress(currentBright);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_light;
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Subscribe
    public void onEventUpdateBrightness(UpdateInfo updateInfo) {
        if (updateInfo != null && updateInfo.getType() == Constant.TYPE_BRIGHTNESS) {
            updateBrightness();
        }
    }

    private void updateBrightness() {
        try {
            int currentBright = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);

            seekbar.setProgress(currentBright);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
