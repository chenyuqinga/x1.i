package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 文件名称：DRecipeCategory
 * 创建时间：2017-09-28 10:35
 * 文件作者：shihuijuan
 * 功能描述：菜谱类别数据结构
 */
@Entity
public class DRecipeCategory implements Parcelable{

    @Id(autoincrement = true)
    private Long _id;

    /**
     * 类别id
     */
    private String id;
    /**
     * 类别名称
     */
    private String name;
    @Generated(hash = 1509328374)
    public DRecipeCategory(Long _id, String id, String name) {
        this._id = _id;
        this.id = id;
        this.name = name;
    }
    @Generated(hash = 582774045)
    public DRecipeCategory() {
    }

    protected DRecipeCategory(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    public static final Creator<DRecipeCategory> CREATOR = new Creator<DRecipeCategory>() {
        @Override
        public DRecipeCategory createFromParcel(Parcel in) {
            return new DRecipeCategory(in);
        }

        @Override
        public DRecipeCategory[] newArray(int size) {
            return new DRecipeCategory[size];
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
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }
}
