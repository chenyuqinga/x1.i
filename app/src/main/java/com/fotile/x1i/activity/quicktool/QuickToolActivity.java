package com.fotile.x1i.activity.quicktool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.quicktool.ble.BleListActiivty1;
import com.fotile.x1i.activity.quicktool.dialog.CleanWindDialog;
import com.fotile.x1i.activity.quicktool.dialog.TickTimeSelectDialog;
import com.fotile.x1i.activity.quicktool.dialog.TimerTickDialog;
import com.fotile.x1i.activity.quicktool.message.MessageActivity;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.listener.OnDialogListener;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.server.TimerServer;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.widget.ProgressCircle;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.DeviceControl;
import fotile.device.cookerprotocollib.helper.bean.DeviceMessage;
import rx.functions.Action1;

/**
 * 文件名称：QuickToolActivity
 * 创建时间：2019/5/24 16:36
 * 文件作者：yaohx
 * 功能描述：快捷工具
 */
public class QuickToolActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.txt_tool_bt_music)
    TextView txt_tool_bt_music;
    @BindView(R.id.txt_tool_lt)
    TextView txt_tool_lt;
    @BindView(R.id.txt_tool_message)
    TextView txt_tool_message;
    @BindView(R.id.message_redpoint)
    ImageView messageRedPoint;
    @BindView(R.id.txt_tool_lock)
    TextView txt_tool_lock;
    @BindView(R.id.layout_message)
    LinearLayout layoutMessage;

    @BindView(R.id.txt_tool_clear)
    TextView txt_tool_clear;

    /**
     * 定时布局
     */
    @BindView(R.id.layout_tick_normal)
    ImageView layout_tick_normal;
    @BindView(R.id.layout_tick_ing)
    View layout_tick_ing;
    /**
     * 定时器倒计时进度
     */
    @BindView(R.id.progress_circle)
    ProgressCircle progress_circle;

    /**
     * 是否锁屏的标识
     */
    boolean isLocked = false;
    /**
     * 定时器服务
     */
    TimerServer timerServer;

    /**
     * 等待 timerServer bind 成功后开启计时的时间
     */
    private int mCountdownTime;

    final int TICK_STATE_NORMAL = 1000;
    final int TICK_STATE_ING = 1001;
    /**
     * 设备控制相关的对象
     */
    public Action1<DeviceMessage> actionDeviceMessage;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        setTickStateView(TICK_STATE_NORMAL);

        //添加action
        if (null != actionDeviceMessage) {
            DeviceReportManager.getInstance().addMessageAction(actionDeviceMessage);
        }
    }

    private void initData() {
        Intent intent = new Intent(context, TimerServer.class);
        context.getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        int countdown_time = getIntent().getIntExtra("countdown_time", 0);
        if (countdown_time != 0) {
            if (timerServer != null) {
                //点击确定开始倒计时
                timerServer.start(countdown_time);
                //开始倒计时dialog
                TimerTickDialog dialog = new TimerTickDialog(context);
                dialog.show();
            } else {
                //等待timerServer连接成功
                mCountdownTime = countdown_time;
            }
        }
    }

    public void updateRedShow() {
        if (PreferenceUtil.hasUnreadMemorandum(this) || PreferenceUtil.hasUnreadNotification(this)) {
            messageRedPoint.setVisibility(View.VISIBLE);
        } else {
            messageRedPoint.setVisibility(View.GONE);
        }
    }

    private void setTickStateView(int tick_state) {
        switch (tick_state) {
            case TICK_STATE_NORMAL:
                layout_tick_normal.setVisibility(View.VISIBLE);
                layout_tick_ing.setVisibility(View.GONE);
                break;
            case TICK_STATE_ING:
                layout_tick_normal.setVisibility(View.GONE);
                layout_tick_ing.setVisibility(View.VISIBLE);
                break;
        }
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerServer.MyBinder myBinder = (TimerServer.MyBinder) service;
            timerServer = myBinder.getServer();
            //设置回调
            timerServer.addOnTimerTickListener(onTimerTickListener);
            if (timerServer.isTick()) {
                setTickStateView(TICK_STATE_ING);
            }

            if (mCountdownTime != 0) {
                //点击确定开始倒计时
                timerServer.start(mCountdownTime);
                //开始倒计时dialog
                TimerTickDialog dialog = new TimerTickDialog(context);
                dialog.show();
                mCountdownTime = 0;
            }
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
            setTickStateView(TICK_STATE_ING);

            int minute = timerServer.getAllTickTimeMinute();
            int maxValue = minute * 60 * 1000;
            int preValue = (int) (maxValue - timerServer.getLeftTickTime());
            progress_circle.setDuration2(preValue, maxValue, "", null);
        }

        //定时结束
        @Override
        public void onTickFinish() {
            setTickStateView(TICK_STATE_NORMAL);
        }

        @Override
        public void onTickCanceled() {
            setTickStateView(TICK_STATE_NORMAL);
        }
    };

    private void initView() {
        txt_tool_bt_music.setOnClickListener(this);
        txt_tool_lt.setOnClickListener(this);
        layoutMessage.setOnClickListener(this);
        txt_tool_lock.setOnClickListener(this);
        txt_tool_clear.setOnClickListener(this);

        layout_tick_normal.setOnClickListener(this);
        layout_tick_ing.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_quick_tool;
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //蓝牙音乐
            case R.id.txt_tool_bt_music:
                launchActivity(BleListActiivty1.class);
                break;
            //投屏
            case R.id.txt_tool_lt:
                launchActivity(ProjectionScreenActivity.class);
                break;
            //消息中心
            case R.id.layout_message:
                launchActivity(MessageActivity.class);
                break;
            //锁屏
            case R.id.txt_tool_lock:
                if (!isLocked) {
                    isLocked = true;
                }
                AppUtil.showLockScreen(context, listener);
                //锁频短鸣
                DeviceControl.getInstance().setBuzzer(DeviceControl.BUZZER_SHORT);
                break;
            //定时器
            case R.id.layout_tick_ing:
            case R.id.layout_tick_normal:
                //如果正在倒计时中
                if (timerServer.isTick()) {
                    //显示倒计时
                    TimerTickDialog dialog = new TimerTickDialog(context);
                    dialog.show();
                } else {
                    //去选择倒计时时间
                    TickTimeSelectDialog dialog = new TickTimeSelectDialog(context);
                    dialog.setOnDialogListener(timerSelectListener);
                    dialog.show();
                }
                break;
            //清洁保养
            case R.id.txt_tool_clear:
                CleanWindDialog careInstructionDialog = new CleanWindDialog(context);
                careInstructionDialog.show();
                break;
        }
    }

    //定时器时间选择
    OnDialogListener timerSelectListener = new OnDialogListener() {
        @Override
        public void onLeftClick(Object... objects) {
        }

        //点击确定
        @Override
        public void onRightClick(Object... objects) {
            int minute = (int) objects[0];
            //点击确定开始倒计时
            timerServer.start(minute);

            //开始倒计时dialog
            TimerTickDialog dialog = new TimerTickDialog(context);
            dialog.show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        updateRedShow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != timerServer) {
            timerServer.removeTimerTickListener(onTimerTickListener);
        }
        DeviceReportManager.getInstance().removeMessageAction(actionDeviceMessage);
    }

    /**
     * 锁屏状态改变的接口
     */
    public interface LockScreenListener {
        void onLockScreenChanged(boolean isLockScreen);
    }

    LockScreenListener listener = new LockScreenListener() {
        @Override
        public void onLockScreenChanged(boolean isLockScreen) {
            isLocked = isLockScreen;
            //解锁完毕，重置锁屏服务
            //ScreenTool.getInstance().addResetData("锁屏状态监听器");
            //解锁长鸣
            DeviceControl.getInstance().setBuzzer(DeviceControl.BUZZER_LONG);

            Log.e("lock", "set lock screen false");
        }
    };

}
