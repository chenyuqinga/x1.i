package com.fotile.bind.mvp.modle;

import android.content.Context;

import com.fotile.bind.mvp.ServerUrl;
import com.fotile.bind.util.BindConstant;
import com.fotile.common.bean.EnginBean;
import com.fotile.common.bean.EnginType;
import com.fotile.common.util.EnginUtil;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 文件名称：RetrofitHelper
 * 创建时间：2017/8/7 14:33
 * 文件作者：zhaoqingjing
 * 功能描述：初始化Retrofit
 */
public class RetrofitHelper {

    private OkHttpClient client = new OkHttpClient();
    private GsonConverterFactory factory = GsonConverterFactory.create(new GsonBuilder().create());
    private static RetrofitHelper instance = null;

    /**
     * 请求类型(正式环境为0,测试环境为1,开发环境为2,默认为0)
     */
    private int requestType = BindConstant.TYPE_REQUEST_ONLINE;
    /**
     * 根据设备获取设备登录的token
     * 二维码扫描
     */
    private Retrofit mRetrofitDeviceToken = null;
//    /**
//     * 获取菜谱token
//     */
//    private Retrofit mRetrofitRecipeToken = null;
//    /**
//     * 菜谱数据请求
//     */
//    private Retrofit mRetrofitRecipeData = null;
//    /**
//     * 天气数据请求
//     */
//    private Retrofit weatherRetrofit = null;

    private Context context;

    public static RetrofitHelper getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitHelper(context);
        }
        return instance;
    }


    private RetrofitHelper(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        initTypeValue();
        resetDeviceToken();
    }

    /**
     * 初始化请求类型
     */
    private void initTypeValue() {
        EnginBean enginBean = EnginUtil.getEnginBean();
        if (null != enginBean){
            EnginType recipe_url = enginBean.recipe_url;
            //正式环境
            if (recipe_url == EnginType.ENGIN_URL_ONLINE){
                requestType = BindConstant.TYPE_REQUEST_ONLINE;
            }
            //测试环境
            else if (recipe_url == EnginType.ENGIN_URL_TEST){
                requestType = BindConstant.TYPE_REQUEST_TEST;
            }
            //开发环境
            else if (recipe_url == EnginType.ENGIN_URL_DEVELOP){
                requestType = BindConstant.TYPE_REQUEST_DEVELOP;
            }
            //默认正式环境
            else {
                requestType = BindConstant.TYPE_REQUEST_ONLINE;
            }
        }
    }

    /**
     * 初始化Retrofit相关
     */
    private void resetDeviceToken() {
        String url = getDeviceTokenUrl();
        mRetrofitDeviceToken = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(factory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
    }

//
//    /**
//     * 初始化天气Retrofit相关
//     */
//    private void resetWeather() {
//        weatherRetrofit = new Retrofit.Builder()
//                .baseUrl(ServerUrl.BASE_WEATHER_URL)
//                .client(client)
//                .addConverterFactory(factory)
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build();
//    }

    /********************************************get url↓********************************************/

    /**
     * 获取设备登录token url
     *
     * @return
     */
    public String getDeviceTokenUrl() {
        String url = BindConstant.TYPE_REQUEST_ONLINE == requestType ? ServerUrl.BASE_TOKEN_URL : ServerUrl
                .BASE_TOKEN_URL_TEST;
        return url;
    }

    /********************************************get url↑********************************************/

    /**
     * 获取设备token的URL
     */
    public RetrofitService getDeviceTokenService() {
        return mRetrofitDeviceToken.create(RetrofitService.class);
    }

//    /**
//     * 获取天气的URL
//     */
//    public RetrofitService getWeatherServer() {
//        return weatherRetrofit.create(RetrofitService.class);
//    }

    public int getRequestType() {
        return requestType;
    }

}
