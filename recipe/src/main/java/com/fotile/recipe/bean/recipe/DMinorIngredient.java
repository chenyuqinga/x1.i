package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 文件名称：DMinorIngredient
 * 创建时间：17-8-29 上午9:47
 * 文件作者：yujiaying
 * 功能描述：DMinorIngredient数据结构
 */

@Entity
public class DMinorIngredient implements Parcelable{

    @Id(autoincrement = true)
    private Long _id;

    /**
     * 菜谱ID
     */
    private String id;

    /** 辅料用量 */
    private String unit;

    /** 辅料名称 */
    private String name;

    @Generated(hash = 422861427)
    public DMinorIngredient(Long _id, String id, String unit, String name) {
        this._id = _id;
        this.id = id;
        this.unit = unit;
        this.name = name;
    }

    @Generated(hash = 417759169)
    public DMinorIngredient() {
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
    }

    protected DMinorIngredient(Parcel in) {
        this._id = (Long) in.readValue(Long.class.getClassLoader());
        this.id = in.readString();
        this.unit = in.readString();
        this.name = in.readString();
    }

    public static final Creator<DMinorIngredient> CREATOR = new Creator<DMinorIngredient>() {
        @Override
        public DMinorIngredient createFromParcel(Parcel source) {
            return new DMinorIngredient(source);
        }

        @Override
        public DMinorIngredient[] newArray(int size) {
            return new DMinorIngredient[size];
        }
    };
}
