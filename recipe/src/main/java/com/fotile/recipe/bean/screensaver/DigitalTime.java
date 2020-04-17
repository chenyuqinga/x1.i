package com.fotile.recipe.bean.screensaver;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 文件名称：DigitalTime
 * 创建时间：2017-11-07 19:42
 * 文件作者：shihuijuan
 * 功能描述：电子时钟时间格式
 */

public class DigitalTime implements Parcelable {
    /**
     * 十二小时制时间
     */
    private String time;
    /**
     * 上午或下午的标识
     */
    private String amOrPm;

    public DigitalTime() {
    }

    protected DigitalTime(Parcel in) {
        time = in.readString();
        amOrPm = in.readString();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmOrPm() {
        return amOrPm;
    }

    public void setAmOrPm(String amOrPm) {
        this.amOrPm = amOrPm;
    }

    public static final Creator<DigitalTime> CREATOR = new Creator<DigitalTime>() {
        @Override
        public DigitalTime createFromParcel(Parcel in) {
            return new DigitalTime(in);
        }

        @Override
        public DigitalTime[] newArray(int size) {
            return new DigitalTime[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(amOrPm);
    }
}
