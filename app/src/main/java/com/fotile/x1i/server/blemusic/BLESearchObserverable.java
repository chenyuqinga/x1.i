package com.fotile.x1i.server.blemusic;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ebanswers.ble.BLEDevice;
import com.ebanswers.ble.BLESerialManager;
import com.ebanswers.ble.listener.BLEScanListener;
import com.fotile.common.util.log.LogUtil;

import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 文件名称：BLESearchObserverable
 * 创建时间：2019/7/24 18:29
 * 文件作者：yaohx
 * 功能描述：蓝牙音箱设备搜索
 */
public class BLESearchObserverable {

    private Context context;
    private static BLESearchObserverable instance;

    /**
     * 搜索时长
     */
    private static final long SEARCH_TIME = 1 * 60 * 1000;

    final int WHAT_SEARCH_STOP = 1001;
    final int WHAT_SEARCH_START = 1002;
    /**
     * 是否正在搜索
     */
    private boolean searching = false;

    CopyOnWriteArrayList<Action1<BLEDevice>> list = new CopyOnWriteArrayList<Action1<BLEDevice>>();

    private BLESearchObserverable(Context context) {
        this.context = context;
        BLESerialManager.getInstance().setScanListener(bleScanListener);
    }

    public static BLESearchObserverable getInstance(Context context) {
        if (null == instance) {
            instance = new BLESearchObserverable(context);
        }
        return instance;
    }

    /**
     * 开始搜索
     */
    public synchronized void startSearch() {
        stopSearch(false);

        LogUtil.LOG_BLE_MUSIC("ble搜索开始", "----------------------------start----------------------------");
        handler.sendEmptyMessageDelayed(WHAT_SEARCH_START, 800);
        handler.sendEmptyMessageDelayed(WHAT_SEARCH_STOP, SEARCH_TIME);
        searching = true;
    }

    BLEScanListener bleScanListener = new BLEScanListener() {
        @Override
        public void onScanStop() {

        }

        @Override
        public void onAddADevice(BLEDevice device) {
            notifySearchData(device);
        }
    };

    /**
     * 停止搜索
     *
     * @param notifyUi 是否需要通知ui
     */
    public synchronized void stopSearch(boolean notifyUi) {
        searching = false;
        BLESerialManager.getInstance().stop_scan();

        if (notifyUi) {
            //告诉订阅者，搜索结束
            notifySearchData(new BLEDevice());
            LogUtil.LOG_BLE_MUSIC("ble搜索结束", "----------------------------stop----------------------------");

            handler.removeMessages(WHAT_SEARCH_START);
            handler.removeMessages(WHAT_SEARCH_STOP);
        }
    }

    public void addSearchAction(Action1<BLEDevice> action1) {
        if (null != action1 && !list.contains(action1)) {
            list.add(action1);
        }
    }

    public void removeSearchAction(Action1<BLEDevice> action1) {
        if (null != action1) {
            list.remove(action1);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //开始搜索
                case WHAT_SEARCH_START:
                    BLESerialManager.getInstance().scan();
                    break;
                //停止搜索
                case WHAT_SEARCH_STOP:
                    stopSearch(true);
                    break;
            }
            return false;
        }
    });

    /**
     * 将数据分发给订阅者
     *
     * @param bleDevice
     */
    private void notifySearchData(BLEDevice bleDevice) {
        if (null != bleDevice) {
            //设置搜索状态
            bleDevice.setSearching(searching);
            //创建一个被观察者对象
            Observable observable = Observable.just(bleDevice);
            if (null != list && !list.isEmpty()) {
                for (Action1 action : list) {
                    observable.subscribeOn(Schedulers.immediate())//指定被观察者执行的线程
                            .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                            .subscribe(action);
                }
            }
        }
    }

}
