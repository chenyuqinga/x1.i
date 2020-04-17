package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeCategory implements Parcelable{

    /** 类别id */
    @SerializedName("_id")
    @Expose
    private String id;

    /** 父类id(一级父类为0，二级父类id为一级类别id) */
    @SerializedName("parent_id")
    @Expose
    private String parentId;

    /** 类别名称 */
    @SerializedName("name")
    @Expose
    private String name;

    /** 类别介绍 */
    @SerializedName("instructions")
    @Expose
    private String instructions;

    /** 创建者 */
    @SerializedName("creator")
    @Expose
    private String creator;

    /** 创建id */
    @SerializedName("create_id")
    @Expose
    private String createId;

    /** 创建时间 */
    @SerializedName("create_time")
    @Expose
    private String createTime;

    /** 版本号 */
    @SerializedName("version")
    @Expose
    private Integer version;

    /** 类别图片 */
    @SerializedName("images")
    @Expose
    private List<String> images = null;

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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
        dest.writeString(this.parentId);
        dest.writeString(this.name);
        dest.writeString(this.instructions);
        dest.writeString(this.creator);
        dest.writeString(this.createId);
        dest.writeString(this.createTime);
        dest.writeValue(this.version);
        dest.writeStringList(this.images);
    }

    public RecipeCategory() {
    }

    protected RecipeCategory(Parcel in) {
        this.id = in.readString();
        this.parentId = in.readString();
        this.name = in.readString();
        this.instructions = in.readString();
        this.creator = in.readString();
        this.createId = in.readString();
        this.createTime = in.readString();
        this.version = (Integer) in.readValue(Integer.class.getClassLoader());
        this.images = in.createStringArrayList();
    }

    public static final Creator<RecipeCategory> CREATOR = new Creator<RecipeCategory>() {
        @Override
        public RecipeCategory createFromParcel(Parcel source) {
            return new RecipeCategory(source);
        }

        @Override
        public RecipeCategory[] newArray(int size) {
            return new RecipeCategory[size];
        }
    };
}
