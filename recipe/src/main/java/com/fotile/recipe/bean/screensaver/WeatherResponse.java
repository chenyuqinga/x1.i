package com.fotile.recipe.bean.screensaver;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 文件名称：WeatherResponse
 * 创建时间：2017-11-07 20:39
 * 文件作者：shihuijuan
 * 功能描述：
 */

public class WeatherResponse<Weather> implements Parcelable {
    /**
     * 天气返回结果
     */
    @SerializedName("weather")
    @Expose
    private List<Weather> weather;

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    protected WeatherResponse(Parcel in) {
    }

    public static final Creator<WeatherResponse> CREATOR = new Creator<WeatherResponse>() {
        @Override
        public WeatherResponse createFromParcel(Parcel in) {
            return new WeatherResponse(in);
        }

        @Override
        public WeatherResponse[] newArray(int size) {
            return new WeatherResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
