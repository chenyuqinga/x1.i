/*
 * ************************************************************
 * 文件：ResponseInfo.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.bean;

public class ResponseInfo {

    private String type;
    private String ap_connected;
    private int home_id;
    private String ssid;
    private String key;
    private String session;
    private int auth_mode;

    public ResponseInfo() {}

    public ResponseInfo(String type, String ap_connected, int home_id, String ssid, String key,
            int auth_mode, String session) {
        this.type = type;
        this.ap_connected = ap_connected;
        this.home_id = home_id;
        this.ssid = ssid;
        this.key = key;
        this.session = session;
        this.auth_mode = auth_mode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAp_connected() {
        return ap_connected;
    }

    public void setAp_connected(String ap_connected) {
        this.ap_connected = ap_connected;
    }

    public int getHome_id() {
        return home_id;
    }

    public void setHome_id(int home_id) {
        this.home_id = home_id;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getAuth_mode() {
        return auth_mode;
    }

    public void setAuth_mode(int auth_mode) {
        this.auth_mode = auth_mode;
    }

    @Override
    public String toString() {
        return "ResponseInfo{" + "type='" + type + '\'' + ", ap_connected='" + ap_connected + '\'' +
               ", home_id=" + home_id + ", ssid='" + ssid + '\'' + ", key='" + key + '\'' +
               ", session='" + session + '\'' + ", auth_mode=" + auth_mode + '}';
    }
}
