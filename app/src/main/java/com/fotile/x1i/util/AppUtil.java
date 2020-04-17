package com.fotile.x1i.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.ebanswers.sdk.util.sleep.ScreenOffManager;
import com.fotile.common.util.AppManagerUtil;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.message.bean.MemorandumDb;
import com.fotile.message.bean.NotificationDb;
import com.fotile.message.util.MessageDataBaseUtil;
import com.fotile.music.manager.MusicManager;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.music.BaseMusicActivity;
import com.fotile.x1i.activity.quicktool.QuickToolActivity;
import com.fotile.x1i.activity.screensaver.BaseClockActivity;
import com.fotile.x1i.bean.event.DelayCloseEvent;
import com.fotile.x1i.bean.event.FinishActivityMessage;
import com.fotile.x1i.dailog.DelayCloseDialog;
import com.fotile.x1i.dailog.LockScreenDialog;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.widget.BottomView;
import com.fotile.x1i.widget.BottomViewArrowDown;
import com.fotile.x1i.widget.BottomViewArrowUp;
import com.fotile.x1i.widget.TopBar;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import fotile.device.cookerprotocollib.helper.DeviceControl;

import static com.fotile.ota.util.OtaConstant.FILE_ABSOLUTE_OTA;
import static com.fotile.x1i.manager.DeviceReportManager.WORK_INIT;


/**
 * 文件名称：AppUtil
 * 创建时间：2017/10/9 10:50
 * 文件作者：yaohx
 * 功能描述：
 */
public class AppUtil {
    public static int[] res_picture;
    private static DelayCloseDialog mDelayCloseDialog;

    /**
     * 获取资源id
     *
     * @param imageName
     * @return 资源名称
     */
    public static int getResourceById(String imageName) {
        Class drawable = R.mipmap.class;
        Field field = null;
        int r_id;
        try {
            field = drawable.getField(imageName);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id = 0;
        }
        return r_id;
    }

    public static String getCurrentActivityName(Context context) {
        Activity activity = AppManagerUtil.getInstance().currentActivity();
        String name = null == activity ? "" : activity.getClass().getSimpleName();
        return name;
    }

    public static Activity getCurrentActivity(Context context) {
        Activity activity = AppManagerUtil.getInstance().currentActivity();
        return activity;
    }

    /**
     * 播放系统提示音
     *
     * @param context
     */
    public static void playNotificationSound(Context context) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(context, uri);
        rt.play();
    }


    /**
     * 获取ip地址
     *
     * @param context
     * @return
     */
    public static String getIpAddress(Context context) {
        String ipAddress = null;
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            //当前使用无线网络
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //得到IPV4地址
                ipAddress = intIPToStringIP(wifiInfo.getIpAddress());
            }
        }
        return ipAddress;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIPToStringIP(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
    }

    /**
     * 照明灯,风量控制、智能巡航 是否在工作
     *
     * @param context
     * @return
     */
    public static boolean isDeviceWorking4Screen(Context context) {
        boolean isWorking = false;
        int lamp = PreferenceUtil.getLamp(context);
        int work_state = DeviceReportManager.getInstance().work_state;
        //灯开、自动挡、风量控制、强制跑
        if (lamp > 0 || work_state == DeviceReportManager.WORK_CLEAN || work_state == DeviceReportManager.WORK_AUTO
                || work_state == DeviceReportManager.WORK_WIND_CONTROL || work_state == DeviceReportManager
                .WORK_FORCEWORK) {
            isWorking = true;
        } else {
            isWorking = false;
        }
        return isWorking;
    }


    /**
     * 关闭所有功能(dock功能,在线音乐,在线影视，智能菜谱播放)
     */
    public static void closeAllFunction(Context context) {
        //关闭dock栏
        DeviceControl.getInstance().closeDevice(true);
        //关闭音乐
        stopPlayMusic(context);
        //关闭视频
        AppUtil.setVideoState(-1, context);
    }


    /**
     * 关闭所有功能(dock功能,在线音乐,在线影视，智能菜谱播放)
     */
    public static void closeAllFunctionButMusic(Context context) {
        //关闭dock栏
        DeviceControl.getInstance().closeDevice(true);
        //关闭视频
        AppUtil.setVideoState(-1, context);
    }

    /**
     * 音乐是否在播放
     */
    public static boolean isMusicPlaying(Context context) {
        XmPlayerManager playerManager = XmPlayerManager.getInstance(context);
        return playerManager.isPlaying();
    }

    /**
     * 停止音乐播放
     *
     * @param context
     */
    public static void stopPlayMusic(Context context) {
        Log.e("MusicPlayActivity", "stop play music by app util");
        XmPlayerManager playerManager = XmPlayerManager.getInstance(context);
        //关闭所有音乐播放界面
        EventBus.getDefault().post(new FinishActivityMessage(BaseMusicActivity.class));
        if (MusicManager.getInstance().isDuiMode()) {
            MusicManager.getInstance().stop();
            return;
        }
        //关闭音乐播放
        playerManager.stop();

    }

    /**
     * 处理延时关机逻辑
     *
     * @param context
     * @param type
     */
    public static void showDelayCloseDialog(Context context, DelayCloseDialog.DELAY_TYPE type) {
        int delayTime = PreferenceUtil.getDelayTime(context);
        boolean delayShutDown = PreferenceUtil.getDelayShutSwitch(context);

        int work_state = DeviceReportManager.getInstance().work_state;
        //只有在延时关机时间大于0并且风机工作中时，才开启延时关机
        if (delayTime > 0 && work_state != WORK_INIT && delayShutDown) {
            //测试提的bug#250，直接关灯
            int lamp = PreferenceUtil.getLamp(context);
            if (lamp > 0) {
                DeviceControl.getInstance().setLamp(DeviceControl.LAMP_CLOSE, false);
            }

            if (mDelayCloseDialog != null && mDelayCloseDialog.isShowing()) {
                mDelayCloseDialog.dismiss();
            }
            //延时关机对话框 (只有TYPE_SYSTEM_ALERT，才可以使用全局 context)
            mDelayCloseDialog = new DelayCloseDialog(context, type);
            mDelayCloseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    EventBus.getDefault().post(new DelayCloseEvent(false));
                    Log.e("coiliaspp", "post delay event false");
                }
            });
            mDelayCloseDialog.show();
            EventBus.getDefault().post(new DelayCloseEvent(true));
            Log.e("coiliaspp", "post delay event true");
        }
        //无延时关机
        else {
            //用户点击电源键
            if (type == DelayCloseDialog.DELAY_TYPE.DELAY_TYPE_POWER) {
//                closeAllFunction(context);
                //修改为和岛式机逻辑一致（不关闭音乐播放）
                closeAllFunctionButMusic(context);
                ScreenTool.getInstance().addResstNowData();
            }
            //用户点击关闭功能
            if (type == DelayCloseDialog.DELAY_TYPE.DELAY_TYPE_CLOSE) {
                DeviceControl.getInstance().closeDevice(true);
            }
            //用户点击灶具关火
            if (type == DelayCloseDialog.DELAY_TYPE.DELAY_TYPE_STOVE) {
                DeviceControl.getInstance().closeDevice(true);
            }
        }
    }

    /**
     * 取消延时关机
     */
    public static void cancelDelayClose() {
        Log.e("coiliaspp", "mDelayCloseDialog: " + mDelayCloseDialog + ", " +
                           (mDelayCloseDialog == null ? null : mDelayCloseDialog.isShowing()));
        if (mDelayCloseDialog != null && mDelayCloseDialog.isShowing()) {
            mDelayCloseDialog.dismiss();
            mDelayCloseDialog = null;
        }
    }

    /**
     * 升级
     */
    public static void upgrade(Context context, String path) {
        //        //apk放到/mnt/sdcard/目录下
        //        Intent intent = new Intent();
        //        intent.setAction("com.ynh.update_apk");
        //        intent.putExtra("apkname", FILE_OTA_NAME); //abc.apk is your apk name
        //        intent.putExtra("packagename", context.getPackageName()); //com.xxx.xxx is the package name of your
        // apk
        //        intent.putExtra("activityname", GuideLoadingActivity.class.getName()); // com.xxx.xxx.xxx is the
        // first activity
        //        // name of
        //        // your  apk
        //        context.sendBroadcast(intent);

        BottomView.getInstance(context).hide();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 是否在屏保时钟界面
     *
     * @return
     */
    public static boolean isAwaitView() {
        Activity activity = AppManagerUtil.getInstance().currentActivity();
        if (null != activity) {
            return activity instanceof BaseClockActivity;
        }
        return false;
    }

    /**
     * 删除超过30天的消息
     */
    public static void deleteOverdayMessage(Context context) {
        MessageDataBaseUtil.getInstance().deleteOverdayMemorandum();
        MessageDataBaseUtil.getInstance().deleteOverdayNotification();
        List<MemorandumDb> memorandumDbList = MessageDataBaseUtil.getInstance().queryAllMemorandum();
        List<NotificationDb> notificationDbList = MessageDataBaseUtil.getInstance().queryAllNotification();
        if (null == memorandumDbList || (null != memorandumDbList && memorandumDbList.size() == 0)) {
            PreferenceUtil.setHasUnreadMemorandum(context, false);
        }

        if (null == notificationDbList || (null != notificationDbList && notificationDbList.size() == 0)) {
            PreferenceUtil.setHasUnreadNotification(context, false);
        }
        if (isSpecifiedPage("QuickToolActivity")) {
            //((QuickToolActivity) AppManagerUtil.getInstance().currentActivity()).updateRedShow();
        }
    }

    /**
     * 显示锁屏
     */

    public static void showLockScreen(Context context, QuickToolActivity.LockScreenListener lockScreenListener) {
        //        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        //        //获取WindowManagerImpl.CompatModeWrapper
        //        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //        //设置window type
        //        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        //        //设置图片格式，效果为背景透明
        //        layoutParams.format = PixelFormat.RGBA_8888;
        //        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams
        //                .FLAG_NOT_FOCUSABLE;
        //        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        //
        //        // 以屏幕左上角为原点，设置x、y初始值
        //        layoutParams.x = 0;
        //        layoutParams.y = 0;
        //
        //        //设置悬浮窗口长宽数据
        //        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        //
        //        //获取浮动窗口视图所在布局
        //        mLockScreenView = new LockScreenView(context, lockScreenListener);
        //        //添加
        //        windowManager.addView(mLockScreenView, layoutParams);
        LockScreenDialog lockScreenDialog = new LockScreenDialog(context);
        lockScreenDialog.show();
        Log.e("lock", "set lock screen true");
    }

    public static void removeLockScreen(Context context) {
        EventBus.getDefault().post(new FinishActivityMessage(LockScreenDialog.class));
    }

    /**
     * 1--打开视频
     * 0--进入后台
     * -1--关闭视频
     *
     * @param state
     */
    public static void setVideoState(int state, Context context) {
        String pkg = "com.qiyi.video.pad";
        String act = "com.qiyi.video.pad.WelcomeActivity";

        if (state == 1) {
            try {
                DeviceReportManager.getInstance().is_video_show = true;
                if (AppUtil.isMusicPlaying(context)) {
                    AppUtil.stopPlayMusic(context);
                }
                //显示底栏和向下箭头
                BottomView.getInstance(context).show();
                BottomView.getInstance(context).setBottomMainBg(R.mipmap.bg_bottom);
                BottomViewArrowDown.getInstance(context).show();
                BottomViewArrowUp.getInstance(context).hide();
                //显示底栏返回键和home键（由于主动打开是在首页，首页没有显示返回按钮和home按钮）
                BottomView.getInstance(context).setHomeBtnShow(true);
                BottomView.getInstance(context).setBackBtnShow(true);

                try {
                    PackageManager packageManager = context.getPackageManager();
                    Intent intent = packageManager.getLaunchIntentForPackage(pkg);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "爱奇艺未安装！", Toast.LENGTH_LONG).show();
                }
                sendVideoChangedBroadcast(context);
            } catch (ActivityNotFoundException e) {

            }
        }

        //关闭视频
        if (state == -1 || state == 0) {
            DeviceReportManager.getInstance().is_video_show = false;
            //隐藏向上\向下箭头
            BottomViewArrowDown.getInstance(context).hide();
            BottomViewArrowUp.getInstance(context).hide();

            // MultiWindowUtils.closeFloatWindow();
            sendVideoChangedBroadcast(context);
            killProjectionProcess(context, pkg);
        }

    }

    /**
     * 杀死指定的程序
     *
     * @param context
     */
    public static void killProjectionProcess(Context context, String pkgName) {
        try {
            //强制杀死进程，防止自启动
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(am, pkgName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送在线视频状态改变的广播
     */
    public static void sendVideoChangedBroadcast(Context context) {
        Intent intent = new Intent(Constant.ACTION_VIDEO_STATUS_CHANGED);
        context.sendBroadcast(intent);
    }

    /**
     * 判断是否为指定的页面
     *
     * @param activityName
     * @return
     */
    public static boolean isSpecifiedPage(String activityName) {
        return AppManagerUtil.getInstance().currentActivity().getClass().getSimpleName().equals(activityName);
    }

    /**
     * 设置系统亮度
     *
     * @param context
     * @param light
     */
    public static void setSysLight(Context context, int light) {
        ContentResolver contentResolver = context.getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, light);
    }

    /**
     * 设置不操作屏幕，系统息屏时间
     *
     * @param context
     * @param now     是否立马不操作进入息屏
     *                false 标识常亮
     */
    public static void setSleepTime(Context context, boolean now) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) != PackageManager
                .PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //最大100小时不息屏
        int time = now ? 1000 : 100 * 60 * 60 * 1000;
        ScreenOffManager.setSystemOffTime(context, time);
    }

    /**
     * 启动其他应用
     *
     * @param context     Context
     * @param packageName 应用包名
     */
    public static boolean startOtherApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent;
        try {
            intent = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void systemUpdate(Activity activity) {
        if (isAppInstalled(activity, "com.softwinner.autoupdate")) {
            //暂停屏保
            ScreenTool.getInstance().addPause();
            //结束设备控制
            DeviceControl.getInstance().closeDevice(false);
            //隐藏底栏\顶栏
            BottomView.getInstance(activity).hide();
            TopBar.getInstance(activity).hide();

            Intent mIntent = new Intent("softwinner.intent.action.autoupdate");
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            mIntent.putExtra("file", FILE_ABSOLUTE_OTA);
            activity.startActivity(mIntent);
            LogUtil.LOGOta("ota文件路径", FILE_ABSOLUTE_OTA);
        }
    }

    /**
     * 判断是否安装了app
     *
     * @param context     上下文
     * @param packageName app的包名
     * @return 是否安装
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }
}
