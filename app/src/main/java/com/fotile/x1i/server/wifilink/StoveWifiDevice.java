package com.fotile.x1i.server.wifilink;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文件名称：StoveWifiDevice
 * 创建时间：2019/7/17 15:30
 * 文件作者：yaohx
 * 功能描述：灶蒸烤wifi设备
 */
public class StoveWifiDevice {

    //{"cmd_type":"response_probe","device_mac":"807D3A58E4A7","ip":"192.168.5.185","port":8888}

    public String cmd_type;
    /**
     * wifi模块的mac地址
     */
    public String mac;
    /**
     * wifi模块的ip
     */
    public String ip;
    /**
     * wifi模块的port
     */
    public int port;

    public boolean searching = true;
    /**
     * 连接状态
     */
    public int linkState;

    public String getDeviceName() {
        if (!TextUtils.isEmpty(mac)) {
            return "FOTILE_ZZK_X2.I_" + mac.substring(mac.length() - 4);
        }
        return "";
    }

    public StoveWifiDevice(String json, boolean searching) {
        this.searching = searching;
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (jsonObject.has("cmd_type")) {
                    cmd_type = jsonObject.getString("cmd_type");
                }
                if (jsonObject.has("device_mac")) {
                    mac = jsonObject.getString("device_mac");
                }
                if (jsonObject.has("ip")) {
                    ip = jsonObject.getString("ip");
                }
                if (jsonObject.has("port")) {
                    port = jsonObject.getInt("port");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "[mac:" + mac + "] [ip:" + ip + "] [port" + port + "]";
    }
}
