package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 文件名称：DMajorIngredient
 * 创建时间：17-8-29 上午9:47
 * 文件作者：yujiaying
 * 功能描述：DMajorIngredient数据结构
 */

@Entity
public class DMajorIngredient implements Parcelable{

    @Id(autoincrement = true)
    private Long _id;

    /**
     * 菜谱ID
     */
    private String id;
    /** 主料用量 */
    private String unit;

    /** 主料名称 */
    private String name;

    /** (主料、辅料) */
    private String type;

    @Generated(hash = 1669561690)
    public DMajorIngredient(Long _id, String id, String unit, String name,
            String type) {
        this._id = _id;
        this.id = id;
        this.unit = unit;
        this.name = name;
        this.type = type;
    }

    @Generated(hash = 230505802)
    public DMajorIngredient() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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
        dest.writeString(this.unit);
        dest.writeString(this.name);
        dest.writeString(this.type);
    }

    protected DMajorIngredient(Parcel in) {
        this._id = (Long) in.readValue(Long.class.getClassLoader());
        this.id = in.readString();
        this.unit = in.readString();
        this.name = in.readString();
        this.type = in.readString();
    }

    public static final Creator<DMajorIngredient> CREATOR = new Creator<DMajorIngredient>() {
        @Override
        public DMajorIngredient createFromParcel(Parcel source) {
            return new DMajorIngredient(source);
        }

        @Override
        public DMajorIngredient[] newArray(int size) {
            return new DMajorIngredient[size];
        }
    };
}
