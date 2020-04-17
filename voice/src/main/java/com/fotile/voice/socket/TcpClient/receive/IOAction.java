/*
 * ************************************************************
 * 文件：IOAction.java  模块：voice  项目：Z1.5
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.TcpClient.receive;

public interface IOAction {
    //收到推送消息响应
    String ACTION_READ_COMPLETE = "action_read_complete";
    //写给服务器响应
    String ACTION_WRITE_COMPLETE = "action_write_complete";
    //发送心跳请求
    String ACTION_PULSE_REQUEST = "action_pulse_request";
}
