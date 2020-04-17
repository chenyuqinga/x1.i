package com.fotile.ota.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.dl7.downloaderlib.entity.FileInfo;
import com.dl7.downloaderlib.model.DownloadStatus;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.ota.bean.UpgradeInfo;
import com.fotile.ota.util.OtaConstant;
import com.fotile.ota.util.OtaUpgradeUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.fotile.ota.util.OtaConstant.OTA_URL_TEST;

/**
 * 项目名称：Ota
 * 创建时间：2018/11/5 16:00
 * 文件作者：yaohx
 * 功能描述：后台自动ota下载固件包服务
 * 该服务需要在开机时启动
 */
public class OtaAutoDownloadServer extends Service {

    private Timer timer = null;
    /**
     * 分钟
     */
    private long MINUTE = 1 * 60 * 1000;
    /**
     * 小时
     */
    private long HOUR = 1 * 60 * MINUTE;
    private UpgradeInfo upgradeInfo;

    @Override
    public void onCreate() {
        super.onCreate();
//        initData();
    }

    /**
     * 每天执行一次定时任务
     */
    private void initData() {
        EventBus.getDefault().register(this);
        long delay = OTA_URL_TEST ? 20000 : MINUTE;
        long periol = OTA_URL_TEST ? MINUTE : HOUR;
        //定时器作为一个守护线程，生命周期依赖于系统
        timer = new Timer("OtaAutoDownloadServer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogUtil.LOGOta("-----------------------------------------------------------后台监测", "start");
                long current_time = System.currentTimeMillis();
                Date date = new Date();

                try {
                    //获取当天日期
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    //允许下载时间起点
                    String time_str_start = df.format(date) + " 01:00:00";
                    //允许下载时间终点
                    String time_str_end = df.format(date) + " 02:00:00";

                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //允许下载时间起点对应的时间戳
                    long time_start = df2.parse(time_str_start).getTime();
                    //允许下载时间终点对应的时间戳
                    long time_end = df2.parse(time_str_end).getTime();

                    String current_date = df2.format(date);
                    LogUtil.LOGOta("自动下载time_str_start", time_str_start + "====" + time_start);
                    LogUtil.LOGOta("自动下载 current_time", current_date + "====" + date.getTime());
                    LogUtil.LOGOta("自动下载 time_str_end", time_str_end + "====" + time_end);

                    boolean download = false;
                    //当前时间点大于允许下载时间起点并且小于允许下载时间终点
                    //确保每天只执行一次
//                    if ((current_time > time_start && current_time < time_end) || OTA_URL_TEST) {
                    if ((current_time > time_start && current_time < time_end)) {
                        download = true;
                    }

//                    boolean needDownload = needDownload();
//                    LogUtil.LOGOta("download",download);
//                    LogUtil.LOGOta("needDownload",needDownload);
//                    LogUtil.LOGOta("upgradeInfo", null != upgradeInfo);

                    if (download && needDownload() && null != upgradeInfo) {
                        Intent intent = new Intent(OtaAutoDownloadServer.this, OtaDownLoadServer.class);
                        intent.putExtra("upgradeInfo", upgradeInfo);
                        intent.putExtra("autoDownload","true");
                        startService(intent);
                        LogUtil.LOGOta("满足后台自动下载条件", "开始自动下载");
                    }else {
                        LogUtil.LOGOta("不满足后台自动下载条件", "继续监测");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                LogUtil.LOGOta("-----------------------------------------------------------后台监测", "end");
            }
        }, delay, periol);
    }

    //下载中进度回调，事件由DownLoadService中发出
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDownloadUpdate(FileInfo fileInfo) {
        if (null != fileInfo) {
            int state = fileInfo.getStatus();
            switch (state) {
                //开始
                case DownloadStatus.START:
//                    LogUtil.LOGOta("自动Ota-文件下载", "下载开始");
                    break;
                //下载中
                case DownloadStatus.DOWNLOADING:
//                    float progress = OtaUpgradeUtil.getProgress(fileInfo.getLoadBytes(), fileInfo.getTotalBytes());
//                    LogUtil.LOGOta("自动Ota-文件下载", "下载中：" + progress);
                    break;
                //完成（固件包或者mcu下载完成都会回调这里）
                case DownloadStatus.COMPLETE:
//                    LogUtil.LOGOta("自动Ota-文件下载", "下载完成");

                    break;
                //报错
                case DownloadStatus.ERROR:

                    break;
            }
        }
    }

    /**
     * 判断是否需要在后台下载固件包
     */
    private boolean needDownload() {
        boolean result = false;
        //用于匹配OTA服务器的包名
        String key = "ota_check_package";
        String check_package_name = Tool.getPkgMetaValue(this, key);
        //校验匹配版本号
        String check_version_code = Tool.getLocalSysVersion(this);
        //校验mac地址
        String check_mac_address = Tool.getLocalMacAddress();

        OtaUpgradeUtil otaUpgradeUtil = new OtaUpgradeUtil();
        String reqUrl = otaUpgradeUtil.buildUrl(check_package_name, check_version_code, check_mac_address, this);
        //测试使用
        if (OtaConstant.OTA_URL_TEST) {
            reqUrl = OtaConstant.test_url;
        }

        LogUtil.LOGOta("OtaAutoDownloadServer-请求Ota包信息url", reqUrl);
        String content = "";
        String miwen = "";
        String mingwen = "";
        try {
            content = otaUpgradeUtil.httpGet(reqUrl);
            //没有可更新的包
            if (!TextUtils.isEmpty(content) && !content.equals("{}")) {
                JSONObject jo = new JSONObject(content);
                miwen = jo.getString("message");
                mingwen = otaUpgradeUtil.Decrypt(miwen, OtaConstant.PASSWORD);
                LogUtil.LOGOta("OtaAutoDownloadServer-请求Ota包信息返回数据", mingwen);

                Gson parser = new Gson();
                upgradeInfo = parser.fromJson(mingwen, UpgradeInfo.class);
                //检测下载包是否在本地已经存在
                if (!OtaUpgradeUtil.exitOtaFile(upgradeInfo)) {
                    result = true;
                } else {
                    LogUtil.LOGOta("OtaAutoDownloadServer-", "请求Ota包本地已下载过了");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //当下载完成后，停止该服务
        if (null != timer) {
            timer.cancel();
            timer.purge();
            LogUtil.LOGOta("自动下载完成", "取消检测timer");
        }
    }
}
