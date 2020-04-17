package com.fotile.recipe.bean.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe implements Parcelable {

    private static final long serialVersionUID = 1221926460333576547L;
    /**
     * 菜谱id
     */
    @SerializedName("_id")
    @Expose
    private String id;

    /**
     * 菜谱名称（短标题）
     */
    @SerializedName("name")
    @Expose
    private String name;

    /**
     * 长标题
     */
    @SerializedName("long_name")
    @Expose
    private String longName;

    /**
     * 本地菜谱id
     */
    @SerializedName("local_id")
    @Expose
    private String localId;

    /**
     * 菜谱介绍（普通菜谱存放的是富文本内容）
     */
    @SerializedName("instructions")
    @Expose
    private String instructions;

    /**
     * 菜谱介绍纯文本（只有普通菜谱该字段才有值）
     */
    @SerializedName("instructionsText")
    @Expose
    private String instructionsText;

    /**
     * 菜谱类型：1：普通 2：智能 3：本地
     */
    @SerializedName("type")
    @Expose
    private String type;

    /**
     * 菜谱来源： 1：官方 2：用户上传
     */
    @SerializedName("source")
    @Expose
    private String source;

    /**
     * app用户id
     */
    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("is_ios")
    @Expose
    private IsIos isIos;

    @SerializedName("is_android")
    @Expose
    private IsAndroid isAndroid;

    @SerializedName("is_pc")
    @Expose
    private IsPc isPc;

    @SerializedName("is_mobile")
    @Expose
    private IsMobile isMobile;

    @SerializedName("is_home")
    @Expose
    private IsHome isHome;

    @SerializedName("add_remarks")
    @Expose
    private String addRemarks;

    @SerializedName("sort")
    @Expose
    private String sort;

    @SerializedName("properties")
    @Expose
    private Properties properties;

    @SerializedName("tips")
    @Expose
    private String tips;

    @SerializedName("pageviews")
    @Expose
    private String pageviews;

    @SerializedName("collect_count")
    @Expose
    private String collectCount;

    @SerializedName("source_id")
    @Expose
    private String sourceId;

    @SerializedName("integral1")
    @Expose
    private String integral1;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("is_link")
    @Expose
    private String isLink;

    @SerializedName("link_type")
    @Expose
    private String linkType;

    @SerializedName("link_val")
    @Expose
    private String linkVal;

    @SerializedName("fit_number")
    @Expose
    private String fitNumber;

    @SerializedName("otstatus")
    @Expose
    private String otstatus;

    @SerializedName("life_home_flag")
    @Expose
    private String lifeHomeFlag;

    @SerializedName("video")
    @Expose
    private String video;

    @SerializedName("video_url")
    @Expose
    private String videoUrl;

    @SerializedName("creator")
    @Expose
    private String creator;

    @SerializedName("ot_create_time")
    @Expose
    private String otCreateTime;

    @SerializedName("create_id")
    @Expose
    private String createId;

    @SerializedName("create_time")
    @Expose
    private String createTime;

    @SerializedName("version")
    @Expose
    private String version;

    @SerializedName("modifyby")
    @Expose
    private String modifyby;

    @SerializedName("serial_number")
    @Expose
    private int serialNumber;

    @SerializedName("moodify_time")
    @Expose
    private String moodifyTime;

    @SerializedName("shared_list")
    @Expose
    private List<String> sharedList = null;

    @SerializedName("param")
    @Expose
    private List<Param> param = null;

    @SerializedName("devices")
    @Expose
    private List<Device> devices = null;

    @SerializedName("end_steps")
    @Expose
    private List<EndStep> endSteps = null;

    @SerializedName("ready_steps")
    @Expose
    private List<ReadyStep> readySteps = null;

    @SerializedName("cooking_steps")
    @Expose
    private List<CookingStep> cookingSteps = null;

    @SerializedName("classification")
    @Expose
    private List<Classification> classification = null;

    @SerializedName("ingredients_background")
    @Expose
    private List<Object> ingredientsBackground = null;
    /**
     * 菜谱D配料
     */
    @SerializedName("D_ingredients")
    @Expose
    private List<DIngredient> typeDIngredients = null;

    /**
     * 菜谱C配料
     */
    @SerializedName("C_ingredients")
    @Expose
    private List<CIngredient> typeCIngredients = null;

    /**
     * 菜谱B配料
     */
    @SerializedName("B_ingredients")
    @Expose
    private List<BIngredient> typeBIngredients = null;

    /**
     * 菜谱A配料
     */
    @SerializedName("A_ingredients")
    @Expose
    private List<AIngredient> typeAIngredients = null;

    /**
     * 菜谱辅料
     */
    @SerializedName("minor_ingredients")
    @Expose
    private List<MinorIngredient> minorIngredients = null;

    /**
     * 菜谱主料
     */
    @SerializedName("major_ingredients")
    @Expose
    private List<MajorIngredient> majorIngredients = null;

    /**
     * 内页头图
     */
    @SerializedName("innerImages")
    @Expose
    private List<String> innerImages = null;

    /**
     * 封面小图
     */
    @SerializedName("minImages")
    @Expose
    private List<String> minImages = null;

    /**
     * 菜谱图片
     */
    @SerializedName("images")
    @Expose
    private List<String> images = null;

    /**
     * 产品id
     */
    @SerializedName("pid")
    @Expose
    private List<String> pid = null;

    public Recipe() {
    }

    protected Recipe(Parcel in) {
        id = in.readString();
        name = in.readString();
        longName = in.readString();
        localId = in.readString();
        instructions = in.readString();
        instructionsText = in.readString();
        type = in.readString();
        source = in.readString();
        userId = in.readString();
        isIos = in.readParcelable(IsIos.class.getClassLoader());
        isAndroid = in.readParcelable(IsAndroid.class.getClassLoader());
        isPc = in.readParcelable(IsPc.class.getClassLoader());
        isMobile = in.readParcelable(IsMobile.class.getClassLoader());
        isHome = in.readParcelable(IsHome.class.getClassLoader());
        addRemarks = in.readString();
        sort = in.readString();
        properties = in.readParcelable(Properties.class.getClassLoader());
        tips = in.readString();
        pageviews = in.readString();
        collectCount = in.readString();
        sourceId = in.readString();
        integral1 = in.readString();
        status = in.readString();
        isLink = in.readString();
        linkType = in.readString();
        linkVal = in.readString();
        fitNumber = in.readString();
        otstatus = in.readString();
        lifeHomeFlag = in.readString();
        video = in.readString();
        videoUrl = in.readString();
        creator = in.readString();
        otCreateTime = in.readString();
        createId = in.readString();
        createTime = in.readString();
        version = in.readString();
        modifyby = in.readString();
        serialNumber = in.readInt();
        moodifyTime = in.readString();
        sharedList = in.createStringArrayList();
        param = in.createTypedArrayList(Param.CREATOR);
        devices = in.createTypedArrayList(Device.CREATOR);
        endSteps = in.createTypedArrayList(EndStep.CREATOR);
        readySteps = in.createTypedArrayList(ReadyStep.CREATOR);
        cookingSteps = in.createTypedArrayList(CookingStep.CREATOR);
        classification = in.createTypedArrayList(Classification.CREATOR);
        typeDIngredients = in.createTypedArrayList(DIngredient.CREATOR);
        typeCIngredients = in.createTypedArrayList(CIngredient.CREATOR);
        typeBIngredients = in.createTypedArrayList(BIngredient.CREATOR);
        typeAIngredients = in.createTypedArrayList(AIngredient.CREATOR);
        minorIngredients = in.createTypedArrayList(MinorIngredient.CREATOR);
        majorIngredients = in.createTypedArrayList(MajorIngredient.CREATOR);
        innerImages = in.createStringArrayList();
        minImages = in.createStringArrayList();
        images = in.createStringArrayList();
        pid = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(longName);
        dest.writeString(localId);
        dest.writeString(instructions);
        dest.writeString(instructionsText);
        dest.writeString(type);
        dest.writeString(source);
        dest.writeString(userId);
        dest.writeParcelable(isIos, flags);
        dest.writeParcelable(isAndroid, flags);
        dest.writeParcelable(isPc, flags);
        dest.writeParcelable(isMobile, flags);
        dest.writeParcelable(isHome, flags);
        dest.writeString(addRemarks);
        dest.writeString(sort);
        dest.writeParcelable(properties, flags);
        dest.writeString(tips);
        dest.writeString(pageviews);
        dest.writeString(collectCount);
        dest.writeString(sourceId);
        dest.writeString(integral1);
        dest.writeString(status);
        dest.writeString(isLink);
        dest.writeString(linkType);
        dest.writeString(linkVal);
        dest.writeString(fitNumber);
        dest.writeString(otstatus);
        dest.writeString(lifeHomeFlag);
        dest.writeString(video);
        dest.writeString(videoUrl);
        dest.writeString(creator);
        dest.writeString(otCreateTime);
        dest.writeString(createId);
        dest.writeString(createTime);
        dest.writeString(version);
        dest.writeString(modifyby);
        dest.writeInt(serialNumber);
        dest.writeString(moodifyTime);
        dest.writeStringList(sharedList);
        dest.writeTypedList(param);
        dest.writeTypedList(devices);
        dest.writeTypedList(endSteps);
        dest.writeTypedList(readySteps);
        dest.writeTypedList(cookingSteps);
        dest.writeTypedList(classification);
        dest.writeTypedList(typeDIngredients);
        dest.writeTypedList(typeCIngredients);
        dest.writeTypedList(typeBIngredients);
        dest.writeTypedList(typeAIngredients);
        dest.writeTypedList(minorIngredients);
        dest.writeTypedList(majorIngredients);
        dest.writeStringList(innerImages);
        dest.writeStringList(minImages);
        dest.writeStringList(images);
        dest.writeStringList(pid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
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

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getInstructionsText() {
        return instructionsText;
    }

    public void setInstructionsText(String instructionsText) {
        this.instructionsText = instructionsText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public IsIos getIsIos() {
        return isIos;
    }

    public void setIsIos(IsIos isIos) {
        this.isIos = isIos;
    }

    public IsAndroid getIsAndroid() {
        return isAndroid;
    }

    public void setIsAndroid(IsAndroid isAndroid) {
        this.isAndroid = isAndroid;
    }

    public IsPc getIsPc() {
        return isPc;
    }

    public void setIsPc(IsPc isPc) {
        this.isPc = isPc;
    }

    public IsMobile getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(IsMobile isMobile) {
        this.isMobile = isMobile;
    }

    public IsHome getIsHome() {
        return isHome;
    }

    public void setIsHome(IsHome isHome) {
        this.isHome = isHome;
    }

    public String getAddRemarks() {
        return addRemarks;
    }

    public void setAddRemarks(String addRemarks) {
        this.addRemarks = addRemarks;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getPageviews() {
        return pageviews;
    }

    public void setPageviews(String pageviews) {
        this.pageviews = pageviews;
    }

    public String getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(String collectCount) {
        this.collectCount = collectCount;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getIntegral1() {
        return integral1;
    }

    public void setIntegral1(String integral1) {
        this.integral1 = integral1;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsLink() {
        return isLink;
    }

    public void setIsLink(String isLink) {
        this.isLink = isLink;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getLinkVal() {
        return linkVal;
    }

    public void setLinkVal(String linkVal) {
        this.linkVal = linkVal;
    }

    public String getFitNumber() {
        return fitNumber;
    }

    public void setFitNumber(String fitNumber) {
        this.fitNumber = fitNumber;
    }

    public String getOtstatus() {
        return otstatus;
    }

    public void setOtstatus(String otstatus) {
        this.otstatus = otstatus;
    }

    public String getLifeHomeFlag() {
        return lifeHomeFlag;
    }

    public void setLifeHomeFlag(String lifeHomeFlag) {
        this.lifeHomeFlag = lifeHomeFlag;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getOtCreateTime() {
        return otCreateTime;
    }

    public void setOtCreateTime(String otCreateTime) {
        this.otCreateTime = otCreateTime;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModifyby() {
        return modifyby;
    }

    public void setModifyby(String modifyby) {
        this.modifyby = modifyby;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMoodifyTime() {
        return moodifyTime;
    }

    public void setMoodifyTime(String moodifyTime) {
        this.moodifyTime = moodifyTime;
    }

    public List<String> getSharedList() {
        return sharedList;
    }

    public void setSharedList(List<String> sharedList) {
        this.sharedList = sharedList;
    }

    public List<Param> getParam() {
        return param;
    }

    public void setParam(List<Param> param) {
        this.param = param;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public List<EndStep> getEndSteps() {
        return endSteps;
    }

    public void setEndSteps(List<EndStep> endSteps) {
        this.endSteps = endSteps;
    }

    public List<ReadyStep> getReadySteps() {
        return readySteps;
    }

    public void setReadySteps(List<ReadyStep> readySteps) {
        this.readySteps = readySteps;
    }

    public List<CookingStep> getCookingSteps() {
        return cookingSteps;
    }

    public void setCookingSteps(List<CookingStep> cookingSteps) {
        this.cookingSteps = cookingSteps;
    }

    public List<Classification> getClassification() {
        return classification;
    }

    public void setClassification(List<Classification> classification) {
        this.classification = classification;
    }

    public List<Object> getIngredientsBackground() {
        return ingredientsBackground;
    }

    public void setIngredientsBackground(List<Object> ingredientsBackground) {
        this.ingredientsBackground = ingredientsBackground;
    }

    public List<DIngredient> getTypeDIngredients() {
        return typeDIngredients;
    }

    public void setTypeDIngredients(List<DIngredient> typeDIngredients) {
        this.typeDIngredients = typeDIngredients;
    }

    public List<CIngredient> getTypeCIngredients() {
        return typeCIngredients;
    }

    public void setTypeCIngredients(List<CIngredient> typeCIngredients) {
        this.typeCIngredients = typeCIngredients;
    }

    public List<BIngredient> getTypeBIngredients() {
        return typeBIngredients;
    }

    public void setTypeBIngredients(List<BIngredient> typeBIngredients) {
        this.typeBIngredients = typeBIngredients;
    }

    public List<AIngredient> getTypeAIngredients() {
        return typeAIngredients;
    }

    public void setTypeAIngredients(List<AIngredient> typeAIngredients) {
        this.typeAIngredients = typeAIngredients;
    }

    public List<MinorIngredient> getMinorIngredients() {
        return minorIngredients;
    }

    public void setMinorIngredients(List<MinorIngredient> minorIngredients) {
        this.minorIngredients = minorIngredients;
    }

    public List<MajorIngredient> getMajorIngredients() {
        return majorIngredients;
    }

    public void setMajorIngredients(List<MajorIngredient> majorIngredients) {
        this.majorIngredients = majorIngredients;
    }

    public List<String> getInnerImages() {
        return innerImages;
    }

    public void setInnerImages(List<String> innerImages) {
        this.innerImages = innerImages;
    }

    public List<String> getMinImages() {
        return minImages;
    }

    public void setMinImages(List<String> minImages) {
        this.minImages = minImages;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getPid() {
        return pid;
    }

    public void setPid(List<String> pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        String gons = new Gson().toJson(this);
        return gons;
    }
}
