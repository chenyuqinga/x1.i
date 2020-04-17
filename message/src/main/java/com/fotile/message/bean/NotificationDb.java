package com.fotile.message.bean;

import com.google.gson.Gson;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by wanghouyu on 17-9-28.
 */
@Entity
public class NotificationDb {
    @Id(autoincrement = true)
    private Long _id;
    private String date;
    private String time;
    private int from_id;
    private String content;
    private String type;
    private String avatar;
    private String recipe_name;
    private String from_name;
    @Generated(hash = 386008309)
    public NotificationDb(Long _id, String date, String time, int from_id,
            String content, String type, String avatar, String recipe_name,
            String from_name) {
        this._id = _id;
        this.date = date;
        this.time = time;
        this.from_id = from_id;
        this.content = content;
        this.type = type;
        this.avatar = avatar;
        this.recipe_name = recipe_name;
        this.from_name = from_name;
    }
    @Generated(hash = 1519093108)
    public NotificationDb() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getFrom_id() {
        return this.from_id;
    }
    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getRecipe_name() {
        return this.recipe_name;
    }
    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }
    public String getFrom_name() {
        return this.from_name;
    }
    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
