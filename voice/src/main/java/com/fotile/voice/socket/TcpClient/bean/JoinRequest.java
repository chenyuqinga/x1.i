/*
 * ************************************************************
 * 文件：JoinRequest.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.TcpClient.bean;

public class JoinRequest {

    private String type;
    private int home_id;
    private int device_type;
    private String session;
    private int device_id;
    private String device_name;
    private boolean whether_smart_device;
    private boolean whether_sound;
    private boolean whether_screen;
    private String device_mac;


    public JoinRequest() {
    }

    public JoinRequest(String type, int home_id, int device_type, String session, int device_id,
            String device_name, boolean whether_smart_device, boolean whether_sound,
            boolean whether_screen, String device_mac) {
        this.type = type;
        this.home_id = home_id;
        this.device_type = device_type;
        this.session = session;
        this.device_id = device_id;
        this.device_name = device_name;
        this.whether_smart_device = whether_smart_device;
        this.whether_sound = whether_sound;
        this.whether_screen = whether_screen;
        this.device_mac = device_mac;
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

    public int getDevice_type() {
        return device_type;
    }

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public boolean isWhether_smart_device() {
        return whether_smart_device;
    }

    public void setWhether_smart_device(boolean whether_smart_device) {
        this.whether_smart_device = whether_smart_device;
    }

    public boolean isWhether_sound() {
        return whether_sound;
    }

    public void setWhether_sound(boolean whether_sound) {
        this.whether_sound = whether_sound;
    }

    public boolean isWhether_screen() {
        return whether_screen;
    }

    public void setWhether_screen(boolean whether_screen) {
        this.whether_screen = whether_screen;
    }

    public String getDevice_mac() {
        return device_mac;
    }

    public void setDevice_mac(String device_mac) {
        this.device_mac = device_mac;
    }

    @Override
    public String toString() {
        return "JoinRequest{" + "type='" + type + '\'' + ", home_id=" + home_id + ", device_type=" +
               device_type + ", session='" + session + '\'' + ", device_id=" + device_id +
               ", device_name='" + device_name + '\'' + ", whether_smart_device=" +
               whether_smart_device + ", whether_sound=" + whether_sound + ", whether_screen=" +
               whether_screen + ", device_mac='" + device_mac + '\'' + '}';
    }
}
