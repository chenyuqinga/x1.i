package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Properties implements Parcelable{

    @SerializedName("label")
    @Expose
    private List<Object> label = null;

    @SerializedName("time_unit")
    @Expose
    private String timeUnit;

    @SerializedName("difficulty")
    @Expose
    private String difficulty;

    @SerializedName("cooking_time")
    @Expose
    private String cookingTime;

    public List<Object> getLabel() {
        return label;
    }

    public void setLabel(List<Object> label) {
        this.label = label;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.label);
        dest.writeString(this.timeUnit);
        dest.writeString(this.difficulty);
        dest.writeString(this.cookingTime);
    }

    public Properties() {
    }

    protected Properties(Parcel in) {
        this.label = new ArrayList<Object>();
        in.readList(this.label, Object.class.getClassLoader());
        this.timeUnit = in.readString();
        this.difficulty = in.readString();
        this.cookingTime = in.readString();
    }

    public static final Creator<Properties> CREATOR = new Creator<Properties>() {
        @Override
        public Properties createFromParcel(Parcel source) {
            return new Properties(source);
        }

        @Override
        public Properties[] newArray(int size) {
            return new Properties[size];
        }
    };
    @Override
    public String toString() {
        String gons = new Gson().toJson(this);
        return gons;
    }
}
