/*
 * ************************************************************
 * 文件：DeviceReceive.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.bean;

import com.google.gson.JsonObject;

public class DeviceReceive {

    private String type;
    private boolean online;
    private JsonObject content;
    private String session;
    private int msgid;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getMsgid() {
        return msgid;
    }

    public void setMsgid(int msgid) {
        this.msgid = msgid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonObject getContent() {
        return content;
    }

    public void setContent(JsonObject content) {
        this.content = content;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "DeviceReceive{" + "type='" + type + '\'' + ", online=" + online + ", content=" +
               content + ", session='" + session + '\'' + ", msgid=" + msgid + '}';
    }
}
