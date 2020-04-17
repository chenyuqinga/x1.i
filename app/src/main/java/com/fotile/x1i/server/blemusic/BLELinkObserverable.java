package com.fotile.x1i.server.blemusic;

import android.text.TextUtils;

import com.ebanswers.ble.BLEDevice;
import com.ebanswers.ble.BLESerialManager;
import com.ebanswers.ble.listener.BLEConnectListener;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;

import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 文件名称：BLELinkObserverable
 * 创建时间：2019/7/25 10:21
 * 文件作者：yaohx
 * 功能描述：处理蓝牙连接回调
 */
public class BLELinkObserverable {

    private static BLELinkObserverable instance;
    private CopyOnWriteArrayList<Action1<String>> list = new CopyOnWriteArrayList<Action1<String>>();

    public BLELinkObserverable() {

    }

    public synchronized static BLELinkObserverable getInstance() {
        if (null == instance) {
            instance = new BLELinkObserverable();
        }
        return instance;
    }

    /**
     * 需要在程序启动时调用
     */
    public void init() {
        //添加连接回调接口
        BLESerialManager.getInstance().setDeviceConnectListener(connectListener);
        String mac = Tool.getLocalMacAddress();
        String name = "X1.i-" + (TextUtils.isEmpty(mac) ? "" : mac.substring(mac.length() - 4));
        //修改ble模块名称
        BLESerialManager.getInstance().changeDeviceName(name);
        //设置配对码
        BLESerialManager.getInstance().setPairCode("0000");
    }

    public void addLinkAction(Action1<String> action1) {
        if (null != action1 && !list.contains(action1)) {
            list.add(action1);
        }
    }

    public void removeLinkAction(Action1<String> action1) {
        if (null != action1) {
            list.remove(action1);
        }
    }

    public void connect(BLEDevice bleDevice) {
        LogUtil.LOG_BLE_MUSIC("蓝牙音箱发起连接", bleDevice);
        BLESerialManager.getInstance().connectDevice(bleDevice.getMac_address());
    }

    public void disConnect(BLEDevice bleDevice) {
        LogUtil.LOG_BLE_MUSIC("蓝牙音箱断开连接", bleDevice);
        BLESerialManager.getInstance().disconnect();
    }

    //板子ble连接回调
    BLEConnectListener connectListener = new BLEConnectListener() {
        @Override
        public void deviceConnected(String connected_mac, String connected_name) {
            LogUtil.LOG_BLE_MUSIC("蓝牙音箱Connected", connected_name);
            notifyLinkData(connected_mac);
        }

        @Override
        public void deviceDisconnected() {
            notifyLinkData("");
        }
    };


    /**
     * 将数据分发给订阅者
     *
     * @param connected_mac
     */
    private void notifyLinkData(String connected_mac) {
        //创建一个被观察者对象
        Observable observable = Observable.just(connected_mac);
        if (null != list && !list.isEmpty()) {
            for (Action1 action : list) {
                observable.subscribeOn(Schedulers.immediate())//指定被观察者执行的线程
                        .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                        .subscribe(action);
            }
        }
    }

}
