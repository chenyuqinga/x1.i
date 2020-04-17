package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PromptContent implements Parcelable{

	/** 提示内容索引 */
	@SerializedName("index")
	@Expose
	private String index;

	/** 提示内容名称（食材） */
	@SerializedName("name")
	@Expose
	private String name;

	/** 提示内容值（用量） */
	@SerializedName("value")
	@Expose
	private String value;

	/** 描述 */
	@SerializedName("describe")
	@Expose
	private String describe;

	@SerializedName("time")
	@Expose
	private String time;

	@SerializedName("type")
	@Expose
	private String type;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
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
		dest.writeString(this.index);
		dest.writeString(this.name);
		dest.writeString(this.value);
		dest.writeString(this.describe);
		dest.writeString(this.time);
		dest.writeString(this.type);
	}

	public PromptContent() {
	}

	protected PromptContent(Parcel in) {
		this.index = in.readString();
		this.name = in.readString();
		this.value = in.readString();
		this.describe = in.readString();
		this.time = in.readString();
		this.type = in.readString();
	}

	public static final Creator<PromptContent> CREATOR = new Creator<PromptContent>() {
		@Override
		public PromptContent createFromParcel(Parcel source) {
			return new PromptContent(source);
		}

		@Override
		public PromptContent[] newArray(int size) {
			return new PromptContent[size];
		}
	};
}
