/*
 * ************************************************************
 * 文件：NluBean.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.bean;

public class NluBean {
    /**
     * timestamp : 1548310203
     * skillId : 2018040200000004
     * skill : 新版天气
     * systime : 13.947021484375
     * res : 5b6a4cfc1debf70001e3e47f
     * version : 2019.1.15.20:40:58
     * pinyin : hang zhou tian qi zen me yang
     * loadtime : 8.927001953125
     * input : 杭州天气怎么样
     * semantics : {"request":{"slots":[{"value":"杭州","pos":[0,1],"rawvalue":"杭州","name":"城市","rawpinyin":"hang zhou"},{"value":"查询天气","name":"intent"}],"task":"天气","slotcount":2,"confidence":1}}
     * skillVersion : 16
     */

    private int timestamp;
    private String skillId;
    private String skill;
    private double systime;
    private String res;
    private String version;
    private String pinyin;
    private double loadtime;
    private String input;
    private SemanticsBean semantics;
    private String skillVersion;

    public int getTimestamp() { return timestamp;}

    public void setTimestamp(int timestamp) { this.timestamp = timestamp;}

    public String getSkillId() { return skillId;}

    public void setSkillId(String skillId) { this.skillId = skillId;}

    public String getSkill() { return skill;}

    public void setSkill(String skill) { this.skill = skill;}

    public double getSystime() { return systime;}

    public void setSystime(double systime) { this.systime = systime;}

    public String getRes() { return res;}

    public void setRes(String res) { this.res = res;}

    public String getVersion() { return version;}

    public void setVersion(String version) { this.version = version;}

    public String getPinyin() { return pinyin;}

    public void setPinyin(String pinyin) { this.pinyin = pinyin;}

    public double getLoadtime() { return loadtime;}

    public void setLoadtime(double loadtime) { this.loadtime = loadtime;}

    public String getInput() { return input;}

    public void setInput(String input) { this.input = input;}

    public SemanticsBean getSemantics() { return semantics;}

    public void setSemantics(SemanticsBean semantics) { this.semantics = semantics;}

    public String getSkillVersion() { return skillVersion;}

    public void setSkillVersion(String skillVersion) { this.skillVersion = skillVersion;}
}
