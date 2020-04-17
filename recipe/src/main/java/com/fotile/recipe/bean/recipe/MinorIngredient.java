package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MinorIngredient implements Parcelable {

    /** 辅料名称 */
    @SerializedName("unit")
    @Expose
    private String unit;

    /** 辅料用量 */
    @SerializedName("name")
    @Expose
    private String name;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.unit);
        dest.writeString(this.name);
    }

    public MinorIngredient() {
    }

    protected MinorIngredient(Parcel in) {
        this.unit = in.readString();
        this.name = in.readString();
    }

    public static final Creator<MinorIngredient> CREATOR = new Creator<MinorIngredient>() {
        @Override
        public MinorIngredient createFromParcel(Parcel source) {
            return new MinorIngredient(source);
        }

        @Override
        public MinorIngredient[] newArray(int size) {
            return new MinorIngredient[size];
        }
    };
}
