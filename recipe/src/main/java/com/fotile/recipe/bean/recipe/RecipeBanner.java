package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 文件名称：RecipeBanner
 * 创建时间：17-9-22 下午2:15
 * 文件作者：zhangqiang
 * 功能描述：菜谱轮播的数据结构
 */
public class RecipeBanner implements Parcelable {

    /**
     * 轮播id
     */
    @SerializedName("_id")
    @Expose
    private String id;

    /**
     * 类别名称
     */
    @SerializedName("name")
    @Expose
    private String name;

    /**
     * 接口参数1
     */
    @SerializedName("module")
    @Expose
    private String module;

    /**
     * 菜谱id
     */
    @SerializedName("id")
    @Expose
    private String recipeId;

    /**
     * 接口参数2
     */
    @SerializedName("api")
    @Expose
    private String api;

    /**
     * 方太官方
     */
    @SerializedName("creator")
    @Expose
    private String creator;

    /**
     * 版本号
     */
    @SerializedName("version")
    @Expose
    private Integer version;

    /**
     * 创建id
     */
    @SerializedName("create_id")
    @Expose
    private String createId;

    /**
     * 创建时间
     */
    @SerializedName("create_time")
    @Expose
    private String createTime;

    /**
     * 图片地址
     */
    @SerializedName("images")
    @Expose
    private List<String> images;

    @Override
    public String toString() {
        String gson = new Gson().toJson(this);
        return gson;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.module);
        dest.writeString(this.recipeId);
        dest.writeString(this.api);
        dest.writeString(this.creator);
        dest.writeValue(this.version);
        dest.writeString(this.createId);
        dest.writeString(this.createTime);
        dest.writeStringList(this.images);
    }

    public RecipeBanner() {
    }

    protected RecipeBanner(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.module = in.readString();
        this.recipeId = in.readString();
        this.api = in.readString();
        this.creator = in.readString();
        this.version = (Integer) in.readValue(Integer.class.getClassLoader());
        this.createId = in.readString();
        this.createTime = in.readString();
        this.images = in.createStringArrayList();
    }

    public static final Parcelable.Creator<RecipeBanner> CREATOR = new Parcelable.Creator<RecipeBanner>() {
        @Override
        public RecipeBanner createFromParcel(Parcel source) {
            return new RecipeBanner(source);
        }

        @Override
        public RecipeBanner[] newArray(int size) {
            return new RecipeBanner[size];
        }
    };
}
