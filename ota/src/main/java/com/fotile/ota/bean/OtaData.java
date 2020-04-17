package com.fotile.ota.bean;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 项目名称：Ota
 * 创建时间：2018/10/16 17:12
 * 文件作者：yaohx
 * 功能描述：和ota相关的数据该类对应保存到本地文件
 */
public class OtaData {
    /**
     * downloaded_version = VERSION_UPDATED
     * 标识本机安装的固件和下载的固件同步
     */
    public static final String VERSION_UPDATED = "version_update_completed";

    public OtaData() {

    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public OtaData(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("downloaded_version")) {
                downloaded_version = jsonObject.getString("downloaded_version");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载完成的固件包版本
     */
    public String downloaded_version;

}
