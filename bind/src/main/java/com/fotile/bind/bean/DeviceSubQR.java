package com.fotile.bind.bean;

import com.google.gson.Gson;

/**
 * 文件名称：DeviceSubQR
 * 创建时间： 2018/11/14
 * 文件作者：huanghuang
 * 功能描述：获取二维码扫描绑定
 */
public class DeviceSubQR {

    private String qrcode;
    private String deviceId;
    private String timeStamp;
    private String businessId;

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @Override
    public String toString() {
        String result = new Gson().toJson(this);
        return result;
    }
}
