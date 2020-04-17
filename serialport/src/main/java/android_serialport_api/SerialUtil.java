package android_serialport_api;

import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Description  串口功能类
 * Created by chenqiao on 2015/11/6.
 */
public class SerialUtil {

    private static final byte[] DEFAULT_DATA = new byte[100];
    //波特率
    private int BAUD_RATE = 9600;
    //串口名
    private String TTY = "/dev/ttyS1";
    //校验位
    private int parity = 0;
    //数据位
    private int csize = 0;
    //停止位
    private int stopbit = 1;
    //标志位
    private int FLAG = 0;


    private SerialPort serialPort = null;
    public OutputStream mOutputStream;
    public InputStream mInputStream;
    private SerialPotReadCallBack readCallBack;

    private String errorMsg;
    public boolean isOpen = false;

    private Thread readThread = null;

    public SerialUtil() {
    }

    public SerialUtil(String tty, int baudRate, int flag) {
        TTY = tty;
        BAUD_RATE = baudRate;
        parity = -1;
        csize = 8;
        stopbit = 1;
        FLAG = flag;
    }
    public SerialUtil(String tty, int baudRate,int _csize, int _parity, int _stopbits) {
        TTY = tty;
        BAUD_RATE = baudRate;
        parity = _parity;
        csize = _csize;
        stopbit = _stopbits;
        FLAG = 0;
    }
    public boolean openSerial() {
        return openSerial(TTY, BAUD_RATE,csize,parity,stopbit, FLAG);
    }

    /**
     * 打开串口
     *
     * @param tty      串口名
     * @param baudRate 波特率
     * @param flag     停止位
     * @return 是否打开成功
     */
    public boolean openSerial(String tty, int baudRate, int _csize, int _parity, int stopbits,int flag) {
        try {
            serialPort = new SerialPort(new File(tty), baudRate,_csize,_parity,stopbits,flag);
            mInputStream = serialPort.getInputStream();
            mOutputStream = serialPort.getOutputStream();
            Log.d("SerialPort", "open success:" + tty);
        } catch (IOException e) {
            Log.e("SerialPort", "open failed:" + e.toString());
            errorMsg = e.toString();
            isOpen = false;
            return false;
        } catch (SecurityException e2) {
            Log.e("SerialPort", "open failed:have no read/write permission to the serial port");
            isOpen = false;
            errorMsg = e2.toString();
            return false;
        }
        isOpen = true;
        errorMsg = null;
        return true;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 发送数据
     *
     * @param cmds 发送的命令
     */
    public synchronized void sendCommands(byte[] cmds) {
        if (mOutputStream != null) {
            try {
                mOutputStream.write(cmds);
                mOutputStream.flush();
            } catch (IOException e) {
                Log.e("SerialPort", "sendCommands failed:" + e.toString());
                errorMsg = e.toString();
            }
        } else {
            Log.e("SerialPort", "sendCommands failed:null");
        }
    }

    /**
     * 初始化接收数据的线程
     *
     * @param callBack 接收数据的回调
     */
    public void initReadThread(SerialPotReadCallBack callBack) {
        this.readCallBack = callBack;
        if (mInputStream != null) {
            readThread = new Thread() {
                @Override
                public void run() {
                    byte[] data = DEFAULT_DATA.clone();
                    while (!isInterrupted()) {
                        try {
                            int len = mInputStream.read(data);
                            if (len > 0) {
                                if (readCallBack != null) {
                                    readCallBack.readData(len, Arrays.copyOf(data,len));
                                }
                                data = DEFAULT_DATA.clone();
                            }
                        } catch (IOException e) {
                            interrupt();
                            Log.e("SerialPort", "readData error:" + e.toString());
                        }
                    }
                }
            };
            readThread.start();
        } else {
            Log.e("SerialPort", "initReadThread failed:null");
        }
    }

    /**
     * 停止接收线程
     */
    public void stopReadThread() {
        if (readThread != null) {
            readThread.interrupt();
            readThread = null;
        }
    }

    /**
     * 关闭串口（之后使用需要使用{@link #openSerial}打开串口）
     */
    public void closeSerialPort() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mOutputStream != null) {
                mOutputStream.close();
                mOutputStream = null;
            }
            if (serialPort != null) {
                serialPort.close();
                serialPort = null;
            }
            isOpen = false;
        } catch (IOException e) {
            isOpen = false;
            Log.e("SerialPort", "closeSerialPort failed:" + e.toString());
        }
    }

    public interface SerialPotReadCallBack {
        void readData(int len, byte[] buffer);
    }
}
