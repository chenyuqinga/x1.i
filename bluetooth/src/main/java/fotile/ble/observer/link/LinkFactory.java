package fotile.ble.observer.link;

import android.content.Context;
import android.text.TextUtils;

import com.fotile.common.util.log.LogUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import fotile.ble.bean.BleDevice;
import fotile.ble.util.BleConstant;
import rx.functions.Action1;

/**
 * 项目名称：BleClient
 * 创建时间：2018/9/17 12:13
 * 文件作者：yaohx
 * 功能描述：生成蓝牙连接方式的工厂类
 */
public class LinkFactory {
    private LinkFactory() {

    }

    /**
     * HashMap存储每一个ble设备对应的ILinkObserverable对象
     */
    static HashMap<String, ILinkObserverable> bleLinkMap = new HashMap<String, ILinkObserverable>();

    static HashMap<String, ILinkObserverable> btLinkMap = new HashMap<String, ILinkObserverable>();


    /**
     * 创建一个连接方式
     * 每一个设备只唯一对应一个ILinkObserverable对象
     * 如果之前创建过bleDevice对应的连接对象，则直接获取
     * 反之需要重新创建一个连接对象
     *
     * @param bleDevice 需要连接的设备
     * @return
     */
    public static ILinkObserverable getLinkObserverable(BleDevice bleDevice, Context context) {
        ILinkObserverable linkObserverable = null;
        if (null != bleDevice) {
            String deviceName = bleDevice.getName();
            //BLE
            if (isBle(bleDevice)) {
                if (bleLinkMap.containsKey(deviceName)) {
                    linkObserverable = bleLinkMap.get(deviceName);
                } else {
                    linkObserverable = new BleLinkObserverable(bleDevice, context);
                    bleLinkMap.put(deviceName, linkObserverable);
                }
//               LogUtil.LOG_TOOTH("LinkFactory", "创建一个BLE连接-" + deviceName);
            }
            //BT
            else {
                if (btLinkMap.containsKey(deviceName)) {
                    linkObserverable = btLinkMap.get(deviceName);
                } else {
                    linkObserverable = new BTLinkObserverable(bleDevice, context);
                    btLinkMap.put(deviceName, linkObserverable);
                }
//               LogUtil.LOG_TOOTH("LinkFactory", "创建一个BT连接" + deviceName);
            }
        }
        return linkObserverable;
    }

    /**
     * 关闭所有蓝牙连接
     */
    public static void disConnectAllDevice() {
        //关闭所有蓝牙
        if (null != bleLinkMap && bleLinkMap.size() > 0) {
            Set<String> keySet = bleLinkMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                ILinkObserverable linkObserverable = bleLinkMap.get(iterator.next());
                linkObserverable.disConnect();
            }
        }

        if (null != btLinkMap && btLinkMap.size() > 0) {
            Set<String> keySet = btLinkMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                ILinkObserverable linkObserverable = btLinkMap.get(iterator.next());
                linkObserverable.disConnect();
            }
        }
    }

    /**
     * 移除该LinkeAction在所有ILinkObserverable中的注册回调
     * @param action1
     */
    public static void removeLinkeAction(Action1<BleDevice> action1) {
        if (null != action1) {
            if (null != bleLinkMap && bleLinkMap.size() > 0) {
                Set<String> keySet = bleLinkMap.keySet();
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    ILinkObserverable linkObserverable = bleLinkMap.get(iterator.next());
                    linkObserverable.removeLinkObserver(action1);
                }
            }

            if (null != btLinkMap && btLinkMap.size() > 0) {
                Set<String> keySet = btLinkMap.keySet();
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    ILinkObserverable linkObserverable = btLinkMap.get(iterator.next());
                    linkObserverable.removeLinkObserver(action1);
                }
            }
        }
    }


    /**
     * 是否连接上JAZ1灶具ble设备
     *
     * @return true 表示JAZ1灶具在线
     */
    public static boolean isJaz1Linked() {
        boolean result = false;
        BleDevice bleDevice = getLinkDevice();
        if (null != bleDevice) {
            result = bleDevice.getDeviceProject() == BleDevice.DEVICE_PROJECT_JAZ1;
        }
        return result;
    }

    /**
     * 获取当前已经连接的 BleDevice
     *
     * @return
     */
    public static BleDevice getLinkDevice() {
        if (null != bleLinkMap && bleLinkMap.size() > 0) {
            Set<String> keySet = bleLinkMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                ILinkObserverable linkObserverable = bleLinkMap.get(iterator.next());
                if (linkObserverable.getState() == ILinkObserverable.STATE_CONNECTED) {
                    return linkObserverable.getLinkDevice();
                }
            }
        }

        if (null != btLinkMap && btLinkMap.size() > 0) {
            Set<String> keySet = btLinkMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                ILinkObserverable linkObserverable = btLinkMap.get(iterator.next());
                if (linkObserverable.getState() == ILinkObserverable.STATE_CONNECTED) {
                    return linkObserverable.getLinkDevice();
                }
            }
        }
        return null;
    }

    /**
     * 获取当前正在连接状态中的 ILinkObserverable
     * @return
     */
    public static ILinkObserverable getLinkingObserverable(){
        if (null != bleLinkMap && bleLinkMap.size() > 0) {
            Set<String> keySet = bleLinkMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                ILinkObserverable linkObserverable = bleLinkMap.get(iterator.next());
                if (linkObserverable.getState() == ILinkObserverable.STATE_CONNECTING) {
                    return linkObserverable;
                }
            }
        }

        if (null != btLinkMap && btLinkMap.size() > 0) {
            Set<String> keySet = btLinkMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                ILinkObserverable linkObserverable = btLinkMap.get(iterator.next());
                if (linkObserverable.getState() == ILinkObserverable.STATE_CONNECTING) {
                    return linkObserverable;
                }
            }
        }
        return null;
    }

    /**
     * 是否是ble设备
     *
     * @return
     */
    private static boolean isBle(BleDevice bleDevice) {
        String deviceName = bleDevice.getName();
        if (!TextUtils.isEmpty(deviceName) && deviceName.contains(BleConstant.DEVICE_NAME_JAZ1)) {
            return true;
        }
        return false;
    }

}
