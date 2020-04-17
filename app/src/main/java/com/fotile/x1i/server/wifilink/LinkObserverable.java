package com.fotile.x1i.server.wifilink;

import android.content.Context;

import com.fotile.common.util.log.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 文件名称：LinkObserverable
 * 创建时间：2019/7/17 17:04
 * 文件作者：yaohx
 * 功能描述：tcp连接处理类，只维持一个连接
 */
public class LinkObserverable {
    /**
     * 未连接
     */
    public static final int STATE_NONE = 0;
    /**
     * 连接失败
     */
    public static final int STATE_ERROR = 1;
    /**
     * 正在连接
     */
    public static final int STATE_CONNECTING = 2;
    /**
     * 已经连接
     */
    public static final int STATE_CONNECTED = 3;

    private Context context;

    private static LinkObserverable instance;
    private StoveWifiDevice wifiDevice;

    private ConnectThread mConnectThread;
    /**
     * 连接状态
     */
    private int state;

    CopyOnWriteArrayList<Action1<StoveWifiDevice>> list = new CopyOnWriteArrayList<Action1<StoveWifiDevice>>();

    private LinkObserverable(Context context) {
        this.context = context;
    }

    public static LinkObserverable getInstance(Context context) {
        if (null == instance) {
            instance = new LinkObserverable(context);
        }
        return instance;
    }

    public synchronized void connect(StoveWifiDevice wifiDevice) {
        if (null != wifiDevice) {
            this.wifiDevice = wifiDevice;

            //取消上一次正在连接的线程
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }

            mConnectThread = new ConnectThread(wifiDevice);
            mConnectThread.start();
            LogUtil.LOG_STOVE_LINK("tcp连接发起", wifiDevice);
            //正在连接
            setState(STATE_CONNECTING);
            //将状态分发给订阅者
            notifyLinkData();
        }
    }

    /**
     * 关闭连接
     */
    public synchronized void disConnection() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        setState(STATE_NONE);
        //将状态分发给订阅者
        notifyLinkData();
    }

    private void setState(int state) {
        this.state = state;
    }

    /**
     * 获取当前连接的设备
     * @return
     */
    public StoveWifiDevice getLinkedStoveDevice(){
        if(state == LinkObserverable.STATE_CONNECTED){
            return wifiDevice;
        }
        return null;
    }

    class ConnectThread extends Thread {
        private StoveWifiDevice device;
        private Socket socket;
        private boolean isCanceled;

        public ConnectThread(StoveWifiDevice wifiDevice) {
            this.device = wifiDevice;
        }

        @Override
        public void run() {
            super.run();
            try {
                //开始连接后关闭搜索
                WifiSearchObserverable.getInstance(context).stopSearch(false);
                socket = new Socket(device.ip, device.port);
                //连接成功
                setState(STATE_CONNECTED);
                //将状态分发给订阅者
                notifyLinkData();

                LogUtil.LOG_STOVE_LINK("tcp连接成功", device);
                try {
                    success();
                } catch (Exception e) {
                    LogUtil.LOG_STOVE_LINK("读写失败error",e.getMessage());
                    e.printStackTrace();
                    //连接失败
                    setState(STATE_ERROR);
                    //将状态分发给订阅者
                    notifyLinkData();
                }
            } catch (IOException e) {
                LogUtil.LOG_STOVE_LINK("连接失败error",e.getMessage());
                e.printStackTrace();
                //连接失败
                setState(STATE_ERROR);
                //将状态分发给订阅者
                notifyLinkData();
            }
        }

        public void success() throws Exception {
            //创建一个socket
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            while (true) {
                byte[] bytes = LinkAction.getInstance(context).getData();
                //写数据
                if (null != bytes && null != outputStream) {
                    outputStream.write(bytes);
                }

                sleep(10);

                //读数据
                int count = inputStream.available();
                if (count != 0) {
                    byte[] cache = new byte[count];
                    int length = inputStream.read(cache);
                    if (length != -1) {
                        LinkAction.getInstance(context).reciverData(cache);
                    }
                }

                if (isCanceled) {
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

        public void cancel() {
            try {
                isCanceled = true;
                //暂停200毫秒，让success中的流资源回收完毕
                sleep(200);
                if (null != socket) {
                    socket.close();
                }
                //连接关闭
                setState(STATE_NONE);
                //将状态分发给订阅者
                notifyLinkData();
                LogUtil.LOG_STOVE_LINK("tcp连接关闭：", device);
                socket = null;
            } catch (Exception e) {
                e.printStackTrace();
                socket = null;
            }
        }
    }

    public void addLinkAction(Action1<StoveWifiDevice> action1) {
        if (null != action1 && !list.contains(action1)) {
            list.add(action1);
        }
    }

    public void removeLinkAction(Action1<StoveWifiDevice> action1) {
        if (null != action1) {
            list.remove(action1);
        }
    }

    /**
     * 将数据分发给订阅者
     */
    private void notifyLinkData() {
        if (null != wifiDevice) {
            wifiDevice.linkState = state;
            //创建一个被观察者对象
            Observable observable = Observable.just(wifiDevice);
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
