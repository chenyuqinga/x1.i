/*
 * ************************************************************
 * 文件：WifiAPManager.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.wifi;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WifiAPManager {
    private static final String TAG = WifiAPManager.class.getSimpleName();
    public final static boolean DEBUG = true;

    public static final int MESSAGE_AP_STATE_ENABLED = 50001;
    public static final int MESSAGE_AP_STATE_FAILED = 50002;
    //默认wifi秘密
    private static final String DEFAULT_AP_PASSWORD = "12345678";
    private static WifiAPManager sInstance;
    private static Handler mHandler;
    private static Context mContext;
    private WifiManager mWifiManager;
    //监听wifi热点的状态变化
    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
    public static int WIFI_AP_STATE_DISABLING = 10;
    public static int WIFI_AP_STATE_DISABLED = 11;
    public static int WIFI_AP_STATE_ENABLING = 12;
    public static int WIFI_AP_STATE_ENABLED = 13;
    public static int WIFI_AP_STATE_FAILED = 14;
    private long mStartTime;
    private boolean mIsApEnable;
    private boolean mIsRegist;

    public enum WifiSecurityType {
        WIFICIPHER_NOPASS, WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_WPA2, WIFICIPHER_INVALID
    }

    private WifiAPManager(Context context) {
        if (DEBUG) {
            Log.e(TAG, "WifiAPUtils construct");
        }
        mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        registerReceiver(context);
    }

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WIFI_AP_STATE_CHANGED_ACTION);
        context.registerReceiver(mWifiStateBroadcastReceiver, filter);
        mIsRegist = true;
    }

    public boolean isRegist() {
        return mIsRegist;
    }

    public void updateContext(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        registerReceiver(context);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (DEBUG) {
            Log.e(TAG, "finalize");
        }
        mContext.unregisterReceiver(mWifiStateBroadcastReceiver);
    }

    public static WifiAPManager getInstance(Context c) {
        if (null == sInstance) {
            sInstance = new WifiAPManager(c);
        }
        return sInstance;
    }

    public boolean turnOnWifiAp(String str, String password, WifiSecurityType Type) {
        mStartTime = System.currentTimeMillis();
        Log.e("NetConfigBusiness", "ap start time: " + mStartTime);
        //配置热点信息。
        WifiConfiguration wcfg = new WifiConfiguration();
        wcfg.SSID = str;
        wcfg.networkId = 1;
        wcfg.allowedAuthAlgorithms.clear();
        wcfg.allowedGroupCiphers.clear();
        wcfg.allowedKeyManagement.clear();
        wcfg.allowedPairwiseCiphers.clear();
        wcfg.allowedProtocols.clear();
        if (Type == WifiSecurityType.WIFICIPHER_NOPASS) {
            if (DEBUG) {
                Log.e(TAG, "wifi ap----no password");
            }
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN, true);
            wcfg.wepKeys[0] = "";
            wcfg.allowedKeyManagement.set(KeyMgmt.NONE);
            wcfg.wepTxKeyIndex = 0;
        } else if (Type == WifiSecurityType.WIFICIPHER_WPA) {
            if (DEBUG) {
                Log.e(TAG, "wifi ap----wpa");
            }
            //密码至少8位，否则使用默认密码
            if (null != password && password.length() >= 8) {
                wcfg.preSharedKey = password;
            } else {
                wcfg.preSharedKey = DEFAULT_AP_PASSWORD;
            }
            wcfg.hiddenSSID = false;
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wcfg.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            //wcfg.allowedKeyManagement.set(4);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wcfg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        } else if (Type == WifiSecurityType.WIFICIPHER_WPA2) {
            if (DEBUG) {
                Log.e(TAG, "wifi ap---- wpa2");
            }
            //密码至少8位，否则使用默认密码
            if (null != password && password.length() >= 8) {
                wcfg.preSharedKey = password;
            } else {
                wcfg.preSharedKey = DEFAULT_AP_PASSWORD;
            }
            //不隐藏热点
            wcfg.hiddenSSID = false;
            //开放系统认证
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            //组密码
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wcfg.allowedKeyManagement.set(4);
            wcfg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wcfg.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        }
        try {
            Method method = mWifiManager.getClass().getMethod("setWifiApConfiguration",
                    wcfg.getClass());
            Boolean rt = (Boolean) method.invoke(mWifiManager, wcfg);
            if (DEBUG) {
                Log.e(TAG, " rt = " + rt);
            }
        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            Log.e(TAG, e.toString());
        }
        Log.e(TAG, "wcfg: " + wcfg.toString());
        return setWifiApEnabled();
    }

    //获取热点状态
    public int getWifiAPState() {
        int state = -1;
        try {
            Method method2 = mWifiManager.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(mWifiManager);
        } catch (Exception e) {
//            Log.e(TAG, e.toString());
        }
        if (DEBUG) {
            Log.e("WifiAP", "getWifiAPState.state " + state);
        }
        return state;
    }

    private boolean setWifiApEnabled() {
        //开启wifi热点需要关闭wifi
        while (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_DISABLED) {
            mWifiManager.setWifiEnabled(false);
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        }
        // 确保wifi 热点关闭。
        while (getWifiAPState() != WIFI_AP_STATE_DISABLED) {
            try {
                Method method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                        WifiConfiguration.class, boolean.class);
                method1.invoke(mWifiManager, null, false);

                Thread.sleep(200);
            } catch (Exception e) {
//                Log.e(TAG, e.getMessage());
                return false;
            }
        }

        //开启wifi热点
        try {
            Method method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            method1.invoke(mWifiManager, null, true);
            Thread.sleep(200);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    //关闭WiFi热点
    public void closeWifiAp() {
        Log.e(TAG, "close wifi ap");
        if (getWifiAPState() != WIFI_AP_STATE_DISABLED) {
            try {
                Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
                Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                        WifiConfiguration.class, boolean.class);
                method2.invoke(mWifiManager, config, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerHandler(Handler handler) {
        mHandler = handler;
    }

    public void unregisterHandler() {
        mHandler = null;
    }

    public void unregisterReceiver() {
        mContext.unregisterReceiver(mWifiStateBroadcastReceiver);
        mIsRegist = false;
    }

    //监听wifi热点状态变化
    private BroadcastReceiver mWifiStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) {
                Log.e(TAG, "WifiAPUtils onReceive: " + intent.getAction());
            }
            if (WIFI_AP_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int cstate = intent.getIntExtra(EXTRA_WIFI_AP_STATE, -1);
                Log.e(TAG, "cstate: " + cstate);
                if (cstate == WIFI_AP_STATE_ENABLED) {
                    long endTime = System.currentTimeMillis();
                    Log.e("NetConfigBusiness", "ap end time: " + endTime);
                    Log.e("NetConfigBusiness", "ap duration: " + (endTime - mStartTime) / 1000 + "s");
                    mIsApEnable = true;
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(MESSAGE_AP_STATE_ENABLED);
                    }
                }
//                if (cstate == WIFI_AP_STATE_DISABLED || cstate == WIFI_AP_STATE_FAILED) {
                if (cstate == WIFI_AP_STATE_FAILED) {
                    mIsApEnable = false;
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(MESSAGE_AP_STATE_FAILED);
                    }
                }
            }
        }
    };

    public boolean isAPEnable() {
        return mIsApEnable;
    }

    //获取热点ssid
    public String getValidApSsid() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration configuration = (WifiConfiguration) method.invoke(mWifiManager);
            return configuration.SSID;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    //获取热点密码
    public String getValidPassword() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration configuration = (WifiConfiguration) method.invoke(mWifiManager);
            return configuration.preSharedKey;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

    }

    //获取热点安全类型
    public int getValidSecurity() {
        WifiConfiguration configuration;
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            configuration = (WifiConfiguration) method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return WifiSecurityType.WIFICIPHER_INVALID.ordinal();
        }

        if (DEBUG) {
            Log.e(TAG, "getSecurity security=" + configuration.allowedKeyManagement);
        }
        if (configuration.allowedKeyManagement.get(KeyMgmt.NONE)) {
            return WifiSecurityType.WIFICIPHER_NOPASS.ordinal();
        } else if (configuration.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return WifiSecurityType.WIFICIPHER_WPA.ordinal();
        } else if (configuration.allowedKeyManagement.get(4)) { //4 means WPA2_PSK
            return WifiSecurityType.WIFICIPHER_WPA2.ordinal();
        }
        return WifiSecurityType.WIFICIPHER_INVALID.ordinal();
    }
}