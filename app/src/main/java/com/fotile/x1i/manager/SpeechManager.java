/*
 * ************************************************************
 * 文件：SpeechManager.java  模块：app  项目：Z1.5
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.x1i.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.music.listener.MusicPlayerStateListener;
import com.fotile.music.manager.MusicManager;
import com.fotile.music.model.MusicBean;
import com.fotile.voice.CommonConst;
import com.fotile.voice.NetConfigBusiness;
import com.fotile.voice.NetConfigStateChangeListener;
import com.fotile.voice.bean.BoxLinkState;
import com.fotile.voice.bean.Command;
import com.fotile.voice.bean.DelayTime;
import com.fotile.voice.bean.DeviceReceive;
import com.fotile.voice.bean.LocalDeviceReceive;
import com.fotile.voice.bean.NluResult;
import com.fotile.voice.bean.SpeechRecipeBean;
import com.fotile.voice.socket.SocketUtil;
import com.fotile.voice.utils.WifiUtil;
import com.fotile.voice.wifi.WifiAPManager;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.MainActivity;
import com.fotile.x1i.activity.control.WindControlActivity;
import com.fotile.x1i.activity.quicktool.QuickToolActivity;
import com.fotile.x1i.activity.recipe.RecipesCategoryActivity;
import com.fotile.x1i.activity.recipe.fragment.RecipesCategoryFragment;
import com.fotile.x1i.activity.setting.DeviceLinkageActivity;
import com.fotile.x1i.activity.setting.WifiListActivity;
import com.fotile.x1i.activity.setting.dialog.BoxLinkPreDialog;
import com.fotile.x1i.bean.event.BoxLinkingStep;
import com.fotile.x1i.bean.event.DelayCloseEvent;
import com.fotile.x1i.bean.event.TimerServerMessage;
import com.fotile.x1i.bean.event.UpdateInfo;
import com.fotile.x1i.bean.speech.DelayOffParam;
import com.fotile.x1i.dailog.BoxLinkExceptionDialog;
import com.fotile.x1i.dailog.DelayCloseDialog;
import com.fotile.x1i.server.TimerServer;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.server.smokelink.SmokeStoveLinkAction;
import com.fotile.x1i.server.wifilink.StoveControl;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.util.Constant;
import com.fotile.x1i.widget.BottomView;
import com.fotile.x1i.widget.SnakeBar;
import com.fotile.x1i.widget.SpeechFloatingLayer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.player.XMediaPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fotile.device.cookerprotocollib.helper.DeviceControl;

//import fotile.ble.observer.link.LinkFactory;

public class SpeechManager {

    public static final String TAG = SpeechManager.class.getSimpleName();

    private static volatile SpeechManager mSpeechManager = null;

    private boolean mIsNeedFeedbackOnMetaDataChanged;
    private JsonObject mFeedbackMsg;
    private NluResult mNluResult;
    private Context mContext;
    private int mHomeId = 0;
    private DelayTime mStoveCloseDelayTime;
    private int mCurrentBrightness;
    private int mShowBrightness;
    /**
     * 连接是否成功
     */
    private boolean mConnectSuccess;
    private int mCurrentVolume;
    private int mMaxVolume;
    private boolean mIsNeedResponse;
    private boolean mIsMusicStateChangedAfterWakeUp;
    private boolean mIsMusicBePaused;
    /**
     * 定义从收到“request disconnected”开始到连接成功/失败为止的过程为连接中状态，当未连接且此标志位为true时即处于该状态
     */
    private boolean mIsConnecting;

    private CharSequence mMusicTitle;
    private CharSequence mMusicSubtitle;
    /**
     * 是否处于延时关机中
     */
    private boolean mIsInDelayClosing;
    /**
     * 默认五档
     */
    public static final int DEFAULT_WIND_VALUE = 0xF5;

    private static final int MSG_RECEIVED_WORDS = 2001;
    private static final int MSG_OUTPUT_WORDS = 2002;
    private static final int MSG_UNDERSTAND_RESULT = 2003;
    private static final int MSG_WAKE_UP = 2004;
    private static final int MSG_FALL_SLEEP = 2005;
    private static final int MSG_SPEAK_BEGIN = 2006;
    private static final int MSG_TIME_END = 2007;
    private static final int MSG_MUSIC_START = 2008;
    private static final int MSG_MUSIC_STOP = 2009;
    private static final int MSG_GET_RECIPES = 2010;
    private static final int MSG_STATE_IDLE = 2011;
    private static final int MSG_STATE_LISTEN = 2012;
    private static final int MSG_STATE_LISTEN_ACTIVELY = 2013;
    private static final int MSG_STATE_UNDERSTANDING = 2014;
    private static final int MSG_STATE_STOP_TTS = 2015;
    private static final int MSG_ENTER_SCREEN_SAVER = 2016;

    private static final int OPERATION_OPEN = 30011;
    private static final int OPERATION_CLOSE = 30012;
    private static final int OPERATION_ILLEGAL = 30013;


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RECEIVED_WORDS:
                    String asrResult = (String) msg.obj;
                    if (!TextUtils.isEmpty(asrResult)) {
                        showAsrResult(asrResult);
                    }
                    break;
                case MSG_OUTPUT_WORDS:
                    String ttsContent = (String) msg.obj;
                    if (!TextUtils.isEmpty(ttsContent)) {
                        Log.e(TAG, "show tts content: " + ttsContent);
                        showTtsContent(ttsContent);
                        //                        SpeechFloatingLayer.get(mContext).showSpeakAnim();
                    }
                    break;
                case MSG_UNDERSTAND_RESULT:
                    NluResult nluResult = (NluResult) msg.obj;
                    processNluResult(nluResult);
                    break;
                case MSG_MUSIC_START:
                    Log.e(TAG, "music start");
                    Bundle musicData = msg.getData();
                    String music_list = musicData.getString("music_list");
                    List<MusicBean> musicList = JSON.parseArray(music_list, MusicBean.class);
                    if (musicList != null && !musicList.isEmpty()) {
                        ArrayList<MusicBean> musicArrayList = new ArrayList<>(musicList);
                        Log.e(TAG, "musicList: " + musicList);
                        startPlayDuiMusic(musicArrayList);
                    }
                    break;
                case MSG_MUSIC_STOP:
                    //                    if (MusicManager.getInstance().isDuiMode()) {
                    //                        MusicManager.getInstance().stop();
                    //                    }
                    AppUtil.stopPlayMusic(mContext);
                    break;
                case MSG_GET_RECIPES:
                    Bundle recipeData = msg.getData();
                    String recipes = recipeData.getString("recipes");
                    Log.e(TAG, "origin recipe: " + recipes);
                    List<SpeechRecipeBean> speechRecipeBeans = JSON.parseArray(recipes,
                            SpeechRecipeBean.class);
                    if (speechRecipeBeans != null && !speechRecipeBeans.isEmpty()) {
                        SpeechFloatingLayer.get(mContext).showRecipe(
                                new ArrayList<>(speechRecipeBeans));
                    }
                    break;
                case MSG_WAKE_UP:
                    String content = null;
                    Bundle data = msg.getData();
                    if (data != null) {
                        content = data.getString("content");
                    }
                    wakeup(content);
                    break;
                case MSG_FALL_SLEEP:

                    break;
                case MSG_SPEAK_BEGIN:

                    break;
                case MSG_STATE_IDLE:
                    SpeechFloatingLayer.get(mContext).showWaitingAnim();
                    SpeechFloatingLayer.get(mContext).dismissByIdle();
                    if (mIsMusicBePaused && !mIsMusicStateChangedAfterWakeUp) {
                        if (MusicManager.getInstance().isDuiMode()) {
                            MusicManager.getInstance().play();
                        } else if (XmPlayerManager.getInstance(mContext).getPlayerStatus() ==
                                   XMediaPlayer.PAUSED) {
                            XmPlayerManager.getInstance(mContext).play();
                        }
                    }
                    mIsMusicBePaused = false;
                    mIsMusicStateChangedAfterWakeUp = false;
                    break;
                case MSG_STATE_LISTEN:
                    SpeechFloatingLayer.get(mContext).showHeardAnim();
                    SpeechFloatingLayer.get(mContext).interruptDismiss();
                    break;
                case MSG_STATE_LISTEN_ACTIVELY:
                    SpeechFloatingLayer.get(mContext).showActivelyHeardAnim();
                    break;
                case MSG_STATE_UNDERSTANDING:
                    SpeechFloatingLayer.get(mContext).showUnderStandAnim();
                    break;
                case MSG_STATE_STOP_TTS:

                    break;
                case MSG_TIME_END:

                    break;
                case MSG_ENTER_SCREEN_SAVER:
                    //                    ScreenTool.getInstance().addResetData("");
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    public void registerApplication(Context context) {
        Log.e(TAG, "registerApplication: " + context);
        this.mContext = context;
        EventBus.getDefault().register(this);
        initNetConfigBusiness();
    }

    public void updateContext(Context context) {
        this.mContext = context;

    }

    private void wakeup(String content) {
        //唤醒时暂停音乐
        if (MusicManager.getInstance().isDuiMode() && MusicManager.getInstance().isPlaying()) {
            MusicManager.getInstance().pause();
            mIsMusicBePaused = true;
        }
        if (XmPlayerManager.getInstance(mContext).isPlaying()) {
            XmPlayerManager.getInstance(mContext).pause();
            mIsMusicBePaused = true;
        }
        mIsMusicStateChangedAfterWakeUp = false;
        //若有屏保则移除屏保
        if ((Boolean) PreferenceUtil.getPreferenceValue(mContext, PreferenceUtil.IS_SCREEN_LOCK,
                false)) {
            Log.e("lock", "wake up and remove lock");
            AppUtil.removeLockScreen(mContext);
        }
        //        if (Tool.isScreenLight(mContext)) {
        Tool.lightScreen(mContext);
        //        }
        if (!TextUtils.isEmpty(content)) {
            SpeechFloatingLayer.get(mContext).show();
            SpeechFloatingLayer.get(mContext).showAsrContent(content);
        } else {
            SpeechFloatingLayer.get(mContext).show();
        }
        SpeechFloatingLayer.get(mContext).showWakeUpAnim();
    }

    public void showAsrResult(String asrResult) {
        SpeechFloatingLayer.get(mContext).showAsrContent(asrResult.replaceAll("\\s*", ""));
    }

    public void showTtsContent(String ttsContent) {
        SpeechFloatingLayer.get(mContext).showTtsContent(ttsContent);
        SpeechFloatingLayer.get(mContext).showSpeakAnim();
    }

    private void processNluResult(NluResult nluResult) {
        mIsNeedResponse = nluResult.isNeed_response();
        //        if (!nluResult.isNeed_response()) {
        //            Log.e(TAG, "d/on't need response, do nothing");
        //        } else {
        ScreenTool.getInstance().addResetData("process speech command");
        if (!nluResult.isOnline()) {
            //local command
            processLocalCommand(nluResult.getCommand(), nluResult);
        } else {
            String commandString = nluResult.getCommand();
            Command command = JSON.parseObject(commandString, Command.class);
            if (command != null) {
                processOnlineCommand(command, nluResult);
            } else {
                Log.e(TAG, "illegal command!!!!");
            }
        }
        //        }

    }

    private void processLocalCommand(String api, NluResult nluResult) {
        Log.e(TAG, "handle local command " + api);
        String msg = null;
        //亮屏
        Tool.lightScreen(mContext);
        //暂停屏保
        ScreenTool.getInstance().addPause();
        // TODO: 2019/6/17 移除锁屏

        //xi.i没有菜谱录制
        /*if (DeviceReportManager.getInstance().isRecipeRecording()) {
            // TODO: 2019/6/6 屏蔽语音功能
            String result = "当前不可以使用语音功能哦";
            feedBackLocalMsg(result, nluResult);
            return;
        }*/
        //x1.i没有智能菜谱烹饪
        /*if (DeviceReportManager.getInstance().isRecipeCooking()) {
            //            feedBackLocalMsg("智能菜谱烹饪中，不能进行其他操作哦", nluResult);
            //            return;
            if (TextUtils.equals(api, "Prev") || TextUtils.equals(api, "PrevPage")) {//上一步
                String result = ((RecipesPlayActivity) AppUtil.getCurrentActivity(mContext))
                        .previousStep();
                feedBackLocalMsg(result, nluResult);
            } else if (TextUtils.equals(api, "Next") || TextUtils.equals(api, "NextPage")) {//下一步
                String result = ((RecipesPlayActivity) AppUtil.getCurrentActivity(mContext))
                        .nextStep();
                feedBackLocalMsg(result, nluResult);
            } else {
                feedBackLocalMsg("智能菜谱烹饪中，不能进行其他操作哦", nluResult);
                return;
            }
        }*/
        switch (api) {
            case "Prev"://上一步
            case "PrevPage"://上一页
                /*if (TextUtils.equals(AppUtil.getCurrentActivityName(mContext),
                        RecipesDetailStepActivity.class.getSimpleName())) {
                    msg = ((RecipesDetailStepActivity) AppUtil.getCurrentActivity(mContext))
                            .previousPage();
                } else {
                    msg = "当前不在菜谱详情页面哦";
                }*/
                //                msg = "暂不支持该功能哦";
                //                break;
            case "Next"://下一步
            case "NextPage"://下一页
                /*if (TextUtils.equals(AppUtil.getCurrentActivityName(mContext),
                        RecipesDetailStepActivity.class.getSimpleName())) {
                    msg = ((RecipesDetailStepActivity) AppUtil.getCurrentActivity(mContext))
                            .nextPage();
                } else {
                    msg = "当前不在菜谱详情页面哦";
                }*/
                //                msg = "暂不支持该功能哦";
                //                break;
            case "First"://第一个
            case "Second"://第二个
            case "Third"://第三个
            case "Fourth"://第四个
            case "Fifth"://第五个
                //                handleRecipeDisplay(api, nluResult);
                //                return;
                msg = "暂不支持该功能哦";
                break;
            case "Return"://返回
                BottomView.getInstance(mContext).back(true);
                msg = CommonConst.OK;
                break;
            case "CloseRangeHood"://关闭烟机
                //                mHandler.sendEmptyMessageDelayed(MSG_ENTER_SCREEN_SAVER, 300);
            case "CloseFan"://关闭风机
                if (DeviceReportManager.getInstance().isInWorkState(
                        DeviceReportManager.WORK_INIT)) {//处于关闭状态
                    msg = "主人，烟机已经处于关闭状态了哦";
                } else {
                    if (!mIsInDelayClosing) {
                        AppUtil.showDelayCloseDialog(mContext,
                                DelayCloseDialog.DELAY_TYPE.DELAY_TYPE_CLOSE);
                    }
                    msg = "好的";
                }
                //                else if (PreferenceUtil.getDelayShutSwitch(mContext)) {//设置了延时关机
                //                    msg = "主人，烟机设置了延时关机哦";
                //                } else {//处于开启状态
                //                    //关风机
                //                    DeviceControl.getInstance().closeDevice(true);
                //                    msg = "好的，油烟机已经关了";
                //                }
                break;
            case "OpenFan"://打开风机
                //此处逻辑暂为若烟机不处于风量控制模式则开启五档
                if (!DeviceReportManager.getInstance().isInWorkState(
                        DeviceReportManager.WORK_WIND_CONTROL)) {
                    //开风机(五档)
                    DeviceControl.getInstance().startWindControl(
                            WindControlActivity.DEFAULT_WIND_VALUE, true);
                    msg = "好的，已为您打开油烟机";
                } else {
                    msg = "主人，烟机已经处于开启状态了哦";
                }
                break;
            case "LockScreen"://打开锁屏
                boolean isLocked = (boolean) PreferenceUtil.getPreferenceValue(mContext,
                        PreferenceUtil.IS_SCREEN_LOCK, false);
                Log.e(TAG, "is screen lock when local command: " + isLocked);
                if (!isLocked) {
                    Log.e(TAG, "lock screen by app util when local command");
                    AppUtil.showLockScreen(AppUtil.getCurrentActivity(null), null);
                }
                msg = "好的，屏幕已锁上";
                break;
            case "OpenDelayShutdown"://打开延时关机
                int delayTime = (int) PreferenceUtil.getPreferenceValue(mContext,
                        PreferenceUtil.DELAY_SHUTDOWN_TIME, 0);
                Log.e(TAG, "delay time: " + delayTime);
                if (DeviceReportManager.getInstance().isInWorkState(
                        DeviceReportManager.WORK_INIT)) {//处于关闭状态
                    msg = "主人，烟机已经处于关闭状态了哦";
                    // TODO: 2019/9/30 若处于延时关机流程中
                } else if (delayTime > 0) {//设置了延时关机
                    //关风机
                    //                    DeviceControl.getInstance().closeDevice(true);
                    //启动延时关机流程
                    if (!mIsInDelayClosing) {
                        AppUtil.showDelayCloseDialog(mContext,
                                DelayCloseDialog.DELAY_TYPE.DELAY_TYPE_CLOSE);
                    }
                    msg = "好的，延时关机已打开";
                } else {//处于开启状态且未设置延时关机
                    //                    DeviceControl.getInstance().closeDevice(true);
                    //启动延时关机流程
                    if (!mIsInDelayClosing) {
                        AppUtil.showDelayCloseDialog(mContext,
                                DelayCloseDialog.DELAY_TYPE.DELAY_TYPE_CLOSE);
                    }
                    msg = "好的，油烟机已经关了";
                }
                break;
            case "OpenLamp"://打开照明灯
                if (PreferenceUtil.getLamp(mContext) != DeviceControl.LAMP_CLOSE) {//灯已开, 直接反馈, 不执行
                    msg = "主人，灯已经打开了哦";
                } else {//开灯
                    DeviceControl.getInstance().setLamp(DeviceControl.LAMP_CENTER, false);
                    msg = "好的，照明灯已打开";
                }
                break;
            case "CloseLamp"://关闭照明灯
                if (PreferenceUtil.getLamp(mContext) == DeviceControl.LAMP_CLOSE) {//灯未开, 直接反馈, 不执行
                    msg = "主人，灯已经关闭了哦";
                } else {//关灯
                    DeviceControl.getInstance().setLamp(DeviceControl.LAMP_CLOSE, false);
                    msg = "好的，照明灯已关闭";
                }
                break;
            case "OpenLink"://打开烟灶联动
                PreferenceUtil.setPreferenceValue(mContext, PreferenceUtil.DEVICE_LINK_SMOKE_STOVE,
                        true);
                //进入烟灶联动界面
                startActivityBySpeech(new Intent(mContext, DeviceLinkageActivity.class));
                msg = "好的，已为您打开烟灶联动";
                break;
            case "CloseLink"://关闭烟灶联动
                PreferenceUtil.setPreferenceValue(mContext, PreferenceUtil.DEVICE_LINK_SMOKE_STOVE,
                        false);
                startActivityBySpeech(new Intent(mContext, DeviceLinkageActivity.class));
                msg = "好的，已为您关闭烟灶联动";
                break;
            case "CloseStove"://关闭灶具
                //close all stoves
                //x1不支持
                msg = "暂不支持该功能哦";
                break;
            case "OpenBig"://打开强档
                msg = "暂不支持该功能哦";
                break;
            case "OpenLittle"://打开弱档
                msg = "暂不支持该功能哦";
                break;
            case "OpenAuto"://打开自动挡
                if (DeviceReportManager.getInstance().isInWorkState(
                        DeviceReportManager.WORK_AUTO)) {
                    //智能巡航运行中, 直接反馈, 不执行
                    msg = "自动档运行中";
                } else {
                    //打开智能巡航
                    DeviceControl.getInstance().startCruise();
                    msg = "好的，已为您开启自动档";
                }
                break;
            case "UpBrightness"://调高亮度
                mCurrentBrightness = 0;
                try {
                    mCurrentBrightness = Settings.System.getInt(mContext.getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS);
                    Log.e(TAG, "origin currentBrightness: " + mCurrentBrightness);
                } catch (Settings.SettingNotFoundException e) {
                    Log.e(TAG, "get brightness fail: " + e.toString());
                    e.printStackTrace();
                }

                if (mCurrentBrightness == 255) {
                    msg = "主人，屏幕已是最亮档，不能再调节了哦。";
                    break;
                }

                mShowBrightness = Math.round(((float) mCurrentBrightness * 100) / 255 + 10);
                mShowBrightness = mShowBrightness > 100 ? 100 : mShowBrightness;
                mCurrentBrightness = mShowBrightness * 255 / 100;
                Log.e(TAG, "showBrightness: " + mShowBrightness + ", currentBrightness: " +
                           mCurrentBrightness);

                AppUtil.setSysLight(mContext, mCurrentBrightness);
                //将亮度保存
                PreferenceUtil.setPreferenceValue(mContext, PreferenceUtil.BRIGHT_LAST,
                        mCurrentBrightness);
                EventBus.getDefault().post(new UpdateInfo(Constant.TYPE_BRIGHTNESS));
                try {
                    int changedBrightness = Settings.System.getInt(mContext.getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS);
                    Log.e(TAG, "changed Brightness: " + changedBrightness);
                } catch (Settings.SettingNotFoundException e) {
                    Log.e(TAG, "get brightness fail: " + e.toString());
                    e.printStackTrace();
                }
                msg = "好的，亮度已调到" + mShowBrightness;
                break;
            case "DownBrightness"://调低亮度
                mCurrentBrightness = 0;
                try {
                    mCurrentBrightness = Settings.System.getInt(mContext.getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS);
                    Log.e(TAG, "origin currentBrightness: " + mCurrentBrightness);
                } catch (Settings.SettingNotFoundException e) {
                    Log.e(TAG, "get brightness fail: " + e.toString());
                    e.printStackTrace();
                }

                if (mCurrentBrightness == 0) {
                    msg = "主人，屏幕已是最暗档，不能再调节了哦。";
                    break;
                }

                mShowBrightness = Math.round(((float) mCurrentBrightness * 100) / 255 - 10);
                mShowBrightness = mShowBrightness < 0 ? 0 : mShowBrightness;
                mCurrentBrightness = mShowBrightness * 255 / 100;
                Log.e(TAG, "showBrightness: " + mShowBrightness + ", currentBrightness: " +
                           mCurrentBrightness);

                AppUtil.setSysLight(mContext, mCurrentBrightness);
                //将亮度保存
                PreferenceUtil.setPreferenceValue(mContext, PreferenceUtil.BRIGHT_LAST,
                        mCurrentBrightness);
                EventBus.getDefault().post(new UpdateInfo(Constant.TYPE_BRIGHTNESS));
                try {
                    int changedBrightness = Settings.System.getInt(mContext.getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS);
                    Log.e(TAG, "changed Brightness: " + changedBrightness);
                } catch (Settings.SettingNotFoundException e) {
                    Log.e(TAG, "get brightness fail: " + e.toString());
                    e.printStackTrace();
                }
                msg = "好的，亮度已调到" + mShowBrightness;
                break;
            case "OpenAirSteward":
                msg = "暂不支持该功能哦";
                break;
            case "CloseAirSteward":
                msg = "暂不支持该功能哦";
                break;
            case "OpenCollectMenu"://打开收藏菜谱
                msg = "暂不支持该功能哦";
                break;
            case "OpenLocalMenu"://打开本地菜谱
                Intent localMenu = new Intent(mContext.getApplicationContext(),
                        RecipesCategoryActivity.class);
                localMenu.putExtra(CommonConst.SPECIFIED_TAB, "local");
                startActivityBySpeech(localMenu);
                msg = "好的";
                //                feedBackLocalMsg("好的", nluResult);
                break;
            case "CloseMenu":
                msg = "暂不支持该功能哦";
                break;
            default:
                break;
        }
        feedBackLocalMsg(msg, nluResult);
        ScreenTool.getInstance().addResetData("local command end");
    }

    private void handleRecipeDisplay(String api, NluResult nluResult) {
        if (!TextUtils.equals(AppUtil.getCurrentActivityName(mContext),
                RecipesCategoryActivity.class.getSimpleName())) {
            feedBackLocalMsg("当前不在智能菜谱页面哦", nluResult);
            return;
        }

        RecipesCategoryActivity currentActivity = (RecipesCategoryActivity) AppUtil
                .getCurrentActivity(mContext);
        Fragment currentShownFragment = currentActivity.getCurrentShownFragment();
        int index = 0;
        switch (api) {
            case "First":
                index = 0;
                break;
            case "Second":
                index = 1;
                break;
            case "Third":
                index = 2;
                break;
            case "Fourth":
                index = 3;
                break;
            case "Fifth":
                index = 4;
                break;
            default:
                break;
        }
        if (currentShownFragment instanceof RecipesCategoryFragment) {
            feedBackLocalMsg(((RecipesCategoryFragment) currentShownFragment).selectItem(index),
                    nluResult);
        }
    }

    private void processOnlineCommand(Command command, NluResult nluResult) {
        String api = command.getApi();
        if (api == null) {
            Log.e(TAG, "online command api is null!!!! nlu result: " + nluResult);
            return;
        }
        if (api.contains(CommonConst.MEDIA_CONTROL)) {
            processMedia(command, nluResult);
        } else {
            JsonObject msg = new JsonObject();
            setJsonElement(msg, "connection", "connected");
            //xi.i没有菜谱录制
            JSONObject jsonCommand = null;
            try {
                jsonCommand = new JSONObject(command.getParam());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonCommand == null) {
                return;
            }
            // x1.i无菜谱播放
            int operation = checkOperation(jsonCommand);
            switch (api) {
                // TODO: 2019/4/3 空气管家、智能巡航、灯的处理都只有开关，是否可以合并
                //空气管家与强弱档、智能巡航互斥
                //空气管家
                case CommonConst.AIR_STEWARD:
                    //x1无空气管家
                    setJsonElement(msg, "result", "disable");
                    break;
                //自动挡
                case CommonConst.AIR_CRUISE:
                    if (operation == OPERATION_OPEN) {
                        if (DeviceReportManager.getInstance().isInWorkState(
                                DeviceReportManager.WORK_AUTO)) {
                            //智能巡航运行中, 直接反馈, 不执行
                            setJsonElement(msg, "result", "is_working");
                        } else {
                            //打开智能巡航
                            DeviceControl.getInstance().startCruise();
                            setJsonElement(msg, "result", "normal");
                        }
                    } else if (operation == OPERATION_CLOSE) {
                        if (!DeviceReportManager.getInstance().isInWorkState(
                                DeviceReportManager.WORK_AUTO)) {
                            //智能巡航未运行, 直接反馈, 不执行
                            setJsonElement(msg, "result", "closed");
                        } else {
                            //关闭智能巡航
                            DeviceControl.getInstance().closeDevice(true);
                            setJsonElement(msg, "result", "normal");
                        }
                    }
                    break;
                case CommonConst.LAMP_OPERATION://灯
                    if (operation == OPERATION_OPEN) {
                        if (PreferenceUtil.getLamp(mContext) !=
                            DeviceControl.LAMP_CLOSE) {//灯已开, 直接反馈, 不执行
                            setJsonElement(msg, "result", "lighted");
                        } else {//开灯
                            DeviceControl.getInstance().setLamp(DeviceControl.LAMP_CENTER, false);
                            setJsonElement(msg, "result", "normal");
                        }
                    } else if (operation == OPERATION_CLOSE) {
                        if (PreferenceUtil.getLamp(mContext) ==
                            DeviceControl.LAMP_CLOSE) {//灯未开, 直接反馈, 不执行
                            setJsonElement(msg, "result", "closed");
                        } else {//关灯
                            DeviceControl.getInstance().setLamp(DeviceControl.LAMP_CLOSE, false);
                            setJsonElement(msg, "result", "normal");
                        }
                    }
                    break;
                case CommonConst.SCREEN_LOCK://锁屏
                    SpeechFloatingLayer.get(mContext).forceDismiss(true);
                    if (!(Boolean) PreferenceUtil.getPreferenceValue(mContext,
                            PreferenceUtil.IS_SCREEN_LOCK, false)) {
                        Log.e("lock_screen", "lock screen by app util when online command");
                        AppUtil.showLockScreen(AppUtil.getCurrentActivity(null), null);
                        setJsonElement(msg, "result", "normal");
                    } else {
                        setJsonElement(msg, "result", "locked");
                    }
                    break;
                case CommonConst.AIR_CONTROL://风量控制
                    /*if (DeviceReportManager.getInstance().isInWorkState(
                            DeviceReportManager.WORK_WIND_CONTROL)) {
                        handleAirControl(msg, jsonCommand);
                    } else {
                        setJsonElement(msg, "result", "cant_control");
                    }*/
                    handleAirControl(msg, jsonCommand);
                    break;
                case CommonConst.DEVICELINK_SPECIFIC://执行具体联动
                    //                                        handleDeviceLink(jsonCommand);
                    //                    break;
                    //                    String deviceName = jsonCommand.optString("deviceName");
                    String deviceLinkOperation = jsonCommand.optString("operation");
                    String deviceLinkTag = PreferenceUtil.DEVICE_LINK_SMOKE_STOVE;
                    boolean isNeedOpen = TextUtils.equals(deviceLinkOperation, "打开");
                    //                    if (TextUtils.equals(deviceName, "烟灶")) {
                    //                        deviceLinkTag = PreferenceUtil.DEVICE_LINK_SMOKE_STOVE;
                    //                    } else if (TextUtils.equals(deviceName, "烟蒸")) {
                    //                        deviceLinkTag = PreferenceUtil.DEVICE_LINK_SMOKE_STEAM;
                    //                    } else if (TextUtils.equals(deviceName, "烟烤")) {
                    //                        deviceLinkTag = PreferenceUtil.DEVICE_LINK_SMOKE_ROAST;
                    //                    }
                    boolean isOpened = (Boolean) PreferenceUtil.getPreferenceValue(mContext,
                            deviceLinkTag, false);
                    if (isOpened == isNeedOpen) {
                        setJsonElement(msg, "result", isOpened ? "opened" : "closed");
                        break;
                    } else {
                        PreferenceUtil.setPreferenceValue(mContext, deviceLinkTag,
                                !jsonCommand.has("operation") || isNeedOpen);
                    }
                case CommonConst.DEVICELINK://跳转到设备控制界面
                    SpeechFloatingLayer.get(mContext).forceDismiss(false);
                    Intent intent = new Intent(mContext.getApplicationContext(),
                            DeviceLinkageActivity.class);
                    startActivityBySpeech(intent);
                    setJsonElement(msg, "result", "normal");
                    break;
                case CommonConst.DEVICELINK_HELP://跳转灶具联动开启方式页面
                    SpeechFloatingLayer.get(mContext).forceDismiss(false);
                    Intent intent2 = new Intent(mContext.getApplicationContext(),
                            DeviceLinkageActivity.class);
                    intent2.putExtra("intent_fragment", "link_tips_fragment");
                    startActivityBySpeech(intent2);
                    setJsonElement(msg, "result", "normal");
                    break;
                case CommonConst.BRIGHTNESS_CONTROL:
                    if (jsonCommand.has("brightness")) {
                        String brightnessControl = jsonCommand.optString("brightness");
                        String content = SpeechFloatingLayer.get(mContext).showBrightnessControl(
                                brightnessControl);
                        if (TextUtils.equals(content, "min") || TextUtils.equals(content, "max")) {
                            setJsonElement(msg, "result", content);
                        } else {
                            setJsonElement(msg, "result", "normal");
                            //                                setJsonElement(msg, "brightness", brightness);
                            setJsonElement(msg, "content", content);
                        }
                    } else {
                        Log.e(TAG, "illegal json command in brightness control!!!");
                    }
                    break;
                case CommonConst.BRIGHTNESS_ADJUST:
                    if (jsonCommand.has("adjustCmd")) {
                        String adjustCmd = jsonCommand.optString("adjustCmd");
                        if (TextUtils.equals(adjustCmd, "最亮")) {

                        } else if (TextUtils.equals(adjustCmd, "最暗")) {

                        } else if (TextUtils.equals(adjustCmd, "调亮")) {

                        } else if (TextUtils.equals(adjustCmd, "调暗")) {

                        }
                    }
                    break;
                case CommonConst.OPERATION://油烟机开关
                    if (jsonCommand.has("operation")) {
                        String hoodSwitch = jsonCommand.optString("operation");
                        if (TextUtils.equals(hoodSwitch, "打开")) {
                            if (!DeviceReportManager.getInstance().isInWorkState(
                                    DeviceReportManager.WORK_WIND_CONTROL)) {//若不处于弱档状态
                                //开风机(五档)
                                DeviceControl.getInstance().startWindControl(DEFAULT_WIND_VALUE,
                                        true);
                                setJsonElement(msg, "result", "normal");
                            } else {
                                setJsonElement(msg, "result", "on");
                            }
                        } else if (TextUtils.equals(hoodSwitch, "关闭")) {
                            if (DeviceReportManager.getInstance().isInWorkState(
                                    DeviceReportManager.WORK_INIT)) {//处于关闭状态
                                setJsonElement(msg, "result", "off");
                            } else if (PreferenceUtil.getDelayShutSwitch(mContext) &&
                                       ((int) PreferenceUtil.getPreferenceValue(mContext,
                                               PreferenceUtil.DELAY_SHUTDOWN_TIME, 0) >
                                        0)) {//设置了延时关机
                                setJsonElement(msg, "result", "delay");
                                setJsonElement(msg, "delayTime", ((int) PreferenceUtil
                                        .getPreferenceValue(mContext,
                                                PreferenceUtil.DELAY_SHUTDOWN_TIME, 0)) + "分钟");
                            } else {//处于开启状态
                                //处于延时关机中
                                if (mIsInDelayClosing) {
                                    AppUtil.cancelDelayClose();
                                }
                                //关风机
                                DeviceControl.getInstance().closeDevice(true);
                                //                                AppUtil.showDelayCloseDialog(mContext,
                                //                                        DelayCloseDialog.DELAY_TYPE.DELAY_TYPE_CLOSE);
                                setJsonElement(msg, "result", "normal");
                            }
                        } else {
                            Log.e(TAG, "illegal json command in hood switch!!!");
                            return;
                        }
                    }
                    break;
                case CommonConst.CLOSE_CONFIRM:
                    SpeechFloatingLayer.get(mContext).dismiss(false);
                    Log.e("coiliaspp", "mIsInDelayClosing: " + mIsInDelayClosing);
                    if (mIsInDelayClosing) {
                        AppUtil.cancelDelayClose();
                    }
                    DeviceControl.getInstance().closeDevice(true);
                    break;
                case CommonConst.CLOSE_CANCEL:
                    setJsonElement(msg, "delayTime",
                            String.valueOf(PreferenceUtil.getDelayTime(mContext)));
                    SpeechFloatingLayer.get(mContext).dismiss(false);
                    if (!mIsInDelayClosing) {
                        AppUtil.showDelayCloseDialog(mContext,
                                DelayCloseDialog.DELAY_TYPE.DELAY_TYPE_CLOSE);
                    }
                    break;
                case CommonConst.CLOSE_DELAY_OPEN:
                    // TODO: 2019/4/9 这里可以省略？
                    setJsonElement(msg, "result", "normal");
                    break;
                case CommonConst.CLOSE_DELAY_CANCEL:
                    boolean delayShutSwitch = PreferenceUtil.getDelayShutSwitch(mContext);
                    if (delayShutSwitch) {//若延时关机开关打开
                        //将延时关机开关关闭
                        PreferenceUtil.setDelayShutSwitch(mContext, false);
                        //界面变化
                        EventBus.getDefault().post(new DelayOffParam(false));
                        //返回normal给dui
                        setJsonElement(msg, "result", "normal");
                    } else {
                        //否则不执行任何操作，返回no_delay给dui
                        setJsonElement(msg, "result", "no_delay");
                    }
                    break;
                case CommonConst.CLOSE_DELAY:
                    if (jsonCommand.has("delayTime")) {
                        String time = jsonCommand.optString("delayTime");
                        Log.e(TAG, "delay time: " + time);
                        if (TextUtils.isEmpty(time)) {
                            Log.e(TAG, "delay time is null!!!");
                            return;
                        }
                        DelayTime delayMinutes = convertTime(time, 99, 1);
                        Log.e(TAG, "close_delay: " + delayMinutes.toString());
                        if (!delayMinutes.isLegal()) {
                            setJsonElement(msg, "result", "illegal");
                            setJsonElement(msg, "time", delayMinutes.getValueFotTTS());
                        } else if (DeviceReportManager.getInstance().isInWorkState(
                                DeviceReportManager.WORK_INIT)) {//处于关闭状态
                            setJsonElement(msg, "result", "off");
                        } else {
                            PreferenceUtil.setDelayShutSwitch(mContext, true);
                            PreferenceUtil.setPreferenceValue(mContext,
                                    PreferenceUtil.DELAY_SHUTDOWN_TIME,
                                    delayMinutes.getValueForDelay());
                            //                            Intent delayOffIntent = new Intent(mContext, DelayOffActivity.class);
                            //                            startActivityBySpeech(delayOffIntent);
                            AppUtil.showDelayCloseDialog(mContext,
                                    DelayCloseDialog.DELAY_TYPE.DELAY_TYPE_CLOSE);
                            SpeechFloatingLayer.get(mContext).forceDismiss(false);
                            setJsonElement(msg, "result", "normal");
                            setJsonElement(msg, "time", delayMinutes.getValueFotTTS());
                        }
                    } else {
                        Log.e(TAG, "illegal json command in hood close delay!!!");
                        return;
                    }
                    break;
                case CommonConst.STOVE_CONTROL_CLOSE:
                    //检测是否连接灶具
                    if (SmokeStoveLinkAction.getInstance().getLinkState() ==
                        SmokeStoveLinkAction.LINK_STATE_RUNNING) {
                        setJsonElement(msg, "stove_connection", "connected");
                    } else {
                        setJsonElement(msg, "stove_connection", "disconnected");
                        break;
                    }
                    StoveControl.closeFire(true, true, mContext);
                    setJsonElement(msg, "result", "success");
                    break;
                case CommonConst.STOVE_SIDE_CONFIRM:
                    /*if (jsonCommand != null) {
                        //检测是否连接灶具
                        if (!LinkFactory.isJaz1Linked()) {
                            setJsonElement(msg, "stove_connection", "disconnected");
                            break;
                        } else {
                            setJsonElement(msg, "stove_connection", "connected");
                        }

                        //查看是否延迟关闭
                        String stoveDelayTime = jsonCommand.optString("delayTime");
                        if (!TextUtils.isEmpty(stoveDelayTime)) {
                            mStoveCloseDelayTime = convertTime(stoveDelayTime, 99, 1);
                            Log.e(TAG, "stove_delay1: " + mStoveCloseDelayTime.toString());
                            if (!mStoveCloseDelayTime.isLegal()) {//若延迟时间非法
                                setJsonElement(msg, "result", "time_illegal");
                                setJsonElement(msg, "time", mStoveCloseDelayTime.getValueFotTTS());
                                break;
                            }
                        } else {
                            mStoveCloseDelayTime = null;
                        }
                        //获取当前灶具具体开关状态
                        List<String> canBeClosedStoves = getStoveInfo();
                        if (canBeClosedStoves.isEmpty()) {//若无可关闭灶眼
                            setJsonElement(msg, "result", "closed");
                            break;
                        }
                        String side = jsonCommand.optString("side");
                        if (!TextUtils.isEmpty(side)) {
                            if (canBeClosedStoves.contains(side)) {//若用户选择的灶眼可以被关闭
                                closeStove(mStoveCloseDelayTime == null
                                           ? 0
                                           : mStoveCloseDelayTime.getValueForDelay(), side);
                                setJsonElement(msg, "result", "success");
                                if (mStoveCloseDelayTime != null &&
                                    mStoveCloseDelayTime.isLegal()) {
                                    setJsonElement(msg, "time",
                                            mStoveCloseDelayTime.getValueFotTTS());
                                }
                            } else {
                                setJsonElement(msg, "result", "closed");
                                setJsonElement(msg, "other_side", canBeClosedStoves.get(0) + "灶");
                            }
                        } else {
                            Log.e(TAG, "illegal json command in stove side confirm!!!");
                            return;
                        }
                    }*/
                    setJsonElement(msg, "result", "disable");
                    break;
                case CommonConst.STOVE_SIDE_CANCEL_COUNTDOWN:
                    /*if (jsonCommand != null && jsonCommand.has("side")) {
                        String side = jsonCommand.optString("side");
                        if (TextUtils.isEmpty(side)) {
                            Log.e(TAG, "side is null!!!");
                            return;
                        }
                        //检测是否连接灶具
                        if (!LinkFactory.isJaz1Linked()) {
                            setJsonElement(msg, "stove_connection", "disconnected");
                            break;
                        } else {
                            setJsonElement(msg, "stove_connection", "connected");
                        }
                        //获取当前灶具具体开关状态
                        List<String> canBeClosedStoves = getStoveInfo();
                        if (canBeClosedStoves.isEmpty()) {//若无可关闭灶眼
                            setJsonElement(msg, "result", "closed");
                            break;
                        }
                        if (!canBeClosedStoves.contains(side)) {
                            setJsonElement(msg, "result", "select_closed");
                            break;
                        }
                        //取消定时
                        if (TextUtils.equals("左", side)) {
                            StoveControl.setJAZ1Timer(StoveControl.JAZ1_STOVE.STOVE_LEFT, 0);
                        } else if (TextUtils.equals("中", side)) {
                            StoveControl.setJAZ1Timer(StoveControl.JAZ1_STOVE.STOVE_CENTER, 0);
                        } else {
                            StoveControl.setJAZ1Timer(StoveControl.JAZ1_STOVE.STOVE_RIGHT, 0);
                        }
                        setJsonElement(msg, "result", "normal");
                    }*/
                    setJsonElement(msg, "result", "disable");
                    break;
                case CommonConst.COUNTDOWN_START:
                    if (jsonCommand.has("countdown_time")) {
                        String countdown_time = jsonCommand.optString("countdown_time");
                        if (TextUtils.isEmpty(countdown_time)) {
                            Log.e(TAG, "delay time is null!!!");
                            return;
                        }
                        DelayTime countdownTime = convertTime(countdown_time, 99, 1);
                        Log.e(TAG, "count_down_delay: " + countdownTime.toString());
                        if (!countdownTime.isLegal()) {
                            setJsonElement(msg, "result", "illegal");
                            setJsonElement(msg, "time", countdownTime.getValueFotTTS());
                        } else {
                            if ((Boolean) PreferenceUtil.getPreferenceValue(mContext,
                                    PreferenceUtil.IS_COUNTING_DOWN, false)) {
                                setJsonElement(msg, "result", "isCountingDown");
                            } else {
                                //跳转定时器界面
                                setJsonElement(msg, "result", "normal");
                                setJsonElement(msg, "time", countdownTime.getValueFotTTS());
                                Intent intent3 = new Intent(mContext.getApplicationContext(),
                                        QuickToolActivity.class);
                                intent3.putExtra("countdown_time",
                                        countdownTime.getValueForDelay());
                                startActivityBySpeech(intent3);
                                SpeechFloatingLayer.get(mContext).forceDismiss(false);
                            }
                        }
                    }
                    break;
                case CommonConst.COUNTDOWN_CANCEL:
                    if ((Boolean) PreferenceUtil.getPreferenceValue(mContext,
                            PreferenceUtil.IS_COUNTING_DOWN, false)) {
                        setJsonElement(msg, "result", "normal");
                        EventBus.getDefault().post(new TimerServerMessage(TimerServer.class));
                    } else {
                        setJsonElement(msg, "result", "canceled");
                    }
                    break;
                case CommonConst.SMART_RECIPE:
                    if (jsonCommand.has("operation")) {
                        String recipeOperation = jsonCommand.optString("operation");
                        String currentActivityName = AppUtil.getCurrentActivityName(mContext);
                        if (currentActivityName.contains(
                                RecipesCategoryActivity.class.getSimpleName())) {
                            if (TextUtils.equals(recipeOperation, "打开")) {
                                setJsonElement(msg, "result", "opened");
                            } else if (TextUtils.equals(recipeOperation, "关闭")) {
                                setJsonElement(msg, "result", "normal");
                                startActivityBySpeech(new Intent(mContext.getApplicationContext(),
                                        MainActivity.class));
                                SpeechFloatingLayer.get(mContext).forceDismiss(false);
                            }
                        } else {
                            if (TextUtils.equals(recipeOperation, "打开")) {
                                setJsonElement(msg, "result", "normal");
                                startActivityBySpeech(new Intent(mContext.getApplicationContext(),
                                        RecipesCategoryActivity.class));
                                SpeechFloatingLayer.get(mContext).forceDismiss(false);
                            } else if (TextUtils.equals(recipeOperation, "关闭")) {
                                setJsonElement(msg, "result", "closed");
                            }
                        }
                    }
                    break;
                case CommonConst.IQIYI_OPERATION:
                    boolean videoOperation = TextUtils.equals(
                            jsonCommand.optString("videoOperation"), "打开");
                    if (DeviceReportManager.getInstance().is_video_show == videoOperation) {
                        setJsonElement(msg, "result", videoOperation ? "opened" : "closed");
                    } else {
                        SpeechFloatingLayer.get(mContext).forceDismiss(false);
                        AppUtil.setVideoState(videoOperation ? 1 : -1, mContext);
                        setJsonElement(msg, "result", "normal");
                        Log.e(TAG, "operation iqy");
                    }
                    break;
                case CommonConst.VOLUME_ADJUST:
                    if (jsonCommand.has("cmd")) {
                        Log.e(TAG, "enter adjust");
                        String cmd = jsonCommand.optString("cmd");
                        if (TextUtils.equals(cmd, "调低")) {
                            String result = SpeechFloatingLayer.get(mContext).showVolumeControl(
                                    CommonConst.CMD_REDUCE);
                            if (TextUtils.equals(result, "maximum") || TextUtils.equals(result,
                                    "minimum")) {
                                setJsonElement(msg, "result", result);
                            } else {
                                setJsonElement(msg, "result", "normal");
                                setJsonElement(msg, "value", result);
                            }
                        } else if (TextUtils.equals(cmd, "调高")) {
                            String result1 = SpeechFloatingLayer.get(mContext).showVolumeControl(
                                    CommonConst.CMD_ADD);

                            if (TextUtils.equals(result1, "maximum") || TextUtils.equals(result1,
                                    "minimum")) {
                                setJsonElement(msg, "result", result1);
                            } else {
                                setJsonElement(msg, "result", "normal");
                                setJsonElement(msg, "value", result1);
                            }
                        }
                    }

                    break;
                case CommonConst.VOLUME_SET:
                    if (jsonCommand.has("volume")) {
                        String volume = jsonCommand.optString("volume");
                        Log.e(TAG, "volume string: " + volume);
                        if (!TextUtils.isEmpty(volume)) {
                            int volumeValue = Integer.valueOf(volume);
                            Log.e(TAG, "volume value: " + volumeValue);
                            if (volumeValue > 100 || volumeValue < 0) {
                                setJsonElement(msg, "result", "outOfRange");
                            } else {
                                setJsonElement(msg, "result", SpeechFloatingLayer.get(mContext)
                                        .showVolumeControl(volumeValue));
                            }
                        }
                    }
                    break;
                case CommonConst.SELECTION:
                    if (!TextUtils.equals(AppUtil.getCurrentActivityName(mContext),
                            RecipesCategoryActivity.class.getSimpleName())) {
                        setJsonElement(msg, "result", "not_smart");
                        break;
                    }
                    if (!jsonCommand.has("num")) {
                        break;
                    }
                    String num = jsonCommand.optString("num");
                    if (TextUtils.isEmpty(num)) {
                        setJsonElement(msg, "result", "illegal");
                        break;
                    }
                    int index = Integer.parseInt(num);
                    if (index <= 0) {
                        setJsonElement(msg, "result", "illegal");
                        break;
                    }
                    RecipesCategoryActivity currentActivity = (RecipesCategoryActivity) AppUtil
                            .getCurrentActivity(mContext);
                    Fragment currentShownFragment = currentActivity.getCurrentShownFragment();
                    //                    if (currentShownFragment instanceof RecipesAdultsFragment) {
                    //                        setJsonElement(msg, "result", "normal");
                    //                        setJsonElement(msg, "content",
                    //                                ((RecipesAdultsFragment) currentShownFragment)
                    //                                        .selectItem(index - 1));
                    //                    } else if (currentShownFragment instanceof RecipesCategoryFragment) {
                    if (currentShownFragment instanceof RecipesCategoryFragment) {
                        setJsonElement(msg, "result", "normal");
                        setJsonElement(msg, "content",
                                ((RecipesCategoryFragment) currentShownFragment)
                                        .selectItem(index - 1));
                    }
                    break;
                case CommonConst.COOKING_OPERATION:
                    setJsonElement(msg, "result", "disable");
                    /*int cookingOperation = checkOperation(jsonCommand, "开始", "结束");
                    if (cookingOperation == OPERATION_OPEN) {
                        Activity currentActivity1 = AppUtil.getCurrentActivity(mContext);
                        if (currentActivity1 instanceof RecipesDetailStepActivity ||
                            currentActivity1 instanceof RecipesDetailHomePageActivity) {
                            //                            fdfaf
                        }
                        setJsonElement(msg, "result", "in_cooking");
                    } else if (cookingOperation == OPERATION_CLOSE) {
                        setJsonElement(msg, "result", "normal");
                    }*/
                    break;
                default:
                    break;
            }
            feedBackMsg(msg, nluResult);
        }
    }

    /*private List<String> getStoveInfo() {
        ArrayList<String> stoveFireInfo = new ArrayList<>();
        boolean isLeftFire = (boolean) PreferenceUtil.getPreferenceValue(mContext,
                PreferenceUtil.JAZ1_STOVE_FIRE_LEFT, false);
        boolean isMiddleFire = (boolean) PreferenceUtil.getPreferenceValue(mContext,
                PreferenceUtil.JAZ1_STOVE_FIRE_MIDDLE, false);
        boolean isRightFire = (boolean) PreferenceUtil.getPreferenceValue(mContext,
                PreferenceUtil.JAZ1_STOVE_FIRE_RIGHT, false);
        if (isLeftFire) {
            stoveFireInfo.add("左");
        }
        if (isMiddleFire) {
            stoveFireInfo.add("中");
        }
        if (isRightFire) {
            stoveFireInfo.add("右");
        }
        return stoveFireInfo;
    }*/

    /*private void closeStove(int stoveDelayTime, String side) {
        Log.e(TAG, "stoveDelayTime: " + stoveDelayTime + ", side: " + side);
        if (TextUtils.equals("左", side)) {
            if (stoveDelayTime != 0) {
                StoveControl.setJAZ1Timer(StoveControl.JAZ1_STOVE.STOVE_LEFT, stoveDelayTime);
                return;
            }
            StoveControl.closeJAZ1Fire(true, false, false);
        } else if (TextUtils.equals("中", side)) {
            if (stoveDelayTime != 0) {
                StoveControl.setJAZ1Timer(StoveControl.JAZ1_STOVE.STOVE_CENTER, stoveDelayTime);
                return;
            }
            StoveControl.closeJAZ1Fire(false, true, false);
        } else {
            if (stoveDelayTime != 0) {
                StoveControl.setJAZ1Timer(StoveControl.JAZ1_STOVE.STOVE_RIGHT, stoveDelayTime);
                return;
            }
            StoveControl.closeJAZ1Fire(false, false, true);
        }
    }*/

    private DelayTime convertTime(String time, int max, int min) {
        String[] split = time.split(":");
        int hours = Integer.parseInt(split[0]);
        int minutes = Integer.parseInt(split[1]);
        int seconds = Integer.parseInt(split[2]);
        Log.e(TAG, "convert time result, hours = " + hours + ", minutes = " + minutes +
                   ", seconds = " + seconds);
        String speakTime = (hours == 0 ? "" : (hours + "小时")) +
                           ((minutes = minutes + (seconds > 30 ? 1 : 0)) == 0
                            ? ""
                            : minutes + "分钟");
        int convertTime = hours * 60 + minutes + (seconds > 30 ? 1 : 0);
        return new DelayTime((convertTime <= max && convertTime >= min), convertTime, speakTime);
    }

    private void handleDeviceLink(JSONObject jsonCommand) {
        if (jsonCommand != null) {
            String deviceName = jsonCommand.optString("deviceName");
            String operation = jsonCommand.optString("operation");
            SpeechFloatingLayer.get(mContext).showDeviceLinkFunction(deviceName,
                    TextUtils.equals(operation, "打开"));
        }
    }

    private void handleAirControl(JsonObject msg, JSONObject command) {
        // TODO: 2019/4/3 待整合
        if (command == null) {
            return;
        }
        String relativeVelocity = command.optString("relative_velocity");
        if (!TextUtils.isEmpty(relativeVelocity) && DeviceReportManager.getInstance().isInWorkState(
                DeviceReportManager.WORK_INIT)) {
            setJsonElement(msg, "result", "not_work");
            return;
        }
        String limitVelocity = command.optString("limit_velocity");
        String absoluteVelocity = command.optString("absolute_velocity");
        int windSpeed = DeviceReportManager.getInstance().getWindSpeed();
        Log.e(TAG, "wind speed: " + windSpeed);
        if (TextUtils.equals(relativeVelocity, "调小")) {
            if (windSpeed == 0xf0) {
                setJsonElement(msg, "result", "minimum");
            } else {
                DeviceControl.getInstance().startWindControl(windSpeed - 1, true);
                setJsonElement(msg, "result", "normal");
                setJsonElement(msg, "velocity", transform2TTS(
                        Integer.valueOf(Integer.toHexString(windSpeed - 1).substring(1))));
            }
        } else if (TextUtils.equals(relativeVelocity, "调大")) {
            if (windSpeed == 0xF9) {
                setJsonElement(msg, "result", "maximum");
            } else {
                DeviceControl.getInstance().startWindControl(windSpeed + 1, true);
                setJsonElement(msg, "result", "normal");
                setJsonElement(msg, "velocity", transform2TTS(
                        Integer.valueOf(Integer.toHexString(windSpeed + 1).substring(1))));
            }
        } else if (TextUtils.equals(limitVelocity, "强档")) {
            if (windSpeed == 0xf9) {
                setJsonElement(msg, "result", "maximum");
            } else {
                DeviceControl.getInstance().startWindControl(0xf9, true);
                setJsonElement(msg, "result", "normal");
                setJsonElement(msg, "velocity", transform2TTS(10));
            }
        } else if (TextUtils.equals(limitVelocity, "弱档")) {
            if (windSpeed == 0xf1) {
                setJsonElement(msg, "result", "minimum");
            } else {
                DeviceControl.getInstance().startWindControl(0xf0, true);
                setJsonElement(msg, "result", "normal");
                setJsonElement(msg, "velocity", transform2TTS(1));
            }
        } else if (!TextUtils.isEmpty(absoluteVelocity)) {
            int speed = Integer.parseInt(absoluteVelocity);
            DeviceControl.getInstance().startWindControl(speed, true);
            setJsonElement(msg, "result", "normal");
            setJsonElement(msg, "velocity", transform2TTS(speed));
        }
    }

    // TODO: 2019/9/20 增加一个类型入参，可根据该入参输出不同类型可供播报的内容
    private String transform2TTS(int windSpeed) {
        return "第" + windSpeed + "档";
    }

    private int checkOperation(JSONObject jsonCommand) {
        if (jsonCommand == null) {
            return OPERATION_ILLEGAL;
        }
        //        String operation = jsonCommand.optString("operation");
        //        if (TextUtils.isEmpty(operation)) {
        //            return OPERATION_ILLEGAL;
        //        }
        //        if (TextUtils.equals(operation, "打开")) {
        //            return OPERATION_OPEN;
        //        } else if (TextUtils.equals(operation, "关闭")) {
        //            return OPERATION_CLOSE;
        //        } else {
        //            return OPERATION_ILLEGAL;
        //        }
        return checkOperation(jsonCommand, "打开", "关闭");
    }

    private int checkOperation(@NonNull JSONObject jsonCommand, @NonNull String operation1,
            @NonNull String operation2) {
        String operation = jsonCommand.optString("operation");
        if (TextUtils.isEmpty(operation)) {
            return OPERATION_ILLEGAL;
        }
        if (TextUtils.equals(operation, operation1)) {
            return OPERATION_OPEN;
        } else if (TextUtils.equals(operation, operation2)) {
            return OPERATION_CLOSE;
        } else {
            return OPERATION_ILLEGAL;
        }
    }

    private void processMedia(Command command, NluResult nluResult) {
        Log.e(TAG, "process media");
        JsonObject msg = new JsonObject();
        /*if (!MusicManager.getInstance().isDuiMode()) {
            msg.addProperty("error_code", "no_media");
            feedBackMsg(msg, nluResult);
            return;
        }*/
        int state = 0;
        PlaybackStateCompat playbackState = MusicManager.getInstance().getPlaybackState();
        if (playbackState != null) {
            state = MusicManager.getInstance().getPlaybackState().getState();
        }
        int xmState = XmPlayerManager.getInstance(mContext).getPlayerStatus();
        switch (command.getApi()) {
            case CommonConst.REPLAY:
                if (MusicManager.getInstance().isDuiMode()) {
                    if (state == PlaybackStateCompat.STATE_STOPPED ||
                        state == PlaybackStateCompat.STATE_NONE) {
                        msg.addProperty("error_code", "no_media");
                        break;
                    }
                    MusicManager.getInstance().seekTo(0);
                    if (state == PlaybackStateCompat.STATE_PAUSED) {
                        MusicManager.getInstance().play();
                        msg.addProperty("error_code", "success");
                    }
                } else {
                    if (xmState != XMediaPlayer.PAUSED && xmState != XMediaPlayer.STARTED) {
                        msg.addProperty("error_code", "no_media");
                        break;
                    }
                    XmPlayerManager.getInstance(mContext).seekTo(0);
                    if (xmState == XMediaPlayer.PAUSED) {
                        XmPlayerManager.getInstance(mContext).play();
                        msg.addProperty("error_code", "success");
                    }
                }
                mIsMusicStateChangedAfterWakeUp = true;
                break;
            case CommonConst.CONTINUE:
                if (MusicManager.getInstance().isDuiMode()) {
                    if (state == PlaybackStateCompat.STATE_STOPPED ||
                        state == PlaybackStateCompat.STATE_NONE) {
                        msg.addProperty("error_code", "no_media");
                    } else if (state == PlaybackStateCompat.STATE_PAUSED) {
                        MusicManager.getInstance().play();
                        MediaMetadataCompat mediaMetadata = MusicManager.getInstance()
                                .getMediaMetadata();
                        if (mediaMetadata != null) {
                            msg.addProperty("error_code", "success");
                            msg.addProperty("title",
                                    mediaMetadata.getDescription().getSubtitle() + "的" +
                                    mediaMetadata.getDescription().getTitle());
                        }
                        Log.e("continue_play", "continue");
                    } else if (state == PlaybackStateCompat.STATE_PLAYING) {
                        msg.addProperty("error_code", "already");
                    }
                } else {
                    String title = null;
                    String name = null;
                    if (xmState != XMediaPlayer.PAUSED && xmState != XMediaPlayer.STARTED) {
                        msg.addProperty("error_code", "no_media");
                        break;
                    } else if (xmState == XMediaPlayer.PAUSED) {
                        XmPlayerManager.getInstance(mContext).play();
                        PlayableModel currSound = XmPlayerManager.getInstance(mContext)
                                .getCurrSound();
                        if (currSound instanceof Track) {
                            Track info = (Track) currSound;
                            title = info.getTrackTitle();
                            name = info.getAnnouncer().getNickname();
                        }
                        msg.addProperty("error_code", "success");
                        msg.addProperty("title", (TextUtils.isEmpty(name) ? mContext.getResources()
                                .getString(R.string.unknown_author) : name) + "的" + title);
                    } else {
                        msg.addProperty("error_code", "already");
                    }
                }
                mIsMusicStateChangedAfterWakeUp = true;
                break;
            case CommonConst.PAUSE:
                if (MusicManager.getInstance().isDuiMode()) {
                    if (state == PlaybackStateCompat.STATE_STOPPED ||
                        state == PlaybackStateCompat.STATE_NONE) {
                        msg.addProperty("error_code", "no_media");
                    } else if (state == PlaybackStateCompat.STATE_PLAYING) {
                        SpeechFloatingLayer.get(mContext).showMusicFunction();
                        MusicManager.getInstance().pause();
                        msg.addProperty("error_code", "success");
                    } else if (state == PlaybackStateCompat.STATE_PAUSED) {
                        msg.addProperty("error_code", "already");
                    }
                } else {
                    if (xmState != XMediaPlayer.PAUSED && xmState != XMediaPlayer.STARTED) {
                        msg.addProperty("error_code", "no_media");
                        break;
                    } else if (xmState == XMediaPlayer.PAUSED) {
                        msg.addProperty("error_code", "already");
                    } else {
                        XmPlayerManager.getInstance(mContext).play();
                        msg.addProperty("error_code", "success");
                    }
                }
                mIsMusicStateChangedAfterWakeUp = true;
                break;
            case CommonConst.STOP:
                if (MusicManager.getInstance().isDuiMode()) {
                    if (state != PlaybackStateCompat.STATE_STOPPED &&
                        state != PlaybackStateCompat.STATE_NONE) {
                        MusicManager.getInstance().stop();
                        msg.addProperty("error_code", "success");
                    } else {
                        msg.addProperty("error_code", "no_media");
                    }
                } else {
                    if (xmState != XMediaPlayer.PAUSED && xmState != XMediaPlayer.STARTED) {
                        msg.addProperty("error_code", "no_media");
                        break;
                    } else {
                        //                        XmPlayerManager.getInstance(mContext).stop();
                        AppUtil.stopPlayMusic(mContext);
                        msg.addProperty("error_code", "success");
                    }
                }
                mIsMusicStateChangedAfterWakeUp = true;
                break;
            case CommonConst.PREVIOUS:
                if (MusicManager.getInstance().isDuiMode()) {
                    if (state != PlaybackStateCompat.STATE_STOPPED &&
                        state != PlaybackStateCompat.STATE_NONE) {
                        MusicManager.getInstance().skipToPrevious();
                        msg.addProperty("error_code", "success");
                        mIsNeedFeedbackOnMetaDataChanged = true;
                        mFeedbackMsg = msg;
                        mNluResult = nluResult;
                        mIsMusicStateChangedAfterWakeUp = true;
                        return;
                    } else {
                        msg.addProperty("error_code", "no_media");
                    }
                } else {
                    if (xmState != XMediaPlayer.PAUSED && xmState != XMediaPlayer.STARTED) {
                        msg.addProperty("error_code", "no_media");
                        break;
                    } else {
                        if (XmPlayerManager.getInstance(mContext).hasPreSound()) {
                            XmPlayerManager.getInstance(mContext).playPre();
                            msg.addProperty("error_code", "success");
                            mIsNeedFeedbackOnMetaDataChanged = true;
                            mFeedbackMsg = msg;
                            mNluResult = nluResult;
                            mIsMusicStateChangedAfterWakeUp = true;
                            return;
                        } else {
                            msg.addProperty("error_code", "already_first");
                        }
                    }
                }
                break;
            case CommonConst.NEXT:
            case CommonConst.ANOTHER:
                if (MusicManager.getInstance().isDuiMode()) {
                    Log.e(TAG, "state when next: " + state);
                    if (state != PlaybackStateCompat.STATE_STOPPED &&
                        state != PlaybackStateCompat.STATE_NONE) {
                        Log.e(TAG, "next song!!!!");
                        MusicManager.getInstance().skipToNext();
                        msg.addProperty("error_code", "success");
                        mIsNeedFeedbackOnMetaDataChanged = true;
                        mFeedbackMsg = msg;
                        mNluResult = nluResult;
                        mIsMusicStateChangedAfterWakeUp = true;
                        return;
                    } else {
                        msg.addProperty("error_code", "no_media");
                    }
                } else {
                    if (xmState != XMediaPlayer.PAUSED && xmState != XMediaPlayer.STARTED) {
                        msg.addProperty("error_code", "no_media");
                        break;
                    } else {
                        if (XmPlayerManager.getInstance(mContext).hasNextSound()) {
                            XmPlayerManager.getInstance(mContext).playNext();
                            msg.addProperty("error_code", "success");
                            mIsNeedFeedbackOnMetaDataChanged = true;
                            mFeedbackMsg = msg;
                            mNluResult = nluResult;
                            mIsMusicStateChangedAfterWakeUp = true;
                            return;
                        } else {
                            msg.addProperty("error_code", "already_last");
                        }
                    }
                }
                break;
            case CommonConst.SINGLE_CIRCULATION:
                if (MusicManager.getInstance().isDuiMode()) {
                    if (state != PlaybackStateCompat.STATE_STOPPED &&
                        state != PlaybackStateCompat.STATE_NONE) {
                        MusicManager.getInstance().setRepeatMode(true);
                        msg.addProperty("error_code", "success");
                    } else {
                        msg.addProperty("error_code", "no_media");
                    }
                } else {
                    if (xmState != XMediaPlayer.PAUSED && xmState != XMediaPlayer.STARTED) {
                        msg.addProperty("error_code", "no_media");
                        break;
                    } else {
                        XmPlayerManager.getInstance(mContext).setPlayMode(
                                XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP);
                        msg.addProperty("error_code", "success");
                    }
                }
                break;
            case CommonConst.CLOSE_SINGLE_CIRCULATION:
                if (MusicManager.getInstance().isDuiMode()) {
                    if (state != PlaybackStateCompat.STATE_STOPPED &&
                        state != PlaybackStateCompat.STATE_NONE) {
                        MusicManager.getInstance().setRepeatMode(false);
                        msg.addProperty("error_code", "success");
                    } else {
                        msg.addProperty("error_code", "no_media");
                    }
                } else {
                    if (xmState != XMediaPlayer.PAUSED && xmState != XMediaPlayer.STARTED) {
                        msg.addProperty("error_code", "no_media");
                        break;
                    } else {
                        XmPlayerManager.getInstance(mContext).setPlayMode(
                                XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
                        msg.addProperty("error_code", "success");
                    }
                }
                break;

            default:
                break;
        }
        feedBackMsg(msg, nluResult);
        Log.e(TAG, "feed back end!!!");
    }

    private void feedBackLocalMsg(String feedBackMsg, NluResult nluResult) {
        Log.e("dydy", "feedBackMsg: " + feedBackMsg);
        if (mIsNeedResponse) {
            LocalDeviceReceive deviceReceive = new LocalDeviceReceive();
            deviceReceive.setContent(feedBackMsg);
            deviceReceive.setMsgid(nluResult.getMsgid());
            deviceReceive.setOnline(nluResult.isOnline());
            deviceReceive.setSession(nluResult.getSession());
            deviceReceive.setType("result");
            String json = new Gson().toJson(deviceReceive);
            Log.e("dydy", "json: " + json);
            SocketUtil.getInstance(mContext).sendMsgByTcp(json);
        }
    }

    private void feedBackMsg(JsonObject msg, NluResult nluResult) {
        feedBackMsg(msg, nluResult.getMsgid(), nluResult.isOnline(), nluResult.getSession());
    }

    private void feedBackMsg(JsonObject msg, int msgId, boolean isOnline, String session) {
        if (msg == null) {
            Log.e(TAG, "feedback msg is null!!!!");
            return;
        }
        if (!mIsNeedResponse) {
            Log.e(TAG, "no need of response");
        }
        String feedBackMsg = msg.toString();
        Log.e("dydy", "feedBackMsg: " + feedBackMsg);
        DeviceReceive deviceReceive = new DeviceReceive();
        deviceReceive.setContent(msg);
        deviceReceive.setMsgid(msgId);
        deviceReceive.setOnline(isOnline);
        deviceReceive.setSession(session);
        deviceReceive.setType("result");
        String feedbackResult = deviceReceive.toString();
        String json = new Gson().toJson(deviceReceive);
        Log.e("dydy", "feedbackResult: " + feedbackResult + ", json: " + json);
        SocketUtil.getInstance(mContext).sendMsgByTcp(json);
    }

    private void startPlayDuiMusic(ArrayList<MusicBean> musicList) {
        if (!MusicManager.getInstance().hasListener(mAudioStatusChangeListener)) {
            MusicManager.getInstance().addOnAudioStatusListener(mAudioStatusChangeListener);
        }
        SpeechFloatingLayer.get(mContext).startMusic(musicList);
        Log.e("MusicManager", "add music listener in speech manager");
    }

    private MusicManager.OnAudioStatusChangeListener mAudioStatusChangeListener = new MusicManager.OnAudioStatusChangeListener() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {

        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            Log.e(TAG, "onMetadataChanged");
            if (mIsNeedFeedbackOnMetaDataChanged && mFeedbackMsg != null && mNluResult != null) {
                if (metadata.getDescription() != null) {
                    mMusicTitle = metadata.getDescription().getTitle();
                    mMusicSubtitle = metadata.getDescription().getSubtitle();
                    if (mMusicTitle != null && mMusicSubtitle != null) {
                        mFeedbackMsg.addProperty("title", mMusicSubtitle + "的" + mMusicTitle);
                        feedBackMsg(mFeedbackMsg, mNluResult);
                        mIsNeedFeedbackOnMetaDataChanged = false;
                        mFeedbackMsg = null;
                        mNluResult = null;
                    }
                }
            }
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {

        }

        @Override
        public void onProgressInit(PlaybackStateCompat state) {

        }
    };

    public void activeStartIntent(String content) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "request_intent");
        jsonObject.addProperty("content", content);
        SocketUtil.getInstance(mContext).sendMsgByTcp(new Gson().toJson(jsonObject));
    }

    private SpeechManager() {
        //        createAction();
        //        initSub();
    }

    private void initNetConfigBusiness() {
        Log.e(TAG, "initNetConfigBusiness");
        NetConfigBusiness.getInstance().init(mContext);
        NetConfigBusiness.getInstance().registerListener(mListener);
    }

/*    private void createAction() {
        mActionDeviceMessage = new Action1<DeviceMessage>() {
            @Override
            public void call(DeviceMessage deviceMessage) {
                if (deviceMessage == null) {
                    return;
                }
                if (TextUtils.isEmpty(deviceMessage.params)) {
                    return;
                }
                if (deviceMessage.code == 6) {
                    //获取最近一次家庭备忘时间存储
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(deviceMessage.params);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonObject != null && !TextUtils.isEmpty(jsonObject.optString("home_id"))) {
                        mHomeId = Integer.parseInt(jsonObject.optString("home_id"));
                    }
                }
            }
        };
    }*/

/*    private void initSub() {
        //添加action
        if (null != mActionDeviceMessage) {
            DeviceReportManager.getInstance().addMessageAction(mActionDeviceMessage);
        }
    }*/

    public static SpeechManager getInstance() {
        if (mSpeechManager == null) {
            synchronized (SpeechManager.class) {
                if (mSpeechManager == null) {
                    mSpeechManager = new SpeechManager();
                }
            }
        }
        return mSpeechManager;
    }

    public boolean isConnected() {
        return mConnectSuccess;
    }

    public boolean isConnecting() {
        return mIsConnecting;
    }

    public boolean bindSpeech() {
        Log.e(TAG, "bind speech");
        mConnectSuccess = false;
        mIsConnecting = false;
        //        String password = (String) PreferenceUtil.getPreferenceValue(mContext,
        //                PreferenceUtil.LAST_CONNECTED_WIFI_PWD, null);
        //        Log.e(TAG, "wifi password: " + password);
        String homeId = ((boolean) PreferenceUtil.getPreferenceValue(mContext,
                PreferenceUtil.DEVICE_BIND_STATE, false)) ? (String) PreferenceUtil
                .getPreferenceValue(mContext, PreferenceUtil.HOME_ID, "") : "0";
        if (mHomeId == 0 && !TextUtils.isEmpty(homeId)) {
            mHomeId = Integer.parseInt(homeId);
        }
        Log.e(TAG, "mHomeId: " + mHomeId);
        return NetConfigBusiness.getInstance().startNetConfig(mHomeId);
    }

    public void stopSpeech() {
        NetConfigBusiness.getInstance().manualDialogControl(false);
        SpeechFloatingLayer.get(mContext).closeSpeech(true);
    }

    public void startSpeech() {
        NetConfigBusiness.getInstance().manualDialogControl(true);
    }

    public void probeBox() {
        mConnectSuccess = false;
        mIsConnecting = false;
        NetConfigBusiness.getInstance().restoreState(false);
        NetConfigBusiness.getInstance().probeVBox();
        Log.e("NetConfigBusiness", "when probe box, mConnectSuccess is " + mConnectSuccess);
    }

    private NetConfigStateChangeListener mListener = new NetConfigStateChangeListener() {
        @Override
        public void onNetConfigStateChange(CommonConst.ConfigState state) {
            switch (state) {
                case PUB_AP_ENABLE:
                    //net will be disconnected

                    break;
                case CONNECT_BOX_SUCCESS:
                    mConnectSuccess = true;
                    mIsConnecting = false;
                    Log.e("NetConfigBusiness",
                            "when connect box success, mConnectSuccess is " + mConnectSuccess);
                    clearLinkDialog();
                    EventBus.getDefault().post(new BoxLinkState(true));
                    //若在引导页
                    if (PreferenceUtil.isInGuide(mContext)) {
                        SnakeBar.makeToastSnake(mContext, "连接成功").show();
                    } else {
                        //亮屏
                        Tool.lightScreen(mContext);
                        //暂停屏保
                        ScreenTool.getInstance().addResetData("show box link success toast end");
                        SnakeBar.makeToastSnake(mContext, "连接成功").show();
                        XmPlayerManager.getInstance(mContext).addPlayerStatusListener(
                                mPlayerStatusListener);
                    }
                    DeviceControl.getInstance().setVboxPower("on");
                    PreferenceUtil.setBoxPowerState(mContext, true);
                    break;
                case START_TCP:
                    mIsConnecting = true;
                    break;
                case BOX_DISCONNECT:
                    //                    SnakeBar.makeToastSnake(mContext, "断开与盒子连接，请检查家庭是否改变").show();
                    XmPlayerManager.getInstance(mContext).removePlayerStatusListener(
                            mPlayerStatusListener);
                    DialogManager.getInstance().showExceptionDialog(mContext,
                            mContext.getString(R.string.str_box_disconnect_title),
                            mContext.getString(R.string.str_boxed_disconnect), "去添加", true, "取消",
                            false, PreferenceUtil.isInGuide(mContext),
                            new BoxLinkExceptionDialog.BtnClickListener() {
                                @Override
                                public void onTopBtnClick() {
                                    Log.e(TAG, "跳转至添加盒子页面");
                                    SpeechManager.getInstance().bindSpeech();
                                    new BoxLinkPreDialog(mContext).show();
                                }

                                @Override
                                public void onBottomBtnClick() {

                                }
                            });
                    //                case BOX_POWER_OFF://断电
                    //                    // TODO: 2019/6/24 是否需要后续操作？
                    //                    Log.e(TAG, "disconnect by power off!");
                    //                    break;
                case BOX_ACTIVELY_DISCONNECT:
                    mConnectSuccess = false;
                    mIsConnecting = false;
                    XmPlayerManager.getInstance(mContext).removePlayerStatusListener(
                            mPlayerStatusListener);
                    Log.e("NetConfigBusiness",
                            "when disconnect box success, mConnectSuccess is " + mConnectSuccess);
                    if (!PreferenceUtil.isInGuide(mContext)) {//若不在引导页
                        //                        DockStateBarView.getInstance(mContext).setSpeechIconVisiable(false);
                        //                        probeBox();
                    }
                    EventBus.getDefault().post(new BoxLinkState(false));
                    break;
                //                case SOCKET_CONNECT_TIME_OUT:
                //
                //                    break;
                case CONNECT_ACTUALLY_NET:
                    Log.e("NetConfigBusiness", "CONNECT_ACTUALLY_NET!!!");
                    EventBus.getDefault().post(new BoxLinkingStep());
                    break;
                case BOX_DISCONNECTED:
                    if (SpeechFloatingLayer.get(mContext).isShowing()) {
                        SpeechFloatingLayer.get(mContext).forceDismiss(false);
                    }
                    //                    BoxWifiChangeDialog dialog = new BoxWifiChangeDialog(mContext);
                    //                    dialog.show();
                    Log.e("NetConfigBusiness", "box disconnected!!!");
                    DialogManager.getInstance().showExceptionDialog(mContext,
                            mContext.getString(R.string.str_box_disconnect_title),
                            mContext.getString(PreferenceUtil.isInGuide(mContext)
                                               ? R.string.str_box_network_change
                                               : R.string.str_box_network_change_reconnect),
                            PreferenceUtil.isInGuide(mContext) ? null : "去检查", true, "确定", false,
                            PreferenceUtil.isInGuide(mContext),
                            new BoxLinkExceptionDialog.BtnClickListener() {
                                @Override
                                public void onTopBtnClick() {
                                    startActivityBySpeech(
                                            new Intent(mContext, WifiListActivity.class));
                                }

                                @Override
                                public void onBottomBtnClick() {

                                }
                            });

                    mConnectSuccess = false;
                    mIsConnecting = false;
                    XmPlayerManager.getInstance(mContext).removePlayerStatusListener(
                            mPlayerStatusListener);
                    if (!PreferenceUtil.isInGuide(mContext)) {//若不处于引导界面
                        EventBus.getDefault().post(new BoxLinkState(false));
                        probeBox();
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNetConfigException(CommonConst.ConfigError errorType) {
            switch (errorType) {
                case HEARTBEAT_TIMEOUT:
                    //在disconnected中处理
                    //                    SnakeBar.makeBoxDisconnectedMsg(mContext).show();
                    break;
                case CONNECT_BOX_FAILED:
                    //最终会走到超时退出
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onDialogStateChange(CommonConst.DialogState state) {
            if (mContext instanceof MainActivity) {
                Log.e("dydy", "state change: " + state);
                switch (state) {
                    case idle:
                        mHandler.sendEmptyMessage(MSG_STATE_IDLE);
                        break;
                    case listen:
                        mHandler.sendEmptyMessage(MSG_STATE_LISTEN);
                        break;
                    case listen_actively:
                        mHandler.sendEmptyMessage(MSG_STATE_LISTEN_ACTIVELY);
                        break;
                    case understanding:
                        mHandler.sendEmptyMessage(MSG_STATE_UNDERSTANDING);
                        break;
                    case stop_tts:
                        mHandler.sendEmptyMessage(MSG_STATE_STOP_TTS);
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void onWakeUp(String content) {
            if (mContext instanceof MainActivity) {
                Message msg = new Message();
                msg.what = MSG_WAKE_UP;
                if (!TextUtils.isEmpty(content)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("content", content);
                    msg.setData(bundle);
                }
                mHandler.sendMessage(msg);
            }
        }

        @Override
        public void onMusicStart(String musicList) {
            if (mContext instanceof MainActivity) {
                Log.e(TAG, "music list get" + musicList.length());
                Message msg = new Message();
                msg.what = MSG_MUSIC_START;
                Bundle bundle = new Bundle();
                bundle.putString("music_list", musicList);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }

        @Override
        public void onMusicStop() {
            mHandler.sendEmptyMessage(MSG_MUSIC_STOP);
        }

        @Override
        public void onRecipesGet(String recipes) {
            Log.e(TAG, "recipe list get: " + recipes.length() + ", " + recipes);
            Message msg = new Message();
            msg.what = MSG_GET_RECIPES;
            Bundle bundle = new Bundle();
            bundle.putString("recipes", recipes);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        @Override
        public void onVboxError(String error) {
            // TODO: 2019/4/11 禁止使用语音
        }
/*        @Override
        public void onSleep(String reason, String error) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_FALL_SLEEP));
        }

        @Override
        public void onNoVoice(String content) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_RECEIVED_WORDS, content));
        }*/


        @Override
        public void onHeard(int type, String asrResult) {
            if (mContext instanceof MainActivity) {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_RECEIVED_WORDS, asrResult));
            }
        }

        @Override
        public void onSpeak(String content) {
            if (mContext instanceof MainActivity) {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_OUTPUT_WORDS, content));
            }
        }

        @Override
        public void onUnderStand(NluResult nluResult) {
            if (mContext instanceof MainActivity) {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_UNDERSTAND_RESULT, nluResult));
            }
        }
    };

    /**
     * 喜马拉雅内部类
     */
    private IXmPlayerStatusListener mPlayerStatusListener = new MusicPlayerStateListener() {
        /**
         *切歌
         * @param laModel 上一首model,可能为空
         * @param curModel 下一首model
         */
        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            LogUtil.LOGE(TAG, "SoundSwitch");
            String title = null;
            String name = null;
            PlayableModel model = XmPlayerManager.getInstance(mContext).getCurrSound();
            if (mIsNeedFeedbackOnMetaDataChanged && mFeedbackMsg != null && mNluResult != null) {
                if (model instanceof Track) {
                    Track info = (Track) model;
                    title = info.getTrackTitle();
                    name = info.getAnnouncer().getNickname();
                    mFeedbackMsg.addProperty("title",
                            (TextUtils.isEmpty(name) ? mContext.getResources()
                                    .getString(com.fotile.music.R.string.unknown_author) : name) +
                            "的" + title);
                    feedBackMsg(mFeedbackMsg, mNluResult);
                    mIsNeedFeedbackOnMetaDataChanged = false;
                }
            }
        }

        /**
         * 播放进度回调
         * @param currPos
         * @param duration
         */
        @Override
        public void onPlayProgress(int currPos, int duration) {
        }

        //播放停止
        @Override
        public void onPlayStop() {
            Log.e(TAG, "on play stop");
        }

        //播放开始
        @Override
        public void onPlayStart() {
        }

        //播放停止
        @Override
        public void onPlayPause() {
        }

        /**
         * 播放完成
         */
        @Override
        public void onSoundPlayComplete() {
        }

        @Override
        public boolean onError(XmPlayerException e) {
            return super.onError(e);
        }
    };

    private void clearLinkDialog() {
        // TODO: 2019/5/23 连接盒子的几个dialog也需要清除
        DialogManager.getInstance().dismissExceptionDialog();
    }

    private void setJsonElement(@NonNull JsonObject msg, @NonNull String name,
            @NonNull String value) {
        msg.addProperty(name, value);
    }

    public void releaseResource() {
        WifiAPManager.getInstance(mContext).unregisterHandler();
        WifiAPManager.getInstance(mContext).unregisterReceiver();
        WifiUtil.getInstance(mContext).unregitsterHandler();
    }

    public void broadcast(String content) {
        NetConfigBusiness.getInstance().broadcast(content);
    }

    public void startActivityBySpeech(Intent intent) {
        Boolean isLock = (Boolean) PreferenceUtil.getPreferenceValue(mContext,
                PreferenceUtil.IS_SCREEN_LOCK, false);
        if (isLock) {
            AppUtil.removeLockScreen(mContext);
            PreferenceUtil.setPreferenceValue(mContext, PreferenceUtil.IS_SCREEN_LOCK, false);
        }
        mContext.startActivity(intent);
    }

    public void testMusic(String test_music_list) {
        List<MusicBean> musicList = JSON.parseArray(test_music_list, MusicBean.class);
        if (musicList != null && !musicList.isEmpty()) {
            ArrayList<MusicBean> musicArrayList = new ArrayList<>(musicList);
            Log.e(TAG, "musicList: " + musicList);
            startPlayDuiMusic(musicArrayList);
        }
    }

    @Subscribe
    public void onDelayCloseStateChange(DelayCloseEvent event) {
        if (event != null) {
            mIsInDelayClosing = event.isInDelay();
            Log.e("coiliaspp", "delay event: " + event.isInDelay());
        }
    }
}
