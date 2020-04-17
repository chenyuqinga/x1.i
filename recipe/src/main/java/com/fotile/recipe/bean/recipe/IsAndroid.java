package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IsAndroid implements Parcelable{

    @SerializedName("search")
    @Expose
    private Integer search;

    @SerializedName("list")
    @Expose
    private Integer list;

    public Integer getSearch() {
        return search;
    }

    public void setSearch(Integer search) {
        this.search = search;
    }

    public Integer getList() {
        return list;
    }

    public void setList(Integer list) {
        this.list = list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.search);
        dest.writeValue(this.list);
    }

    public IsAndroid() {
    }

    protected IsAndroid(Parcel in) {
        this.search = (Integer) in.readValue(Integer.class.getClassLoader());
        this.list = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<IsAndroid> CREATOR = new Creator<IsAndroid>() {
        @Override
        public IsAndroid createFromParcel(Parcel source) {
            return new IsAndroid(source);
        }

        @Override
        public IsAndroid[] newArray(int size) {
            return new IsAndroid[size];
        }
    };
}
