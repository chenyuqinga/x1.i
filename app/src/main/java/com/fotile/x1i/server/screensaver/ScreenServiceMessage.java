package com.fotile.x1i.server.screensaver;

/**
 * 文件名称：ScreenServiceMessage
 * 创建时间：2018/3/7 17:37
 * 文件作者：yaohx
 * 功能描述：屏保指令传输类
 */
public class ScreenServiceMessage {
    public int command;

    public Class to_class;

    public ScreenServiceMessage(int command) {
        this.command = command;
    }

    public ScreenServiceMessage(int command, Class to_class) {
        this.command = command;
        this.to_class = to_class;
    }
}
