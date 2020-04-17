package com.fotile.x1i.util;

/**
 * Description  EventBus事件发布管理类
 * Created by chenqiao on 2016/9/9.
 */
public class EventBusManager {
    private static EventBusManager ourInstance;
    public static final String TAG_TIME_FINISHED = "TAG_TIME_FINISHED";
    public static final String TAG_BOTTOM_BACKGROUND = "TAG_BOTTOM_BACKGROUND";
    public static final String TAG_BACK_HOME = "TAG_BACK_HOME";
    public static final String TAG_UPDATE_WEATHER = "TAG_UPDATE_WEATHER";
    public static final String TAG_ADD_FUN = "TAG_ADD_FUN";
    public static final String TAG_DELETE_FUN = "TAG_DELETE_FUN";
    public static final String TAG_DELETE_ALL_FUN = "TAG_DELETE_ALL_FUN";
    public static final String TAG_DELETE_CARD = "TAG_DELETE_CARD";
    public static final String TAG_BLE_CONNECTED = "TAG_BLE_CONNECTED";
    public static final String TAG_BLE_DISCONNECTED = "TAG_BLE_DISCONNECTED";
    public static final String TAG_UPDATE_MUSIC_INFO = "TAG_UPDATE_MUSIC_INFO";
    public static final String TAG_UPDATE_STOVE_INFO = "TAG_UPDATE_STOVE_INFO";
    public static final String TAG_UPDATE_NOTIFICATION = "TAG_UPDATE_NOTIFICATION";
    public static final String TAG_TOPBAR_NOTIFICATION = "TAG_TOPBAR_NOTIFICATION";
    public static final String TAG_UPDATE_WIND_LEVEL = "TAG_UPDATE_WIND_LEVEL";
    public static final String TAG_UPDATE_CRUISE_CHIMNEY_RESISTANCE = "TAG_UPDATE_CRUISE_CHIMNEY_RESISTANCE";
    public static final String TAG_UPDATE_CRUISE_LAMPBLACK_DENSITY = "TAG_UPDATE_CRUISE_LAMPBLACK_DENSITY";
    public static final String TAG_UPDATE_CRUISE_RESISTANCE_DENSITY = "TAG_UPDATE_CRUISE_RESISTANCE_DENSITY";
    public static final String TAG_CANCEL_SELECTION = "TAG_CANCEL_SELECTION";
    public static final String TAG_PROJECT_TEST_PANEL = "TAG_PROJECT_TEST_PANEL";
    public static final String TAG_UPDATE_WIND_CARD = "TAG_UPDATE_WIND_CARD";
    public static final String TAG_CLOSE_WIND_CARD = "TAG_CLOSE_WIND_CARD";
    public static final String TAG_SHOW_ERROR_MESSAGE = "TAG_SHOW_ERROR_MESSAGE";
    public static final String TAG_MAIN_BACK = "TAG_MAIN_BACK";
    public static final String TAG_LEFT_FIRE_OPEN = "TAG_LEFT_FIRE_OPEN";
    public static final String TAG_RIGHT_FIRE_OPEN = "TAG_RIGHT_FIRE_OPEN";
    public static final String TAG_STOVE_HEAT = "TAG_STOVE_HEAT";
    public static final String TAG_SMART_RECIPE_PLAY_CONFIRM = "TAG_SMART_RECIPE_PLAY_CONFIRM";
    public static final String TAG_TOPBAR_BOUND_STATE = "TAG_TOPBAR_BOUND_STATE";
    public static final String TAG_APP_MODE_HAND = "TAG_APP_MODE_HAND";
    public static final String TAG_USB_INSERT = "TAG_USB_INSERT";
    public static final String TAG_USB_MOUNTED = "TAG_USB_MOUNTED";
    public static final String TAG_VIDEO_PLAYANDPAUSE = "TAG_VIDEO_PLAYANDPAUSE";
    public static final String TAG_EXIT_STOVE_COOK = "TAG_EXIT_STOVE_COOK";
    public static final String TAG_POWER_BUTTON = "TAG_POWER_BUTTON";
    public static final String TAG_UPDATE_DEVICEID = "TAG_UPDATE_DEVICEID";

    public static EventBusManager getInstance() {
        if (ourInstance == null) {
            synchronized (EventBusManager.class) {
                if (ourInstance == null) {
                    ourInstance = new EventBusManager();
                }
            }
        }
        return ourInstance;
    }

    private EventBusManager() {
    }

}
