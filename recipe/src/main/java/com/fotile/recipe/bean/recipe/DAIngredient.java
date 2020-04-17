package com.fotile.recipe.bean.recipe;
import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 文件名称：DAIngredient
 * 创建时间：2017-12-22 15:42
 * 文件作者：shihuijuan
 * 功能描述：菜谱A配料数据结构
 */

@Entity
public class DAIngredient implements Parcelable{

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

    @Generated(hash = 255388733)
    public DAIngredient(Long _id, String id, String unit, String name) {
        this._id = _id;
        this.id = id;
        this.unit = unit;
        this.name = name;
    }

    @Generated(hash = 276126563)
    public DAIngredient() {
    }

    protected DAIngredient(Parcel in) {
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

    public static final Creator<DAIngredient> CREATOR = new Creator<DAIngredient>() {
        @Override
        public DAIngredient createFromParcel(Parcel in) {
            return new DAIngredient(in);
        }

        @Override
        public DAIngredient[] newArray(int size) {
            return new DAIngredient[size];
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
