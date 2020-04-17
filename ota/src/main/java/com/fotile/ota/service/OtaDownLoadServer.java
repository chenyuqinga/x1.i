package com.fotile.ota.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.dl7.downloaderlib.DownloadConfig;
import com.dl7.downloaderlib.DownloadListener;
import com.dl7.downloaderlib.FileDownloader;
import com.dl7.downloaderlib.entity.FileInfo;
import com.dl7.downloaderlib.model.DownloadStatus;
import com.fotile.common.util.FileUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.ota.bean.MyFileInfo;
import com.fotile.ota.bean.OtaData;
import com.fotile.ota.bean.UpgradeInfo;
import com.fotile.ota.util.OtaConstant;
import com.fotile.ota.util.OtaUpgradeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 文件名称：OtaDownLoadServer
 * 创建时间：2017/12/25 14:47
 * 文件作者：yaohx
 * 功能描述：固件包下载服务
 */
public class OtaDownLoadServer extends Service {
    /**
     * 固件包的保存目录
     */
    private String file_folder = OtaConstant.FILE_FOLDER;
    /**
     * 系统固件包文件完整路径名
     */
    public final String file_name_sys = OtaConstant.FILE_ABSOLUTE_OTA;
    /**
     * 文件下载地址
     */
    private String url_sys;
    /**
     * 文件校验md5
     */
    private String md5_sys;
    /**
     * 当前下载状态
     */
    private static int state = DownloadStatus.NORMAL;
    /**
     * 记录上一次进度百分比
     */
    private float last_progress = 0;
    /**
     * 前后两次刷新进度差值的最小值
     */
    private static final float INTERVAL = 0.005f;

    private UpgradeInfo upgradeInfo;
    /**
     * 标识下载入口是否来自后台自动下载服务
     */
    private boolean autoDownload;

    private Timer timer_net;
    private int retry_time = 0;
    /**
     * 开始下载
     */
    final int WHAT_START_DOWNLOAD = 1001;
    /**
     * 报错计时
     */
    final int WHAT_ERROR_TIMER = 1002;

    private boolean isError = true;

    private ListenerWrapper listenerWrapper;

    @Override
    public void onCreate() {
        super.onCreate();
        listenerWrapper = new ListenerWrapper();
    }

    public int getDownLoadState() {
        return state;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 开始下载文件
     *
     * @param upgradeInfo
     * @param autoDownload 是否来自后台自动下载
     */
    public void startDownload(UpgradeInfo upgradeInfo, boolean autoDownload) {
        this.autoDownload = autoDownload;
        this.upgradeInfo = upgradeInfo;
        //创建文件下载目录
        initData();

        //下载ota包
        if (!TextUtils.isEmpty(url_sys)) {
            startSysDownload();
        }
    }

    /**
     * 初始化并且创建文件保存目录
     */
    private void initData() {
        url_sys = upgradeInfo.url;
        md5_sys = upgradeInfo.md5;

        FileDownloader.init(this);
        DownloadConfig config = new DownloadConfig.Builder().setDownloadDir(file_folder).build();
        FileDownloader.setConfig(config);

        File tmpFile = new File(file_folder);
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
    }

    /**
     * 开始下载固件包
     */
    private void startSysDownload() {
        if (!TextUtils.isEmpty(url_sys)) {
            handler.sendEmptyMessage(WHAT_START_DOWNLOAD);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                //将下载任务放到主线程中操作
                case WHAT_START_DOWNLOAD:
                    state = DownloadStatus.NORMAL;
                    LogUtil.LOGOta("下载固件包url", url_sys);
                    LogUtil.LOGOta("下载固件包保存的本地路径", file_name_sys);
                    //开始下载
                    FileDownloader.start(url_sys, OtaConstant.FILE_OTA_NAME, new ListenerWrapper());
                    break;
                case WHAT_ERROR_TIMER:
                    isError = true;
                    break;
            }
            return false;
        }
    });

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!TextUtils.isEmpty(url_sys)) {
            FileDownloader.cancel(url_sys);
        }
        state = DownloadStatus.NORMAL;
        EventBus.getDefault().unregister(this);
    }

    /**
     * 监听器封装类-执行在子线程中
     */
    class ListenerWrapper implements DownloadListener {

        @Override
        public void onStart(FileInfo fileInfo) {
            last_progress = 0f;
            state = DownloadStatus.START;
            EventBus.getDefault().post(new MyFileInfo(fileInfo,true));
            LogUtil.LOGOta("OtaDownLoadServer-文件下载", "下载开始");
        }

        @Override
        public void onUpdate(FileInfo fileInfo) {
            //当前下载进度
            float progress = OtaUpgradeUtil.getProgress(fileInfo.getLoadBytes(), fileInfo.getTotalBytes());
            if (Math.abs(progress - last_progress) > INTERVAL) {
                last_progress = progress;
                state = DownloadStatus.DOWNLOADING;
                EventBus.getDefault().post(new MyFileInfo(fileInfo,true));
                LogUtil.LOGOta("OtaDownLoadServer-文件下载", "下载中:" + progress);
            } else {
//                String current_act_name = OtaTool.getCurrentActivityName(OtaDownLoadServer.this);
//                //程序在后台下载
//                if (!current_act_name.contains("SettingActivity") && !show_downing_tip) {
//                    Message msg = uiHandler.obtainMessage();
//                    msg.obj = fileInfo;
//                    uiHandler.sendMessage(msg);
//                    last_progress = progress;
//                }
            }
        }

        @Override
        public synchronized void  onError(FileInfo fileInfo, String error) {
            LogUtil.LOGOta("OtaDownLoadServer-文件下载", "onError:" + error);
            handler.sendEmptyMessageDelayed(WHAT_ERROR_TIMER, 20000);
            if (isError) {
                isError = false;
             /*
             * 这里需要延时1秒
             *
             * 网络断开的时候不会立马检测到，所以要延迟
             */
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                state = DownloadStatus.ERROR;
                EventBus.getDefault().post(new MyFileInfo(fileInfo,true));
                retry();
            }
        }

        @Override
        public void onComplete(FileInfo fileInfo) {
            last_progress = 100f;
            state = DownloadStatus.COMPLETE;

            //停止自动检测ota后台下载的服务
            Intent intent = new Intent(OtaDownLoadServer.this, OtaAutoDownloadServer.class);
            stopService(intent);

            //校验成功
            if(OtaUpgradeUtil.exitOtaFile(upgradeInfo)){
                EventBus.getDefault().post(new MyFileInfo(fileInfo,true));

                //下载完成更新对应的版本号
                OtaData otaData = OtaUpgradeUtil.getOtaData();
                otaData.downloaded_version = upgradeInfo.version;
                OtaUpgradeUtil.updateOtaData(otaData);
                LogUtil.LOGOta("OtaDownLoadServer-文件下载", "onComplete md5校验成功");
            }
            else {
                EventBus.getDefault().post(new MyFileInfo(fileInfo,false));
                LogUtil.LOGOta("OtaDownLoadServer-文件下载", "onComplete md5校验失败，删除固件包");
                //校验失败 删除ota固件包
                FileUtil.deleteFile(OtaConstant.FILE_ABSOLUTE_OTA);
            }
        }

        @Override
        public void onCancel(FileInfo fileInfo) {
            last_progress = 0f;
            state = DownloadStatus.CANCEL;
            EventBus.getDefault().post(new MyFileInfo(fileInfo,true));
        }

        @Override
        public void onStop(FileInfo fileInfo) {
            state = DownloadStatus.STOP;
            EventBus.getDefault().post(new MyFileInfo(fileInfo,true));
        }
    }

    /**
     * 该方法属于子线程
     */
    private void retry() {
        cancelNetTimer();
        //网络造成的下载失败
        if (!Tool.isNetworkAvailable(this)) {
            timer_net = new Timer();
            timer_net.schedule(new TimerTask() {
                @Override
                public void run() {
                    //网络连接时，自动下载
                    if (Tool.isNetworkAvailable(OtaDownLoadServer.this)) {
                        startSysDownload();
                        cancelNetTimer();
                    } else {
                        retry_time++;
                    }
                    if (retry_time >= 120) {
                        cancelNetTimer();
                    }
                    LogUtil.LOGOta("下载失败重试", retry_time);
                }
            }, 1000, 1000);
        } else {
            startSysDownload();
        }
    }

    public synchronized void cancelNetTimer() {
        if (null != timer_net) {
            timer_net.cancel();
            timer_net.purge();
            timer_net = null;
            retry_time = 0;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new OtaBinder();
    }

    public class OtaBinder extends Binder {
        public OtaDownLoadServer getServer() {
            return OtaDownLoadServer.this;
        }
    }
}
