package com.fotile.recipe.uitl;


/**
 * 项目名称：X1.I
 * 创建时间：2018/12/11 14:50
 * 文件作者：yaohx
 * 功能描述：
 */
public class RecipeConstant {
    /*********************************打包需要全部更改为false↓*********************************/
    /**
     * token测试字段，用于测试获取token，打包时改成 false
     */
    public static boolean RECIPE_TOKEN_TEST = false;
    /**
     * 本地菜谱数据是否来自网络，打包时改成 false
     */
    public static final boolean LOCAL_RECIPE_FROM_NET = false;
    /*********************************打包需要全部更改为false↑*********************************/

    /**
     * 根据设备名称获取对应的菜谱
     */
    public static final String RECIPE_CATEGORY = "X2.i灶蒸烤一体机";
    /**
     * 不同设备的菜谱轮播
     */
    public static final String RECIPE_DEVICES_BANNER = "C2.i灶具轮播位";
    /**
     * 达人秀标识
     */
    public static final String RECIPE_ADULTS = "C2.i灶具达人秀";

    //菜谱请求类型:正式环境
    public static final int TYPE_REQUEST_ONLINE = 0;
    //菜谱请求类型:测试环境
    public static final int TYPE_REQUEST_TEST = 1;
    //菜谱请求类型:开发环境
    public static final int TYPE_REQUEST_DEVELOP = 2;

    //本地菜谱数据库名称
    public static final String DB_LOCAL_RECIPE = "Local-db";
    //网络菜谱数据库名称（达人秀/菜谱轮播/我的上传）--开发中轮播菜谱没有保存到该数据库
    public static final String DB_NETWORK_RECIPE = "Net-db";
    //我的收藏数据库名称
    public static final String DB_FAVORITE_RECIPE = "Favorite-db";

    //灶具product_id
    public final static String STOVE_PRODUCT_ID = "1607d4b3bbb004381607d4b3bbb06c01";
    //油烟机product_id
    public final static String LAMPBLACK_PRODUCT_ID = "cd558da92007499795ef0d4d75ce19a7";

    //插入数据库时用于标识菜谱类型:主页轮播菜谱
    public static final int TYPE_HOME_RECIPE = 0;
    //插入数据库时用于标识菜谱类型:达人秀菜谱
    public static final int TYPE_ADULT_RECIPE = 1;
    //插入数据库时用于标识菜谱类型:除主页轮播菜谱和达人秀菜谱以外的菜谱,如本地菜谱/收藏菜谱/上传菜谱
    public static final int TYPE_OTHER_RECIPE = 2;

    //菜谱数据表标识
    public static final String RECIPE_TABLE = "1";
    //达人秀菜谱数据表标识
    public static final String ADULT_TABLE = "2";

    /**
     * 菜谱分类、菜谱达人秀、菜谱详情中的图片压缩比例
     */
    public static final float GLIDE_THUMBNAIL_RECIPE_LIST = 0.8f;
    public static final float GLIDE_THUMBNAIL_RECIPE_DETAIL = 0.6f;

    //官方菜谱标识值-值来自recipe对象
    public static final String RECIPE_SOURCE_OFFICIAL = "1";
    //用户上传菜谱标识值-值来自recipe对象
    public static final String RECIPE_SOURCE_UPLOAD = "2";
    //达人秀菜谱标识值-值来自recipe对象
    public static final String RECIPE_SOURCE_ADULT = "3";

    /**
     * 我的收藏限制数量
     */
    public static final int MAX_MY_FAVORITE_VALUE = 20;
    /**
     * 获取到灶具mac地址后，通知Service重新获取token
     */
    public static final String ACTION_UPDATE_STOVE_INFO = "com.fotile.z15.ACTION_UPDATE_STOVE_INFO";

}
