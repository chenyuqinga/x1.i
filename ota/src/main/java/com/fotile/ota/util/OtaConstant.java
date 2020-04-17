
package com.fotile.ota.util;

import android.os.Environment;


/**
 * 文件名称：OtaConstant
 * 创建时间：2018/11/6 11:27
 * 文件作者：yaohx
 * 功能描述：OtaConstant
 */
public class OtaConstant {
    /*********************************打包需要全部更改为false↓*********************************/
    /**
     * 打包改为 false
     */
    public static final boolean OTA_URL_TEST = false;
    /*********************************打包需要全部更改为false↑*********************************/

    /**
     * 固件包文件名称
     */
    public final static String FILE_OTA_NAME = "X1iCH-release-signed.apk";
    /**
     * 测试固件包保存的文件夹--/sdcard/ota
     */
    private final static String file_folder_test = Environment.getExternalStorageDirectory().getPath() + "/fotile_ota/";
    /**
     * 正式固件包保存的文件夹--/data/media
     */
//    private final static String file_folder_normal = Environment.getDataDirectory() + "/media/";
    private final static String file_folder_normal = Environment.getExternalStorageDirectory() + "/";
    /**
     * 固件包的下载目录
     */
    public final static String FILE_FOLDER = OTA_URL_TEST ? file_folder_test : file_folder_normal;
    /**
     * 固件包的完整名称
     */
    public final static String FILE_ABSOLUTE_OTA = FILE_FOLDER + FILE_OTA_NAME;
    /**
     * OTA解密密码
     */
    public static final String PASSWORD =
            "9588028820109132570743325311898426347857298773549468758875018579537757772163084478873699447306034466200616411960574122434059469100235892702736860872901247123456";
    /**
     * 测试时使用的固件包 url
     */
    public static String test_url = "http://develop.fotile.com:8080/fotileAdminSystem/upgrade.do?package=com.fotile"
            + ".c2i.steamermicro&version=C2ZW-SA103&mac=0025921941a1";

    /**
     * MCU升级包文件名称
     */
    public final static String FILE_MCU_NAME = "mcu.bin";
    /**
     * mcu包的完整名称
     */
    public final static String FILE_ABSOLUTE_MCU = FILE_FOLDER + FILE_MCU_NAME;

    public static final String ACTION_OTA_BOOT = "com.fotile.ota.z15.OtaBootReceiver";

}
