package com.fotile.recipe.bean.screensaver;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 文件名称：City
 * 创建时间：2017-11-07 16:20
 * 文件作者：shihuijuan
 * 功能描述：获取城市信息返回的数据结构
 */

public class City implements Parcelable {
    /**
     * 返回结果描述
     */
    @SerializedName("ret")
    @Expose
    private String result;
    /**
     * ip地址
     */
    @SerializedName("ip")
    @Expose
    private String ip;
    /**
     * 返回数据:包含国家,省份,城市等
     */
    @SerializedName("data")
    @Expose
    private List<String> dataList;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    protected City(Parcel in) {
        result = in.readString();
        ip = in.readString();
        dataList = in.createStringArrayList();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(result);
        dest.writeString(ip);
        dest.writeStringList(dataList);
    }
}
