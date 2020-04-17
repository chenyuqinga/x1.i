package com.fotile.x1i.server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.bean.event.TimerServerMessage;
import com.fotile.x1i.dailog.FullScreenDialog;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.widget.SnakeBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fotile.device.cookerprotocollib.helper.DeviceControl;

public class TimerServer extends Service {
    private TimerCountDownTimer countDownTimer;
    /**
     * 是否正在计时
     */
    private boolean isTick;
    /**
     * 定时结束蜂鸣
     */
    private Timer timer;
    /**
     * 定时总时长
     */
    private long tick_time_all;
    private long tick_time_left;
    private int minute;
    /**
     * 回调listener
     */
    private List<OnTimerTickListener> listListener = new ArrayList<OnTimerTickListener>();

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    /**
     * 开启倒计时
     *
     * @param minute
     */
    public void start(int minute) {
        if (!isTick && minute > 0) {
            isTick = true;
            this.minute = minute;
            tick_time_all = minute * 60 * 1000;
            countDownTimer = new TimerCountDownTimer(tick_time_all, 1000);
            countDownTimer.start();
            PreferenceUtil.setPreferenceValue(this, PreferenceUtil.IS_COUNTING_DOWN, true);

            LogUtil.LOG_TICK("计时器开始", "start---------------------------------------");
        }
    }


    /**
     * 计时管理
     */
    class TimerCountDownTimer extends CountDownTimer {
        public TimerCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            isTick = false;
            //定时结束，提示框
            showTickEnd();
            //定时结束，开始蜂鸣
            startBuzzerTimer();
            //定时结束告诉app
            DeviceControl.getInstance().setTimerEnd();

            //通知UI计时结束
            if (null != listListener && listListener.size() > 0) {
                for (OnTimerTickListener onTimerTickListener : listListener) {
                    onTimerTickListener.onTickFinish();
                }
            }
            PreferenceUtil.setPreferenceValue(TimerServer.this, PreferenceUtil.IS_COUNTING_DOWN, true);
            LogUtil.LOG_TICK("计时器结束", "end---------------------------------------");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tick_time_left = millisUntilFinished;
            isTick = true;
            //计算剩余分钟
            int min = (int) Tool.divide(millisUntilFinished, 60000);
            //计算剩余秒
            int second = (int) (millisUntilFinished / 1000 % 60);
            //回调到view的string
            String minStr = min < 10 ? "0" + min : String.valueOf(min);
            //回调到view的string
            String secStr = second < 10 ? "0" + second : String.valueOf(second);
            LogUtil.LOG_TICK("定时onTick", minStr + ":" + secStr);

            if (null != listListener && listListener.size() > 0) {
                for (OnTimerTickListener onTimerTickListener : listListener) {
                    onTimerTickListener.onTicking(tick_time_left, tick_time_all);
                }
            }
        }
    }

    /**
     * 处理定时结束
     */
    private void showTickEnd(){
        ScreenTool.getInstance().addResetData("倒计时结束");
        FullScreenDialog dialog = new FullScreenDialog(this, FullScreenDialog.FullScreenTip.ONE_BTN);
        dialog.setMessage("倒计时结束");
        dialog.showSystemDialog();
    }

    /**
     * 定时结束，开启蜂鸣
     */
    private void startBuzzerTimer() {
        if (null != timer) {
            timer.cancel();
            timer.purge();
        }
        final int item = 1000;
        final int count = 3;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            //总时长
            int time = 0;

            @Override
            public void run() {
                if (time >= item * count) {
                    cancelTimer();
                } else {
                    //定时结束，开始蜂鸣
                    DeviceControl.getInstance().setBuzzer(DeviceControl.BUZZER_SHORT);
                }
                time = time + item;
            }

        }, 100, item);
    }

    /**
     * 取消蜂鸣定时和倒计时定时
     */
    private void cancelTimer() {
        isTick = false;
        //结束蜂鸣器的定时
        if (null != timer) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        //结束用户设置定时
        if (null != countDownTimer) {
            countDownTimer.cancel();
        }
        PreferenceUtil.setPreferenceValue(this, PreferenceUtil.IS_COUNTING_DOWN, false);
    }

    public long getLeftTickTime() {
        return tick_time_left;
    }

    /**
     * 返回分钟
     *
     * @return
     */
    public int getAllTickTimeMinute() {
        return minute;
    }

    /**
     * 停止计时器
     */
    public void stop() {
        if (null != listListener && listListener.size() > 0) {
            for (OnTimerTickListener onTimerTickListener : listListener) {
                onTimerTickListener.onTickCanceled();
            }
        }
        cancelTimer();
        LogUtil.LOG_TICK("计时器被取消", "stop---------------------------------------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        listListener.clear();
    }

    @Subscribe
    public void onEventCancel(TimerServerMessage timerServerMessage) {
        if (timerServerMessage.to_class.getSimpleName().contains("TimerServer")) {
            stop();
//            cancelTimer();
//            if (null != onTimerTickListener) {
//                onTimerTickListener.onTickCanceled();
//            }
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public TimerServer getServer() {
            return TimerServer.this;
        }
    }


    //------------------------UI交互逻辑-------------------------//
    public boolean isTick() {
        return isTick;
    }

    public interface OnTimerTickListener {
        void onTicking(long left_time, long all_time);

        //计时器倒计时结束
        void onTickFinish();

        //计时器被外部取消
        void onTickCanceled();
    }

    public void addOnTimerTickListener(OnTimerTickListener onTimerTickListener) {
        if (null != onTimerTickListener && !listListener.contains(onTimerTickListener)) {
            listListener.add(onTimerTickListener);
        }
    }

    public void removeTimerTickListener(OnTimerTickListener onTimerTickListener) {
        if (null != onTimerTickListener) {
            listListener.remove(onTimerTickListener);
        }
    }

}
