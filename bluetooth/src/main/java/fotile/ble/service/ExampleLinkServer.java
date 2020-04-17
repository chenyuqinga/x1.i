package fotile.ble.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;



import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import fotile.ble.bean.BleDevice;
import fotile.ble.bean.BleMessage;
import fotile.ble.observer.link.ILinkObserverable;
import fotile.ble.observer.link.LinkFactory;
import fotile.ble.observer.search.SearchObserverable;
import rx.functions.Action1;

import static fotile.ble.bean.BleDevice.SEARCHING;
import static fotile.ble.bean.BleDevice.SEARCHOVER;
import static fotile.ble.bean.BleMessage.BLE_MSG_CLOSE;
import static fotile.ble.bean.BleMessage.BLE_MSG_LINK;
import static fotile.ble.observer.link.ILinkObserverable.STATE_CONNECTED;
import static fotile.ble.observer.link.ILinkObserverable.STATE_CONNECTING;
import static fotile.ble.observer.link.ILinkObserverable.STATE_ERROR;
import static fotile.ble.observer.link.ILinkObserverable.STATE_NONE;
import static fotile.ble.observer.search.SearchObserverable.SEARCH_TIME_NORMAL;

/**
 * 项目名称：BleClient
 * 创建时间：2018/9/28 13:56
 * 文件作者：yaohx
 * 功能描述：
 * 1、蓝牙搜索逻辑由Server执行，在onDeviceSearch中
 * 2、搜索开始后，发现上一次连接过的蓝牙，开启自动连接
 * 3、处理蓝牙断电 breakOffListener
 * 4、处理由view发起的EventBus事件，蓝牙搜索\连接\断开
 * 5、LinkAction 处理连接回调后的一些数据保存和重连机制
 * 6、该Server类和引用项目中的Server应该一致
 */
@Deprecated
public class ExampleLinkServer extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
//    /**
//     * 服务中只有一个搜索对象
//     */
//    private SearchObserverable bleSearchObserverable;
//
//    private Action1<BleDevice> actionSearch;
//
//    /**
//     * 延迟获取deviceId
//     */
//    private final int WHAT_DELAY_DEVICE_ID = 1001;
//    /**
//     * 连接蓝牙
//     */
//    private final int WHAT_LINK_TOOTH = 1002;
//    /**
//     * 服务开启时，第一次连接失败，重连次数
//     */
//    private int error_retry_link = 0;
//
//    final int WHAT_FIRST_START = 1003;
//    final int WHAT_FIRST_CLOSE = 1004;
//    final int WHAT_FIRST_RESTART = 1005;
//
//    /**
//     * 延迟连接时间short
//     */
//    private final int DELAY_LINK_TIME_SHORT = 800;
//    /**
//     * 延迟连接时间long
//     */
//    private final int DELAY_LINK_TIME_LONG = 1200;
//
//    /**
//     * 是否自动重连到上一次连接的设备
//     */
//    private boolean autoLinkLast = false;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        createSearchAction();
//        initData();
//    }
//
//    private void initData() {
//        EventBus.getDefault().register(this);
//        bleSearchObserverable = SearchObserverable.getInstance(this);
//        bleSearchObserverable.setOnDeviceBreakOffListener(breakOffListener);
//        //注册action
//        bleSearchObserverable.addSearchObserver(actionSearch);
//        //服务开启时，开始蓝牙搜索
//        startSearch();
//    }
//
//    /**
//     * 监听蓝牙设备连接断开--蓝牙断电或者灶具断电
//     */
//    private SearchObserverable.OnDeviceBreakOffListener breakOffListener = new SearchObserverable
//            .OnDeviceBreakOffListener() {
//        @Override
//        public void onDeviceBreakOff(BleDevice bleDevice) { //断电的蓝牙模块
//            if (null != bleDevice) {
//                ILinkObserverable linkObserverable = LinkFactory.getLinkObserverable(bleDevice);
//                if (null != linkObserverable) {
//                    int state = linkObserverable.getState();
//                    //灶具断电，本地连接状态没有更新，所以还是连接状态
//                    if (state == STATE_CONNECTED) {
//                        //当前连接的mac地址
//                        String linkDeviceMac = linkObserverable.getLinkDevicedMac();
//                        //断开设备的mac地址
//                        String breakDeviceMac = bleDevice.getAddress();
//
//                        //如果断开的设备的mac和本地暂时连接的mac一致，说明断开的设备是正在连接的设备
//                        if (breakDeviceMac.equals(linkDeviceMac)) {
//                            LogUtil.LOG_TOOTH("蓝牙设备连接断开", "蓝牙断电或者灶具断电");
//                            //关闭蓝牙连接，目的是刷新listadapter
//                            linkObserverable.disConnect();
//                            //发起重连
//                            linkObserverable.retryConnect();
//                        }
//                    }
//                }
//            }
//        }
//    };
//
//    /**
//     * 蓝牙搜索回调
//     */
//    public void createSearchAction() {
//        actionSearch = new Action1<BleDevice>() {
//            @Override
//            public void call(BleDevice bleDevice) {
//                //搜索中
//                if (bleDevice.searchStatus == SEARCHING) {
//                    BluetoothDevice bluetoothDevice = bleDevice.bluetoothDevice;
//                    if (null == bluetoothDevice) {
//                        return;
//                    }
//                    //如果当前为非连接状态-自动连接到一个已经连接过的设备，该设备未非连接状态
//                    String lastLinkName = (String) PreferenceUtil.getPreferenceValue(ExampleLinkServer
//                            .this, PreferenceUtil.BLUE_LINK_DEVICE_NAME, "");
//                    String lastLinkMac = (String) PreferenceUtil.getPreferenceValue(ExampleLinkServer
//                            .this, PreferenceUtil.BLUE_LINK_DEVICE_ADDRESS, "");
//
//                    if (autoLinkLast) {
//                        //如果搜索到最近一次连接到的设备
//                        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(address)) {
//                            if (name.equals(lastLinkName) && address.equals(lastLinkMac)) {
//                                connectDevice(bleDevice, DELAY_LINK_TIME_SHORT);
//                            }
//                        }
//                    }
//                }
//                //结束搜索
//                if (bleDevice.searchStatus == SEARCHOVER) {
//
//                }
//            }
//        };
//    }
//
//    //读取蓝牙数据回调
//    class ReadAction implements Action1<byte[]> {
//        @Override
//        public void call(byte[] data_write) {
//            // do something yourself
//        }
//    }
//
//    //Link 回调Action1,一个连接对象可以有多个回调
//    class LinkAction implements Action1<BleDevice> {
//        @Override
//        public void call(BleDevice bleDevice) {
//            ILinkObserverable linkObserverable = LinkFactory.getLinkObserverable(bleDevice);
//            //每一次有连接结果，均取消上一次的心跳
//            linkObserverable.cancelBeatTimer();
//            int state = bleDevice.linkStatus;
//
//            String deviceName = bleDevice.getName();
//            String address = bleDevice.getAddress();
//            //连接中
//            if (state == STATE_CONNECTING) {
//
//            }
//            //连接成功
//            if (state == STATE_CONNECTED) {
//                //连接成功之后就取消重连定时器
//                linkObserverable.cancelRetryTimer();
//
//                LogUtil.LOG_TOOTH("蓝牙连接成功-BlueToothServer", deviceName);
//                //保存连接成功的设备名称
//                PreferenceUtil.setPreferenceValue(ExampleLinkServer.this, PreferenceUtil.BLUE_LINK_DEVICE_NAME,
//                        deviceName);
//                PreferenceUtil.setPreferenceValue(ExampleLinkServer.this, PreferenceUtil.BLUE_LINK_DEVICE_ADDRESS, address);
//                //连接成功获取灶具deviceId
//                handler_delay.sendEmptyMessageDelayed(WHAT_DELAY_DEVICE_ID, 1500);
//                //连接成功之后向灶具发送心跳包
//                linkObserverable.startBeatTimer();
//            }
//            //连接手动关闭-或者关闭灶具蓝牙
//            if (state == STATE_NONE) {
//
//            }
//            //数据连接失败-传输失败
//            if (state == STATE_ERROR) {
//                //第一次开启服务时，连接失败，重连
//                //解决灶具断电重启，联动开关断开重连--搜索到蓝牙设备越久连接上的概率越高
//                if (error_retry_link <= 4 && error_retry_link >= 0) {
//                    //连接失败后，重连一次
//                    connectDevice(bleDevice, DELAY_LINK_TIME_LONG);
//                    error_retry_link++;
//                }
//            }
//        }
//    }
//
//    /**
//     * 开启一个新的连接，上一次的连接不断开
//     *
//     * @param bleDevice
//     * @param delay_link_time 延迟发起连接的时间
//     */
//    private synchronized void connectDevice(final BleDevice bleDevice, int delay_link_time) {
//        if (null != bleDevice) {
//            BleDecorator bleDecorator = new BleDecorator(bleDevice, new LinkAction(), new ReadAction());
//            ILinkObserverable linkObserverable = LinkFactory.createLinkObserverable(bleDevice, bleDecorator,
//                    ExampleLinkServer.this);
//            //如果正在连接中，return
//            int link_state = linkObserverable.getState();
//            if (link_state == STATE_CONNECTING) {
//                return;
//            }
//
//            //关闭该设备原有的连接
//            linkObserverable.disConnect();
//            Message message = new Message();
//            message.obj = bleDevice;
//            message.what = WHAT_LINK_TOOTH;
//            handler_delay.sendMessageDelayed(message, delay_link_time);
//        }
//    }
//
//    Handler handler_delay = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            //延迟获取deviceId
//            if (msg.what == WHAT_DELAY_DEVICE_ID) {
//
//            }
//
//            //恢复出厂之后开启
//            if (msg.what == WHAT_FIRST_START && null != bleSearchObserverable) {
//                bleSearchObserverable.startBleSearch(SEARCH_TIME_NORMAL);
//            }
//            //恢复出厂之后关闭
//            if (msg.what == WHAT_FIRST_CLOSE && null != bleSearchObserverable) {
//                bleSearchObserverable.stopBleSearch();
//            }
//            //恢复出厂之后开启
//            if (msg.what == WHAT_FIRST_RESTART && null != bleSearchObserverable) {
//                //上一次保存的开关状态
//                boolean device_link_smoke_stove = (boolean) PreferenceUtil.getPreferenceValue(ExampleLinkServer
//                        .this, PreferenceUtil.DEVICE_LINK_SMOKE_STOVE, false);
//                //如果在这其间用户没有主动关闭
//                if (device_link_smoke_stove) {
//                    bleSearchObserverable.startBleSearch(SEARCH_TIME_FIRST);
//                }
//            }
//            //连接蓝牙
//            if (msg.what == WHAT_LINK_TOOTH) {
//                BleDevice bleDevice = (BleDevice) msg.obj;
//                ILinkObserverable linkObserverable = LinkFactory.getLinkObserverable(bleDevice);
//                linkObserverable.connect();
//            }
//            return false;
//        }
//    });
//
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        closeAllLink();
//    }
//
//    /**
//     * 关闭所有蓝牙连接
//     */
//    private void closeAllLink() {
//        //关闭蓝牙搜索
//        if (null != bleSearchObserverable) {
//            bleSearchObserverable.stopBleSearch();
//        }
//        //关闭所有蓝牙连接
//        LinkFactory.disConnectAllDevice();
//        //取消Message
//        handler_delay.removeCallbacksAndMessages(null);
//        error_retry_link = -1;
//    }
//
//    /**
//     * 开始蓝牙搜索
//     */
//    private void startSearch() {
//        error_retry_link = 0;
//
//        boolean first_open = (boolean) PreferenceUtil.getPreferenceValue(this, PreferenceUtil
//                .FIRST_OPEN_STOVE_LINK, true);
//        //解决恢复出厂后，第一次搜索的蓝牙没有名称的bug
//        if (first_open) {
//            //先搜索
//            handler_delay.sendEmptyMessage(WHAT_FIRST_START);
//            //暂停15秒后，关闭
//            handler_delay.sendEmptyMessageDelayed(WHAT_FIRST_CLOSE, 12000);
//            //再暂停5秒，开始搜索
//            handler_delay.sendEmptyMessageDelayed(WHAT_FIRST_RESTART, 12000 + 5000);
//
//            PreferenceUtil.setPreferenceValue(this, PreferenceUtil.FIRST_OPEN_STOVE_LINK, false);
//        }
//        //正常流程
//        else {
//            //开启蓝牙搜索
//            bleSearchObserverable.startBleSearch(SEARCH_TIME_NORMAL);
//        }
//    }
//
//    /********************************************************************/
//
//    //开始蓝牙搜索
//    @Subscribe
//    public void onDeviceSearch(BleMessage blueToothMessage) {
//        if (blueToothMessage.to_class.getSimpleName().contains("ExampleLinkServer")) {
//            if (blueToothMessage.blueToothType == BLE_MSG_SEARCH) {
//                startSearch();
//            }
//        }
//    }
//
//    //开始连接某一个蓝牙
//    @Subscribe
//    public void onDeviceLink(BleMessage blueToothMessage) {
//        if (blueToothMessage.to_class.getSimpleName().contains("ExampleLinkServer")) {
//            if (blueToothMessage.blueToothType == BLE_MSG_LINK) {
//                if (null != blueToothMessage.bleDevice) {
//                    error_retry_link = 0;
//                    connectDevice(blueToothMessage.bleDevice, DELAY_LINK_TIME_SHORT);
//                }
//            }
//        }
//    }
//
//    // 关闭某一个蓝牙
//    @Subscribe
//    public void onDeviceClose(BleMessage bleMessage) {
//        if (bleMessage.to_class.getSimpleName().contains("ExampleLinkServer")) {
//            if (bleMessage.blueToothType == BLE_MSG_CLOSE) {
//                //关闭蓝牙搜索
//                if (null != bleSearchObserverable) {
//                    bleSearchObserverable.stopBleSearch();
//                }
//                ILinkObserverable linkObserverable = LinkFactory.getLinkObserverable(bleMessage.bleDevice);
//                if (null != linkObserverable) {
//                    linkObserverable.disConnect();
//                }
//                //取消Message
//                handler_delay.removeCallbacksAndMessages(null);
//                error_retry_link = -1;
//            }
//        }
//    }
}
