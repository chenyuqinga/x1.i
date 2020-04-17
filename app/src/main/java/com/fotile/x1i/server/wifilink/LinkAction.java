package com.fotile.x1i.server.wifilink;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.server.smokelink.SmokeStoveLinkAction;


import java.util.LinkedList;
import java.util.Queue;

/**
 * 文件名称：LinkAction
 * 创建时间：2019/7/17 18:10
 * 文件作者：yaohx
 * 功能描述：烟灶联动业务处理类
 */
public class LinkAction {

    /**
     * tcp连接建立后，发给灶具的握手指令
     */
    public static final byte[] request = {(byte) 0xf4, (byte) 0xf5, 0x00, 0x08, 0x05, 0x00, 0x03, 0x01, 0x00, 0x00,
            0x00, 0x0e};
    /**
     * tcp连接建立后，灶具答复的握手指令
     */
    public static final byte[] response = {(byte) 0xf4, (byte) 0xf5, 0x00, 0x08, 0x05, 0x00, 0x03, 0x02, 0x00, 0x00,
            0x00, 0x0d};

    private static LinkAction instance;
    private Context context;
    /**
     * 用一个队列缓存需要的数据
     */
    private static Queue<byte[]> queue = new LinkedList<byte[]>();

    /**
     * 记录上一次的灶具点火状态
     */
    private boolean last_left_fire = false;
    private boolean last_righe_fire = false;
    /**
     * 30秒未收到心跳包，即主动断开联动
     */
    private static final int WHAT_CLOSE_WIFI_LINK = 1001;

    private LinkAction(Context context) {
        this.context = context;
    }

    public static LinkAction getInstance(Context context) {
        if (null == instance) {
            instance = new LinkAction(context);
        }
        return instance;
    }


    /**
     * 处理来自灶具的数据
     *
     * @param data
     */
    public void reciverData(byte[] data) {
        if (null != data) {
//            LogUtil.LOG_STOVE_LINK("灶具wifi上报数据", Tool.getHexBinString(data));

            //接受灶具心跳回包 f4 f5 00 08 05 00 02 02 00 00 00 0c
            if ((data[6] & 0xFF) == 0x02 && (data[7] & 0xFF) == 0x02) {
                LogUtil.LOG_STOVE_LINK("接受灶具的心跳包", Tool.getHexBinString(data));
                //移除上一次的延时
                removeCloseWifiWhat();
                handler.sendEmptyMessageDelayed(WHAT_CLOSE_WIFI_LINK,25 * 1000);
                return;
            }

            //bit6 == cmd
            int cmd = data[6] & 0xff;
            //0x32 MCU主动上报    0x30 wifi查询
            if (cmd == 0x32 || cmd == 0x30) {
                StoveBean stoveBean = new StoveBean(data);
                //处理烟灶联动
                handleSmokeStoveLink(stoveBean);
            }
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                //主动关闭wifi连接
                case WHAT_CLOSE_WIFI_LINK:
                    LinkObserverable.getInstance(context).disConnection();
                    //如果有灶具是开火状态
                    if(last_left_fire || last_righe_fire){
                        SmokeStoveLinkAction.getInstance().closeStoveLink("主动断开wifi连接，关闭风机");
                    }
                    break;
            }
            return false;
        }
    });

    public void removeCloseWifiWhat() {
        if (null != handler) {
            handler.removeMessages(WHAT_CLOSE_WIFI_LINK);
        }
    }

    /**
     * 处理烟灶联动
     *
     * @param stoveBean
     */
    private synchronized void handleSmokeStoveLink(StoveBean stoveBean) {
        LogUtil.LOG_STOVE_LINK("灶具Bean", stoveBean);
        LogUtil.LOG_STOVE("灶具上一次的点火状态", "【" + last_left_fire + " " + last_righe_fire + "】");

        //如果返回状态切换了
        if (stoveBean.left_firing != last_left_fire || stoveBean.right_firing != last_righe_fire) {
            //左灶或者右灶是点火状态
            if (stoveBean.left_firing || stoveBean.right_firing) {
                //只有在两个灶头上一次的状态都是未点火时，才执行烟灶联动
                if (!last_left_fire && !last_righe_fire) {
                    SmokeStoveLinkAction.getInstance().startStoveLink("灶具灶头点火");
                }
            }
            //两个灶头都没有点火
            else {
                SmokeStoveLinkAction.getInstance().closeStoveLink("灶具两个灶头都关火");
            }
            last_left_fire = stoveBean.left_firing;
            last_righe_fire = stoveBean.right_firing;
        }
        //状体没有切换，不执行联动操作
        else {

        }
    }


    /**
     * 将需要传递给灶具的数据扔到队列中
     *
     * @param bytes
     */
    public synchronized void addData(byte[] bytes) {
        queue.add(bytes);
    }

    /**
     * 从队列中拉取数据传递给灶具-移除并返问队列头部的元素
     *
     * @return
     */
    public byte[] getData() {
        if (null != queue && queue.size() != 0) {
            return queue.poll();
        }
        return null;
    }

}
