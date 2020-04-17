/*
 * ************************************************************
 * 文件：NluResult.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.bean;

public class NluResult {
    /**
     * type : control
     * target_device : 设备类型定义
     * online : true
     * command : DUI返回api&param
     * session : vbox provide id
     * msgid : 123
     * need_response : true
     */



    private String type;
    private String target_device;
    private boolean online;
    private String command;
    private String session;
    private int msgid;
    private boolean need_response;

    public String getType() { return type;}

    public void setType(String type) { this.type = type;}

    public String getTarget_device() { return target_device;}

    public void setTarget_device(String target_device) { this.target_device = target_device;}

    public boolean isOnline() { return online;}

    public void setOnline(boolean online) { this.online = online;}

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public String getSession() { return session;}

    public void setSession(String session) { this.session = session;}

    public int getMsgid() { return msgid;}

    public void setMsgid(int msgid) { this.msgid = msgid;}

    public boolean isNeed_response() { return need_response;}

    public void setNeed_response(boolean need_response) { this.need_response = need_response;}

    @Override
    public String toString() {
        return "NluResult{" + "type='" + type + '\'' + ", target_device='" + target_device + '\'' +
               ", online=" + online + ", command='" + command + '\'' + ", session='" + session +
               '\'' + ", msgid=" + msgid + ", need_response=" + need_response + '}';
    }
}
