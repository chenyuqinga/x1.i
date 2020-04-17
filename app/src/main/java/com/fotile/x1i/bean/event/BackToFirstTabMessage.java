package com.fotile.x1i.bean.event;

/**
 * 文件名称： BackToFirstTabMessage
 * 创建时间： 2019/8/8
 * 文件作者： chenyqi
 * 功能描述： 回到菜谱第一个标签页
 */


public class BackToFirstTabMessage {
    /**
     * 指令发送给谁
     */
    public Class to_class;

    public BackToFirstTabMessage(Class to_class) {
        this.to_class = to_class;
    }
}
