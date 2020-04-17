/*
 * ************************************************************
 * 文件：BaseValidationHelper.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.TcpClient.helper.validation;

/**
 */
public class BaseValidationHelper implements AbsValidationHelper {
    @Override
    public boolean execute(byte[] msg) {
        return true;
    }
}
