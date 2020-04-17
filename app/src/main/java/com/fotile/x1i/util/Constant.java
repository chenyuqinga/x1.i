package com.fotile.x1i.util;


/**
 * 文件名称：SettingConstant
 * 创建时间：2018/7/4 11:58
 * 文件作者：yaohx
 * 功能描述：存放常量的类
 */
public class Constant {

    /*********************************打包需要全部更改为false↓*********************************/
    /**
     * 屏保处于测试状态，打包时改成 false
     */
    public static boolean IS_SCREEN_TEST = false;
    /**
     * 故障界面处于测试状态（测试时均不显示故障界面），打包改成 false
     */
    public static boolean FAULT_SHOW_TEST = false;
    /*********************************打包需要全部更改为false↑*********************************/
    /**
     * 乐投包名
     */
    public static final String PACKAGE_NAME_LT = "com.hpplay.happyplay.aw";
    /**
     * 项目的缓存文件存放目录
     * /sdcard/fotile/x1i/
     */
    public static String FILE_CACHE_FOLDER = "/sdcard/fotile/x1i/";
    /**
     * 存放图片缓存的目录
     */
    public static final String FILE_CACHE_FOLDER_PIC = "pic";
    /**
     * 爱奇艺目录
     */
    public static final String FILE_CACHE_FOLDER_AQY = "aqy";
    /**
     * IKCC_NAME
     */
    public static final String IKCC_NAME = "FotileAP_";
    /**
     * 屏幕尺寸width
     */
    public static final int SCREEN_WIDTH = 1280;
    /**
     * 屏幕尺寸height
     */
    public static final int SCREEN_HEIGHT = 800;

    public static final String PRIDUCT_ID_TEST = "b815c4def4f344a0bf558d8abf7eea85";
    /**
     * 工程模式数据版本号
     */
    public static final int FACTORY_VERSION_CODE = 5;


    public static final String OTA_FOTILE_URL = "http://ota.fotile.com:8080/fotileAdminSystem/upgrade.do?package=%s&version=%s&mac=%s";

    /**
     * 愛奇艺包名
     */
    public static final String IQIYI_PACKAGE_NAME = "com.qiyi.video.pad";
    /**
     * 在线视频状态改变的广播
     */
    public static final String ACTION_VIDEO_STATUS_CHANGED = "com.fotile.c2i.ACTION_VIDEO_STATUS_CHANGED";
    /**
     * 方太画报标记
     */
    public static final int SCREEN_FOTILE_SAVER = -100;

    /**
     * 喜马拉雅接口签名
     */
    public static final String appSecret = "7569aa71f10d4272fb2b40a0a1587200";

    /**
     * 更新音量
     */
    public static final int TYPE_VOLULME = 20010;

    /**
     * 更新亮度
     */
    public static final int TYPE_BRIGHTNESS = 20011;
}
