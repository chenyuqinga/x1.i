package com.fotile.recipe.net.modle;

import android.content.ComponentName;
import android.content.Context;

import com.fotile.common.util.log.LogUtil;
import com.fotile.recipe.bean.device.DeviceSubQR;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.bean.recipe.RecipeBanner;
import com.fotile.recipe.bean.recipe.RecipeBannerId;
import com.fotile.recipe.bean.recipe.RecipeCategory;
import com.fotile.recipe.bean.reponse.Response;
import com.fotile.recipe.bean.screensaver.City;
import com.fotile.recipe.bean.screensaver.Weather;
import com.fotile.recipe.bean.screensaver.WeatherResponse;
import com.fotile.recipe.bean.token.DeviceloginToken;
import com.fotile.recipe.bean.token.UploadRecipeToken;
import com.fotile.recipe.net.ServerUrl;
import com.fotile.recipe.request.GetBannerRequest;
import com.fotile.recipe.request.GetDeviceParentIdRequest;
import com.fotile.recipe.request.GetRecipeBannerOperateIdRequest;
import com.fotile.recipe.request.GetRecipeByIdRequest;
import com.fotile.recipe.request.GetRecipeCategoryListRequest;
import com.fotile.recipe.request.GetRecipeListRequest;
import com.fotile.recipe.request.GetRecipeRequest;
import com.fotile.recipe.request.GetSearchRecipeListRequest;
import com.fotile.recipe.request.GetSourceRecipeListRequest;
import com.fotile.recipe.request.GetTokenRequest;
import com.fotile.recipe.request.GetUploadTokenRequest;
import com.fotile.recipe.request.GetWeatherRequest;
import com.fotile.recipe.uitl.RecipeConstant;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;

import static com.fotile.recipe.net.ServerUrl.RECIPES_LIST_URL;
import static com.fotile.recipe.net.ServerUrl.RECIPES_LIST_URL_TEST;
import static com.fotile.recipe.net.ServerUrl.RECIPE_CATEGORY_LIST_URL;
import static com.fotile.recipe.net.ServerUrl.RECIPE_CATEGORY_LIST_URL_TEST;

/**
 * 项目名称：X1.I
 * 创建时间：2018/12/7 15:07
 * 文件作者：yaohx
 * 功能描述：管理网络请求入口
 */
public class DataManager {
    Context context;

    //根据设备获取设备的token
    private RetrofitService deviceTokenService;
    //获取菜谱需要的token
    private RetrofitService recipeTokenService;
    //获取菜谱数据
    private RetrofitService recipeDataService;
    //获取天气数据
    private RetrofitService weatherService;


    public final static String TAG = "设备二维码";

    public DataManager(Context context) {
        deviceTokenService = RetrofitHelper.getInstance(context).getDeviceTokenService();
        recipeTokenService = RetrofitHelper.getInstance(context).getRecipeTokenService();
        recipeDataService = RetrofitHelper.getInstance(context).getRecipeDataService();
        this.weatherService = RetrofitHelper.getInstance(context).getWeatherServer();
    }

    /**
     * 根据设备获取设备的token
     */
    public Observable<DeviceloginToken> getDeviceLoginToken(String id, String mac, String authorizeCode) {
        GetTokenRequest request = new GetTokenRequest();
        String json = request.createRequest(id, mac, authorizeCode);
        RequestBody body = createRequestBody(json);

        String url = RetrofitHelper.getInstance(context).getDeviceTokenUrl();
        LogUtil.LOG_REQUEST("获取设备token的url", url + "device_login");
        LogUtil.LOG_REQUEST("获取设备token的body", json);
        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        //正式环境或者测试环境
        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? deviceTokenService.getLoginTokenOnLine(body) :
                deviceTokenService.getLoginTokenTest(body);
    }

    /**
     * 根据设备获取我的上传的token
     *
     * @param id
     * @param deviceToken
     * @param deviceId
     * @return
     */
    public Observable<UploadRecipeToken> getUploadRecipeToken(String id, String deviceToken, String deviceId) {
        GetUploadTokenRequest request = new GetUploadTokenRequest();
        String json = request.createRequest(id);
        RequestBody body = createRequestBody(json);

        String url = RetrofitHelper.getInstance(context).getRecipeToeknUrl();
        LogUtil.LOG_REQUEST("获取菜谱token的url", url + "v2/apply_token");
        LogUtil.LOG_REQUEST("获取菜谱token的body", json);
        LogUtil.LOG_REQUEST("获取菜谱token的Access-Token", deviceToken);
        LogUtil.LOG_REQUEST("获取菜谱token的X-Device", deviceId);
        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        //正式环境或者测试环境
        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? recipeTokenService.getUploadRecipeTokenOnLine(body,
                deviceToken, deviceId) : recipeTokenService.getUploadRecipeTokenTest(body, deviceToken, deviceId);
    }

    /**
     * 获取设备parentId
     *
     * @param name 设备名称
     * @return
     */
    public Observable<Response<RecipeCategory>> getDeviceParentId(String name) {
        GetDeviceParentIdRequest request = new GetDeviceParentIdRequest();
        String json = request.createRequest(name);

        String url = RetrofitHelper.getInstance(context).getRecipeToeknUrl();
        RequestBody body = createRequestBody(json);

        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        LogUtil.LOG_RECIPE("获取ParentId url",url + (requestType == RecipeConstant.TYPE_REQUEST_ONLINE
                ?RECIPE_CATEGORY_LIST_URL : RECIPE_CATEGORY_LIST_URL_TEST));
        LogUtil.LOG_RECIPE("获取ParentId body",json);

        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? recipeDataService.getRecipeCategoryListOnLine(body) :
                recipeDataService.getRecipeCategoryListTest(body);
    }

    /**
     * 根据parentId获取菜谱分类
     */
    public Observable<Response<RecipeCategory>> getRecipeCategoryList(String parentId) {
        GetRecipeCategoryListRequest request = new GetRecipeCategoryListRequest();
        String json = request.createRequest(parentId);
        RequestBody body = createRequestBody(json);

        LogUtil.LOG_REQUEST("获取菜谱分类的body", json);
        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? recipeDataService.getRecipeCategoryListOnLine(body) :
                recipeDataService.getRecipeCategoryListTest(body);
    }

    /**
     * 根据标签获取菜谱
     *
     * @param classificationSubId 分类标签id
     * @param tagName             分类标签名称
     * @param preset              菜谱预置（0否，1是）
     * @return
     */
    public Observable<Response<Recipe>> getRecipeListByLabel(String classificationSubId, String tagName, int preset) {
        GetRecipeListRequest request = new GetRecipeListRequest();
        String json = request.createRequest(classificationSubId, preset);
        RequestBody body = createRequestBody(json);

        int requestType = RetrofitHelper.getInstance(context).getRequestType();

        String url = RetrofitHelper.getInstance(context).getRecipeDataUrl();
//        LogUtil.LOG_RECIPE("---------------------------------------------------","start");
//        String moudle = RecipeConstant.TYPE_REQUEST_ONLINE == requestType ?  RECIPES_LIST_URL:RECIPES_LIST_URL_TEST;
//        LogUtil.LOG_RECIPE("获取标签下菜谱-tagName",tagName);
//        LogUtil.LOG_RECIPE("获取标签下菜谱-body",json);
//        LogUtil.LOG_RECIPE("获取标签下菜谱-url",url + moudle);
//        LogUtil.LOG_RECIPE("---------------------------------------------------","end");

        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? recipeDataService.getRecipeListOnLine(body) :
                recipeDataService.getRecipeListTest(body);
    }

    /**
     * 根据关键字搜索菜谱
     *
     * @param keyString
     * @param page
     * @param devicesProductsId
     * @return
     */
    public Observable<Response<Recipe>> getRecipeListBySearchKey(String keyString, int page, String devicesProductsId) {
        GetSearchRecipeListRequest request = new GetSearchRecipeListRequest();
        String json = request.createRequest(keyString, page, devicesProductsId);
        RequestBody body = createRequestBody(json);

        LogUtil.LOG_REQUEST("搜索菜谱数据body", json);
        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? recipeDataService.getRecipeListOnLine(body) :
                recipeDataService.getRecipeListTest(body);
    }

    /**
     * 根据设备获取菜谱运营位ID
     *
     * @param name
     * @return
     */
    public Observable<Response<RecipeBannerId>> getRecipeBannerOperateId(String name) {
        GetRecipeBannerOperateIdRequest request = new GetRecipeBannerOperateIdRequest();
        String json = request.createRequest(name);
        RequestBody body = createRequestBody(json);

        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? recipeDataService.getRecipeBannerIdOnLine(body) :
                recipeDataService.getRecipeBannerIdTest(body);
    }

    /**
     * 根据设备获取菜谱轮播
     *
     * @param list
     * @return
     */
    public Observable<Response<RecipeBanner>> getRecipeBanner(List<String> list) {
        GetBannerRequest request = new GetBannerRequest();
        String json = request.createRequest(list);
        RequestBody body = createRequestBody(json);
        LogUtil.LOG_REQUEST("获取轮播菜谱（达人秀）的请求body", json);

        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? recipeDataService.getRecipeBannerOnLine(body) :
                recipeDataService.getRecipeBannerTest(body);
    }

    /**
     * 根据轮播数据中菜谱ID获取轮播菜谱
     */
    public Observable<Response<Recipe>> getRecipeBannerDetail(String module, String api, String recipeId) {
        GetRecipeRequest request = new GetRecipeRequest();
        String json = request.createRequest(recipeId);
        RequestBody body = createRequestBody(json);
        LogUtil.LOG_REQUEST("获取轮播菜谱（达人秀）详情的请求body", json);

        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? recipeDataService.getRecipeDetailOnLine(module, api, body)
                : recipeDataService.getRecipeDetailTest(module, api, body);
    }

    /**
     * 获取自制菜谱
     *
     * @param page
     * @param uploadRecipeToken
     * @param deviceId          灶具的 deviceId
     * @return
     */
    public Observable<Response<Recipe>> getUploadRecipe(int page, String uploadRecipeToken, String deviceId) {
        GetSourceRecipeListRequest request = new GetSourceRecipeListRequest();
        String json = request.createRequest(deviceId, page);
        RequestBody body = createRequestBody(json);

        LogUtil.LOG_REQUEST("自制菜谱请求json", json);
        LogUtil.LOG_REQUEST("自制菜谱请求token", uploadRecipeToken);

        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? recipeDataService.getUploadRecipeListOnline(body, uploadRecipeToken, deviceId)
                : recipeDataService.getUploadRecipeListTest(body, uploadRecipeToken, deviceId);
    }

    /**
     * 删除自制菜谱
     *
     * @param id
     * @param uploadRecipeToken
     * @param deviceId
     * @return 灶具的 deviceId
     */
    public Observable<Response> deleteUploadRecipe(String id, String uploadRecipeToken, String deviceId) {
        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? recipeDataService.deleteUploadRecipeOnline(id, uploadRecipeToken, deviceId)
                : recipeDataService.deleteUploadRecipeTest(id, uploadRecipeToken, deviceId);
    }

    /**
     * 根据菜谱ID网络获取菜谱（灶具录制菜谱）
     *
     * @param recipeId
     * @return
     */
    public Observable<Response<Recipe>> getRecipeById(String recipeId, String token, String deviceId) {
        GetRecipeByIdRequest request = new GetRecipeByIdRequest();
        String json = request.createRequest(recipeId);
        RequestBody body = createRequestBody(json);

        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? recipeDataService.getRecipeByIdOnline(body, token,deviceId)
                : recipeDataService.getRecipeByIdTest(body, token, deviceId);
    }

    /**
     * 获取扫码订阅二维码
     *
     * @param deviceId
     * @param token
     * @return
     */
    public Observable<DeviceSubQR> getDeviceSubQR(String deviceId, String token) {
        String url = RetrofitHelper.getInstance(context).getDeviceTokenUrl();
        LogUtil.LOGE(TAG, "获取二维码接口地址:" + url + ServerUrl.SUB_QR);
        LogUtil.LOGE(TAG, "获取二维码接口deviceId:" + deviceId);
        LogUtil.LOGE(TAG, "获取二维码接口token:" + token);

        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        return RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? deviceTokenService.getDeviceSubQROnline(deviceId, token)
                : deviceTokenService.getDeviceSubQRTest(deviceId, token);
    }

    /**
     * 构建json实体
     */
    private RequestBody createRequestBody(String json) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        return body;
    }

    /**
     * 根据ip地址获取当前城市
     *
     * @param ip
     * @return
     */
    public Observable<City> getCity(String ip) {
        return weatherService.getCityByIp(ip);
    }

    /**
     * 根据城市获取其天气信息
     *
     * @param city
     * @return
     */
    public Observable<WeatherResponse<Weather>> getWeatherInfo(String city) {
        GetWeatherRequest request = new GetWeatherRequest();
        String json = request.createRequest(city);
        RequestBody body = createRequestBody(json);
        return weatherService.getWeatherInfo(body);
    }
}
