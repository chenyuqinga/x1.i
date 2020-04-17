package com.fotile.music.model;

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
