/*
 * ************************************************************
 * 文件：WifiUtil.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.voice.socket.bean.SelfNetInfo;
import com.fotile.voice.wifi.WifiAPManager;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;


/**
 * Created by melo on 2017/9/23.
 */

public class WifiUtil {

    private static final String TAG = WifiUtil.class.getSimpleName();

    private static volatile WifiUtil instance = null;

    private WifiManager mWifiManager;

    private Context mContext;
    private String mWifiname;
    private Handler mHandler;
    public static final int MSG_CONNECT_WIFI = 50003;
    public static final int MSG_CONNECT_WIFI_TIMEOUT = 50004;
    private boolean mIsWifiConnected;
    private WifiInfo mWifiinfo;
    private DhcpInfo mDhcpinfo;
    private String mWaitLinkWifiName;
    private String mWaitLinkWifiPwd;

    private WifiUtil(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(
                Context.WIFI_SERVICE);
        Log.e(TAG, "test...." + mWifiManager.getConfiguredNetworks());
    }

    public static WifiUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (WifiUtil.class) {
                if (instance == null) {
                    instance = new WifiUtil(context);
                }
            }
        }
        return instance;
    }

    public boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);

        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getLocalIPAddress() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return intToIp(wifiInfo.getIpAddress());
    }

    public String getServerIPAddress() {
        DhcpInfo mDhcpInfo = mWifiManager.getDhcpInfo();
        return intToIp(mDhcpInfo.gateway);
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        Log.d("IPs", inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

    public String getWifiApIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                         enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() &&
                            (inetAddress.getAddress().length == 4)) {
                            Log.d(TAG, inetAddress.getHostAddress());
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." +
               ((i >> 24) & 0xFF);
    }

    public SelfNetInfo getWifiInfo(Context context) {
        ConnectivityManager mConnectManager = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = mConnectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mIsWifiConnected = netinfo.isAvailable() && netinfo.isConnected();

        mDhcpinfo = mWifiManager.getDhcpInfo();

        mWifiinfo = mWifiManager.getConnectionInfo();

        mWifiname = mWifiinfo.getSSID();

        int validSecurity = WifiAPManager.getInstance(context).getValidSecurity();
        Log.e(TAG, "security: " + validSecurity);

        String passWord = PreferenceUtil.getPwd(context,
                mWifiname.substring(1, mWifiname.length() - 1), "get_no_key");
        //        String passWord = "qwer123@";

        String WIFI_INFO = "当前网络信息如下:" + "\n" + (mIsWifiConnected ? "可用" : "不可用") + "\n" +
                           mWifiinfo.getSSID() + "\n" + "=====================" + "\n"

                           + "ip:   " + intToIp(mDhcpinfo.ipAddress) + "\n"

                           + "mask:   " + mDhcpinfo.netmask + "\n"

                           + "netgate:   " + mDhcpinfo.gateway + "\n"

                           + "dns:   " + mDhcpinfo.dns1 + "\n"

                           + "密码:   " + passWord + "\n"

                           + "加密:   " + validSecurity + "\n";

        Log.e(TAG, WIFI_INFO);
        return new SelfNetInfo(mIsWifiConnected, mWifiinfo.getSSID().replace("\"", ""),
                intToIp(mDhcpinfo.ipAddress),
                TextUtils.equals(passWord, "get_no_key") ? null : passWord, validSecurity);
    }


    /**
     * 切换到指定wifi
     *
     * @param wifiName 指定的wifi名字
     * @param wifiPwd  wifi密码，如果已经保存过密码，可以传入null
     */
    public void changeToWifi(String wifiName, String wifiPwd) {
        registerNetBroadcastReceiver(mContext);
        if (mWifiManager == null) {
            Log.e(TAG, " ***** init first ***** ");
            return;
        }

        if (TextUtils.isEmpty(wifiName)) {
            Log.e(TAG, "wifi name is null");
            return;
        }

        printCurWifiInfo();
        boolean isSetSuccess = mWifiManager.setWifiEnabled(true);
        Log.e(TAG, "set wifi enable result: " + isSetSuccess + ", is wifi enable: " +
                   mWifiManager.isWifiEnabled());
        mHandler.sendEmptyMessageDelayed(MSG_CONNECT_WIFI_TIMEOUT, 60000);
        if (mWifiManager.isWifiEnabled()) {
            scanWifi(wifiName, wifiPwd);
        } else {
            mWaitLinkWifiName = wifiName;
            mWaitLinkWifiPwd = wifiPwd;
        }
    }

    private void scanWifi(String wifiName, String wifiPwd) {
        String __wifiName__ = "\"" + wifiName + "\"";
        List wifiList = mWifiManager.getConfiguredNetworks();
        Log.e(TAG, "wifi list: " + wifiList);
        if (wifiList != null) {
            for (int i = 0; i < wifiList.size(); ++i) {
                WifiConfiguration wifiInfo0 = (WifiConfiguration) wifiList.get(i);
                // 先找到对应的wifi
                if (__wifiName__.equals(wifiInfo0.SSID) || wifiName.equals(wifiInfo0.SSID)) {
                    // 1、 先启动，可能已经输入过密码，可以直接启动
                    Log.i(TAG, " set wifi 1 = " + wifiInfo0.SSID);
                    doChange2Wifi(wifiInfo0.networkId);
                }
            }
        } else {
            if (mHandler.hasMessages(MSG_CONNECT_WIFI_TIMEOUT)) {
                mHandler.removeMessages(MSG_CONNECT_WIFI_TIMEOUT);
                mHandler.sendEmptyMessage(MSG_CONNECT_WIFI_TIMEOUT);
            }
            return;
        }

        // 2、如果wifi还没有输入过密码，尝试输入密码，启动wifi
        WifiConfiguration wifiNewConfiguration = createWifiInfo(wifiName, wifiPwd);//使用wpa2的wifi加密方式
        int newNetworkId = mWifiManager.addNetwork(wifiNewConfiguration);
        if (newNetworkId == -1) {
            Log.e(TAG, "操作失败,需要您到手机wifi列表中取消对设备连接的保存");
        } else {
            doChange2Wifi(newNetworkId);
        }
    }

    private void doChange2Wifi(int newNetworkId) {
        // 如果wifi权限没打开（1、先打开wifi，2，使用指定的wifi
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }

        boolean enableNetwork = mWifiManager.enableNetwork(newNetworkId, true);
        if (!enableNetwork) {
            Log.e(TAG, "切换到指定wifi失败");
        } else {
            Log.e(TAG, "切换到指定wifi成功");
        }
    }

    /**
     * 创建 WifiConfiguration，这里创建的是wpa2加密方式的wifi
     *
     * @param ssid     wifi账号
     * @param password wifi密码
     * @return
     */
    private WifiConfiguration createWifiInfo(String ssid, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        config.preSharedKey = "\"" + password + "\"";
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.status = WifiConfiguration.Status.ENABLED;
        return config;
    }

    public void printCurWifiInfo() {
        if (mWifiManager == null) {
            return;
        }

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        Log.i(TAG, "cur wifi = " + wifiInfo.getSSID());
        Log.i(TAG, "cur getNetworkId = " + wifiInfo.getNetworkId());
    }

    //监听wifi是否连接上某一固定网络
    private BroadcastReceiver mWifiConnectedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Log.e(TAG, "wifi state: " + wifiState);
                if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                    if (!TextUtils.isEmpty(mWaitLinkWifiName) && !TextUtils.isEmpty(mWaitLinkWifiPwd)) {
                        scanWifi(mWaitLinkWifiName, mWaitLinkWifiPwd);
                        mWaitLinkWifiName = null;
                        mWaitLinkWifiPwd = null;
                    }
                }
            }
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    //如果当前的网络连接成功并且网络连接可用
                    if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                        if (TextUtils.equals(wifiInfo.getSSID(), mWifiname)) {
                            Log.e(TAG, "connect " + mWifiname + " success!!!!");
                            if (mHandler.hasMessages(MSG_CONNECT_WIFI_TIMEOUT)) {
                                mHandler.removeMessages(MSG_CONNECT_WIFI_TIMEOUT);
                            }
                            mHandler.sendEmptyMessage(MSG_CONNECT_WIFI);
                            mContext.unregisterReceiver(mWifiConnectedBroadcastReceiver);
                        }
                    } else {
                        Log.i("TAG", "断开");
                    }
                }
            }
        }
    };

    private void registerNetBroadcastReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mWifiConnectedBroadcastReceiver, filter);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mContext.unregisterReceiver(mWifiConnectedBroadcastReceiver);
    }

    public void registerHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void unregitsterHandler() {
        mHandler = null;
    }
}
