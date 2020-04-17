package com.fotile.x1i.bean.event;

public class WifiConnection {
    private boolean isConnected;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public WifiConnection(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
