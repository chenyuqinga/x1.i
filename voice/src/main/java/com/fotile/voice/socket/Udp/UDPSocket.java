/*
 * ************************************************************
 * 文件：UDPSocket.java  模块：voice  项目：Z1.5
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.Udp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.Log;

import com.fotile.voice.CommonConst;
import com.fotile.voice.socket.BaseSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UDPSocket extends BaseSocket {

    private static final String TAG = "UDPSocket";

    private static final int BUFFER_LENGTH = 1024;
    private byte[] receiveByte = new byte[BUFFER_LENGTH];
    private static final int MSG_TIME_OUT = 30001;

    private int mTimeOut = 60000;
    private boolean mIsThreadRunning = false;

    private Context mContext;
    private MulticastSocket mMultiCastClient;
    private DatagramSocket mUdpClient;
    private InetAddress broadcastAddress = null;//当前设备在局域网下的IP地址
    private DatagramPacket mReceivePacket;

    private ExecutorService mThreadPool;
    private Thread mClientThread;
    private final List<OnUdpEventListener> mMessageReceiveList;
    private WifiManager.MulticastLock mMultiCastLock = null;

    @Override
    protected void dealWithMsg(Message msg) {
        switch (msg.what) {
            case MSG_TIME_OUT:
                Log.e(TAG, "notify time out");
                notifyTimeout();
                break;
            default:
                break;
        }
    }

    public UDPSocket(Context context) {

        this.mContext = context;

        int cpuNumbers = Runtime.getRuntime().availableProcessors();
        // 根据CPU数目初始化线程池
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * CommonConst.POOL_SIZE_PER_CORE);

        mMessageReceiveList = new ArrayList<>();
    }

    public void addOnMessageReceiveListener(OnUdpEventListener listener) {
        mMessageReceiveList.add(listener);
    }


    public void startUDPSocket(int port) {
        if (mUdpClient != null) {
            return;
        }
        try {
            // 表明这个 Socket 在设置的端口上监听数据。
            mUdpClient = new DatagramSocket(port);
            mUdpClient.setReuseAddress(true);
            if (mReceivePacket == null) {
                // 创建接受数据的 packet
                mReceivePacket = new DatagramPacket(receiveByte, BUFFER_LENGTH);
            }
            startSocketThread();
            refreshTimeOut();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startMultiCastSocket(String ip, int port) {
        // TODO: 2019/9/11 该锁目前只有在开启server时申请，若单纯发送使用需要单独申请
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && mMultiCastLock != null) {
            mMultiCastLock = wifiManager.createMulticastLock("multicast.test");
            if (!mMultiCastLock.isHeld()) {
                mMultiCastLock.acquire();
            }
        }
        if (mMultiCastClient != null) {
            return;
        }
        try {
            // 表明这个 Socket 在设置的端口上监听数据。
            mMultiCastClient = new MulticastSocket(port);
            // TODO: 2019/9/9 ip应作为参数传入"239.255.255.250"
            broadcastAddress = InetAddress.getByName(ip);
            mMultiCastClient.joinGroup(broadcastAddress);
            mMultiCastClient.setReuseAddress(true);
            if (mReceivePacket == null) {
                // 创建接受数据的 packet
                mReceivePacket = new DatagramPacket(receiveByte, BUFFER_LENGTH);
            }
            startSocketThread();
            refreshTimeOut();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "start udp group, exception: " + e.toString());
        }
    }

    /**
     * 开启接收数据的线程
     */
    private void startSocketThread() {
        mClientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                receiveMessage();
            }
        });
        mIsThreadRunning = true;
        mClientThread.start();
        Log.d(TAG, "开启 UDP 数据接收线程");
    }

    /**
     * 处理接受到的消息
     */
    private void receiveMessage() {
        while (mIsThreadRunning) {
            Log.e(TAG, "multi: " + mMultiCastClient + ", mUdpClient: " + mUdpClient);
            try {
                if (mMultiCastClient != null) {
                    mMultiCastClient.receive(mReceivePacket);
                }
                if (mUdpClient != null) {
                    mUdpClient.receive(mReceivePacket);
                }
                Log.d(TAG, "receive packet success...");
            } catch (IOException e) {
                Log.e(TAG, "UDP数据包接收失败！线程停止, " + e.toString());
                stopUDPSocket();
                e.printStackTrace();
                return;
            }

            if (mReceivePacket == null || mReceivePacket.getLength() == 0) {
                Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }

            String strReceive = new String(mReceivePacket.getData(), mReceivePacket.getOffset(),
                    mReceivePacket.getLength());
            Log.d(TAG, strReceive + " from " + mReceivePacket.getAddress().getHostAddress() + ":" +
                       mReceivePacket.getPort());

            //解析接收到的 json 信息
            notifyMessageReceive(mReceivePacket);
            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (mReceivePacket != null) {
                mReceivePacket.setLength(BUFFER_LENGTH);
            }
        }
    }

    /**
     * 将消息通过接口发送到每个页面
     *
     * @param strReceive
     */
    private void notifyMessageReceive(DatagramPacket strReceive) {
        refreshTimeOut();
        for (OnUdpEventListener listener : mMessageReceiveList) {
            if (listener != null) {
                listener.onMessageReceived(strReceive);
            }
        }
    }

    private void refreshTimeOut() {
        Log.e(TAG, "refresh time out: " + mTimeOut);
        if (mUIHandler.hasMessages(MSG_TIME_OUT)) {
            mUIHandler.removeMessages(MSG_TIME_OUT);
        }
        if (mTimeOut != 0) {
            mUIHandler.sendEmptyMessageDelayed(MSG_TIME_OUT, mTimeOut);
        }
    }

    private void notifyTimeout() {
        for (OnUdpEventListener listener : mMessageReceiveList) {
            if (listener != null) {
                listener.onTimeOut();
            }
        }
    }

    public void stopUDPSocket() {
        Log.e(TAG, "close socket, remove time out");
        mIsThreadRunning = false;
        mReceivePacket = null;
        if (mUIHandler.hasMessages(MSG_TIME_OUT)) {
            mUIHandler.removeMessages(MSG_TIME_OUT);
        }
        if (mClientThread != null) {
            mClientThread.interrupt();
        }
        if (mThreadPool != null) {
            mThreadPool.shutdown();
        }
        if (mMultiCastClient != null) {
            mMultiCastClient.close();
            mMultiCastClient = null;
        }
        if (mUdpClient != null) {
            mUdpClient.close();
            mUdpClient = null;
        }
        if (mMultiCastLock != null && mMultiCastLock.isHeld()) {
            mMultiCastLock.release();
        }
    }


    public void sendMsg(final InetAddress inetAddress, final String msg, final int port) {
        Log.e(TAG, "send msg to inetAddress: " + inetAddress);
        DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                inetAddress, port);
        executeSend(packet);
    }

    public void sendMsg(final String ip, final String msg, final int port) {
        Log.e(TAG, "send msg to ip " + ip);
        InetAddress targetAddress = null;
        try {
            targetAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (targetAddress != null) {
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    targetAddress, port);
            Log.e(TAG, "packet: " + packet.getAddress() + ", " + Arrays.toString(packet.getData()) +
                       ", " + packet.getPort());
            executeSend(packet);
        } else {
            Log.e(TAG, "inetAddress is null");
        }
    }

    private void executeSend(final DatagramPacket packet) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (mMultiCastClient != null) {
                    try {
                        mMultiCastClient.send(packet);
                        // 数据发送事件
                        Log.e(TAG, "组播数据发送成功");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (mUdpClient != null) {
                    try {
                        mUdpClient.send(packet);
                        // 数据发送事件
                        Log.e(TAG, "广播数据发送成功");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "mClient is null");
                }
            }
        });
    }

    public int getTimeOut() {
        return mTimeOut;
    }

    public void setTimeOut(int timeOut) {
        this.mTimeOut = timeOut;
    }
}
