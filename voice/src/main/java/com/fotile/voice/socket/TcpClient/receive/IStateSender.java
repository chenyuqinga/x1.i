/*
 * ************************************************************
 * 文件：IStateSender.java  模块：voice  项目：Z1.5
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.TcpClient.receive;

import java.io.Serializable;

public interface IStateSender {

    void sendBroadcast(String action, Serializable serializable);

    void sendBroadcast(String action);
}
