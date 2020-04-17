/*
 * ************************************************************
 * 文件：BaseSocket.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 */
public abstract class BaseSocket {
    protected Handler mUIHandler;
    protected Object lock;

    public BaseSocket() {
        mUIHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                dealWithMsg(msg);
                return false;
            }
        });
        lock = new Object();
    }

    protected abstract void dealWithMsg(Message msg);

    protected void runOnUiThread(Runnable runnable) {
        mUIHandler.post(runnable);
    }
}
