/*
 * ************************************************************
 * 文件：ClientActionDispatcher.java  模块：voice  项目：Z1.5
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.socket.TcpClient.receive;

import java.io.Serializable;

public class ClientActionDispatcher implements IStateSender {

    private ClientActionListener mActionListener;

    public ClientActionDispatcher(ClientActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public void sendBroadcast(final String action, final Serializable serializable) {
        if (mActionListener == null) {
            return;
        }
        dispatch(action, serializable);
    }

    @Override
    public void sendBroadcast(String action) {
        sendBroadcast(action, null);
    }

    private void dispatch(String action, Serializable serializable) {
        switch (action) {
            case IAction.Client.ACTION_READ_THREAD_START: {
                try {
                    mActionListener.onClientReadReady();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case IAction.Client.ACTION_READ_THREAD_SHUTDOWN: {
                try {
                    Exception exception = (Exception) serializable;
                    mActionListener.onClientReadDead(exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case IOAction.ACTION_READ_COMPLETE: {
                try {
                    OriginalData data = (OriginalData) serializable;
                    mActionListener.onClientRead(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            /*case IAction.Client.ACTION_WRITE_THREAD_START: {
                try {
                    mActionListener.onClientWriteReady();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case IAction.Client.ACTION_WRITE_THREAD_SHUTDOWN: {
                try {
                    Exception exception = (Exception) serializable;
                    mActionListener.onClientWriteDead(exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case IOAction.ACTION_WRITE_COMPLETE: {
                try {
                    ISendable data = (ISendable) serializable;
                    mActionListener.onClientWrite(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }*/
        }
    }

    public interface ClientActionListener {
        void onClientReadReady();

//        void onClientWriteReady();

        void onClientReadDead(Exception e);

//        void onClientWriteDead(Exception e);

        void onClientRead(OriginalData originalData);

//        void onClientWrite(ISendable sendable);
    }
}
