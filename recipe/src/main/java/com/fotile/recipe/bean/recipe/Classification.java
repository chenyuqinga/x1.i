package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Classification implements Parcelable{

	/** 父类 */
	@SerializedName("main")
	@Expose
	private Main main;

	/** 子类 */
	@SerializedName("sub")
	@Expose
	private Sub sub;

	public Main getMain() {
		return main;
	}

	public void setMain(Main main) {
		this.main = main;
	}

	public Sub getSub() {
		return sub;
	}

	public void setSub(Sub sub) {
		this.sub = sub;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.main, flags);
		dest.writeParcelable(this.sub, flags);
	}

	public Classification() {
	}

	protected Classification(Parcel in) {
		this.main = in.readParcelable(Main.class.getClassLoader());
		this.sub = in.readParcelable(Sub.class.getClassLoader());
	}

	public static final Creator<Classification> CREATOR = new Creator<Classification>() {
		@Override
		public Classification createFromParcel(Parcel source) {
			return new Classification(source);
		}

		@Override
		public Classification[] newArray(int size) {
			return new Classification[size];
		}
	};
}
