package com.fotile.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件名称：PreferenceUtil
 * 创建时间：2018/11/2 11:33
 * 文件作者：yaohx
 * 功能描述：用于帮助管理SharedPreference的util
 */
public class PreferenceUtil {
    /**
     * SharedPreference文件的名字
     */
    private static final String PREFERENCE_NAME = "x1i_app";
    /********************************************* ble控制 ↓******************************************************/
    /**
     * 是否第一次开关蓝牙
     */
    public static final String FIRST_OPEN_STOVE_LINK = "first_open_stove_link";
    /**
     * 烟灶联动开关是否打开
     */
    public static final String DEVICE_LINK_SMOKE_STOVE = "smoke_stove";

    /**
     * 记录上一次连接的设备名称-在主动断开某一个连接后保存""
     */
    public static final String STOVE_LINK_DEVICE_MAC = "stove_link_device_mac";
    /**
     * 记录上一次连接的设备名称-如果连接失败保存“”
     */
    public static final String BLUE_LINK_DEVICE_NAME = "blue_link_name";
    /**
     * 记录上一次连接的设备mac-如果连接失败保存“”
     */
    public static final String BLUE_LINK_DEVICE_ADDRESS = "blue_link_address";

    /********************************************* ble控制 ↑******************************************************/

    /********************************************* recipe ↓******************************************************/
    /**
     * 灶具的mac地址
     */
    public static final String STOVE_MAC = "stove_mac";
    /**
     * 灶具authcode
     */
    public static final String STOVE_AUTHCODE = "stove_authcode";
    /**
     * 灶具的deviceId
     */
    public static final String STOVE_DEVICE_ID = "stove_device_id";
    /**
     * 灶具录制菜谱的token
     */
    public static final String STOVE_UPLOAD_RECIPE_TOKEN = "stove_upload_recipe_token";

    /**
     * 烟机登录token
     */
    public static final String LAMPBLACK_LOGIN_TOKEN = "lampblack_login_token";
    /********************************************* recipe ↑******************************************************/

    /********************************************* settings ↓******************************************************/
    /**
     * 屏保类型
     */
    private static final String SCREEN_SAVER_TYPE = "screen_saver_type";
    /**
     * 烟蒸联动开关
     */
    public static final String DEVICE_LINK_SMOKE_STEAM = "smoke_steam";
    /**
     * 烟烤联动
     */
    public static final String DEVICE_LINK_SMOKE_ROAST = "smoke_roast";
    /**
     * 投屏功能
     */
    private static final String PROJECTION_SCREEN = "projection_screen";
    /**
     * 最后一次连接的wifi名称
     */
    public static final String LAST_CONNECTED_WIFI_NAME = "last_connected_wifi_name";

    /**
     * 最后一次连接的wifi密码
     */
    public static final String LAST_CONNECTED_WIFI_PWD = "last_connected_wifi_pwd";
    /**
     * 延时关机时间
     */
    public static final String DELAY_SHUTDOWN_TIME = "delay_shut_down_time";


    /**
     * 保存延时关机时的风机状态--用于解决bug4386
     */
    public static final String DELAY_SHUT_WORK_STATE = "delay_shut_work_state";
    /**
     * wifi列表正在连接的AP名字
     */
    public static final String CONNECTING_WIFI_NAME = "connecting_wifi_name";
    /**
     * 记录屏幕变暗之前的亮度
     */
    public static final String BRIGHT_LAST = "bright_last";
    /**
     * 储存mac地址
     */
    public static final String SHARE_MAC_ADDRESS_KEY = "share_mac_address_key";
    /**
     * 设备当前远程控制
     */
    public static final String CURRENT_REMOTE_LINK = "current_remote_link";
    /**
     * 延时关机时间
     */
    private static final String DELAY_SHUTDOWN = "delay_shut_down";
    /**
     * 是否有ota更新
     */
    public static final String OTA_AVAILABLE = "ota_available";
    /********************************************* settings ↑******************************************************/

    /**
     * 开启烟灶联动时的工作状态
     */
    public static final String BEFORE_LINK_WORK_STATE = "before_link_work_state";
    /**
     * 设备绑定状态
     */
    public static final String DEVICE_BIND_STATE = "device_bind_state";
    /**
     * 是否有未读的通知信息
     */
    public static final String HAS_UNREAD_NOTIFICATION = "has_unread_notification";
    /**
     * 家庭备忘录最后一次时间
     */
    private static final String FINALLY_MEMORANDUM_TIME = "finally_memorandum_time";
    /**
     * 是否有未读消息
     */
    public static final String HAS_UNREAD_MEMORANDUM = "has_unread_memorandum";
    /**
     * 最近一次的烟机工作状态-（风量控制、智能巡航、空气管家）才保存
     */
    public static final String LAST_WORK_STATE = "current_work_state";
    /**
     * 保存烟机运行时间
     */
    public static final String RUN_TIME = "run_time";
    /**
     * 是否经过空气管家中的延时两分钟关闭
     */
    public static final String PASS_AIR_DELAY_CLOSE = "pass_air_delay_close";
    /**
     * 用户第一次进入app
     */
    public static final String FIRST_ENTER_APP = "first_enter_app";
    /**
     * 是否处于锁屏状态
     */
    public static final String IS_SCREEN_LOCK = "is_screen_lock";
    /**
     * 故障tip
     */
    public static final String FAULT_TIP = "fault_tip";
    /**
     * 远程控制开关
     */
    public static final String TELE_CONTROL = "telecontrol";

    public static final String HOME_ID = "HOME_ID";

    public static final String POWER_STATE = "power_state";

    //清洗 0 -正常提示
    public static final int RUN_CLEAN_TIP_NORMAL = 0;
    //清洗 2 -稍后提示
    public static final int RUN_CLEAN_TIP_NEXT = 2;
    //清洗 1 -不在打扰
    public static final int RUN_CLEAN_TIP_NEVER = 1;
    /**
     * 运行时间提示框是否不再提示
     */
    private static final String RUN_TIME_TIP = "run_time_tip";
    /**
     * 存储wifi的ssid和密码
     */
    public static final String WIFI_INFO = "wifi_info";
    /**
     * 过滤ip
     */
    public static final String BLOCKED_IP = "blocked_ip";

    /**
     * 计时器状态
     */
    public static final String IS_COUNTING_DOWN = "is_counting_down";
    /**
     * 保存用户上一次滑动滑条保存的灯控值
     */
    public static final String LAST_LAMP_VALUE = "last_lamp_value";
    /**
     * 蓝牙音乐开关
     */
    private static final String BLE_MUSIC_SWITCH = "ble_music_switch";

    public static final String RECEIVE_BOX_MSG = "receive_box_msg";

    /**
     * 获取sharedprerence 中key对应的值
     *
     * @param context
     * @param preferenceKey
     * @param defaultResult
     * @return
     */
    public static Object getPreferenceValue(Context context, String preferenceKey, Object defaultResult) {
        if (null != context) {
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            if (defaultResult instanceof String) {
                return preferences.getString(preferenceKey, (String) defaultResult);
            } else if (defaultResult instanceof Integer) {
                return preferences.getInt(preferenceKey, (Integer) defaultResult);
            } else if (defaultResult instanceof Boolean) {
                return preferences.getBoolean(preferenceKey, (Boolean) defaultResult);
            } else if (defaultResult instanceof Float) {
                return preferences.getFloat(preferenceKey, (Float) defaultResult);
            } else if (defaultResult instanceof Long) {
                return preferences.getLong(preferenceKey, (Long) defaultResult);
            }
        }
        return null;
    }

    /**
     * 修改sharedprerence 中key对应的值
     *
     * @param context
     * @param preferenceKey
     * @param value
     */
    public static void setPreferenceValue(Context context, String preferenceKey, Object value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof String) {
            editor.putString(preferenceKey, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(preferenceKey, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(preferenceKey, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(preferenceKey, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(preferenceKey, (Long) value);

        }
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * 设置是否有未读家庭备忘
     *
     * @param context
     * @param value
     */
    public static void setHasUnreadMemorandum(Context context, boolean value) {
        setPreferenceValue(context, HAS_UNREAD_MEMORANDUM, value);
    }

    /**
     * 判断是否有未读家庭备忘
     *
     * @param context
     * @return
     */
    public static boolean hasUnreadMemorandum(Context context) {
        boolean hasUnread = (boolean) getPreferenceValue(context, HAS_UNREAD_MEMORANDUM, false);
        return hasUnread;
    }

    /**
     * 设置是否有未读通知
     *
     * @param context
     * @param value
     */
    public static void setHasUnreadNotification(Context context, boolean value) {
        setPreferenceValue(context, HAS_UNREAD_NOTIFICATION, value);
    }

    /**
     * 判断是否有未读通知
     *
     * @param context
     * @return
     */
    public static boolean hasUnreadNotification(Context context) {
        boolean hasUnread = (boolean) getPreferenceValue(context, HAS_UNREAD_NOTIFICATION, false);
        return hasUnread;
    }

    /**
     * 获取延时关机时间
     *
     * @param context
     * @return
     */
    public static int getDelayTime(Context context) {
        int value = (Integer) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.DELAY_SHUTDOWN_TIME, 2);
        return value;
    }
    /*************************************************设备状态****************************************************/
    /**
     * 保存风量控制的档位
     * 设备端上报的风机档位为F1-F9
     */
    public static void saveWindSpeed(Context context, int speed_value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("speed_value", speed_value);
        editor.commit();
    }

    /**
     * 获取保存的上一次的风机档位
     *
     * @param context
     * @return [speed_value]
     */
    public static int getWindSpeed(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        int speed_value = sharedPreferences.getInt("speed_value", -1);
        return speed_value;
    }

    /**
     * 保存灯的状态
     * 0-关闭
     */
    public static void saveLamp(Context context, int lamp) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("lamp_value", lamp);
        editor.commit();
    }

    /**
     * 获取灯的状态
     * 0-关闭
     */
    public static int getLamp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("lamp_value", 0);

    }

    /**
     * 移除某个key对应的值
     *
     * @param context
     * @param preferenceKey
     */
    public static void remove(Context context, String preferenceKey) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(preferenceKey);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public static boolean getLtScreen(Context context) {
        return (boolean) getPreferenceValue(context, PROJECTION_SCREEN, false);
    }

    public static String getStoveUploadRecipeToken(Context context) {
        return (String) getPreferenceValue(context, STOVE_UPLOAD_RECIPE_TOKEN, "");
    }

    public static String getLampblackLoginTokenToken(Context context) {
        return (String) getPreferenceValue(context, LAMPBLACK_LOGIN_TOKEN, "");
    }

    public static void setStoveUploadRecipeToken(Context context, String token) {
        setPreferenceValue(context, STOVE_UPLOAD_RECIPE_TOKEN, token);
    }

    public static void setLampblackLoginTokenToken(Context context, String token) {
        setPreferenceValue(context, LAMPBLACK_LOGIN_TOKEN, token);
    }

    /**
     * 获取延时关机开关值
     *
     * @param context
     * @return
     */
    public static boolean getDelayShutSwitch(Context context) {
        return (boolean) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.DELAY_SHUTDOWN, true);
    }

    public static void setDelayShutSwitch(Context context, boolean switch_) {
        PreferenceUtil.setPreferenceValue(context, DELAY_SHUTDOWN, switch_);
    }


    public static void saveMemorTime(Context context, String memorandumTime) {
        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.FINALLY_MEMORANDUM_TIME, memorandumTime);
    }

    public static String getMemorTime(Context context) {
        return (String) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.FINALLY_MEMORANDUM_TIME, "");
    }

    public static void setBoxPowerState(Context context, boolean isPowerOn) {
        PreferenceUtil.setPreferenceValue(context, POWER_STATE, isPowerOn);
    }

    public static boolean getBoxPowerState(Context context) {
        return (boolean) PreferenceUtil.getPreferenceValue(context, POWER_STATE, false);
    }

    /**
     * 屏保样式
     *
     * @param context
     * @return 7==无屏保时钟
     */
    public static int getScreenSaverType(Context context) {
//        return (int) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.SCREEN_SAVER_TYPE, 7);
        return (int) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.SCREEN_SAVER_TYPE, 0);
    }

    public static void saveScreenSaverType(Context context, int type) {
        if ((type < 0 || type > 7) && type != -100) {
            throw new IllegalArgumentException("屏保类型type取值为[0-7]，方太画报为-100");
        }
        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.SCREEN_SAVER_TYPE, type);
    }

    /**
     * 获取运行2000小时提醒模式
     *
     * @param context
     * @return
     */
    public static int getRuntimeTip(Context context) {
        return (int) PreferenceUtil.getPreferenceValue(context, RUN_TIME_TIP, RUN_CLEAN_TIP_NORMAL);
    }

    public static void setRunTimeTip(Context context, int tip) {
        PreferenceUtil.setPreferenceValue(context, RUN_TIME_TIP, tip);
    }

    /**
     * 以map形式存储wifi密码
     *
     * @param context
     * @param ssid
     * @param pwd
     */
    public static void setPwd(Context context, String ssid, String pwd) {
        Log.e("SharePreference", "set value, ssid: " + ssid + ", pwd: " + pwd);
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Gson gson = new Gson();
        String jsonMap = (String) getPreferenceValue(context, WIFI_INFO, "");
        if (null == jsonMap || gson.fromJson(jsonMap, type) == null) {
            jsonMap = gson.toJson(new HashMap<String, String>());
            setPreferenceValue(context, WIFI_INFO, jsonMap);
            Log.e("SharePreference", "set new json");
        }
        Map<String, String> wifiInfoMap = gson.fromJson(jsonMap, type);
        Log.e("SharePreference", "wifiInfoMap: " + wifiInfoMap);
        if (wifiInfoMap != null) {
            wifiInfoMap.put(ssid, pwd);
            Log.e("SharePreference", "add wifi info: " + wifiInfoMap.values().size() + ", json: " + gson.toJson
                    (wifiInfoMap));
            setPreferenceValue(context, WIFI_INFO, gson.toJson(wifiInfoMap));
        } else {
            Log.e("SharePreference", "error map!!!!");
        }
    }

    /**
     * 从map中移除密码
     *
     * @param context
     * @param ssid
     */
    public static void removeWifiInfo(Context context, String ssid) {
        Log.e("SharePreference", "remove wifi info: " + ssid);
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Gson gson = new Gson();
        String jsonMap = (String) getPreferenceValue(context, WIFI_INFO, "");
        if (null == jsonMap || gson.fromJson(jsonMap, type) == null) {
            jsonMap = gson.toJson(new HashMap<String, String>());
            setPreferenceValue(context, WIFI_INFO, jsonMap);
            return;
        }
        Map<String, String> wifiInfoMap = gson.fromJson(jsonMap, type);
        if (wifiInfoMap != null) {
            wifiInfoMap.remove(ssid);
            setPreferenceValue(context, WIFI_INFO, gson.toJson(wifiInfoMap));
            String jsonReult = (String) getPreferenceValue(context, WIFI_INFO, "");
            Log.e("SharePreference", "remove result: " + jsonReult);
        } else {
            Log.e("SharePreference", "error map!!!!");
        }
    }

    /**
     * 根据ssid获取密码
     *
     * @param context
     * @param ssid
     * @param defaultValue
     * @return
     */
    public static String getPwd(Context context, String ssid, String defaultValue) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Gson gson = new Gson();
        String jsonMap = (String) getPreferenceValue(context, WIFI_INFO, defaultValue);
        Log.e("SharePreference", "get value, jsonMap: " + jsonMap);
        if (TextUtils.equals(jsonMap, defaultValue) || gson.fromJson(jsonMap, type) == null) {
            jsonMap = gson.toJson(new HashMap<String, String>());
            setPreferenceValue(context, WIFI_INFO, jsonMap);
            return defaultValue;
        }
        Map<String, String> wifiInfoMap = gson.fromJson(jsonMap, type);
        Log.e("SharePreference", "get value, get map: " + wifiInfoMap);
        if (wifiInfoMap != null) {
            String pwd = wifiInfoMap.get(ssid);
            Log.e("SharePreference", "get value, ssid: " + ssid + ", pwd: " + pwd);
            return TextUtils.isEmpty(pwd) ? defaultValue : pwd;
        } else {
            Log.e("SharePreference", "error map!!!!");
        }
        return defaultValue;
    }

    public static boolean isInGuide(Context context) {
        return (boolean) PreferenceUtil.getPreferenceValue(context, FIRST_ENTER_APP, true);
    }

    public static void setGuide(Context context, boolean guide) {
        PreferenceUtil.setPreferenceValue(context, FIRST_ENTER_APP, guide);
    }

    public static boolean getBleMusicSwitch(Context context){
        return (boolean) PreferenceUtil.getPreferenceValue(context, BLE_MUSIC_SWITCH, false);
    }

    public static void setBleMusicSwitch(Context context, boolean value){
        PreferenceUtil.setPreferenceValue(context, BLE_MUSIC_SWITCH, value);
    }

    public static boolean isBoxMsgReceive(Context context) {
        return (boolean) PreferenceUtil.getPreferenceValue(context, RECEIVE_BOX_MSG, false);
    }
    /**
     * 设备联动开关状态
     *
     * @param context
     * @return
     */
    public static boolean getDeviceLinkSwitch(Context context) {
        return (boolean) PreferenceUtil.getPreferenceValue(context, DEVICE_LINK_SMOKE_STOVE, true);
    }
}
