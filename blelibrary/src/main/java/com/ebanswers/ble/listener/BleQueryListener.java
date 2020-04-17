package com.ebanswers.ble.listener;

/**
 * Created by caixd on 2016/10/12.
 */

public interface BleQueryListener {
    //获取蓝牙状态:1:待机  2:连接中  3:连接成功  4:电话拨出 5:电话打入 6:通话中
    void getBleStatus(int status);

    //获取蓝牙已配对设备信息
    void getLatestPairedDevice(String mac);

    //获取蓝牙模块名称
    void getBleName(String name);

}
