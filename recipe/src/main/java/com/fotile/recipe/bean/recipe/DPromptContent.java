package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class DPromptContent implements Parcelable{

	@Id(autoincrement = true)
	private Long _id;
	/**
	 * 菜谱ID
	 */
	private String recipeId;

	/**
	 * 提示信息ID
	 */
	private Long infoId;
	/**
	 * 提示内容索引
	 */
	private String index;

	/**
	 * 提示内容名称（食材）
	 */
	private String name;

	/**
	 * 提示内容值（用量）
	 */
	private String value;

	/** 描述 */
	private String describe;
	/**
	 * 时间
	 */
	private String time;
	/**
	 * 类型
	 */
	private String type;
	@Generated(hash = 1415290979)
	public DPromptContent(Long _id, String recipeId, Long infoId, String index,
			String name, String value, String describe, String time, String type) {
		this._id = _id;
		this.recipeId = recipeId;
		this.infoId = infoId;
		this.index = index;
		this.name = name;
		this.value = value;
		this.describe = describe;
		this.time = time;
		this.type = type;
	}
	@Generated(hash = 221422651)
	public DPromptContent() {
	}

	protected DPromptContent(Parcel in) {
		recipeId = in.readString();
		index = in.readString();
		name = in.readString();
		value = in.readString();
		describe = in.readString();
		time = in.readString();
		type = in.readString();
	}

	public static final Creator<DPromptContent> CREATOR = new Creator<DPromptContent>() {
		@Override
		public DPromptContent createFromParcel(Parcel in) {
			return new DPromptContent(in);
		}

		@Override
		public DPromptContent[] newArray(int size) {
			return new DPromptContent[size];
		}
	};

	public Long get_id() {
		return this._id;
	}
	public void set_id(Long _id) {
		this._id = _id;
	}
	public String getRecipeId() {
		return this.recipeId;
	}
	public void setRecipeId(String recipeId) {
		this.recipeId = recipeId;
	}
	public Long getInfoId() {
		return this.infoId;
	}
	public void setInfoId(Long infoId) {
		this.infoId = infoId;
	}
	public String getIndex() {
		return this.index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescribe() {
		return this.describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getTime() {
		return this.time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getType() {
		return this.type;
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
		dest.writeString(recipeId);
		dest.writeString(index);
		dest.writeString(name);
		dest.writeString(value);
		dest.writeString(describe);
		dest.writeString(time);
		dest.writeString(type);
	}
}
