package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;

/**
 * 文件名称：DCookingStep
 * 创建时间：17-8-29 上午9:47
 * 文件作者：yujiaying
 * 功能描述：DCookingStep数据结构
 */

@Entity
public class DReadyStep implements Parcelable{

    @Id(autoincrement = true)
    private Long _id;

    /**
     * 菜谱ID
     */
    private String id;

    /**
     * 烹饪序号
     */
    private Integer index;

    /**
     * 步骤图片
     */
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> images = null;

    /**
     *  时间
     */
    private String time;

    /**
     * 小贴士纯文本内容（只有普通菜谱才有）
     *
     */
    private String reminderText;

    /**
     * 小贴士
     */
    private String reminder;

    /**
     * 描述纯文本内容（只有普通菜谱才有）
     */
    private String descriptionText;

    /**
     * 烹饪描述
     */
    private String description;

    @Generated(hash = 1134365810)
    public DReadyStep(Long _id, String id, Integer index, List<String> images,
            String time, String reminderText, String reminder,
            String descriptionText, String description) {
        this._id = _id;
        this.id = id;
        this.index = index;
        this.images = images;
        this.time = time;
        this.reminderText = reminderText;
        this.reminder = reminder;
        this.descriptionText = descriptionText;
        this.description = description;
    }

    @Generated(hash = 1327029517)
    public DReadyStep() {
    }

    protected DReadyStep(Parcel in) {
        if (in.readByte() == 0) {
            _id = null;
        } else {
            _id = in.readLong();
        }
        id = in.readString();
        if (in.readByte() == 0) {
            index = null;
        } else {
            index = in.readInt();
        }
        images = in.createStringArrayList();
        time = in.readString();
        reminderText = in.readString();
        reminder = in.readString();
        descriptionText = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(_id);
        }
        dest.writeString(id);
        if (index == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(index);
        }
        dest.writeStringList(images);
        dest.writeString(time);
        dest.writeString(reminderText);
        dest.writeString(reminder);
        dest.writeString(descriptionText);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DReadyStep> CREATOR = new Creator<DReadyStep>() {
        @Override
        public DReadyStep createFromParcel(Parcel in) {
            return new DReadyStep(in);
        }

        @Override
        public DReadyStep[] newArray(int size) {
            return new DReadyStep[size];
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

    public Integer getIndex() {
        return this.index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public List<String> getImages() {
        return this.images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReminderText() {
        return this.reminderText;
    }

    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }

    public String getReminder() {
        return this.reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getDescriptionText() {
        return this.descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
