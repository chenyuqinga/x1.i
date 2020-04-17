/*
 * ************************************************************
 * 文件：SpeechRecipeBean.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SpeechRecipeBean implements Parcelable {

    /**
     * object_code : 4
     * name : 红烧鲫鱼
     * image : http://fotileiotcloudtest.osscnhangzhou.aliyuncs.com/avatars/96538e42dd5c45cca2985eeb735c7c68.jpg
     * video :
     * _id : 5ac9ba24975157024e9c4abd
     * type : 3
     */

    private int object_code;
    private String name;
    private String image;
    private List<String> suit;
    private String video;
    private String _id;
    private String type;

    public SpeechRecipeBean() {
    }

    public int getObject_code() {
        return object_code;
    }

    public void setObject_code(int object_code) {
        this.object_code = object_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSuit() {
        return suit;
    }

    public void setSuit(List<String> suit) {
        this.suit = suit;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.object_code);
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeStringList(this.suit);
        dest.writeString(this.video);
        dest.writeString(this._id);
        dest.writeString(this.type);
    }

    protected SpeechRecipeBean(Parcel in) {
        this.object_code = in.readInt();
        this.name = in.readString();
        this.image = in.readString();
        this.suit = in.createStringArrayList();
        this.video = in.readString();
        this._id = in.readString();
        this.type = in.readString();
    }

    public static final Creator<SpeechRecipeBean> CREATOR = new Creator<SpeechRecipeBean>() {
        @Override
        public SpeechRecipeBean createFromParcel(Parcel source) {
            return new SpeechRecipeBean(source);
        }

        @Override
        public SpeechRecipeBean[] newArray(int size) {return new SpeechRecipeBean[size];}
    };

    @Override
    public String toString() {
        return "SpeechRecipeBean{" + "object_code=" + object_code + ", name='" + name + '\'' +
               ", image='" + image + '\'' + ", suit=" + suit + ", video='" + video + '\'' +
               ", _id='" + _id + '\'' + ", type='" + type + '\'' + '}';
    }
}
