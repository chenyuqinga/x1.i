package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IsHome implements Parcelable{

    @SerializedName("sort")
    @Expose
    private String sort;

    @SerializedName("yn")
    @Expose
    private Integer yn;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sort);
        dest.writeValue(this.yn);
    }

    public IsHome() {
    }

    protected IsHome(Parcel in) {
        this.sort = in.readString();
        this.yn = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<IsHome> CREATOR = new Creator<IsHome>() {
        @Override
        public IsHome createFromParcel(Parcel source) {
            return new IsHome(source);
        }

        @Override
        public IsHome[] newArray(int size) {
            return new IsHome[size];
        }
    };
}
