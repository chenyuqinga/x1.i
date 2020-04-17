/*
 * ************************************************************
 * 文件：DefaultNormalReaderProtocol.java  模块：voice  项目：Z1.5
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.TcpClient.receive;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DefaultNormalReaderProtocol implements IReaderProtocol {

    @Override
    public int getHeaderLength() {
        // TODO: 2019/8/1 暂时将default值改为6
//        return 4;
        return 6;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        if (header == null || header.length < getHeaderLength()) {
            return 0;
        }
        ByteBuffer bb = ByteBuffer.wrap(header, 4, 2);
        bb.order(byteOrder);
        return (int) bb.getShort();
    }
}