/*
 * ************************************************************
 * 文件：ExceptionUtils.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.utils;

/**
 */
public class ExceptionUtils {
    private static final String TAG = ExceptionUtils.class.getSimpleName();

    public static void throwException(String message) {
        throw new IllegalStateException(TAG + " : " + message);
    }
}
