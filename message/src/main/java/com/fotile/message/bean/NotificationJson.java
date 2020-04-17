package com.fotile.message.bean;

import android.text.TextUtils;


import com.fotile.message.util.MessageDateUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wanghouyu on 17-10-13.
 */

public class NotificationJson {
    /**
     * 数据库存储家庭备忘录json
     *
     * @param jsonData
     * @return
     */
    public MemorandumDb parseMemoFromJSONObject(String jsonData) {

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            MemorandumDb memorandumDb = new MemorandumDb();
            if (jsonObject.has("content")){
                memorandumDb.setContent(jsonObject.getString("content"));
            }
            if (jsonObject.has("from_id")){
                memorandumDb.setFrom_id(jsonObject.getInt("from_id"));
            }
            if (jsonObject.has("from_name")){
                memorandumDb.setFrom_name(jsonObject.getString("from_name"));
            }
            if (jsonObject.has("home_id")){
                memorandumDb.setHome_id(jsonObject.getString("home_id"));
            }
            if (jsonObject.has("type")){
                memorandumDb.setType(jsonObject.getString("type"));
            }
            if (jsonObject.has("title")){
                memorandumDb.setTitle(jsonObject.getString("title"));
            }
            if (jsonObject.has("home_name")){
                memorandumDb.setHome_name(jsonObject.getString("home_name"));
            }
            if (jsonObject.has("avatar")) {
                memorandumDb.setAvatar(jsonObject.getString("avatar"));
            }
            if (jsonObject.has("time")) {
                String time = jsonObject.getString("time");
                if (!TextUtils.isEmpty(time)) {
                    memorandumDb.setDate(time.substring(0,time.indexOf("T")).trim());
                    memorandumDb.setTime(time.substring(time.indexOf("T")+1,time.indexOf(".")).trim());
                }
            }
            return memorandumDb;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 数据库存储通知json
     *
     * @param jsonData
     * @return
     */
    public NotificationDb parseNotificationFromJSONObject(String jsonData) {

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            NotificationDb notificationDb = new NotificationDb();
            TimeBean timeBean = MessageDateUtil.getCurrentTimeToBean();
            notificationDb.setDate(timeBean.getDateFormat());
            notificationDb.setTime(timeBean.getTimeFormat());
            notificationDb.setContent(jsonObject.getString("content"));
            if (jsonObject.has("from_id"))
                notificationDb.setFrom_id(jsonObject.getInt("from_id"));
            if (jsonObject.has("from_name"))
                notificationDb.setFrom_name(jsonObject.getString("from_name"));
            if (jsonObject.has("type"))
                notificationDb.setType(jsonObject.getString("type"));
            if (jsonObject.has("avatar")) {
                notificationDb.setAvatar(jsonObject.getString("avatar"));
            }
            if (jsonObject.has("recipe_name")) {
                notificationDb.setRecipe_name("recipe_name");
            }
            return notificationDb;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
