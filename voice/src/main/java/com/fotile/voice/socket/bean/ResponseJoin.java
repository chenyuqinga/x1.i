/*
 * ************************************************************
 * 文件：ResponseJoin.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.bean;

import android.text.TextUtils;

public class ResponseJoin {

    private String type;
    private String result;
    private String statues;
    private String session;

    public ResponseJoin() {
    }

    public ResponseJoin(String type, String result, String statues, String session) {
        this.type = type;
        this.result = result;
        this.statues = statues;
        this.session = session;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setStatues(String statues) {
        this.statues = statues;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public boolean isConnect() {
        return TextUtils.equals(result, "success");
    }

    public String getStatues() {
        return statues;
    }

    public String getSession() {
        return session;
    }

    @Override
    public String toString() {
        return "ResponseJoin{" + "type='" + type + '\'' + ", result='" + result + '\'' +
               ", statues='" + statues + '\'' + ", session='" + session + '\'' + '}';
    }
}
