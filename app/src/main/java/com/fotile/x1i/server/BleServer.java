package com.fotile.x1i.server;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;


import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.bean.event.FinishActivityMessage;
import com.fotile.x1i.server.smokelink.SmokeStoveLinkAction;
import com.fotile.x1i.server.wifilink.StoveControl;
import com.fotile.x1i.widget.TopBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import fotile.ble.bean.BleDevice;
import fotile.ble.bean.BleMessage;
import fotile.ble.observer.link.ILinkObserverable;
import fotile.ble.observer.link.LinkFactory;
import fotile.ble.observer.search.SearchObserverable;
import fotile.device.cookerprotocollib.helper.DeviceControl;
import rx.functions.Action1;

import static fotile.ble.bean.BleDevice.DEVICE_PROJECT_C2I;
import static fotile.ble.bean.BleDevice.DEVICE_PROJECT_JAZ1;
import static fotile.ble.bean.BleDevice.SEARCHING;
import static fotile.ble.bean.BleDevice.SEARCHOVER;
import static fotile.ble.bean.BleMessage.BLE_LINK_RESULT_CLOSE;
import static fotile.ble.bean.BleMessage.BLE_LINK_RESULT_ERROR;
import static fotile.ble.bean.BleMessage.BLE_MSG_CLOSE;
import static fotile.ble.bean.BleMessage.BLE_MSG_LINK;
import static fotile.ble.bean.BleMessage.BLE_MSG_SEARCH_START;
import static fotile.ble.bean.BleMessage.BLE_MSG_SEARCH_STOP;
import static fotile.ble.bean.BleMessage.BLE_MSG_WRITE;
import static fotile.ble.observer.link.ILinkObserverable.STATE_CONNECTED;
import static fotile.ble.observer.link.ILinkObserverable.STATE_CONNECTING;
import static fotile.ble.observer.link.ILinkObserverable.STATE_ERROR;
import static fotile.ble.observer.link.ILinkObserverable.STATE_NONE;
import static fotile.ble.observer.search.SearchObserverable.SEARCH_TIME_NORMAL;
import static fotile.ble.util.BleConstant.BEAT_DATA;

/**
 * 项目名称：BleClient
 * 创建时间：2018/9/28 13:56
 * 文件作者：yaohx
 * 功能描述：Server开启后只做一些初始化操作，不做搜索和连接动作
 * 1、蓝牙搜索逻辑由Server执行，在onDeviceSearch中
 * 2、搜索开始后，发现上一次连接过的蓝牙，开启自动连接
 * 3、处理蓝牙断电 breakOffListener
 * 4、处理由view发起的EventBus事件，蓝牙搜索\连接\断开
 * 5、LinkAction 处理连接回调后的一些数据保存和重连机制
 * 6、该Server类和引用项目中的Server应该一致
 */
public class BleServer extends Service {
    /**
     * 服务中只有一个搜索对象
     */
    private SearchObserverable bleSearchObserverable;

    private Action1<BleDevice> actionSearch;
    /**
     * 蓝牙通道读取数据回调
     */
    private Action1<BleDevice> actionRead;
    /**
     * 读取蓝牙连接状态
     */
    private LinkAction linkAction = new LinkAction();

    /**
     * 延迟获取deviceId
     */
    private final int WHAT_DELAY_DEVICE_ID = 1001;
    /**
     * 连接蓝牙
     */
    private final int WHAT_LINK_TOOTH = 1002;
    /**
     * 恢复出厂后的搜索蓝牙
     */
    private final int WHAT_FIRST_START = 1003;
    private final int WHAT_FIRST_CLOSE = 1004;
    private final int WHAT_FIRST_RESTART = 1005;
    /**
     * 延迟连接时间short
     */
    private final int DELAY_LINK_TIME_SHORT = 800;
    /**
     * 延迟连接时间long
     */
    private final int DELAY_LINK_TIME_LONG = 1200;

    /**
     * 服务开启时，第一次连接失败，重连次数
     */
    private int error_retry_link = 0;
    /**
     * 是否自动重连到上一次连接的设备
     */
    private boolean autoLinkLast = true;
//    /**
//     * 蓝牙连接失败dialog
//     */
//    private ErrorBleDialog errorBleDialog;

    @Override
    public void onCreate() {
        super.onCreate();
        createSearchAction();
        createReadAction();
        initData();
    }

    private void initData() {
        EventBus.getDefault().register(this);
        bleSearchObserverable = SearchObserverable.getInstance(this);
        bleSearchObserverable.setOnDeviceBreakOffListener(breakOffListener);
        //注册action
        bleSearchObserverable.addSearchObserver(actionSearch);

    }

    /**
     * 监听蓝牙设备连接断开--蓝牙断电或者灶具断电
     */
    private SearchObserverable.OnDeviceBreakOffListener breakOffListener = new SearchObserverable
            .OnDeviceBreakOffListener() {
        @Override
        public void onDeviceBreakOff(BleDevice bleDevice) { //断电的蓝牙模块
            if (null != bleDevice) {
                ILinkObserverable linkObserverable = LinkFactory.getLinkObserverable(bleDevice, BleServer.this);
                if (null != linkObserverable) {
                    int state = linkObserverable.getState();
                    //灶具断电，本地连接状态没有更新，所以还是连接状态
                    if (state == STATE_CONNECTED) {
                        //当前连接的mac地址
                        String linkDeviceMac = linkObserverable.getLinkDevicedMac();
                        //断开设备的mac地址
                        String breakDeviceMac = bleDevice.getAddress();

                        //如果断开的设备的mac和本地暂时连接的mac一致，说明断开的设备是正在连接的设备
                        if (breakDeviceMac.equals(linkDeviceMac)) {
                            LogUtil.LOG_TOOTH("蓝牙设备连接断开-BleServer", "蓝牙断电或者灶具断电");
                            LinkFactory.disConnectAllDevice();
//                            //关闭蓝牙连接，目的是刷新listadapter
//                            linkObserverable.disConnect();
                            //发起重连
                            linkObserverable.retryConnect();
//                            //灶具断电或者恢复出厂，告诉app灶具在线状态
//                            if (bleDevice.getDeviceProject() == DEVICE_PROJECT_JAZ1) {
//                                //告诉appJAZ1灶具在线状态
//                                DeviceControl.getInstance().setStoveJaz1LinkState(false);
//                            }
//                            //结束录制菜谱页面
//                            EventBus.getDefault().post(new FinishActivityMessage(RecipesRecordActivity.class));
                            //关闭烟灶联动
                            SmokeStoveLinkAction.getInstance().closeStoveLink("true");
                        }
                    }
                }
            }
        }
    };

    /**
     * 蓝牙搜索回调
     */
    public void createSearchAction() {
        actionSearch = new Action1<BleDevice>() {
            @Override
            public void call(BleDevice bleDevice) {
//                //搜索中
//                if (bleDevice.searchStatus == SEARCHING) {
//                    BluetoothDevice bluetoothDevice = bleDevice.bluetoothDevice;
//                    if (null == bluetoothDevice) {
//                        return;
//                    }
//                    //更新状态栏设备联动icon,在这里设置会报空
//                    //DockStateBarView.getInstance(BleServer.this).setDeviceLinkTip(null);
//                    //如果当前为非连接状态-自动连接到一个已经连接过的设备，该设备未非连接状态
//                    String lastLinkName = (String) PreferenceUtil.getPreferenceValue(BleServer.this, PreferenceUtil.BLUE_LINK_DEVICE_NAME, "");
//                    String lastLinkMac = (String) PreferenceUtil.getPreferenceValue(BleServer.this, PreferenceUtil.BLUE_LINK_DEVICE_ADDRESS, "");
//                    String name = bleDevice.getName();
//                    String address = bleDevice.getAddress();
//
//                    if (autoLinkLast) {
//                        //如果搜索到最近一次连接到的设备
//                        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(address)) {
//                            if (name.equals(lastLinkName) && address.equals(lastLinkMac)) {
//                                //开机自连标记为非手动连接
//                                connectDevice(bleDevice, DELAY_LINK_TIME_SHORT,false);
//                            }
//                        }
//                    }
//                }
//                //结束搜索
//                if (bleDevice.searchStatus == SEARCHOVER) {
//
//                }
            }
        };
    }

    // Link 回调Action1,一个连接对象可以有多个回调（BleServer或者UI界面中均可注册回调）
    // 该LinkAction用于处理连接回调逻辑
    class LinkAction implements Action1<BleDevice> {
        @Override
        public void call(BleDevice bleDevice) {

//            ILinkObserverable linkObserverable = LinkFactory.getLinkObserverable(bleDevice, BleServer.this);
//            //每一次有连接结果，均取消上一次的心跳
//            linkObserverable.cancelBeatTimer();
//            int state = bleDevice.linkStatus;
//            LogUtil.LOG_TOOTH("回调蓝牙连接状态-BleServer", bleDevice.linkStatus);
////            //更新顶栏icon
////            TopBar.getInstance(BleServer.this).updateLinkState(true);
//
//            //连接中
//            if (state == STATE_CONNECTING) {
//
//            }
//            //连接成功
//            if (state == STATE_CONNECTED) {
//                //连接成功后，将error_retry_link置零
//                error_retry_link = 0;
//
////                if (errorBleDialog != null && errorBleDialog.isShowing()) {
////                    errorBleDialog.cancel();
////                }
//                //连接成功之后就取消重连定时器-该重连定时器在 breakOffListener 中
//                linkObserverable.cancelRetryTimer();
//                //重置连接次数
//                linkObserverable.resetLinkCount();
//
//                LogUtil.LOG_TOOTH("蓝牙连接成功-BleServer", bleDevice.getName());
//                //保存连接成功的设备名称
//                PreferenceUtil.setPreferenceValue(BleServer.this, PreferenceUtil.BLUE_LINK_DEVICE_NAME, bleDevice.getName());
//                PreferenceUtil.setPreferenceValue(BleServer.this, PreferenceUtil.BLUE_LINK_DEVICE_ADDRESS, bleDevice.getAddress());
//                //连接成功之后向灶具发送心跳包
//                linkObserverable.startBeatTimer(BEAT_DATA);
//
//                //JAZ1业务逻辑
////                if (bleDevice.getDeviceProject() == DEVICE_PROJECT_JAZ1) {
////                    //告诉appJAZ1灶具在线状态
////                    DeviceControl.getInstance().setStoveJaz1LinkState(true);
////                }
//                //c2业务逻辑
////                if (bleDevice.getDeviceProject() == DEVICE_PROJECT_C2I) {
////                    //连接成功获取灶具deviceId
////                    Message message = new Message();
////                    message.obj = bleDevice;
////                    message.what = WHAT_DELAY_DEVICE_ID;
////                    handler_delay.sendMessageDelayed(message, 1500);
////                }
//                //连接成功后，取消搜索，高丹提出的需求
////                if (null != bleSearchObserverable) {
////                    //用户关闭设备联动开关，关闭搜索
////                    bleSearchObserverable.stopBleSearch(true);
////                }
//            }
//            //连接手动关闭-或者关闭灶具蓝牙
//            if (state == STATE_NONE) {
//                //JAZ1业务逻辑
//                if (bleDevice.getDeviceProject() == DEVICE_PROJECT_JAZ1) {
////                    //告诉appJAZ1灶具在线状态
////                    DeviceControl.getInstance().setStoveJaz1LinkState(false);
//                }
////                //c2业务逻辑
////                if (bleDevice.getDeviceProject() == DEVICE_PROJECT_C2I) {
////                    //联动关闭，关闭菜谱选择界面
////                    EventBus.getDefault().post(new FinishActivityMessage(RecipesStoveSelectDialog.class));
////                    //联动关闭，关闭菜谱播放界面
////                    EventBus.getDefault().post(new FinishActivityMessage(RecipesPlayActivity.class));
////                    //联动关闭，告诉菜谱详情界面弹出提示框
////                    EventBus.getDefault().post(new BleMessage(RecipesDetailHomePageActivity.class,BLE_LINK_RESULT_CLOSE));
////                }
//            }
//            //连接失败
//            if (state == STATE_ERROR) {
//                LogUtil.LOG_TOOTH("蓝牙连接失败-BleServer", bleDevice.getName());
//                //JAZ1业务逻辑
////                if (bleDevice.getDeviceProject() == DEVICE_PROJECT_JAZ1) {
////                    //告诉appJAZ1灶具在线状态
////                    DeviceControl.getInstance().setStoveJaz1LinkState(false);
//                    //连接失败后重连
//                    if (error_retry_link <= 2 && error_retry_link >= 0) {
//                        //连接失败后，重连一次
//                        //连接失败重连时标记为非手动连接
//                        connectDevice(bleDevice, DELAY_LINK_TIME_LONG, false);
//                        error_retry_link++;
//                        LogUtil.LOG_TOOTH("蓝牙连接失败-BleServer-error_retry_link","重连次数:" + error_retry_link);
//                    }
////                }
////                //c2业务逻辑
////                if (bleDevice.getDeviceProject() == DEVICE_PROJECT_C2I) {
////                }
//            }
        }
    }

    //蓝牙连接失败（指令来自ILinkObserverable）
    @Subscribe
    public void onEventBleLinkError(BleMessage message) {
        if (message.to_class.getSimpleName().contains("BleServer")) {
            int command = message.blueToothType;
            if (command == BLE_LINK_RESULT_ERROR) {
                showLinkErrorDialog(message.bleDevice);
            }
        }
    }

    /**
     * 蓝牙连接失败提示dialog
     */
    private synchronized void showLinkErrorDialog(final BleDevice bleDevice) {
//        LogUtil.LOG_TOOTH("showLinkErrorDialog",bleDevice.link_count);
//        if (null == errorBleDialog) {
//            errorBleDialog = new ErrorBleDialog(BleServer.this);
//        }
//        if (!errorBleDialog.isShowing()) {
//            //手动连接的才提示
//            if(bleDevice.active){
//                errorBleDialog.show();
//            }
//        }
    }

    public void createReadAction() {
        actionRead = new Action1<BleDevice>() {
            @Override
            public void call(BleDevice bleDevice) {
                if (null != bleDevice && null != bleDevice.data_read) {
//                    StoveAction.getInstance().stoveAction(bleDevice);
                }
            }
        };
    }

    /**
     * 开启一个新的连接
     *
     * @param bleDevice
     * @param delay_link_time 延迟发起连接的时间
     * @param active 表示是否是用户主动连接设备
     */
    private synchronized void connectDevice(final BleDevice bleDevice, int delay_link_time, boolean active) {
        if (null != bleDevice) {
            bleDevice.active = active;
            ILinkObserverable linkObserverable = LinkFactory.getLinkObserverable(bleDevice, BleServer.this);
            linkObserverable.addLinkObserver(linkAction);
            linkObserverable.setReadObserver(actionRead);
            //如果正在连接中，return
            int link_state = linkObserverable.getState();
            LogUtil.LOG_TOOTH("开启新的连接蓝牙连接状态-BleServer", link_state );
            if (link_state == STATE_CONNECTING || link_state == STATE_CONNECTED) {
                return;
            }

//            //关闭该设备原有的连接
//            linkObserverable.disConnect();
            Message message = new Message();
            message.obj = bleDevice;
            message.what = WHAT_LINK_TOOTH;
            handler_delay.sendMessageDelayed(message, delay_link_time);
        }
    }

    Handler handler_delay = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //延迟获取deviceId
//                case WHAT_DELAY_DEVICE_ID:
//                    StoveControl.getStoveDeviceId();
//                    break;
                //恢复出厂之后开启
                case WHAT_FIRST_START:
                    if (null != bleSearchObserverable) {
                        bleSearchObserverable.startBleSearch(SEARCH_TIME_NORMAL);
                    }
                    break;
                //恢复出厂之后关闭
                case WHAT_FIRST_CLOSE:
                    if (null != bleSearchObserverable) {
                        //假的效果，用户感知不到
                        bleSearchObserverable.stopBleSearch(false);
                    }
                    break;
                //恢复出厂之后开启
                case WHAT_FIRST_RESTART:
                    //上一次保存的开关状态
                    boolean device_link_smoke_stove = PreferenceUtil.getDeviceLinkSwitch(BleServer.this);
                    //如果在这其间用户没有主动关闭
                    if (null != bleSearchObserverable && device_link_smoke_stove) {
                        bleSearchObserverable.startBleSearch(SEARCH_TIME_NORMAL);
                    }
                    break;
                //连接蓝牙
                case WHAT_LINK_TOOTH:
                    BleDevice bleDevice = (BleDevice) msg.obj;
                    ILinkObserverable linkObserverable = LinkFactory.getLinkObserverable(bleDevice, BleServer.this);
                    linkObserverable.connect(bleDevice.active);
                    break;
            }
            return false;
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeAllLink();
    }

    /**
     * 关闭所有蓝牙连接
     */
    private void closeAllLink() {
        //关闭所有蓝牙连接
        LinkFactory.disConnectAllDevice();
        //取消Message
        handler_delay.removeCallbacksAndMessages(null);
    }

    /**
     * 开始蓝牙搜索
     */
    private void startSearch() {

//        boolean first_open = (boolean) PreferenceUtil.getPreferenceValue(this, PreferenceUtil.FIRST_OPEN_STOVE_LINK,
//                true);
//        //解决恢复出厂后，第一次搜索的蓝牙没有名称的bug
//        if (first_open) {
//            //先搜索
//            handler_delay.sendEmptyMessage(WHAT_FIRST_START);
//            //搜索10秒后，关闭
//            handler_delay.sendEmptyMessageDelayed(WHAT_FIRST_CLOSE, 10000);
//            //再暂停5秒，开始搜索（恢复出厂后，会在设备联动引导页面开始搜索）
//            handler_delay.sendEmptyMessageDelayed(WHAT_FIRST_RESTART, 10000 + 5000);
//
//            PreferenceUtil.setPreferenceValue(this, PreferenceUtil.FIRST_OPEN_STOVE_LINK, false);
//        }
//        //正常流程（第二次开机后，开始搜索蓝牙，搜索到已经连接的设备后会自动重连）
//        else {
            //开启蓝牙搜索
            bleSearchObserverable.startBleSearch(SEARCH_TIME_NORMAL);
//        }
    }

    /***********************************蓝牙搜索、连接、断开逻辑均在BleServer中处理↓***********************************/

    //停止蓝牙搜索
    @Subscribe
    public void onDeviceSearchStop(BleMessage bleMessage) {
        if (bleMessage.to_class.getSimpleName().contains("BleServer")) {
            if (bleMessage.blueToothType == BLE_MSG_SEARCH_STOP) {
                if (null != bleSearchObserverable) {
                    //移除搜索相关的handler，因为停止搜索时，可能handler还没有执行完毕
                    handler_delay.removeMessages(WHAT_FIRST_START);
                    handler_delay.removeMessages(WHAT_FIRST_CLOSE);
                    handler_delay.removeMessages(WHAT_FIRST_RESTART);
                    //用户关闭设备联动开关，关闭搜索
                    bleSearchObserverable.stopBleSearch(true);
                }
            }
        }
    }

    //开始蓝牙搜索
    @Subscribe
    public void onDeviceSearchStart(BleMessage bleMessage) {
        if (bleMessage.to_class.getSimpleName().contains("BleServer")) {
            if (bleMessage.blueToothType == BLE_MSG_SEARCH_START) {
                startSearch();
            }
        }
    }

    //开始连接某一个蓝牙
    @Subscribe
    public void onDeviceLink(BleMessage bleMessage) {
        if (bleMessage.to_class.getSimpleName().contains("BleServer")) {
            if (bleMessage.blueToothType == BLE_MSG_LINK) {
                if (null != bleMessage.bleDevice) {
                    //开始连接时，停止蓝牙搜索，为了节省资源
                    if (null != bleSearchObserverable) {
                        bleSearchObserverable.stopBleSearch(false);
                    }
                    //发起连接时，关闭所有的之前连接。如果选择不关闭，则可以一次连接多个蓝牙
                    closeAllLink();

                    connectDevice(bleMessage.bleDevice, DELAY_LINK_TIME_SHORT,true);
                    //重连次数置为 0
                    error_retry_link = 0;
                }
            }
        }
    }

    //写入数据
    @Subscribe
    public void onDeviceWrite(BleMessage bleMessage) {
        if (bleMessage.to_class.getSimpleName().contains("BleServer")) {
            if (bleMessage.blueToothType == BLE_MSG_WRITE) {
                BleDevice bleDevice = bleMessage.bleDevice;
                ILinkObserverable linkObserverable = LinkFactory.getLinkObserverable(bleDevice, BleServer.this);
                linkObserverable.write(bleDevice.data_write);
            }
        }
    }

    // 关闭某一个蓝牙连接
    @Subscribe
    public void onDeviceClose(BleMessage bleMessage) {
        if (bleMessage.to_class.getSimpleName().contains("BleServer")) {
            if (bleMessage.blueToothType == BLE_MSG_CLOSE) {
                ILinkObserverable linkObserverable = LinkFactory.getLinkObserverable(bleMessage.bleDevice, BleServer
                        .this);
                if (null != linkObserverable) {
                    linkObserverable.disConnect();
                    LogUtil.LOG_TOOTH("断开连接111", bleMessage );
                }
               LinkFactory.disConnectAllDevice();
//                //取消心跳包导致的30秒延时
//                StoveAction.getInstance().removeRetryWhat();
                //取消Message
                handler_delay.removeCallbacksAndMessages(null);
                //重连次数置为 0
                error_retry_link = 0;
            }
        }
    }
    /***********************************蓝牙搜索、连接、断开逻辑均在BleServer中处理↑***********************************/
}
