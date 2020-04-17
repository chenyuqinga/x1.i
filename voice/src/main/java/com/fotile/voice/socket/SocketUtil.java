/*
 * ************************************************************
 * 文件：SocketUtil.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket;

import android.content.Context;
import android.util.Log;

import com.fotile.voice.CommonConst;
import com.fotile.voice.socket.TcpClient.TcpClient;
import com.fotile.voice.socket.TcpClient.TcpConnConfig;
import com.fotile.voice.socket.TcpClient.bean.TargetInfo;
import com.fotile.voice.socket.TcpClient.helper.stickpackage.VariableLenStickPackageHelper;
import com.fotile.voice.socket.TcpClient.listener.TcpClientListener;
import com.fotile.voice.socket.Udp.OnUdpEventListener;
import com.fotile.voice.socket.Udp.UDPSocket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.InetAddress;
import java.nio.ByteOrder;


public class SocketUtil {

    public static final String TAG = SocketUtil.class.getSimpleName();
    private Context mContext;
    private volatile static SocketUtil mInstance = null;
    private UDPSocket mUdpSocket;
    private TargetInfo mTargetInfo;

    private SocketUtil(Context context) {
        mContext = context;
    }

    public static SocketUtil getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SocketUtil.class) {
                if (mInstance == null) {
                    mInstance = new SocketUtil(context);
                }
            }
        }
        return mInstance;
    }

    public void updateContext(Context context) {
        mContext = context;
        mUdpSocket = new UDPSocket(context);
    }

    //启动udp服务
    public void startUdpServer(int port, OnUdpEventListener listener, int timeOut) {
        if (mUdpSocket == null) {
            mUdpSocket = new UDPSocket(mContext);
        }
//        if (timeOut != 0) {
            mUdpSocket.setTimeOut(timeOut);
//        }
        mUdpSocket.addOnMessageReceiveListener(listener);
        mUdpSocket.startUDPSocket(port);
    }

    public void startMultiCastServer(String ip, int port, OnUdpEventListener listener, int timeOut) {
        if (mUdpSocket == null) {
            mUdpSocket = new UDPSocket(mContext);
        }
        mUdpSocket.setTimeOut(timeOut);
        mUdpSocket.addOnMessageReceiveListener(listener);
        mUdpSocket.startMultiCastSocket(ip, port);
    }

    public void sendMsgByUdp(String msg, InetAddress address, int port) {
        if (mUdpSocket == null) {
            mUdpSocket = new UDPSocket(mContext);
        }
        mUdpSocket.sendMsg(address, msg, port);
    }

    public void sendMsgByUdp(String word, String ip, int port) {
        if (mUdpSocket == null) {
            mUdpSocket = new UDPSocket(mContext);
        }
        mUdpSocket.sendMsg(ip, word, port);
    }

    public void setUdpTimeOut(int timeOut) {
        if (mUdpSocket == null) {
            mUdpSocket = new UDPSocket(mContext);
        }
        mUdpSocket.setTimeOut(timeOut);
    }

    public int getUdpTimeOut() {
        if (mUdpSocket == null) {
            mUdpSocket = new UDPSocket(mContext);
        }
        return mUdpSocket.getTimeOut();
    }

    public void stopUdpSocket() {
        if (mUdpSocket != null) {
            mUdpSocket.stopUDPSocket();
            mUdpSocket = null;
        }
    }

    public void startTcpSocket(String ip, int port, TcpClientListener listener) {
        mTargetInfo = new TargetInfo(ip, port);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", CommonConst.REQUEST_HEARTBEAT);

        VariableLenStickPackageHelper variableLenStickPackageHelper = new VariableLenStickPackageHelper(
                ByteOrder.LITTLE_ENDIAN, 2, 4, 6);
        TcpConnConfig tcpConnConfig = new TcpConnConfig.Builder().setIsReconnect(false)
                .setConnTimeout(0).setStickPackageHelper(variableLenStickPackageHelper)
                .setHeartBeat(new Gson().toJson(jsonObject)).create();
        TcpClient tcpClient = TcpClient.getTcpClient(mTargetInfo, tcpConnConfig);
        tcpClient.addTcpClientListener(listener);
        if (tcpClient.isDisconnected()) {
            tcpClient.connect();
        }
    }

    public void stopTcpSocket() {
        if (mTargetInfo != null) {
            TcpClient tcpClient = TcpClient.getTcpClient(mTargetInfo);
            if (tcpClient != null) {
                tcpClient.disconnect();
            }
        }
    }

    public void startTcpHeartBeat() {
        Log.e(TAG, "start heartbeat, mTargetInfo: " + mTargetInfo);
        if (mTargetInfo != null) {
            TcpClient tcpClient = TcpClient.getTcpClient(mTargetInfo);
            Log.e(TAG, "start heartbeat, tcpClient: " + tcpClient);
            if (tcpClient != null) {
                Log.e(TAG, "actually start heartbeat");
                tcpClient.startHeartBeat();
            }
        }
    }

    public boolean sendMsgByTcp(String msg) {
        if (mTargetInfo != null) {
            TcpClient tcpClient = TcpClient.getTcpClient(mTargetInfo);
            if (tcpClient != null) {
                tcpClient.sendMsg(msg);
                return true;
            }
        }
        return false;
    }

    public String getTargetIp() {
        if (TcpClient.getTcpClient(mTargetInfo) != null && TcpClient.getTcpClient(mTargetInfo).isConnected()) {
            return mTargetInfo.getIp();
        }
        return null;
    }

    public boolean hasTcpConnection() {
        if (mTargetInfo != null) {
            TcpClient tcpClient = TcpClient.getTcpClient(mTargetInfo);
            if (tcpClient != null) {
                return tcpClient.isConnected();
            }
        }
        return false;
    }
}
