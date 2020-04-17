package fotile.ble.observer.link;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;


import com.fotile.common.util.log.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import fotile.ble.util.BleConstant;
import fotile.ble.bean.BleDevice;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 文件名称：BTLinkObserverable
 * 创建时间：2017/11/23 12:34
 * 文件作者：yaohx
 * 功能描述：
 * BT连接处理类
 * 一个Client【中心设备】可以连接多个Server Ble【外围设备】
 * 一个BT外围设备只对应一个该类对象
 */
class BTLinkObserverable extends ILinkObserverable {

    /**
     * 手机和蓝牙适配器通信，必须使用这个UUID
     */
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * 蓝牙适配器
     */
    private final BluetoothAdapter bluetoothAdapter;
    /**
     * 负责连接设备的线程
     */
    private ConnectThread mConnectThread;
    /**
     * 当前设备连接状态
     */
    private int linkStatus;

    /**
     * 是否创建安全连接
     */
    private boolean secure;

    /**
     * 远程设备
     */
    private BleDevice bleDevice;
    /**
     * 用一个队列缓存需要传给蓝牙的数据
     */
    private Queue<byte[]> queue = new LinkedList<byte[]>();

//    private static BTLinkObserverable blueToothLinkObserverable;

//    public static BTLinkObserverable getInstance() {
//        if (null == blueToothLinkObserverable) {
//            blueToothLinkObserverable = new BTLinkObserverable(false);
//        }
//        return blueToothLinkObserverable;
//    }

    public BTLinkObserverable(BleDevice bleDevice, Context context) {
        this.bleDevice = bleDevice;

        this.secure = false;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setState(STATE_NONE);
    }

    /**
     * 设置当前的连接状态
     *
     * @param state
     */
    public void setState(int state) {
        linkStatus = state;
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


    /**
     * 开始连接一个远程设备，并且将上一次的连接关闭
     */
    @Override
    public void connect(boolean active) {
        if (null != bleDevice) {
            bleDevice.active = active;
            //连接次数++
            link_count++;
            //取消上一次正在连接的线程
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }

            // Start the thread to connect with the given device
            mConnectThread = new ConnectThread(bleDevice.bluetoothDevice);
            mConnectThread.start();
            setState(STATE_CONNECTING);
            uiHandler.sendEmptyMessage(STATE_CONNECTING);
        }
    }

    /**
     * 灶具断电后的重连机制
     */
    @Override
    public void retryConnect() {
        //如果在重连中，return
        if (null != timer_retry_min) {
            return;
        }
        if (null == timer_retry_min) {
            timer_retry_min = new Timer();
        }
        //定时器
        timer_retry_min.schedule(new TimerTask() {
            @Override
            public void run() {
                //重连时，标记位非手动连接
                connect(false);
                LogUtil.LOG_TOOTH("蓝牙重连retryConnect：", link_count);
            }
        }, 1000, 20 * 1000);
    }

    /**
     * 关闭连接
     */
    public synchronized void disConnect() {
        super.disConnect();
        //主动断开，将本地状态为重设
        setState(STATE_NONE);

        if (mConnectThread != null) {
            //在cancel方法中会主动notify，将状态告诉View
            mConnectThread.cancel();
            mConnectThread = null;
            LogUtil.LOG_TOOTH("蓝牙BT","disConnect");
        }
    }

    @Override
    public synchronized void write(byte[] data) {
        if (null != data && linkStatus == STATE_CONNECTED) {
            queue.add(data);
        }
    }

    @Override
    public void read() {

    }

    /**
     * 负责连接远程设备的线程
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;
        private boolean isCanceled;

        public ConnectThread(BluetoothDevice device) {
            //设置线程名称
//            setName("线程名称：" + System.currentTimeMillis());
            mmDevice = device;
            mSocketType = secure ? "Secure" : "Insecure";


            try {
                Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                mmSocket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
//            try {
//                //创建一个安全的socket连接
//                if (secure) {
//                    mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
//                }
//                //创建一个非安全的socket连接
//                else {
//                    mmSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        public void run() {
            //连接时，停止搜索
            bluetoothAdapter.cancelDiscovery();
            LogUtil.LOG_TOOTH("连接线程打开", mmDevice.getName() + ":" + mmDevice.getAddress());
            // Make a connection to the BluetoothSocket
            try {
                if (!mmSocket.isConnected()) {
                    // This is a blocking call and will only return on a successful connection or an exception
                    mmSocket.connect();
                }
                //状态同步
                setState(STATE_CONNECTED);
                LogUtil.LOG_TOOTH("Client连接到", mmDevice.getName());
                // Start the connected thread
                // connected(mmSocket, mmDevice, mSocketType);
                //连接成功通知ui界面
                uiHandler.sendEmptyMessage(STATE_CONNECTED);
                // 开启while循环
                try {
                    //只有当连接上方太的灶具蓝牙才开启线程
                    if (mmDevice.getName().contains(BleConstant.DEVICE_NAME_C2I)) {
                        success();
                    }
                } catch (Exception e) {
                    //数据传输失败，报错
                    if (null != mmSocket) {
                        mmSocket.close();
                    }
                    setState(STATE_DATA_ERROR);
                    //通知ui界面--连接失败
                    Message message = new Message();
                    message.what = STATE_DATA_ERROR;
                    message.obj = e;
                    uiHandler.sendMessage(message);
                }
            } catch (IOException e) {
                try {
                    //连接失败关闭Socket
                    if (null != mmSocket) {
                        mmSocket.close();
                        mmSocket = null;
                    }
                    setState(STATE_ERROR);
                    //通知ui界面--连接失败
                    Message message = new Message();
                    message.what = STATE_ERROR;
                    message.obj = e;
                    uiHandler.sendMessage(message);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }

        //主动关闭蓝牙
        public void cancel() {
            try {
                isCanceled = true;
                //暂停200毫秒，让success中的流资源回收完毕
                sleep(200);
                if (null != mmSocket) {
                    mmSocket.close();
                }
                setState(STATE_NONE);
                uiHandler.sendEmptyMessage(STATE_NONE);
                LogUtil.LOG_TOOTH("蓝牙连接关闭", mmDevice.getName() + ":" + mmDevice.getAddress());
                mmSocket = null;
            } catch (Exception e) {
                e.printStackTrace();
                mmSocket = null;
            }
        }

        private void success() throws Exception {
            InputStream inputStream = mmSocket.getInputStream();
            OutputStream outputStream = mmSocket.getOutputStream();

            while (true) {
                //写数据
                if (queue.size() != 0) {
                    byte[] bytes = queue.poll();
//                    LogUtil.LOG_TOOTH("============write bytes", Tool.getHexBinString(bytes,false));
                    if (null != bytes && null != outputStream) {
                        outputStream.write(bytes);
                    }
                }
                sleep(10);

                //读数据
                int count = inputStream.available();
                if (count != 0) {
                    byte[] cache = new byte[count];
                    int length = inputStream.read(cache);
                    if (length > 0) {
                        bleDevice.data_read = cache;
                        notifyReadData(bleDevice);
                    }
                }

                if (isCanceled) {
                    //回收资源
                    if (null != inputStream) {
                        inputStream.close();
                    }
                    if (null != outputStream) {
                        outputStream.close();
                    }
                    break;
                }
            }
        }
    }

    Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            bleDevice.linkStatus = msg.what;
            notifyLinkData(bleDevice);
            return false;
        }
    });

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

            for (Action1 action : list_link) {
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
            Observable<BleDevice> observable = Observable.just(bleDevice);

            observable.subscribeOn(Schedulers.immediate())//指定被观察者的执行线程（立即在当前线程执行指定的工作）
                    .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                    .subscribe(action_read);
        }
    }
}
