package com.fotile.bind.mvp.modle;



import com.fotile.bind.bean.DeviceSubQR;
import com.fotile.bind.bean.DeviceloginToken;
import com.fotile.bind.mvp.ServerUrl;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 文件名称：RetrofitService
 * 创建时间：2017/8/7 14:31
 * 文件作者：zhaoqingjing
 * 功能描述：定义了接口访问，使用RxJava
 */
public interface RetrofitService {

    /**
     * 根据设备获取设备的token-正式环境
     */
    @POST("device_login")
    Observable<DeviceloginToken> getLoginTokenOnLine(@Body RequestBody body);

    /**
     * 请求登录的设备token-测试环境
     */
    @POST("device_login")
    Observable<DeviceloginToken> getLoginTokenTest(@Body RequestBody body);

//    /**
//     * 请求菜谱token-正式环境
//     */
//    @POST("v2/apply_token")
//    Observable<UploadRecipeToken> getUploadRecipeTokenOnLine(@Body RequestBody body, @Header("Access-Token") String
//            header, @Header("X-Device") String deviceId);
//
//    /**
//     * 请求菜谱token-测试环境
//     */
//    @POST("v2/apply_token")
//    Observable<UploadRecipeToken> getUploadRecipeTokenTest(@Body RequestBody body, @Header("Access-Token") String
//            header, @Header("X-Device") String deviceId);
//
//    /**
//     * 请求菜谱分类接口-正式环境
//     */
//    @POST(ServerUrl.RECIPE_CATEGORY_LIST_URL)
//    Observable<Response<RecipeCategory>> getRecipeCategoryListOnLine(@Body RequestBody body);
//
//    /**
//     * 请求菜谱分类接口-测试环境
//     */
//    @POST(ServerUrl.RECIPE_CATEGORY_LIST_URL_TEST)
//    Observable<Response<RecipeCategory>> getRecipeCategoryListTest(@Body RequestBody body);
//
//    /**
//     * 请求本地菜谱接口-正式环境
//     */
//    @POST(ServerUrl.RECIPES_LIST_URL)
//    Observable<Response<Recipe>> getRecipeListOnLine(@Body RequestBody body);
//
//    /**
//     * 请求本地菜谱接口-测试环境
//     */
//    @POST(ServerUrl.RECIPES_LIST_URL_TEST)
//    Observable<Response<Recipe>> getRecipeListTest(@Body RequestBody body);
//
//    /**
//     * 请求轮播运营位ID-正式环境
//     */
//    @POST(ServerUrl.BANNER_ID_URL)
//    Observable<Response<RecipeBannerId>> getRecipeBannerIdOnLine(@Body RequestBody body);
//
//    /**
//     * 请求轮播运营位ID-测试环境
//     */
//    @POST(ServerUrl.BANNER_ID_URL_TEST)
//    Observable<Response<RecipeBannerId>> getRecipeBannerIdTest(@Body RequestBody body);
//
//    /**
//     * 请求轮播数据-正式环境
//     */
//    @POST(ServerUrl.BANNER_URL)
//    Observable<Response<RecipeBanner>> getRecipeBannerOnLine(@Body RequestBody body);
//
//    /**
//     * 请求轮播数据-测试环境
//     */
//    @POST(ServerUrl.BANNER_URL_TEST)
//    Observable<Response<RecipeBanner>> getRecipeBannerTest(@Body RequestBody body);
//
//    /**
//     * 请求轮播菜谱接口-正式环境
//     */
//    @POST(ServerUrl.BANNER_RECIPES_LIST_URL)
//    Observable<Response<Recipe>> getRecipeDetailOnLine(@Path("module") String module, @Path("api") String api, @Body
//            RequestBody body);
//
//    /**
//     * 请求轮播菜谱接口-测试环境
//     */
//    @POST(ServerUrl.BANNER_RECIPES_LIST_URL_TEST)
//    Observable<Response<Recipe>> getRecipeDetailTest(@Path("module") String module, @Path("api") String api, @Body
//            RequestBody body);
//
//    /**
//     * 请求上传菜谱接口-正式环境
//     */
//    @POST(ServerUrl.RECIPES_LIST_URL)
//    Observable<Response<Recipe>> getUploadRecipeListOnline(@Body RequestBody body, @Header("Access-Token") String
//            header, @Header("X-Device") String deviceId);
//
//    /**
//     * 请求上传菜谱接口-测试环境
//     */
//    @POST(ServerUrl.RECIPES_LIST_URL_TEST)
//    Observable<Response<Recipe>> getUploadRecipeListTest(@Body RequestBody body, @Header("Access-Token") String
//            header, @Header("X-Device") String deviceId);
//
//    /**
//     * 删除上传菜谱-正式环境
//     *
//     * @param id
//     * @return
//     */
//    @DELETE(ServerUrl.RECIPE_DELETE_URL)
//    Observable<Response> deleteUploadRecipeOnline(@Path("id") String id, @Header("Access-Token") String token,
//                                                  @Header("X-Device") String deviceId);
//
//    /**
//     * 删除上传菜谱-测试环境
//     *
//     * @param id
//     * @return
//     */
//    @DELETE(ServerUrl.RECIPE_DELETE_URL_TEST)
//    Observable<Response> deleteUploadRecipeTest(@Path("id") String id, @Header("Access-Token") String token, @Header("X-Device") String deviceId);
//
    /**
     * 请求扫码绑定的二维码-正式环境
     *
     * @param deviceId
     * @param header
     * @return
     */
    @GET(ServerUrl.SUB_QR)
    Observable<DeviceSubQR> getDeviceSubQROnline(@Path("deviceId") String deviceId, @Header("Access-Token") String header);

    /**
     * 请求扫码绑定的二维码-测试环境
     *
     * @param deviceId
     * @param header
     * @return
     */
    @GET(ServerUrl.SUB_QR)
    Observable<DeviceSubQR> getDeviceSubQRTest(@Path("deviceId") String deviceId, @Header("Access-Token") String header);
//
//    /**
//     * 根据ip地址请求城市信息
//     *
//     * @param header
//     * @return
//     */
//    @POST(ServerUrl.CITY_URL)
//    Observable<City> getCityByIp(@Header("ip") String header);
//
//    /**
//     * 请求天气信息
//     *
//     * @param body
//     * @return
//     */
//    @POST(ServerUrl.WEATHER_URL)
//    Observable<WeatherResponse<Weather>> getWeatherInfo(@Body RequestBody body);
//
//    /**
//     * 根据id请求菜谱（灶具录制菜谱）详情-正式环境
//     */
//    @POST(ServerUrl.RECIPES_LIST_URL)
//    Observable<Response<Recipe>> getRecipeByIdOnline(@Body RequestBody body, @Header("Access-Token") String header,
//                                                     @Header("X-Device") String deviceId);
//
//    /**
//     * 根据id请求菜谱（灶具录制菜谱）详情-测试环境
//     */
//    @POST(ServerUrl.RECIPES_LIST_URL_TEST)
//    Observable<Response<Recipe>> getRecipeByIdTest(@Body RequestBody body, @Header("Access-Token") String header, @Header("X-Device") String deviceId);

}
