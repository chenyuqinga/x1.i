/*
 * ************************************************************
 * 文件：TcpClientManager.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.TcpClient.manager;


import com.fotile.voice.socket.TcpClient.TcpClient;
import com.fotile.voice.socket.TcpClient.bean.TargetInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * tcpclient的管理者
 */
public class TcpClientManager {
    private static Set<TcpClient> sMXTcpClients = new HashSet<>();

    public static void putTcpClient(TcpClient TcpClient) {
        sMXTcpClients.add(TcpClient);
    }

    public static TcpClient getTcpClient(TargetInfo targetInfo) {
        for (TcpClient tc : sMXTcpClients) {
            if (tc.getTargetInfo().equals(targetInfo)) {
                return tc;
            }
        }
        return null;
    }
}
