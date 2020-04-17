package fotile.device.cookerprotocollib.helper;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 文件名称：DeviceControl
 * 创建时间：2017/10/27 17:08
 * 文件作者：yaohx
 * 功能描述：设备控制指令下发
 */

public class DeviceControl {
    private Context context;
    /**
     * 不鸣
     */
    public static final int BUZZER_NO = 0x00;

    /**
     * 短鸣
     */
    public static final int BUZZER_SHORT = 0x01;
    /**
     * 长鸣
     */
    public static final int BUZZER_LONG = 0x02;
    /**
     * 响提示音（响150ms停150ms再响150ms）
     */
    public static final int BUZZER_TIP = 0x03;
    /**
     * 响报警音（响150ms停150ms响150ms停150ms响150ms停500ms，连续两分钟）
     */
    public static final int BUZZER_POLICE = 0x04;

    private static DeviceControl deviceControl;

    //灯控参数
    public static final int LAMP_MIN = 36;
    public static final int LAMP_CENTER = 58;
    public static final int LAMP_MAX = 80;
    public static final int LAMP_CLOSE = 0;

    private final int WHAT_DELAY_CONTROL_LAMP = 1001;

    private DeviceControl() {

    }

    public static synchronized DeviceControl getInstance() {
        if (null == deviceControl) {
            deviceControl = new DeviceControl();
        }
        return deviceControl;
    }

    public void initContext(Context context) {
        this.context = context;
    }


    /**
     * 蜂鸣器
     *
     * @param buzzer
     */
    public void setBuzzer(int buzzer) {
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            switch (buzzer) {
                //短鸣
                case BUZZER_SHORT:
                    bodyObj.put("buzzer", 0x01);
                    break;
                //长鸣
                case BUZZER_LONG:
                    bodyObj.put("buzzer", 0x02);
                    break;
            }

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 开启空气管家
     */
    public void startAirSteWard() {
        //设备控制
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            //工作模式
            bodyObj.put("workmode", "airsteward");
            //电机档数
            bodyObj.put("motorgear", 0);
            //关机/待机/开机
            bodyObj.put("devstate", "on");
            //蜂鸣
            bodyObj.put("buzzer", BUZZER_SHORT);

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 开启空气管家-自检模式
     */
    public void startAirSteWardSelftest() {
        //设备控制
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            //工作模式
            bodyObj.put("workmode", "selftest");
            //电机档数
            bodyObj.put("motorgear", 0);
            //关机/待机/开机
            bodyObj.put("devstate", "on");
            //蜂鸣
            bodyObj.put("buzzer", BUZZER_SHORT);

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 开启智能巡航
     */
    public void startCruise() {
        //设备控制
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            //工作模式
            bodyObj.put("workmode", "autocruise");
            //电机档数
            bodyObj.put("motorgear", 0);
            //关机/待机/开机
            bodyObj.put("devstate", "on");
            //蜂鸣
            bodyObj.put("buzzer", BUZZER_SHORT);

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 开启清洁保养模式
     */
    public void startClean() {
        //设备控制
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            //工作模式
            bodyObj.put("workmode", "clean");
            //关机/待机/开机
            bodyObj.put("devstate", "on");
            //蜂鸣
            bodyObj.put("buzzer", BUZZER_SHORT);
            bodyObj.put("lamp",  LAMP_CENTER);

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 开启风量控制
     *
     * @param speed       档位 F0到F9
     * @param need_buzzer 是否需要蜂鸣器
     */
    public void startWindControl(int speed, boolean need_buzzer) {
        if (speed < 0xF0 || speed > 0xF9) {
            return;
        }
        LogUtil.LOGE("================风机档位",speed);

        //设备控制
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            //工作模式
            bodyObj.put("workmode", "manual");
            //电机档数
            bodyObj.put("motorgear", speed);
            //关机/待机/开机
            bodyObj.put("devstate", "on");
            if (need_buzzer) {
                //蜂鸣
                bodyObj.put("buzzer", BUZZER_SHORT);
            }

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 开启强制跑模式
     */
    public void startForcework() {
        //设备控制
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            //工作模式
            bodyObj.put("workmode", "forcework");
            //电机档数
            bodyObj.put("motorgear", 0);
            //关机/待机/开机
            bodyObj.put("devstate", "on");
            //蜂鸣
            bodyObj.put("buzzer", BUZZER_SHORT);

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 关闭某一个功能，除了灯
     */
    public void closeDeviceOutLamp(boolean need_buzzer) {
        //设备控制
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            //工作模式
            bodyObj.put("workmode", "idle");
            //电机档数
            bodyObj.put("motorgear", 0);
            if (need_buzzer) {
                //蜂鸣
                bodyObj.put("buzzer", BUZZER_SHORT);
            }

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 关闭设备所有运行状态
     * 设置手动模式
     * 将风机档位置位0
     * 将灯关闭
     * 发送待机指令
     */
    public void closeDevice(boolean need_buzzer) {
        //设备控制
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            //工作模式
            bodyObj.put("workmode", "idle");
            //电机档数
            bodyObj.put("motorgear", 0);
            //关灯
            bodyObj.put("lamp", LAMP_CLOSE);
            //关机/待机/开机
            bodyObj.put("devstate", "wait");
            if (need_buzzer) {
                //蜂鸣
                bodyObj.put("buzzer", BUZZER_SHORT);
            }

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 开关远程控制
     *
     * @param telecontrol
     */
    public void setTelecontrol(String telecontrol) {
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("telecontrol", telecontrol);
            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 查询设备信息
     */
    public void getDeviceInfo() {
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            jsonObject.put("type", "reqdp");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }


    //滑动灯的时候延时2秒发送指令，等待电源板执行完上一个灯的渐变效果
    Handler delay_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == WHAT_DELAY_CONTROL_LAMP) {
                String result = (String) msg.obj;
                setStatus(result);
            }
            return false;
        }
    });

    /**
     * 调节灯
     * 默认打开执行 LAMP_CENTER
     * 关闭执行     LAMP_CLOSE
     * @param value [LAMP_MIN,LAMP_CENTER,LAMP_MAX,LAMP_CLOSE]
     * @param isScroll 是否是处理滑条调节灯亮度
     */
    public void setLamp(int value, boolean isScroll) {
        if ((value < LAMP_MIN || value > LAMP_MAX)) {
            if (value == LAMP_CLOSE) {
                value = LAMP_CLOSE;
            } else if (value < LAMP_MIN) {
                value = LAMP_MIN;
            } else if (value > LAMP_MAX) {
                value = LAMP_MAX;
            }
        }

        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("lamp", value);
            if (value > 0) {
                //关机/待机/开机
                bodyObj.put("devstate", "on");
            }
            //关灯时执行蜂鸣器
            if(!isScroll){
                bodyObj.put("buzzer", BUZZER_SHORT);
            }

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");

        delay_handler.removeMessages(WHAT_DELAY_CONTROL_LAMP);
        //控制滑条滑动时，延时下发灯控指令
        if(isScroll){
            Message message = new Message();
            message.obj = result;
            message.what = WHAT_DELAY_CONTROL_LAMP;
            delay_handler.sendMessageDelayed(message, 200);
        }else {
            setStatus(result);
        }
    }

    /**
     * 告诉底层是否烟灶联动中
     *
     * @param link
     */
    public void setSmokeStoveLink(boolean link) {
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        JSONObject linkObj = new JSONObject();
        try {
            linkObj.put("stove", link ? "on" : "off");

            //设备联动开关
            bodyObj.put("linkflag", linkObj);
            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 设置设备联动状态
     *
     * @param context
     */
    public void setDeviceLink(Context context) {
        //默认为false
        boolean smoke_steam = (boolean) PreferenceUtil.getPreferenceValue(context, PreferenceUtil
                .DEVICE_LINK_SMOKE_STEAM, false);
        boolean smoke_roast = (boolean) PreferenceUtil.getPreferenceValue(context, PreferenceUtil
                .DEVICE_LINK_SMOKE_ROAST, false);

        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        JSONObject linkObj = new JSONObject();
        try {
            //烟蒸
            linkObj.put("steam", smoke_steam ? "on" : "off");
            linkObj.put("steammicro", smoke_steam ? "on" : "off");
            //烟烤
            linkObj.put("oven", smoke_roast ? "on" : "off");
            //ikcc
            linkObj.put("ikcc", "on");

            //设备联动开关
            bodyObj.put("linkflag", linkObj);
            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 定时结束
     */
    public void setTimerEnd() {
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("timerstate", "finish");

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    public void linkTest() {
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("steamlink", "on");

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 语音盒子供电
     *
     * @param open on 或者 off
     */
    public void setVboxPower(String open) {
        String result = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("vboxpower", open);

            jsonObject.put("type", "ctrl");
            jsonObject.put("body", bodyObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result = jsonObject.toString().replace("\\", "");
        setStatus(result);
    }

    /**
     * 绑定设备上线
     *
     * @param type 0表示停止，1表示启动，2表示解除设备绑定，其他保留
     */
    public static void setXlinkServer(int type) {
        CookerProtocol.setXlinkServer(type);
//        //让设备上线
//        if (type == 1) {
//            PreferenceUtil.setPreferenceValue(context, PreferenceUtil.CURRENT_REMOTE_LINK, true);
//        }
//        //让设备下线
//        if (type == 0) {
//            PreferenceUtil.setPreferenceValue(context, PreferenceUtil.CURRENT_REMOTE_LINK, false);
//        }
//        //解绑，更新本地变量
//        if (type == 2) {
//            PreferenceUtil.setPreferenceValue(context, PreferenceUtil.DEVICE_BIND_STATE, false);
//        }
    }

    /**
     * 物联网云平台
     *
     * @param number 0表示正式地址 1表示测试地址 2表示开发环境
     */
    public static void setHostNameOrIp(int number) {
        CookerProtocol.setHostNameOrIp(number);
    }

    public static int getHostNameOrIp() {
        return CookerProtocol.getHostNameOrIp();
    }

    /**
     * 获取AuthCode
     *
     * @return
     */
    public static String getAuthCode() {
        return CookerProtocol.getAuthCode();
    }

    /**
     * 获取device.device.id信息
     *
     * @return
     */
    public static String getDeviceID() {
        return CookerProtocol.getDeviceID();
    }

    /**
     * 获取绑定二维码
     *
     * @return
     */
    public static String getQRCode() {
        return CookerProtocol.getQRCode();
    }


    /**
     * 获取登录token
     *
     * @return
     */
    public static String getAccessToken() {
        return CookerProtocol.getAccessToken();
    }


    /**
     * 是否开启电源板log
     *
     * @param isOpen 0开，3关
     * @return
     */
    public static void setPrintLevel(int isOpen) {
        CookerProtocol.setPrintLevel(isOpen);
    }

    private void setStatus(String json) {
        LogUtil.LOG_COMMAND("下发", json);
        CookerProtocol.setStatus(json);
    }
}
