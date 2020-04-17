package com.fotile.message.bean;

/**
 * 文件名称：MessageBean
 * 创建时间：2017-12-26 17:00
 * 文件作者：shihuijuan
 * 功能描述：通知及家庭备忘显示的数据结构
 */

public class MessageBean {
    /**
     * 内容
     */
    private String content;
    /**
     * 头像
     */
    private String avatar;

    public MessageBean(String content, String avatar) {
        this.content = content;
        this.avatar = avatar;
    }

    public MessageBean() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
