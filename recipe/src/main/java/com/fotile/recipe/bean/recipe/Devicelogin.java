package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 文件名称：Devicelogin
 * 创建时间：17-9-21 下午8:18
 * 文件作者：zhangqiang
 * 功能描述：登录请求的设备的数据结构
 */
public class Devicelogin implements Parcelable{

    /**
     * 登录时间
     */
    @SerializedName("expire_in")
    @Expose
    private String expireIn;

    /**
     * 设备id
     */
    @SerializedName("device_id")
    @Expose
    private String deviceId;


    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;

    /**
     * token
     */
    @SerializedName("access_token")
    @Expose
    private String accessToken;

    public String getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(String expireIn) {
        this.expireIn = expireIn;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.expireIn);
        dest.writeString(this.deviceId);
        dest.writeString(this.refreshToken);
        dest.writeString(this.accessToken);
    }

    public Devicelogin() {
    }

    protected Devicelogin(Parcel in) {
        this.expireIn = in.readString();
        this.deviceId = in.readString();
        this.refreshToken = in.readString();
        this.accessToken = in.readString();
    }

    public static final Creator<Devicelogin> CREATOR = new Creator<Devicelogin>() {
        @Override
        public Devicelogin createFromParcel(Parcel source) {
            return new Devicelogin(source);
        }

        @Override
        public Devicelogin[] newArray(int size) {
            return new Devicelogin[size];
        }
    };
}
