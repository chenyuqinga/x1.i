package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MajorIngredient implements Parcelable {

    /** 主料用量 */
    @SerializedName("unit")
    @Expose
    private String unit;

    /** 主料名称 */
    @SerializedName("name")
    @Expose
    private String name;

    /** (主料、辅料) */
    @SerializedName("type")
    @Expose
    private String type;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.unit);
        dest.writeString(this.name);
        dest.writeString(this.type);
    }

    public MajorIngredient() {
    }

    protected MajorIngredient(Parcel in) {
        this.unit = in.readString();
        this.name = in.readString();
        this.type = in.readString();
    }

    public static final Creator<MajorIngredient> CREATOR = new Creator<MajorIngredient>() {
        @Override
        public MajorIngredient createFromParcel(Parcel source) {
            return new MajorIngredient(source);
        }

        @Override
        public MajorIngredient[] newArray(int size) {
            return new MajorIngredient[size];
        }
    };
}
