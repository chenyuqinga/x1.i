package com.fotile.wifi.bean;

import android.net.wifi.ScanResult;

/**
 * 文件名称：MScanWifi
 * 创建时间：2017/8/15
 * 文件作者：wanghouyu
 * 功能描述：自定义wifi信息，用于显示在lisAdapter中
 */
public class MScanWifi {
    private int level;
    private String WifiName;
    public ScanResult scanResult;
    /**
     * 加密
     */
    private boolean isLock;
    /**
     * 查看是否配置过网络
     */
    private boolean isExsit;
    private boolean isConnectedNow;
    /**
     * 是否正在连接
     */
    private boolean isConnecting;

    public MScanWifi() {

    }

    public MScanWifi(ScanResult scanResult, String WifiName, int level, Boolean isLock) {
        this.WifiName = WifiName;
        this.level = level;
        this.isLock = isLock;
        this.scanResult = scanResult;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getWifiName() {
        return WifiName;
    }

    public void setWifiName(String wifiName) {
        WifiName = wifiName;
    }

    public Boolean getIsLock() {
        return isLock;
    }

    public void setIsLock(boolean isLock) {
        this.isLock = isLock;
    }

    public boolean getIsExsit() {
        return isExsit;
    }

    public void setIsExsit(boolean isExsit) {
        this.isExsit = isExsit;
    }

    public boolean getIsConnectedNow() {
        return isConnectedNow;
    }

    public void setIsConnectedNow(boolean isConnected) {
        this.isConnectedNow = isConnected;
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    public void setConnecting(boolean connecting) {
        isConnecting = connecting;
    }

    @Override
    public String toString() {
        if(isExsit){
            return "[name：" + WifiName + "] [isExsit：" + isExsit + "]";
        }
       else {
            return "****************************";
        }
    }
}
