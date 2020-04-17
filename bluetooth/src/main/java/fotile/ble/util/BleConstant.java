package fotile.ble.util;


public class BleConstant {

    /**
     * 是否打印心跳包数据
     */
    public static final boolean LOG_BEAT = true;

    /**
     * 需要连接到的蓝牙设备名称-被包含关系
     */
    public static final String DEVICE_NAME_C2I = "FOTILE_TCC2.I_";

    public static final String DEVICE_NAME_JAZ1 = "FOTILE_HAZ1_";

    /**
     * service的uuid
     */
    public static final String UUID_SERVICE = "0000a00a-0000-1000-8000-00805f9b34fb";
    /**
     * characteristic写入数据UUID
     */
    public static final String UUID_CHARAC_WRITE = "0000b002-0000-1000-8000-00805f9b34fb";
    /**
     * characteristic读取数据UUID
     */
    public static final String UUID_CHARAC_READ = "0000b003-0000-1000-8000-00805f9b34fb";
    /**
     * 发送给灶具的心跳包
     * f4 f5 00 08 05 00 02 01 00 00 00 0f
     */
    public static final byte[] BEAT_DATA = {(byte) 0xf4, (byte) 0xf5, 0x00, 0x08, 0x05, 0x00, 0x02, 0x01, 0x00, 0x00, 0x00, 0x0f};

}
