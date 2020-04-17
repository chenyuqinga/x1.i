package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.fotile.recipe.greendao.DCookingPromptsInfoDao;
import com.fotile.recipe.greendao.DDeviceDao;
import com.fotile.recipe.greendao.DProductsDao;
import com.fotile.recipe.greendao.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * 文件名称：DDevice
 * 创建时间：2017-12-04 13:44
 * 文件作者：shihuijuan
 * 功能描述：菜谱设备信息数据表结构
 */
@Entity
public class DDevice implements Parcelable{

    @Id(autoincrement = true)
    private Long _id;
    /**
     * 菜谱ID
     */
    private String recipeId;

    /**
     * 设备产品id
     */
    private String id;

    /**
     *设备名称
     */
    private String name;

    /**
     * 设备烹饪时长
     */
    private String time;

    /**
     * 设备烹饪参数
     */
    private String title;

    /**
     * 烹饪设备id
     */
    private String deviceId;

    /**
     * 设备指令编码类型
     */
    private String autoType;

    /**
     * 设备指令内容
     */
    private String autoValue;
    /**
     * 烹饪步骤提示信息
     */
    @ToMany(joinProperties = {@JoinProperty(name="_id",referencedName = "deviceId")})
    private List<DCookingPromptsInfo> cookingPromptsInfos = null;
    /**
     * 产品信息
     */
    @ToMany(joinProperties = {@JoinProperty(name="_id",referencedName = "deviceId")})
    private List<DProducts> productses = null;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1652741436)
    private transient DDeviceDao myDao;
    @Generated(hash = 1298941683)
    public DDevice(Long _id, String recipeId, String id, String name, String time,
            String title, String deviceId, String autoType, String autoValue) {
        this._id = _id;
        this.recipeId = recipeId;
        this.id = id;
        this.name = name;
        this.time = time;
        this.title = title;
        this.deviceId = deviceId;
        this.autoType = autoType;
        this.autoValue = autoValue;
    }
    @Generated(hash = 1689642350)
    public DDevice() {
    }

    protected DDevice(Parcel in) {
        recipeId = in.readString();
        id = in.readString();
        name = in.readString();
        time = in.readString();
        title = in.readString();
        deviceId = in.readString();
        autoType = in.readString();
        autoValue = in.readString();
        cookingPromptsInfos = in.createTypedArrayList(DCookingPromptsInfo.CREATOR);
        productses = in.createTypedArrayList(DProducts.CREATOR);
    }

    public static final Creator<DDevice> CREATOR = new Creator<DDevice>() {
        @Override
        public DDevice createFromParcel(Parcel in) {
            return new DDevice(in);
        }

        @Override
        public DDevice[] newArray(int size) {
            return new DDevice[size];
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
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDeviceId() {
        return this.deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getAutoType() {
        return this.autoType;
    }
    public void setAutoType(String autoType) {
        this.autoType = autoType;
    }
    public String getAutoValue() {
        return this.autoValue;
    }
    public void setAutoValue(String autoValue) {
        this.autoValue = autoValue;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1444204127)
    public List<DCookingPromptsInfo> getCookingPromptsInfos() {
        if (cookingPromptsInfos == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DCookingPromptsInfoDao targetDao = daoSession.getDCookingPromptsInfoDao();
            List<DCookingPromptsInfo> cookingPromptsInfosNew = targetDao
                    ._queryDDevice_CookingPromptsInfos(_id);
            synchronized (this) {
                if (cookingPromptsInfos == null) {
                    cookingPromptsInfos = cookingPromptsInfosNew;
                }
            }
        }
        return cookingPromptsInfos;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 922777074)
    public synchronized void resetCookingPromptsInfos() {
        cookingPromptsInfos = null;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 22248830)
    public List<DProducts> getProductses() {
        if (productses == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DProductsDao targetDao = daoSession.getDProductsDao();
            List<DProducts> productsesNew = targetDao._queryDDevice_Productses(_id);
            synchronized (this) {
                if (productses == null) {
                    productses = productsesNew;
                }
            }
        }
        return productses;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1134930726)
    public synchronized void resetProductses() {
        productses = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
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
        dest.writeString(time);
        dest.writeString(title);
        dest.writeString(deviceId);
        dest.writeString(autoType);
        dest.writeString(autoValue);
        dest.writeTypedList(cookingPromptsInfos);
        dest.writeTypedList(productses);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 415118237)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDDeviceDao() : null;
    }
}
