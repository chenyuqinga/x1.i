package com.fotile.bind.mvp;


/**
 * 文件名称：DataManager
 * 创建时间：2017/8/7
 * 文件作者：zhaoqingjing
 * 功能描述:服务器URL地址
 */
public class ServerUrl {
    /**********************************上传菜谱token的APP_ID↓************************************/
    //请求上传菜谱token的APP_ID（测试环境）
    public static final String MYUP_TOKEN_APP_ID_TEST = "2e07d4b4250a9e00";
    //请求上传菜谱token的APP_ID（正式环境）
    public static final String MYUP_TOKEN_APP_ID = "2e07d4b036476800";
    /**********************************上传菜谱token的APP_ID↑************************************/

    /**
     * 请求轮播菜谱和达人秀菜谱token的APP_ID
     */
    private static final String BANNER_TOKEN_APP_ID = "2e07d6b4250ac600";
    /**
     * 请求轮播菜谱和达人秀菜谱相关数据的APP_ID（测试环境）
     */
    public static final String BANNER_APP_ID_TEST = "2e07d6b4250b4800";

    /**
     * 请求轮播菜谱和达人秀菜谱相关数据的APP_ID（正式环境）
     */
    public static final String BANNER_APP_ID = "2e07d4b03647a200";
    /**
     * 扫码订阅接口
     */
    public static final String SUB_QR = "device/{deviceId}/subQR";


    /**
     * 测试环境请求url
     */
    public static final String MATERIAL_LIST_URL_TEST = "/module/recipes/" + MYUP_TOKEN_APP_ID_TEST + "/api/material/list";
    public static final String RECIPE_CATEGORY_LIST_URL_TEST = "/module/recipes/" + MYUP_TOKEN_APP_ID_TEST + "/api/recipeProps/list";
    public static final String RECIPES_LIST_URL_TEST = "/module/recipes/" + MYUP_TOKEN_APP_ID_TEST + "/api/recipes/list";
    public static final String BANNER_ID_URL_TEST = "/module/operatePosition/" + BANNER_APP_ID_TEST + "/api/operatePosition/list";
    public static final String BANNER_URL_TEST = "/module/operatePosition/" + BANNER_APP_ID_TEST + "/api/content/list";
    public static final String RECIPE_DELETE_URL_TEST = "/module/recipes/" + MYUP_TOKEN_APP_ID_TEST + "/api/recipes/delete/{id}";
    public static final String BANNER_CONTENTS_URL_TEST = "/module/recipes/" + BANNER_TOKEN_APP_ID + "/api/recipes/list";
    public static final String BANNER_RECIPES_LIST_URL_TEST = "/module/{module}/" + MYUP_TOKEN_APP_ID_TEST + "/api/{api}/list";

    /**
     * 正式环境请求url
     */
    public static final String MATERIAL_LIST_URL = "/module/recipes/" + MYUP_TOKEN_APP_ID + "/api/material/list";
    public static final String RECIPE_CATEGORY_LIST_URL = "/module/recipes/" + MYUP_TOKEN_APP_ID + "/api/recipeProps/list";
    public static final String RECIPES_LIST_URL = "/module/recipes/" + MYUP_TOKEN_APP_ID + "/api/recipes/list";
    public static final String BANNER_ID_URL = "/module/operatePosition/" + BANNER_APP_ID + "/api/operatePosition/list";
    public static final String BANNER_URL = "/module/operatePosition/" + BANNER_APP_ID + "/api/content/list";
    public static final String RECIPE_DELETE_URL = "/module/recipes/" + MYUP_TOKEN_APP_ID + "/api/recipes/delete/{id}";
    public static final String BANNER_CONTENTS_URL = "/module/recipes/" + BANNER_TOKEN_APP_ID + "/api/recipes/list";
    public static final String BANNER_RECIPES_LIST_URL = "/module/{module}/" + MYUP_TOKEN_APP_ID + "/api/{api}/list";


    /**
     * 测试环境LOGIN_URL
     */
    public static final String LOGIN_URL_TEST = "device_login";
//    public static final String BASE_RECIPE_TOKEN_URL = "http://47.96.38.80:8280/";


//    public static final String RECIPE_BANNER_TOKEN_URL = "cloud-portal-plugin/apply_token";
    /**
     * 测试环境BANNER_TOKEN_URL
     */
    public static final String RECIPE_BANNER_TOKEN_URL_TEST = "v2/apply_token";

    //public static final String BASE_WEATHER_URL = "http://develop.fotile.com:4567/";
    public static final String BASE_WEATHER_URL = "http://118.31.249.164/v2/";
    public static final String CITY_URL = "city";
    public static final String WEATHER_URL = "weather";

    /**********************************获取device登录token地址↓************************************/
    //正式环境TOKEN_URL
    public static final String BASE_TOKEN_URL = "https://api.fotile.com/v2/";
    //测试环境TOKEN_URL
    public static final String BASE_TOKEN_URL_TEST = "http://118.31.249.164/v2/";
    /**********************************获取device登录token地址↑************************************/
    /**
     * 正式环境LOGIN_URL
     */
    public static final String LOGIN_URL = "device_login";

    /**********************************菜谱token地址↓************************************/
    //正式环境RECIPE_TOKEN_URL
    public static final String BASE_RECIPE_TOKEN_URL = "https://api.fotile.com/";
    //测试环境RECIPE_TOKEN_URL
    public static final String BASE_RECIPE_TOKEN_URL_TEST = "http://118.31.249.164/";
    /**********************************菜谱token地址↑************************************/

    /**
     * 正式环境BANNER_TOKEN_URL
     */
    public static final String RECIPE_BANNER_TOKEN_URL = "v2/apply_token";

    /**********************************菜谱平台地址↓************************************/
    //菜谱测试平台
    public static final String BASE_URL_RECIPE_TEST = "http://47.96.38.80:3000";
    //菜谱开发平台
    public static final String BASE_URL_RECIPE_DEVELOP = "http://118.31.249.97:3000";
    //菜谱正式服务器
    public static final String BASE_URL_RECIPE_API = "http://api.fotile.com";
    /**********************************菜谱平台地址↑************************************/

    // public static final String BASE_URL = "http://operate-console.fotile.com:80";
    public static String BASE_URL = BASE_URL_RECIPE_API;

    public static final String MESSAGES_URL_TEST = "v2/home/{home}/inbox/messages";

    public static final String MESSAGES_URL = "v2/home/{home}/inbox/messages";

    public static final String HOME_ID_URL = "device/{deviceId}";

}
