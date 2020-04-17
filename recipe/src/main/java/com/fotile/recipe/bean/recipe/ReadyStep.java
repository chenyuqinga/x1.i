package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReadyStep implements Parcelable {

    /** 准备步骤描述 */
    @SerializedName("description")
    @Expose
    private String description;

    /** 描述纯文本内容（只有普通菜谱才有） */
    @SerializedName("descriptionText")
    @Expose
    private String descriptionText;

    /** 小贴士 */
    @SerializedName("reminder")
    @Expose
    private String reminder;

    /** 小贴士纯文本内容（只有普通菜谱才有） */
    @SerializedName("reminderText")
    @Expose
    private String reminderText;

    /** 时间 */
    @SerializedName("time")
    @Expose
    private String time;

    /** 准备步骤图片 */
    @SerializedName("images")
    @Expose
    private List<String> images = null;

    /** 准备步骤序号 */
    @SerializedName("index")
    @Expose
    private Integer index;

    public ReadyStep() {
    }

    protected ReadyStep(Parcel in) {
        description = in.readString();
        descriptionText = in.readString();
        reminder = in.readString();
        reminderText = in.readString();
        time = in.readString();
        images = in.createStringArrayList();
        if (in.readByte() == 0) {
            index = null;
        } else {
            index = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(descriptionText);
        dest.writeString(reminder);
        dest.writeString(reminderText);
        dest.writeString(time);
        dest.writeStringList(images);
        if (index == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(index);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReadyStep> CREATOR = new Creator<ReadyStep>() {
        @Override
        public ReadyStep createFromParcel(Parcel in) {
            return new ReadyStep(in);
        }

        @Override
        public ReadyStep[] newArray(int size) {
            return new ReadyStep[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getReminderText() {
        return reminderText;
    }

    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
