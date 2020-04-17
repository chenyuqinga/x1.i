package fotile.ble.observer.search;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;


import com.fotile.common.util.log.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

import fotile.ble.bean.BleDevice;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static fotile.ble.observer.link.ILinkObserverable.STATE_CONNECTED;
import static fotile.ble.observer.link.ILinkObserverable.STATE_CONNECTING;
import static fotile.ble.util.BleConstant.DEVICE_NAME_C2I;
import static fotile.ble.bean.BleDevice.SEARCHING;
import static fotile.ble.bean.BleDevice.SEARCHOVER;
import static fotile.ble.observer.link.ILinkObserverable.STATE_NONE;
import static fotile.ble.util.BleConstant.DEVICE_NAME_JAZ1;

/**
 * 项目名称：BleClient
 * 创建时间：2018/9/3 17:39
 * 文件作者：yaohx
 * 功能描述：ble搜索处理类，该类只能有一个对象
 */
public class SearchObserverable implements ISearchObserverable {

    /**
     * 持续搜索蓝牙的时间 ms--正常流程
     * 安卓系统支持的搜索时间为1分钟左右，如果超出1分钟，会自动取消搜索
     */
    public static final long SEARCH_TIME_NORMAL = 1 * 60 * 1000;
//    /**
//     * 恢复出厂设置后的第一次搜索时间
//     */
//    public static final long SEARCH_TIME_FIRST = 2 * 60 * 1000;
    /**
     * 单例
     */
    private BluetoothAdapter bluetoothAdapter;

    private Context context;

    private final int WHAT_BLUE_SEARCH_START = 1;
    private final int WHAT_BLUE_SEARCH_CLOSE = 2;
    private long search_time;
    /**
     * 检测搜索，如果搜索结束并且还未到搜索时间，重启
     */
    private Timer timer_check = null;

    private static SearchObserverable bleSearchObserverable;

    private SearchObserverable(Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        registerReceiver();
    }

    public synchronized static SearchObserverable getInstance(Context context) {
        if (null == bleSearchObserverable) {
            bleSearchObserverable = new SearchObserverable(context);
        }
        return bleSearchObserverable;
    }

    /**
     * 开启蓝牙搜索,如果有正在执行的搜索，先取消
     * 该方法只能在Server中调用
     *
     * @param search_time 搜索时间 -ms
     */
    public void startBleSearch(long search_time) {
        this.search_time = search_time;
        //打开蓝牙开关
        LogUtil.LOG_TOOTH("蓝牙是否可用", bluetoothAdapter.isEnabled());

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        // 检查当前手机是否支持蓝牙
        boolean support_blue_tooth = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

        //设备支持蓝牙
        if (support_blue_tooth) {
            //支持蓝牙并且可用
            if (null != bluetoothAdapter && bluetoothAdapter.isEnabled()) {
                //如果蓝牙正在搜索，先取消
                if (bluetoothAdapter.isDiscovering()) {
                    stopBleSearch(false);
                }
                //开始搜索
                handler.sendEmptyMessageDelayed(WHAT_BLUE_SEARCH_START, 800);
                //SEARCH_TIME 秒后停止搜索
                handler.sendEmptyMessageDelayed(WHAT_BLUE_SEARCH_CLOSE, search_time);
                LogUtil.LOG_TOOTH("蓝牙搜索开始", "---------搜索时间（毫秒）：" + search_time);

                //恢复出厂或者首次开机时才搜索长时间
                if (search_time > SEARCH_TIME_NORMAL) {
                    startCheckTimer();
                }
            } else {
//                Toast.makeText(context, "请打开蓝牙", Toast.LENGTH_SHORT).show();
            }
        } else {
//            Toast.makeText(context, "您的设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 如果蓝牙搜索超过一分钟，系统会自动断开
     * 如果检测到断开了，重新打开搜索
     */
    private void startCheckTimer() {
        if (null == timer_check) {
            timer_check = new Timer();
        }
        timer_check.schedule(new TimerTask() {
            @Override
            public void run() {
                //如果搜索超过了一分钟，搜索会断开
                if (!bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.startDiscovery();
                }
            }
        }, 800, 5000);
    }

    private void cancelCheckTimer() {
        if (null != timer_check) {
            timer_check.cancel();
            timer_check.purge();
            timer_check = null;
        }
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //开始搜索
            if (msg.what == WHAT_BLUE_SEARCH_START) {
                bluetoothAdapter.startDiscovery();
//                bluetoothAdapter.startLeScan(leScanCallback);
            }
            if (msg.what == WHAT_BLUE_SEARCH_CLOSE) {
                //搜索时间到，通知ui
                stopBleSearch(true);
            }
            return false;
        }
    });

    /**
     * 注册蓝牙搜索广播
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        //发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //蓝牙设备状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //蓝牙设备断开
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        //蓝牙名称改变
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        //蓝牙设备已连接
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        context.registerReceiver(mBluetoothReceiver, filter);
    }

    /**
     * 蓝牙搜索广播
     */
    BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Handler handler = new Handler();
            final BluetoothDevice device1 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            final BleDevice bleDevice1 = new BleDevice(device1);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    LogUtil.LOG_TOOTH("蓝牙搜索广播---延时6200毫秒执行", device1.getName());
                    bleDevice1.linkStatus = STATE_CONNECTED;
                    notifySearchData(bleDevice1);
                }
            };
            //每扫描到一个设备，系统都会发送此广播或者本地蓝牙名称发生变化
            if (BluetoothDevice.ACTION_FOUND.equals(action) || BluetoothDevice.ACTION_NAME_CHANGED.equals(action)) {
                //获取蓝牙设备
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = bluetoothDevice.getName();
                LogUtil.LOG_TOOTH("蓝牙搜索广播---搜索到的蓝牙", deviceName);
                //搜索到新蓝牙通知ui
                notifySearchData(new BleDevice(bluetoothDevice, SEARCHING));
            }
            //蓝牙设备连接断开--蓝牙断电或者灶具断电（连接失败也会走这个广播）
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                handler.removeCallbacks(runnable);
                //获取蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BleDevice bleDevice = new BleDevice(device);
                bleDevice.linkStatus = STATE_NONE;
                notifySearchData(bleDevice);
                LogUtil.LOG_TOOTH("蓝牙搜索广播----", "ACTION_ACL_DISCONNECTED: "+ bleDevice.getName());
            }
            //蓝牙设备已连接
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //获取蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BleDevice bleDevice = new BleDevice(device);
                bleDevice.linkStatus = STATE_CONNECTED;
                notifySearchData(bleDevice);
                LogUtil.LOG_TOOTH("蓝牙搜索广播----", "ACTION_ACL_CONNECTED----" + bleDevice.getName());
            }
            //蓝牙设备正在配对中
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                LogUtil.LOG_TOOTH("蓝牙搜索广播----", "ACTION_BOND_STATE_CHANGED)" + intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1));
                if (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1) == BluetoothDevice.BOND_BONDING) {
                    //获取蓝牙设备
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    BleDevice bleDevice = new BleDevice(device);
                    bleDevice.linkStatus = STATE_CONNECTING;
                    notifySearchData(bleDevice);
                }
                if(intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1) == BluetoothDevice.BOND_BONDED){
                    handler.postDelayed(runnable,6200);
                }

            }
            //扫描结束
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                LogUtil.LOG_TOOTH("蓝牙搜索广播关闭", "---------");
            }
        }
    };

//    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
//        @Override
//        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//            String deviceName = device.getName();
//            //蓝牙名称过滤
//            if (filterDeviceName(deviceName)) {
//                LogUtil.LOG_TOOTH("搜索到的蓝牙", deviceName);
//                //搜索到新蓝牙通知ui
//                notifySearchData(new BleDevice(device, SEARCHING));
//            }
//        }
//    };

//    /**
//     * 过滤搜索到的设备名称
//     */
//    private boolean filterDeviceName(String deviceName) {
//        if (!TextUtils.isEmpty(deviceName)) {
//            if (deviceName.contains(DEVICE_NAME_C2I)) {
//                return true;
//            }
//            if (deviceName.contains(DEVICE_NAME_JAZ1)) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 停止搜索蓝牙设备并且解绑广播
     */
    public void stopBleSearch(boolean notifyUi) {
        //停止搜索
        if (null != bluetoothAdapter) {
            bluetoothAdapter.cancelDiscovery();
//            bluetoothAdapter.stopLeScan(leScanCallback);
            cancelCheckTimer();
        }

        //移除message
        handler.removeMessages(WHAT_BLUE_SEARCH_START);
        handler.removeMessages(WHAT_BLUE_SEARCH_CLOSE);

        //是否需要通知ui停止转圈
        if (notifyUi) {
            LogUtil.LOG_TOOTH("蓝牙搜索停止", "stopBleSearch");
            //取消搜索通知ui结束搜索视图
            notifySearchData(new BleDevice(SEARCHOVER));
        }
    }

    /**
     * 当前是否在搜索中
     * @return
     */
    public boolean isSearching(){
        boolean result = false;
        if(null != bluetoothAdapter){
            result = bluetoothAdapter.isDiscovering();
        }
        return result;
    }

    //灶具断电回调
    public interface OnDeviceBreakOffListener {
        abstract void onDeviceBreakOff(BleDevice bleDevice);
    }

    private OnDeviceBreakOffListener onDeviceBreakOffListener;

    public void setOnDeviceBreakOffListener(OnDeviceBreakOffListener onDeviceBreakOffListener) {
        this.onDeviceBreakOffListener = onDeviceBreakOffListener;
    }

    /**
     * 添加观察者
     *
     * @param iAction
     */
    @Override
    public void addSearchObserver(Action1<BleDevice> iAction) {
        if (!list_search.contains(iAction) && null != iAction) {
            list_search.add(iAction);
        }
    }

    /**
     * remove观察者
     */
    @Override
    public void removeSearchObserver(Action1<BleDevice> iAction) {
        if (null != list_search && null != iAction) {
            list_search.remove(iAction);
        }
    }

    @Override
    public void notifySearchData(final BleDevice bleDevice) {
        if (null != list_search && null != bleDevice) {
            Observable<BleDevice> observable = Observable.just(bleDevice);

            for (Action1 action : list_search) {
                observable.subscribeOn(Schedulers.immediate())//指定被观察者的执行线程（立即在当前线程执行指定的工作）
                        .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                        .subscribe(action);
            }
        }
    }

}
