package com.fotile.common.uart;

/**
 * 项目名称：X1.I
 * 创建时间：2018/7/20 11:35
 * 文件作者：yaohx
 * 功能描述：传感器传递Message
 */
public class SerialPortMessage {
    /**
     * 指定消息给哪一个类去处理
     */
    public Class to_class;
    /**
     * 控制指令
     */
    public int command;
    /**
     * 传感器串口发送数据
     */
    public static final String COMMAND_UART_SEND_DATA = "SerialPortMessage" + 001;
    /**
     * 传递的数据值
     */
    public byte[] data;


    public SerialPortMessage(Class to_class, int command) {
        this.to_class = to_class;
        this.command = command;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
