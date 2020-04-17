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
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;
import com.fotile.recipe.greendao.DaoSession;
import com.fotile.recipe.greendao.DMajorIngredientDao;
import com.fotile.recipe.greendao.DMinorIngredientDao;
import com.fotile.recipe.greendao.DAIngredientDao;
import com.fotile.recipe.greendao.DBIngredientDao;
import com.fotile.recipe.greendao.DCIngredientDao;
import com.fotile.recipe.greendao.DDIngredientDao;
import com.fotile.recipe.greendao.DCookingStepDao;
import com.fotile.recipe.greendao.DReadyStepDao;
import com.fotile.recipe.greendao.DDeviceDao;
import com.fotile.recipe.greendao.DPropertiesDao;
import com.fotile.recipe.greendao.DRecipeDao;

/**
 * 文件名称：DRecipe
 * 创建时间：17-8-29 上午9:47
 * 文件作者：yujiaying
 * 功能描述：DRecipe数据结构
 */
@Entity
public class DRecipe implements Parcelable{
    @Id(autoincrement = true)
    private Long _id;
    /**
     * 菜谱Id
     */
    private String id;
    /**
     * 菜谱名称（短标题）
     */
    private String name;

    private String longName;
    /**
     * 本地菜谱ID
     */

    private String localId;
    /**
     * 菜谱介绍（普通菜谱存放的是富文本内容）
     */

    private String instructions;
    /**
     * 菜谱介绍纯文本（只有普通菜谱该字段才有值）
     */

    private String instructionsText;
    /**
     * 菜谱类型：1：普通 2：智能 3：本地
     */

    private String type;
    /**
     * 菜谱来源： 1：官方 2：用户上传
     */

    private String source;
    /**
     * app用户id
     */
    private String userId;
    /**
     *编辑备注
     */
    private String addRemarks;
    /**
     * 菜谱属性
     */
    @ToOne(joinProperty = "_id")
    private DProperties properties;
    /**
     * 烹饪技巧
     */
    private String tips;
    /**
     * 浏览量
     */
    private String pageViews;
    /**
     * 收藏数量
     */
    private String collectCount;
    /**
     *来源Id
     */
    private String sourceId;
    /**
     * 状态(0:待审核 1：已发布 2:审核失败)
     */
    private String status;
    /**
     * 视频地址
     */
    private String videoUrl;
    /**
     * 创建者
     */
    private String creator;
    /**
     * 上架时间
     */
    private String otCreateTime;
    /**
     *创建Id
     */
    private String createId;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     *版本号
     */
    private String version;
    /**
     * 修改人
     */
    private String modifyBy;
    /**
     * 排序权重
     */
    private int serialNumber;
    /**
     * 修改时间
     */
    private String modifyTime;

    /**
     * 设备信息
     */
    @ToMany(joinProperties = {@JoinProperty(name="id",referencedName = "recipeId")})
    private List<DDevice> devices = null;
    /**
     * 烹饪步骤
     */
    @ToMany(joinProperties = {@JoinProperty(name="id",referencedName = "id")})
    private List<DReadyStep> readySteps = null;
    /**
     * 烹饪步骤
     */
    @ToMany(joinProperties = {@JoinProperty(name="id",referencedName = "id")})
    private List<DCookingStep> cookingSteps = null;

    /**
     * 菜谱D配料
     */
    @ToMany(joinProperties = {@JoinProperty(name="id",referencedName = "id")})
    private List<DDIngredient> typeDIngredients = null;

    /**
     * 菜谱C配料
     */
    @ToMany(joinProperties = {@JoinProperty(name="id",referencedName = "id")})
    private List<DCIngredient> typeCIngredients = null;

    /**
     * 菜谱B配料
     */
    @ToMany(joinProperties = {@JoinProperty(name="id",referencedName = "id")})
    private List<DBIngredient> typeBIngredients = null;

    /**
     * 菜谱A配料
     */
    @ToMany(joinProperties = {@JoinProperty(name="id",referencedName = "id")})
    private List<DAIngredient> typeAIngredients = null;

    /**
     * 辅料
     */
    @ToMany(joinProperties = {@JoinProperty(name="id",referencedName = "id")})
    private List<DMinorIngredient> minorIngredients = null;
    /**
     * 主料
     */
    @ToMany(joinProperties = {@JoinProperty(name="id",referencedName = "id")})
    private List<DMajorIngredient> majorIngredients = null;
    /**
     *内页头图
     */
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> innerImages = null;
    /**
     * 封面小图
     */
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> minImages = null;
    /**
     * 菜谱图片
     */
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> images = null;
    /**
     * 类别ID
     */
    private String categoryId;
    /**
     * 是否为达人秀菜谱
     */
    private boolean isAdults;

    /**
     * 是否为主页的轮播菜谱
     */
    private boolean isHome;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1879521343)
    private transient DRecipeDao myDao;

    @Generated(hash = 1413969586)
    public DRecipe(Long _id, String id, String name, String longName,
            String localId, String instructions, String instructionsText,
            String type, String source, String userId, String addRemarks,
            String tips, String pageViews, String collectCount, String sourceId,
            String status, String videoUrl, String creator, String otCreateTime,
            String createId, String createTime, String version, String modifyBy,
            int serialNumber, String modifyTime, List<String> innerImages,
            List<String> minImages, List<String> images, String categoryId,
            boolean isAdults, boolean isHome) {
        this._id = _id;
        this.id = id;
        this.name = name;
        this.longName = longName;
        this.localId = localId;
        this.instructions = instructions;
        this.instructionsText = instructionsText;
        this.type = type;
        this.source = source;
        this.userId = userId;
        this.addRemarks = addRemarks;
        this.tips = tips;
        this.pageViews = pageViews;
        this.collectCount = collectCount;
        this.sourceId = sourceId;
        this.status = status;
        this.videoUrl = videoUrl;
        this.creator = creator;
        this.otCreateTime = otCreateTime;
        this.createId = createId;
        this.createTime = createTime;
        this.version = version;
        this.modifyBy = modifyBy;
        this.serialNumber = serialNumber;
        this.modifyTime = modifyTime;
        this.innerImages = innerImages;
        this.minImages = minImages;
        this.images = images;
        this.categoryId = categoryId;
        this.isAdults = isAdults;
        this.isHome = isHome;
    }

    @Generated(hash = 1113673443)
    public DRecipe() {
    }

    protected DRecipe(Parcel in) {
        if (in.readByte() == 0) {
            _id = null;
        } else {
            _id = in.readLong();
        }
        id = in.readString();
        name = in.readString();
        longName = in.readString();
        localId = in.readString();
        instructions = in.readString();
        instructionsText = in.readString();
        type = in.readString();
        source = in.readString();
        userId = in.readString();
        addRemarks = in.readString();
        properties = in.readParcelable(DProperties.class.getClassLoader());
        tips = in.readString();
        pageViews = in.readString();
        collectCount = in.readString();
        sourceId = in.readString();
        status = in.readString();
        videoUrl = in.readString();
        creator = in.readString();
        otCreateTime = in.readString();
        createId = in.readString();
        createTime = in.readString();
        version = in.readString();
        modifyBy = in.readString();
        serialNumber = in.readInt();
        modifyTime = in.readString();
        devices = in.createTypedArrayList(DDevice.CREATOR);
        readySteps = in.createTypedArrayList(DReadyStep.CREATOR);
        cookingSteps = in.createTypedArrayList(DCookingStep.CREATOR);
        typeDIngredients = in.createTypedArrayList(DDIngredient.CREATOR);
        typeCIngredients = in.createTypedArrayList(DCIngredient.CREATOR);
        typeBIngredients = in.createTypedArrayList(DBIngredient.CREATOR);
        typeAIngredients = in.createTypedArrayList(DAIngredient.CREATOR);
        minorIngredients = in.createTypedArrayList(DMinorIngredient.CREATOR);
        majorIngredients = in.createTypedArrayList(DMajorIngredient.CREATOR);
        innerImages = in.createStringArrayList();
        minImages = in.createStringArrayList();
        images = in.createStringArrayList();
        categoryId = in.readString();
        isAdults = in.readByte() != 0;
        isHome = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(_id);
        }
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(longName);
        dest.writeString(localId);
        dest.writeString(instructions);
        dest.writeString(instructionsText);
        dest.writeString(type);
        dest.writeString(source);
        dest.writeString(userId);
        dest.writeString(addRemarks);
        dest.writeParcelable(properties, flags);
        dest.writeString(tips);
        dest.writeString(pageViews);
        dest.writeString(collectCount);
        dest.writeString(sourceId);
        dest.writeString(status);
        dest.writeString(videoUrl);
        dest.writeString(creator);
        dest.writeString(otCreateTime);
        dest.writeString(createId);
        dest.writeString(createTime);
        dest.writeString(version);
        dest.writeString(modifyBy);
        dest.writeInt(serialNumber);
        dest.writeString(modifyTime);
        dest.writeTypedList(devices);
        dest.writeTypedList(readySteps);
        dest.writeTypedList(cookingSteps);
        dest.writeTypedList(typeDIngredients);
        dest.writeTypedList(typeCIngredients);
        dest.writeTypedList(typeBIngredients);
        dest.writeTypedList(typeAIngredients);
        dest.writeTypedList(minorIngredients);
        dest.writeTypedList(majorIngredients);
        dest.writeStringList(innerImages);
        dest.writeStringList(minImages);
        dest.writeStringList(images);
        dest.writeString(categoryId);
        dest.writeByte((byte) (isAdults ? 1 : 0));
        dest.writeByte((byte) (isHome ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DRecipe> CREATOR = new Creator<DRecipe>() {
        @Override
        public DRecipe createFromParcel(Parcel in) {
            return new DRecipe(in);
        }

        @Override
        public DRecipe[] newArray(int size) {
            return new DRecipe[size];
        }
    };

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongName() {
        return this.longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getLocalId() {
        return this.localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getInstructions() {
        return this.instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getInstructionsText() {
        return this.instructionsText;
    }

    public void setInstructionsText(String instructionsText) {
        this.instructionsText = instructionsText;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddRemarks() {
        return this.addRemarks;
    }

    public void setAddRemarks(String addRemarks) {
        this.addRemarks = addRemarks;
    }

    public String getTips() {
        return this.tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getPageViews() {
        return this.pageViews;
    }

    public void setPageViews(String pageViews) {
        this.pageViews = pageViews;
    }

    public String getCollectCount() {
        return this.collectCount;
    }

    public void setCollectCount(String collectCount) {
        this.collectCount = collectCount;
    }

    public String getSourceId() {
        return this.sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getOtCreateTime() {
        return this.otCreateTime;
    }

    public void setOtCreateTime(String otCreateTime) {
        this.otCreateTime = otCreateTime;
    }

    public String getCreateId() {
        return this.createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModifyBy() {
        return this.modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    public int getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public List<String> getInnerImages() {
        return this.innerImages;
    }

    public void setInnerImages(List<String> innerImages) {
        this.innerImages = innerImages;
    }

    public List<String> getMinImages() {
        return this.minImages;
    }

    public void setMinImages(List<String> minImages) {
        this.minImages = minImages;
    }

    public List<String> getImages() {
        return this.images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean getIsAdults() {
        return this.isAdults;
    }

    public void setIsAdults(boolean isAdults) {
        this.isAdults = isAdults;
    }

    public boolean getIsHome() {
        return this.isHome;
    }

    public void setIsHome(boolean isHome) {
        this.isHome = isHome;
    }

    @Generated(hash = 111760526)
    private transient Long properties__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1853515986)
    public DProperties getProperties() {
        Long __key = this._id;
        if (properties__resolvedKey == null
                || !properties__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DPropertiesDao targetDao = daoSession.getDPropertiesDao();
            DProperties propertiesNew = targetDao.load(__key);
            synchronized (this) {
                properties = propertiesNew;
                properties__resolvedKey = __key;
            }
        }
        return properties;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 192886323)
    public void setProperties(DProperties properties) {
        synchronized (this) {
            this.properties = properties;
            _id = properties == null ? null : properties.get_id();
            properties__resolvedKey = _id;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1093153610)
    public List<DDevice> getDevices() {
        if (devices == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DDeviceDao targetDao = daoSession.getDDeviceDao();
            List<DDevice> devicesNew = targetDao._queryDRecipe_Devices(id);
            synchronized (this) {
                if (devices == null) {
                    devices = devicesNew;
                }
            }
        }
        return devices;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1428662284)
    public synchronized void resetDevices() {
        devices = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1174285525)
    public List<DReadyStep> getReadySteps() {
        if (readySteps == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DReadyStepDao targetDao = daoSession.getDReadyStepDao();
            List<DReadyStep> readyStepsNew = targetDao._queryDRecipe_ReadySteps(id);
            synchronized (this) {
                if (readySteps == null) {
                    readySteps = readyStepsNew;
                }
            }
        }
        return readySteps;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1413749028)
    public synchronized void resetReadySteps() {
        readySteps = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1317941956)
    public List<DCookingStep> getCookingSteps() {
        if (cookingSteps == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DCookingStepDao targetDao = daoSession.getDCookingStepDao();
            List<DCookingStep> cookingStepsNew = targetDao
                    ._queryDRecipe_CookingSteps(id);
            synchronized (this) {
                if (cookingSteps == null) {
                    cookingSteps = cookingStepsNew;
                }
            }
        }
        return cookingSteps;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1243620280)
    public synchronized void resetCookingSteps() {
        cookingSteps = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1358172810)
    public List<DDIngredient> getTypeDIngredients() {
        if (typeDIngredients == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DDIngredientDao targetDao = daoSession.getDDIngredientDao();
            List<DDIngredient> typeDIngredientsNew = targetDao
                    ._queryDRecipe_TypeDIngredients(id);
            synchronized (this) {
                if (typeDIngredients == null) {
                    typeDIngredients = typeDIngredientsNew;
                }
            }
        }
        return typeDIngredients;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1237212901)
    public synchronized void resetTypeDIngredients() {
        typeDIngredients = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 172625276)
    public List<DCIngredient> getTypeCIngredients() {
        if (typeCIngredients == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DCIngredientDao targetDao = daoSession.getDCIngredientDao();
            List<DCIngredient> typeCIngredientsNew = targetDao
                    ._queryDRecipe_TypeCIngredients(id);
            synchronized (this) {
                if (typeCIngredients == null) {
                    typeCIngredients = typeCIngredientsNew;
                }
            }
        }
        return typeCIngredients;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1341559989)
    public synchronized void resetTypeCIngredients() {
        typeCIngredients = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 838836865)
    public List<DBIngredient> getTypeBIngredients() {
        if (typeBIngredients == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBIngredientDao targetDao = daoSession.getDBIngredientDao();
            List<DBIngredient> typeBIngredientsNew = targetDao
                    ._queryDRecipe_TypeBIngredients(id);
            synchronized (this) {
                if (typeBIngredients == null) {
                    typeBIngredients = typeBIngredientsNew;
                }
            }
        }
        return typeBIngredients;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1809554075)
    public synchronized void resetTypeBIngredients() {
        typeBIngredients = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 47851203)
    public List<DAIngredient> getTypeAIngredients() {
        if (typeAIngredients == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DAIngredientDao targetDao = daoSession.getDAIngredientDao();
            List<DAIngredient> typeAIngredientsNew = targetDao
                    ._queryDRecipe_TypeAIngredients(id);
            synchronized (this) {
                if (typeAIngredients == null) {
                    typeAIngredients = typeAIngredientsNew;
                }
            }
        }
        return typeAIngredients;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 2072004446)
    public synchronized void resetTypeAIngredients() {
        typeAIngredients = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1583005380)
    public List<DMinorIngredient> getMinorIngredients() {
        if (minorIngredients == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DMinorIngredientDao targetDao = daoSession.getDMinorIngredientDao();
            List<DMinorIngredient> minorIngredientsNew = targetDao
                    ._queryDRecipe_MinorIngredients(id);
            synchronized (this) {
                if (minorIngredients == null) {
                    minorIngredients = minorIngredientsNew;
                }
            }
        }
        return minorIngredients;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 29491870)
    public synchronized void resetMinorIngredients() {
        minorIngredients = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1602083891)
    public List<DMajorIngredient> getMajorIngredients() {
        if (majorIngredients == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DMajorIngredientDao targetDao = daoSession.getDMajorIngredientDao();
            List<DMajorIngredient> majorIngredientsNew = targetDao
                    ._queryDRecipe_MajorIngredients(id);
            synchronized (this) {
                if (majorIngredients == null) {
                    majorIngredients = majorIngredientsNew;
                }
            }
        }
        return majorIngredients;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1710142074)
    public synchronized void resetMajorIngredients() {
        majorIngredients = null;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2056575280)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDRecipeDao() : null;
    }


}
