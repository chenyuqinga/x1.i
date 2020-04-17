package com.fotile.x1i.activity.control;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fotile.common.util.ImageFrameHandlerUtil;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.widget.SeekBar;
import com.ta.utdid2.android.utils.SystemUtils;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.DeviceControl;
import fotile.device.cookerprotocollib.helper.bean.WorkBean;
import rx.functions.Action1;

import static com.fotile.common.util.PreferenceUtil.LAST_LAMP_VALUE;
import static fotile.device.cookerprotocollib.helper.DeviceControl.LAMP_MAX;
import static fotile.device.cookerprotocollib.helper.DeviceControl.LAMP_MIN;

/**
 * 文件名称：LampActivity
 * 创建时间：2019/5/15 19:20
 * 文件作者：yaohx
 * 功能描述：灯
 */
public class LampActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.seekbar)
    SeekBar seekbar;
    /**
     * 关闭按钮
     */
    @BindView(R.id.txt_close)
    TextView txtClose;

    /**
     * 灯带背景
     */
    @BindView(R.id.img_lamp)
    ImageView img_lamp;
    /**
     * 灯带动效
     */
    @BindView(R.id.anim_lamp)
    ImageView anim_lamp;

    ImageFrameHandlerUtil imageFrameHandlerUtil;
    /**
     * 设备控制指令下发时间
     */
    private long time_control = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAnim();
        //设置默认的进度
        int lamp = PreferenceUtil.getLamp(context);
        if(lamp==0){
            seekbar.setPercent(0.5f);
        }else{
        //设置默认打开的灯的进度
        setLampProgress();}
        //每次进来默认进度条居中
//        seekbar.setPercent(0.5f);
        DeviceControl.getInstance().setLamp(lamp, false);
    }

    /**
     * 设置等待动画透明度
     */
    private void setTran() {
        float tran = seekbar.getPercent();
        int temp = (int) (tran * 255);
        if (temp <= 10) {
            temp = 10;
        }
        img_lamp.setImageAlpha(temp);
    }

    private void initView() {
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(int progress) {
                setTran();
            }

            @Override
            public void onProgressStop(int progress) {
                //根据滑动条进度计算对应的lamp值
                float tran = seekbar.getPercent();
                int lamp = (int) (tran * (LAMP_MAX - DeviceControl.LAMP_MIN)) + DeviceControl.LAMP_MIN;
                DeviceControl.getInstance().setLamp(lamp, true);
                PreferenceUtil.setPreferenceValue(context, LAST_LAMP_VALUE, lamp);
                time_control = System.currentTimeMillis();

            }
        });
        txtClose.setOnClickListener(this);
    }

    private void initAnim() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageFrameHandlerUtil = new ImageFrameHandlerUtil(context, anim_lamp, getRes(), 40);
                imageFrameHandlerUtil.start();
            }
        }, 500);
    }

    @Override
    public void createAction() {
        super.createAction();
        actionWorkBean = new Action1<WorkBean>() {
            @Override
            public void call(WorkBean workBean) {
                //关灯
                if (0 == workBean.lamp) {
                    finish();
                } else {
                    long current_time = System.currentTimeMillis();
                    if (current_time - time_control > 2000) {
                        setLampProgress();
                    }
                }
            }
        };
    }

    /**
     * 根据lamp值计算滑动条进度
     */
    private void setLampProgress() {
        //设置默认的进度
        int lamp = PreferenceUtil.getLamp(context);
        float p = Tool.divide(lamp - LAMP_MIN, LAMP_MAX - LAMP_MIN);

        //当前进度和lamp值计算得出的进度误差
        float dp = Math.abs(seekbar.getPercent() - p);
        if (dp > 0.05) {
            seekbar.setPercent(p);
        }
        //如果误差不超过0.05 （5%），判断为设备端自己操控，不执行动作
        else {

        }
        //        int lamp_progress = (int) (p * seekbar.getMax());
    }

    private int[] getRes() {
        int length = 75;
        int[] res = new int[length];
        for (int i = 0; i < length; i++) {
            int res_id;
            res_id = AppUtil.getResourceById("lamp_anim_" + i);
            res[i] = res_id;
        }
        return res;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_control_lamp;
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        if (null != imageFrameHandlerUtil) {
            imageFrameHandlerUtil.stop();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //关闭照明灯
            case R.id.txt_close:
                DeviceControl.getInstance().setLamp(DeviceControl.LAMP_CLOSE, false);
                finish();
                break;
        }
    }
}
