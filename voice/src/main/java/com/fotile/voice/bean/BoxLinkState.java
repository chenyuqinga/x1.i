/*
 * ************************************************************
 * 文件：BoxLinkState.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.bean;

public class BoxLinkState {
    private boolean mIsLinked;

    public BoxLinkState(boolean isLinked) {
        mIsLinked = isLinked;
    }

    public boolean isBoxLinked() {
        return mIsLinked;
    }
}
