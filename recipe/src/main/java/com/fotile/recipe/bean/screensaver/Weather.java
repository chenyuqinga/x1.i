package com.fotile.recipe.bean.screensaver;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 文件名称：Weather
 * 创建时间：2017-11-07 20:42
 * 文件作者：shihuijuan
 * 功能描述：具体天气信息数据
 */

public class Weather implements Parcelable {
    /**
     * 温度
     */
    @SerializedName("tmp")
    @Expose
    private String temperature;
    /**
     * 天气图标地址
     */
    @SerializedName("icon")
    @Expose
    private String iconUrl;

    @SerializedName("weather")
    @Expose
    private String weather;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    protected Weather(Parcel in) {
        temperature = in.readString();
        iconUrl = in.readString();
    }

    public static final Creator<Weather> CREATOR = new Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel in) {
            return new Weather(in);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(temperature);
        dest.writeString(iconUrl);
    }
}
