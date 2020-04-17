/*
 * ************************************************************
 * 文件：OnUdpEventListener.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.Udp;

import java.net.DatagramPacket;

public interface OnUdpEventListener {
    void onMessageReceived(DatagramPacket message);
    void onTimeOut();
}
