/*
 * ************************************************************
 * 文件：SpeechFloatingLayer.java  模块：app  项目：Z1.5
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.x1i.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.fotile.common.util.ImageFrameHandlerUtil;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.music.manager.MusicManager;
import com.fotile.music.model.MusicBean;
import com.fotile.music.widget.GlideRoundTransform;
import com.fotile.music.widget.ProgressTouchView;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.bean.recipe.RecipeBanner;
import com.fotile.recipe.net.presenter.BannerRecipePresenter;
import com.fotile.recipe.net.view.BannerRecipeView;
import com.fotile.voice.CommonConst;
import com.fotile.voice.bean.SpeechRecipeBean;
import com.fotile.voice.net.SpeechNetHelper;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.music.DuiMusicPlayActivity;
import com.fotile.x1i.activity.recipe.RecipesDetailsActivity;
import com.fotile.x1i.activity.setting.DeviceLinkageActivity;
import com.fotile.x1i.adapter.speech.SpeechHelpAdapter;
import com.fotile.x1i.adapter.speech.SpeechRecipesAdapter;
import com.fotile.x1i.listener.OnItemClickListener;
import com.fotile.x1i.manager.SpeechManager;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.util.AnimConstant;
import com.fotile.x1i.util.AppUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

public class SpeechFloatingLayer extends LinearLayout implements View.OnClickListener {

    private static final String TAG = SpeechFloatingLayer.class.getSimpleName();
    private static volatile SpeechFloatingLayer mSpeechFloatingLayer = null;
    private Context mContext;
    private Activity mActivity;
    private PlaybackStateCompat mLastPlaybackState;
    private SpeechRecipesAdapter mAdapter;
    private ArrayList<SpeechRecipeBean> mRecipeList;
    private SpeechHelpAdapter mHelpAdapter;
    private ArrayList<String> mHelpList;
    private ImageFrameHandlerUtil mSpeechAnim;
    private SpeechNetHelper mHelper;
    private int mMaxVolume;
    private AudioManager mAudioManager;
    private boolean mIsIdleState;
    private boolean mIsNeedDelay;
    private int[] mWakeupInitResIds;
    private int[] mWakeupCycleResIds;
    private int[] mHeardInitResIds;
    private int[] mHeardCycleResIds;
    private int[] mUnderstandInitResIds;
    private int[] mUnderstandCycleResIds;
    private int[] mSpeakInitResIds;
    private int[] mSpeakCycleResIds;

    private static final int MSG_ANIM_WAKE_UP_CYCLE = 30001;
    private static final int MSG_ANIM_HEARD_CYCLE = 30002;
    private static final int MSG_ANIM_UNDERSTAND_CYCLE = 30003;
    private static final int MSG_ANIM_SPEAK_CYCLE = 30004;
    private static final int MSG_DISMISS = 30005;
    private static final int MSG_DISMISS_DELAY = 30006;
    private static final int MSG_RECIPES_SHOW = 30007;
    private static final int MSG_HELP_CONTENT_GET = 30008;
    private static final int MSG_HELP_CONTENT_GET_FAIL = 30009;

    /**
     * 传值list<recipe>的key
     */
    public static final String RECIPE_BEAN = "recipe";

    private String mDeviceLinkTag;

    //@BindView(R.id.iv_speech_help)
    ImageView mSpeechHelperIv;

    //@BindView(R.id.iv_close_speech)
    ImageView mCloseSpeechIv;

    //@BindView(R.id.tv_speech_question)
    TextView mSpeechQuestionTv;

    //@BindView(R.id.tv_speech_answer)
    TextView mSpeechAnswerTv;

    //@BindView(R.id.ll_device_link)
    LinearLayout mLinkToCookerLl;

    //@BindView(R.id.tv_device_link)
    TextView mDeviceLinkTv;

    //@BindView(R.id.tv_device_link_description)
    TextView mDeviceLinkDescriptionTv;

    //@BindView(R.id.rl_speech_brightness_setting)
    RelativeLayout mBrightnessSettingLl;

    //@BindView(R.id.rl_speech_sound_setting)
    RelativeLayout mSoundSettingLl;

    //@BindView(R.id.rl_speech_recipe)
    RelativeLayout mSpeechRecipeRl;

    //@BindView(R.id.rl_speech_music)
    RelativeLayout mSpeechMusicRl;

    //@BindView(R.id.ll_speech_help)
    LinearLayout mSpeechHelpLl;

    //@BindView(R.id.btn_speech_link_switch)
    //    SwitchButton mLinkSv;
    Switch mLinkSv;

    //@BindView(R.id.sb_speech_brightness)
    SeekBar mBrightnessSb;

    //@BindView(R.id.tv_speech_brightness)
    TextView mBrightnessTv;

    //@BindView(R.id.sb_speech_sound)
    SeekBar mSoundSb;

    //@BindView(R.id.tv_speech_sound)
    TextView mSoundTv;

    //@BindView(R.id.rv_speech_recipe)
    RecyclerView mRecipeRv;

    //@BindView(R.id.iv_speech_music_action)
    ImageView mSpeechMusicAction;

    //@BindView(R.id.iv_speech_music_img)
    ImageView mSpeechMusicImg;

    //@BindView(R.id.tv_speech_music_name)
    TextView mSpeechMusicNameTv;

    //@BindView(R.id.tv_speech_music_singer)
    TextView mSpeechMusicSingerTv;

    //@BindView(R.id.iv_speech_anim)
    ImageView mIvSpeechAnim;

    //@BindView(R.id.rv_speech_example)
    RecyclerView mRvHelpContent;

    //@BindView(R.id.tv_change)
    TextView mChangeTv;

    //    Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ANIM_WAKE_UP_CYCLE:
                    Log.e(TAG, "show wake up cycle after init");
                    showWaitingAnim();
                    break;
                case MSG_ANIM_HEARD_CYCLE:
                    stopAnim();
                    mSpeechAnim = new ImageFrameHandlerUtil(mContext, mIvSpeechAnim,
                            mHeardCycleResIds, 55, true);
                    mSpeechAnim.start();
                    Log.e(TAG, "start heard cycle anim after init");
                    break;
                case MSG_ANIM_UNDERSTAND_CYCLE:
                    stopAnim();
                    mSpeechAnim = new ImageFrameHandlerUtil(mContext, mIvSpeechAnim,
                            mUnderstandCycleResIds, 55, true);
                    mSpeechAnim.start();
                    Log.e(TAG, "start understand cycle anim after init");
                    break;
                case MSG_ANIM_SPEAK_CYCLE:
                    stopAnim();
                    mSpeechAnim = new ImageFrameHandlerUtil(mContext, mIvSpeechAnim,
                            mSpeakCycleResIds, 55, true);
                    mSpeechAnim.start();
                    Log.e(TAG, "start speak cycle anim after init");
                    break;
                case MSG_DISMISS_DELAY:
                    mIsNeedDelay = false;
                    Log.e(TAG, "set mIsNeedDelay false, on dismiss delay case, mIsIdleState: " +
                               mIsIdleState);
                    if (mIsIdleState) {
                        dismiss(true);
                    }
                    break;
                case MSG_DISMISS:
                    Log.d(TAG, "dismiss, mIsNeedDelay: " + mIsNeedDelay);
                    if (!mIsNeedDelay) {
                        stopAnim();
                        clearDialogArea();
                        Log.e(TAG, "clear function when dismiss");
                        clearFunctionArea();
                        setVisibility(GONE);
                        if ((Boolean) PreferenceUtil.getPreferenceValue(mContext,
                                PreferenceUtil.IS_SCREEN_LOCK, false)) {
                            Log.e("lock_screen", "show lock screen by app util when dismiss");
                            AppUtil.showLockScreen(AppUtil.getCurrentActivity(null), null);
                        }
                        Log.e("lock", "no lock screen");
                        if (!PreferenceUtil.isInGuide(mContext)) {
                            ScreenTool.getInstance().addResetData("speech floating layer dismiss");
                            BottomView.getInstance(mContext).show();
                        }
                        mIsIdleState = false;
                    }
                    break;
                case MSG_RECIPES_SHOW:
                    Recipe recipe = msg.getData().getParcelable("recipe_show");
                    Log.e(TAG, "selected speech recipe: " + recipe);
                    Intent intent = new Intent(mContext, RecipesDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(RECIPE_BEAN, recipe);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    forceDismiss(false);
                    break;
                case MSG_HELP_CONTENT_GET:
                    Bundle data = msg.getData();
                    Log.e(TAG, "get list");
                    mHelpList = data.getStringArrayList("content_list");
                    mHelpList = processHelpList(mHelpList);
                    mHelpAdapter.updateHelpList(mHelpList);
                    mHelpAdapter.notifyDataSetChanged();
                    mSpeechHelpLl.setVisibility(VISIBLE);
                    break;
                case MSG_HELP_CONTENT_GET_FAIL:
                    mHelpList = null;
                    mHelpAdapter.notifyDataSetChanged();
                    mSpeechHelpLl.setVisibility(VISIBLE);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private ArrayList<String> processHelpList(ArrayList<String> helpContentList) {
        // TODO: 2019/6/28 若list为空或size为空则加载本地数据
        if (helpContentList == null) {
            return null;
        }
        Collections.shuffle(helpContentList);
        if (helpContentList.size() > 7) {
            return new ArrayList<>(helpContentList.subList(0, 7));
        } else {
            return helpContentList;
        }
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            SpeechRecipeBean speechRecipeBean = mRecipeList.get(position);
            SpeechManager.getInstance().activeStartIntent(speechRecipeBean.getName());
        }
    };

    private void showRecipePage(SpeechRecipeBean speechRecipeBean) {
        if (Tool.isNetworkAvailable(mContext)) {
            String deviceId = (String) PreferenceUtil.getPreferenceValue(mContext,
                    PreferenceUtil.STOVE_DEVICE_ID, "");
            String token = PreferenceUtil.getStoveUploadRecipeToken(
                    mContext.getApplicationContext());
            BannerRecipePresenter recipeBannerPresenter = new BannerRecipePresenter(mContext);
            CompositeSubscription compositeSubscription = new CompositeSubscription();
            recipeBannerPresenter.onCreate(compositeSubscription);
            recipeBannerPresenter.attachBannerRecipeView(mBannerView);
            recipeBannerPresenter.getRecipeById(speechRecipeBean.get_id(), token, deviceId);
        }
    }

    private BannerRecipeView mBannerView = new BannerRecipeView() {
        @Override
        public void onRecipeBannerSuccess(List<RecipeBanner> list) {

        }

        @Override
        public void onRecipeBannerError(String e) {

        }

        @Override
        public void onRecipeBannerDetailSuccess(Recipe recipe) {
            Log.e(TAG, "recipe: " + recipe);
            if (recipe != null) {
                //获取完成
                Bundle bundle = new Bundle();
                bundle.putParcelable("recipe_show", recipe);
                Message msg = new Message();
                msg.setData(bundle);
                msg.what = MSG_RECIPES_SHOW;
                mHandler.sendMessage(msg);
            } else {
                Log.e(TAG, "get recipe null!!!!");
            }
        }

        @Override
        public void onRecipeBannerDetailError(String e) {

        }
    };

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        Log.e(TAG, "onVisibilityChanged, visibility: " + visibility);
        if (visibility == VISIBLE) {
            Log.e("DiffuseView", "dismiss when floating layer show");
            //            DiffuseView.getInstance(mContext).dismiss();
            ScreenTool.getInstance().addPause();
        } else if (visibility == GONE || visibility == INVISIBLE) {
            if (SpeechManager.getInstance().isConnected()) {
                Log.e("DiffuseView", "show when floating layer dismiss");
                //                DiffuseView.getInstance(mContext).show();
            }
            ScreenTool.getInstance().addResetData("speech floating layer back");
        }
    }

    private SpeechFloatingLayer(Context context) {
        super(context);
    }

    private SpeechFloatingLayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e(TAG, "MotionEvent: " + ev.getAction());
        if (ev.getAction() == KeyEvent.ACTION_UP) {
            mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DELAY, CommonConst.ONE_SECOND * 10);
            Log.e(TAG, "send delay msg because dispatch touch event: " +
                       mHandler.hasMessages(MSG_DISMISS_DELAY));
        } else {
            mIsNeedDelay = true;
            Log.e(TAG, "set mIsNeedDelay true");
            if (mHandler.hasMessages(MSG_DISMISS_DELAY)) {
                mHandler.removeMessages(MSG_DISMISS_DELAY);
                Log.e(TAG, "remove delay msg");
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private SpeechFloatingLayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static SpeechFloatingLayer get(Context context) {
        if (mSpeechFloatingLayer == null) {
            synchronized (SpeechFloatingLayer.class) {
                if (mSpeechFloatingLayer == null) {
                    mSpeechFloatingLayer = new SpeechFloatingLayer(context);
                }
            }
        }
        return mSpeechFloatingLayer;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close_speech:
                // TODO: 2018/7/18 此处需解耦
                Log.e(TAG, "click close btn");
                SpeechManager.getInstance().stopSpeech();
                break;
            case R.id.iv_speech_help:
                // TODO: 2018/6/25 go to speech help
                Log.e(TAG, "speech help click");
                clearDialogArea();
                clearFunctionArea();
                getHelpContent();
                break;
            case R.id.rl_speech_music:
                closeSpeech(false);
                startActivity(DuiMusicPlayActivity.class);
                break;
            case R.id.iv_speech_music_action:
                if (MusicManager.getInstance().isPlaying()) {
                    MusicManager.getInstance().pause();
                } else {
                    MusicManager.getInstance().play();
                }
                break;
            case R.id.ll_device_link:
                closeSpeech(false);
                startActivity(DeviceLinkageActivity.class);
                break;
            case R.id.rv_speech_recipe:
                closeSpeech(false);
                //                startActivity();
                break;
            case R.id.tv_change:
                clearDialogArea();
                clearFunctionArea();
                getHelpContent();
                break;
            default:
                break;
        }
    }

    private void getHelpContent() {
        if (mHelper != null) {
            mHelper.getSpeechContent();
        }
    }

    SpeechNetHelper.SpeechContentGetListener helpListener = new SpeechNetHelper.SpeechContentGetListener() {
        @Override
        public void onContentGet(List<String> list) {
            Log.e(TAG, "get list success");
            ArrayList<String> strings = new ArrayList<>(list);
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("content_list", strings);
            msg.setData(bundle);
            msg.what = MSG_HELP_CONTENT_GET;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onGetFail() {
            Log.e(TAG, "get list fail");
            mHandler.sendEmptyMessage(MSG_HELP_CONTENT_GET_FAIL);
        }
    };

    public void init(Context context) {
        Log.e(TAG, "init");
        mContext = context;
        Log.e(TAG, "mContext: " + mContext);
        LayoutInflater inflater = LayoutInflater.from(context);
        Log.e(TAG, "LayoutInflater: " + inflater);
        View view = inflater.inflate(R.layout.layout_speech_mask, this, true);
        Log.e(TAG, "view: " + view);
        //        Unbinder bind = ButterKnife.bind(this, view);
        //        Log.e(TAG, "ButterKnife: " + bind);
        initView();
        initData();
        Log.e(TAG, "init complete");
    }

    private void initData() {
        Log.e(TAG, "initData");
        mRecipeList = new ArrayList<>();
        mAdapter = new SpeechRecipesAdapter(mContext, mRecipeList);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecipeRv.setLayoutManager(layoutManager);
        mRecipeRv.setAdapter(mAdapter);
        mHelpList = new ArrayList<>();
        mHelpAdapter = new SpeechHelpAdapter(mContext, mHelpList);
        mHelper = new SpeechNetHelper(helpListener);
        LinearLayoutManager speechLayoutManager = new LinearLayoutManager(mContext);
        speechLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvHelpContent.setLayoutManager(speechLayoutManager);
        mRvHelpContent.setAdapter(mHelpAdapter);
        mWakeupInitResIds = getResIds(AnimConstant.ANIM_SPEECH_WAKEUP_INIT);
        mWakeupCycleResIds = getResIds(AnimConstant.ANIM_SPEECH_WAKEUP_CYCLE);
        mHeardInitResIds = getResIds(AnimConstant.ANIM_SPEECH_HEARD_INIT);
        mHeardCycleResIds = getResIds(AnimConstant.ANIM_SPEECH_HEARD_CYCLE);
        mUnderstandInitResIds = getResIds(AnimConstant.ANIM_SPEECH_UNDERSTAND_INIT);
        mUnderstandCycleResIds = getResIds(AnimConstant.ANIM_SPEECH_UNDERSTAND_CYCLE);
        mSpeakInitResIds = getResIds(AnimConstant.ANIM_SPEECH_SPEAK_INIT);
        mSpeakCycleResIds = getResIds(AnimConstant.ANIM_SPEECH_SPEAK_CYCLE);
    }

    private void initView() {
        Log.e(TAG, "initView");
        mSpeechHelperIv = (ImageView) findViewById(R.id.iv_speech_help);
        mCloseSpeechIv = (ImageView) findViewById(R.id.iv_close_speech);
        mSpeechQuestionTv = (TextView) findViewById(R.id.tv_speech_question);
        mSpeechAnswerTv = (TextView) findViewById(R.id.tv_speech_answer);
        mLinkToCookerLl = (LinearLayout) findViewById(R.id.ll_device_link);
        mDeviceLinkTv = (TextView) findViewById(R.id.tv_device_link);
        mDeviceLinkDescriptionTv = (TextView) findViewById(R.id.tv_device_link_description);
        mBrightnessSettingLl = (RelativeLayout) findViewById(R.id.rl_speech_brightness_setting);
        mSoundSettingLl = (RelativeLayout) findViewById(R.id.rl_speech_sound_setting);
        mSpeechRecipeRl = (RelativeLayout) findViewById(R.id.rl_speech_recipe);
        mSpeechMusicRl = (RelativeLayout) findViewById(R.id.rl_speech_music);
        mSpeechHelpLl = (LinearLayout) findViewById(R.id.ll_speech_help);
        mLinkSv = (Switch) findViewById(R.id.btn_speech_link_switch);
        mBrightnessSb = (SeekBar) findViewById(R.id.sb_speech_brightness);
        mBrightnessTv = (TextView) findViewById(R.id.tv_speech_brightness);
        mSoundSb = (SeekBar) findViewById(R.id.sb_speech_sound);
        mSoundTv = (TextView) findViewById(R.id.tv_speech_sound);
        mRecipeRv = (RecyclerView) findViewById(R.id.rv_speech_recipe);
        mSpeechMusicAction = (ImageView) findViewById(R.id.iv_speech_music_action);
        mSpeechMusicImg = (ImageView) findViewById(R.id.iv_speech_music_img);
        mSpeechMusicNameTv = (TextView) findViewById(R.id.tv_speech_music_name);
        mSpeechMusicSingerTv = (TextView) findViewById(R.id.tv_speech_music_singer);
        mIvSpeechAnim = (ImageView) findViewById(R.id.iv_speech_anim);
        mRvHelpContent = (RecyclerView) findViewById(R.id.rv_speech_example);
        mChangeTv = (TextView) findViewById(R.id.tv_change);
        Log.e(TAG, "findViewById complete");
        mSpeechHelperIv.setOnClickListener(this);
        mCloseSpeechIv.setOnClickListener(this);
        //        mBrightnessSb.setOnProgressTouchListener(mBrightnessProgressTouchListener);
        //        mSoundSb.setOnProgressTouchListener(mSoundProgressTouchListener);
        mBrightnessSb.setOnSeekBarChangeListener(mBrightnessChangeListener);
        mSoundSb.setOnSeekBarChangeListener(mVolumeChangeListener);
        mSpeechRecipeRl.setOnClickListener(this);
        mSpeechMusicRl.setOnClickListener(this);
        mSpeechMusicAction.setOnClickListener(this);
        mLinkToCookerLl.setOnClickListener(this);
        mRecipeRv.setOnClickListener(this);
        mChangeTv.setOnClickListener(this);
        mSpeechQuestionTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        mSpeechAnswerTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRvHelpContent.setLayoutManager(layoutManager);
        Log.e(TAG, "initView complete");
    }

    public void closeSpeech(boolean isNeedShowScreenLock) {
        ScreenTool.getInstance().addResetData("语音关闭");
        stopAnim();
        forceDismiss(isNeedShowScreenLock);
    }

    public void show() {
        if (!isShowing()) {
            Tool.lightScreen(mContext);
            ScreenTool.getInstance().addPause();
            setVisibility(VISIBLE);
            BottomView.getInstance(mContext).hide();
        }
    }

    public void bindActivity(Activity activity) {
        mActivity = activity;
        Log.e(TAG,
                "bind, SpeechFloatingLayer.get(mActivity): " + SpeechFloatingLayer.get(mActivity) +
                ", activity: " + mActivity);
    }

    public void unbindActivity() {
        mActivity = null;
        Log.e(TAG, "unbind, SpeechFloatingLayer.get(mActivity): " +
                   SpeechFloatingLayer.get(mActivity) + ", activity: " + mActivity);
    }

    // TODO: 2018/7/23 多个start可整合
    public void showHeardAnim() {
        if (isShowing()) {
            stopAnim();
            Log.e(TAG, "show heard anim");
            mSpeechAnim = new ImageFrameHandlerUtil(mContext, mIvSpeechAnim, mHeardInitResIds, 55,
                    false);
            mSpeechAnim.setPlayFinishListener(
                    new ImageFrameHandlerUtil.ImgFramePlayFinishListener() {
                        @Override
                        public void onPlayFinish() {
                            mHandler.sendEmptyMessage(MSG_ANIM_HEARD_CYCLE);
                        }
                    });
            mSpeechAnim.start();
        }
    }

    public void showActivelyHeardAnim() {
        if (isShowing()) {
            stopAnim();
            mSpeechAnim = new ImageFrameHandlerUtil(mContext, mIvSpeechAnim, mHeardCycleResIds, 55,
                    true);
            mSpeechAnim.start();
        }
    }


    public void showWakeUpAnim() {
        if (isShowing()) {
            stopAnim();
            mSpeechAnim = new ImageFrameHandlerUtil(mContext, mIvSpeechAnim, mWakeupInitResIds, 55,
                    false);
            mSpeechAnim.setPlayFinishListener(
                    new ImageFrameHandlerUtil.ImgFramePlayFinishListener() {
                        @Override
                        public void onPlayFinish() {
                            mHandler.sendEmptyMessage(MSG_ANIM_WAKE_UP_CYCLE);
                        }
                    });
            mSpeechAnim.start();
        }
    }

    public void showWaitingAnim() {
        if (isShowing()) {
            stopAnim();
            mSpeechAnim = new ImageFrameHandlerUtil(mContext, mIvSpeechAnim, mWakeupCycleResIds, 55,
                    true);
            mSpeechAnim.start();
        }
    }

    public void showUnderStandAnim() {
        if (isShowing()) {
            stopAnim();
            mSpeechAnim = new ImageFrameHandlerUtil(mContext, mIvSpeechAnim, mUnderstandInitResIds,
                    55, false);
            mSpeechAnim.setPlayFinishListener(
                    new ImageFrameHandlerUtil.ImgFramePlayFinishListener() {
                        @Override
                        public void onPlayFinish() {
                            mHandler.sendEmptyMessage(MSG_ANIM_UNDERSTAND_CYCLE);
                        }
                    });
            mSpeechAnim.start();
        }
    }

    public void showSpeakAnim() {
        stopAnim();
        mSpeechAnim = new ImageFrameHandlerUtil(mContext, mIvSpeechAnim, mSpeakInitResIds, 55,
                false);
        mSpeechAnim.setPlayFinishListener(new ImageFrameHandlerUtil.ImgFramePlayFinishListener() {
            @Override
            public void onPlayFinish() {
                mHandler.sendEmptyMessage(MSG_ANIM_SPEAK_CYCLE);
            }
        });
        mSpeechAnim.start();
    }

    public void stopAnim() {
        mHandler.removeMessages(MSG_ANIM_WAKE_UP_CYCLE);
        mHandler.removeMessages(MSG_ANIM_HEARD_CYCLE);
        mHandler.removeMessages(MSG_ANIM_UNDERSTAND_CYCLE);
        mHandler.removeMessages(MSG_ANIM_SPEAK_CYCLE);
        if (mSpeechAnim != null) {
            mSpeechAnim.stop(true);

            Log.e(TAG, "stop anim");
        }
    }

    public int[] getResIds(String[] imgNames) {
        String packageName = mContext.getPackageName();
        int[] resIds = new int[imgNames.length];
        try {
            for (int i = 0; i <= imgNames.length - 1; i++) {
                resIds[i] = mContext.getResources().getIdentifier(imgNames[i], "mipmap",
                        packageName);
                //                Log.w(TAG, "res: " + resIds[i] + ", " +
                //                           mContext.getResources().openRawResource(resIds[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resIds;
    }

    public boolean isShowing() {
        return isShown();
    }

    public void dismissByIdle() {
        if (isShowing()) {
            mIsIdleState = true;
            Log.e(TAG, "set mIsIdleState: " + mIsIdleState + ", msg delay: " +
                       mHandler.hasMessages(MSG_DISMISS_DELAY) + ", mIsNeedDelay: " + mIsNeedDelay);
            //若通过触摸有延时指令则不考虑来自盒子的idle指令
            if (!mHandler.hasMessages(MSG_DISMISS_DELAY) || mIsNeedDelay) {
                if (isFunctionAreaShown() || isHelpContentShown()) {
                    mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DELAY,
                            CommonConst.ONE_SECOND * 10);
                    Log.e(TAG, "send delay msg because function or help content shown: " +
                               mHandler.hasMessages(MSG_DISMISS_DELAY));
                } else {
                    dismiss(true);
                    Log.e(TAG, "dismiss by idle directly");
                }
            }
        }
    }

    public void dismiss(boolean needShowScreenLock) {
        if (isShowing()) {
            if (!needShowScreenLock) {
                PreferenceUtil.setPreferenceValue(mContext, PreferenceUtil.IS_SCREEN_LOCK, false);
                Log.e("lock_screen", "set lock screen false when speech floating layer dismiss");
            }
            // TODO: 2019/4/3 主动调用时可能会被onTouch的延时打断？
            mHandler.sendEmptyMessage(MSG_DISMISS);
            Log.e(TAG, "send dismiss msg");
        }
    }

    public void forceDismiss(boolean needShowScreenLock) {
        if (isShowing()) {
            if (!needShowScreenLock) {
                PreferenceUtil.setPreferenceValue(mContext, PreferenceUtil.IS_SCREEN_LOCK, false);
                Log.e("lock_screen",
                        "set lock screen false when speech floating layer force dismiss");
            }
            mIsNeedDelay = false;
            mIsIdleState = true;
            mHandler.sendEmptyMessage(MSG_DISMISS);
        }
    }

    public void interruptDismiss() {
        mIsIdleState = false;
    }

    public void showAsrContent(String content) {
        Log.e(TAG, "asr content: " + content + ", " + isShowing());
        if (isShowing()) {
            clearDialogArea();
            mSpeechQuestionTv.setText(content);
            mSpeechQuestionTv.requestLayout();
            mSpeechQuestionTv.setVisibility(VISIBLE);
            Log.e(TAG, "show asr content: " + mSpeechQuestionTv.getVisibility() + ", " +
                       mSpeechQuestionTv.getText() + ", " + mSpeechQuestionTv.getMeasuredWidth());
        }
    }

    public void showTtsContent(String content) {
        Log.e(TAG, "tts content: " + content + ", " + isShowing());
        if (isShowing()) {
            clearHelpContent();
            mSpeechAnswerTv.setText(content);
            mSpeechAnswerTv.requestLayout();
            mSpeechAnswerTv.invalidate();
            mSpeechAnswerTv.setVisibility(VISIBLE);
            Log.e(TAG, "show tts content: " + mSpeechAnswerTv.getVisibility() + ", " +
                       mSpeechAnswerTv.getText());
        }
    }

    public void startMusic(ArrayList<MusicBean> musicList) {
        show();
        showMusicFunction();
        if (MusicManager.getInstance().hasListener(onAudioStatusChangeListener)) {
            MusicManager.getInstance().stop();
            MusicManager.getInstance().updatePlayList(musicList);
            MusicManager.getInstance().play();
        } else {
            MusicManager.getInstance().addOnAudioStatusListener(onAudioStatusChangeListener);
            MusicManager.getInstance().init(mContext, musicList, true);
            MusicManager.getInstance().connect();
        }
    }

    public void showMusicFunction() {
        if (isShowing()) {
            Log.e(TAG, "clear function before show music function");
            clearFunctionArea();
            Log.e(TAG, "show music function");
            mSpeechMusicRl.setVisibility(VISIBLE);
        }
    }

    public void showRecipe(ArrayList<SpeechRecipeBean> speechRecipeList) {
        Log.e(TAG, "show recipe list: " + speechRecipeList.get(0) + speechRecipeList.size());
        if (speechRecipeList.size() == 1) {
            SpeechRecipeBean speechRecipeBean = speechRecipeList.get(0);
            showRecipePage(speechRecipeBean);
        } else {
            clearFunctionArea();
            mRecipeList = speechRecipeList;
            mAdapter.updateRecipeList(speechRecipeList);
            mAdapter.notifyDataSetChanged();
            mRecipeRv.smoothScrollToPosition(2);
            mSpeechRecipeRl.setVisibility(VISIBLE);
        }
    }

    public String showBrightnessControl(String cmd) {

        int currentBrightness = 0;
        String content = null;
        try {
            currentBrightness = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "get brightness fail: " + e.toString());
            e.printStackTrace();
        }
        float graphProgress = (float) (currentBrightness * 1.0 / 255);
        int textProgress = Math.round((float) (currentBrightness * 1.0 * 100 / 255));
        //        int textProgress = (int) (graphProgress * 100);
        if (TextUtils.equals(cmd, "调亮")) {
            if (textProgress == 100) {
                return "max";
            }
            textProgress = (textProgress + 10) > 100 ? 100 : (textProgress + 10);
            content = "好的，已为您调亮屏幕亮度";
        } else if (TextUtils.equals(cmd, "调暗")) {
            if (textProgress == 0) {
                return "min";
            }
            textProgress = (textProgress - 10) < 0 ? 0 : (textProgress - 10);
            content = "好的，已为您调暗屏幕亮度";
        } else if (TextUtils.equals(cmd, "最亮")) {
            if (textProgress == 100) {
                return "max";
            }
            textProgress = 100;
            content = "好的，屏幕亮度已调到最亮";
        } else if (TextUtils.equals(cmd, "最暗")) {
            if (textProgress == 0) {
                return "min";
            }
            textProgress = 0;
            content = "好的，屏幕亮度已调到最暗";
        }
        graphProgress = (float) (textProgress * 1.0 / 100);
        currentBrightness = Math.round(graphProgress * 255);
        mBrightnessSb.setProgress(textProgress);
        mBrightnessTv.setText(String.valueOf(textProgress));
        AppUtil.setSysLight(mContext, currentBrightness);
        clearFunctionArea();
        mBrightnessSettingLl.setVisibility(VISIBLE);
        //        return String.valueOf(textProgress);
        return content;
    }

    SeekBar.OnSeekBarChangeListener mBrightnessChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    SeekBar.OnSeekBarChangeListener mVolumeChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    ProgressTouchView.onProgressTouchListener mBrightnessProgressTouchListener = new ProgressTouchView.onProgressTouchListener() {
        @Override
        public void onTouchMove(float progress) {
            // 当前进度
            int curProgress = (int) (progress * 255);
            AppUtil.setSysLight(mContext, curProgress);

            mBrightnessTv.setText((int) (progress * 100));

            // TODO: 2019/4/8 是否应该放在onTouchUp里？
            //将亮度百分比保存
            PreferenceUtil.setPreferenceValue(mContext, PreferenceUtil.BRIGHT_LAST, curProgress);
        }

        @Override
        public void onTouchUp(float progress) {

        }
    };

    public String showVolumeControl(int cmd) {
        Log.e(TAG, "showVolumeControl");
        clearFunctionArea();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float progress = 0;
        if (cmd == CommonConst.CMD_REDUCE) {
            progress = (float) (((float) currentVolume * 1.0 - 0.5) / mMaxVolume);
        } else if (cmd == CommonConst.CMD_ADD) {
            progress = (float) (((float) currentVolume * 1.0 + 0.5) / mMaxVolume);
        } else {
            progress = (float) ((float) cmd * 1.0 / mMaxVolume / 10);
        }
        if (progress > 1) {
            return "maximum";
        } else if (progress < 0) {
            return "minimum";
        } else {
            mSoundSettingLl.setVisibility(VISIBLE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (progress * mMaxVolume),
                    0);
            mSoundSb.setProgress((int) progress);
            int showVolume = (int) (progress * mMaxVolume * 10);
            mSoundTv.setText(String.valueOf(showVolume));
            return String.valueOf(showVolume);
        }

    }

    ProgressTouchView.onProgressTouchListener mSoundProgressTouchListener = new ProgressTouchView.onProgressTouchListener() {
        @Override
        public void onTouchMove(float progress) {
            int actuallyVolume = (int) (progress * mMaxVolume);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, actuallyVolume, 0);
            int showVolume = (int) (progress * 100);
            mSoundTv.setText(showVolume);
        }

        @Override
        public void onTouchUp(float progress) {

        }
    };

    private MusicManager.OnAudioStatusChangeListener onAudioStatusChangeListener = new MusicManager.OnAudioStatusChangeListener() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            //            if (mSpeechMusicRl.isShown()) {
            updatePlaybackState(state);
            //            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            //            if (mSpeechMusicRl.isShown()) {
            updateMediaDescription(metadata);
            //            }
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
        }

        @Override
        public void onProgressInit(PlaybackStateCompat state) {
        }
    };

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        Log.e(TAG, "play back state: " + state.getState());
        Log.e(TAG, "last state: " +
                   (mLastPlaybackState == null ? null : mLastPlaybackState.getState()));
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mSpeechMusicAction.setImageResource(R.mipmap.img_btn_pause);
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mSpeechMusicAction.setImageResource(R.mipmap.img_btn_play);
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                if (mLastPlaybackState != null &&
                    (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PAUSED ||
                     mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING)) {
                    Log.e(TAG, "music function gone when stop");
                    mSpeechMusicRl.setVisibility(GONE);
                }
                break;
            default:
                Log.d(TAG, "Unhandled state " + state.getState());
                break;
        }
        mLastPlaybackState = state;
    }

    private void updateMediaDescription(MediaMetadataCompat metadata) {
        Log.e(TAG, "updateMediaDescription");
        if (metadata == null) {
            Log.e(TAG, "metadata is null!!!!");
            return;
        }
        Log.e(TAG, "updateMediaDescription called ");
        mSpeechMusicNameTv.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mSpeechMusicSingerTv.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        Glide.with(mContext).load(
                metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)).asBitmap()
                .skipMemoryCache(true).override(80, 80).transform(new CenterCrop(mContext),
                new GlideRoundTransform(mContext, 8)).into(mSpeechMusicImg);
    }

    //显示设备联动功能模块
    public void showDeviceLinkFunction(String linkDevice, boolean isNeedOpen) {
        if (!isShowing()) {
            show();
        }
        Log.e(TAG, "clear function before show device link function");
        clearFunctionArea();
        mLinkToCookerLl.setVisibility(VISIBLE);
        Log.e(TAG, "show link cooker ll");
        if (TextUtils.equals(linkDevice, "烟灶")) {
            mDeviceLinkTv.setText("烟灶联动");
            mDeviceLinkDescriptionTv.setText(CommonConst.HOOD_COOKER_LINK_DESCRIPTION);
            mDeviceLinkTag = PreferenceUtil.DEVICE_LINK_SMOKE_STOVE;
            //            mLinkSv.setOnCheckedChangeListener(mOnCheckedChangeListener);
            mLinkSv.setOnCheckedChangeListener(mOnCheckedChangeListener);
            mLinkSv.setChecked(isNeedOpen);
        } else if (TextUtils.equals(linkDevice, "烟蒸")) {
            mDeviceLinkTv.setText("烟蒸/蒸微联动");
            mDeviceLinkDescriptionTv.setText(CommonConst.HOOD_STEAM_LINK_DESCRIPTION);
            mDeviceLinkTag = PreferenceUtil.DEVICE_LINK_SMOKE_STEAM;
            mLinkSv.setOnCheckedChangeListener(mOnCheckedChangeListener);
            mLinkSv.setChecked(isNeedOpen);
        } else if (TextUtils.equals(linkDevice, "烟烤")) {
            mDeviceLinkTv.setText("烟烤联动");
            mDeviceLinkDescriptionTv.setText(CommonConst.HOOD_STEAM_LINK_DESCRIPTION);
            mDeviceLinkTag = PreferenceUtil.DEVICE_LINK_SMOKE_ROAST;
            mLinkSv.setOnCheckedChangeListener(mOnCheckedChangeListener);
            mLinkSv.setChecked(isNeedOpen);
        }
    }

    //跳转界面
    private void startActivity(Class<?> activityClass) {
        forceDismiss(false);
        Intent intent = new Intent(mContext.getApplicationContext(), activityClass);
        mContext.startActivity(intent);
    }

    private void startActivity(Intent intent) {
        mContext.startActivity(intent);
    }

    /*SwitchButton.OnCheckedChangeListener mOnCheckedChangeListener = new SwitchButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            if (isChecked) {
                PreferenceUtil.setPreferenceValue(mContext, mDeviceLinkTag, true);
            } else {
                PreferenceUtil.setPreferenceValue(mContext, mDeviceLinkTag, false);
            }
        }
    };*/

    CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                PreferenceUtil.setPreferenceValue(mContext, mDeviceLinkTag, true);
            } else {
                PreferenceUtil.setPreferenceValue(mContext, mDeviceLinkTag, false);
            }
        }
    };

    private void clearDialogArea() {
        Log.e(TAG, "clear dialog area");
        mSpeechQuestionTv.setText(null);
        mSpeechAnswerTv.setText(null);
        clearHelpContent();
    }

    private void clearHelpContent() {
        if (mSpeechHelpLl.isShown()) {
            mSpeechHelpLl.setVisibility(GONE);
        }
    }

    private void clearFunctionArea() {
        Log.e(TAG, "clear function area");
        if (mLinkToCookerLl.isShown()) {
            mLinkToCookerLl.setVisibility(GONE);
        }
        if (mBrightnessSettingLl.isShown()) {
            mBrightnessSettingLl.setVisibility(GONE);
        }
        if (mSoundSettingLl.isShown()) {
            mSoundSettingLl.setVisibility(GONE);
        }
        if (mSpeechRecipeRl.isShown()) {
            mSpeechRecipeRl.setVisibility(GONE);
        }
        if (mSpeechMusicRl.isShown()) {
            mSpeechMusicRl.setVisibility(GONE);
            Log.e(TAG, "clear music function");
        }
        clearHelpContent();
    }

    private boolean isFunctionAreaShown() {
        return mLinkToCookerLl.isShown() || mBrightnessSettingLl.isShown() ||
               mSoundSettingLl.isShown() || mSpeechRecipeRl.isShown() || mSpeechMusicRl.isShown();
    }

    private boolean isHelpContentShown() {
        return mSpeechHelpLl.isShown();
    }
}
