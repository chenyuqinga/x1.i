package fotile.ble.observer.link;



import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import fotile.ble.bean.BleDevice;
import fotile.ble.bean.BleMessage;
import rx.functions.Action1;

import static fotile.ble.util.BleConstant.LOG_BEAT;

/**
 * 项目名称：BleClient
 * 创建时间：2018/9/4 15:10
 * 文件作者：yaohx
 * 功能描述：
 * list_link中注册的订阅者，用于更新界面（由View订阅），或者处理一些重连逻辑（由Server订阅）
 * action_read的订阅者在Server中，负责处理一些和灶具蓝牙的数据交互逻辑，此订阅者要求为全局static且常驻内存
 * 由此可以得出，蓝牙连接由Server发起，并且在Server中注册一个action_read订阅者
 */
public abstract class ILinkObserverable {

    public CopyOnWriteArrayList<Action1<BleDevice>> list_link = new CopyOnWriteArrayList<Action1<BleDevice>>();
    /**
     * 一个ILinkObserverable只维护一个action_read对象
     */
    public Action1<BleDevice> action_read;

    /*****************************************蓝牙连接相关↓*************************************/
    /**
     * 记录连接BleDevice的次数，包括正常连接和重连
     */
    public int link_count = 0;
    /**
     * 前十次的定时器
     */
    public Timer timer_retry_min = null;
    /**
     * 十次以后的定时器
     */
    public Timer timer_retry_max = null;
    /**
     * 蓝牙心跳定时器
     */
    private Timer timer_beat = null;
    /**
     * 蓝牙心跳时间-服务Socket判断时间为10*1000*3，连续三次没有收到心跳包执行断开
     */
    private final int beat_time = 10 * 1000;
    /**
     * 未连接
     */
    public static final int STATE_NONE = 0;
    /**
     * 连接失败
     */
    public static final int STATE_ERROR = -1;
    /**
     * 数据传输错误
     */
    public static final int STATE_DATA_ERROR = -2;
    /**
     * 正在连接
     */
    public static final int STATE_CONNECTING = 2;
    /**
     * 已经连接
     */
    public static final int STATE_CONNECTED = 1;
    /*****************************************蓝牙连接相关↑*************************************/

    /**
     * 添加连接数据观察者
     *
     * @param action1
     */
    public abstract void addLinkObserver(Action1<BleDevice> action1);

    public abstract void removeLinkObserver(Action1<BleDevice> action1);

    public void notifyLinkData(BleDevice bleDevice){
        if(null != bleDevice){
            try {
                //记录连接次数
                bleDevice.link_count = link_count;
                int state = bleDevice.linkStatus;
                //通知app，蓝牙连接失败，处理逻辑在BleServer中
                if(state == STATE_ERROR){
                    Class to_class = Class.forName("com.fotile.x1i.server.BleServer");
                    BleMessage bleMessage = new BleMessage(to_class,BleMessage.BLE_LINK_RESULT_ERROR);
                    bleMessage.setBleDevice(bleDevice);
                    EventBus.getDefault().post(bleMessage);
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加读取数据观察者
     * 一个蓝牙连接，只能有一个读取数据观察者
     *
     * @param action1
     */
    public void setReadObserver(Action1<BleDevice> action1) {
        if (null != action1) {
            action_read = action1;
        }
    }

    public void clearReadObserver() {
        action_read = null;
    }

    public abstract void notifyReadData(BleDevice bleDevice);


    /*****************************************蓝牙连接相关↓*************************************/
    public abstract void write(byte[] data);

    public abstract void setState(int state);

    public abstract int getState();

    public abstract BleDevice getLinkDevice();

    public abstract String getLinkDevicedMac();

    /**
     * 连接蓝牙设备
     * @param active 表示是否是用户手动连接
     */
    public abstract void connect(boolean active);

    public abstract void read();

    /**
     * 断开连接
     */
    public void disConnect() {
        //取消心跳
        cancelBeatTimer();
        //取消重连定时器
        cancelRetryTimer();
    }

    public abstract void retryConnect();

    /**
     * 取消重连定时器
     */
    public void cancelRetryTimer() {
        if (null != timer_retry_min) {
            timer_retry_min.cancel();
            timer_retry_min.purge();
            timer_retry_min = null;
        }
        if (null != timer_retry_max) {
            timer_retry_max.cancel();
            timer_retry_max.purge();
            timer_retry_max = null;
        }
    }

    /**
     * 开启心跳包
     */
    public synchronized void startBeatTimer(final byte[] beat_data) {
        cancelBeatTimer();
        timer_beat = new Timer();
        timer_beat.schedule(new TimerTask() {
            @Override
            public void run() {
                write(beat_data);
                if(LOG_BEAT){
                    LogUtil.LOG_TOOTH("发给灶具的心跳包", Tool.getHexBinString(beat_data, false));
                }
            }
        }, 800, beat_time);
    }

    /**
     * 取消心跳包
     */
    public synchronized void cancelBeatTimer() {
        if (null != timer_beat) {
            timer_beat.cancel();
            timer_beat.purge();
            timer_beat = null;
        }
    }

    /**
     * 重置连接次数
     */
    public synchronized void resetLinkCount(){
        link_count = 0;
    }

    /*****************************************蓝牙连接相关↑*************************************/

}
