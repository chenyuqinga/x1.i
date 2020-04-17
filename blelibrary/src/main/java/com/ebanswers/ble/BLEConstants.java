package com.ebanswers.ble;

/**
 * Description
 * Created by chenqiao on 2016/10/12.
 */

public class BLEConstants {
    static final String CMD_QUERY_STATUS = "AT#CY\r\n";
    static final String CMD_QUERY_PAIR = "AT#MX\r\n";//查询所有配对记录
    static final String CMD_QUERY_CURRENT_MAC = "AT#QA\r\n";//查询当前连接设备的mac地址
    static final String CMD_QUERY_CURRENT_NAME = "AT#QB\r\n";//查询当前连接设备的名称
    static final String CMD_ENABLE_BLE = "AT#P1\r\n";
    static final String CMD_DISABLE_BLE = "AT#P0\r\n";
    static final String CMD_SEARCH = "AT#PR\r\n";
    static final String CMD_STOP_SEARCH = "AT#PX\r\n";
    static final String CMD_CONNECT = "AT#CC%s\r\n";//AT#CC
    static final String CMD_DISCONNECT = "AT#CD\r\n";//AT#CV
    static final String CMD_PLAY_PAUSE = "AT#MA\r\n";
    static final String CMD_PLAY_NEXT = "AT#MD\r\n";
    static final String CMD_PLAY_PREVIOUS = "AT#ME\r\n";
    static final String CMD_PAUSE = "AT#MB\r\n";
    static final String CMD_STOP_PLAY = "AT#MC\r\n";
    static final String CMD_QUERY_NOW_SONG = "AT#MK\r\n";
    static final String CMD_PHONE_ACCEPT = "AT#CE\r\n";
    static final String CMD_PHONE_REFUSE = "AT#CF\r\n";
    static final String CMD_PHONE_HUNGUP = "AT#CG\r\n";
    static final String CMD_PHONE_DIAL = "AT#CW%s\r\n";
    static final String CMD_PHONE_RE_DIAL = "AT#CH\r\n";
    static final String CMD_ADD_VOLUME = "AT#CK1\r\n";
    static final String CMD_REDUCE_VOLUME = "AT#CK0\r\n";
    static final String CMD_CHANGE_DEVICE_NAME = "AT#MM%s\r\n";
    static final String CMD_GET_DEVICE_NAME = "AT#MM\r\n";
    static final String CMD_SET_PAIR_CODE = "AT#MN%s\r\n";
    static final String CMD_SET_AUTO_CONNECTION = "AT#MG\r\n";
    static final String CMD_SET_VISIBLE = "AT#CA\r\n";//蓝牙可见
    static final String CMD_QUERY_VERSION = "AT#MY\r\n";
    static final String CMD_DELETE_ALL_PAIR = "AT#CV\r\n";//删除所有配对记录


    static final String OK = "OK";
    static final String CONNECT_CODE = "MN";
    static final String DEVICE_DISCONNECT = "MY";
    static final String DEVICE_HEADER = "QA";
    static final String DEVICE_CONNECTED_MAC = "JH";
    static final String DEVICE_CONNECTED_NAME = "SA";
    static final String STOP_SCAN = "QC";
    static final String SONG_NAME_HEADER = "RE";
    static final String SONG_SINGER_HEADER = "RR";
    static final String SONG_ALBUM_HEADER = "RA";
    static final String SONG_INDEX = "RC";
    static final String SONG_NUMS = "RZ";
    static final String SONG_ALL_TIME = "RT";
    static final String SONG_NOW_TIME = "RS";
    static final String DEVICE_NAME = "MM";
    static final String SONG_PAUSE = "MA";
    static final String SONG_PLAY = "MB";

    static final String PHONE_ID = "ID";//来电号码前缀
    static final String PHONE_IG = "IG";//电话接通
    static final String PHONE_IF = "IF";//挂断
    static final String PHONE_IR = "IR";//接通后的对方号码
    static final String PHONE_IC = "IC";//主动打电话

    static final String OK_STATUS_OPEN = "PO";
    static final String OK_STATUS_CLOSE = "PF";
    static final String OK_STATUS_START_SCAN = "PR";
    static final String OK_STATUS_STOP_SCAN = "PX";
    static final String OK_STATUS_DISCONNECT = "CV";//清除配对记录
//    static final String OK_STATUS_PLAY_OR_PAUSE = "MA";
//    static final String OK_STATUS_PLAY_PAUSE = "MB";
    static final String OK_STATUS_PLAY_NEXT = "MD";
    static final String OK_STATUS_PLAY_PRE = "ME";
    static final String OK_STATUS_PLAY_STOP = "MC";
    static final String OK_STATUS_REFUSE_PHONE = "CF";
    static final String OK_STATUS_ACCEPT_PHONE = "CE";
    static final String OK_STATUS_HUNGUP_PHONE = "CG";
    static final String OK_STATUS_QUERY = "MG";
    static final String OK_STATUS_PAIR = "MX1";
    static final String OK_STATUS_CONNECT_SUCCESS = "IB";//蓝牙连接成功
    static final String OK_STATUS_CONNECT_FAILURE = "IA";//蓝牙断开连接

}