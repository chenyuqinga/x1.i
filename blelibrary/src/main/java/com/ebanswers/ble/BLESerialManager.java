package com.ebanswers.ble;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Keep;
import android.text.TextUtils;
import android.util.Log;

import com.ebanswers.ble.listener.BLEConnectListener;
import com.ebanswers.ble.listener.BLEPhoneListener;
import com.ebanswers.ble.listener.BLEScanListener;
import com.ebanswers.ble.listener.BLESongListener;
import com.ebanswers.ble.listener.BleQueryListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_serialport_api.SerialUtil;


/**
 * Description
 * Created by chenqiao on 2016/8/31.
 */
@Keep
public class BLESerialManager {
    public static final int STATUS_INIT = 1;
    public static final int STATUS_CONNECTING = 2;
    public static final int STATUS_CONNECTED = 3;
    public static final int STATUS_DIALING = 4;
    public static final int STATUS_COMING = 5;
    public static final int STATUS_CALLING = 6;

    private static BLESerialManager instance;
    private SerialUtil serialUtil;
    private ExecutorService sendPool;
    private Thread readThread;
    private BLEScanListener listener;
    private BLEConnectListener connectListener;
    private BLEPhoneListener phoneListener;
    private BleQueryListener queryListener;
    private BLESongListener songListener;

    public static String connected_mac = "";
    public static String connected_name = "";

    private int pre_num = 0;
    private Handler handler;

    public static BLESerialManager getInstance() {
        if (instance == null) {
            synchronized (BLESerialManager.class) {
                if (instance == null) {
                    instance = new BLESerialManager();
                }
            }
        }
        return instance;
    }

    private BLESerialManager() {
        handler = new Handler(Looper.getMainLooper());
        serialUtil = new SerialUtil("/dev/ttyS0", 115200, 0);
        sendPool = Executors.newSingleThreadExecutor();
        new Thread(new Runnable() {
            @Override
            public void run() {
                serialUtil.openSerial();
                initRead();
            }
        }).start();
    }

    public void setScanListener(BLEScanListener listener) {
        this.listener = listener;
    }

    public void setDeviceConnectListener(BLEConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public void setPhoneListener(BLEPhoneListener phoneListener) {
        this.phoneListener = phoneListener;
    }

    public void setQueryListener(BleQueryListener queryListener) {
        this.queryListener = queryListener;
    }

    public void setSongListener(BLESongListener songListener) {
        this.songListener = songListener;
    }

    private void initRead() {
        if (serialUtil != null && serialUtil.isOpen) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(serialUtil.mInputStream));
            if (readThread != null) {
                readThread.interrupt();
                readThread = null;
            }
            readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String temp;
                    while (readThread != null && !readThread.isInterrupted()) {
                        try {
                            while (reader.ready() && (temp = reader.readLine()) != null) {
                                Log.d("BLESerialManager","run: readString=" + temp);
                                Thread.sleep(20);
                                dealWith(temp);
                            }
                            Thread.sleep(100);
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            });
            readThread.start();
        }
    }

    //在异步线程处理，注意UI更新
    private void dealWith(String temp) {
        try {
            if (temp.startsWith(BLEConstants.OK)) {
                String action = temp.substring(2);
                if (action.startsWith(BLEConstants.OK_STATUS_OPEN)) {
                    Log.d("BLESerialManager", "dealWith: open");
                } else if (action.startsWith(BLEConstants.OK_STATUS_CLOSE)) {
                    Log.d("BLESerialManager", "dealWith: close");
                } else if (action.startsWith(BLEConstants.OK_STATUS_START_SCAN)) {
                    Log.d("BLESerialManager", "dealWith: start scan");
                } else if (action.startsWith(BLEConstants.OK_STATUS_STOP_SCAN)) {
                    Log.d("BLESerialManager", "dealWith: stop scan");
                    if (listener != null) {
                        listener.onScanStop();
                    }
                } else if (action.startsWith(BLEConstants.OK_STATUS_DISCONNECT)) {
                    Log.d("BLESerialManager","dealWith: disconnect");
                } else if (action.startsWith(BLEConstants.OK_STATUS_PLAY_NEXT)) {
                    Log.d("BLESerialManager", "dealWith: play_next");
                } else if (action.startsWith(BLEConstants.OK_STATUS_PLAY_PRE)) {
                    Log.d("BLESerialManager", "dealWith: play_pre");
                } else if (action.startsWith(BLEConstants.OK_STATUS_PLAY_STOP)) {
                    Log.d("BLESerialManager", "dealWith: play_stop");

                } else if (action.startsWith(BLEConstants.OK_STATUS_REFUSE_PHONE)) {
                    Log.d("BLESerialManager", "dealWith: refuse phone");
                    if (phoneListener != null) {
                        phoneListener.refuseOk();
                    }
                } else if (action.startsWith(BLEConstants.OK_STATUS_ACCEPT_PHONE)) {
                    Log.d("BLESerialManager", "dealWith: accept phone");
                    if (phoneListener != null) {
                        phoneListener.acceptOk();
                    }
                } else if (action.startsWith(BLEConstants.OK_STATUS_HUNGUP_PHONE)) {
                    Log.d("BLESerialManager", "dealWith: hung up phone");
                    if (phoneListener != null) {
                        phoneListener.hungUpOk();
                    }
                }
            } else if (temp.startsWith(BLEConstants.CONNECT_CODE)) {
                //TODO 拿到配对码MN123456
                Log.d("BLESerialManager", "get connect code");
            } else if (temp.startsWith(BLEConstants.DEVICE_DISCONNECT)) {
                Log.d("BLESerialManager", "device has disconnected ");
                connected_name = "";
                connected_mac = "";
                if (connectListener != null) {
                    connectListener.deviceDisconnected();
                }
                //通知所有ui，设备连接断开
            } else if (temp.startsWith(BLEConstants.STOP_SCAN)) {
                Log.d("BLESerialManager", "scan has stopped");
                if (listener != null) {
                    listener.onScanStop();
                }
            } else if (temp.startsWith(BLEConstants.DEVICE_HEADER)) {
                int offset = 0;
                if (pre_num > 9) {
                    offset = 1;
                    pre_num = Integer.valueOf(temp.substring(2, 4));
                } else {
                    pre_num = Integer.valueOf(temp.substring(2, 3));
                }
                String address = temp.substring(3 + offset, 15 + offset);
                String name = temp.substring(15 + offset);
               BLEDevice device = new BLEDevice(address, name);
                Log.d("BLESerialManager", "find: "+name+"," + address + "," + connected_mac);
                if (TextUtils.equals(address, connected_mac) && TextUtils.equals(name, connected_name)) {
                    device.setStatus(BLEDevice.STATUS_CONNECTED);
                    Log.d("BLESerialManager", "dealWith: STATUS_CONNECTED");
                } else {
                    device.setStatus(BLEDevice.STATUS_NORMAL);
                    Log.d("BLESerialManager", "dealWith: STATUS_NORMAL");
                }
                if (listener != null) {
                    listener.onAddADevice(device);
                }
            } else if (temp.startsWith(BLEConstants.DEVICE_CONNECTED_MAC)) {
                //TODO 连接上的设备的mac地址
                connected_mac = temp.substring(2);
                handler.removeCallbacks(queryCurrentConnectDevice);
                Log.d("BLESerialManager", "connect device mac:" + connected_mac);
            } else if (temp.startsWith(BLEConstants.DEVICE_CONNECTED_NAME)) {
                //TODO 连接上的设备的名称
                connected_name = temp.substring(2);
                if (connectListener != null && !TextUtils.isEmpty(connected_name) && !TextUtils.isEmpty(connected_name)) {
                    connectListener.deviceConnected(connected_mac,connected_name);
                }
                Log.d("BLESerialManager", "connect device name:" + connected_name);
            } else if (temp.startsWith(BLEConstants.SONG_NAME_HEADER)) {
                BLESong.name = temp.substring(2);
                Log.d("BLESerialManager", "play song name:" + BLESong.name);
            } else if (temp.startsWith(BLEConstants.SONG_SINGER_HEADER)) {
                BLESong.singer = temp.substring(2);
                Log.d("BLESerialManager", "play song singer:" + BLESong.singer);
            } else if (temp.startsWith(BLEConstants.SONG_ALBUM_HEADER)) {
                BLESong.album = temp.substring(2);
                Log.d("BLESerialManager", "play song album:" + BLESong.album);
            } else if (temp.startsWith(BLEConstants.SONG_INDEX)) {
                if (!TextUtils.isEmpty(temp.replace(" ", "").substring(2))) {
                    BLESong.index = Integer.valueOf(temp.substring(2));
                    Log.d("BLESerialManager", "play song index:" + BLESong.index);
                }
            } else if (temp.startsWith(BLEConstants.SONG_NUMS)) {
                if (!TextUtils.isEmpty(temp.replace(" ", "").substring(2))) {
                    BLESong.nums = Integer.valueOf(temp.substring(2));
                    Log.d("BLESerialManager", "play song nums:" + BLESong.nums);
                }
            } else if (temp.startsWith(BLEConstants.SONG_ALL_TIME)) {

            } else if (temp.startsWith(BLEConstants.SONG_NOW_TIME)) {

            } else if (temp.startsWith(BLEConstants.SONG_PLAY)) {
                Log.d("BLESerialManager", "dealWith: song play" + "," + connected_mac + "," + connected_name);
                if (songListener != null && !TextUtils.isEmpty(connected_mac) && !TextUtils.isEmpty(connected_name)) {
                    songListener.onSongPlay();
                }
            } else if (temp.startsWith(BLEConstants.SONG_PAUSE)) {
                Log.d("BLESerialManager", "dealWith: song pause");
                if (songListener != null && !TextUtils.isEmpty(connected_mac) && !TextUtils.isEmpty(connected_name)) {
                    songListener.onSongPause();
                }
            } else if (temp.startsWith(BLEConstants.PHONE_ID)) {
                String phoneNum = temp.substring(2);
                Log.d("BLESerialManager", "phone call from:" + phoneNum);
                if (phoneListener != null) {
                    phoneListener.onPhoneFrom(phoneNum);
                }
            } else if (temp.startsWith(BLEConstants.PHONE_IR)) {
                String phoneNum = temp.substring(2);
                Log.d("BLESerialManager", "phone calling:" + phoneNum);
                if (phoneListener != null) {
                    phoneListener.onPhoneNum(phoneNum);
                }
            } else if (temp.startsWith(BLEConstants.PHONE_IG)) {
                Log.d("BLESerialManager", "phone accept");
                if (phoneListener != null) {
                    phoneListener.onPhoneAccepted();
                }
            } else if (temp.startsWith(BLEConstants.PHONE_IF)) {
                Log.d("BLESerialManager", "phone hung up");
                if (phoneListener != null) {
                    phoneListener.onPhoneHungUp();
                }
            } else if (temp.startsWith(BLEConstants.PHONE_IC)) {
                Log.d("BLESerialManager", "phone call to");
                if (phoneListener != null) {
                    phoneListener.onPhoneTo();
                }
            } else if (temp.startsWith(BLEConstants.OK_STATUS_QUERY)) {
                if (!TextUtils.isEmpty(temp.substring(2)) && queryListener != null) {
                    queryListener.getBleStatus(Integer.parseInt(temp.substring(2)));
                    Log.d("BLESerialManager", "query status:" + temp.substring(2));
                }
            } else if (temp.startsWith(BLEConstants.OK_STATUS_PAIR)) {
                if (!TextUtils.isEmpty(temp.substring(3))) {
                    String mac = temp.substring(3, 15);
                    if (queryListener != null && !TextUtils.isEmpty(mac)) {
                        queryListener.getLatestPairedDevice(mac);
                    }
                    Log.d("BLESerialManager", "pair device mac: " + mac);
                }
            } else if (temp.startsWith(BLEConstants.DEVICE_NAME)) {
                if (!TextUtils.isEmpty(temp.substring(2))) {
                    String name = temp.substring(2);
                    if (queryListener != null) {
                        queryListener.getBleName(name);
                    }
                    Log.d("BLESerialManager", "ble name: " + name);
                }
            }else if(temp.equals(BLEConstants.OK_STATUS_CONNECT_SUCCESS)){
                //蓝牙重新打开,与手机连接成功,只能收到IB, 收不到JH,需要重新查询
                //400ms后还没收到JH就查询, 收到JH取消查询, 防止收到两次JH SA
                Log.d("BLESerialManager", "dealWith: connected,get mac and name");
                handler.postDelayed(queryCurrentConnectDevice, 400);
            }
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private Runnable queryCurrentConnectDevice = new Runnable() {
        @Override
        public void run() {
            sendCommand(BLEConstants.CMD_QUERY_CURRENT_MAC.getBytes());
            sendCommand(BLEConstants.CMD_QUERY_CURRENT_NAME.getBytes());
        }
    };

    public void enableBle(boolean enable) {
        Log.d("BLESerialManager", "enableBle: " + enable);
        if (enable) {
            sendCommand(BLEConstants.CMD_ENABLE_BLE.getBytes());
        } else {
            sendCommand(BLEConstants.CMD_DISABLE_BLE.getBytes());
            connected_name = "";
            connected_mac = "";
        }
    }

    public void scan() {
        pre_num = 0;
//        scanResults.clear();
        Log.d("BLESerialManager", "scan: scanResults clear");
        sendCommand(BLEConstants.CMD_SEARCH.getBytes());
    }

    public void stop_scan() {
        sendCommand(BLEConstants.CMD_STOP_SEARCH.getBytes());
    }

    //修改蓝牙配对密码
    public void setPairCode(String code) {
        String cmd = String.format(Locale.getDefault(), BLEConstants.CMD_SET_PAIR_CODE, code);
        sendCommand(cmd.getBytes());
    }

    public void connectDevice(String mac) {
        String cmd = String.format(Locale.getDefault(), BLEConstants.CMD_CONNECT, mac);
        sendCommand(cmd.getBytes());
    }

    public void disconnect() {
        sendCommand(BLEConstants.CMD_DISCONNECT.getBytes());
    }

    /**
     * 暂停\恢复设备音乐播放
     */
    public void play_or_pause() {
        sendCommand(BLEConstants.CMD_PLAY_PAUSE.getBytes());
    }

    /**
     * 暂停设备音乐播放
     */
    public void pauseMusic() {
        sendCommand(BLEConstants.CMD_PAUSE.getBytes());
    }

    public void play_next() {
        sendCommand(BLEConstants.CMD_PLAY_NEXT.getBytes());
    }

    public void play_pre() {
        sendCommand(BLEConstants.CMD_PLAY_PREVIOUS.getBytes());
    }

    public void play_stop() {
        sendCommand(BLEConstants.CMD_STOP_PLAY.getBytes());
    }

    public void query() {
        sendCommand(BLEConstants.CMD_QUERY_NOW_SONG.getBytes());
    }

    public void acceptPhone() {
        sendCommand(BLEConstants.CMD_PHONE_ACCEPT.getBytes());
    }

    public void refusePhone() {
        sendCommand(BLEConstants.CMD_PHONE_REFUSE.getBytes());
    }

    public void hungUpPhone() {
        sendCommand(BLEConstants.CMD_PHONE_HUNGUP.getBytes());
    }

    public void queryStatus() {
        sendCommand(BLEConstants.CMD_QUERY_STATUS.getBytes());
    }

    public void queryPairDevice() {
        sendCommand(BLEConstants.CMD_QUERY_PAIR.getBytes());
    }

    public void dialPhone(String num) {
        sendCommand(String.format(BLEConstants.CMD_PHONE_DIAL, num).getBytes());
    }

    public void reDialPhone() {
        sendCommand(BLEConstants.CMD_PHONE_RE_DIAL.getBytes());
    }

    public void addVolume() {
        sendCommand(BLEConstants.CMD_ADD_VOLUME.getBytes());
    }

    public void reduceVolume() {
        sendCommand(BLEConstants.CMD_REDUCE_VOLUME.getBytes());
    }

    public void getDeviceName() {
        sendCommand(BLEConstants.CMD_GET_DEVICE_NAME.getBytes());
    }

    public void changeDeviceName(String name) {
        sendCommand(String.format(BLEConstants.CMD_CHANGE_DEVICE_NAME, name).getBytes());
    }

    public void setAutoConnection() {
        sendCommand(BLEConstants.CMD_SET_AUTO_CONNECTION.getBytes());
    }

    public void setBleVisible() {
        sendCommand(BLEConstants.CMD_SET_VISIBLE.getBytes());
    }

    public void queryBleVersion() {
        sendCommand(BLEConstants.CMD_QUERY_VERSION.getBytes());
    }

    public void deleteAllPairInfo() {
        sendCommand(BLEConstants.CMD_DELETE_ALL_PAIR.getBytes());
    }

    public void getCurrentPairDeviceMac() {
        sendCommand(BLEConstants.CMD_QUERY_CURRENT_MAC.getBytes());
    }
    public void getCurrentPairDeviceName() {
        sendCommand(BLEConstants.CMD_QUERY_CURRENT_NAME.getBytes());
    }

    private void sendCommand(final byte[] cmd) {
        if (serialUtil != null && serialUtil.isOpen) {
            sendPool.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("BLESerialManager", "send Commands:" + new String(cmd));
                    serialUtil.sendCommands(cmd);
                }
            });
        }
    }
}