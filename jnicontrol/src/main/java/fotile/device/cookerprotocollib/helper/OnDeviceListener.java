package fotile.device.cookerprotocollib.helper;

/**
 * 文件名称：OnDeviceListener
 * 创建时间：2018/6/26 17:13
 * 文件作者：yaohx
 * 功能描述：jni回调接口
 */
public interface OnDeviceListener {
    /**
     * 获取设备状态接口
     *
     * @param params
     */
    void onReportStatus(String params);

    /**
     * 获取authorcode
     *
     * @param authorcode
     */
    void onAuthorCode(String authorcode);

    /**
     * 通知
     *
     * @param params
     */
    void onMsgNotify(int code, String params);

    void onStartRecord(String params);

    void onSetSubscribeFlag(int i);

    /**
     * 录制菜谱信息上传接口
     *
     * @param params
     * @param count  开门次数
     */
    void onReportRecordMenuData(byte[] params, int count);
}
