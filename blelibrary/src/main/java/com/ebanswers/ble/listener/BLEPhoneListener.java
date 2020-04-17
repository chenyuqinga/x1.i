package com.ebanswers.ble.listener;

/**
 * Description
 * Created by chenqiao on 2016/10/11.
 */

public interface BLEPhoneListener {
    //有来电
    void onPhoneFrom(String phoneNum);

    //电话挂断
    void onPhoneHungUp();

    //电话接通
    void onPhoneAccepted();

    //电话号码
    void onPhoneNum(String phoneNum);

    //主动打电话
    void onPhoneTo();

    //接听来电
    void acceptOk();

    //拒接来电
    void refuseOk();

    //挂断来电
    void hungUpOk();
}