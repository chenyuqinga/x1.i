package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CookingStep implements Parcelable {

    private String recipeName;

    /**
     * 烹饪序号
     */
    @SerializedName("index")
    @Expose
    private Integer index;

    /**
     * 步骤图片
     */
    @SerializedName("images")
    @Expose
    private List<String> images = null;

    /**
     * 时间
     */
    @SerializedName("time")
    @Expose
    private String time;

    /**
     * 小贴士纯文本内容（只有普通菜谱才有）
     */
    @SerializedName("reminderText")
    @Expose
    private String reminderText;

    /**
     * 小贴士
     */
    @SerializedName("reminder")
    @Expose
    private String reminder;

    /**
     * 描述纯文本内容（只有普通菜谱才有）
     */
    @SerializedName("descriptionText")
    @Expose
    private String descriptionText;

    /**
     * 烹饪描述
     */
    @SerializedName("description")
    @Expose
    private String description;

    @Override
    public String toString() {
        String gons = new Gson().toJson(this);
        return gons;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReminderText() {
        return reminderText;
    }

    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.index);
        dest.writeStringList(this.images);
        dest.writeString(this.time);
        dest.writeString(this.reminderText);
        dest.writeString(this.reminder);
        dest.writeString(this.descriptionText);
        dest.writeString(this.description);
    }

    public CookingStep() {
    }

    protected CookingStep(Parcel in) {
        this.index = (Integer) in.readValue(Integer.class.getClassLoader());
        this.images = in.createStringArrayList();
        this.time = in.readString();
        this.reminderText = in.readString();
        this.reminder = in.readString();
        this.descriptionText = in.readString();
        this.description = in.readString();
    }

    public static final Creator<CookingStep> CREATOR = new Creator<CookingStep>() {
        @Override
        public CookingStep createFromParcel(Parcel source) {
            return new CookingStep(source);
        }

        @Override
        public CookingStep[] newArray(int size) {
            return new CookingStep[size];
        }
    };
}
