/*
 * ************************************************************
 * 文件：SelfNetInfo.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.bean;

public class SelfNetInfo {
    private boolean isWifiConnected;
    private String ssid;
    private String ip;
    private String passWord;
    private int security;

    public SelfNetInfo() {
    }

    /**
     * 自身网络信息
     *
     * @param isWifiConnected
     * @param ssid
     * @param ip
     * @param passWord
     */
    public SelfNetInfo(boolean isWifiConnected, String ssid, String ip, String passWord,
            int security) {
        this.isWifiConnected = isWifiConnected;
        this.ssid = ssid;
        this.ip = ip;
        this.passWord = passWord;
        this.security = security;
    }

    public boolean isWifiConnected() {
        return isWifiConnected;
    }

    public void setWifiConnected(boolean wifiConnected) {
        isWifiConnected = wifiConnected;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public int getSecurity() {
        return security;
    }

    public void setSecurity(int security) {
        this.security = security;
    }

    @Override
    public String toString() {
        return "SelfNetInfo{" + "isWifiConnected=" + isWifiConnected + ", ssid='" + ssid + '\'' +
               ", ip='" + ip + '\'' + ", passWord='" + passWord + '\'' + ", security='" + security +
               '\'' + '}';
    }
}
