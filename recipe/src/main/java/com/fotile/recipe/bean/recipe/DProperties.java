package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 文件名称：DProperties
 * 创建时间：17-8-29 上午9:47
 * 文件作者：yujiaying
 * 功能描述：DProperties数据结构
 */
@Entity
public class DProperties implements Parcelable{

    @Id(autoincrement = true)
    private Long _id;

    private String id;

    private String timeUnit;

    private String difficulty;

    private String cookingTime;

    @Generated(hash = 413075867)
    public DProperties(Long _id, String id, String timeUnit, String difficulty,
            String cookingTime) {
        this._id = _id;
        this.id = id;
        this.timeUnit = timeUnit;
        this.difficulty = difficulty;
        this.cookingTime = cookingTime;
    }

    @Generated(hash = 658959138)
    public DProperties() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getTimeUnit() {
        return this.timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCookingTime() {
        return this.cookingTime;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this._id);
        dest.writeString(this.id);
        dest.writeString(this.timeUnit);
        dest.writeString(this.difficulty);
        dest.writeString(this.cookingTime);
    }

    protected DProperties(Parcel in) {
        this._id = (Long) in.readValue(Long.class.getClassLoader());
        this.id = in.readString();
        this.timeUnit = in.readString();
        this.difficulty = in.readString();
        this.cookingTime = in.readString();
    }

    public static final Creator<DProperties> CREATOR = new Creator<DProperties>() {
        @Override
        public DProperties createFromParcel(Parcel source) {
            return new DProperties(source);
        }

        @Override
        public DProperties[] newArray(int size) {
            return new DProperties[size];
        }
    };
}
