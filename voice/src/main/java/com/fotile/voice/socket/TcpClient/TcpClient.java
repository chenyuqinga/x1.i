/*
 * ************************************************************
 * 文件：TcpClient.java  模块：voice  项目：Z1.5
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.TcpClient;


import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.fotile.voice.socket.BaseSocket;
import com.fotile.voice.socket.TcpClient.bean.TargetInfo;
import com.fotile.voice.socket.TcpClient.bean.TcpMsg;
import com.fotile.voice.socket.TcpClient.helper.stickpackage.BaseStickPackageHelper;
import com.fotile.voice.socket.TcpClient.helper.stickpackage.VariableLenStickPackageHelper;
import com.fotile.voice.socket.TcpClient.listener.TcpClientListener;
import com.fotile.voice.socket.TcpClient.manager.TcpClientManager;
import com.fotile.voice.socket.TcpClient.receive.ClientActionDispatcher;
import com.fotile.voice.socket.TcpClient.receive.ClientReadThread;
import com.fotile.voice.socket.TcpClient.receive.IReader;
import com.fotile.voice.socket.TcpClient.receive.OkServerOptions;
import com.fotile.voice.socket.TcpClient.receive.OriginalData;
import com.fotile.voice.socket.TcpClient.receive.ReaderImpl;
import com.fotile.voice.socket.TcpClient.state.ClientState;
import com.fotile.voice.utils.ByteUtils;
import com.fotile.voice.utils.CharsetUtil;
import com.fotile.voice.utils.ExceptionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * tcp客户端
 */
public class TcpClient extends BaseSocket {
    private static final String TAG = TcpClient.class.getSimpleName();
    private static final String TAG_RECEIVE = TcpClient.class.getSimpleName() + "_receive";
    private TargetInfo mTargetInfo;//目标ip和端口号
    private Socket mSocket;
    private ClientState mClientState;
    private TcpConnConfig mTcpConnConfig;
    private ConnectionThread mConnectionThread;
    private SendThread mSendThread;
    //    private ReceiveThread mReceiveThread;
    private List<TcpClientListener> mTcpClientListeners;
    private LinkedBlockingQueue<TcpMsg> msgQueue;
    private HeartbeatTimer mTimer;
    private long mPangTime;
    private int mTimeoutTimes;
    private static final int MSG_CHECK_PANG = 3001;
    private VariableLenStickPackageHelper mPackageHelper;
    private BaseStickPackageHelper mBaseStickPackageHelper;
    private IReader mReader;
    private OkServerOptions mOptions;
    private ClientActionDispatcher mActionDispatcher;
    private int index;
    private ClientReadThread mClientReadThread;

    @Override
    protected void dealWithMsg(Message msg) {
        switch (msg.what) {
            case MSG_CHECK_PANG:
                long pingTime = (long) msg.obj;
                Log.e(TAG, "check pang, ping_time: " + pingTime + ", pang_time: " + mPangTime +
                           ", minus: " + (mPangTime - pingTime));
                // TODO: 2019/1/11 此处待修改
                if (mPangTime - pingTime > 2000 || mPangTime - pingTime < 0) {
                    //心跳超时
                    mTimeoutTimes += 1;
                } else {
                    mTimeoutTimes = 0;
                }
                if (mTimeoutTimes > 3) {
                    notifyPingTimeout();
                    disconnect("heartbeat disconnected", null);
                    Log.e(TAG, "heartbeat disconnected");
                }
                break;
            default:
                break;
        }
    }

    private TcpClient() {
        super();
    }

    /**
     * 创建tcp连接，需要提供服务器信息
     *
     * @param targetInfo
     * @return
     */
    public static TcpClient getTcpClient(TargetInfo targetInfo) {
        return getTcpClient(targetInfo, null);
    }

    public static TcpClient getTcpClient(TargetInfo targetInfo, TcpConnConfig tcpConnConfig) {
        TcpClient TcpClient = TcpClientManager.getTcpClient(targetInfo);
        if (TcpClient == null) {
            TcpClient = new TcpClient();
            TcpClient.init(targetInfo, tcpConnConfig);
            TcpClientManager.putTcpClient(TcpClient);
        }
        return TcpClient;
    }

    /**
     * 根据socket创建client端，目前仅用在socketServer接受client之后
     *
     * @param socket
     * @return
     */
    public static TcpClient getTcpClient(Socket socket, TargetInfo targetInfo) {
        return getTcpClient(socket, targetInfo, null);
    }

    public static TcpClient getTcpClient(Socket socket, TargetInfo targetInfo,
            TcpConnConfig connConfig) {
        if (!socket.isConnected()) {
            ExceptionUtils.throwException("socket is closeed");
        }
        TcpClient tcpClient = new TcpClient();
        tcpClient.init(targetInfo, connConfig);
        tcpClient.mSocket = socket;
        tcpClient.mClientState = ClientState.Connected;
        tcpClient.onConnectSuccess();
        return tcpClient;
    }


    private void init(TargetInfo targetInfo, TcpConnConfig connConfig) {
        this.mTargetInfo = targetInfo;
        mClientState = ClientState.Disconnected;
        mTcpClientListeners = new ArrayList<>();
        if (mTcpConnConfig == null && connConfig == null) {
            VariableLenStickPackageHelper variableLenStickPackageHelper = new VariableLenStickPackageHelper(
                    ByteOrder.LITTLE_ENDIAN, 2, 4, 6);
            mTcpConnConfig = new TcpConnConfig.Builder().setStickPackageHelper(
                    variableLenStickPackageHelper).create();
        } else if (connConfig != null) {
            mTcpConnConfig = connConfig;
        }
        mPackageHelper = (VariableLenStickPackageHelper) mTcpConnConfig.getStickPackageHelper();
        mBaseStickPackageHelper = new BaseStickPackageHelper();
    }

    public synchronized TcpMsg sendMsg(String message) {
        TcpMsg msg = new TcpMsg(message, mTargetInfo, TcpMsg.MsgType.Send);
        Log.e("dydy", "message: " + message + ", TcpMsg: " + msg);
        return sendMsg(msg);
    }

    public synchronized TcpMsg sendMsg(byte[] message) {
        TcpMsg msg = new TcpMsg(message, mTargetInfo, TcpMsg.MsgType.Send);
        return sendMsg(msg);
    }

    public synchronized TcpMsg sendMsg(TcpMsg msg) {
        if (isDisconnected()) {
            Log.e(TAG, "发送消息 " + msg + "，当前没有tcp连接，先进行连接");
            connect();
        }
        boolean re = enqueueTcpMsg(msg);
        Log.e("dydy", "enqueueTcpMsg: " + msg);
        if (re) {
            return msg;
        }
        return null;
    }

    public synchronized boolean cancelMsg(TcpMsg msg) {
        return getSendThread().cancel(msg);
    }

    public synchronized boolean cancelMsg(int msgId) {
        return getSendThread().cancel(msgId);
    }

    public synchronized void connect() {
        if (!isDisconnected()) {
            Log.e(TAG, "已经连接了或正在连接");
            return;
        }
        Log.e(TAG, "tcp connecting");
        setClientState(ClientState.Connecting);//正在连接
        getConnectionThread().start();
    }

    public synchronized Socket getSocket() {
        if (mSocket == null || isDisconnected() || !mSocket.isConnected()) {
            mSocket = new Socket();
            try {
                mSocket.setSoTimeout((int) mTcpConnConfig.getReceiveTimeout());
            } catch (SocketException e) {
                //                e.printStackTrace();
                Log.e(TAG, "SocketException: " + e.toString());
            }
        }
        return mSocket;
    }

    public synchronized void disconnect() {
        disconnect("手动关闭tcpclient", null);
    }

    protected synchronized void onErrorDisConnect(String msg, Exception e) {
        if (isDisconnected()) {
            return;
        }
        disconnect(msg, e);
        // TODO: 2019/1/10 重连次数
        if (mTcpConnConfig.isReconnect()) {//重连
            connect();
        }
    }

    protected synchronized void disconnect(String msg, Exception e) {
        if (isDisconnected()) {
            return;
        }
        stopHeartbeatTimer();
        closeSocket();
        getConnectionThread().interrupt();
        getSendThread().interrupt();
        //        getReceiveThread().interrupt();
        shutdownReadThread(e);
        setClientState(ClientState.Disconnected);
        notifyDisconnected(msg, e);
        Log.e(TAG, "tcp closed, msg: " + msg + ", exception: " + (null == e ? null : e.toString()));
    }

    private synchronized boolean closeSocket() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                //                e.printStackTrace();
            }
        }
        mSocket = null;
        return true;
    }

    //连接已经连接，接下来的流程，创建发送和接受消息的线程
    private void onConnectSuccess() {
        Log.e(TAG, "tcp connect 建立成功");
        setClientState(ClientState.Connected);//标记为已连接
        SendThread sendThread = getSendThread();
        if (!sendThread.isAlive()) {
            sendThread.start();
        }
        //        if (!TextUtils.isEmpty(mTcpConnConfig.getPingString())) {
        //            startHeartbeatTimer(mTcpConnConfig.getPingString());
        //        }
        /*ReceiveThread receiveThread = getReceiveThread();
        if (!receiveThread.isAlive()) {
            receiveThread.start();
        }*/
        mReader = new ReaderImpl();
        mOptions = OkServerOptions.getDefault();
        mActionDispatcher = new ClientActionDispatcher(mActionListener);
        mReader.setOption(mOptions);
        try {
            mReader.initialize(getSocket().getInputStream(), mActionDispatcher);
        } catch (IOException e) {
            e.printStackTrace();
        }
        startReadEngine();
    }

    public void startReadEngine() {
        Log.e(TAG, "start read engine");
        if (mClientReadThread != null) {
            mClientReadThread.shutdown();
            mClientReadThread = null;
        }
        mClientReadThread = new ClientReadThread(mReader, mActionDispatcher);
        mClientReadThread.start();
    }

    private void shutdownReadThread(Exception e) {
        if (mClientReadThread != null) {
            mClientReadThread.shutdown(e);
            mClientReadThread = null;
        }
    }

    public void startHeartBeat() {
        Log.e(TAG, "PingString(): " + mTcpConnConfig.getPingString());
        if (!TextUtils.isEmpty(mTcpConnConfig.getPingString())) {
            startHeartbeatTimer(mTcpConnConfig.getPingString());
        }
    }

    /**
     * tcp连接线程
     */
    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            try {
                int localPort = mTcpConnConfig.getLocalPort();
                Log.e(TAG, "local port: " + localPort);
                if (localPort > 0) {
                    Log.e(TAG, "is bound: " + getSocket().isBound());
                    if (!getSocket().isBound()) {
                        getSocket().bind(new InetSocketAddress(localPort));
                    }
                }
                Log.e(TAG, "after bind: " + getSocket().isClosed());
                getSocket().connect(
                        new InetSocketAddress(mTargetInfo.getIp(), mTargetInfo.getPort()),
                        (int) mTcpConnConfig.getConnTimeout());
                Log.e(TAG, "创建连接成功,target=" + mTargetInfo + ",localport=" + localPort);
            } catch (Exception e) {
                Log.e(TAG, "创建连接失败,target=" + mTargetInfo + "," + e);
                onErrorDisConnect("创建连接失败", e);
                return;
            }
            notifyConnected();
            onConnectSuccess();
        }
    }

    public boolean enqueueTcpMsg(final TcpMsg tcpMsg) {
        if (tcpMsg == null || getMsgQueue().contains(tcpMsg)) {
            return false;
        }
        try {
            getMsgQueue().put(tcpMsg);
            return true;
        } catch (InterruptedException e) {
            //            e.printStackTrace();
        }
        return false;
    }

    protected LinkedBlockingQueue<TcpMsg> getMsgQueue() {
        if (msgQueue == null) {
            msgQueue = new LinkedBlockingQueue<>();
        }
        return msgQueue;
    }

    private class SendThread extends Thread {
        private TcpMsg sendingTcpMsg;

        protected SendThread setSendingTcpMsg(TcpMsg sendingTcpMsg) {
            this.sendingTcpMsg = sendingTcpMsg;
            return this;
        }

        public TcpMsg getSendingTcpMsg() {
            return this.sendingTcpMsg;
        }

        public boolean cancel(TcpMsg packet) {
            return getMsgQueue().remove(packet);
        }

        public boolean cancel(int tcpMsgID) {
            return getMsgQueue().remove(new TcpMsg(tcpMsgID));
        }

        @Override
        public void run() {
            TcpMsg msg;
            try {
                while (isConnected() && !Thread.interrupted() &&
                       (msg = getMsgQueue().take()) != null) {
                    setSendingTcpMsg(msg);//设置正在发送的
                    Log.e(TAG, "tcp sending msg=" + msg);
                    byte[] data = msg.getSourceDataBytes();
                    if (data == null) {//根据编码转换消息
                        data = CharsetUtil.stringToData(msg.getSourceDataString(),
                                mTcpConnConfig.getCharsetName());
                    }
                    byte[] vboxes = CharsetUtil.stringToData("vbox",
                            mTcpConnConfig.getCharsetName());
                    //                    Log.e(TAG, "vbox_byte: " + Arrays.toString(vboxes));
                    //                    int length = msg.getSourceDataString().toCharArray().length;
                    int length = CharsetUtil.stringToData(msg.getSourceDataString(),
                            mTcpConnConfig.getCharsetName()).length;
                    //                    Log.e(TAG, "int_length: " + length);
                    short shortLength = (short) length;
                    //                    Log.e(TAG, "short_length: " + shortLength);
                    byte[] lengthBytes = ByteUtils.shortToByte2Invert(shortLength);
                    //                    Log.e(TAG, "lengthBytes: " + Arrays.toString(lengthBytes));
                    byte[] finalBytes = ByteUtils.concatAll(vboxes, lengthBytes, data);
                    //                    Log.e(TAG, "final_byte: " + Arrays.toString(finalBytes));
                    if (finalBytes != null && finalBytes.length > 0) {
                        try {
                            getSocket().getOutputStream().write(finalBytes);
                            getSocket().getOutputStream().flush();
                            msg.setTime();
                            notifySent(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                            onErrorDisConnect("发送消息失败", e);
                            return;
                        }
                    }
                }
            } catch (InterruptedException e) {
                //                e.printStackTrace();
            }
        }
    }

    private class ReceiveThread extends Thread {
        public ReceiveThread() {
            super();
            //            mPackageHelper.setOnSnippetGetListener(this);
        }

        @Override
        public void run() {
            byte[] result;
            int len = 0;
            List<Byte> cache = new ArrayList<>();
            List<Byte> temp = new ArrayList<>();
            try {
                InputStream is = getSocket().getInputStream();
                while (isConnected() && !Thread.interrupted()) {
                    Log.e(TAG_RECEIVE, "InputStream: " + index++ + ", len: " + len);
                    if (len == 0) {
                        temp = mPackageHelper.execute(is);//粘包处理
                        len = mPackageHelper.getLen();
                        Log.e(TAG_RECEIVE,
                                "temp by len stick: " + (temp == null ? 0 : temp.size()) +
                                ", len: " + len);
                    }
                    //                    else {
                    //                        byte[] b = mBaseStickPackageHelper.execute(is);
                    //                        for (byte b1 : b) {
                    //                            temp.add(b1);
                    //                        }
                    //                        Log.e(TAG_RECEIVE, "temp by base stick: " + temp.size());
                    //                    }
                    if (temp == null || temp.isEmpty()) {
                        Log.e(TAG_RECEIVE, "tcp Receive 粘包处理失败");
                        //                        onErrorDisConnect("粘包处理中发送错误", null);
                        break;
                    }
                    cache.addAll(temp);
                    Log.e(TAG_RECEIVE, "cache: " + cache.size() + ", len: " + len + ",is enough: " +
                                       (cache.size() == len + 6));
                    if (cache.size() == len + 6) {
                        result = new byte[cache.size()];
                        for (int i = 0; i < result.length; i++) {
                            result[i] = cache.get(i);
                        }
                        Log.e(TAG_RECEIVE, "final result: " + result.length);
                        cache.clear();
                        len = 0;
                        mPackageHelper.setLen(0);
                        //                        Log.e(TAG, "tcp Receive final: " + Arrays.toString(result));
                        TcpMsg tcpMsg = new TcpMsg(result, mTargetInfo, TcpMsg.MsgType.Receive);
                        tcpMsg.setTime();
                        String msgStr = new String(result, StandardCharsets.UTF_8).substring(6);
                        tcpMsg.setSourceDataString(msgStr);
                        Log.e(TAG_RECEIVE, "tcp Receive string = " + msgStr);
                        byte[][] decodeBytes = mTcpConnConfig.getDecodeHelper().execute(result,
                                mTargetInfo, mTcpConnConfig);
                        tcpMsg.setEndDecodeData(decodeBytes);
                        if (msgStr.contains("response_heartbeat")) {
                            mPangTime = System.currentTimeMillis();
                            Log.e(TAG, "response_heartbeat");
                        } else {
                            notifyReceive(tcpMsg);//notify listener
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG_RECEIVE, "tcp Receive  error  " + e);
                if (!e.toString().contains("org.json.JSONException")) {
                    onErrorDisConnect("接受消息错误", e);
                }
            }
        }
    }

    private ClientActionDispatcher.ClientActionListener mActionListener = new ClientActionDispatcher.ClientActionListener() {
        @Override
        public void onClientReadReady() {

        }

        @Override
        public void onClientRead(OriginalData originalData) {
            String str = new String(originalData.getBodyBytes(), Charset.forName("utf-8"));
            if (TextUtils.isEmpty(str)) {
                return;
            }
            TcpMsg tcpMsg = new TcpMsg(originalData.getBodyBytes(), mTargetInfo,
                    TcpMsg.MsgType.Receive);
            tcpMsg.setTime();
            tcpMsg.setSourceDataString(str);
            Log.e(TAG_RECEIVE, "tcp Receive string = " + str);
            if (str.contains("response_heartbeat")) {
                mPangTime = System.currentTimeMillis();
                Log.e(TAG, "response_heartbeat");
            } else {
                notifyReceive(tcpMsg);//notify listener
            }
        }

        @Override
        public void onClientReadDead(Exception e) {

        }
    };

    /*private int inputstreamindex = 0;
    private int getByteindex = 0;
    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                InputStream is = getSocket().getInputStream();
                Log.e(TAG, "InputStream: " + is + ", " + inputstreamindex++);
                while (isConnected() && !Thread.interrupted()) {
                    Log.e(TAG, "getByteindex: " + is + ", " + getByteindex++);
                    byte[] result = mBaseStickPackageHelper.execute(is);//粘包处理
                    Log.e(TAG, "isConnected: " + isConnected() + ", thread: " + Thread.interrupted());
                    if (result == null) {//报错
                        Log.e(TAG, "tcp Receive 粘包处理失败 ");
                        onErrorDisConnect("粘包处理中发送错误", null);
                        break;
                    }
                    Log.e(TAG, "tcp Receive 解决粘包之后的数据 " + Arrays.toString(result));
                    TcpMsg tcpMsg = new TcpMsg(result, mTargetInfo, TcpMsg.MsgType.Receive);
                    tcpMsg.setTime();
                    String msgStr = new String(result, StandardCharsets.UTF_8);
                    tcpMsg.setSourceDataString(msgStr);
                    Log.e(TAG, "tcp Receive string = " + msgStr);
                    byte[][] decodebytes = mTcpConnConfig.getDecodeHelper().execute(result,
                            mTargetInfo, mTcpConnConfig);
                    tcpMsg.setEndDecodeData(decodebytes);
                    if (msgStr.contains("response_heartbeat")) {
                        mPangTime = System.currentTimeMillis();
                    } else {
                        notifyReceive(tcpMsg);//notify listener
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "tcp Receive  error  " + e);
                if (!e.toString().contains("org.json.JSONException")) {
                    onErrorDisConnect("接受消息错误", e);
                }
            }
        }
    }*/

    /*protected ReceiveThread getReceiveThread() {
        if (mReceiveThread == null || !mReceiveThread.isAlive()) {
            mReceiveThread = new ReceiveThread();
        }
        return mReceiveThread;
    }*/

    protected SendThread getSendThread() {
        if (mSendThread == null || !mSendThread.isAlive()) {
            mSendThread = new SendThread();
        }
        return mSendThread;
    }

    protected ConnectionThread getConnectionThread() {
        if (mConnectionThread == null || !mConnectionThread.isAlive() ||
            mConnectionThread.isInterrupted()) {
            mConnectionThread = new ConnectionThread();
        }
        return mConnectionThread;
    }

    public ClientState getClientState() {
        return mClientState;
    }

    protected void setClientState(ClientState state) {
        if (mClientState != state) {
            mClientState = state;
        }
    }

    public boolean isDisconnected() {
        return getClientState() == ClientState.Disconnected;
    }

    public boolean isConnected() {
        return getClientState() == ClientState.Connected;
    }

    private void notifyConnected() {
        for (TcpClientListener wl : mTcpClientListeners) {
            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onConnected(TcpClient.this);
                }
            });
        }
    }

    private void notifyDisconnected(final String msg, final Exception e) {
        for (TcpClientListener wl : mTcpClientListeners) {
            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onDisconnected(TcpClient.this, msg, e);
                }
            });
        }
    }


    private void notifyReceive(final TcpMsg tcpMsg) {
        for (TcpClientListener wl : mTcpClientListeners) {
            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onReceive(TcpClient.this, tcpMsg);
                }
            });
        }
    }


    private void notifySent(final TcpMsg tcpMsg) {
        for (TcpClientListener wl : mTcpClientListeners) {
            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onSent(TcpClient.this, tcpMsg);
                }
            });
        }
    }

    private void notifyValidationFail(final TcpMsg tcpMsg) {
        for (TcpClientListener wl : mTcpClientListeners) {
            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onValidationFail(TcpClient.this, tcpMsg);
                }
            });
        }
    }

    private void notifyPingTimeout() {
        for (TcpClientListener mTcpClientListener : mTcpClientListeners) {
            final TcpClientListener finalL = mTcpClientListener;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onPingTimeout(TcpClient.this);
                }
            });
        }
    }

    public TargetInfo getTargetInfo() {
        return mTargetInfo;
    }

    public void addTcpClientListener(TcpClientListener listener) {
        if (mTcpClientListeners.contains(listener)) {
            return;
        }
        mTcpClientListeners.add(listener);
    }

    public void removeTcpClientListener(TcpClientListener listener) {
        mTcpClientListeners.remove(listener);
    }

    public void config(TcpConnConfig tcpConnConfig) {
        mTcpConnConfig = tcpConnConfig;
    }

    /**
     * 启动心跳
     */
    public void startHeartbeatTimer(final String ping) {
        if (mTimer == null) {
            mTimer = new HeartbeatTimer();
        }
        mTimer.setOnScheduleListener(new HeartbeatTimer.OnScheduleListener() {
            @Override
            public void onSchedule() {
                Log.e(TAG, "mTimer is onSchedule...");
                long pingTime = System.currentTimeMillis();
                Message message = mUIHandler.obtainMessage(MSG_CHECK_PANG, pingTime);
                mUIHandler.sendMessageDelayed(message, 5000);
                sendMsg(ping);
            }
        });
        mTimer.startTimer(0, 1000 * 5);
    }

    public void stopHeartbeatTimer() {
        if (mTimer != null) {
            mTimer.exit();
            mTimer = null;
        }
        if (mUIHandler.hasMessages(MSG_CHECK_PANG)) {
            mUIHandler.removeMessages(MSG_CHECK_PANG);
        }
    }

    @Override
    public String toString() {
        return "TcpClient{" + "mTargetInfo=" + mTargetInfo + ",state=" + mClientState +
               ",isconnect=" + isConnected() + '}';
    }
}
