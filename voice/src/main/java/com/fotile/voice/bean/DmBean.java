/*
 * ************************************************************
 * 文件：DmBean.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.bean;

public class DmBean {

    /**
     * intentName : 查询天气
     * intentId : 5b6a4cfc1debf70001e3e49c
     * widget :
     * nlg : 杭州今天晴，气温1~14℃，东北风，3级，空气质量轻度污染，天气偏凉，该加衣服了。
     * task : 天气
     * status : 1
     * taskId : 5b6a4cfc1debf70001e3e47f
     * shouldEndSession : true
     */

    private String intentName;
    private String intentId;
    private String nlg;
    private String task;
    private int status;
    private String taskId;
    private boolean shouldEndSession;

    public String getIntentName() { return intentName;}

    public void setIntentName(String intentName) { this.intentName = intentName;}

    public String getIntentId() { return intentId;}

    public void setIntentId(String intentId) { this.intentId = intentId;}

    public String getNlg() { return nlg;}

    public void setNlg(String nlg) { this.nlg = nlg;}

    public String getTask() { return task;}

    public void setTask(String task) { this.task = task;}

    public int getStatus() { return status;}

    public void setStatus(int status) { this.status = status;}

    public String getTaskId() { return taskId;}

    public void setTaskId(String taskId) { this.taskId = taskId;}

    public boolean isShouldEndSession() { return shouldEndSession;}

    public void setShouldEndSession(
            boolean shouldEndSession) { this.shouldEndSession = shouldEndSession;}


}
