package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 文件名称：Products
 * 创建时间：2017/12/12 14:41
 * 文件作者：huanghuang
 * 功能描述：
 */
@Entity
public class DProducts implements Parcelable{

    /**
     * id : 1607d4b3e0a204381607d4b3e0a24801
     * name : C2.i蒸箱
     */

    @Id(autoincrement = true)
    private Long _id;
    /**
     * 菜谱ID
     */
    private String recipeId;
    /**
     * device自增ID
     */
    private Long deviceId;
    /**
     * 产品id
     */
    private String id;

    /**
     * 产品名字
     */
    private String name;

    @Generated(hash = 367235370)
    public DProducts(Long _id, String recipeId, Long deviceId, String id,
            String name) {
        this._id = _id;
        this.recipeId = recipeId;
        this.deviceId = deviceId;
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 599718793)
    public DProducts() {
    }

    protected DProducts(Parcel in) {
        recipeId = in.readString();
        id = in.readString();
        name = in.readString();
    }

    public static final Creator<DProducts> CREATOR = new Creator<DProducts>() {
        @Override
        public DProducts createFromParcel(Parcel in) {
            return new DProducts(in);
        }

        @Override
        public DProducts[] newArray(int size) {
            return new DProducts[size];
        }
    };

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getRecipeId() {
        return this.recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public Long getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
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
        dest.writeString(recipeId);
        dest.writeString(id);
        dest.writeString(name);
    }
}
