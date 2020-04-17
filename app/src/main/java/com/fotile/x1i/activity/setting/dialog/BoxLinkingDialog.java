package com.fotile.x1i.activity.setting.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.voice.CommonConst;
import com.fotile.voice.NetConfigBusiness;
import com.fotile.voice.NetConfigStateChangeListener;
import com.fotile.voice.bean.NluResult;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.WifiListActivity;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.dailog.BoxLinkExceptionDialog;
import com.fotile.x1i.manager.DialogManager;
import com.fotile.x1i.manager.SpeechManager;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.widget.BottomView;
import com.fotile.x1i.widget.RotationLoadingView;
import com.fotile.x1i.widget.TopBar;

import butterknife.BindView;

/**
 * @author chenyqi
 * @date 2019/4/19
 * @company 杭州方太智能科技有限公司
 * @description 语音盒子连接中Dialog
 * <p>
 * Copyright (c) 2018, FOTILE GROUP.
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * - Neither the name of the FOTILE GROUP nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL FOTILE GROUP BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


public class BoxLinkingDialog extends BaseDialog implements View.OnClickListener {

    private static final String TAG = BoxLinkingDialog.class.getSimpleName();

    //    @BindView(R.id.progress_bar)
    //    ProgressBar progressBar;

    @BindView(R.id.rl_box_link)
    RotationLoadingView mLinkingRl;

    @BindView(R.id.tv_link)
    TextView tvLink;

    @BindView(R.id.tv_tips)
    TextView tvTips;

    @BindView(R.id.tv_btn_left)
    TextView tvBtnLeft;

    @BindView(R.id.tv_btn_right)
    TextView tvBtnRight;

    /**
     * 开始连接
     */
    @BindView(R.id.layout_linkage)
    LinearLayout layoutLinkage;

    /**
     * 连接超时
     */
    @BindView(R.id.layout_linkage_time_out)
    LinearLayout layoutLinkageTimeOut;

    /**
     * 取消连接
     */
    @BindView(R.id.tv_link_cancel)
    TextView linkCancelTv;

    private static final int MESSAGE_LINK = 1;
    private static final int MESSAGE_RELINK = 2;
    private static final int MSG_SHOW_WIFI_LIST = 20001;
    private static final int MSG_CONNECT_NET = 20002;
    private static final int MSG_CONNECT_SUCCESS = 20003;
    private static final int MSG_CONNECT_TIMEOUT = 20004;
    private static final int MSG_START_TCP_COUNT_DOWN = 20004;

    private boolean mIsNetConnected;

    public BoxLinkingDialog(@NonNull Context context) {
        super(context, R.style.FullScreenDialog);
        Log.e(TAG, "constructor");
        this.context = context;
    }

    public BoxLinkingDialog(@NonNull Context context, boolean isNetConnected) {
        super(context, R.style.FullScreenDialog);
        Log.e(TAG, "constructor");
        this.context = context;
        mIsNetConnected = isNetConnected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "on create");
        initView();
        initData();
        NetConfigBusiness.getInstance().registerListener(mListener);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_box_linking;
    }

    /**
     * 初始化
     */
    private void initView() {
        //确定
        tvBtnLeft.setOnClickListener(this);
        //重新添加
        tvBtnRight.setOnClickListener(this);
        //        mHandler.sendEmptyMessageDelayed(MESSAGE_LINK, 8 * 1000);
        linkCancelTv.setOnClickListener(this);
        mLinkingRl.startRotationAnimation();
        if (mIsNetConnected) {
            tvTips.setVisibility(View.GONE);
            tvLink.setText("正在连接");
        }
        Log.e(TAG, "init View");
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Log.e(TAG, "init data");

        //        tvRelink.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                handler.sendEmptyMessage(MESSAGE_RELINK);
        //            }
        //        });
        //
        //        tvCancel.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                dismiss();
        //            }
        //        });
        //
        //        linkCancelTv.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                Log.e(TAG, "click cancel");
        //                if (!SpeechManager.getInstance().isConnected()) {
        //                    NetConfigBusiness.getInstance().restoreState(true);
        //                }
        //                dismiss();
        //            }
        //        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //确定
            case R.id.tv_btn_left:
                dismiss();
                break;
            //重新添加
            case R.id.tv_btn_right:
                mHandler.sendEmptyMessage(MESSAGE_RELINK);
                break;
            //连接中取消
            case R.id.tv_link_cancel:
                Log.e(TAG, "click cancel");
                if (!SpeechManager.getInstance().isConnected()) {
                    NetConfigBusiness.getInstance().restoreState(true);
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * 更新UI
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_LINK:
                    Log.e(TAG, "link box overtime");
                    //                    showLinkFailure();
                    break;

                case MESSAGE_RELINK:
                    showRelinkView();
                    break;
                case MSG_SHOW_WIFI_LIST:
                    dismiss();
                    context.startActivity(new Intent(context, WifiListActivity.class));
                    break;
                case MSG_CONNECT_NET:
                    //连上网络，更新界面
                    tvTips.setVisibility(View.GONE);
                    tvLink.setText("正在连接");
                    mHandler.sendEmptyMessageDelayed(MSG_CONNECT_TIMEOUT, 70000);
                    break;
                case MSG_CONNECT_SUCCESS:
                    DialogManager.getInstance().dismissExceptionDialog();
                    dismiss();
                    Log.e(TAG, "dialog dismiss");
                    break;
                case MSG_CONNECT_TIMEOUT:
                    Log.e(TAG, "link time out");
                    NetConfigBusiness.getInstance().restoreState(true);
                    dismiss();
                    DialogManager.getInstance().showExceptionDialog(context,
                            context.getString(R.string.str_box_add_time_out_title),
                            context.getString(R.string.str_box_udp_time_out),
                            PreferenceUtil.isInGuide(context) ? null : "确定", true, "重新添加", false,
                            PreferenceUtil.isInGuide(context),
                            new BoxLinkExceptionDialog.BtnClickListener() {
                                @Override
                                public void onTopBtnClick() {
                                }

                                @Override
                                public void onBottomBtnClick() {
                                    BottomView.getInstance(context).hide();
                                    SpeechManager.getInstance().bindSpeech();
                                    BoxLinkPreDialog boxDialog = new BoxLinkPreDialog(context);
                                    boxDialog.show();
                                }
                            });
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    /**
     * 点击 重新连接
     */
    private void showRelinkView() {
        setViewStatus();
        tvTips.setVisibility(View.GONE);
    }

    private void setViewStatus() {
        layoutLinkageTimeOut.setVisibility(View.GONE);
        layoutLinkage.setVisibility(View.VISIBLE);
    }

    /**
     * 连接超时，页面显示
     */
    private void showLinkFailure() {
        layoutLinkage.setVisibility(View.GONE);
        layoutLinkageTimeOut.setVisibility(View.VISIBLE);
    }

    private NetConfigStateChangeListener mListener = new NetConfigStateChangeListener() {
        @Override
        public void onNetConfigStateChange(CommonConst.ConfigState state) {
            if (state == CommonConst.ConfigState.CONNECT_ACTUALLY_NET) {
                mHandler.sendEmptyMessage(MSG_CONNECT_NET);
            } else if (state == CommonConst.ConfigState.CONNECT_BOX_SUCCESS) {
                mHandler.sendEmptyMessage(MSG_CONNECT_SUCCESS);
                //            } else if (state == CommonConst.ConfigState.SOCKET_CONNECT_TIME_OUT) {
            } else if (state == CommonConst.ConfigState.START_TCP) {
                mHandler.sendEmptyMessageDelayed(MSG_START_TCP_COUNT_DOWN, 100000);
            }
        }

        @Override
        public void onNetConfigException(CommonConst.ConfigError errorType) {
            switch (errorType) {
                case AP_TIME_OUT://ap创建失败
                    Log.e(TAG, "AP_TIME_OUT");
                    DialogManager.getInstance().showExceptionDialog(
                            AppUtil.getCurrentActivity(context), "添加失败",
                            context.getString(R.string.str_box_added_failure), "确定", true, null,
                            false, PreferenceUtil.isInGuide(context),
                            new BoxLinkExceptionDialog.BtnClickListener() {
                                @Override
                                public void onTopBtnClick() {
                                    NetConfigBusiness.getInstance().restoreState(true);
                                }

                                @Override
                                public void onBottomBtnClick() {
                                }
                            });
                    //                    dismiss();
                    break;
                case CONNECT_WIFI_FAILED://重连wifi失败
                    Log.e(TAG, "CONNECT_WIFI_FAILED");
                    dismiss();
                    DialogManager.getInstance().showExceptionDialog(
                            AppUtil.getCurrentActivity(context),
                            context.getString(R.string.str_box_add_time_out_title),
                            context.getString(R.string.str_box_wifi_timeout),
                            PreferenceUtil.isInGuide(context) ? null : "去检查", true, "确定", false,
                            PreferenceUtil.isInGuide(context),
                            new BoxLinkExceptionDialog.BtnClickListener() {
                                @Override
                                public void onTopBtnClick() {
                                    NetConfigBusiness.getInstance().restoreState(false);
                                    context.startActivity(
                                            new Intent(context, WifiListActivity.class));
                                }

                                @Override
                                public void onBottomBtnClick() {
                                    NetConfigBusiness.getInstance().restoreState(false);
                                }
                            });
                    break;
                case ILLEGAL_HOME_ID://homeId不匹配
                    Log.e(TAG, "home id illegal");
                    dismiss();
                    DialogManager.getInstance().showExceptionDialog(context,
                            context.getString(R.string.str_box_add_fail_title),
                            context.getString(R.string.str_box_home_id_illegal), null, false, "确定",
                            true, PreferenceUtil.isInGuide(context),
                            new BoxLinkExceptionDialog.BtnClickListener() {
                                @Override
                                public void onTopBtnClick() {

                                }

                                @Override
                                public void onBottomBtnClick() {
                                    NetConfigBusiness.getInstance().restoreState(false);
                                }
                            });
                    break;
                case CONNECT_BOX_FAILED://盒子拒绝||tcp断连
                    Log.e(TAG, "CONNECT_BOX_FAILED");

                    break;
                case UDP_RECEIVE_TIMEOUT://公共ap||二次联网 接收udp失败
                    Log.e(TAG, "udp receive timeout");
                    DialogManager.getInstance().showExceptionDialog(context,
                            context.getString(R.string.str_box_add_time_out_title),
                            context.getString(R.string.str_box_udp_time_out),
                            PreferenceUtil.isInGuide(context) ? null : "确定", true, "重新添加", false,
                            PreferenceUtil.isInGuide(context),
                            new BoxLinkExceptionDialog.BtnClickListener() {
                                @Override
                                public void onTopBtnClick() {
                                }

                                @Override
                                public void onBottomBtnClick() {
                                    SpeechManager.getInstance().bindSpeech();
                                    BottomView.getInstance(context).hide();
                                    new BoxLinkPreDialog(context).show();
                                }
                            });
                    dismiss();
                    break;
                case BOX_CONNECTING:
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onDialogStateChange(CommonConst.DialogState state) {

        }

        @Override
        public void onHeard(int type, String asrResult) {

        }

        @Override
        public void onSpeak(String content) {

        }

        @Override
        public void onUnderStand(NluResult nluResult) {

        }

        @Override
        public void onWakeUp(String content) {

        }

        @Override
        public void onMusicStart(String musicList) {

        }

        @Override
        public void onMusicStop() {

        }

        @Override
        public void onRecipesGet(String recipes) {

        }

        @Override
        public void onVboxError(String error) {

        }
    };

    @Override
    public void dismiss() {
        super.dismiss();
        Log.e(TAG, "dismiss");
        mLinkingRl.stopRotationAnimation();
        NetConfigBusiness.getInstance().unRegisterListener(mListener);
        ScreenTool.getInstance().addResetData("link_dialog_dismiss");
        TopBar.getInstance(context).show();
    }

    @Override
    public void dismissOutBottom() {
        super.dismissOutBottom();
        NetConfigBusiness.getInstance().unRegisterListener(mListener);
        ScreenTool.getInstance().addResetData("link_dialog_dismiss");
        TopBar.getInstance(context).show();
    }

    @Override
    public void show() {
        super.show();
        ScreenTool.getInstance().addPause();
        TopBar.getInstance(context).hide();
    }
}
