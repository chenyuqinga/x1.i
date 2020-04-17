package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 文件名称：DCIngredient
 * 创建时间：2017-12-22 15:42
 * 文件作者：shihuijuan
 * 功能描述：菜谱C配料数据结构
 */

@Entity
public class DCIngredient implements Parcelable{

    @Id(autoincrement = true)
    private Long _id;

    /**
     * 菜谱ID
     */
    private String id;

    /**
     * 辅料用量
     */
    private String unit;

    /**
     * 辅料名称
     */
    private String name;

    @Generated(hash = 2084577940)
    public DCIngredient(Long _id, String id, String unit, String name) {
        this._id = _id;
        this.id = id;
        this.unit = unit;
        this.name = name;
    }

    @Generated(hash = 1357048736)
    public DCIngredient() {
    }

    protected DCIngredient(Parcel in) {
        id = in.readString();
        unit = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(unit);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DCIngredient> CREATOR = new Creator<DCIngredient>() {
        @Override
        public DCIngredient createFromParcel(Parcel in) {
            return new DCIngredient(in);
        }

        @Override
        public DCIngredient[] newArray(int size) {
            return new DCIngredient[size];
        }
    };

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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


}
