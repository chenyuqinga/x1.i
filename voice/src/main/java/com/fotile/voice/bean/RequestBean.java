/*
 * ************************************************************
 * 文件：RequestBean.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.bean;

import java.util.List;

public class RequestBean {
    /**
     * slots : [{"value":"杭州","pos":[0,1],"rawvalue":"杭州","name":"城市","rawpinyin":"hang zhou"},{"value":"查询天气","name":"intent"}]
     * task : 天气
     * slotcount : 2
     * confidence : 1
     */

    private String task;
    private int slotcount;
    private int confidence;
    private List<SlotsBean> slots;

    public String getTask() { return task;}

    public void setTask(String task) { this.task = task;}

    public int getSlotcount() { return slotcount;}

    public void setSlotcount(int slotcount) { this.slotcount = slotcount;}

    public int getConfidence() { return confidence;}

    public void setConfidence(int confidence) { this.confidence = confidence;}

    public List<SlotsBean> getSlots() { return slots;}

    public void setSlots(List<SlotsBean> slots) { this.slots = slots;}

}
