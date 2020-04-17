package com.fotile.ota.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.dl7.downloaderlib.DownloadConfig;
import com.dl7.downloaderlib.DownloadListener;
import com.dl7.downloaderlib.FileDownloader;
import com.dl7.downloaderlib.entity.FileInfo;
import com.dl7.downloaderlib.model.DownloadStatus;
import com.fotile.common.util.log.LogUtil;
import com.fotile.ota.util.OtaConstant;
import com.fotile.ota.util.OtaUpgradeUtil;

import java.io.File;

/**
 * 文件名称：OtaDownLoadServer
 * 创建时间：2017/12/25 14:47
 * 文件作者：yaohx
 * 功能描述：mcu下载服务
 * mcu包很小，可以不用考虑error重试的情况
 */
public class McuDownLoadServer extends Service {
    /**
     * mcu包的保存目录
     */
    private String file_folder = OtaConstant.FILE_FOLDER;
    /**
     * mcu包文件完整路径名
     */
    public static final String file_name_mcu = OtaConstant.FILE_ABSOLUTE_MCU;
    /**
     * mcu文件下载地址
     */
    private String url_mcu;
    /**
     * mcu文件校验md5
     */
    private String md5_mcu;
    /**
     * 当前下载状态
     */
    private static int state = DownloadStatus.NORMAL;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static int getDownLoadState() {
        return state;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent && null != intent.getExtras()) {
            //创建文件下载目录
            initData();
            url_mcu = intent.getExtras().getString("url_mcu");
            md5_mcu = intent.getExtras().getString("md5_mcu");
            startMcuDownload();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化并且创建文件保存目录
     */
    private void initData() {
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
    private void startMcuDownload() {
        if (!TextUtils.isEmpty(url_mcu)) {
            state = DownloadStatus.NORMAL;
            LogUtil.LOGOta("下载mcu包url", url_mcu);
            LogUtil.LOGOta("下载mcu包保存的本地路径", file_name_mcu);
            //开始下载
            FileDownloader.start(url_mcu, OtaConstant.FILE_MCU_NAME, new ListenerWrapper());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!TextUtils.isEmpty(url_mcu)) {
            FileDownloader.cancel(url_mcu);
        }
        state = DownloadStatus.NORMAL;
    }

    /**
     * 监听器封装类-执行在子线程中
     */
    class ListenerWrapper implements DownloadListener {

        @Override
        public void onStart(FileInfo fileInfo) {
            state = DownloadStatus.START;
        }

        @Override
        public void onUpdate(FileInfo fileInfo) {
            //当前下载进度
            float progress = OtaUpgradeUtil.getProgress(fileInfo.getLoadBytes(), fileInfo.getTotalBytes());

            state = DownloadStatus.DOWNLOADING;
        }

        @Override
        public void onStop(FileInfo fileInfo) {
            state = DownloadStatus.STOP;

        }

        @Override
        public void onComplete(FileInfo fileInfo) {
            state = DownloadStatus.COMPLETE;
            LogUtil.LOGOta("下载mcu包", "下载完成");
        }

        @Override
        public void onCancel(FileInfo fileInfo) {
            state = DownloadStatus.CANCEL;
        }

        @Override
        public void onError(FileInfo fileInfo, String s) {
//             /*
//             * 这里需要延时1秒
//             *
//             * 网络断开的时候不会立马检测到，所以要延迟
//             */
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            state = DownloadStatus.ERROR;
            LogUtil.LOGOta("下载mcu包", "onError" + s);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
