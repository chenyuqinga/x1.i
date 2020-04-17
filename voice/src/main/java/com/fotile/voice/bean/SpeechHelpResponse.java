/*
 * ************************************************************
 * 文件：SpeechHelpResponse.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.bean;

import java.io.Serializable;
import java.util.List;

public class SpeechHelpResponse implements Serializable {


    /**
     * request_id : c108124ca01847d28366e304b2d1cfab
     * code : 0
     * msg : Success
     * data : ["今天天气怎么样","关闭烟机"]
     */

    private String request_id;
    private int code;
    private String msg;
    private List<String> data;

    public SpeechHelpResponse() {
    }

//    public SpeechHelpResponse(String request_id, int code, String msg, List<String> data) {
//        this.request_id = request_id;
//        this.code = code;
//        this.msg = msg;
//        this.data = data;
//    }

    public String getRequest_id() { return request_id;}

    public void setRequest_id(String request_id) { this.request_id = request_id;}

    public int getCode() { return code;}

    public void setCode(int code) { this.code = code;}

    public String getMsg() { return msg;}

    public void setMsg(String msg) { this.msg = msg;}

    public List<String> getData() { return data;}

    public void setData(List<String> data) { this.data = data;}

    @Override
    public String toString() {
        return "SpeechHelpResponse{" + "request_id='" + request_id + '\'' + ", code=" + code +
               ", msg='" + msg + '\'' + ", data=" + data + '}';
    }
}
