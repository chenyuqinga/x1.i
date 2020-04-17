package com.fotile.message.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by wanghouyu on 17-9-28.
 */
@Entity
public class MemorandumDb {
    @Id(autoincrement = true)
    private Long _id;
    private String date;
    private String time;
    private String content;
    private int from_id;
    private String from_name;
    private String type;
    private String title;
    private String home_name;
    private String home_id;
    private String avatar;
    @Generated(hash = 1241250716)
    public MemorandumDb(Long _id, String date, String time, String content,
                        int from_id, String from_name, String type, String title,
                        String home_name, String home_id, String avatar) {
        this._id = _id;
        this.date = date;
        this.time = time;
        this.content = content;
        this.from_id = from_id;
        this.from_name = from_name;
        this.type = type;
        this.title = title;
        this.home_name = home_name;
        this.home_id = home_id;
        this.avatar = avatar;
    }
    @Generated(hash = 768791820)
    public MemorandumDb() {
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
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getFrom_id() {
        return this.from_id;
    }
    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }
    public String getFrom_name() {
        return this.from_name;
    }
    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getHome_name() {
        return this.home_name;
    }
    public void setHome_name(String home_name) {
        this.home_name = home_name;
    }
    public String getHome_id() {
        return this.home_id;
    }
    public void setHome_id(String home_id) {
        this.home_id = home_id;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
