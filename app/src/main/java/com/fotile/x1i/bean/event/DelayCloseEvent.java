package com.fotile.x1i.bean.event;

public class DelayCloseEvent {
    boolean mIsInDelay;

    public DelayCloseEvent(boolean mIsInDelay) {
        this.mIsInDelay = mIsInDelay;
    }

    public boolean isInDelay() {
        return mIsInDelay;
    }
}
