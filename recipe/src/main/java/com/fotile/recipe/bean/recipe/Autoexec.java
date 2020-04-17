package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Autoexec implements Parcelable {

	/** 设备指令编码类型 */
	@SerializedName("type")
	@Expose
	private String type;

	/** 设备指令内容 */
	@SerializedName("value")
	@Expose
	private String value;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.type);
		dest.writeString(this.value);
	}

	public Autoexec() {
	}

	protected Autoexec(Parcel in) {
		this.type = in.readString();
		this.value = in.readString();
	}

	public static final Creator<Autoexec> CREATOR = new Creator<Autoexec>() {
		@Override
		public Autoexec createFromParcel(Parcel source) {
			return new Autoexec(source);
		}

		@Override
		public Autoexec[] newArray(int size) {
			return new Autoexec[size];
		}
	};
}
