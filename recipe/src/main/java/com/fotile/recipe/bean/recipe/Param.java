package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Param implements Parcelable{

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("type")
	@Expose
	private String type;

	@SerializedName("param_id")
	@Expose
	private String paramId;

	@SerializedName("autoexec")
	@Expose
	private Autoexec autoexec;

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

	public String getParamId() {
		return paramId;
	}

	public void setParamId(String paramId) {
		this.paramId = paramId;
	}

	public Autoexec getAutoexec() {
		return autoexec;
	}

	public void setAutoexec(Autoexec autoexec) {
		this.autoexec = autoexec;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.type);
		dest.writeString(this.paramId);
		dest.writeParcelable(this.autoexec, flags);
	}

	public Param() {
	}

	protected Param(Parcel in) {
		this.name = in.readString();
		this.type = in.readString();
		this.paramId = in.readString();
		this.autoexec = in.readParcelable(Autoexec.class.getClassLoader());
	}

	public static final Creator<Param> CREATOR = new Creator<Param>() {
		@Override
		public Param createFromParcel(Parcel source) {
			return new Param(source);
		}

		@Override
		public Param[] newArray(int size) {
			return new Param[size];
		}
	};
}
