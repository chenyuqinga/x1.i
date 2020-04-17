package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sub implements Parcelable {

	/** 子类名称 */
	@SerializedName("name")
	@Expose
	private String name;

	/** 子类id */
	@SerializedName("id")
	@Expose
	private String id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.id);
	}

	public Sub() {
	}

	protected Sub(Parcel in) {
		this.name = in.readString();
		this.id = in.readString();
	}

	public static final Creator<Sub> CREATOR = new Creator<Sub>() {
		@Override
		public Sub createFromParcel(Parcel source) {
			return new Sub(source);
		}

		@Override
		public Sub[] newArray(int size) {
			return new Sub[size];
		}
	};
}
