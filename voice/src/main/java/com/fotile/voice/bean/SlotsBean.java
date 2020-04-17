/*
 * ************************************************************
 * 文件：SlotsBean.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.bean;

import java.util.List;

public class SlotsBean {
    /**
     * value : 杭州
     * pos : [0,1]
     * rawvalue : 杭州
     * name : 城市
     * rawpinyin : hang zhou
     */

    private String value;
    private String rawvalue;
    private String name;
    private String rawpinyin;
    private List<Integer> pos;

    public String getValue() { return value;}

    public void setValue(String value) { this.value = value;}

    public String getRawvalue() { return rawvalue;}

    public void setRawvalue(String rawvalue) { this.rawvalue = rawvalue;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getRawpinyin() { return rawpinyin;}

    public void setRawpinyin(String rawpinyin) { this.rawpinyin = rawpinyin;}

    public List<Integer> getPos() { return pos;}

    public void setPos(List<Integer> pos) { this.pos = pos;}
}

