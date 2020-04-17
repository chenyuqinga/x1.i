package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名称：RecipeBanner
 * 创建时间：17-9-21 下午4:58
 * 文件作者：zhangqiang
 * 功能描述：菜谱运营位的数据结构
 */
public class RecipeBannerId implements Parcelable {

    /**
     * 类别id
     */
    @SerializedName("_id")
    @Expose
    private String id;

    /**
     * 类别名称（所属设备名称）
     */
    @SerializedName("name")
    @Expose
    private String name;

    /**
     * 创建者
     */
    @SerializedName("creator")
    @Expose
    private String creator;

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
     * 版本号
     */
    @SerializedName("version")
    @Expose
    private Integer version;

    /**
     * 菜谱运营位ID
     */
    @SerializedName("content")
    @Expose
    private List<ContentId> content = null;

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

    public List<ContentId> getContent() {
        return content;
    }

    public void setContent(List<ContentId> content) {
        this.content = content;
    }

    public class ContentId {
        /**
         * 菜谱运营位ID
         */
        @SerializedName("id")
        @Expose
        private String id;

        /**
         * 菜谱运营位index
         */
        @SerializedName("index")
        @Expose
        private String index;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.creator);
        dest.writeString(this.createId);
        dest.writeString(this.createTime);
        dest.writeValue(this.version);
        dest.writeList(this.content);
    }

    public RecipeBannerId() {
    }

    protected RecipeBannerId(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.creator = in.readString();
        this.createId = in.readString();
        this.createTime = in.readString();
        this.version = (Integer) in.readValue(Integer.class.getClassLoader());
        this.content = new ArrayList<ContentId>();
        in.readList(this.content, ContentId.class.getClassLoader());
    }

    public static final Creator<RecipeBannerId> CREATOR = new Creator<RecipeBannerId>() {
        @Override
        public RecipeBannerId createFromParcel(Parcel source) {
            return new RecipeBannerId(source);
        }

        @Override
        public RecipeBannerId[] newArray(int size) {
            return new RecipeBannerId[size];
        }
    };
}
