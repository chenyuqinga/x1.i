package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;


import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import com.fotile.recipe.greendao.DaoSession;
import com.fotile.recipe.greendao.DPromptContentDao;
import com.fotile.recipe.greendao.DCookingPromptsInfoDao;

@Entity
public class DCookingPromptsInfo implements Parcelable{

	@Id(autoincrement = true)
	private Long _id;
	/**
	 * 菜谱ID
	 */
	private String recipeId;
	/**
	 * device自增ID
	 */
	private Long deviceId;
	/**
	 * 烹饪过程提示信息索引
	 */
	private String index;

	/**
	 * 提示类型
	 */
	private String type;

	/**
	 * 提示文本
	 */
	private String promptText;

	/**
	 * 按钮类型
	 */
	private String buttonType;

	/**
	 * 按钮文本
	 */
	private String buttonText;

	/**
	 * 按钮图片
	 */
	@Convert(columnType = String.class, converter = StringConverter.class)
	private List<String> promptImages;

	/**
	 * 背景图片
	 */
	@Convert(columnType = String.class, converter = StringConverter.class)
	private List<String> backgroupImages;

	/**
	 * 时长
	 */
	private String time;

    /**
     * 描述
     */
    private String describe;
	/**
	 * 提示内容
	 */
	@ToMany(joinProperties = {@JoinProperty(name="_id",referencedName = "infoId")})
	private List<DPromptContent> promptContents = null;
	/** Used to resolve relations */
	@Generated(hash = 2040040024)
	private transient DaoSession daoSession;
	/** Used for active entity operations. */
	@Generated(hash = 1862340639)
	private transient DCookingPromptsInfoDao myDao;
	@Generated(hash = 125619596)
	public DCookingPromptsInfo(Long _id, String recipeId, Long deviceId,
			String index, String type, String promptText, String buttonType,
			String buttonText, List<String> promptImages, List<String> backgroupImages,
			String time, String describe) {
		this._id = _id;
		this.recipeId = recipeId;
		this.deviceId = deviceId;
		this.index = index;
		this.type = type;
		this.promptText = promptText;
		this.buttonType = buttonType;
		this.buttonText = buttonText;
		this.promptImages = promptImages;
		this.backgroupImages = backgroupImages;
		this.time = time;
		this.describe = describe;
	}
	@Generated(hash = 1856628708)
	public DCookingPromptsInfo() {
	}

	protected DCookingPromptsInfo(Parcel in) {
		recipeId = in.readString();
		index = in.readString();
		type = in.readString();
		promptText = in.readString();
		buttonType = in.readString();
		buttonText = in.readString();
		promptImages = in.createStringArrayList();
		backgroupImages = in.createStringArrayList();
		time = in.readString();
		describe = in.readString();
		promptContents = in.createTypedArrayList(DPromptContent.CREATOR);
	}

	public static final Creator<DCookingPromptsInfo> CREATOR = new Creator<DCookingPromptsInfo>() {
		@Override
		public DCookingPromptsInfo createFromParcel(Parcel in) {
			return new DCookingPromptsInfo(in);
		}

		@Override
		public DCookingPromptsInfo[] newArray(int size) {
			return new DCookingPromptsInfo[size];
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
	public Long getDeviceId() {
		return this.deviceId;
	}
	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
	public String getIndex() {
		return this.index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPromptText() {
		return this.promptText;
	}
	public void setPromptText(String promptText) {
		this.promptText = promptText;
	}
	public String getButtonType() {
		return this.buttonType;
	}
	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}
	public String getButtonText() {
		return this.buttonText;
	}
	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}
	public List<String> getPromptImages() {
		return this.promptImages;
	}
	public void setPromptImages(List<String> promptImages) {
		this.promptImages = promptImages;
	}
	public List<String> getBackgroupImages() {
		return this.backgroupImages;
	}
	public void setBackgroupImages(List<String> backgroupImages) {
		this.backgroupImages = backgroupImages;
	}
	public String getTime() {
		return this.time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDescribe() {
		return this.describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	/**
	 * To-many relationship, resolved on first access (and after reset).
	 * Changes to to-many relations are not persisted, make changes to the target entity.
	 */
	@Generated(hash = 1605506755)
	public List<DPromptContent> getPromptContents() {
		if (promptContents == null) {
			final DaoSession daoSession = this.daoSession;
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			DPromptContentDao targetDao = daoSession.getDPromptContentDao();
			List<DPromptContent> promptContentsNew = targetDao
					._queryDCookingPromptsInfo_PromptContents(_id);
			synchronized (this) {
				if (promptContents == null) {
					promptContents = promptContentsNew;
				}
			}
		}
		return promptContents;
	}
	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	@Generated(hash = 459586440)
	public synchronized void resetPromptContents() {
		promptContents = null;
	}
	/**
	 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
	 * Entity must attached to an entity context.
	 */
	@Generated(hash = 128553479)
	public void delete() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.delete(this);
	}
	/**
	 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
	 * Entity must attached to an entity context.
	 */
	@Generated(hash = 1942392019)
	public void refresh() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.refresh(this);
	}
	/**
	 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
	 * Entity must attached to an entity context.
	 */
	@Generated(hash = 713229351)
	public void update() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.update(this);
	}
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(recipeId);
		dest.writeString(index);
		dest.writeString(type);
		dest.writeString(promptText);
		dest.writeString(buttonType);
		dest.writeString(buttonText);
		dest.writeStringList(promptImages);
		dest.writeStringList(backgroupImages);
		dest.writeString(time);
		dest.writeString(describe);
		dest.writeTypedList(promptContents);
	}
	/** called by internal mechanisms, do not call yourself. */
	@Generated(hash = 183581636)
	public void __setDaoSession(DaoSession daoSession) {
		this.daoSession = daoSession;
		myDao = daoSession != null ? daoSession.getDCookingPromptsInfoDao() : null;
	}
}
