package fotile.device.cookerprotocollib.helper;

/**
 * 文件名称：CookerProtocol
 * 创建时间：2017/8/7 17:18
 * 文件作者：yaohx
 * 功能描述：jni测试类
 */
class CookerProtocol {

    public CookerProtocol() {
    }

    static {
        System.loadLibrary("cooker-fcp");   //defaultConfig.ndk.moduleName
    }

    /**
     * 初始化JNI
     *
     * @param type
     * @param mac  mac地址
     */
    public static native void init(int type, String mac);

    //注销JNI
    public static native void destroy();

    //    public native String getCStringByJni();
    public static native int getRangHoodType();
    //将回调接口传给JNI
    public static native int setCallListener(Object listener);

    //获取状态信息
//    public  native String getInf(String status);

    //上传状态信息
    public static native int setStatus(String params);

    //获取AuthCode信息
    public static native String getAuthCode();

    //获取device.device.id信息
    public static native String getDeviceID();

    public static native void setAppState(int i);

    //控制蜂蜜器
    public static native void ctrlBuzzer(int i);

    /**
     * 物联网云平台
     *
     * @param i 0表示正式地址 1表示测试地址 2表示开发环境
     */
    public static native void setHostNameOrIp(int i);

    public static native int getHostNameOrIp();

    //设备解绑
//    public static native void RemoveBinding();

    /**
     * 远程控制
     *
     * @param type 0表示停止，1表示启动，2表示解除设备绑定，其他保留
     */
    public static native void setXlinkServer(int type);

    public static native void startSelftest(int i);

    public static native void enterSelftest(int i);

    /**
     * 获取电源板版本号
     *
     * @return
     */
    public static native int getPowerPanelSoftVer();

    public static native void initBoardState();

    /**
     * 查看电源板通讯状态
     *
     * @return
     */
    public static native int getBoardState();

    /**
     * 是否开启电源板log
     *
     * @param isOpen 0开，3关
     * @return
     */
    public static native void setPrintLevel(int isOpen);

    /**
     * 设备端启动、结束录制状态 1启动 0结束
     *
     * @param i
     */
    public static native int recordStatus(int i);

    /**
     * 获取AccessToken信息
     *
     * @return
     */
    public static native String getAccessToken();

    /**
     * 获取二维码信息
     *
     * @return
     */
    public static native String getQRCode();

    /**
     * 底层获取上层本地自制菜谱的数量
     *
     * @param i
     */
    public static native void setRecipesNum(int i);


}
