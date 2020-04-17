package com.fotile.x1i.activity.control;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.widget.WaveSmartView;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.DeviceControl;
import fotile.device.cookerprotocollib.helper.bean.WorkBean;
import rx.functions.Action1;

import static com.fotile.x1i.manager.DeviceReportManager.WORK_AUTO;
import static com.fotile.x1i.manager.DeviceReportManager.WORK_FORCEWORK;

/**
 * 文件名称：CruiseActivity
 * 创建时间：2019/5/16 13:28
 * 文件作者：yaohx
 * 功能描述：智能巡航
 */
public class CruiseActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 关闭按钮
     */
    @BindView(R.id.txt_close)
    TextView txtClose;

    @BindView(R.id.wavesmart_icc)
    WaveSmartView waveSmartView;

    @BindView(R.id.txt_pressure)
    TextView txtPressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void createAction() {
        super.createAction();
        actionWorkBean = new Action1<WorkBean>() {
            @Override
            public void call(WorkBean workBean) {
                int work_state = DeviceReportManager.work_state;
                //自动档或者强制跑
                if (workBean != null) {
                    if (work_state == WORK_FORCEWORK) {
                        txtPressure.setText("强制跑 运行中");
                        return;
                    } else if (work_state == WORK_AUTO) {
                        setViewValue(workBean);
                    }
                    //非自动档状态关闭页面
                    else {
                        finish();
                    }

                }
            }
        };
    }

    private void setViewValue(WorkBean workBean) {
        String tip = "烟道检测中";
        //烟道阻力大、油烟量大
        if (workBean.autocruise_pressure && workBean.autocruise_smoke) {
            tip = "烟道阻力大 增压中\n油烟量大 增压中";
            txtPressure.setText(tip);
            waveSmartView.translateToFirst();
            return;
        }
        //烟道阻力大
        else if (workBean.autocruise_pressure) {
            tip = "烟道阻力大 增压中";
            txtPressure.setText(tip);
            waveSmartView.translateToFirst();
            return;

        }
        //油烟大
        else if (workBean.autocruise_smoke) {
            tip = "油烟量大 增压中";
            txtPressure.setText(tip);
            waveSmartView.translateToFirst();
            return;
        } else {
            tip = "烟道检测中";
            txtPressure.setText(tip);
            waveSmartView.translateToSecond();
            return;
        }
    }

    private void initView() {
        waveSmartView.translateToSecond();
        txtPressure.setText("烟道检测中");
        txtClose.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_control_cruise;
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
