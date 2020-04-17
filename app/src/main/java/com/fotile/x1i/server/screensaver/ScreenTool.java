package com.fotile.x1i.server.screensaver;


import org.greenrobot.eventbus.EventBus;

/**
 * 文件名称：ScreenTool
 * 创建时间：2017/12/15 15:27
 * 文件作者：yaohx
 * 功能描述：屏保相关工具类
 */
public class ScreenTool {

    /**
     * 屏保服务重置
     */
    public static final int SCREEN_TYPE_RESET = 0x01;
    /**
     * 3s后直接进入屏保
     */
    public static final int SCREEN_TYPE_RESET_NOW = 0x02;
    /**
     * 屏幕保持暗屏
     */
    public static final int SCREEN_TYPE_KEEP_DARK = 0x03;
    /**
     * 暂停屏保服务
     */
    public static final int SCREEN_TYPE_PAUSE = 0x04;
    /**
     * 键盘弹出时，屏幕亮屏1小时
     */
    public static final int SCREEN_TYPE_KEYBOARD_UP = 0x05;
    /**
     * 终止屏保逻辑
     */
    public static final int SCREEN_TYPE_STOP = 0x06;
    /**
     * 恢复屏保逻辑
     */
    public static final int SCREEN_TYPE_RESUME = 0x07;

    private static ScreenTool screenTool;

    private ScreenTool() {
    }

    public synchronized static ScreenTool getInstance() {
        if (null == screenTool) {
            screenTool = new ScreenTool();
        }
        return screenTool;
    }


    /**
     * 告诉屏保服务，需要重置定时器了
     */
    public synchronized void addResetData(String tag) {
        EventBus.getDefault().post(new ScreenServiceMessage(SCREEN_TYPE_RESET,ScreenSaverService.class));
    }

    /**
     * 告诉屏保服务，需要直接进入屏保
     */
    public synchronized void addResstNowData() {
        EventBus.getDefault().post(new ScreenServiceMessage(SCREEN_TYPE_RESET_NOW,ScreenSaverService.class));
    }

    public synchronized void addPause() {
        EventBus.getDefault().post(new ScreenServiceMessage(SCREEN_TYPE_PAUSE,ScreenSaverService.class));
    }

    public synchronized void addStop() {
        EventBus.getDefault().post(new ScreenServiceMessage(SCREEN_TYPE_STOP,ScreenSaverService.class));
    }
    public synchronized void addResume() {
        EventBus.getDefault().post(new ScreenServiceMessage(SCREEN_TYPE_RESUME,ScreenSaverService.class));
    }

    public synchronized void addKeepDark() {
        EventBus.getDefault().post(new ScreenServiceMessage(SCREEN_TYPE_KEEP_DARK,ScreenSaverService.class));
    }

    public synchronized void addKeyboardUp() {
        EventBus.getDefault().post(new ScreenServiceMessage(SCREEN_TYPE_KEYBOARD_UP,ScreenSaverService.class));
    }

}
