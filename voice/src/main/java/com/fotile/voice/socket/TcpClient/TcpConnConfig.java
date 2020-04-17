/*
 * ************************************************************
 * 文件：TcpConnConfig.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.TcpClient;

import com.fotile.voice.socket.TcpClient.helper.decode.AbsDecodeHelper;
import com.fotile.voice.socket.TcpClient.helper.decode.BaseDecodeHelper;
import com.fotile.voice.socket.TcpClient.helper.stickpackage.AbsStickPackageHelper;
import com.fotile.voice.socket.TcpClient.helper.stickpackage.VariableLenStickPackageHelper;
import com.fotile.voice.socket.TcpClient.helper.validation.AbsValidationHelper;
import com.fotile.voice.socket.TcpClient.helper.validation.BaseValidationHelper;
import com.fotile.voice.utils.CharsetUtil;
import com.fotile.voice.utils.StringValidationUtils;

import java.nio.ByteOrder;


/**
 * 连接配置
 */
public class TcpConnConfig {
    private String charsetName = CharsetUtil.UTF_8;//默认编码
    private long connTimeout = 5000;//连接超时时间
    private long receiveTimeout = 0;//接受消息的超时时间,0为无限大
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;//大端还是小端
    private AbsStickPackageHelper mStickPackageHelper = new VariableLenStickPackageHelper(
            ByteOrder.LITTLE_ENDIAN, 2, 4, 6);//解决粘包
    private AbsDecodeHelper mDecodeHelper = new BaseDecodeHelper();//解析数据
    private AbsValidationHelper mValidationHelper = new BaseValidationHelper();//消息验证
    private boolean isReconnect = false;//是否重连
    private int localPort = -1;
    private String mPingString = null;

    private TcpConnConfig() {
    }

    public String getCharsetName() {
        return charsetName;
    }

    public long getConnTimeout() {
        return connTimeout;
    }

    public boolean isReconnect() {
        return isReconnect;
    }

    public AbsValidationHelper getValidationHelper() {
        return mValidationHelper;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public AbsDecodeHelper getDecodeHelper() {
        return mDecodeHelper;
    }

    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    public AbsStickPackageHelper getStickPackageHelper() {
        return mStickPackageHelper;
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getPingString() {
        return mPingString;
    }

    public static class Builder {
        private TcpConnConfig mTcpConnConfig;

        public Builder() {
            mTcpConnConfig = new TcpConnConfig();
        }

        public TcpConnConfig create() {
            return mTcpConnConfig;
        }

        public Builder setCharsetName(String charsetName) {
            mTcpConnConfig.charsetName = charsetName;
            return this;
        }

        public Builder setByteOrder(ByteOrder byteOrder) {
            mTcpConnConfig.byteOrder = byteOrder;
            return this;
        }

        public Builder setValidationHelper(AbsValidationHelper validationHelper) {
            mTcpConnConfig.mValidationHelper = validationHelper;
            return this;
        }

        public Builder setConnTimeout(long timeout) {
            mTcpConnConfig.connTimeout = timeout;
            return this;
        }

        public Builder setIsReconnect(boolean b) {
            mTcpConnConfig.isReconnect = b;
            return this;
        }

        //bug
        @Deprecated
        public Builder setLocalPort(int localPort) {
            if (localPort > 0 && StringValidationUtils.validateRegex(localPort + "",
                    StringValidationUtils.RegexPort)) {
                mTcpConnConfig.localPort = localPort;
            }
            return this;
        }

        public Builder setStickPackageHelper(AbsStickPackageHelper helper) {
            mTcpConnConfig.mStickPackageHelper = helper;
            return this;
        }

        public Builder setDecodeHelper(AbsDecodeHelper helper) {
            mTcpConnConfig.mDecodeHelper = helper;
            return this;
        }

        public Builder setHeartBeat(String pingString) {
            mTcpConnConfig.mPingString = pingString;
            return this;
        }
    }
}
