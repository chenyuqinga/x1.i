/*
 * ************************************************************
 * 文件：TcpClientListener.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.TcpClient.listener;


import com.fotile.voice.socket.TcpClient.TcpClient;
import com.fotile.voice.socket.TcpClient.bean.TcpMsg;

/**
 */
public interface TcpClientListener {
    void onConnected(TcpClient client);

    void onSent(TcpClient client, TcpMsg tcpMsg);

    void onDisconnected(TcpClient client, String msg, Exception e);

    void onReceive(TcpClient client, TcpMsg tcpMsg);

    void onValidationFail(TcpClient client, TcpMsg tcpMsg);

    void onPingTimeout(TcpClient client);
}
