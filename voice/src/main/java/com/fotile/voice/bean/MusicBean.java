/*
 * ************************************************************
 * 文件：MusicBean.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicBean implements Parcelable {

    private String title;
    private String linkUrl;
    private String imageUrl;
    private String subTitle;

    public MusicBean() {}

    public MusicBean(Parcel in) {
        title = in.readString();
        linkUrl = in.readString();
        imageUrl = in.readString();
        subTitle = in.readString();
    }

    public static final Creator<MusicBean> CREATOR = new Creator<MusicBean>() {
        @Override
        public MusicBean createFromParcel(Parcel in) {
            return new MusicBean(in);
        }

        @Override
        public MusicBean[] newArray(int size) {
            return new MusicBean[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @Override
    public String toString() {
        return "MusicBean{" + "title='" + title + '\'' + ", linkUrl='" + linkUrl + '\'' +
               ", imageUrl='" + imageUrl + '\'' + ", subTitle='" + subTitle + '\'' + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(linkUrl);
        dest.writeString(imageUrl);
        dest.writeString(subTitle);
    }
}
