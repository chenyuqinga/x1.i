/*
 * ************************************************************
 * 文件：CommonConst.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice;

public class CommonConst {

    public static final int POOL_SIZE_PER_CORE = 5;

    public static final String MULTI_CAST_IP = "239.255.255.250";
    public static final int NET_PROBE_PORT = 8000;
    public static final int BOX_PROBE_PORT = 8001;
    public static final int PROBE_BOX_PORT = 9000;

    public static final int TYPE_LOCAL_COMMAND = 21000;
    public static final int TYPE_ONLINE_SPEAK = 21001;

    public static final int HOOD_DEVICE_TPYE = 0x0400;

    public static final int CMD_ADD = 101;
    public static final int CMD_REDUCE = -1;

    //    public static final String BROADCAST_IP = "192.168.255.255";

    public static final String REQUEST_INFO = "request_info";
    public static final String RESPONSE_INFO = "response_info";
    public static final String REQUEST_DISCONNECT = "request_disconnect";
    public static final String DEVICE_PROBE = "device_probe";
    public static final String REQUEST_JOIN = "request_join";
    public static final String RESPONSE_JOIN = "response_join";
    public static final String HOME_ID_GET = "home_id_get";
    public static final String RESPONSE_LEAVE = "response_leave";
    public static final String VBOX_STATUS = "vbox_status";
    public static final String VBOX_ERROR = "vbox_error";
    public static final String MUSIC = "music";
    public static final String COOK_DETAIL = "cook_detail";

    public static final String REQUEST_HEARTBEAT = "request_heartbeat";

    public static final String CONTROL = "control";
    public static final String TEXT = "text";
    //    public static final String ERROR = "error";

    public static final String CONTINUE = "communal.mediacontrol.continue";
    public static final String PAUSE = "communal.mediacontrol.pause";
    public static final String STOP = "communal.mediacontrol.stop";
    public static final String REPLAY = "communal.mediacontrol.replay";
    public static final String PREVIOUS = "communal.mediacontrol.prev";
    public static final String NEXT = "communal.mediacontrol.next";
    public static final String SINGLE_CIRCULATION = "communal.mediacontrol.singlecirculation";
    public static final String CLOSE_SINGLE_CIRCULATION = "communal.mediacontrol.closesinglecirculation";
    public static final String ANOTHER = "communal.mediacontrol.another";

    public static final String MEDIA_CONTROL = "communal.mediacontrol";

    public static final String AIR_STEWARD = "hood.air.steward";
    public static final String AIR_CRUISE = "hood.air.cruise";
    public static final String LAMP_OPERATION = "hood.lamp.operation";
    public static final String SCREEN_LOCK = "hood.screen.lock";
    public static final String STOVE_CONTROL_CLOSE = "hood.stove_control.close";
    public static final String STOVE_SIDE_CONFIRM = "hood.stove_control.side_confirm";
    public static final String STOVE_SIDE_CANCEL_COUNTDOWN = "hood.stove_control.cancel_countdown";
    public static final String BRIGHTNESS_CONTROL = "hood.brightness.control";
    public static final String BRIGHTNESS_ADJUST = "hood.brightness.adjust";
    public static final String OPERATION = "hood.operation";
    public static final String CLOSE_DELAY = "hood.close_delay";
    public static final String CLOSE_DELAY_OPEN = "hood.close_delay.open";
    public static final String CLOSE_DELAY_CANCEL = "hood.close_delay.cancel";
    public static final String CLOSE_CONFIRM = "hood.close_confirm";
    public static final String CLOSE_CANCEL = "hood.close_cancel";
    public static final String DEVICELINK_SPECIFIC = "hood.deviceLinkSpecific";
    public static final String DEVICELINK = "hood.deviceLink";
    public static final String DEVICELINK_HELP = "hood.deviceLinkHelp";
    public static final String AIR_CONTROL = "hood.air.control";
    public static final String COUNTDOWN_START = "hood.countdown.start";
    public static final String COUNTDOWN_CANCEL = "hood.countdown.cancel";
    public static final String SMART_RECIPE = "hood.smart_recipe.operation";
    public static final String IQIYI_OPERATION = "hood.mediacontrol.iqiyi.operation";
    public static final String VOLUME_ADJUST = "hood.mediacontrol.volumeAdjust";
    public static final String VOLUME_SET = "hood.mediacontrol.volumeSet";
    public static final String STEP_PREVIOUS = "hood.step.previous";
    public static final String STEP_NEXT = "hood.step.next";
    public static final String SELECTION = "hood.selection";
    public static final String COOKING_OPERATION = "hood.cook.operation";

    public static final String HOOD_COOKER_LINK_DESCRIPTION = "当烟灶联动开启后，操作灶具时可以联动油烟机。";
    public static final String HOOD_STEAM_LINK_DESCRIPTION = "烹饪蒸箱菜谱或者蒸微一体机的智能菜谱时，检测到设备开门会后会自动打开油烟机，祛除异味，检测到关门后，烟机继续运作2分钟。（此功能需与“大厨管家”连接）";
    public static final String HOOD_OVEN_LINK_DESCRIPTION = "烹饪烤箱的河海鲜类和肉类智能菜谱时，检测到设备开门后会自动打开油烟机，祛除异味，检测到关门后，烟机继续运作2分钟。（此功能需与“大厨管家”连接）";

    public static final String ERROR_INFO_HOME_ID_ILLEGAL = "设备所属家庭不一致，请重置盒子并重新添加";
    public static final String ERROR_TITLE_ADD_TIMEOUT = "添加超时";
    public static final String ERROR_INFO_UDP_RECEIVE_TIMEOUT = "您还可以：\n1.检查盒子是否放置正确；\n2.尝试重置盒子（具体方法见说明书）；\n3.确认设备网络连接是否正常";

    public static final String LAST_STEP = "已经是最后一步了哦";
    public static final String FIRST_STEP = "已经是第一步了哦";
    public static final String OK = "好的";

    public static final String SPEECH_READY = "speech_ready";
    public static final String RECIPE_NEXT_STEP = "recipe_next_step";
    public static final String RECIPE_PREVIOUS_STEP = "recipe_previous_step";


    public static final int ONE_SECOND = 1000;
    public static final int ONE_MINUTE = ONE_SECOND * 60;
    public static final String SPECIFIED_TAB = "specified_tab";

//    public static final String BASE_URL = "http://47.96.38.200:5001/fotile_dm/common/";
// TODO: 2019/6/13 测试时用
    public static final String BASE_URL = "http://47.96.38.200:7100/fotile_dm/common/";
    public static final String HELP_CONTENT = "vpa/helper/";

    public static String getCommandAsr(String command) {
        return null;
    }


    public enum ConfigState {
        CONNECT_BOX_SUCCESS,
        PUB_AP_ENABLE,
        BOX_ACTIVELY_DISCONNECT,
        BOX_DISCONNECT,
//        BOX_POWER_OFF,
        START_TCP,
//        SOCKET_CONNECT_TIME_OUT,
        CONNECT_ACTUALLY_NET,
        BOX_DISCONNECTED
    }

    public enum ConfigError {
        AP_TIME_OUT, UDP_RECEIVE_TIMEOUT, CONNECT_WIFI_FAILED,
        BOX_CONNECTING,//已经绑定过盒子
        ILLEGAL_HOME_ID,//homeId不合规
        CONNECT_BOX_FAILED, HEARTBEAT_TIMEOUT, WIFI_PASSWORD_EMPTY
    }

    public enum DialogState {
        idle, listen, listen_actively, understanding, stop_tts
    }

    public enum DialogContent {
        ASR_CONTENT_GET, TTS_CONTENT_GET, NO_VOICE_CONTENT_GET, BROADCAST_CONTENT_GET
    }

    public static final String test_music_list = "[{\"extra\":{\"origintitle\":\"火星人来过\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.45.59/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.45.59/443277013.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"火星人来过\"},{\"extra\":{\"origintitle\":\"你还要我怎样\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.45.59/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.45.59/27955653.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"你还要我怎样\"},{\"extra\":{\"origintitle\":\"天份\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"\",\"label\":\"天份\",\"linkUrl\":\"http://oss.iot.aispeech.com/dcmp/A3063BBDF087917DFFCD1240DD1C821A.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"天份\"},{\"extra\":{\"origintitle\":\"演员\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.45.59/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.45.59/32507038.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"演员\"},{\"extra\":{\"origintitle\":\"高尚\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.45.59/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.45.59/466122271.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"高尚\"},{\"extra\":{\"origintitle\":\"绅士\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.45.59/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.45.59/32192436.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"绅士\"},{\"extra\":{\"origintitle\":\"我好像在哪见过你\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.36.22/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.36.22/417859631.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"我好像在哪见过你\"},{\"extra\":{\"origintitle\":\"肆无忌惮\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"\",\"label\":\"肆无忌惮\",\"linkUrl\":\"http://oss.iot.aispeech.com/dcmp/02/88/CiAB81thdm2AKLF2AB-_R9D4y0A956.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"肆无忌惮\"},{\"extra\":{\"origintitle\":\"初学者\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.36.22/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.36.22/412902689.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"初学者\"},{\"extra\":{\"origintitle\":\"暧昧\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.45.59/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.45.59/471385043.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"暧昧\"},{\"extra\":{\"origintitle\":\"我害怕\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.36.22/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.36.22/494858544.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"我害怕\"},{\"extra\":{\"origintitle\":\"那是你离开了北京的生活\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"\",\"label\":\"那是你离开了北京的生活\",\"linkUrl\":\"http://oss.iot.aispeech.com/dcmp/02/88/CiAB81thdnSAZDtNACDIBCw5ZiY949.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"那是你离开了北京的生活\"},{\"extra\":{\"origintitle\":\"动物世界\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.45.59/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.45.59/468517654.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"动物世界\"},{\"extra\":{\"origintitle\":\"像风一样\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.45.59/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.45.59/516657051.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"像风一样\"},{\"extra\":{\"origintitle\":\"别\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.36.22/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.36.22/515803379.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"别\"},{\"extra\":{\"origintitle\":\"刚刚好\",\"resType\":\"mp3\",\"source\":0},\"imageUrl\":\"http://47.98.36.22/c/5781.jpg\",\"label\":\"\",\"linkUrl\":\"http://47.98.36.22/464009783.mp3\",\"subTitle\":\"薛之谦\",\"title\":\"刚刚好\"}]";

}
