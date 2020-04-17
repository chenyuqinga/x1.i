package com.fotile.recipe.uitl.db;

import android.content.Context;


import com.fotile.recipe.bean.recipe.AIngredient;
import com.fotile.recipe.bean.recipe.Autoexec;
import com.fotile.recipe.bean.recipe.BIngredient;
import com.fotile.recipe.bean.recipe.CIngredient;
import com.fotile.recipe.bean.recipe.Classification;
import com.fotile.recipe.bean.recipe.CookingPromptsInfo;
import com.fotile.recipe.bean.recipe.CookingStep;
import com.fotile.recipe.bean.recipe.DAIngredient;
import com.fotile.recipe.bean.recipe.DBIngredient;
import com.fotile.recipe.bean.recipe.DCIngredient;
import com.fotile.recipe.bean.recipe.DCookingPromptsInfo;
import com.fotile.recipe.bean.recipe.DCookingStep;
import com.fotile.recipe.bean.recipe.DDIngredient;
import com.fotile.recipe.bean.recipe.DDevice;
import com.fotile.recipe.bean.recipe.DIngredient;
import com.fotile.recipe.bean.recipe.DMajorIngredient;
import com.fotile.recipe.bean.recipe.DMinorIngredient;
import com.fotile.recipe.bean.recipe.DProducts;
import com.fotile.recipe.bean.recipe.DPromptContent;
import com.fotile.recipe.bean.recipe.DProperties;
import com.fotile.recipe.bean.recipe.DReadyStep;
import com.fotile.recipe.bean.recipe.DRecipe;
import com.fotile.recipe.bean.recipe.DRecipeBanner;
import com.fotile.recipe.bean.recipe.Device;
import com.fotile.recipe.bean.recipe.MajorIngredient;
import com.fotile.recipe.bean.recipe.MinorIngredient;
import com.fotile.recipe.bean.recipe.Products;
import com.fotile.recipe.bean.recipe.PromptContent;
import com.fotile.recipe.bean.recipe.Properties;
import com.fotile.recipe.bean.recipe.ReadyStep;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.bean.recipe.RecipeBanner;
import com.fotile.recipe.uitl.RecipeConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * 文件名称：DataBaseUtil
 * 创建时间：17-8-29 上午9:47
 * 文件作者：yujiaying
 * 功能描述：网络菜谱数据库操作类（达人秀、轮播位、我的上传）
 *          开发中轮播菜谱没有保存到该数据库
 */
public class DataNetBaseUtil {
    /**
     * 单例对象
     */
    private static DataNetBaseUtil instance = null;
    private RecipeNetDaoManager recipeDaoManager;
    /**
     * 数据观察者键值对列表,键:观察者,值:观察的数据表(标识)
     */
    private Map<DatabaseObserver, String> observerMap = new HashMap<>();

    private DataNetBaseUtil(){

    }

    /**
     * 单例模式,获取对象实例
     *
     * @return
     */
    public static DataNetBaseUtil getInstance() {
        if (instance == null) {
            instance = new DataNetBaseUtil();
        }
        return instance;
    }

    /**
     * 初始化,创建数据库管理类实例
     *
     * @param context
     */
    public void init(Context context) {
        recipeDaoManager = new RecipeNetDaoManager(context);
    }

    /**
     * 注册数据变化观察者
     *
     * @param type     表类型(菜谱,常用模式等)
     * @param observer 观察者
     */
    public void registerDatabaseObserver(String type, DatabaseObserver observer) {
        observerMap.put(observer, type);
    }

    /**
     * 取消注册数据变化观察者,从列表中移除
     *
     * @param observer
     */
    public void unregisterDatabaseObserver(DatabaseObserver observer) {
        observerMap.remove(observer);
    }


    /**
     * 插入菜谱
     *
     * @param recipe     菜谱
     * @param type 菜谱类型:0是否为主页轮播菜谱,1为达人秀菜谱,2为本地菜谱/收藏菜谱/上传菜谱
     * @return
     */
    public long insert(Recipe recipe, int type) {
        DRecipe dRecipe = new DRecipe();
        dRecipe.setId(recipe.getId());
        dRecipe.setName(recipe.getName());
        dRecipe.setLongName(recipe.getLongName());
        dRecipe.setLocalId(recipe.getLocalId());
        dRecipe.setInstructions(recipe.getInstructions());
        dRecipe.setInstructionsText(recipe.getInstructionsText());
        dRecipe.setType(recipe.getType());
        dRecipe.setSource(recipe.getSource());
        dRecipe.setUserId(recipe.getUserId());
        dRecipe.setAddRemarks(recipe.getAddRemarks());
        dRecipe.setTips(recipe.getTips());
        dRecipe.setPageViews(recipe.getPageviews());
        dRecipe.setCollectCount(recipe.getCollectCount());
        dRecipe.setSourceId(recipe.getSourceId());
        dRecipe.setStatus(recipe.getStatus());
        dRecipe.setOtCreateTime(recipe.getOtCreateTime());
        dRecipe.setCreateId(recipe.getCreateId());
        dRecipe.setCreateTime(recipe.getCreateTime());
        dRecipe.setVersion(recipe.getVersion());
        dRecipe.setVideoUrl(recipe.getVideo());
        dRecipe.setModifyBy(recipe.getModifyby());
        dRecipe.setSerialNumber(recipe.getSerialNumber());
        dRecipe.setModifyTime(recipe.getMoodifyTime());
        DProperties dProperties = new DProperties();
        dProperties.setId(recipe.getId());
        Properties properties = recipe.getProperties();
        if (properties != null) {
            dProperties.setTimeUnit(properties.getTimeUnit());
            dProperties.setDifficulty(properties.getDifficulty());
            dProperties.setCookingTime(properties.getCookingTime());
        }
        dRecipe.setProperties(dProperties);
        recipeDaoManager.insert(dProperties);
        //设备信息
        List<Device> deviceList = recipe.getDevices();
        if (null != deviceList){
            for (int i = 0; i < deviceList.size(); i++) {
                DDevice dDevice = new DDevice();
                dDevice.setRecipeId(recipe.getId());
                Device device = deviceList.get(i);
                if (device != null) {
                    dDevice.setId(device.getId());
                    dDevice.setName(device.getName());
                    dDevice.setTime(device.getTime());
                    dDevice.setTitle(device.getTitle());
                    dDevice.setDeviceId(device.getDeviceId());
                    Autoexec autoexec = device.getAutoexec();
                    if (autoexec != null) {
                        dDevice.setAutoType(autoexec.getType());
                        dDevice.setAutoValue(autoexec.getValue());
                    }
                    recipeDaoManager.insert(dDevice);
                    List<CookingPromptsInfo> cookingPromptsInfos = device.getCookingPromptsInfo();
                    if (cookingPromptsInfos != null) {
                        for (CookingPromptsInfo info : cookingPromptsInfos) {
                            DCookingPromptsInfo dCookingPromptsInfo = new DCookingPromptsInfo();
                            dCookingPromptsInfo.setRecipeId(recipe.getId());
                            dCookingPromptsInfo.setDeviceId(dDevice.get_id());
                            if (info != null) {
                                dCookingPromptsInfo.setIndex(info.getIndex());
                                dCookingPromptsInfo.setType(info.getType());
                                dCookingPromptsInfo.setDescribe(info.getDescribe());
                                dCookingPromptsInfo.setButtonText(info.getButtonText());
                                dCookingPromptsInfo.setButtonType(info.getButtonType());
                                dCookingPromptsInfo.setPromptText(info.getPromptText());
                                dCookingPromptsInfo.setBackgroupImages(info.getBackgroupImages());
                                dCookingPromptsInfo.setPromptImages(info.getPromptImages());
                                dCookingPromptsInfo.setTime(info.getTime());
                                recipeDaoManager.insert(dCookingPromptsInfo);
                                List<PromptContent> promptContents = info.getPromptContent();
                                if (promptContents != null) {
                                    for (PromptContent content : promptContents) {
                                        DPromptContent dPromptContent = new DPromptContent();
                                        dPromptContent.setRecipeId(recipe.getId());
                                        if (content != null) {
                                            dPromptContent.setInfoId(dCookingPromptsInfo.get_id());
                                            dPromptContent.setIndex(content.getIndex());
                                            dPromptContent.setTime(content.getTime());
                                            dPromptContent.setType(content.getType());
                                            dPromptContent.setName(content.getName());
                                            dPromptContent.setValue(content.getValue());
                                            dPromptContent.setDescribe(content.getDescribe());
                                        }
                                        recipeDaoManager.insert(dPromptContent);
                                    }
                                }
                            }

                        }
                    }
                    List<Products> productsList = device.getProducts();
                    if (productsList != null) {
                        for (Products products : productsList) {
                            DProducts dProducts = new DProducts();
                            dProducts.setRecipeId(recipe.getId());
                            dProducts.setDeviceId(dDevice.get_id());
                            if (products != null) {
                                dProducts.setId(products.getId());
                                dProducts.setName(products.getName());
                            }
                            recipeDaoManager.insert(dProducts);
                        }
                    }
                }
            }
        }
        //烹饪准备步骤
        List<ReadyStep> readyStepList = recipe.getReadySteps();
        if (null != readyStepList){
            for (int i = 0; i < readyStepList.size(); i++) {
                DReadyStep dReadyStep = new DReadyStep();
                dReadyStep.setId(recipe.getId());
                ReadyStep readyStep = readyStepList.get(i);
                if (readyStep != null) {
                    dReadyStep.setIndex(readyStep.getIndex());
                    dReadyStep.setImages(readyStep.getImages());
                    dReadyStep.setTime(readyStep.getTime());
                    dReadyStep.setReminderText(readyStep.getReminderText());
                    dReadyStep.setReminder(readyStep.getReminder());
                    dReadyStep.setDescriptionText(readyStep.getDescriptionText());
                    dReadyStep.setDescription(readyStep.getDescription());
                }
                recipeDaoManager.insert(dReadyStep);
            }
        }
        //烹饪步骤
        List<CookingStep> cookingStepList = recipe.getCookingSteps();
        if (null != cookingStepList){
            for (int i = 0; i < cookingStepList.size(); i++) {
                DCookingStep dCookingStep = new DCookingStep();
                dCookingStep.setId(recipe.getId());
                CookingStep cookingStep = cookingStepList.get(i);
                if (cookingStep != null) {
                    dCookingStep.setIndex(cookingStep.getIndex());
                    dCookingStep.setImages(cookingStep.getImages());
                    dCookingStep.setTime(cookingStep.getTime());
                    dCookingStep.setReminderText(cookingStep.getReminderText());
                    dCookingStep.setReminder(cookingStep.getReminder());
                    dCookingStep.setDescriptionText(cookingStep.getDescriptionText());
                    dCookingStep.setDescription(cookingStep.getDescription());
                }
                recipeDaoManager.insert(dCookingStep);
            }
        }
        //菜谱D配料
        List<DIngredient> dIngredientList = recipe.getTypeDIngredients();
        if (null != dIngredientList){
            for (int i = 0; i < dIngredientList.size(); i++) {
                DDIngredient ddIngredient = new DDIngredient();
                ddIngredient.setId(recipe.getId());
                DIngredient dIngredient = dIngredientList.get(i);
                if (dIngredient != null) {
                    ddIngredient.setUnit(dIngredient.getUnit());
                    ddIngredient.setName(dIngredient.getName());
                }
                recipeDaoManager.insert(ddIngredient);
            }
        }
        //菜谱C配料
        List<CIngredient> cIngredientList = recipe.getTypeCIngredients();
        if (null != cIngredientList){
            for (int i = 0; i < cIngredientList.size(); i++) {
                DCIngredient dcIngredient = new DCIngredient();
                dcIngredient.setId(recipe.getId());
                CIngredient cIngredient = cIngredientList.get(i);
                if (cIngredient != null) {
                    dcIngredient.setUnit(cIngredient.getUnit());
                    dcIngredient.setName(cIngredient.getName());
                }
                recipeDaoManager.insert(dcIngredient);
            }
        }
        //菜谱B配料
        List<BIngredient> bIngredientList = recipe.getTypeBIngredients();
        if (null != bIngredientList){
            for (int i = 0; i < bIngredientList.size(); i++) {
                DBIngredient dbIngredient = new DBIngredient();
                dbIngredient.setId(recipe.getId());
                BIngredient bIngredient = bIngredientList.get(i);
                if (bIngredient != null) {
                    dbIngredient.setUnit(bIngredient.getUnit());
                    dbIngredient.setName(bIngredient.getName());
                }
                recipeDaoManager.insert(dbIngredient);
            }
        }
        //菜谱A配料
        List<AIngredient> aIngredientList = recipe.getTypeAIngredients();
        if (null != aIngredientList){
            for (int i = 0; i < aIngredientList.size(); i++) {
                DAIngredient daIngredient = new DAIngredient();
                daIngredient.setId(recipe.getId());
                AIngredient aIngredient = aIngredientList.get(i);
                if (aIngredient != null) {
                    daIngredient.setUnit(aIngredient.getUnit());
                    daIngredient.setName(aIngredient.getName());
                }
                recipeDaoManager.insert(daIngredient);
            }
        }
        //菜谱辅料
        List<MinorIngredient> minorIngredientList = recipe.getMinorIngredients();
        if (null != minorIngredientList){
            for (int i = 0; i < minorIngredientList.size(); i++) {
                DMinorIngredient dMinorIngredient = new DMinorIngredient();
                dMinorIngredient.setId(recipe.getId());
                MinorIngredient minorIngredient = minorIngredientList.get(i);
                if (minorIngredient != null) {
                    dMinorIngredient.setUnit(minorIngredient.getUnit());
                    dMinorIngredient.setName(minorIngredient.getName());
                }
                recipeDaoManager.insert(dMinorIngredient);
            }
        }
        //菜谱主料
        List<MajorIngredient> majorIngredientList = recipe.getMajorIngredients();
        if (null != majorIngredientList){
            for (int i = 0; i < majorIngredientList.size(); i++) {
                DMajorIngredient dMajorIngredient = new DMajorIngredient();
                dMajorIngredient.setId(recipe.getId());
                MajorIngredient majorIngredient = majorIngredientList.get(i);
                if (majorIngredient != null) {
                    dMajorIngredient.setUnit(majorIngredient.getUnit());
                    dMajorIngredient.setName(majorIngredient.getName());
                    dMajorIngredient.setType(majorIngredient.getType());
                }
                recipeDaoManager.insert(dMajorIngredient);
            }
        }
        //菜谱类别
        String categoryId = "";
        List<Classification> classificationList = recipe.getClassification();
        if (null != classificationList){
            for (int i = 0; i < classificationList.size(); i++) {
                Classification classification = classificationList.get(i);
                if (classification != null && classification.getSub() != null) {
                    categoryId += classification.getSub().getId() + " ";
                }
            }
        }
        dRecipe.setCategoryId(categoryId);
        //内页头图
        dRecipe.setInnerImages(recipe.getInnerImages());
        //封面小图
        dRecipe.setMinImages(recipe.getMinImages());
        //菜谱图片
        dRecipe.setImages(recipe.getImages());
        if (type == RecipeConstant.TYPE_HOME_RECIPE) {
            dRecipe.setIsHome(true);
        } else if (type == RecipeConstant.TYPE_ADULT_RECIPE) {
            dRecipe.setIsAdults(true);
        }
        long result = recipeDaoManager.insert(dRecipe);
        //数据变化,通知数据库观察者
        if (result != -1) {
            for (Map.Entry<DatabaseObserver, String> entry : observerMap.entrySet()) {
                if (RecipeConstant.RECIPE_TABLE.equals(entry.getValue())) {
                    entry.getKey().onChange();
                }
            }

        }
        return result;
    }

    /**
     * 根据ID,查询菜谱,使用RXJAVA
     *
     * @param id
     * @return
     */
    public Observable<DRecipe> queryRecipeRX(String id) {
        return recipeDaoManager.queryRecipeByRecipeIdRX(id).unique();
    }

    /**
     * 根据ID,查询菜谱,未使用RXJAVA
     */
    public Recipe queryRecipe(String id) {
        DRecipe dRecipe = recipeDaoManager.queryRecipeByRecipeId(id);
        if (dRecipe != null) {
            return convertToRecipe(dRecipe);
        }
        return null;
    }

    /**
     * 将DRecipe转换为Recipe
     *
     * @param dRecipe
     * @return
     */
    public Recipe convertToRecipe(DRecipe dRecipe) {
        Recipe recipe = new Recipe();
        recipe.setId(dRecipe.getId());
        recipe.setName(dRecipe.getName());
        recipe.setLongName(dRecipe.getLongName());
        recipe.setLocalId(dRecipe.getLocalId());
        recipe.setInstructions(dRecipe.getInstructions());
        recipe.setInstructionsText(dRecipe.getInstructionsText());
        recipe.setType(dRecipe.getType());
        recipe.setSource(dRecipe.getSource());
        recipe.setUserId(dRecipe.getUserId());
        recipe.setAddRemarks(dRecipe.getAddRemarks());
        recipe.setTips(dRecipe.getTips());
        recipe.setPageviews(dRecipe.getPageViews());
        recipe.setCollectCount(dRecipe.getCollectCount());
        recipe.setSourceId(dRecipe.getSourceId());
        recipe.setStatus(dRecipe.getStatus());
        recipe.setOtCreateTime(dRecipe.getOtCreateTime());
        recipe.setCreateId(dRecipe.getCreateId());
        recipe.setCreateTime(dRecipe.getCreateTime());
        recipe.setVersion(dRecipe.getVersion());
        recipe.setVideo(dRecipe.getVideoUrl());
        recipe.setModifyby(dRecipe.getModifyBy());
        recipe.setSerialNumber(dRecipe.getSerialNumber());
        recipe.setMoodifyTime(dRecipe.getModifyTime());
        Properties properties = new Properties();
        DProperties dProperties = dRecipe.getProperties();
        if (null != dProperties){
            properties.setTimeUnit(dProperties.getTimeUnit());
            properties.setDifficulty(dProperties.getDifficulty());
            properties.setCookingTime(dProperties.getCookingTime());
        }
        recipe.setProperties(properties);
        //设备信息
        ArrayList<Device> devices = new ArrayList<>();
        List<DDevice> dDeviceList = dRecipe.getDevices();
        if (null != dDeviceList){
            for (int i = 0; i < dDeviceList.size(); i++) {
                Device device = new Device();
                DDevice dDevice = dDeviceList.get(i);
                if (null != dDevice){
                    device.setId(dDevice.getId());
                    device.setName(dDevice.getName());
                    device.setTime(dDevice.getTime());
                    device.setTitle(dDevice.getTitle());
                    device.setDeviceId(dDevice.getDeviceId());
                    Autoexec autoexec = new Autoexec();
                    autoexec.setType(dDevice.getAutoType());
                    autoexec.setValue(dDevice.getAutoValue());
                    device.setAutoexec(autoexec);
                    List<DCookingPromptsInfo> dCookingPromptsInfos = dDevice.getCookingPromptsInfos();
                    ArrayList<CookingPromptsInfo> cookingPromptsInfos = new ArrayList<>();
                    if (dCookingPromptsInfos != null) {
                        for (DCookingPromptsInfo dCookingPromptsInfo : dCookingPromptsInfos) {
                            CookingPromptsInfo cookingPromptsInfo = new CookingPromptsInfo();
                            if (null != dCookingPromptsInfo){
                                cookingPromptsInfo.setIndex(dCookingPromptsInfo.getIndex());
                                cookingPromptsInfo.setType(dCookingPromptsInfo.getType());
                                cookingPromptsInfo.setTime(dCookingPromptsInfo.getTime());
                                cookingPromptsInfo.setDescribe(dCookingPromptsInfo.getDescribe());
                                cookingPromptsInfo.setBackgroupImages(dCookingPromptsInfo.getBackgroupImages());
                                cookingPromptsInfo.setButtonText(dCookingPromptsInfo.getButtonText());
                                cookingPromptsInfo.setButtonType(dCookingPromptsInfo.getButtonType());
                                List<DPromptContent> dPromptContents = dCookingPromptsInfo.getPromptContents();
                                ArrayList<PromptContent> promptContents = new ArrayList<>();
                                if (null != dPromptContents){
                                    for (DPromptContent dPromptContent : dPromptContents) {
                                        PromptContent promptContent = new PromptContent();
                                        if (null != dPromptContent){
                                            promptContent.setName(dPromptContent.getName());
                                            promptContent.setDescribe(dPromptContent.getDescribe());
                                            promptContent.setValue(dPromptContent.getValue());
                                            promptContent.setIndex(dPromptContent.getIndex());
                                            promptContent.setTime(dPromptContent.getTime());
                                            promptContent.setType(dPromptContent.getType());
                                        }
                                        promptContents.add(promptContent);
                                    }
                                }
                                cookingPromptsInfo.setPromptContent(promptContents);
                                cookingPromptsInfo.setPromptImages(dCookingPromptsInfo.getPromptImages());
                                cookingPromptsInfo.setPromptText(dCookingPromptsInfo.getPromptText());
                            }
                            cookingPromptsInfos.add(cookingPromptsInfo);
                        }
                    }
                    device.setCookingPromptsInfo(cookingPromptsInfos);
                    List<DProducts> dProductsList = dDevice.getProductses();
                    ArrayList<Products> productsList = new ArrayList<>();
                    if (dProductsList != null) {
                        for (DProducts dProducts : dProductsList) {
                            Products products = new Products();
                            if (null != dProducts){
                                products.setName(dProducts.getName());
                                products.setId(dProducts.getId());
                            }
                            productsList.add(products);
                        }
                    }
                    device.setProducts(productsList);
                }
                devices.add(device);
            }
        }
        recipe.setDevices(devices);
        //烹饪步准备骤
        ArrayList<ReadyStep> readySteps = new ArrayList<>();
        List<DReadyStep> dReadyStepList = dRecipe.getReadySteps();
        if (null != dReadyStepList){
            for (int i = 0; i < dReadyStepList.size(); i++) {
                ReadyStep readyStep = new ReadyStep();
                DReadyStep dReadyStep = dReadyStepList.get(i);
                if (null != dReadyStep){
                    readyStep.setIndex(dReadyStep.getIndex());
                    readyStep.setImages(dReadyStep.getImages());
                    readyStep.setTime(dReadyStep.getTime());
                    readyStep.setReminderText(dReadyStep.getReminderText());
                    readyStep.setReminder(dReadyStep.getReminder());
                    readyStep.setDescriptionText(dReadyStep.getDescriptionText());
                    readyStep.setDescription(dReadyStep.getDescription());
                }
                readySteps.add(readyStep);
            }
        }
        recipe.setReadySteps(readySteps);
        //烹饪步骤
        ArrayList<CookingStep> cookingSteps = new ArrayList<>();
        List<DCookingStep> dCookingStepList = dRecipe.getCookingSteps();
        if (null != dCookingStepList){
            for (int i = 0; i < dCookingStepList.size(); i++) {
                CookingStep cookingStep = new CookingStep();
                DCookingStep dCookingStep = dCookingStepList.get(i);
                if (null != dCookingStep){
                    cookingStep.setIndex(dCookingStep.getIndex());
                    cookingStep.setImages(dCookingStep.getImages());
                    cookingStep.setTime(dCookingStep.getTime());
                    cookingStep.setReminderText(dCookingStep.getReminderText());
                    cookingStep.setReminder(dCookingStep.getReminder());
                    cookingStep.setDescriptionText(dCookingStep.getDescriptionText());
                    cookingStep.setDescription(dCookingStep.getDescription());
                }
                cookingSteps.add(cookingStep);
            }
        }
        recipe.setCookingSteps(cookingSteps);
        //菜谱D配料
        List<DIngredient> dIngredients = new ArrayList<>();
        List<DDIngredient> ddIngredientList = dRecipe.getTypeDIngredients();
        if (null != ddIngredientList){
            for (int i = 0; i < ddIngredientList.size(); i++) {
                DDIngredient ddIngredient = ddIngredientList.get(i);
                DIngredient dIngredient = new DIngredient();
                if (null != ddIngredient){
                    dIngredient.setUnit(ddIngredient.getUnit());
                    dIngredient.setName(ddIngredient.getName());
                }
                dIngredients.add(dIngredient);
            }
        }
        recipe.setTypeDIngredients(dIngredients);

        //菜谱C配料
        List<CIngredient> cIngredients = new ArrayList<>();
        List<DCIngredient> dcIngredientList = dRecipe.getTypeCIngredients();
        if (null != dcIngredientList){
            for (int i = 0; i < dcIngredientList.size(); i++) {
                DCIngredient dcIngredient = dcIngredientList.get(i);
                CIngredient cIngredient = new CIngredient();
                if (null != dcIngredient){
                    cIngredient.setUnit(dcIngredient.getUnit());
                    cIngredient.setName(dcIngredient.getName());
                }
                cIngredients.add(cIngredient);
            }
        }
        recipe.setTypeCIngredients(cIngredients);

        //菜谱B配料
        List<BIngredient> bIngredients = new ArrayList<>();
        List<DBIngredient> dbIngredientList = dRecipe.getTypeBIngredients();
        if (null != dbIngredientList){
            for (int i = 0; i < dbIngredientList.size(); i++) {
                DBIngredient dbIngredient = dbIngredientList.get(i);
                BIngredient bIngredient = new BIngredient();
                if (null != dbIngredient){
                    bIngredient.setUnit(dbIngredient.getUnit());
                    bIngredient.setName(dbIngredient.getName());
                }
                bIngredients.add(bIngredient);
            }
        }
        recipe.setTypeBIngredients(bIngredients);
        //菜谱A配料
        List<AIngredient> aIngredients = new ArrayList<>();
        List<DAIngredient> daIngredientList = dRecipe.getTypeAIngredients();
        if (null != daIngredientList){
            for (int i = 0; i < daIngredientList.size(); i++) {
                DAIngredient daIngredient = daIngredientList.get(i);
                AIngredient aIngredient = new AIngredient();
                if (null != daIngredient){
                    aIngredient.setUnit(daIngredient.getUnit());
                    aIngredient.setName(daIngredient.getName());
                }
                aIngredients.add(aIngredient);
            }
        }
        recipe.setTypeAIngredients(aIngredients);
        //菜谱辅料
        List<MinorIngredient> minorIngredients = new ArrayList<>();
        List<DMinorIngredient> dMinorIngredientList = dRecipe.getMinorIngredients();
        if (null != dMinorIngredientList){
            for (int i = 0; i < dMinorIngredientList.size(); i++) {
                DMinorIngredient dMinorIngredient = dMinorIngredientList.get(i);
                MinorIngredient minorIngredient = new MinorIngredient();
                if (null != dMinorIngredient){
                    minorIngredient.setUnit(dMinorIngredient.getUnit());
                    minorIngredient.setName(dMinorIngredient.getName());
                }
                minorIngredients.add(minorIngredient);
            }
        }
        recipe.setMinorIngredients(minorIngredients);
        //菜谱主料
        List<MajorIngredient> majorIngredients = new ArrayList<>();
        List<DMajorIngredient> dMajorIngredientList = dRecipe.getMajorIngredients();
        if (null != dMajorIngredientList){
            for (int i = 0; i < dMajorIngredientList.size(); i++) {
                DMajorIngredient dMajorIngredient = dMajorIngredientList.get(i);
                MajorIngredient majorIngredient = new MajorIngredient();
                if (null != dMajorIngredient){
                    majorIngredient.setUnit(dMajorIngredient.getUnit());
                    majorIngredient.setName(dMajorIngredient.getName());
                    majorIngredient.setType(dMajorIngredient.getType());
                }
                majorIngredients.add(majorIngredient);
            }
        }
        recipe.setMajorIngredients(majorIngredients);
        // 内页头图
        recipe.setInnerImages(dRecipe.getInnerImages());
        //封面小图
        recipe.setMinImages(dRecipe.getMinImages());
        //菜谱图片
        recipe.setImages(dRecipe.getImages());
        return recipe;
    }

    /**
     * 查询全部菜谱
     *
     * @return
     */
    public Observable<List<DRecipe>> queryAll() {
        return recipeDaoManager.queryAll().list();
    }

    /**
     * 根据ID删除菜谱
     *
     * @param id
     */
    public void deleteByRecipeId(String id) {
        recipeDaoManager.deleteByRecipeId(id);
        //数据变化,通知数据库观察者
        for (Map.Entry<DatabaseObserver, String> entry : observerMap.entrySet()) {
            if (RecipeConstant.RECIPE_TABLE.equals(entry.getValue())) {
                entry.getKey().onChange();
            }
        }
    }

    /**
     * 查询轮播菜谱广告位数据或达人秀数据
     *
     * @return
     */
    public Observable<List<DRecipeBanner>> queryRecipeBanner(boolean isAdults) {
        return recipeDaoManager.queryRecipeBanner(isAdults).list();
    }

    /**
     * 查询达人秀菜谱
     *
     * @return
     */
    public DRecipeBanner queryRecipeBanner(String recipeId) {
        return recipeDaoManager.queryRecipeBanner(recipeId);
    }

    /**
     * DRecipeBanner转换为RecipeBanner
     *
     * @return
     */
    public RecipeBanner convertRecipeBanner(DRecipeBanner dRecipeBanner) {
        RecipeBanner recipeBanner = new RecipeBanner();
        if (null != dRecipeBanner){
            recipeBanner.setId(dRecipeBanner.getId());
            recipeBanner.setName(dRecipeBanner.getName());
            recipeBanner.setModule(dRecipeBanner.getModule());
            recipeBanner.setRecipeId(dRecipeBanner.getRecipeId());
            recipeBanner.setApi(dRecipeBanner.getApi());
            recipeBanner.setCreator(dRecipeBanner.getCreator());
            recipeBanner.setVersion(dRecipeBanner.getVersion());
            recipeBanner.setCreateId(dRecipeBanner.getCreateId());
            recipeBanner.setCreateTime(dRecipeBanner.getCreateTime());
            recipeBanner.setImages(dRecipeBanner.getImages());
        }
        return recipeBanner;
    }

    /**
     * 插入轮播菜谱广告位数据
     *
     * @param list
     * @param isAdults 是达人秀（true）菜谱轮播（false）
     */
    public void insertRecipeBanner(List<RecipeBanner> list, boolean isAdults) {
        List<DRecipeBanner> bannerList = new ArrayList<>();
        for (RecipeBanner banner : list) {
            DRecipeBanner recipeBanner = new DRecipeBanner();
            recipeBanner.setId(banner.getId());
            recipeBanner.setName(banner.getName());
            recipeBanner.setModule(banner.getModule());
            recipeBanner.setRecipeId(banner.getRecipeId());
            recipeBanner.setApi(banner.getApi());
            recipeBanner.setCreator(banner.getCreator());
            recipeBanner.setVersion(banner.getVersion());
            recipeBanner.setCreateId(banner.getCreateId());
            recipeBanner.setCreateTime(banner.getCreateTime());
            recipeBanner.setImages(banner.getImages());
            recipeBanner.setIsAdults(isAdults);
            bannerList.add(recipeBanner);
        }
        recipeDaoManager.insertRecipeBanner(bannerList);
        if (isAdults){
            //数据变化,通知数据库观察者
            for (Map.Entry<DatabaseObserver, String> entry : observerMap.entrySet()) {
                if (RecipeConstant.ADULT_TABLE.equals(entry.getValue())) {
                    entry.getKey().onChange();
                }
            }
        }
    }

    /**
     * 插入轮播菜谱广告位数据
     *
     * @param banner
     */
    public void insertRecipeBanner(RecipeBanner banner) {
        List<DRecipeBanner> bannerList = new ArrayList<>();
        if (banner != null) {
            DRecipeBanner recipeBanner = new DRecipeBanner();
            recipeBanner.setId(banner.getId());
            recipeBanner.setName(banner.getName());
            recipeBanner.setModule(banner.getModule());
            recipeBanner.setRecipeId(banner.getRecipeId());
            recipeBanner.setApi(banner.getApi());
            recipeBanner.setCreator(banner.getCreator());
            recipeBanner.setVersion(banner.getVersion());
            recipeBanner.setCreateId(banner.getCreateId());
            recipeBanner.setCreateTime(banner.getCreateTime());
            recipeBanner.setImages(banner.getImages());
            bannerList.add(recipeBanner);
            recipeDaoManager.insertRecipeBanner(bannerList);
        }

    }

    /**
     * 删除轮播菜谱广告位数据或达人秀数据
     */
    public void deleteRecipeBanner(boolean isAdults) {
        recipeDaoManager.deleteRecipeBanner(isAdults);
    }

    /**
     * 删除轮播菜谱
     */
    public void deleteHomeRecipe() {
        List<DRecipe> recipeList = recipeDaoManager.queryHomeRecipe();
        if (recipeList != null){
            for (DRecipe recipe : recipeList){
                recipeDaoManager.deleteByRecipeId(recipe.getId());
            }
        }
    }

    /**
     * 删除达人秀菜谱
     */
    public void deleteAdultRecipe() {
        List<DRecipe> recipeList = recipeDaoManager.queryAdultRecipe();
        if (recipeList != null){
            for (DRecipe recipe : recipeList){
                recipeDaoManager.deleteByRecipeId(recipe.getId());
            }
        }
    }

    /**
     * DRecipeBanner转换为RecipeBanner
     *
     * @param list
     * @return
     */
    public List<RecipeBanner> convertToRecipeBanner(List<DRecipeBanner> list) {
        if (list == null) return null;
        List<RecipeBanner> bannerList = new ArrayList<>();
        for (DRecipeBanner banner : list) {
            RecipeBanner recipeBanner = new RecipeBanner();
            recipeBanner.setId(banner.getId());
            recipeBanner.setName(banner.getName());
            recipeBanner.setModule(banner.getModule());
            recipeBanner.setRecipeId(banner.getRecipeId());
            recipeBanner.setApi(banner.getApi());
            recipeBanner.setCreator(banner.getCreator());
            recipeBanner.setVersion(banner.getVersion());
            recipeBanner.setCreateId(banner.getCreateId());
            recipeBanner.setCreateTime(banner.getCreateTime());
            recipeBanner.setImages(banner.getImages());
            bannerList.add(recipeBanner);
        }
        return bannerList;
    }

    /**
     * 更新菜谱类型的状态
     *
     * @param id
     * @param type
     */
    public void updateRecipeTypeStatus(String id, int type) {
        DRecipe dRecipe = recipeDaoManager.queryRecipeByRecipeId(id);
        if (dRecipe != null) {
            if (type == RecipeConstant.TYPE_ADULT_RECIPE){
                dRecipe.setIsAdults(true);

            }else if (type == RecipeConstant.TYPE_HOME_RECIPE){
                dRecipe.setIsHome(true);
            }
            recipeDaoManager.updateRecipe(dRecipe);
        }
    }

    /**
     * 判断菜谱是否已经存在数据库中
     * @param id
     * @return
     */
    public boolean isDBRecipe(String id) {
        DRecipe dRecipe = recipeDaoManager.queryRecipeByRecipeId(id);

        boolean isDataBaseRecipe = dRecipe == null ? false : true;

        return isDataBaseRecipe;
    }

    /**
     * 判断recipeBanner是否已经存在数据库中
     * @param id
     * @return
     */
    public boolean isDbRecipeBanner(String id) {
        DRecipeBanner dRecipeBanner = recipeDaoManager.queryRecipeBanner(id);
        boolean isDataBaseRecipe = dRecipeBanner == null ? false : true;
        return isDataBaseRecipe;
    }

    /**
     * 查询上传菜谱
     *
     * @return
     */
    public Observable<List<DRecipe>> queryUploadRecipe() {
        return recipeDaoManager.queryUploadRecipeRx().list();
    }

    /**
     * 删除上传菜谱
     */
    public void deleteUploadRecipe() {
        List<DRecipe> recipeList = recipeDaoManager.queryUploadRecipe();
        if (recipeList != null){
            for (DRecipe recipe : recipeList){
                recipeDaoManager.deleteByRecipeId(recipe.getId());
            }
        }
    }
}
