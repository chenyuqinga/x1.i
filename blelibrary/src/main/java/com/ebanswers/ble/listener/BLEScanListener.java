package com.ebanswers.ble.listener;

import com.ebanswers.ble.BLEDevice;

/**
 * Description
 * Created by chenqiao on 2016/9/1.
 */
public interface BLEScanListener {

    void onScanStop();

    void onAddADevice(BLEDevice device);
}
