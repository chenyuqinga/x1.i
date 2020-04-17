package fotile.ble.bean;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import fotile.ble.util.BleConstant;

/**
 * 文件名称：BleDevice
 * 创建时间：2018/9/3 18:08
 * 文件作者：yaohx
 * 功能描述：Ble设备或者BT设备
 */
public class BleDevice implements Parcelable {

    /**
     * 远程设备
     */
    public BluetoothDevice bluetoothDevice;
    /**
     * 搜索状态
     */
    public int searchStatus;
    /**
     * 正在搜索
     */
    public static final int SEARCHING = 1;
    /**
     * 搜索结束
     */
    public static final int SEARCHOVER = -1;
    /**********************************************************/

    /**
     * 连接状态，对应linkObserverable中的状态
     */
    public int linkStatus;
    /**
     * 写入蓝牙的字节数组
     */
    public byte[] data_write;
    /**
     * 读取蓝牙通道的数据
     */
    public byte[] data_read;
    /**
     * 记录这个设备被连接的次数
     */
    public int link_count = 0;
    /**
     * 表示是否是用户手动去连接该设备
     */
    public boolean active;

    /**********************************************************/
    /**
     * 处理C2设备蓝牙对应的业务
     */
    public static final int DEVICE_PROJECT_C2I = 0x01;
    /**
     * 处理御厨设备蓝牙对应的业务
     */
    public static final int DEVICE_PROJECT_JAZ1 = 0x02;

    public BleDevice(int searchStatus) {
        this.searchStatus = searchStatus;
    }

    /**
     * 搜索ble对应的构造方法
     *
     * @param bluetoothDevice
     * @param searchStatus
     */
    public BleDevice(BluetoothDevice bluetoothDevice, int searchStatus) {
        this.bluetoothDevice = bluetoothDevice;
        this.searchStatus = searchStatus;
    }

    public BleDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    /**
     * 根据蓝牙名称获取对应的项目，来处理对应的业务逻辑
     *
     * @return
     */
    public int getDeviceProject() {
        int result = -1;
        String deviceName = getName();
        if (!TextUtils.isEmpty(deviceName)) {
            //C2灶具
            if (deviceName.contains(BleConstant.DEVICE_NAME_C2I)) {
                result = DEVICE_PROJECT_C2I;
            }
            //御厨三眼灶
            if (deviceName.contains(BleConstant.DEVICE_NAME_JAZ1)) {
                result = DEVICE_PROJECT_JAZ1;
            }
        }
        return result;
    }

    /**
     * 获取设备名称，当ble设备断开后获取的name为空
     *
     * @return
     */
    public String getName() {
        if (null != bluetoothDevice) {
            String name = bluetoothDevice.getName();
            if (!TextUtils.isEmpty(name)) {
                return name;
            }
        }
        return "";
    }

    public String getAddress() {
        if (null != bluetoothDevice) {
            String mac = bluetoothDevice.getAddress();
            if (!TextUtils.isEmpty(mac)) {
                return mac;
            }
        }
        return "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.bluetoothDevice, flags);
        dest.writeInt(this.searchStatus);
        dest.writeInt(this.linkStatus);
        dest.writeByteArray(this.data_write);
        dest.writeByteArray(this.data_read);
        dest.writeInt(this.link_count);
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
    }

    protected BleDevice(Parcel in) {
        this.bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        this.searchStatus = in.readInt();
        this.linkStatus = in.readInt();
        this.data_write = in.createByteArray();
        this.data_read = in.createByteArray();
        this.link_count = in.readInt();
        this.active = in.readByte() != 0;
    }

    public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
        @Override
        public BleDevice createFromParcel(Parcel source) {
            return new BleDevice(source);
        }

        @Override
        public BleDevice[] newArray(int size) {
            return new BleDevice[size];
        }
    };
}
