package com.fotile.x1i.server.wifilink;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.widget.TopBar;

import java.util.Timer;
import java.util.TimerTask;

import rx.functions.Action1;

import static com.fotile.common.util.PreferenceUtil.STOVE_LINK_DEVICE_MAC;

public class LinkServer extends Service {

    private Context context;
    private Action1<StoveWifiDevice> searchAction;
    private Action1<StoveWifiDevice> linkAction;

    private final int beat_time = 10 * 1000;
    /**
     * 蓝牙心跳定时器
     */
    private Timer timer_beat = null;
    /**
     * 心跳包
     */
    byte[] beat_data = {(byte) 0xf4, (byte) 0xf5, 0x00, 0x08, 0x05, 0x00, 0x02, 0x01, 0x00, 0x00, 0x00, 0x0f};

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        createSearchAction();
        createLinkAction();
        initData();
    }

    private void initData() {
        WifiSearchObserverable.getInstance(context).addSearchAction(searchAction);
        LinkObserverable.getInstance(context).addLinkAction(linkAction);
    }

    private void createSearchAction() {
        searchAction = new Action1<StoveWifiDevice>() {
            @Override
            public void call(StoveWifiDevice wifiDevice) {
                if (null != wifiDevice) {
                    //如果搜索到上一次连接的设备
                    String lastLinkMac = (String) PreferenceUtil.getPreferenceValue(context, STOVE_LINK_DEVICE_MAC, "");
                    //搜索中
                    if (wifiDevice.searching) {
                        String mac = wifiDevice.mac;
                        //如果搜索到上一次连接的设备
                        if (!TextUtils.isEmpty(lastLinkMac) && !TextUtils.isEmpty(mac)) {
                            //两者相同，发起连接
                            if (mac.equals(lastLinkMac)) {
                                LogUtil.LOG_STOVE_LINK("连接到上一次的灶具wifi设备", mac);
                                LinkObserverable.getInstance(context).connect(wifiDevice);
                            }
                        }
                    }
                    //搜索结束
                    else {

                    }
                }
            }
        };
    }

    private void createLinkAction() {
        linkAction = new Action1<StoveWifiDevice>() {
            @Override
            public void call(StoveWifiDevice wifiDevice) {
                //只要状态上报即取消心跳包
                cancelBeatTimer();
                int state = wifiDevice.linkState;
                //连接中
                if (state == LinkObserverable.STATE_CONNECTING) {

                }
                //连接成功
                if (state == LinkObserverable.STATE_CONNECTED) {
                    LogUtil.LOG_STOVE_LINK("LinkServer-连接成功", wifiDevice);
                    //保存连接成功的设备mac
                    PreferenceUtil.setPreferenceValue(context, PreferenceUtil.STOVE_LINK_DEVICE_MAC, wifiDevice.mac);
                    //设备联动icon
                    TopBar.getInstance(context).updateLinkState(true);
                    //连接成功发送一个握手包
                    LinkAction.getInstance(context).addData(LinkAction.request);
                    LogUtil.LOG_STOVE_LINK("LinkServer-发送握手", Tool.getHexBinString(LinkAction.request));

                    //连接成功，发送心跳包
                    startBeatTimer();
                }
                //连接失败
                if (state == LinkObserverable.STATE_ERROR) {
                    LogUtil.LOG_STOVE_LINK("LinkServer-连接失败", "连接失败");
                    //设备联动icon
                    TopBar.getInstance(context).updateLinkState(false);
                }
                //连接关闭
                if (state == LinkObserverable.STATE_NONE) {
                    //设备联动icon
                    TopBar.getInstance(context).updateLinkState(false);
                }
            }
        };
    }

    /**
     * 开启心跳包
     */
    public synchronized void startBeatTimer() {
        cancelBeatTimer();
        timer_beat = new Timer();
        timer_beat.schedule(new TimerTask() {
            @Override
            public void run() {
                LinkAction.getInstance(context).addData(beat_data);
                LogUtil.LOG_STOVE_LINK("发给灶具的心跳包", Tool.getHexBinString(beat_data));
            }
        }, 800, beat_time);
    }

    /**
     * 取消心跳包
     */
    public synchronized void cancelBeatTimer() {
        if (null != timer_beat) {
            timer_beat.cancel();
            timer_beat.purge();
            timer_beat = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
