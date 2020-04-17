/*
 * ************************************************************
 * 文件：BoxNetInfo.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.bean;

public class BoxNetInfo {

    private String type;
    private int home_id;
    private String ip;
    private int port;
    private String session;

    public BoxNetInfo() {
    }

    public BoxNetInfo(String type, int home_id, String ip, int port, String session) {

        this.type = type;
        this.home_id = home_id;
        this.ip = ip;
        this.port = port;
        this.session = session;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getHome_id() {
        return home_id;
    }

    public void setHome_id(int home_id) {
        this.home_id = home_id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "BoxNetInfo{" + "type='" + type + '\'' + ", home_id='" + home_id + '\'' + ", ip='" +
               ip + '\'' + ", port='" + port + '\'' + ", session='" + session + '\'' + '}';
    }
}
