package com.fotile.recipe.bean.recipe;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Device implements Parcelable{

	/** 设备产品id */
	@SerializedName("id")
	@Expose
	private String id;

	/** 设备名称 */
	@SerializedName("name")
	@Expose
	private String name;

	/** 设备烹饪时长 */
	@SerializedName("time")
	@Expose
	private String time;

	/** 设备烹饪参数*/
	@SerializedName("title")
	@Expose
	private String title;

	/** 烹饪设备id */
	@SerializedName("device_id")
	@Expose
	private String deviceId;

	/** 设备指令 */
	@SerializedName("autoexec")
	@Expose
	private Autoexec autoexec;

	/** 烹饪过程提示信息 */
	@SerializedName("cooking_prompts_info")
	@Expose
	private List<CookingPromptsInfo> cookingPromptsInfo;

	/**
	 * 产品信息
	 */
	@SerializedName("products")
	@Expose
	private List<Products> products;

	public Device() {
	}

	protected Device(Parcel in) {
		id = in.readString();
		name = in.readString();
		time = in.readString();
		title = in.readString();
		deviceId = in.readString();
		autoexec = in.readParcelable(Autoexec.class.getClassLoader());
		cookingPromptsInfo = in.createTypedArrayList(CookingPromptsInfo.CREATOR);
		products = in.createTypedArrayList(Products.CREATOR);
	}

	public static final Creator<Device> CREATOR = new Creator<Device>() {
		@Override
		public Device createFromParcel(Parcel in) {
			return new Device(in);
		}

		@Override
		public Device[] newArray(int size) {
			return new Device[size];
		}
	};

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Autoexec getAutoexec() {
		return autoexec;
	}

	public void setAutoexec(Autoexec autoexec) {
		this.autoexec = autoexec;
	}

	public List<CookingPromptsInfo> getCookingPromptsInfo() {
		return cookingPromptsInfo;
	}

	public void setCookingPromptsInfo(List<CookingPromptsInfo> cookingPromptsInfo) {
		this.cookingPromptsInfo = cookingPromptsInfo;
	}

	public List<Products> getProducts() {
		return products;
	}

	public void setProducts(List<Products> products) {
		this.products = products;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(time);
		dest.writeString(title);
		dest.writeString(deviceId);
		dest.writeParcelable(autoexec, flags);
		dest.writeTypedList(cookingPromptsInfo);
		dest.writeTypedList(products);
	}
}
