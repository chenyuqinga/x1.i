package com.fotile.x1i.activity.screensaver;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.fotile.common.util.AppManagerUtil;
import com.fotile.x1i.bean.event.FinishActivityMessage;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.widget.BottomView;
import com.fotile.x1i.widget.TopBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import fotile.device.cookerprotocollib.helper.DeviceControl;

public class BaseClockActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //进入屏保隐藏状态栏和底栏
        BottomView.getInstance(this).hide();
        TopBar.getInstance(this).hide();

//        //按键灯熄灭
//        PowerUtil.powerLampOff();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (DiffuseView.getInstance(this).isShown()) {
//            DiffuseView.getInstance(this).dismiss();
//        }

        AppManagerUtil.getInstance().addActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (SpeechManager.getInstance().isConnected()) {
//            DiffuseView.getInstance(this).show();
//        }

        //移除当前Activity
        AppManagerUtil.getInstance().removeActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        //重置屏保定时服务
        ScreenTool.getInstance().addResetData("DigitalClockActivity onKeyDown");
        finish();
//        PowerUtil.powerLampOn();//按键灯亮
        DeviceControl.getInstance().setBuzzer(DeviceControl.BUZZER_LONG);
    }

    @Subscribe
    public void onActivityFinish(FinishActivityMessage message) {
        if (message.to_class.getSimpleName().contains("BaseClockActivity")) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F3) {
            //重置屏保定时服务
            ScreenTool.getInstance().addResetData("DigitalClockActivity onKeyDown");
            finish();
//            PowerUtil.powerLampOn();//按键灯亮
            DeviceControl.getInstance().setBuzzer(DeviceControl.BUZZER_LONG);
        }
        return super.onKeyDown(keyCode, event);
    }
}
