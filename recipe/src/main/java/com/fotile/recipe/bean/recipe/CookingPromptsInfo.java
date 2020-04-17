package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CookingPromptsInfo implements Parcelable {

    /**
     * 烹饪过程提示信息索引
     */
    @SerializedName("index")
    @Expose
    private String index;

    /**
     * 提示类型
     */
    @SerializedName("type")
    @Expose
    private String type;

    /**
     * 提示文本
     */
    @SerializedName("prompt_text")
    @Expose
    private String promptText;

    /**
     * 按钮类型
     */
    @SerializedName("button_type")
    @Expose
    private String buttonType;

    /**
     * 按钮文本
     */
    @SerializedName("button_text")
    @Expose
    private String buttonText;

    /**
     * 按钮图片
     */
    @SerializedName("prompt_images")
    @Expose
    private List<String> promptImages;

    /**
     * 背景图片
     */
    @SerializedName("backgroup_images")
    @Expose
    private List<String> backgroupImages;

    /**
     * 提示内容
     */
    @SerializedName("prompt_content")
    @Expose
    private List<PromptContent> promptContent;

    /**
     * 时长
     */
    @SerializedName("time")
    @Expose
    private String time;

    /**
     * 描述
     */
    @SerializedName("describe")
    @Expose
    private String describe;

    private String recipeName;

    public CookingPromptsInfo() {
    }

    protected CookingPromptsInfo(Parcel in) {
        index = in.readString();
        type = in.readString();
        promptText = in.readString();
        buttonType = in.readString();
        buttonText = in.readString();
        promptImages = in.createStringArrayList();
        backgroupImages = in.createStringArrayList();
        promptContent = in.createTypedArrayList(PromptContent.CREATOR);
        time = in.readString();
        describe = in.readString();
    }

    public static final Creator<CookingPromptsInfo> CREATOR = new Creator<CookingPromptsInfo>() {
        @Override
        public CookingPromptsInfo createFromParcel(Parcel in) {
            return new CookingPromptsInfo(in);
        }

        @Override
        public CookingPromptsInfo[] newArray(int size) {
            return new CookingPromptsInfo[size];
        }
    };

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPromptText() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    public String getButtonType() {
        return buttonType;
    }

    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public List<String> getPromptImages() {
        return promptImages;
    }

    public void setPromptImages(List<String> promptImages) {
        this.promptImages = promptImages;
    }

    public List<String> getBackgroupImages() {
        return backgroupImages;
    }

    public void setBackgroupImages(List<String> backgroupImages) {
        this.backgroupImages = backgroupImages;
    }

    public List<PromptContent> getPromptContent() {
        return promptContent;
    }

    public void setPromptContent(List<PromptContent> promptContent) {
        this.promptContent = promptContent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeName() {
        return recipeName;
    }

    @Override
    public String toString() {
        String gons = new Gson().toJson(this);
        return gons;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(index);
        dest.writeString(type);
        dest.writeString(promptText);
        dest.writeString(buttonType);
        dest.writeString(buttonText);
        dest.writeStringList(promptImages);
        dest.writeStringList(backgroupImages);
        dest.writeTypedList(promptContent);
        dest.writeString(time);
        dest.writeString(describe);
    }
}
