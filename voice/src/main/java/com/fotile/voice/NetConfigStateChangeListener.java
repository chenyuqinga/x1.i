/*
 * ************************************************************
 * 文件：NetConfigStateChangeListener.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice;

import com.fotile.voice.bean.NluResult;

public interface NetConfigStateChangeListener {

    void onNetConfigStateChange(CommonConst.ConfigState state);

    void onNetConfigException(CommonConst.ConfigError errorType);

    void onDialogStateChange(CommonConst.DialogState state);

    void onHeard(int type, String asrResult);

    void onSpeak(String content);

    void onUnderStand(NluResult nluResult);

    void onWakeUp(String content);

//    void onMusicStart(ArrayList<MusicBean> musicList);
    void onMusicStart(String musicList);

    void onMusicStop();

    void onRecipesGet(String recipes);

    void onVboxError(String error);

//    void onSleep(String reason,String error);

//    void onNoVoice(String content);
}
