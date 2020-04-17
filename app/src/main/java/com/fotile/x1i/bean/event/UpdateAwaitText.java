/**
 * 文件名称： UpdateAwaitText
 * 创建时间： 2019/7/30
 * 文件作者： chenyqi
 * 功能描述： 当前待机画面文字
 */


package com.fotile.x1i.bean.event;

public class UpdateAwaitText {

    public int type;
    /**
     * 指令发送给谁
     */
    public Class to_class;

    public UpdateAwaitText(int type, Class to_class) {
        this.type = type;
        this.to_class = to_class;
    }

}
