package com.fotile.x1i.server.screensaver;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


import com.ebanswers.sdk.util.sleep.ScreenOffManager;
import com.fotile.common.util.AppManagerUtil;
import com.fotile.common.util.CommonConstant;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.activity.screensaver.AwaitClockActivity;
import com.fotile.x1i.activity.screensaver.BaseClockActivity;
import com.fotile.x1i.activity.screensaver.DigitalClockActivity;
import com.fotile.x1i.activity.screensaver.VideoViewActivity;
import com.fotile.x1i.bean.event.FinishActivityMessage;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 文件名称：ScreenSaverService
 * 创建时间：2017/11/16 9:55
 * 文件作者：yaohx
 * 功能描述：屏保
 */

public class ScreenSaverService extends Service {

    /**
     * 暗屏定时器
     */
    private Timer timer_dark = new Timer();
    /**
     * 屏保定时器
     */
    private Timer timer_saver = new Timer();
    /**
     * 息屏定时器
     */
    private Timer timer_sleep = new Timer();
    /**
     * 控制定制延迟800毫秒执行的定时器
     */
    private Timer timer_main = new Timer();

    /**
     * 主界面无操作10s后暗屏-ms
     */
    private long dark_time_main = CommonConstant.IS_SCREEN_TEST ? 10 * 1000 : 10 * 1000;
    /**
     * 在菜谱详情浏览界面，无操作20min后暗屏-ms
     */
    private long dark_time_recipe_detail = CommonConstant.IS_SCREEN_TEST ? 10 * 1000 : 20 * 60 * 1000;
    /**
     * 在菜谱主页，无操作2min后暗屏-ms
     */
    private long dark_time_recipe_main = CommonConstant.IS_SCREEN_TEST ? 10 * 1000 : 2 * 60 * 1000;
    /**
     * 灯开启，工作中，或者音乐播放中2min进入暗屏，暗屏保持，不进入屏保
     */
    private long dark_time_working_playmusic = CommonConstant.IS_SCREEN_TEST ? 10 * 1000 : 2 * 60 * 1000;
    /**
     * 屏幕经过多少秒进入到暗屏--保持暗屏--应用场景为点击锁屏按钮
     */
    private long dark_keep_time = CommonConstant.IS_SCREEN_TEST ? 5 * 1000 : 10 * 1000;
    /**
     * 其他情况下无操作2min后暗屏
     */
    private long dark_time_other = CommonConstant.IS_SCREEN_TEST ? 10 * 1000 : 2 * 60 * 1000;
    /**
     * 键盘弹出时的暗屏时间
     */
    private long dark_time_keyboard = CommonConstant.IS_SCREEN_TEST ? 10 * 1000 : 1 * 60 * 60 * 1000;

    /**
     * 暗屏之后经过10s进入屏保-ms
     */
    private long awaitClockTime = CommonConstant.IS_SCREEN_TEST ? 10 * 1000 : 10 * 1000;
    /**
     * 屏保待机无操作1小时黑屏-ms
     */
    private long turnOffTime = CommonConstant.IS_SCREEN_TEST ? 10 * 1000 : 1 * 60 * 60 * 1000;
    /**
     * 正常
     */
    public static final int SCREEN_NORMAL = 0;
    /**
     * 暗屏
     */
    public static final int SCREEN_DARK = 1;
    /**
     * 屏保
     */
    private static final int SCREEN_SAVER = 2;
    /**
     * 灭屏
     */
    public static final int SCREEN_SLEEP = 3;

    private static int current_screen_state = SCREEN_NORMAL;

    /**
     * 屏幕是否正在暗屏阶段
     */
    private boolean isScreenDarking = false;
    /**
     * 屏幕是否正在屏保阶段
     */
    private boolean isScreenSavering = false;
    /**
     * 屏幕是否处于熄灭状态
     */
    private boolean isScreenSleeping = false;

    /**
     * 进入息屏延时重置状态的时间
     */
    private final long delay_reset_time = 3000;
    /**
     * 延时关闭屏保界面（关闭页面会导致执行onResume，需要保证onResume时状态为sleep状态，所以需要先关闭页面后更新状态）
     */
    private final long delay_close_saver = 1500;

    /**
     * 是否工作中和灯开启中
     */
    private boolean is_working = false;
    /**
     * 是否音乐播放中
     */
    private boolean is_music_playing = false;

    private boolean is_stop = false;
    /**
     * 每小时轮训查看屏保状态，保证能够进入屏保
     */
    private final int LOOP_TIME = CommonConstant.IS_SCREEN_TEST ? 80 * 1000 : 2 * 60 * 60 * 1000;

    final int WHAT_DELAY_CLOSE = 1001;
    final int WHAT_DELAY_RESET = 1002;
    final int WHAT_KEEP_LOOP = 1003;

    private ScreenOffManager screenOffManager = new ScreenOffManager();

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        //取消可能正在运行的定时器
        cancelTimer();
        startTimer(false, false, false);
        startLoopTimer();
    }

    //接受屏保指令
    @Subscribe
    public synchronized void onPostScreenMessage(ScreenServiceMessage message) {
        int command = message.command;
        if (command != -1) {
            //终止了屏保
            if (is_stop) {
                return;
            }
            //屏保重启时设置为10小时不操作才会息屏
            AppUtil.setSleepTime(this, false);
            //关闭屏保时钟界面
            EventBus.getDefault().post(new FinishActivityMessage(BaseClockActivity.class));
            //取消可能正在运行的定时器
            cancelTimer();
            current_screen_state = SCREEN_NORMAL;
            changeScreenState();
            //将亮度调亮
            Tool.setCurrentBrightLight(ScreenSaverService.this);

            //stop屏保所有逻辑
            if (command == ScreenTool.SCREEN_TYPE_STOP) {
                is_stop = true;
                return;
            }
            //恢复屏保逻辑
            if (command == ScreenTool.SCREEN_TYPE_RESUME) {
                is_stop = false;
                return;
            }
            //用户操作或者界面跳转，定时服务重启
            if (command == ScreenTool.SCREEN_TYPE_RESET) {
                LogUtil.LOG_SCREEN("屏保服务定时器重置--定时器重启");
                startMainTimer(false, false, false);
            }
            //立即进入屏保
            if (command == ScreenTool.SCREEN_TYPE_RESET_NOW) {
                LogUtil.LOG_SCREEN("屏保服务定时器重置--立即进入屏保");
                startMainTimer(true, false, false);
            }
            //锁频状态
            if (command == ScreenTool.SCREEN_TYPE_KEEP_DARK) {
                LogUtil.LOG_SCREEN("屏保服务定时器重置--锁频状态保持暗屏");
                startMainTimer(false, true, false);
            }
            //暂停屏保
            if (command == ScreenTool.SCREEN_TYPE_PAUSE) {
                LogUtil.LOG_SCREEN("屏保服务定时器重置--暂停屏保");
            }
            //键盘弹出时
            if (command == ScreenTool.SCREEN_TYPE_KEYBOARD_UP) {
                LogUtil.LOG_SCREEN("屏保服务定时器重置--键盘弹出时保持1小时亮屏");
                startMainTimer(false, false, true);
            }
        }

    }

    /**
     * 每小时轮训查看屏保状态，保证能够进入屏保
     */
    private void startLoopTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler_reset.sendEmptyMessage(WHAT_KEEP_LOOP);
            }
        }, 1000, LOOP_TIME);
    }

    /**
     * 开启主定时器，延迟800毫秒
     * 当用户关闭某一个功能时，页面跳转到了onResume，但是此时状态还未上报，所以还是工作状态
     * 所以需要延时开启屏保定时服务，等待工作状态及时更新
     */
    private synchronized void startMainTimer(final boolean is_now, final boolean keep_dark, final boolean keyboard_up) {
        initTimer();
        if (is_now || keep_dark) {
            startTimer(is_now, keep_dark, keyboard_up);
        } else {
            if (null != timer_main) {
                timer_main.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        startTimer(is_now, keep_dark, keyboard_up);
                    }
                }, 900);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        current_screen_state = SCREEN_NORMAL;
        changeScreenState();
        EventBus.getDefault().register(this);
    }

    public static int getCurrentState() {
        return current_screen_state;
    }

    /**
     * 开启定时器，检测屏保
     *
     * @param is_now      是否直接进入屏保
     * @param keep_dark   是否是锁频状态
     * @param keyboard_up 键盘是否弹出
     */
    private synchronized void startTimer(final boolean is_now, final boolean keep_dark, final boolean keyboard_up) {
        //定时器执行过程中，如果视频打开，不执行屏保相关操作
        if (DeviceReportManager.getInstance().is_video_show) {
            LogUtil.LOG_SCREEN("爱奇艺/卖场视频播放中,亮屏");
            cancelTimer();
            return;
        }
        //投屏打开时，不执行屏保
        if (DeviceReportManager.getInstance().is_lt_show) {
            LogUtil.LOG_SCREEN("投屏打开时，不执行屏保");
            cancelTimer();
            return;
        }

        //获取设备状态
        is_working = AppUtil.isDeviceWorking4Screen(this);
        is_music_playing = AppUtil.isMusicPlaying(this);
        initTimer();
        //计算暗屏时间
        final long dark_time = getDarkTime(is_now, keep_dark, keyboard_up);

        //如果保持暗屏-不执行后续逻辑
        if (keep_dark) {
            //定时一段时间后执行暗屏
            timer_dark.schedule(new TimerTask() {
                @Override
                public void run() {
                    handlerScreenDarkLogic();
                }
            }, dark_time);
            return;
        }
        //如果立即进入屏保-执行后续逻辑（息屏）
        if (is_now) {
            handlerScreenSaverLogic();
            //后续再进入息屏
            timer_sleep.schedule(new TimerTask() {
                @Override
                public void run() {
                    handlerScreenSleepLogic();
                }
            }, dark_time + turnOffTime);
            return;
        }
        //如果键盘弹出时，保持1小时亮屏
        if (keyboard_up) {
            timer_dark.schedule(new TimerTask() {
                @Override
                public void run() {
                    handlerScreenDarkLogic();
                }
            }, dark_time);
            timer_saver.schedule(new TimerTask() {
                @Override
                public void run() {
                    handlerScreenSaverLogic();
                }
            }, dark_time + awaitClockTime);
            timer_sleep.schedule(new TimerTask() {
                @Override
                public void run() {
                    handlerScreenSleepLogic();
                }
            }, dark_time + awaitClockTime + turnOffTime);
            return;
        }

        //如果系统--工作中-灯亮-音乐播放中，只暗屏-不执行后续逻辑
        if (is_working || is_music_playing) {
            LogUtil.LOG_SCREEN("工作中-灯亮-音乐播放中,只暗屏");
            timer_dark.schedule(new TimerTask() {
                @Override
                public void run() {
                    handlerScreenDarkLogic();
                }
            }, dark_time);
            return;
        }

        /***********************其他正常逻辑↓***********************/
        timer_dark.schedule(new TimerTask() {
            @Override
            public void run() {
                handlerScreenDarkLogic();
            }
        }, dark_time);

        timer_saver.schedule(new TimerTask() {
            @Override
            public void run() {
                handlerScreenSaverLogic();
            }
        }, dark_time + awaitClockTime);

        timer_sleep.schedule(new TimerTask() {
            @Override
            public void run() {
                handlerScreenSleepLogic();
            }
        }, dark_time + awaitClockTime + turnOffTime);
        /***********************其他正常逻辑↑***********************/
    }

    /**
     * 初始化定时器
     */
    private void initTimer() {
        if (null == timer_dark) {
            timer_dark = new Timer();
        }
        if (null == timer_saver) {
            timer_saver = new Timer();
        }
        if (null == timer_sleep) {
            timer_sleep = new Timer();
        }
        if (null == timer_main) {
            timer_main = new Timer();
        }
    }

    /**
     * 处理从亮屏到暗屏逻辑--经过了dark_time之后才会执行该方法
     */
    private void handlerScreenDarkLogic() {
        //只有在正常状态下才可以进入暗屏
        if (current_screen_state == SCREEN_NORMAL) {
            Tool.setCurrentBrightDark(ScreenSaverService.this);
            current_screen_state = SCREEN_DARK;
            changeScreenState();
            LogUtil.LOG_SCREEN("进入暗屏");
        }
    }

    /**
     * 处理进入屏保的逻辑
     */
    private void handlerScreenSaverLogic() {
        //暗屏下，--或者正常状态下选择立即进入屏保
        if (current_screen_state == SCREEN_DARK || current_screen_state == SCREEN_NORMAL) {
            current_screen_state = SCREEN_SAVER;
            changeScreenState();
            //屏保样式
            int styleSelected = PreferenceUtil.getScreenSaverType(ScreenSaverService.this);
            //默认无屏保，选择灭屏
            if (styleSelected == 7) {
                LogUtil.LOG_SCREEN("没有屏保，息屏");
                //延时关闭屏保
                handler_reset.sendEmptyMessageDelayed(WHAT_DELAY_CLOSE, delay_close_saver);
                //延时重置状态
                handler_reset.sendEmptyMessageDelayed(WHAT_DELAY_RESET, delay_reset_time);
                current_screen_state = SCREEN_SLEEP;
                changeScreenState();
                //发送息屏广播
                sleep();
            } else {
                LogUtil.LOG_SCREEN("当前屏保style" + styleSelected);
                //显示屏保
                startAwaitClock(styleSelected);
                LogUtil.LOG_SCREEN("进入屏保");
            }
            //进入屏保之后，关闭暗屏定时器
            cancelDarkTimer();
        }
    }

    /**
     * 处理进入息屏逻辑
     */
    private void handlerScreenSleepLogic() {
        //只有在屏保下才可以进入息屏
        if (current_screen_state == SCREEN_SAVER) {
            LogUtil.LOG_SCREEN("进入息屏");
            //延时关闭屏保
            handler_reset.sendEmptyMessageDelayed(WHAT_DELAY_CLOSE, delay_close_saver);
            //延时重置状态
            handler_reset.sendEmptyMessageDelayed(WHAT_DELAY_RESET, delay_reset_time);
            current_screen_state = SCREEN_SLEEP;
            changeScreenState();
            //息屏广播
            sleep();
            //进入息屏之后，关闭屏保定时器
            cancelSaverTimer();
        }
    }


    /**
     * 计算各种情况的暗屏时间
     *
     * @param is_now
     * @param keep_dark
     * @param keyboard_up
     */
    private long getDarkTime(boolean is_now, boolean keep_dark, boolean keyboard_up) {
        long dark_time = 0;
        //经过dark_keep_time之后，保持暗屏
        if (keep_dark) {
            dark_time = dark_keep_time;
            LogUtil.LOG_SCREEN("保持暗屏状态-暗屏时间：" + dark_time);
            return dark_time;
        }
        //立即进入屏保
        if (is_now) {
            dark_time = 0;
            LogUtil.LOG_SCREEN("立即进入屏保-暗屏时间：" + dark_time);
            return dark_time;
        }
        //键盘弹出时，保持1小时亮屏
        if (keyboard_up) {
            dark_time = dark_time_keyboard;
            LogUtil.LOG_SCREEN("键盘弹出时-暗屏时间：" + dark_time);
            return dark_time;
        }

        String current_act_name = AppUtil.getCurrentActivityName(ScreenSaverService.this);
        LogUtil.LOG_SCREEN("--当前屏保界面--：" + current_act_name);

        //工作中-灯亮-音乐播放中
        if (is_working || is_music_playing) {
            if (dark_time <= 0) {
                dark_time = dark_time_working_playmusic;
            }
            LogUtil.LOG_SCREEN("工作中或者音乐播放中-暗屏时间：" + dark_time);
        } else {
            //主页
            if (current_act_name.equals("MainActivity")) {
                if (dark_time <= 0) {
                    dark_time = dark_time_main;
                }
                LogUtil.LOG_SCREEN("主页-暗屏时间：" + dark_time);
            }
            //菜谱详情界面
            else if (current_act_name.contains("RecipesDetailsActivity")) {
                if (dark_time <= 0) {
                    dark_time = dark_time_recipe_detail;
                }
                LogUtil.LOG_SCREEN("菜谱详情界面-暗屏时间：" + dark_time);
            }
            //其他
            else {
                if (dark_time <= 0) {
                    dark_time = dark_time_other;
                }
                LogUtil.LOG_SCREEN("其他界面-暗屏时间：" + dark_time);
            }
        }
        return dark_time;
    }

    /**
     * 进入息屏之后，关闭定时器
     */
    Handler handler_reset = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //先延迟1s关闭屏保，再去重置状态
                case WHAT_DELAY_CLOSE:
                    //关闭屏保相关界面 AwaitClockActivity DigitalClockActivity
                    EventBus.getDefault().post(new FinishActivityMessage(BaseClockActivity.class));
                    LogUtil.LOG_SCREEN("进入息屏之后，关闭屏保Activity");
                    break;
                //重置状态
                case WHAT_DELAY_RESET:
                    cancelTimer();
                    //将亮度调亮
                    Tool.setCurrentBrightLight(ScreenSaverService.this);
                    //重置状态-为了能让onResume监听到事件
                    current_screen_state = SCREEN_NORMAL;
                    changeScreenState();

                    LogUtil.LOG_SCREEN("进入息屏之后，取消定时器，将亮度调亮，重置状态");
                    break;
                //loop
                case WHAT_KEEP_LOOP:
                    //如果一个小时后，屏幕状态是normal
                    if (current_screen_state == SCREEN_NORMAL || current_screen_state == SCREEN_DARK || Tool
                            .isScreenLight(ScreenSaverService.this)) {
//                        LogUtil.LOG_SCREEN("屏保loop");
//                        ScreenTool.getInstance().addResetData("loop");
                    }
                    break;
            }
            return false;
        }
    });

    /**
     * 取消暗屏定时器
     */
    private synchronized void cancelDarkTimer() {
    }

    /**
     * 取消屏保定时器
     */
    private synchronized void cancelSaverTimer() {
    }

    private synchronized void cancelTimer() {
        if (null != timer_dark) {
            timer_dark.cancel();
            timer_dark.purge();
            timer_dark = null;
        }
        if (null != timer_saver) {
            timer_saver.cancel();
            timer_saver.purge();
            timer_saver = null;
        }
        if (null != timer_sleep) {
            timer_sleep.cancel();
            timer_sleep.purge();
            timer_sleep = null;
        }
        if (null != timer_main) {
            timer_main.cancel();
            timer_main.purge();
            timer_main = null;
        }

    }


    private void changeScreenState() {
        switch (current_screen_state) {
            case SCREEN_NORMAL:
                isScreenDarking = false;
                isScreenSavering = false;
                isScreenSleeping = false;
                break;
            case SCREEN_DARK:
                isScreenDarking = true;
                isScreenSavering = false;
                isScreenSleeping = false;
                break;
            case SCREEN_SAVER:
                isScreenDarking = false;
                isScreenSavering = true;
                isScreenSleeping = false;
                break;
            case SCREEN_SLEEP:
                isScreenDarking = false;
                isScreenSavering = false;
                isScreenSleeping = true;
                break;
        }
    }

    /**
     * 进入屏保act
     *
     * @param styleSelected
     */
    public void startAwaitClock(int styleSelected) {
        Intent intent = new Intent();
        //区分电子时钟样式与其他样式
        if (styleSelected == 6) {
            intent.setClass(this, DigitalClockActivity.class);
        }
        //方太画报
        else if (styleSelected == Constant.SCREEN_FOTILE_SAVER) {
            intent.setClass(this, VideoViewActivity.class);
        } else {
            intent.setClass(this, AwaitClockActivity.class);
        }
        Activity activity = AppManagerUtil.getInstance().currentActivity();
        if (null != activity) {
            activity.startActivity(intent);
        }
    }

    private void sleep() {
        Activity activity = AppManagerUtil.getInstance().currentActivity();

//        int styleSelected = PreferenceUtil.getScreenSaverType(ScreenSaverService.this);
//        //方太画报不息屏
//        if(styleSelected == Constant.SCREEN_FOTILE_SAVER){
//            AppUtil.setSleepTime(this, false);
//        }else {
//        }
        if (null == activity) {
            screenOffManager.sleep(this);
        } else {
            screenOffManager.sleep(activity);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
