package com.fotile.x1i.bean.event;

/**
 * 文件名称：BaseEventMessage
 * 创建时间：2018/7/5 15:50
 * 文件作者：yaohx
 * 功能描述：用于EventBus类之间传输数据的Message基类
 */
public class BaseEventMessage {
    /**
     * 指定消息给哪一个类去处理
     */
    public Class to_class;
    /**
     * 消息来自哪一个类
     */
    public Class from_class;
    /**
     * 控制指令
     */
    public int command;

    public BaseEventMessage(Class from_class, Class to_class, int command) {
        this.from_class = from_class;
        this.to_class = to_class;
        this.command = command;
    }

    public BaseEventMessage(Class from_class, int command) {
        this.from_class = from_class;
        this.command = command;
    }

    public BaseEventMessage(Class to_class){
        this.to_class = to_class;
    }
}
