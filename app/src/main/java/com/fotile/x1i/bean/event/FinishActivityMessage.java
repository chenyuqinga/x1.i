package com.fotile.x1i.bean.event;

/**
 * 文件名称：FinishActivityMessage
 * 创建时间：2018/2/28 16:23
 * 文件作者：yaohx
 * 功能描述：关闭某一个activity的Event Message
 */
public class FinishActivityMessage{
    /**
     * 指令发送给谁
     */
    public Class to_class;

    public FinishActivityMessage(Class to_class) {
        this.to_class = to_class;
    }
}
