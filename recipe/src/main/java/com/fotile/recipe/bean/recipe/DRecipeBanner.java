package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;

/**
 * 文件名称：DRecipeBanner
 * 创建时间：2017-09-29 17:03
 * 文件作者：shihuijuan
 * 功能描述：轮播菜谱数据结构
 */
@Entity
public class DRecipeBanner implements Parcelable{

    @Id(autoincrement = true)
    private Long _id;
    /**
     * 轮播id
     */
    private String id;
    /**
     * 类别名称
     */
    private String name;
    /**
     * 接口参数1
     */
    private String module;
    /**
     * 菜谱id
     */
    private String recipeId;
    /**
     * 接口参数2
     */
    private String api;
    /**
     * 方太官方
     */
    private String creator;
    /**
     * 版本号
     */
    private Integer version;
    /**
     * 创建id
     */
    private String createId;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 图片地址
     */
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> images = null;

    /**
     * 是否为达人秀
     */
    private boolean isAdults;

    @Generated(hash = 2024020373)
    public DRecipeBanner(Long _id, String id, String name, String module,
            String recipeId, String api, String creator, Integer version,
            String createId, String createTime, List<String> images,
            boolean isAdults) {
        this._id = _id;
        this.id = id;
        this.name = name;
        this.module = module;
        this.recipeId = recipeId;
        this.api = api;
        this.creator = creator;
        this.version = version;
        this.createId = createId;
        this.createTime = createTime;
        this.images = images;
        this.isAdults = isAdults;
    }

    @Generated(hash = 1740127624)
    public DRecipeBanner() {
    }

    protected DRecipeBanner(Parcel in) {
        id = in.readString();
        name = in.readString();
        module = in.readString();
        recipeId = in.readString();
        api = in.readString();
        creator = in.readString();
        createId = in.readString();
        createTime = in.readString();
        images = in.createStringArrayList();
        isAdults = in.readByte() != 0;
    }

    public static final Creator<DRecipeBanner> CREATOR = new Creator<DRecipeBanner>() {
        @Override
        public DRecipeBanner createFromParcel(Parcel in) {
            return new DRecipeBanner(in);
        }

        @Override
        public DRecipeBanner[] newArray(int size) {
            return new DRecipeBanner[size];
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

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getRecipeId() {
        return this.recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getApi() {
        return this.api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCreateId() {
        return this.createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<String> getImages() {
        return this.images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public boolean getIsAdults() {
        return this.isAdults;
    }

    public void setIsAdults(boolean isAdults) {
        this.isAdults = isAdults;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(module);
        dest.writeString(recipeId);
        dest.writeString(api);
        dest.writeString(creator);
        dest.writeString(createId);
        dest.writeString(createTime);
        dest.writeStringList(images);
        dest.writeByte((byte) (isAdults ? 1 : 0));
    }
}
