/*
 * ************************************************************
 * 文件：BaseStickPackageHelper.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.TcpClient.helper.stickpackage;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * 最简单的做法，不处理粘包，直接读取返回，最大长度256
 */



public class BaseStickPackageHelper {
    private int maxLen = 10*1024;//最大长度256

    public BaseStickPackageHelper() {
    }

    public BaseStickPackageHelper(int maxLen) {
        if (maxLen > 0) {
            this.maxLen = maxLen;
        }
    }

    public byte[] execute(InputStream is) {
        byte[] bytes = new byte[maxLen];
        int len;
        try {
            if ((len = is.read(bytes)) != -1) {
                byte[] result= Arrays.copyOf(bytes, len);
                Log.e("TcpClient", "tcp bytes length: " + result.length + ", len: " + len);
                return result;
            }
            Log.e("TcpClient", "len: " + len);
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return null;
    }

    public boolean isEnough() {
        return false;
    }
}
