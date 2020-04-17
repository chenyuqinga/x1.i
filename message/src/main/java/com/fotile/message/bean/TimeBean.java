package com.fotile.message.bean;


import com.fotile.message.util.MessageDateUtil;

/**
 * 文件名称：TimeBean
 * 创建时间：2017/8/15
 * 文件作者：wanghouyu
 * 功能描述：时间
 */

public class TimeBean {
    private int year;
    private int month;
    private int day;
    private int weekOfYear;
    private int dayOfWeek;
    private int dayOfYear;
    private int hour;
    private int minute;
    private int second;

    public TimeBean() {
        super();
    }

    public TimeBean(int year, int month, int day, int weekOfYear, int dayOfWeek, int dayOfYear, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.weekOfYear = weekOfYear;
        this.dayOfWeek = dayOfWeek;
        this.dayOfYear = dayOfYear;
        this.hour = hour;
        this.minute = minute;
        this.second = second;

    }

    public int getYear() {
        return year;
    }

    public String getDateFormat() {
        return String.valueOf(year) + "-" + MessageDateUtil.thanTen(month) + "-" + MessageDateUtil.thanTen(day);
    }

    public String getTimeFormat() {
        return MessageDateUtil.thanTen(hour) + ":" + MessageDateUtil.thanTen(minute) + ":" + MessageDateUtil.thanTen(second);
    }

    public String getMessageShowDateFormat() {
        return String.valueOf(year) + "年" + String.valueOf(month) + "月" + String.valueOf(day) + "日";
    }

    public int getWeekOfYear() {
        return weekOfYear;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }
    public int getDayOfYear() {
        return dayOfYear;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    }
}
