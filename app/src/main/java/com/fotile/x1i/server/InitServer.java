package com.fotile.x1i.server;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.drm.DrmStore;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.bigkoo.pickerview.lib.WheelView;
import com.fotile.common.bean.EnginBean;
import com.fotile.common.bean.EnginType;
import com.fotile.common.util.CommonConstant;
import com.fotile.common.util.EnginUtil;
import com.fotile.common.util.FileUtil;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.message.util.MessageDataBaseUtil;
import com.fotile.recipe.uitl.db.DataFavoriteUtil;
import com.fotile.recipe.uitl.db.DataLocalBaseUtil;
import com.fotile.recipe.uitl.db.DataNetBaseUtil;
import com.fotile.voice.CommonConst;
import com.fotile.voice.NetConfigBusiness;
import com.fotile.x1i.bean.event.WifiConnection;
import com.fotile.x1i.receiver.PowerReceiver;
import com.fotile.x1i.server.blemusic.BLELinkObserverable;
import com.fotile.x1i.server.wifilink.LinkAction;
import com.fotile.x1i.server.wifilink.LinkObserverable;
import com.fotile.x1i.server.wifilink.LinkServer;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.server.smokelink.SmokeStoveLinkAction;
import com.fotile.x1i.manager.SpeechManager;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.util.Constant;
import com.fotile.x1i.util.CrashHandler;
import com.fotile.x1i.util.StatusBarUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;

import fotile.device.cookerprotocollib.helper.DeviceControl;

import static com.fotile.common.util.PreferenceUtil.OTA_AVAILABLE;

/**
 * @author： yhx
 * @data： 2019/4/15 15:00
 * @company： Fotile 智能厨电研究院
 * @description： 系统初始化服务
 */
public class InitServer {

    /**
     * mac地址，默认000000000000
     */
    private String macAddress = "000000000000";
    /**
     * 获取mac地址次数
     */
    private int macCount = 0;
    /**
     * 设备上线消息
     */
    private static final int MESSAGE_DEVICE_LINK = 2;
    /**
     * 让中间层指向线上环境
     */
    private static final int MESSAGE_ENGIN_ONLINE = 3;
    /**
     * 初始化jni
     */
    private static final int MESSAGE_INIT_JNI = 5;
    /**
     * 首次开机时，需要调用的设备关机接口
     */
    private static final int MESSAGE_INIT_CLOSE = 6;
    /**
     * 更新盒子供电模块状态
     */
    private static final int MESSAGE_UPDATE_BOX_POWER_STATE = 7;
    /**
     * 更新盒子供电模块状态
     */
    private static final int MESSAGE_PROBE_BOX = 8;
    /**
     * 执行静默安装
     */
    private static final int MESSAGE_SILENCE_INSTALL = 9;

    private Context context;

    /**
     * 用于wifi状态改变的filter
     */
    private IntentFilter filter;

    /**
     * 上次的网络状态
     */
    private NetworkInfo.State mLastState = null;

    //电源键上报短广播
    private PowerReceiver powerReceiver;


    public void init(Context context) {
        this.context = context;
        setBuildTypesField();
        //addWindowManager();
        initDefaultValue();
        initData();
        initService();
        handleReleaseAndDebug();
        //初始化数据库相关
        initDbData();
        copyAsset();
    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //静默安装
                case MESSAGE_SILENCE_INSTALL:

                    break;
                //初始化jni
                case MESSAGE_INIT_JNI:
                    DeviceReportManager.getInstance().initDeviceJni(macAddress);
                    break;
                //开机调用设备接口
                case MESSAGE_INIT_CLOSE:
                    DeviceControl.getInstance().closeDevice(false);
                    break;
                //让中间层指向线上环境
                case MESSAGE_ENGIN_ONLINE:
                    try {
                        DeviceControl.getInstance().setHostNameOrIp(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                //程序启动时，就让设备上线
                case MESSAGE_DEVICE_LINK:
                    try {
                        //程序启动时，就让设备上线
                        DeviceControl.getInstance().setXlinkServer(1);
                        //设置和大厨管家的联动
                        DeviceControl.getInstance().setDeviceLink(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MESSAGE_UPDATE_BOX_POWER_STATE:
                    // TODO: 2019/7/17 增加盒子上电接口
//                    DeviceControl.getInstance().setVboxPower(PreferenceUtil.getBoxPowerState(context) ? "on" : "off");
                    break;
                case MESSAGE_PROBE_BOX:
                    Log.e("NetConfigBusiness", "probe box when connected");
                    SpeechManager.getInstance().probeBox();
                    break;
            }
            return false;
        }
    });

    /**
     * 初始化一些默认值
     */
    private void initDefaultValue() {
        //initContext
        DeviceReportManager.getInstance().initContext(context);
        //        StoveAction.getInstance().initContext(this);
        SmokeStoveLinkAction.getInstance().initContext(context);
        DeviceControl.getInstance().initContext(context);

        //初始化一些默认的值
        DeviceReportManager.getInstance().setDefault(context);
        //每一次开机将故障提示设置为true
        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.FAULT_TIP, true);
        //每一次开机，如果上一次选择的是稍后提示，则更新为该次开机正常清洗
        if (PreferenceUtil.getRuntimeTip(context) == PreferenceUtil.RUN_CLEAN_TIP_NEXT) {
            PreferenceUtil.setRunTimeTip(context, PreferenceUtil.RUN_CLEAN_TIP_NORMAL);
        }
        //开机默认不显示ota可更新
        PreferenceUtil.setPreferenceValue(context, OTA_AVAILABLE, false);
    }

    /**
     * 启动一些app需要使用到的全局服务
     */
    private void initService() {
        //启动蓝牙服务-初始化一些变量
        Intent intent = new Intent(context, BleServer.class);
        context.startService(intent);
        //开机开启蓝牙
        BluetoothAdapter.getDefaultAdapter().enable();
        //烟灶联动服务
        Intent intent1 = new Intent(context, LinkServer.class);
        context.startService(intent1);
        //注册电源键上报短按广播
        powerReceiver = new PowerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.action.POWERKEY_TOUCH");
        context.registerReceiver(powerReceiver,filter);

    }

    private void initData() {
        EventBus.getDefault().register(this);
        //创建LinkAction对象，为了创建handler,不然会在LinkObserverable中子线程创建该对象
        LinkAction.getInstance(context);
        //开机时，先关闭灶具wifi连接，防止出现连接错误
        LinkObserverable.getInstance(context).disConnection();
        //默认打开wifi
        Tool.setWifiEnable(context, true);
        //初始化蓝牙模块
//        BLELinkObserverable.getInstance().init();
        //开发过程中该值是true
        if (CommonConstant.BUGLY_LOG_UN_REPORT) {
            //每次开机设置最大亮度
            Tool.setMaxBrightLight(context);
        }
        //开机打开蓝牙
//        BluetoothAdapter.getDefaultAdapter().enable();
        //隐藏状态栏
        StatusBarUtils.hideStatusBar(context);
        //设置屏幕10小时常亮
        AppUtil.setSleepTime(context, false);
        Tool.lightScreen(context);
        //创建项目的缓存文件存放目录
        FileUtil.createFolder(Constant.FILE_CACHE_FOLDER);
        //获取mac地址
        new Thread(macRunnable).start();
        //初始化jni
        handler.sendEmptyMessageDelayed(MESSAGE_INIT_JNI, 1000);
        //让设备上线
        handler.sendEmptyMessageDelayed(MESSAGE_DEVICE_LINK, 2000);
        //让设备处于关机状态
        handler.sendEmptyMessageDelayed(MESSAGE_INIT_CLOSE, 6000);
        //reset工程模式
        boolean reset = EnginUtil.resetEnginBean(Constant.FACTORY_VERSION_CODE);
        if (reset) {
            //云平台指向线上环境，延时等待完成jni初始化
            handler.sendEmptyMessageDelayed(MESSAGE_ENGIN_ONLINE, 3000);
        }

        //根据sp值开启/关闭盒子供电
        handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_BOX_POWER_STATE, 4000);
    }

    /**
     * 初始化数据库相关
     */
    private void initDbData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataLocalBaseUtil.getInstance().init(context);
                //初始化菜谱网络数据库
                DataNetBaseUtil.getInstance().init(context);
                //初始化菜谱收藏（我的上传）数据库
                DataFavoriteUtil.getInstance().init(context);
                //初始化消息数据库
                MessageDataBaseUtil.getInstance().init(context);
            }
        }).start();
    }

    /**
     * 将爱奇艺文件拷贝到sdcard
     */
    private void copyAsset() {
        String folder = Constant.FILE_CACHE_FOLDER + Constant.FILE_CACHE_FOLDER_AQY;
        final String fileName = "aiqiyi.apk";
        //创建文件夹
        FileUtil.createFolder(folder);
        final File file = new File(folder + "/" + fileName);

        //判断aiqiyi是否安装
        if(AppUtil.isAppInstalled(context, Constant.IQIYI_PACKAGE_NAME)){
            LogUtil.LOGE("================爱奇艺已安装", Constant.IQIYI_PACKAGE_NAME);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!file.exists()) {
                    //执行拷贝
                    try {
                        file.createNewFile();
                        AssetManager assetManager = context.getAssets();
                        InputStream is = assetManager.open(fileName);
                        if (is != null) {
                            OutputStream os = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = is.read(buffer)) > 0) {
                                os.write(buffer, 0, len);
                            }
                            os.flush();
                            os.close();
                            is.close();
                            //静默安装
                            //handler.sendEmptyMessage(MESSAGE_SILENCE_INSTALL);
                            installApp();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //本地已经存在aiqiyi.apk
                else {
                    //静默安装
                    //handler.sendEmptyMessage(MESSAGE_SILENCE_INSTALL);
                    installApp();
                }
            }
        }).start();
    }

    public static boolean installApp() {
        String folder = Constant.FILE_CACHE_FOLDER + Constant.FILE_CACHE_FOLDER_AQY;
        String fileName = "/aiqiyi.apk";
        String apkPath = folder + fileName;

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
//            process = new ProcessBuilder("pm", "install","-i","com.qiyi.video.pad", "-r", apkPath).start();
            process = new ProcessBuilder("pm", "install", "-r", apkPath).start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {

            }
            if (process != null) {
                process.destroy();
            }
        }
        LogUtil.LOGE("=============爱奇艺静默安装", "successMsg:" + successMsg);
        //如果含有“success”单词则认为安装成功
        return successMsg.toString().equalsIgnoreCase("success");
    }

    @Subscribe
    public void onEventSpeechReady(String msg) {
        if (TextUtils.equals(CommonConst.SPEECH_READY, msg)) {
            initVBoxListener();
        }
    }


    /**
     * 监听网络状态，联网后发出探测包
     * 监听homeId改变
     */
    private void initVBoxListener() {
        initWifiStateIntentFilter();
        registerWifiStateBroadcast();
    }

    /**
     * 设置buildTypes中配置的字段
     */
    private void setBuildTypesField() {
        //初始化一些测试时使用到的字段值
        try {
            String packageName = context.getPackageName();
            Class buildConfig = Class.forName(packageName + ".BuildConfig");

            Field bugly_log_un_report = buildConfig.getField("bugly_log_un_report");
            CommonConstant.BUGLY_LOG_UN_REPORT = bugly_log_un_report.getBoolean(false);

            Field is_screen_test = buildConfig.getField("is_screen_test");
            CommonConstant.IS_SCREEN_TEST = is_screen_test.getBoolean(false);

            Field log_and_crash_show = buildConfig.getField("log_and_crash_show");
            CommonConstant.LOG_AND_CRASH_SHOW = log_and_crash_show.getBoolean(false);

            LogUtil.isDebug = CommonConstant.LOG_AND_CRASH_SHOW ? true : EnginUtil.isDebug();

//            Log.e("======buildTypes bugly_log_invaild", CommonConstant.BUGLY_LOG_UN_REPORT + "");
//            Log.e("======buildTypes is_screen_test", CommonConstant.IS_SCREEN_TEST + "");
//            Log.e("======buildTypes log_and_crash_show", CommonConstant.LOG_AND_CRASH_SHOW + "");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Runnable macRunnable = new Runnable() {
        @Override
        public void run() {
            while ("000000000000".equals(macAddress)) {
                //获取mac地址
                macAddress = Tool.getLocalMacAddress();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogUtil.LOGE("===================macAddress", macAddress);
                macCount++;
                //最多获取十次
                if (macCount >= 3) {
                    break;
                }
            }
        }
    };

    private void handleReleaseAndDebug() {
//        //开发调试过程中，不上传bugly错误日志
//        if (!CommonConstant.BUGLY_LOG_UN_REPORT) {
        EnginBean enginBean = EnginUtil.getEnginBean();
        //relase模式-crash日志被CrashHandler捕获
        if (enginBean.pack_mode == EnginType.ENGIN_PACK_RELASE) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(context);
            //关闭中间层log
            DeviceControl.getInstance().setPrintLevel(3);
        }
        //debug模式--crash日志被bugly捕获
        else {
            // 获取当前包名
            String packageName = context.getPackageName();
            // 获取当前进程名
            String processName = Tool.getProcessName(context);
            // 设置是否为上报进程
            CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
            strategy.setUploadProcess(processName == null || processName.equals(packageName));
            // 初始化Bugly
            CrashReport.initCrashReport(context, "5b4c8ff1f0", false, strategy);
            //打开中间层log
            DeviceControl.getInstance().setPrintLevel(0);
        }
//        }
    }

    /**
     * 初始化filter
     */
    private void initWifiStateIntentFilter() {
        filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    }

    /**
     * 注册广播接收器
     */
    private void registerWifiStateBroadcast() {
        context.registerReceiver(mReceiver, filter);
    }

    /**
     * 注销广播接收器
     */
    private void unregisterWifiStateBroadcast() {
        context.unregisterReceiver(mReceiver);
    }

    /**
     * 广播接收，监听网络
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            handleEvent(intent);
        }
    };

    /**
     * 处理事件
     *
     * @param intent
     */
    private void handleEvent(Intent intent) {
        final String action = intent.getAction();
        if ((WifiManager.NETWORK_STATE_CHANGED_ACTION).equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.State state = networkInfo.getState();
            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            Log.e("NetConfigBusiness_wifi", "internet intent: " + intent.toString() + ", action: " + intent.getAction
                    () + ", state:" + state);
            if (wifiInfo != null && state != mLastState) {
                switch (state) {
                    case CONNECTED://连上网络
                        EventBus.getDefault().post(new WifiConnection(true));
                        Log.e("NetConfigBusiness", "SpeechManager.getInstance().isConnected(): " + SpeechManager
                                .getInstance().isConnected() + PreferenceUtil.isBoxMsgReceive(context));
                        if (!SpeechManager.getInstance().isConnected() && PreferenceUtil.isBoxMsgReceive(context)) {
                            Log.e("NetConfigBusiness", "start send probe ");
                            handler.sendEmptyMessageDelayed(MESSAGE_PROBE_BOX, 1000);
                        }
                        break;
                    case DISCONNECTED://断开网络
                        NetConfigBusiness.getInstance().restoreState(false);
                        EventBus.getDefault().post(new WifiConnection(false));
                        break;
                    default:
                        break;
                }
            }
            mLastState = state;
        }
    }

}
