package com.ebanswers.ble.listener;

/**
 * Description
 * Created by chenqiao on 2016/9/1.
 */
public interface BLEConnectListener {

    void deviceConnected(String connected_mac,String connected_name);

    void deviceDisconnected();

}
