/*
 * ************************************************************
 * 文件：DelayTime.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.bean;

public class DelayTime {

    private boolean isLegal;
    private int valueForDelay;
    private String valueFotTTS;

    public DelayTime(boolean isLegal, int valueForDelay, String valueFotTTS) {
        this.isLegal = isLegal;
        this.valueForDelay = valueForDelay;
        this.valueFotTTS = valueFotTTS;
    }

    public boolean isLegal() {
        return isLegal;
    }

    public void setLegal(boolean legal) {
        isLegal = legal;
    }

    public int getValueForDelay() {
        return valueForDelay;
    }

    public void setValueForDelay(int valueForDelay) {
        this.valueForDelay = valueForDelay;
    }

    public String getValueFotTTS() {
        return valueFotTTS;
    }

    public void setValueFotTTS(String valueFotTTS) {
        this.valueFotTTS = valueFotTTS;
    }

    @Override
    public String toString() {
        return "DelayTime{" + "isLegal=" + isLegal + ", valueForDelay=" + valueForDelay +
               ", valueFotTTS='" + valueFotTTS + '\'' + '}';
    }
}
