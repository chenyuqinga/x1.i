package com.ebanswers.ble;

import android.support.annotation.Keep;

/**
 * Description
 * Created by chenqiao on 2016/9/1.
 */
@Keep
public class BLEDevice {

    public static final int STATUS_CONNECTED = 712;
    public static final int STATUS_CONNECTING = 501;
    public static final int STATUS_DISCONNECTING = 353;
    public static final int STATUS_NORMAL = 6;

    private String mac_address;
    private String name;
    private int status;
    private boolean searching;

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSearching() {
        return searching;
    }

    public void setSearching(boolean searching) {
        this.searching = searching;
    }

    public BLEDevice() {
    }

    public BLEDevice(String mac_address) {
        this.mac_address = mac_address;
    }

    public BLEDevice(String mac_address, String name) {
        this.mac_address = mac_address;
        this.name = name;
    }

    public BLEDevice(String mac_address, String name, int status) {
        this.mac_address = mac_address;
        this.name = name;
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BLEDevice) {
            return this.getMac_address().equals(((BLEDevice) obj).getMac_address());
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "[MAC地址:"+ mac_address +"] [" + status +"] [名称:" + name+"]";
        return result;
    }
}
