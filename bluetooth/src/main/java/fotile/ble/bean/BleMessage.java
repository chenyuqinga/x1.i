package fotile.ble.bean;

/**
 * 文件名称：BleMessage
 * 创建时间：2018/6/1 10:03
 * 文件作者：yaohx
 * 功能描述：在BlueToothServer和其他类之间传递的蓝牙交互Message
 */
public class BleMessage {
    /**
     * 开始蓝牙搜索
     */
    public static final int BLE_MSG_SEARCH_START = 0x01;
    /**
     * 开始蓝牙连接
     */
    public static final int BLE_MSG_LINK = 0x02;
    /**
     * 关闭蓝牙连接
     */
    public static final int BLE_MSG_CLOSE = 0x03;
    /**
     * 写入蓝牙数据
     */
    public static final int BLE_MSG_WRITE = 0x04;
    /**
     * 停止蓝牙搜索
     */
    public static final int BLE_MSG_SEARCH_STOP = 0x05;
    public int blueToothType;

    /**
     * 消息交给某一个类去处理
     */
    public Class to_class;
    public BleDevice bleDevice;

    /**
     * 蓝牙连接错误
     */
    public static final int BLE_LINK_RESULT_ERROR = 1001;
    /**
     * 用户主动断开
     */
    public static final int BLE_LINK_RESULT_CLOSE = 1002;

    public BleMessage(Class to_class, int blueToothType) {
        this.to_class = to_class;
        this.blueToothType = blueToothType;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

}
