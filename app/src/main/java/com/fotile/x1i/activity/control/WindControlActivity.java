package com.fotile.x1i.activity.control;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.widget.SeekBar;
import com.fotile.x1i.widget.WindSineView;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.DeviceControl;
import fotile.device.cookerprotocollib.helper.bean.WorkBean;
import rx.functions.Action1;

import static com.fotile.x1i.manager.DeviceReportManager.WORK_WIND_CONTROL;

/**
 * 文件名称：WindControlActivity
 * 创建时间：2019/5/15 19:20
 * 文件作者：yaohx
 * 功能描述：风量控制
 */
public class WindControlActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.seekbar)
    SeekBar seekbar;
    /**
     * 关闭按钮
     */
    @BindView(R.id.txt_close)
    TextView txtClose;

    @BindView(R.id.wind_sine)
    WindSineView sine;
    /**
     * 档位
     */
    private int speed_value;

    private static int[] default_seekbar_value = new int[]{1, 15, 25, 35, 45, 55, 65, 75, 85, 99};
    /**
     * F0-F9十个档位
     */
    public int[] winds = new int[]{0xF0, 0xF1, 0xF2, 0xF3, 0xF4, 0xF5, 0xF6, 0xF7, 0xF8, 0xF9};
    /**
     * 默认五档
     */
    public static final int DEFAULT_WIND_VALUE = 0xF5;
    /**
     * 设置控制指令下发时间
     */
    private long time_control = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setSpeedValue();
    }

    @Override
    public void createAction() {
        super.createAction();
        actionWorkBean = new Action1<WorkBean>() {
            @Override
            public void call(WorkBean workBean) {
                int work_state = DeviceReportManager.work_state;
                if (work_state == WORK_WIND_CONTROL) {
                    long current_time = System.currentTimeMillis();
                    //指令上报时间和指令下发时间差大于2秒，才去更新进度条
                    //指令由设备端下发其实不需要更新进度条，此处主要是更新手机app下发的指令
                    if(current_time - time_control> 2000){
                        setSpeedValue();
                    }
                }
                //非风量控制状态关闭页面
                else {
                    finish();
                }
            }
        };
    }

    private void initView() {
        //振幅压缩比例
        final float f = (float) 0.15;
        final int max_level = (int) (100 * f);
//        sine.updateSine(1, max_level);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(int progress) {
                sine.updateSine((int) (progress * f), max_level);
            }

            @Override
            public void onProgressStop(int progress) {
                //progress 0-99，更新档位
                speed_value = getWindSpeed(progress);
                //开启设备更换风机档位
                DeviceControl.getInstance().startWindControl(speed_value, false);
                time_control = System.currentTimeMillis();
            }
        });

        txtClose.setOnClickListener(this);
    }

    /**
     * 根据当前的进度值计算对应的档位
     *
     * @param progress
     * @return
     */
    private int getWindSpeed(int progress) {
        //progress 0-99
        int index = progress / 10 + 1;
        if (index < 1) {
            index = 1;
        }
        if (index > 10) {
            index = 10;
        }
        return winds[index - 1];
    }

    /**
     * 根据风量档位F0-F9，获取对应的默认progress
     *
     * @param wind F0-F9
     * @return
     */
    private int getWindProgress(int wind) {
        int progress = 0;
        int index = -1;
        for (int k = 0; k < winds.length; k++) {
            if (winds[k] == wind) {
                index = k;
                break;
            }
        }
        if (index >= 0 && index <= default_seekbar_value.length - 1) {
            progress = default_seekbar_value[index];
        }
        return progress;
    }

    /**
     * 设置风量档位对应的progress
     */
    private void setSpeedValue() {
        //读取保存的档位 F0-F9
        speed_value = PreferenceUtil.getWindSpeed(context);
        // 0-99
        //根据当前的进度值计算对应的档位
        int value = getWindSpeed(seekbar.getProgress());
        //如果结果相同，说明没有更新档位
        if (speed_value == value) {

        }
        //如果结果不相同，说明档位变化来自手机app
        else {
            //获取档位对应的progress
            int progres = getWindProgress(speed_value);
            seekbar.setProgress(progres);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_control_wind;
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //关闭
            case R.id.txt_close:
                DeviceControl.getInstance().closeDeviceOutLamp(true);
                finish();
                break;
        }
    }
}
