package com.fotile.x1i.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.fotile.common.util.AppManagerUtil;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.message.bean.MemorandumDb;
import com.fotile.message.bean.NotificationDb;
import com.fotile.message.bean.NotificationJson;
import com.fotile.message.util.MessageDataBaseUtil;
import com.fotile.x1i.activity.control.CruiseActivity;
import com.fotile.x1i.activity.control.FactoryCheckActivity;
import com.fotile.x1i.activity.control.LampActivity;
import com.fotile.x1i.activity.control.WindControlActivity;
import com.fotile.x1i.activity.screensaver.BaseClockActivity;
import com.fotile.x1i.bean.event.FinishActivityMessage;
import com.fotile.x1i.dailog.FaultDialog;
import com.fotile.x1i.server.wifilink.StoveControl;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.widget.SnakeBar;
import com.fotile.x1i.widget.TopBar;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import fotile.device.cookerprotocollib.helper.DeviceControl;
import fotile.device.cookerprotocollib.helper.DeviceObserverable;
import fotile.device.cookerprotocollib.helper.IDeviceReportListener;
import fotile.device.cookerprotocollib.helper.bean.DeviceMessage;
import fotile.device.cookerprotocollib.helper.bean.WorkBean;
import rx.functions.Action1;

/**
 * 项目名称：X1.I
 * 创建时间：2018/7/6 15:09
 * 文件作者：yaohx
 * 功能描述：处理中间件上报的数据
 */
public class DeviceReportManager implements IDeviceReportListener {


    /**
     * -1--待机
     * 1--空气管家
     * 2--智能巡航
     * 3--风量控制
     * 5--强制跑
     * 6--空气管家自检
     */
    public static final int WORK_INIT = -1;
    //    public static final int WORK_AIR_STEWARD = 1;
    public static final int WORK_AUTO = 2;
    public static final int WORK_WIND_CONTROL = 3;
    //    public static final int WORK_WEAK_WIND_CONTROL = 4;
    public static final int WORK_FORCEWORK = 5;
    //    public static final int WORK_AIR_CHECK = 6;
    public static final int WORK_CLEAN = 7;

    /**
     * 当前设备的工作模式
     */
    public static int work_state = WORK_INIT;

    /**
     * 当前设备的风速
     */
    public static int wind_speed = 0;
    /**
     * 全局对象，只有一个DeviceObserverable
     */
    private DeviceObserverable deviceObserverable;
    /**
     * 上一次接受到的workBean
     */
    private WorkBean oldWorkBean;

    /**
     * 视频是否打开
     */
    public boolean is_video_show = false;

    private Context context;
    private static DeviceReportManager deviceManager;
    /**
     * 家庭id
     */
    private int mHomeId = 0;
    /**
     * 故障提示框
     */
    private FaultDialog faultDialog;
    /**
     * 乐投是否打开
     */
    public boolean is_lt_show = false;

    private static long time_init;

    private DeviceReportManager() {
        deviceObserverable = DeviceObserverable.getInstance();
    }

    public static DeviceReportManager getInstance() {
        if (null == deviceManager) {
            deviceManager = new DeviceReportManager();
            time_init = System.currentTimeMillis();
        }
        return deviceManager;
    }

    public void initContext(Context context) {
        this.context = context;
    }

    /**
     * 初始化jni
     *
     * @param macAddress
     */
    public void initDeviceJni(String macAddress) {
        if (null != deviceObserverable) {
            deviceObserverable.initJNI(macAddress, this);
            //在action中处理数据逻辑
            deviceObserverable.addMessageAction(actionMessage);
            //在action中处理数据逻辑
            deviceObserverable.addBindAction(actionBind);
        }
    }

    /**
     * 开机设置一些默认值
     */
    public void setDefault(Context context) {
        is_video_show = false;
        is_lt_show = false;
        work_state = WORK_INIT;
        //开机将灯设置为关闭（因为关机时，会关闭灯，如果此时本地保存为on就会不一致）
        PreferenceUtil.saveLamp(context, 0);
    }

    public void addWorkBeanAction(Action1<WorkBean> action1) {
        deviceObserverable.addWorkBeanAction(action1);
    }

    public void removeWorkBeanAction(Action1<WorkBean> action1) {
        deviceObserverable.removeWorkBeanAction(action1);
    }

    public void addMessageAction(Action1<DeviceMessage> action1) {
        deviceObserverable.addMessageAction(action1);
    }

    public void removeMessageAction(Action1<DeviceMessage> action1) {
        deviceObserverable.removeMessageAction(action1);
    }

    //设备上报回调，在该类中处理界面跳转逻辑和设备状态更新逻辑
    //回调已在主线程
    @Override
    public void onDeviceReport(WorkBean workBean) {
        if (null == context || equalsWorkBean(workBean)) {
            return;
        }
        LogUtil.LOG_COMMAND("上报", workBean);
        //处理故障（让故障页面弹出）
        executeFault(workBean);
        //处理屏保逻辑
        executeAwaken(workBean);
        //处理相应的界面问题（在更改状态之前跳转）
        executeUi(workBean);
        //更改work_state
        changeWorkState(workBean);
        //保存数据
        saveData(workBean);

        oldWorkBean = workBean;
    }

    private boolean equalsWorkBean(WorkBean workBean) {
        if (null != oldWorkBean && null != workBean) {
            return oldWorkBean.equalParam().equals(workBean.equalParam());
        }
        return false;
    }

    //消息相关信息上报
    //回调已在主线程
    Action1<DeviceMessage> actionMessage = new Action1<DeviceMessage>() {
        @Override
        public void call(DeviceMessage message) {
            NotificationJson notificationJson = new NotificationJson();
            //处理消息中心逻辑
            if (message.code == 16) {
                //保存消息到数据库
                NotificationDb notificationDb = notificationJson.parseNotificationFromJSONObject(message.params);
                if (notificationDb != null) {
                    MessageDataBaseUtil.getInstance().insertNotification(notificationDb);
                }
                //更新小红点tip
                PreferenceUtil.setHasUnreadNotification(context, true);
                //远程操作通知显示
                SnakeBar.makeControlSnake(context, notificationDb.getContent()).show();
            }
            //处理家庭备忘录逻辑
            if (message.code == 9) {
                //保存备忘录到数据库
                MemorandumDb memorandumDb = notificationJson.parseMemoFromJSONObject(message.params);
                if (memorandumDb != null) {
                    MessageDataBaseUtil.getInstance().insertMemorandum(memorandumDb);
                }
                //更新小红点tip
                PreferenceUtil.setHasUnreadMemorandum(context, true);
                //家庭备忘录新消息提示
                SnakeBar.makeMsgSnake(context, memorandumDb.getFrom_name() + ":" + memorandumDb.getContent()).show();
                //获取最近一次家庭备忘时间存储
                String time = memorandumDb.getDate() + " " + memorandumDb.getTime().trim() + ".01";
                saveMemorTime(time);
            }
            //处理绑定逻辑
            if (message.code == 6) {
                bindMessage(message.params);
            }

            //获取烟机登录token
            if (message.code == 7) {
                PreferenceUtil.setLampblackLoginTokenToken(context, message.params);
            }

        }
    };

    //绑定相关信息上报--0表示未被订阅，1表示已被订阅，由jni onSetSubscribeFlag上报
    //回调已在主线程
    Action1<Integer> actionBind = new Action1<Integer>() {
        @Override
        public void call(Integer integer) {
            //绑定成功
            if (integer!= 0) {
                PreferenceUtil.setPreferenceValue(context, PreferenceUtil.DEVICE_BIND_STATE, true);
                TopBar.getInstance(context).updateBindState(true);
            }else  {
                PreferenceUtil.setPreferenceValue(context, PreferenceUtil.DEVICE_BIND_STATE, false);
                TopBar.getInstance(context).updateBindState(false);
            }
        }
    };

    /**
     * 处理绑定逻辑
     *
     * @param params
     */
    private void bindMessage(String params) {
        try {
            JSONObject jsonObject = new JSONObject(params);
            String sub = jsonObject.getString("sub");
            String tip = "";
            //绑定成功
            if (!"0".equals(sub)) {
                PreferenceUtil.setPreferenceValue(context, PreferenceUtil.DEVICE_BIND_STATE, true);
                TopBar.getInstance(context).updateBindState(true);
                //存储绑定时间给请求家庭备忘录使用
                String time1 = jsonObject.getString("time");
                if (!TextUtils.isEmpty(time1)) {
                    String memorandumTime = time1.substring(0, time1.indexOf("T")).trim() + " " + time1.substring
                            (time1.indexOf("T") + 1, time1.indexOf(".")).trim() + ".01";
                    saveMemorTime(memorandumTime);
                }
            }
            //解除绑定
            else {
                PreferenceUtil.setPreferenceValue(context, PreferenceUtil.DEVICE_BIND_STATE, false);
                TopBar.getInstance(context).updateBindState(false);
            }
            //homeId
            if (jsonObject != null && jsonObject.has("home_id")) {
                int newHomeId = Integer.parseInt(jsonObject.optString("home_id"));
                if (newHomeId != mHomeId) {//若homeId改变，发送探测包
                    mHomeId = newHomeId;
//                    NetConfigBusiness.getInstance().changeHomeId(mHomeId);
                }
                LogUtil.LOG_Memor("保存的homeId", mHomeId);
                PreferenceUtil.setPreferenceValue(context, PreferenceUtil.HOME_ID, mHomeId + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存最近一次更新备忘录的时间
     */
    private void saveMemorTime(String memorandumTime) {
        LogUtil.LOG_Memor("备忘录保存时间", memorandumTime);
        PreferenceUtil.saveMemorTime(context, memorandumTime);
    }

    /**
     * 处理故障界面
     *
     * @return true发生故障
     */
    private void executeFault(final WorkBean workBean) {
        //油烟机着火,主动关闭油烟机和灶具
        if (workBean.errornum == 0b11) {
            StoveControl.closeFire(true, true, context);
            DeviceControl.getInstance().closeDevice(true);
        }
        //是否提示故障
        boolean faultTip = (boolean) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.FAULT_TIP, true);
        //处理故障
        if (workBean.errornum > 0 && filterErrorNum(workBean.errornum)) {
            if (faultTip) {
                if (null == faultDialog) {
                    Activity current_activity = AppUtil.getCurrentActivity(context);
                    //时间相隔10秒才提示
                    if (System.currentTimeMillis() - time_init > 10 * 1000) {
                        //故障提示框
                        faultDialog = new FaultDialog(current_activity);
                    }
                }
                if (null != faultDialog) {
                    String errornum = String.valueOf(workBean.errornum);
                    faultDialog.setPushTip(errornum);
                    faultDialog.show();
                }
            }
            TopBar.getInstance(context).updateFaultState(true);
        }
        //无故障
        else {
            TopBar.getInstance(context).updateFaultState(false);
        }
    }

    /**
     * 过滤一些无用的故障码
     *
     * @param errornum
     * @return true 表示有故障
     * false 表示无故障
     */
    private boolean filterErrorNum(int errornum) {
        //x1.i没有油烟传感器
        if (errornum == 0x04) {
            return false;
        }
        return true;
    }

    /**
     * 处理相应的界面问题
     *
     * @param workBean
     */
    private void executeUi(final WorkBean workBean) {
        if (null != workBean && "on".equals(workBean.devstate)) {
            jump2Lamp(workBean);
            jump2Auto(workBean);
            jump2Wind(workBean);
        }
    }

    public void executeAwaken(WorkBean workBean) {
        //如果上一次的状态是非工作
        if (work_state == WORK_INIT) {
            //息屏下或者屏保界面中，收到手机app发送的控制指令或者空气管家上报
            if (!Tool.isScreenLight(context) || AppUtil.isAwaitView()) {
                //处理风机运行状态，比如空气管家或者手机发送了控制风机指令
                if ("on".equals(workBean.devstate)) {
                    //前后的workBean有变化才点亮屏幕-当手机app进入内页会间隔查询设备状态导致数据上报
                    if (!equalsWorkBean(workBean)) {
//                        int old_lamp = oldWorkBean.lamp;
//                        int new_lamp = workBean.lamp;
                        //风机大于0或者灯开\关
                        if (workBean.motorgear > 0) {
                            closeSaverAndLight();
                        }
                    }
                }
            }
        }
    }

    /**
     * 关闭屏保以及点亮屏幕
     */
    private void closeSaverAndLight() {
        //接受到底层指令后，关闭屏保
        EventBus.getDefault().post(new FinishActivityMessage(BaseClockActivity.class));
        //点亮屏幕
        Tool.lightScreen(context);
    }


    private void jump2Wind(WorkBean workBean) {
        if ("manual".equals(workBean.workmode) && workBean.motorgear > 0 && "on".equals(workBean.devstate)) {
            //如果已经是风量控制状态
            if (work_state == WORK_WIND_CONTROL) {
                return;
            }
            if (!needJump(WindControlActivity.class, workBean)) {
                return;
            }

            Activity activity = AppManagerUtil.getInstance().currentActivity();
            if (null != activity) {
                if (!(activity instanceof WindControlActivity)) {
                    launchActivity(WindControlActivity.class, activity);
                }
            }
        }
    }

    private void jump2Auto(WorkBean workBean) {
        //自动档跳转逻辑
        if ("autocruise".equals(workBean.workmode) && "on".equals(workBean.devstate)) {
            //如果已经是自动挡状态
            if (work_state == WORK_AUTO) {
                return;
            }
            if (!needJump(CruiseActivity.class, workBean)) {
                return;
            }

            Activity activity = AppManagerUtil.getInstance().currentActivity();
            if (null != activity) {
                //产线自检时不跳转
                if (!(activity instanceof FactoryCheckActivity) && !(activity instanceof CruiseActivity)) {
                    launchActivity(CruiseActivity.class, activity);
                }
            }
        }
    }

    private void jump2Lamp(WorkBean workBean) {
        if (null != oldWorkBean) {
            //如果前后灯状态一致，不执行跳转
            if (oldWorkBean.lamp == (workBean.lamp)) {
                return;
            }
            if (!needJump(LampActivity.class, workBean)) {
                return;
            }

            if (workBean.lamp > 0) {
                Activity activity = AppManagerUtil.getInstance().currentActivity();
                if (null != activity) {
                    //产线自检时不跳转
                    if (!(activity instanceof FactoryCheckActivity) && !(activity instanceof LampActivity)) {
                        Intent intent = new Intent(context, LampActivity.class);
                        intent.putExtra("lamp", workBean.lamp);
                        launchActivity(intent, activity);
                    }
                }
            }
        }
    }

    private boolean needJump(Class class_, WorkBean workBean) {
        Activity activity = AppManagerUtil.getInstance().currentActivity();
        //爱奇艺打开时不跳转
        if (is_video_show) {
            return false;
        }
        //乐投打开时不跳转
        if (is_lt_show) {
            return false;
        }
        //清洁保养模式下，不执行跳转
        if ("clean".equals(workBean.workmode)) {
            return false;
        }
        if (null != activity) {
            //当栈中没有对应页面时才执行跳转
            if (!AppManagerUtil.getInstance().activtiyInStack(class_)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 每一次消息上报，均保存数据
     * 工作状态work_state已更新
     *
     * @param workBean
     */
    private void saveData(WorkBean workBean) {
        //保存风机档位
        PreferenceUtil.saveWindSpeed(context, workBean.motorgear);
        //保存灯的状态
        PreferenceUtil.saveLamp(context, workBean.lamp);
        //保存运行时间
        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.RUN_TIME, workBean.runtime);
        //保存远程开关
        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.TELE_CONTROL, workBean.telecontrol);

    }

    /**
     * 更改工作状态
     *
     * @param workBean
     */
    private void changeWorkState(WorkBean workBean) {
        //待机状态-手动加0档是停止风机运行
        if (("manual".equals(workBean.workmode) && workBean.motorgear == 0) || "idle".equals(workBean.workmode)) {
            work_state = WORK_INIT;
        }
        //关机
        else if ("wait".equals(workBean.devstate) || "off".equals(workBean.devstate)) {
            work_state = WORK_INIT;
        }
        //开机
        else if ("on".equals(workBean.devstate)) {
            //智能巡航
            if ("autocruise".equals(workBean.workmode)) {
                work_state = WORK_AUTO;
            }
            //            //空气管家
            //            if ("airsteward".equals(workBean.workmode)) {
            //                work_state = WORK_AIR_STEWARD;
            //            }
            //风量控制 - 风量控制的风机要大于0
            if ("manual".equals(workBean.workmode) && workBean.motorgear > 0) {
                work_state = WORK_WIND_CONTROL;
                wind_speed = workBean.motorgear;
            }
            //强制跑模式
            if ("forcework".equals(workBean.workmode) && workBean.motorgear > 0) {
                work_state = WORK_FORCEWORK;
            }
            //            //空气管家自检
            //            if ("selftest".equals(workBean.workmode)) {
            //                work_state = WORK_AIR_CHECK;
            //            }
            //清洁保养
            if ("clean".equals(workBean.workmode)) {
                work_state = WORK_CLEAN;
            }
        }
    }

    private void launchActivity(Class class_, Activity activity) {
        if (null != activity) {
            //界面跳转
            Intent intent = new Intent(context, class_);
            activity.startActivity(intent);
        } else {
            //界面跳转
            Intent intent = new Intent(context, class_);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private void launchActivity(Intent intent, Activity activity) {
        if (null != activity) {
            //界面跳转
            activity.startActivity(intent);
        } else {
            //界面跳转
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public boolean isInWorkState(int... workStates) {
        Log.e("NetConfigBusiness", "workState: " + work_state);
        if (workStates.length == 1) {
            return workStates[0] == work_state;
        } else {
            for (int workState : workStates) {
                if (workState == work_state) {
                    return true;
                }
            }
            return false;
        }
    }

    public int getWindSpeed() {
        return wind_speed;
    }
}
