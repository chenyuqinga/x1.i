package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main implements Parcelable{

	/** 父类名称 */
	@SerializedName("name")
	@Expose
	private String name;

	/** 父类id */
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

	public Main() {
	}

	protected Main(Parcel in) {
		this.name = in.readString();
		this.id = in.readString();
	}

	public static final Creator<Main> CREATOR = new Creator<Main>() {
		@Override
		public Main createFromParcel(Parcel source) {
			return new Main(source);
		}

		@Override
		public Main[] newArray(int size) {
			return new Main[size];
		}
	};
}
