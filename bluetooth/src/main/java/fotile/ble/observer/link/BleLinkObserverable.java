package fotile.ble.observer.link;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;


import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fotile.ble.bean.BleDevice;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static fotile.ble.util.BleConstant.BEAT_DATA;
import static fotile.ble.util.BleConstant.UUID_CHARAC_READ;
import static fotile.ble.util.BleConstant.UUID_CHARAC_WRITE;
import static fotile.ble.util.BleConstant.UUID_SERVICE;

/**
 * 项目名称：BleClient
 * 创建时间：2018/9/4 15:18
 * 文件作者：yaohx
 * 功能描述：
 * ble连接处理类
 * 一个Client【中心设备】可以连接多个Server Ble【外围设备】
 * 一个ble外围设备只对应一个该类对象
 */
class BleLinkObserverable extends ILinkObserverable {

    /**
     * 当前设备连接状态
     */
    private int linkStatus;

    private BleDevice bleDevice;
    /**
     * 管理ble连接
     */
    private BluetoothGatt bluetoothGatt;
    /**
     * 写入数据使用到的ble特征
     */
    private BluetoothGattCharacteristic gattCharacteristicWrite;

    private BluetoothGattCharacteristic gattCharacteristicRead;

    BluetoothGattService linkLossService;
    private Context context;

//    private static BleLinkObserverable bleLinkObserverable;

    public BleLinkObserverable(BleDevice bleDevice, Context context) {
        this.bleDevice = bleDevice;
        this.context = context;
    }
//
//    public static BleLinkObserverable getInstance() {
//        if (null == bleLinkObserverable) {
//            bleLinkObserverable = new BleLinkObserverable();
//        }
//        return bleLinkObserverable;
//    }

    private boolean isSendConnectTag;

    /**
     * 灶具连接配对超时
     */
    private int WHAT_STOVE_TIME_OUT = 1001;
    /**
     * discover ble模块
     */
    private int WHAT_STOVE_DIS_COVER = 1002;
    private int WHAT_DELAY_DISCONNECT = 1003;

    /**
     * 间隔发送提醒灶具上报数据的时间间隔
     */
    private final int TIME_LOOP = 1000;
    /**
     * ble模块配对超时时间
     */
    private final int ble_time_out = 32 * 1000;
    /**
     * 心跳包
     */
    byte[] beat_data = {(byte) 0xf4, (byte) 0xf5, 0x00, 0x08, 0x05, 0x00, 0x02, 0x01, 0x00, 0x00, 0x00, 0x0f};

    Timer timer;

    private long last_discover_time = 0;
    /**
     * 设置当前的连接状态
     *
     * @param state
     */
    public void setState(int state) {
        linkStatus = state;
        bleDevice.linkStatus = state;
    }

    public synchronized int getState() {
        return linkStatus;
    }

    @Override
    public BleDevice getLinkDevice() {
        return bleDevice;
    }

    public String getLinkDevicedMac() {
        return linkStatus == STATE_CONNECTED && null != bleDevice ? bleDevice.getAddress() : "";
    }

    @Override
    public synchronized void connect(boolean active) {
        if (null != bleDevice) {
            //防止延时disConnect影响到连接操作
            handler.removeMessages(WHAT_DELAY_DISCONNECT);

            bleDevice.active = active;
            //连接次数++
            link_count++;
            cancelLoopTimer();
            isSendConnectTag = false;

            //断开上一次的连接
            if (null != bluetoothGatt) {
                //断开ble连接
                bluetoothGatt.disconnect();
                //关闭中央设备 （不用时及时关闭，否则有的手机重连连不上）
                bluetoothGatt.close();
                bluetoothGatt = null;

                gattCharacteristicRead = null;
                gattCharacteristicWrite = null;
            }

            //将状态通知ui
            setState(STATE_CONNECTING);
            notifyLinkData(bleDevice);

            BluetoothDevice bluetoothDevice = bleDevice.bluetoothDevice;
            if (null != bluetoothDevice) {
                bluetoothGatt = bluetoothDevice.connectGatt(context, true, gattCallback);
                String name = bleDevice.getName();
                String mac = bleDevice.getAddress();
                LogUtil.LOG_TOOTH("-------------------------------------分割线","-------------------------------------");
                LogUtil.LOG_TOOTH("BLE连接打开", name + ":" + mac);
                //发起连接时，停止搜索，节省资源消耗
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            }
        }
    }

    /**
     * 灶具断电后的重连机制
     */
    @Override
    public void retryConnect() {
        //重连一次，保证有一个可连接的bluetoothGatt对象
        //重连时，标记位非手动连接
        connect(false);
        LogUtil.LOG_TOOTH("BLE重连并等待retryConnect", link_count);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //灶具超时
            if (msg.what == WHAT_STOVE_TIME_OUT) {
                if (linkStatus != STATE_CONNECTED) {
                    LogUtil.LOG_TOOTH("蓝牙BLE连接超时", "执行disConnect");
                    disConnectLogic();

                    //超时之后发送一个error状态
                    setState(STATE_ERROR);
                    notifyLinkData(bleDevice);
                }
            }
            //discover ble模块
            if(msg.what == WHAT_STOVE_DIS_COVER){
                if(null != bluetoothGatt){
                    //连接成功，查询ble设备提供的服务
                    bluetoothGatt.discoverServices();
                }
            }
            //延时disConnect
            if(msg.what == WHAT_DELAY_DISCONNECT){
                disConnectGatt(true);
            }
            return false;
        }
    });

    private void startLoopTimer() {
//        cancelLoopTimer();
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                //写入discover后，写入一条心跳包数据，目的是让ble模组主动上报一条数据，上报数据后监听，更新连接状态
//                write(beat_data);
//                LogUtil.LOG_TOOTH("ble蓝牙Discovered发送一条心跳包数据", Tool.getHexBinString(beat_data));
//            }
//        }, 1000, TIME_LOOP);
    }

    private void cancelLoopTimer() {
//        if (null != timer) {
//            timer.cancel();
//            timer.purge();
//            timer = null;
//        }
    }

    public synchronized void disConnect() {
        super.disConnect();
        cancelLoopTimer();
        //移除灶具连接配对超时message
        handler.removeMessages(WHAT_STOVE_TIME_OUT);
        //主动断开，将本地状态为重设
        setState(STATE_NONE);
        //考虑到有时候断开不会进入onConnectionStateChange回调函数，在这里设置状态更新
        notifyLinkData(bleDevice);

        //先让异步执行的UI更新完成，再去disConnect，因为当ble设备断开后获取的name为空
        handler.sendEmptyMessageDelayed(WHAT_DELAY_DISCONNECT,300);
    }

    /**
     * 关闭连接（排除ui更新），当灶具超时时调用
     */
    private synchronized void disConnectLogic() {
        super.disConnect();
        cancelLoopTimer();
        //移除灶具连接配对超时message
        handler.removeMessages(WHAT_STOVE_TIME_OUT);
        disConnectGatt(false);
    }

    /**
     * 关闭Gatt协议
     */
    private void disConnectGatt(boolean log){
        if (null != bluetoothGatt) {
            //断开ble连接
            bluetoothGatt.disconnect();
            //关闭中央设备 （不用时及时关闭，否则有的手机重连连不上）
            bluetoothGatt.close();
            bluetoothGatt = null;

            gattCharacteristicRead = null;
            gattCharacteristicWrite = null;

            if(log){
                LogUtil.LOG_TOOTH("蓝牙BLE", "disConnect:" + bleDevice.getName());
            }
        }
    }

    /**
     * 写入数据
     *
     * @param data
     */
    @Override
    public synchronized void write(byte[] data) {
        if (null != data && (linkStatus == STATE_CONNECTED || linkStatus == STATE_CONNECTING)) {
            if (null != gattCharacteristicWrite && null != bluetoothGatt) {
                //设置该特征具有Notification功能
                bluetoothGatt.setCharacteristicNotification(gattCharacteristicWrite, true);
                //将指令放置进特征中
                gattCharacteristicWrite.setValue(data);
                //设置回复形式
                gattCharacteristicWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                //开始写数据
                bluetoothGatt.writeCharacteristic(gattCharacteristicWrite);
            }
        }
    }

    @Override
    public void read() {
        bluetoothGatt.readCharacteristic(gattCharacteristicRead);//读取
    }

    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {//发现服务，在蓝牙连接的时候会调用
            List<BluetoothGattService> list = gatt.getServices();
            for (BluetoothGattService service : list) {
                //service的uuid
                String service_uuid = service.getUuid().toString();
                LogUtil.LOG_TOOTH("[Service uuid", service_uuid + "]");

                if (UUID_SERVICE.equals(service_uuid)) {
                    //一个服务中有多个BluetoothGattCharacteristic
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    int index = characteristics.size();
                    for (int i = 0; i < index; i++) {
                        BluetoothGattCharacteristic characteristic = characteristics.get(i);
                        //characteristic的uuid
                        String chara_uuid = characteristic.getUuid().toString();
                        LogUtil.LOG_TOOTH((i + 1) + "、characteristic uuid", chara_uuid);

                        //读取
                        if (UUID_CHARAC_READ.equals(chara_uuid)) {
                            //找出需要通知改变的特性
                            gattCharacteristicRead = characteristic;
                            boolean result = gatt.setCharacteristicNotification(gattCharacteristicRead, true);
                            //启用ble周边的特征通知
                            if (result) {
                                List<BluetoothGattDescriptor> descriptorList = gattCharacteristicRead.getDescriptors();
                                if (descriptorList != null && descriptorList.size() > 0) {
                                    for (BluetoothGattDescriptor descriptor : descriptorList) {
                                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                        gatt.writeDescriptor(descriptor);
                                        LogUtil.LOG_TOOTH("  读取descriptor的uuid",descriptor.getUuid().toString());
                                    }
                                }
                            }
                        }
                        //写入
                        if (UUID_CHARAC_WRITE.equals(chara_uuid)) {
                            //                        linkLossService = service;
                            //找出需要通知改变的特性
                            gattCharacteristicWrite = characteristic;
                            //启动通知，ble 应用程序通常在设备上的特定特性发生变化时要求收到通知
                            //一旦为特性启用通知，如果远程设备上的特性发生变化，则触发回调onCharacteristicChanged()
                            boolean result = gatt.setCharacteristicNotification(gattCharacteristicWrite, true);
                            if (result) {
                                List<BluetoothGattDescriptor> descriptorList = gattCharacteristicWrite.getDescriptors();
                                if (descriptorList != null && descriptorList.size() > 0) {
                                    for (BluetoothGattDescriptor descriptor : descriptorList) {
                                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                        gatt.writeDescriptor(descriptor);
                                        LogUtil.LOG_TOOTH("  写入descriptor的uuid",descriptor.getUuid().toString());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //连接状态改变的回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String deviceName = bleDevice.getName();
            switch (newState) {
                //连接已断开 （如果ble模块超过30秒还未配对会主动断开连接回调 STATE_DISCONNECTED）
                case BluetoothProfile.STATE_DISCONNECTED:
                    LogUtil.LOG_TOOTH("onConnectionStateChange", "BLE连接关闭:" + deviceName);
                    break;
                //正在连接中
                case BluetoothProfile.STATE_CONNECTING:
                    LogUtil.LOG_TOOTH("onConnectionStateChange", "STATE_CONNECTING");
                    break;
                //连接成功
                case BluetoothProfile.STATE_CONNECTED:
                    long current_time = System.currentTimeMillis();
                    if(current_time - last_discover_time > 1000){
                        LogUtil.LOG_TOOTH("onConnectionStateChange", "BLE连接成功:"+ deviceName);
                        //解决有时候不会回调onServicesDiscovered的问题
                        handler.sendEmptyMessageDelayed(WHAT_STOVE_DIS_COVER,600);
                        // ble模组不支持配对，暂停使用指令交互方式确认连接成功
                        //这里做一个假的 STATE_CONNECTING
                        setState(STATE_CONNECTING);

                        //移除上一个的超时message
                        handler.removeMessages(WHAT_STOVE_TIME_OUT);
                        handler.sendEmptyMessageDelayed(WHAT_STOVE_TIME_OUT, ble_time_out);

                        startLoopTimer();
                        last_discover_time = current_time;
                    }
                    break;
                //连接断开中
                case BluetoothProfile.STATE_DISCONNECTING:
                    //将状态通知ui
                    setState(STATE_NONE);
                    break;
            }
        }

        //写操作的回调
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String deviceName = bleDevice.getName();
            }
        }

        //读操作的回调
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String deviceName = bleDevice.getName();
                byte[] data = characteristic.getValue();
                bleDevice.data_read = data;
                //将数据分发
                notifyReadData(bleDevice);
            }
        }

        //数据有改变操作的回调
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (null != characteristic) {
                String deviceName = bleDevice.getName();
                byte[] data = characteristic.getValue();
                bleDevice.data_read = data;
                //将数据分发
                notifyReadData(bleDevice);
            }
        }

        //蓝牙设备BluetoothGatt writeDescriptor 成功的回调
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            //enable成功后表示可以数据通信
            //A GATT operation completed successfully
            if(status == BluetoothGatt.GATT_SUCCESS){
//                //此处不能移除，因为用户不按b键会发生超时
//                handler.removeMessages(WHAT_STOVE_TIME_OUT);
                String uuid = descriptor.getUuid().toString();
                LogUtil.LOG_TOOTH("Descriptor enable成功的回调","uuid：" + uuid +" status：" + status);

                //发送一条心跳包，校验数据通信是否建立
                LogUtil.LOG_TOOTH("BLE发送一条心跳包，校验数据通信是否建立", Tool.getHexBinString(BEAT_DATA));
                write(BEAT_DATA);
            }
        }
    };

    /************************************************************************/
    @Override
    public void addLinkObserver(Action1<BleDevice> iAction) {
        if (!list_link.contains(iAction) && null != iAction) {
            list_link.add(iAction);
        }
    }

    @Override
    public void removeLinkObserver(Action1<BleDevice> action1) {
        if (null != list_link && null != action1) {
            list_link.remove(action1);
        }
    }

    @Override
    public synchronized void notifyLinkData(BleDevice bleDevice) {
        super.notifyLinkData(bleDevice);
        if (null != list_link && null != bleDevice) {
            Observable<BleDevice> observable = Observable.just(bleDevice);

            for (Action1<BleDevice> action : list_link) {
                observable.subscribeOn(Schedulers.immediate())//指定被观察者的执行线程（立即在当前线程执行指定的工作）
                        .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                        .subscribe(action);
            }
        }
    }

    //上报读取到的数据
    @Override
    public synchronized void notifyReadData(BleDevice bleDevice) {
        if (null != action_read && null != bleDevice) {
            if (null != bleDevice.data_read && bleDevice.data_read.length > 8) {
                //收到第一条指令时告诉UI连接成功
                if (!isSendConnectTag) {
                    isSendConnectTag = true;
                    setState(STATE_CONNECTED);
                    notifyLinkData(bleDevice);
                    LogUtil.LOG_TOOTH("BLE模块的连接成功指令", Tool.getHexBinString(bleDevice.data_read, false));
                    cancelLoopTimer();
                }

//                ble模组不支持配对，暂停使用指令交互方式确认连接成功
//                f4 f5 00 08 05 00 73 01 00 00 00 7e 该指令标志位连接成功
//                int index0 = bleDevice.data_read[0];
//                int index1 = bleDevice.data_read[1];
//                int cmd = bleDevice.data_read[6];
//                int stat = bleDevice.data_read[7];
//                if ((index0 & 0xff) == 0xf4 && (index1 & 0xff) == 0xf5) {
//                    if ((cmd & 0xFF) == 0x73 && (stat & 0xFF) == 0x01) {
//                        LogUtil.LOG_TOOTH("BLE模块的连接成功指令", Tool.getHexBinString(bleDevice.data_read, false));
//                        setState(STATE_CONNECTED);
//                        notifyLinkData(bleDevice);
//                    }
//                }

                Observable<BleDevice> observable = Observable.just(bleDevice);

                observable.subscribeOn(Schedulers.immediate())//指定被观察者的执行线程（立即在当前线程执行指定的工作）
                        .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                        .subscribe(action_read);
            }
        }
    }
}
