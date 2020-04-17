package com.fotile.x1i.server.wifilink;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 文件名称：WifiSearchObserverable
 * 创建时间：2019/7/15 11:35
 * 文件作者：yaohx
 * 功能描述：灶具wifi搜索处理类
 */
public class WifiSearchObserverable {
    /**
     * 使用端口号负责发送
     */
    private static final int SERVER_PORT = 8101;
    /**
     * 使用端口号负责接受
     */
    private static final int RECEIVER_PORT = 9100;

    private Context context;
    private static WifiSearchObserverable instance;
    /**
     * udp发送广播timer
     */
    private Timer udpSendTimer = null;

    final int WHAT_UDP_SEARCH_START = 1;
    final int WHAT_UDP_SEARCH_STOP = 2;
    /**
     * 搜索时长
     */
    private static final long SEARCH_TIME = 1 * 60 * 1000;
    /**
     * udp发送数据
     */
    private String msg;
    /**
     * 是否正在搜索
     */
    private boolean searching = false;

    CopyOnWriteArrayList<Action1<StoveWifiDevice>> list = new CopyOnWriteArrayList<Action1<StoveWifiDevice>>();

    private WifiSearchObserverable(Context context) {
        this.context = context;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd_type", "probe_device");
            jsonObject.put("version", Tool.getVersionName(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg = jsonObject.toString();
    }

    public static WifiSearchObserverable getInstance(Context context) {
        if (null == instance) {
            instance = new WifiSearchObserverable(context);
        }
        return instance;
    }

    /**
     * 开始搜索
     */
    public synchronized void startSearch() {
        stopSearch(false);

        udpSendTimer = new Timer();
        udpSendTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                send();
            }
        }, 800, 3 * 1000);
        LogUtil.LOG_STOVE_LINK("udp搜索开始", "----------------------------start----------------------------");

        handler.sendEmptyMessageDelayed(WHAT_UDP_SEARCH_STOP, SEARCH_TIME);
        searching = true;
        //开启线程监听搜索结果
        reviceUdpResult();
    }

    /**
     * 开启线程监听udp接受数据
     */
    private void reviceUdpResult() {
        try {
            final byte[] buffer = new byte[1024];
            final DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length);
            final DatagramSocket dSoket = new DatagramSocket(RECEIVER_PORT);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(100);
                            if (searching) {
                                dSoket.receive(dPacket);
                                String result = new String(dPacket.getData(), dPacket.getOffset(), dPacket.getLength());
                                if (!TextUtils.isEmpty(result)) {
                                    StoveWifiDevice wifiDevice = new StoveWifiDevice(result, searching);
                                    LogUtil.LOG_STOVE_LINK("udp接受数据-recv", wifiDevice);
                                    notifySearchData(wifiDevice);
                                }
                            } else {
                                //将Socket关闭，防止占用端口
                                dSoket.close();
                                break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止搜索
     *
     * @param notifyUi 是否需要通知ui
     */
    public void stopSearch(boolean notifyUi) {
        searching = false;
        cancelSendTimer();

        if (notifyUi) {
            handler.removeMessages(WHAT_UDP_SEARCH_STOP);
            //告诉订阅者，搜索结束
            notifySearchData(new StoveWifiDevice("", searching));
            LogUtil.LOG_STOVE_LINK("udp搜索结束", "----------------------------stop----------------------------");
        }
    }

    public void addSearchAction(Action1<StoveWifiDevice> action1) {
        if (null != action1 && !list.contains(action1)) {
            list.add(action1);
        }
    }

    public void removeSearchAction(Action1<StoveWifiDevice> action1) {
        if (null != action1) {
            list.remove(action1);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
//                case WHAT_UDP_SEARCH_START:
//                    break;
                //停止搜索
                case WHAT_UDP_SEARCH_STOP:
                    stopSearch(true);
                    break;
            }
            return false;
        }
    });

    private void cancelSendTimer() {
        if (null != udpSendTimer) {
            udpSendTimer.purge();
            udpSendTimer.cancel();
            udpSendTimer = null;
        }
    }

    /**
     * 将数据分发给订阅者
     *
     * @param wifiDevice
     */
    private void notifySearchData(StoveWifiDevice wifiDevice) {
        if (null != wifiDevice) {
            //创建一个被观察者对象
            Observable observable = Observable.just(wifiDevice);
            if (null != list && !list.isEmpty()) {
                for (Action1 action : list) {
                    observable.subscribeOn(Schedulers.immediate())//指定被观察者执行的线程
                            .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                            .subscribe(action);
                }
            }
        }
    }

    public void send() {
        DatagramSocket dSocket = null;
        InetAddress local = null;
        try {
            local = InetAddress.getByName(intIPToStringBroadcastIP(getIpAddress(context))); // 本机测试
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            dSocket = new DatagramSocket(); // 注意此处要先在配置文件里设置权限,否则会抛权限不足的异常
        } catch (SocketException e) {
            e.printStackTrace();
        }
        int msg_len = msg == null ? 0 : msg.length();
        DatagramPacket dPacket = new DatagramPacket(msg.getBytes(), msg_len, local, SERVER_PORT);
        try {
            dSocket.send(dPacket);
            LogUtil.LOG_STOVE_LINK("udp发送数据-send", msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dSocket.close();
    }

    /**
     * 获取ip地址
     *
     * @param context
     * @return
     */
    private static int getIpAddress(Context context) {
        int ipAddress = 0;
        ConnectivityManager systemService = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (systemService != null) {
            info = systemService.getActiveNetworkInfo();
        }
        if (info != null && info.isConnected()) {
            //当前使用无线网络
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context
                        .WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //得到IPV4地址
                //                ipAddress = intIPToStringIP(wifiInfo.getIpAddress());
                ipAddress = wifiInfo.getIpAddress();
            } else {

            }
        } else {

        }
        return ipAddress;
    }

    private static String intIPToStringBroadcastIP(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + ".255";
    }
}
