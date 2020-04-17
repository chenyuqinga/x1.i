package com.fotile.x1i.server.smokelink;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.x1i.activity.screensaver.BaseClockActivity;
import com.fotile.x1i.bean.event.FinishActivityMessage;
import com.fotile.x1i.dailog.DelayCloseDialog;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.widget.SnakeBar;

import org.greenrobot.eventbus.EventBus;

import fotile.device.cookerprotocollib.helper.DeviceControl;
import fotile.device.cookerprotocollib.helper.bean.WorkBean;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fotile.x1i.manager.DeviceReportManager.WORK_INIT;


/**
 * 项目名称：X1.I
 * 创建时间：2018/8/6 10:10
 * 文件作者：yaohx
 * 功能描述：烟灶联动处理类
 */
public class SmokeStoveLinkAction {

    private Context context;
    /**
     * 正常状态-烟灶联动未开启
     */
    public static final int LINK_STATE_NORMAL = 1;
    /**
     * 烟灶联动运行中
     */
    public static final int LINK_STATE_RUNNING = 2;
    /**
     * 烟灶联动倒计时中
     */
    public static final int LINK_STATE_TIMER_DOWN = 3;

    private int link_state = LINK_STATE_NORMAL;
    /**
     * 告诉app烟灶联动状态
     */
    final int WHAT_TELL_DEVICE_LINK = 1002;
    /**
     * 延迟关闭自动档-烟灶联动
     */
    final int WHAT_CLOSE_STOVE_LINK = 1003;
    /**
     * 关闭zwk联动
     */
    final int WHAT_CLOSE_ZWK_LINK = 1004;

    /**
     * 延时告诉手机处于烟灶联动中
     */
    final int delay_time = 1000;

    /**
     * 记录开启烟灶联动的时间节点
     */
    private long time_start_stove_link = 0;

    private static SmokeStoveLinkAction instance;

    /**
     * 设备控制相关的对象
     */
    public Action1<WorkBean> actionWorkBean;

    private SmokeStoveLinkAction() {

    }

    public void initContext(Context context) {
        this.context = context;
        createAction();
    }

    public static SmokeStoveLinkAction getInstance() {
        if (null == instance) {
            instance = new SmokeStoveLinkAction();
        }
        return instance;
    }


    /**
     * 开启烟灶联动
     *
     * @param tag
     */
    public synchronized void startStoveLink(String tag) {
        Observable.just("")
                //指定被观察者执行的线程
                .subscribeOn(Schedulers.immediate())
                //指定观察者的执行线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override public void call(String s) {
                        //正常状态或者倒计时才可以开启烟灶联动，如果是倒计时中，则取消倒计时重新开始流程
                        if (link_state == LINK_STATE_NORMAL || link_state == LINK_STATE_TIMER_DOWN) {
                            boolean result = startLink();
                            //如果联动开启成功
                            if (result) {
                                //远程操作通知显示
                                SnakeBar.makeDeviceLinkMsgSnake(context, "设备联动启动中").show();
                            }
                        }
                    }
                });
    }

    /**
     * 开启其他联动，烟\IKCC\蒸微烤
     *
     * @param tag
     */
    public synchronized void startOtherLink(String tag) {
        Observable.just("")
                //指定被观察者执行的线程
                .subscribeOn(Schedulers.immediate())
                //指定观察者的执行线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override public void call(String s) {
                        //正常状态或者倒计时才可以开启烟灶联动，如果是倒计时中，则取消倒计时重新开始流程
                        if (link_state == LINK_STATE_NORMAL || link_state == LINK_STATE_TIMER_DOWN) {
                            boolean result = startLink();
                            //如果联动开启成功
                            if (result) {
                                //远程操作通知显示
                                SnakeBar.makeDeviceLinkMsgSnake(context, "设备联动启动中").show();
                                //zwk联动，延时两分钟关闭联动
                                int delay = 2 * 60 * 1000;
                                Message message = new Message();
                                message.what = WHAT_CLOSE_ZWK_LINK;
                                handler.sendMessageDelayed(message, delay);
                                //记录ikcc\蒸微烤联动开启时间
//                                time_start_ikcc_link = System.currentTimeMillis();
                            }
                        }
                    }
                });
    }

    /**
     * 开启联动
     */
    private boolean startLink() {
        int work_state = DeviceReportManager.getInstance().work_state;
        //如果烟机正在工作中,直允许
        if (work_state != WORK_INIT) {
            //排除延时风机和延时电源-排除空气管家
            if (link_state == LINK_STATE_TIMER_DOWN) {

            } else {
                //烟机正常模式下，开启智能巡航或者风量控制时，不允许进入烟灶联动
                return false;
            }
        }
        //取消上一次可能存在的延时
        cancelHandler();
//        //保存开启烟灶联动前的状态，如果烟灶联动前时空气管家，结束后需要切换回去
//        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.BEFORE_LINK_WORK_STATE, work_state);
        //告诉app处于烟灶联动状态--延时发送，解决指令紊乱问题
        Message message = new Message();
        message.obj = true;
        message.what = WHAT_TELL_DEVICE_LINK;
        handler.sendMessageDelayed(message, delay_time);

        //开启自动档
        DeviceControl.getInstance().startCruise();
        link_state = LINK_STATE_RUNNING;
        /***************************取消延时风机和延时电源***************************/
        //取消上一次的烟灶联动延时关机
        EventBus.getDefault().post(new FinishActivityMessage(DelayCloseDialog.class));

        //点亮屏幕（解决烟灶联动时偶现进入屏保时钟bug）
        Tool.lightScreen(context);
        //关闭屏保时钟界面
        EventBus.getDefault().post(new FinishActivityMessage(BaseClockActivity.class));
        //开启烟灶联动时，重启屏保
        ScreenTool.getInstance().addResetData("开启烟灶联动时，重启屏保");
        //记录联动开始时间点
        time_start_stove_link = System.currentTimeMillis();
        return true;
    }

    /**
     * 关闭联动--当灶具关火或者蓝牙断开时调用
     * 调用者为灶具关火或者蓝牙断电
     *
     * @param tag
     */
    public synchronized void closeStoveLink(String tag) {
        Action1<String> action = new Action1<String>() {
            @Override
            public void call(String s) {
                //迭代新需求（只要灶具关火时，烟机在运行--非空气管家，就选择关闭烟机）
                int work_state = DeviceReportManager.getInstance().work_state;
                //WORK_INIT 时更新link_state
                if (work_state == WORK_INIT) {
                    endLink(false);
                    return;
                }
                //取消上一次可能存在的延时
                cancelHandler();
                int delayTime = PreferenceUtil.getDelayTime(context);
                //延时关闭烟灶联动-ms
                delayTime = delayTime * 60 * 1000;
                boolean delayShutDown = PreferenceUtil.getDelayShutSwitch(context);

                //存在延时关机
                if (delayTime > 0 && delayShutDown) {
                    //延时关闭烟灶联动，进入延时关机
                    Message message = new Message();
                    message.what = WHAT_CLOSE_STOVE_LINK;
                    handler.sendMessageDelayed(message, delayTime);
                    link_state = LINK_STATE_TIMER_DOWN;
                    AppUtil.showDelayCloseDialog(context, DelayCloseDialog.DELAY_TYPE.DELAY_TYPE_STOVE);
                }
                //不存在延时关机
                else {
                    endLink(false);
                }
            }
        };
        Observable<String> observable = Observable.just("");
        observable.subscribeOn(Schedulers.immediate())//指定被观察者执行的线程
                .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                .subscribe(action);
    }

    /**
     * 立即关闭延时电源或者延时风机-结束烟灶联动
     */
    public void closeSmokeLinkNow() {
        //取消上一次的烟灶联动延时关机
        cancelHandler();
        endLink(false);
        //关闭延时电源
        EventBus.getDefault().post(new FinishActivityMessage(DelayCloseDialog.class));
    }

    /**
     * 结束联动-关闭自动档
     * <p>
     * isInterrupt 表示是否是用户手动切换风机截断联动状态
     */
    private void endLink(boolean isInterrupt) {
        cancelHandler();
        link_state = LINK_STATE_NORMAL;
        //告诉app处于烟灶联动状态--延时发送，解决指令紊乱问题
        Message message = new Message();
        message.obj = false;
        message.what = WHAT_TELL_DEVICE_LINK;
        handler.sendMessageDelayed(message, delay_time);

        //只有自然关闭联动中才去处理下列逻辑
        if (!isInterrupt) {
//            //获取烟灶联动前的设备状态
//            int work_state = (int) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.BEFORE_LINK_WORK_STATE,
//                    WORK_INIT);
//            if (work_state == WORK_AIR_STEWARD) {
//                DeviceControl.getInstance().startAirSteWard();
//            } else {
            //后续产品经理修改为关闭灯、挡烟板、风机
            DeviceControl.getInstance().closeDevice(true);
//            }
            //处理完跳转空气管家逻辑后，重置字段，因为会有关闭功能按钮调用endlink
//            PreferenceUtil.setPreferenceValue(context, PreferenceUtil.BEFORE_LINK_WORK_STATE, -100);
        }


        //关闭延时Dialog
        EventBus.getDefault().post(new FinishActivityMessage(DelayCloseDialog.class));
        //烟灶联动关闭，重启屏保。烟灶联动开始时，重启了屏保，如果此时不重启，会一直保持在暗屏状态
        ScreenTool.getInstance().addResetData("烟灶联动结束，重启屏保");
    }

    public void createAction() {
        actionWorkBean = new Action1<WorkBean>() {
            @Override
            public void call(WorkBean workBean) {
                int work_state = DeviceReportManager.getInstance().work_state;
                if (workBean != null) {
                    //防止在联动开启后，由于其他操作导致的上报会更改link_state，避开2秒
                    //例如三眼灶先开启联动，同时下发灶头点火状态，该指令上报后将running更改为normal导致状态错误
                    if (System.currentTimeMillis() - time_start_stove_link < 2000) {
                        return;
                    }
                    //解决用户手动切换风机模式，需要中断联动状态
                    if (work_state == WORK_INIT || work_state == DeviceReportManager.WORK_WIND_CONTROL) {
                        if (link_state == LINK_STATE_RUNNING) {
                            endLink(true);
                        }
                    }
                }
            }
        };
        DeviceReportManager.getInstance().addWorkBeanAction(actionWorkBean);
    }

    /**
     * 取消上一次的handler延时
     */
    private void cancelHandler() {
        handler.removeMessages(WHAT_TELL_DEVICE_LINK);
        handler.removeMessages(WHAT_CLOSE_STOVE_LINK);
        handler.removeMessages(WHAT_CLOSE_ZWK_LINK);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //告诉app烟灶联动状态
                case WHAT_TELL_DEVICE_LINK:
                    DeviceControl.getInstance().setSmokeStoveLink((Boolean) msg.obj);
                    break;
                //关闭烟灶联动
                case WHAT_CLOSE_STOVE_LINK:
                    endLink(false);
                    break;
                //关闭zwk联动
                case WHAT_CLOSE_ZWK_LINK:
                    endLink(false);
                    break;
            }
            return false;
        }
    });

    public int getLinkState() {
        return link_state;
    }

    /**
     * 弹出延时关机对话框时，设置link_state
     */
    public void setLinkState(int link_state) {
        this.link_state = link_state;
    }

    public boolean isDeviceLinking() {
        return link_state == LINK_STATE_NORMAL ? false : true;
    }
}
