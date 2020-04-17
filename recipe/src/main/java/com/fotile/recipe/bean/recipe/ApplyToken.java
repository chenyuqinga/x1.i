package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 文件名称：ApplyToken
 * 创建时间：17-9-22 上午9:07
 * 文件作者：zhangqiang
 * 功能描述：请求菜谱或我的上传token数据结构
 */
public class ApplyToken implements Parcelable{

    /**
     *
     */
    @SerializedName("app_id")
    @Expose
    private String appId;

    /**
     * token
     */
    @SerializedName("access_token")
    @Expose
    private String accessToken;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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
        dest.writeString(this.appId);
        dest.writeString(this.accessToken);
    }

    public ApplyToken() {
    }

    protected ApplyToken(Parcel in) {
        this.appId = in.readString();
        this.accessToken = in.readString();
    }

    public static final Creator<ApplyToken> CREATOR = new Creator<ApplyToken>() {
        @Override
        public ApplyToken createFromParcel(Parcel source) {
            return new ApplyToken(source);
        }

        @Override
        public ApplyToken[] newArray(int size) {
            return new ApplyToken[size];
        }
    };
}
