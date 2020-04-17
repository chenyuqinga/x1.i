/*
 * ************************************************************
 * 文件：SemanticsBean.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.bean;

public class SemanticsBean {

    /**
     * request : {"slots":[{"value":"杭州","pos":[0,1],"rawvalue":"杭州","name":"城市","rawpinyin":"hang zhou"},{"value":"查询天气","name":"intent"}],"task":"天气","slotcount":2,"confidence":1}
     */

    private RequestBean request;

    public RequestBean getRequest() { return request;}

    public void setRequest(RequestBean request) { this.request = request;}

}
