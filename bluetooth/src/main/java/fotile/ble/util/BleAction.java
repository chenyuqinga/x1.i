package fotile.ble.util;


import android.content.Context;
import android.content.Intent;


import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;

import org.greenrobot.eventbus.EventBus;

import fotile.ble.bean.BleDevice;
import fotile.ble.bean.BleMessage;

import static fotile.ble.bean.BleMessage.BLE_MSG_CLOSE;
import static fotile.ble.bean.BleMessage.BLE_MSG_LINK;
import static fotile.ble.bean.BleMessage.BLE_MSG_SEARCH_START;
import static fotile.ble.bean.BleMessage.BLE_MSG_SEARCH_STOP;
import static fotile.ble.bean.BleMessage.BLE_MSG_WRITE;

/**
 * 文件名称：BleAction
 * 创建时间：2018/5/22 10:05
 * 文件作者：yaohx
 * 功能描述：控制蓝牙连接、断开操作类
 */
public class BleAction {
    private static BleAction bleAction;

    private BleAction() {

    }

    public static BleAction getInstance() {
        if (null == bleAction) {
            bleAction = new BleAction();
        }
        return bleAction;
    }

    /**
     * 开始蓝牙搜索
     * 蓝牙搜索统一入口，搜索执行逻辑在BleServer中
     *
     * @param to_class 处理逻辑的Server
     */
    public void startDeviceSearch(Class to_class,Context context) {
        boolean device_link = PreferenceUtil.getBleMusicSwitch(context);
        LogUtil.LOG_TOOTH("蓝牙搜索startDeviceSearch device_link", device_link);
        if(device_link){
            BleMessage bleMessage = new BleMessage(to_class, BLE_MSG_SEARCH_START);
            EventBus.getDefault().post(bleMessage);
        }
    }

    /**
     * 停止蓝牙搜索
     * @param to_class
     */
    public void stopDeviceSearch(Class to_class){
        BleMessage bleMessage = new BleMessage(to_class, BLE_MSG_SEARCH_STOP);
        EventBus.getDefault().post(bleMessage);
    }

    /**
     * 开始连接蓝牙
     *
     * @param bleDevice
     * @param to_class  处理逻辑的Server class类型
     */
    public void startDeviceLink(BleDevice bleDevice, Class to_class) {
        if (null != bleDevice) {
            BleMessage bleMessage = new BleMessage(to_class, BLE_MSG_LINK);
            bleMessage.setBleDevice(bleDevice);
            EventBus.getDefault().post(bleMessage);
        }
    }

    /**
     * 写入数据
     *
     * @param bleDevice
     * @param to_class
     */
    public void writeDeviceData(BleDevice bleDevice, Class to_class) {
        if (null != bleDevice) {
            BleMessage bleMessage = new BleMessage(to_class, BLE_MSG_WRITE);
            bleMessage.setBleDevice(bleDevice);
            EventBus.getDefault().post(bleMessage);
        }
    }

    /**
     * 关闭蓝牙连接以及回收资源
     *
     * @param to_class 处理逻辑的Server class类型
     */
    public void closeDeviceLink(BleDevice bleDevice, Class to_class) {
        BleMessage bleMessage = new BleMessage(to_class, BLE_MSG_CLOSE);
        bleMessage.setBleDevice(bleDevice);
        EventBus.getDefault().post(bleMessage);
    }

    /**
     * 关闭服务
     *
     * @param server 处理逻辑的Server class类型
     */
    public void stopLinkServer(Class server, Context context) {
        Intent intent = new Intent(context, server);
        context.stopService(intent);
    }

}
