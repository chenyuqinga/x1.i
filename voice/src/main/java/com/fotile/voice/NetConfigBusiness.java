/*
 * ************************************************************
 * 文件：NetConfigBusiness.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.voice.bean.NluResult;
import com.fotile.voice.socket.SocketUtil;
import com.fotile.voice.socket.TcpClient.TcpClient;
import com.fotile.voice.socket.TcpClient.bean.JoinRequest;
import com.fotile.voice.socket.TcpClient.bean.TcpMsg;
import com.fotile.voice.socket.TcpClient.listener.TcpClientListener;
import com.fotile.voice.socket.Udp.OnUdpEventListener;
import com.fotile.voice.socket.bean.BoxNetInfo;
import com.fotile.voice.socket.bean.ResponseInfo;
import com.fotile.voice.socket.bean.ResponseJoin;
import com.fotile.voice.socket.bean.SelfNetInfo;
import com.fotile.voice.utils.DeviceUtil;
import com.fotile.voice.utils.WifiUtil;
import com.fotile.voice.wifi.WifiAPManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.util.ArrayList;

public class NetConfigBusiness {

    public static final String TAG = NetConfigBusiness.class.getSimpleName();
    //    private MyNetInfo mWifiInfo;
    private SelfNetInfo mNetInfo;
    private Context mContext;
    private boolean mIsPublicAp;
    private int mHomeId;
    private String mSsid;
    private String mPassWord;
    private BoxNetInfo mBoxNetInfo;
    private ArrayList<NetConfigStateChangeListener> mListeners;
    private int mNewHomeId;
    private boolean isNeedReconnect;
    private boolean mIsNeedTcp;
    private int mProbeTimes;
    private boolean mIsBoxConnected;
    private boolean mIsNeedUdpTimeOut;
    private NluResult mNluResult;
    private String mSession;
    private boolean mIsInProbe;
    private boolean mIsLeaveActively;
    private LeaveStatues mLeaveStatues;
    private static NetConfigBusiness instance = null;

    private static final int MSG_AP_CREATE_FAILED = 4003;
    private static final int MSG_PUB_AP_RECEIVE_TIMEOUT = 4004;
    private static final int MSG_GET_BOX_INFO_TIMEOUT = 4005;
    private static final int MSG_PROBE_BOX = 4007;
    private static final int MSG_AP_CREATE = 4008;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AP_CREATE:
                    //放出ap
                    mIsPublicAp = false;
                    WifiAPManager.getInstance(mContext).turnOnWifiAp(mSsid, mPassWord,
                            WifiAPManager.WifiSecurityType.WIFICIPHER_WPA2);
                    mHandler.sendEmptyMessageDelayed(MSG_AP_CREATE_FAILED, 30000);
                    break;
                case WifiAPManager.MESSAGE_AP_STATE_ENABLED:
                    Log.e(TAG, "ap开启成功");
                    if (mHandler.hasMessages(MSG_AP_CREATE_FAILED)) {
                        mHandler.removeMessages(MSG_AP_CREATE_FAILED);
                    }
                    if (mIsPublicAp) {
                        notifyNetConfigStateChange(CommonConst.ConfigState.PUB_AP_ENABLE);
                        //开udp服务,端口8000
                        SocketUtil.getInstance(mContext).startUdpServer(CommonConst.NET_PROBE_PORT,
                                mMessageReceiveListener, 0);
                        //点击下一步时开始倒计时
                        //                        startUdpTimeOutCountdown();
                        mIsNeedUdpTimeOut = true;
                    } else {
                        //开udp服务,端口8001
                        SocketUtil.getInstance(mContext).startUdpServer(CommonConst.BOX_PROBE_PORT,
                                mMessageReceiveListener, CommonConst.ONE_SECOND * 70);
                    }
                    break;
                case WifiAPManager.MESSAGE_AP_STATE_FAILED:
                    Log.e(TAG, "ap关闭");
                    if (isNeedReconnect) {
                        isNeedReconnect = false;
                        reconnectWifi();
                    }
                    break;
                case MSG_AP_CREATE_FAILED:
                    //turn on ap time out
                    Log.e(TAG, "ap time out, close it");
                    restoreState(true);
                    notifyNetConfigError(CommonConst.ConfigError.AP_TIME_OUT);
                    isNeedReconnect = true;
                    break;
                case MSG_PUB_AP_RECEIVE_TIMEOUT:
                    notifyNetConfigError(CommonConst.ConfigError.UDP_RECEIVE_TIMEOUT);
                    Log.e(TAG, "udp receive time out, close it");
                    restoreState(true);
                    isNeedReconnect = true;
                    break;
                case MSG_GET_BOX_INFO_TIMEOUT:
                    notifyNetConfigError(CommonConst.ConfigError.UDP_RECEIVE_TIMEOUT);
                    restoreState(true);
                    Log.e(TAG, "GET_BOX_INFO_TIMEOUT");
                    break;
                case WifiUtil.MSG_CONNECT_WIFI:
                    Log.e(TAG, "connect wifi, end time: " + System.currentTimeMillis());
                    //                    if (!mIsInProbe) {
                    //                        probeVBox();
                    //                    }
                    if (PreferenceUtil.isInGuide(mContext)) {
                        if (!mIsInProbe) {
                            Log.e(TAG, "start probe when connect wifi");
                            probeVBox();
                        }
                    }
                    if (mIsNeedTcp) {
                        Log.e(TAG, "send MSG_GET_BOX_INFO_TIMEOUT");
                        mHandler.sendEmptyMessageDelayed(MSG_GET_BOX_INFO_TIMEOUT, 60000);
                        //广播探测包
                        //                        probeVBox();
                    }
                    break;
                case WifiUtil.MSG_CONNECT_WIFI_TIMEOUT:
                    notifyNetConfigError(CommonConst.ConfigError.CONNECT_WIFI_FAILED);
                    break;
                case MSG_PROBE_BOX:
                    if (!mIsInProbe) {
                        Log.e(TAG, "stop execute because finish probe");
                        break;
                    }
                    if (mProbeTimes > 5) {
                        Log.e(TAG, "has probe " + mProbeTimes + " times!!!");
                        // TODO: 2019/1/21 探测超时！！！

                    }
                    mProbeTimes += 1;
                    String boxProbeString = (String) msg.obj;
                    int ipAddress = DeviceUtil.getIpAddress(mContext);
                    Log.e(TAG, "ipAddress: " + ipAddress);
                    String broadcastIP = DeviceUtil.intIPToStringBroadcastIP(ipAddress);
                    Log.e(TAG, "broadcastIP: " + broadcastIP);
                    //                    SocketUtil.getInstance(mContext).sendMsgByUdp(boxProbeString, broadcastIP,
                    //                            CommonConst.PROBE_BOX_PORT);
                    // TODO: 2019/9/9 改为组播，ip、端口变化
                    SocketUtil.getInstance(mContext).sendMsgByUdp(boxProbeString,
                            CommonConst.MULTI_CAST_IP, 8001);
                    Log.e(TAG, "probe vbox in handler");
                    Message message = mHandler.obtainMessage(MSG_PROBE_BOX, boxProbeString);
                    mHandler.sendMessageDelayed(message, 10000);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public void startUdpTimeOutCountdown() {
        if (mHandler.hasMessages(MSG_PUB_AP_RECEIVE_TIMEOUT)) {
            mHandler.removeMessages(MSG_PUB_AP_RECEIVE_TIMEOUT);
        }
        mHandler.sendEmptyMessageDelayed(MSG_PUB_AP_RECEIVE_TIMEOUT, CommonConst.ONE_SECOND * 70);
    }

    private NetConfigBusiness() {
        mListeners = new ArrayList<>();
    }

    public static NetConfigBusiness getInstance() {
        if (instance == null) {
            synchronized (NetConfigBusiness.class) {
                if (instance == null) {
                    instance = new NetConfigBusiness();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;
        //注册handler
        WifiAPManager.getInstance(context).registerHandler(mHandler);
        WifiUtil.getInstance(context).registerHandler(mHandler);
    }

    public void updateContext(Context context) {
        mContext = context;
    }

    public void restoreState(boolean isNeedReconnect) {
        Log.e(TAG, "restore state: " + isNeedReconnect);
        mHandler.removeCallbacksAndMessages(null);
        if (mHandler.hasMessages(MSG_PROBE_BOX)) {
            mHandler.removeMessages(MSG_PROBE_BOX);
        }
        mProbeTimes = 0;
        SocketUtil.getInstance(mContext).stopUdpSocket();
        //        SocketUtil.getInstance(mContext).stopTcpSocket();
        if (WifiAPManager.getInstance(mContext).isAPEnable()) {
            WifiAPManager.getInstance(mContext).closeWifiAp();
        }
        if (isNeedReconnect) {
            reconnectWifi();
        }
        mIsNeedUdpTimeOut = false;
    }

    public boolean startNetConfig(int homeId) {
        Log.e(TAG, "startNetConfig");
        restoreState(false);
        //        PreferenceUtil.setPreferenceValue(mContext, PreferenceUtil.BLOCKED_IP, "");
        Log.e(TAG, "remove blocked ip");
        //获取当前wifi信息
        // TODO: 2019/5/21 需要适配网络无密码的情况
        mNetInfo = WifiUtil.getInstance(mContext).getWifiInfo(mContext);
        if (mNetInfo.isWifiConnected() && TextUtils.isEmpty(mNetInfo.getPassWord())) {
            notifyNetConfigError(CommonConst.ConfigError.WIFI_PASSWORD_EMPTY);
            return false;
        }
        mHomeId = homeId;
        Log.e(TAG, "wifi info: " + mNetInfo.toString());
        //开启ap
        Log.e(TAG, "open hotspot");
        mIsPublicAp = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean turnOnWifiAp = WifiAPManager.getInstance(mContext).turnOnWifiAp(
                        "Fotile_Voice", "fotilelife",
                        WifiAPManager.WifiSecurityType.WIFICIPHER_WPA2);
                Log.e(TAG, "turnOnWifiAp: " + turnOnWifiAp);
            }
        });
        //        boolean turnOnWifiAp = WifiAPManager.getInstance(mContext).turnOnWifiAp("Fotile_Voice",
        //                "fotilelife", WifiAPManager.WifiSecurityType.WIFICIPHER_WPA2);
        //        Log.e(TAG, "turnOnWifiAp: " + turnOnWifiAp);
        mHandler.sendEmptyMessageDelayed(MSG_AP_CREATE_FAILED, 30000);
        return true;
    }

    private OnUdpEventListener mMessageReceiveListener = new OnUdpEventListener() {
        @Override
        public void onMessageReceived(DatagramPacket message) {
            handleUdpMsg(message);
        }

        @Override
        public void onTimeOut() {
            // TODO: 2019/1/3 超时
            Log.e(TAG, "udp timeout");
            if (mHandler.hasMessages(MSG_PUB_AP_RECEIVE_TIMEOUT)) {
                mHandler.removeMessages(MSG_PUB_AP_RECEIVE_TIMEOUT);
            }
            if (mIsNeedUdpTimeOut) {
                restoreState(true);
            }
            notifyNetConfigError(CommonConst.ConfigError.UDP_RECEIVE_TIMEOUT);
        }
    };

    private void handleUdpMsg(DatagramPacket message) {
        // TODO: 2018/12/28 回应盒子消息
        String strReceive = new String(message.getData(), message.getOffset(), message.getLength());
        Log.e(TAG,
                "udp msg: " + strReceive + ", " + message.getAddress() + ", " + message.getPort());
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(strReceive);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            if (jsonObject.has("type")) {
                switch (jsonObject.optString("type")) {
                    case CommonConst.REQUEST_INFO:
                        if (mHandler.hasMessages(MSG_PUB_AP_RECEIVE_TIMEOUT)) {
                            mHandler.removeMessages(MSG_PUB_AP_RECEIVE_TIMEOUT);
                        }
                        Log.e("udp", "send msg");
                        mSsid = mNetInfo.isWifiConnected()
                                ? mNetInfo.getSsid()
                                : ("Fotile_Voice_" + DeviceUtil.getRandomString(4));
                        mPassWord = mNetInfo.isWifiConnected()
                                    ? mNetInfo.getPassWord()
                                    : DeviceUtil.getRandomString(8);
                        ResponseInfo responseInfo = new ResponseInfo(CommonConst.RESPONSE_INFO,
                                mNetInfo.isWifiConnected() ? "yes" : "no", mHomeId, mSsid,
                                mPassWord, mNetInfo.getSecurity(), jsonObject.optString("session"));
                        //回网络信息
                        String msg = JSON.toJSONString(responseInfo);
                        SocketUtil.getInstance(mContext).sendMsgByUdp(msg, message.getAddress(),
                                message.getPort());
                        break;
                    case CommonConst.REQUEST_DISCONNECT:
                        Log.e(TAG, "REQUEST_DISCONNECT, close socket, close ap");
                        //请求断开连接
                        SocketUtil.getInstance(mContext).stopUdpSocket();
                        WifiAPManager.getInstance(mContext).closeWifiAp();
                        if (mNetInfo.isWifiConnected()) {
                            //连接家庭路由
                            WifiUtil.getInstance(mContext).changeToWifi(mNetInfo.getSsid(),
                                    mNetInfo.getPassWord());
                            mIsNeedTcp = true;
                        } else {
                            //开启ap热点
                            mHandler.sendEmptyMessageDelayed(MSG_AP_CREATE, 2000);
                        }
                        mIsNeedUdpTimeOut = true;
                        notifyNetConfigStateChange(CommonConst.ConfigState.CONNECT_ACTUALLY_NET);
                        break;
                    case CommonConst.DEVICE_PROBE:
                        mBoxNetInfo = JSON.parseObject(strReceive, BoxNetInfo.class);
                        /*Log.e(TAG, "boxip: " + mBoxNetInfo.getIp() + ", blocked ip: " +
                                   PreferenceUtil
                                           .getPreferenceValue(mContext, PreferenceUtil.BLOCKED_IP,
                                                   ""));*/
                        /*if (TextUtils.equals(mBoxNetInfo.getIp(), (String) PreferenceUtil
                                .getPreferenceValue(mContext, PreferenceUtil.BLOCKED_IP, ""))) {
                            return;
                        }*/
                        if (!matchHomeId(mHomeId, mBoxNetInfo.getHome_id())) {//若homeId不匹配
                            if (mHandler.hasMessages(MSG_GET_BOX_INFO_TIMEOUT)) {//若处于主动配网流程中
                                notifyNetConfigError(CommonConst.ConfigError.ILLEGAL_HOME_ID);
                            }
                            Log.e(TAG, "home id not match");
                            return;
                        }
                        notifyNetConfigStateChange(CommonConst.ConfigState.START_TCP);
                        if (mHandler.hasMessages(MSG_PROBE_BOX)) {
                            mHandler.removeMessages(MSG_PROBE_BOX);
                            mIsNeedUdpTimeOut = false;
                        }
                        if (mHandler.hasMessages(MSG_GET_BOX_INFO_TIMEOUT)) {
                            mHandler.removeMessages(MSG_GET_BOX_INFO_TIMEOUT);
                            Log.e(TAG, "remove MSG_GET_BOX_INFO_TIMEOUT because start tcp");
                        }
                        SocketUtil.getInstance(mContext).stopUdpSocket();
                        mProbeTimes = 0;
                        mIsInProbe = false;
                        if (isConnectToBox()) {
                            notifyNetConfigError(CommonConst.ConfigError.BOX_CONNECTING);
                            SocketUtil.getInstance(mContext).stopUdpSocket();
                            return;
                        }
                        if (!TextUtils.isEmpty(mBoxNetInfo.getSession())) {
                            mSession = mBoxNetInfo.getSession();
                        }
                        //若homeid合规则继续
                        //                        if (matchHomeId(mHomeId, mBoxNetInfo.getHome_id())) {
                        SocketUtil.getInstance(mContext).startTcpSocket(mBoxNetInfo.getIp(),
                                mBoxNetInfo.getPort(), mTcpClientListener);
                        //                        }
                        SocketUtil.getInstance(mContext).stopUdpSocket();
                        break;
                    default:
                        break;
                }
            } else {
                Log.e(TAG, "not voice box");
            }
        }
    }

    private TcpClientListener mTcpClientListener = new TcpClientListener() {
        @Override
        public void onConnected(TcpClient client) {
            //request join
            JoinRequest joinRequest = new JoinRequest(CommonConst.REQUEST_JOIN, mHomeId,
                    CommonConst.HOOD_DEVICE_TPYE, mBoxNetInfo.getSession(), 0, "御厨套系-烟机-Z1TA1.5",
                    true, true, true, DeviceUtil.getMac(mContext));
            SocketUtil.getInstance(mContext).sendMsgByTcp(JSON.toJSONString(joinRequest));
        }

        @Override
        public void onSent(TcpClient client, TcpMsg tcpMsg) {
            if (!TextUtils.isEmpty(tcpMsg.getSourceDataString()) &&
                !tcpMsg.getSourceDataString().contains("request_heartbeat")) {
                Log.e(TAG, "onSent, client: " + client + ", tcpMsg: " + tcpMsg);
            }
        }

        @Override
        public void onDisconnected(TcpClient client, String msg, Exception e) {
            Log.e(TAG, "tcp disconnected: " + e + ", is box connected: " + mIsBoxConnected +
                       ", leave state: " + mLeaveStatues);
            notifyNetConfigError(CommonConst.ConfigError.CONNECT_BOX_FAILED);
            if (mIsBoxConnected) {
                notifyNetConfigStateChange(CommonConst.ConfigState.BOX_DISCONNECTED);
                mIsBoxConnected = false;
            } else {
                if (mLeaveStatues != LeaveStatues.ACTIVELY_LEAVE) {
                    restoreState(false);
                    Log.e(TAG, "start probe when tcp disconnected");
                    probeVBox();
                }
            }
        }

        @Override
        public void onReceive(TcpClient client, TcpMsg tcpMsg) {
            handleTcpMsg(tcpMsg.getSourceDataString());
        }

        @Override
        public void onValidationFail(TcpClient client, TcpMsg tcpMsg) {

        }

        @Override
        public void onPingTimeout(TcpClient client) {
            Log.e(TAG, "heartbeat disconnect: " + client);
            notifyNetConfigError(CommonConst.ConfigError.HEARTBEAT_TIMEOUT);
        }
    };

    private void handleTcpMsg(String sourceDataString) {
        Log.e(TAG, "tcp source data string: " + sourceDataString);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(sourceDataString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null && jsonObject.has("type")) {
            switch (jsonObject.optString("type")) {
                case CommonConst.RESPONSE_JOIN:
                    ResponseJoin responseJoin = JSON.parseObject(sourceDataString,
                            ResponseJoin.class);
                    if (responseJoin != null) {
                        Log.e(TAG,
                                "responseJoin: " + responseJoin + ", " + responseJoin.isConnect() +
                                ", " + responseJoin.getStatues());
                        if (!responseJoin.isConnect()) {
                            Log.e(TAG, "connect fail because box refused");
                            notifyNetConfigError(CommonConst.ConfigError.CONNECT_BOX_FAILED);
                        } else {
                            //连接成功，确定状态
                            Log.e(TAG, "response join, stop udp, start tcp heartbeat");
                            notifyNetConfigStateChange(CommonConst.ConfigState.CONNECT_BOX_SUCCESS);
                            SocketUtil.getInstance(mContext).stopUdpSocket();
                            SocketUtil.getInstance(mContext).startTcpHeartBeat();
                            mIsBoxConnected = true;
                        }
                    } else {
                        Log.e(TAG, "illegal response!!!");
                    }
                    break;
                case CommonConst.HOME_ID_GET:
                    int boxHomeId = jsonObject.optInt("home_id");
                    if (matchHomeId(mNewHomeId, boxHomeId)) {
                        Log.e(TAG, "home id matched, do nothing");
                    } else {
                        Log.e(TAG,
                                "box home id: " + boxHomeId + ", machine home id: " + mNewHomeId +
                                ", disconnect");
                        requestLeave(false);
                    }
                    break;
                case CommonConst.RESPONSE_LEAVE:
                    Log.e(TAG, "RESPONSE_LEAVE, close socket, close ap");
                    /*String targetIp = SocketUtil.getInstance(mContext).getTargetIp();
                    if (!TextUtils.isEmpty(targetIp)) {
                        PreferenceUtil.setPreferenceValue(mContext, PreferenceUtil.BLOCKED_IP,
                                targetIp);
                        Log.e(TAG, "set blocked ip: " + targetIp);
                    }*/
                    SocketUtil.getInstance(mContext).stopTcpSocket();
                    if (WifiAPManager.getInstance(mContext).isAPEnable()) {
                        WifiAPManager.getInstance(mContext).closeWifiAp();
                    }
                    // TODO: 2019/5/27 udp接收端过滤盒子ip
                    //                    if (mIsLeaveActively) {
                    if (mLeaveStatues == LeaveStatues.ACTIVELY_LEAVE) {
                        notifyNetConfigStateChange(CommonConst.ConfigState.BOX_ACTIVELY_DISCONNECT);
                    } else if (mLeaveStatues == LeaveStatues.HOME_ID_ILLEGAL) {
                        notifyNetConfigStateChange(CommonConst.ConfigState.BOX_DISCONNECT);
                    }
                    mIsBoxConnected = false;
                    break;
                case CommonConst.CONTROL:
                    notifyNlpResultGet(jsonObject);
                    break;
                case CommonConst.TEXT:
                    notifyDialogContentGet(jsonObject);
                    break;
                case CommonConst.VBOX_STATUS:
                    String status = jsonObject.optString("status");
                    notifyDialogStateChange(status);
                    break;
                case CommonConst.VBOX_ERROR:
                    String vboxError = jsonObject.optString("error");
                    notifyVboxError(vboxError);
                    break;
                case CommonConst.MUSIC:
                    String command = jsonObject.optString("command");
                    if (!TextUtils.isEmpty(command)) {
                        notifyMusicStop();
                    }
                    String musicListString = jsonObject.optString("music_list");
                    Log.e(TAG, "music list json string: " + musicListString);
                    if (!TextUtils.isEmpty(musicListString)) {
                        notifyMusicStart(musicListString);
                    }
                    break;
                case CommonConst.COOK_DETAIL:
                    String recipes = jsonObject.optString("detail");
                    if (!TextUtils.isEmpty(recipes)) {
                        notifyRecipesDisplay(recipes);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isConnectToBox() {
        return false;
    }

    private boolean matchHomeId(int deviceHomeId, int boxHomeId) {
        return deviceHomeId == 0 || boxHomeId == 0 || deviceHomeId == boxHomeId;
    }

    private void reconnectWifi() {
        Log.e(TAG, "mNetInfo: " + mNetInfo + ", is wifi connected: " +
                   (mNetInfo == null ? null : mNetInfo.isWifiConnected()));
        if (mNetInfo != null && mNetInfo.isWifiConnected()) {
            WifiUtil.getInstance(mContext).changeToWifi(mNetInfo.getSsid(), mNetInfo.getPassWord());
            Log.e(TAG, "start link time: " + System.currentTimeMillis());
            mIsNeedTcp = false;
        }
    }

    public void registerListener(NetConfigStateChangeListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void unRegisterListener(NetConfigStateChangeListener listener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }

    public void notifyNetConfigStateChange(CommonConst.ConfigState state) {
        for (NetConfigStateChangeListener listener : mListeners) {
            listener.onNetConfigStateChange(state);
        }
    }

    public void notifyNetConfigError(CommonConst.ConfigError error) {
        for (NetConfigStateChangeListener listener : mListeners) {
            listener.onNetConfigException(error);
        }
    }

    private void notifyNlpResultGet(JSONObject jsonObject) {
        for (NetConfigStateChangeListener listener : mListeners) {
            //判断是否快速命令词
            if (!jsonObject.optBoolean("online")) {
                listener.onHeard(CommonConst.TYPE_LOCAL_COMMAND,
                        CommonConst.getCommandAsr(jsonObject.optString("command")));
            }
            mNluResult = JSON.parseObject(jsonObject.toString(), NluResult.class);
            if (mNluResult != null) {
                listener.onUnderStand(mNluResult);
                Log.e(TAG, "onUnderStand");
            } else {
                Log.e(TAG, "can't parse the nlu result!!!!");
            }
        }
    }

    private void notifyDialogContentGet(JSONObject jsonObject) {
        for (NetConfigStateChangeListener listener : mListeners) {
            if (jsonObject.has("category")) {
                switch (jsonObject.optString("category")) {
                    case "asr":
                        listener.onHeard(CommonConst.TYPE_ONLINE_SPEAK,
                                jsonObject.optString("content"));
                        break;
                    case "wakeup":
                        listener.onWakeUp(jsonObject.optString("content"));
                        break;
                    case "tts":
                    case "no_voice":
                        listener.onSpeak(jsonObject.optString("content"));
                        break;
                    case "boardcast":

                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void notifyDialogStateChange(String state) {
        for (NetConfigStateChangeListener listener : mListeners) {
            try {
                listener.onDialogStateChange(CommonConst.DialogState.valueOf(state));
            } catch (Exception e) {
                Log.e(TAG, "the state is not preset" + state);
                e.printStackTrace();
            }
        }
    }

    private void notifyVboxError(String error) {
        for (NetConfigStateChangeListener listener : mListeners) {
            listener.onVboxError(error);
        }
    }

    private void notifyMusicStart(String musicListJson) {
        for (NetConfigStateChangeListener listener : mListeners) {
            listener.onMusicStart(musicListJson);
        }
    }

    private void notifyMusicStop() {
        for (NetConfigStateChangeListener listener : mListeners) {
            listener.onMusicStop();
        }
    }

    private void notifyRecipesDisplay(String recipes) {
        for (NetConfigStateChangeListener listener : mListeners) {
            listener.onRecipesGet(recipes);
        }
    }

/*    private void notifyNoVoice(String content) {
        for (NetConfigStateChangeListener listener : mListeners) {
            listener.onNoVoice(content);
        }
    }

    private void notifyDialogEnd(SleepBean sleepBean) {
        for (NetConfigStateChangeListener listener : mListeners) {
            listener.onSleep(sleepBean.getReason(), sleepBean.getErrorInfo());
        }
    }*/

    //    设备由未绑定切换为绑定状态时，设备向盒子发送tcp端口8002
    //｛
    //“type”:”get_home_id”
    //｝
    //盒子收到后，回复
    //｛
    //“type”:”home_id_get”
    //“home_id”:xxx
    //｝
    //
    //1、若收到的home_id和现在的home_id相同，则维持现状
    //2、若收到的home_id和现在的home_id不相同，则发送断开连接请求
    public boolean changeHomeId(int homeId) {
        mNewHomeId = homeId;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "get_home_id");
            jsonObject.put("home_id", mNewHomeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return SocketUtil.getInstance(mContext).sendMsgByTcp(jsonObject.toString());
    }

    //设备发送离开请求tcp端口8002
    //｛
    //	“type”:” request_leave”,
    //	“session”:”vbox provide id”
    //｝
    //盒子回复
    //{
    //	“type”:” response_leave”,
    //	“session”:”vbox provide id”
    //}
    //盒子发送完回复后，关闭对应连接，
    //设备收到回复后关闭对应连接。
    public boolean requestLeave(boolean isLeaveActively) {
        // TODO: 2019/1/12
        setLeaveState(isLeaveActively);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "request_leave");
            jsonObject.put("session", mSession);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return SocketUtil.getInstance(mContext).sendMsgByTcp(jsonObject.toString());
    }

    public void setLeaveState(boolean isLeaveActively) {
        mIsLeaveActively = isLeaveActively;
        setStatues(isLeaveActively ? LeaveStatues.ACTIVELY_LEAVE : LeaveStatues.HOME_ID_ILLEGAL);
        if (SocketUtil.getInstance(mContext).hasTcpConnection()) {
            /*String targetIp = SocketUtil.getInstance(mContext).getTargetIp();
            if (!TextUtils.isEmpty(targetIp)) {
                PreferenceUtil.setPreferenceValue(mContext, PreferenceUtil.BLOCKED_IP, targetIp);
                Log.e(TAG, "set blocked ip: " + targetIp);
            }*/
        }
    }

    public void probeVBox() {
        // TODO: 2019/9/9 改为组播，端口都为8001？
        SocketUtil.getInstance(mContext).startMultiCastServer(CommonConst.MULTI_CAST_IP,
                CommonConst.BOX_PROBE_PORT, mMessageReceiveListener, 0);
        //        SocketUtil.getInstance(mContext).startUdpServer(CommonConst.BOX_PROBE_PORT,
        //                mMessageReceiveListener, 60000);
        // TODO: 2019/1/12
        JSONObject boxProbe = new JSONObject();
        try {
            boxProbe.put("type", "vbox_probe");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        Log.e(TAG, "boxProbe: " + boxProbe);
        String boxProbeString = boxProbe.toString();
        Log.e(TAG, "boxProbeString: " + boxProbeString);
        int ipAddress = DeviceUtil.getIpAddress(mContext);
        Log.e(TAG, "ipAddress: " + ipAddress);
        String broadcastIP = DeviceUtil.intIPToStringBroadcastIP(ipAddress);
        Log.e(TAG, "broadcastIP: " + broadcastIP);
        // TODO: 2019/9/9 改为组播，ip、端口变化
        SocketUtil.getInstance(mContext).sendMsgByUdp(boxProbeString, CommonConst.MULTI_CAST_IP,
                8001);
        Log.e(TAG, "probe vbox");
        Message message = mHandler.obtainMessage(MSG_PROBE_BOX, boxProbeString);
        mHandler.sendMessageDelayed(message, 10000);
        mProbeTimes += 1;
        mIsInProbe = true;
    }

    public void stopProbe() {
        if (mHandler.hasMessages(MSG_PROBE_BOX)) {
            mHandler.removeMessages(MSG_PROBE_BOX);
        }
        if (mHandler.hasMessages(MSG_GET_BOX_INFO_TIMEOUT)) {
            mHandler.removeMessages(MSG_GET_BOX_INFO_TIMEOUT);
        }
        SocketUtil.getInstance(mContext).stopUdpSocket();
        mProbeTimes = 0;
        mIsInProbe = false;
    }

    public boolean manualDialogControl(boolean dialogState) {
        JsonObject dialogControl = new JsonObject();
        dialogControl.addProperty("type", "wakeup");
        dialogControl.addProperty("command", dialogState ? "on" : "off");
        return SocketUtil.getInstance(mContext).sendMsgByTcp(new Gson().toJson(dialogControl));
    }

    //发送主动播报内容
    //烟机暂时不涉及
    public boolean broadcast(String machineState) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "broadcast");
        jsonObject.addProperty("level", 3);
        jsonObject.addProperty("content", machineState);
        return SocketUtil.getInstance(mContext).sendMsgByTcp(new Gson().toJson(jsonObject));
    }

    public SelfNetInfo getNetInfo() {
        return mNetInfo;
    }

    //    public void powerOff() {
    //        notifyNetConfigStateChange(CommonConst.ConfigState.BOX_POWER_OFF);
    //    }

    public void setStatues(LeaveStatues statues) {
        mLeaveStatues = statues;
        Log.e(TAG, "leave statues: " + statues);
    }

    public enum LeaveStatues {
        HOME_ID_ILLEGAL, ACTIVELY_LEAVE, ACTIVELY_POWER_OFF
    }
}
