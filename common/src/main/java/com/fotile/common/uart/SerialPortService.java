package com.fotile.common.uart;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 项目名称：X1.I
 * 创建时间：2018/7/20 10:50
 * 文件作者：yaohx
 * 功能描述：传感器串口通信服务
 */
public class SerialPortService extends Service {
    private final String TAG = "串口通讯";
    /**
     * 串口名称
     */
    private final String path = "/dev/ttyMT1";
    /**
     * 波特率
     */
    private int baudrate = 9600;
    /**
     * 是否打开串口标志
     */
    public boolean isSerialPortOpen = false;
    /**
     * 操作串口对象
     */
    public SerialPort serialPort = null;

    /**
     * 串口输入流
     */
    public InputStream inputStream = null;
    /**
     * 串口输出流
     */
    public OutputStream outputStream = null;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        openSerialPort();
    }

    /**
     * 打开串口
     *
     * @return serialPort串口对象
     */
    @Subscribe
    private SerialPort openSerialPort() {
        try {
            serialPort = new SerialPort(new File(path), baudrate, 0);
            isSerialPortOpen = true;

            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();

            new ReadThread().start(); //开始线程监控是否有数据要接收
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serialPort;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        closeSerialPort();
    }

    /**
     * 关闭串口
     */
    private void closeSerialPort() {
        try {
            inputStream.close();
            outputStream.close();

            isSerialPortOpen = false;
            serialPort.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送串口指令（字符串）
     *
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendSerialPort(SerialPortMessage message) {
//        String _className = message.to_class.getSimpleName();
//        //向串口发送指令
//        if (_className.contains("SerialPortService") && COMMAND_UART_SEND_DATA.equals(message.command)) {
//            byte[] data = message.data;
//            if (null != data && data.length > 0) {
//
//                byte[] bb = HexTool.HexStrToByteArr("f4f50f010501020100ff0100000000006aca");
//                try {
//                    outputStream.write(bb);
//                    outputStream.write('\n');
//                    //outputStream.write('\r'+'\n');
//                    outputStream.flush();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    /**
     * 单开一线程，来读数据
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
//            while (isSerialPortOpen) {
//                byte[] buffer = new byte[64];
//                int size;
//                try {
//                    size = inputStream.read(buffer);
//                    if (size > 0) {
//                        CommonLogUtil.LOGE(TAG, "传感器接收到了数据：" + HexTool.HexByteArr2String(buffer));
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
