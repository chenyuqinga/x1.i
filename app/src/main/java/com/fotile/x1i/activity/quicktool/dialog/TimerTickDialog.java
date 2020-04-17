package com.fotile.x1i.activity.quicktool.dialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.server.TimerServer;
import com.fotile.x1i.widget.ProgressCircle;

import butterknife.BindView;

/**
 * 文件名称：TickTimeSelectDialog
 * 创建时间：2019/5/27 18:22
 * 文件作者：yaohx
 * 功能描述：定时器倒计时dialog，进入该dialog是正在倒计时中
 */
public class TimerTickDialog extends BaseDialog implements View.OnClickListener {
    /**
     * 立即结束
     */
    @BindView(R.id.txt_confirm)
    TextView tvConfirm;
    /**
     * 退出
     */
    @BindView(R.id.txt_cancel)
    TextView tvCancel;

    @BindView(R.id.progress_circle)
    ProgressCircle progressCircle;

    int minute;

    TimerServer timerServer;

    public TimerTickDialog(Context context) {
        super(context, R.style.FullScreenDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = new Intent(context, TimerServer.class);
        context.getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerServer.MyBinder myBinder = (TimerServer.MyBinder) service;
            timerServer = myBinder.getServer();
            //设置回调
            timerServer.addOnTimerTickListener(onTimerTickListener);

            int minute = timerServer.getAllTickTimeMinute();
            int maxValue = minute * 60 * 1000;
            int preValue = (int) (maxValue - timerServer.getLeftTickTime());
            progressCircle.setDuration2(preValue, maxValue,minute +"分",null);
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

        }

        //定时结束
        @Override
        public void onTickFinish() {
            dismiss();
        }

        @Override
        public void onTickCanceled() {
            dismiss();
        }
    };

    @Override
    public void dismiss() {
        super.dismiss();
        if(null != timerServer){
            timerServer.removeTimerTickListener(onTimerTickListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //退出dialog
            case R.id.txt_confirm:
                dismiss();
                break;
            //立即结束倒计时
            case R.id.txt_cancel:
                dismiss();
                if (null != timerServer) {
                    //停止计时，停止服务
                    timerServer.stop();
                    context.getApplicationContext().unbindService(connection);
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_timer_tick;
    }

}