package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EndStep implements Parcelable{

    /** 描述 */
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

    /** 图片 */
    @SerializedName("images")
    @Expose
    private List<String> images = null;

    /** 序号 */
    @SerializedName("index")
    @Expose
    private String index;

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

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeString(this.descriptionText);
        dest.writeString(this.reminder);
        dest.writeString(this.reminderText);
        dest.writeString(this.time);
        dest.writeStringList(this.images);
        dest.writeString(this.index);
    }

    public EndStep() {
    }

    protected EndStep(Parcel in) {
        this.description = in.readString();
        this.descriptionText = in.readString();
        this.reminder = in.readString();
        this.reminderText = in.readString();
        this.time = in.readString();
        this.images = in.createStringArrayList();
        this.index = in.readString();
    }

    public static final Creator<EndStep> CREATOR = new Creator<EndStep>() {
        @Override
        public EndStep createFromParcel(Parcel source) {
            return new EndStep(source);
        }

        @Override
        public EndStep[] newArray(int size) {
            return new EndStep[size];
        }
    };
}
