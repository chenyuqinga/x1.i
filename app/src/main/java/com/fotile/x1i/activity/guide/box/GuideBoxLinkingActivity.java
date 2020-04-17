package com.fotile.x1i.activity.guide.box;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.voice.CommonConst;
import com.fotile.voice.NetConfigBusiness;
import com.fotile.voice.NetConfigStateChangeListener;
import com.fotile.voice.bean.NluResult;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.DeviceBindActivity;
import com.fotile.x1i.activity.setting.WifiListActivity;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.dailog.BoxLinkExceptionDialog;
import com.fotile.x1i.manager.DialogManager;
import com.fotile.x1i.manager.SpeechManager;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.widget.RotationLoadingView;

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


public class GuideBoxLinkingActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = GuideBoxLinkingActivity.class.getSimpleName();

    //    @BindView(R.id.progress_bar)
    //    ProgressBar progressBar;

    @BindView(R.id.tv_link)
    TextView tvLink;

    /**
     * 连接失败标题
     */
    @BindView(R.id.txt_fault_ttile)
    TextView txtFaultTitle;
    /**
     * 连接失败message
     */
    @BindView(R.id.txt_fault_message)
    TextView txtFaultMessage;

    @BindView(R.id.tv_btn_left)
    TextView tvBtnLeft;

    @BindView(R.id.tv_btn_right)
    TextView tvBtnRight;
    /**
     * 标题
     */
    @BindView(R.id.txt_guide_title)
    TextView txtGuideTitle;

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

    @BindView(R.id.tv_tips)
    TextView tvTips;

    /**
     * 取消连接
     */
    @BindView(R.id.tv_link_cancel)
    TextView linkCancelTv;

    @BindView(R.id.rl_box_link)
    RotationLoadingView mLinkingRl;

    @BindView(R.id.iv_guid_link_success)
    ImageView mGuideLinkSuccessIv;

    private static final int MESSAGE_LINK = 1;
    private static final int MESSAGE_NEXT = 20005;
    private static final int MESSAGE_RELINK = 20004;
    private static final int MSG_SHOW_WIFI_LIST = 20001;
    private static final int MSG_CONNECT_NET = 20002;
    private static final int MSG_CONNECT_SUCCESS = 20003;


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_LINK:
                    Log.e(TAG, "link box overtime");
                    //                    showLinkFailure();
                    break;

                case MESSAGE_RELINK:
                    context.startActivity(new Intent(context, GuideBoxLinkPreActivity.class));
                    break;
                case MESSAGE_NEXT:
                    Intent intent = new Intent(context, DeviceBindActivity.class);
                    intent.putExtra("guide", true);
                    launchActivity(intent);
                    break;
                case MSG_SHOW_WIFI_LIST:
                    Intent intent2 = new Intent(context, WifiListActivity.class);
                    intent2.putExtra("guide", true);
                    launchActivity(intent2);
                    break;
                case MSG_CONNECT_NET:
                    //连上网络，更新界面
                    tvTips.setVisibility(View.GONE);
                    tvLink.setText("正在连接");
                    break;
                case MSG_CONNECT_SUCCESS:
                    DialogManager.getInstance().dismissExceptionDialog();
                    tvLink.setText("添加成功");
                    mLinkingRl.setVisibility(View.GONE);
                    tvTips.setVisibility(View.GONE);
                    mGuideLinkSuccessIv.setVisibility(View.VISIBLE);
                    linkCancelTv.setText("下一步");
                    linkCancelTv.setOnClickListener(GuideBoxLinkingActivity.this);
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_box_linking;
    }

    /**
     * 初始化
     */
    private void initView() {
        txtGuideTitle.setVisibility(View.VISIBLE);
        //确定
        tvBtnLeft.setOnClickListener(this);
        //重新添加
        tvBtnRight.setOnClickListener(this);
        //        handler.sendEmptyMessageDelayed(MESSAGE_LINK, 5 * 1000);

        tvBtnLeft.setText("跳过");
        tvBtnRight.setText("重新添加");
        linkCancelTv.setOnClickListener(this);
        mLinkingRl.startRotationAnimation();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Log.e(TAG, "init data");
        NetConfigBusiness.getInstance().registerListener(mListener);
        if (!SpeechManager.getInstance().bindSpeech()) {
            DialogManager.getInstance().showExceptionDialog(AppUtil.getCurrentActivity(context),
                    context.getString(R.string.str_box_add_fail_title),
                    context.getString(R.string.str_box_wifi_timeout), "去检查", true, "下一步", false,
                    PreferenceUtil.isInGuide(context),
                    new BoxLinkExceptionDialog.BtnClickListener() {
                        @Override
                        public void onTopBtnClick() {
                            NetConfigBusiness.getInstance().restoreState(false);
                            mHandler.sendEmptyMessage(MSG_SHOW_WIFI_LIST);
                        }

                        @Override
                        public void onBottomBtnClick() {
                            NetConfigBusiness.getInstance().restoreState(false);
                            mHandler.sendEmptyMessage(MESSAGE_NEXT);
                        }
                    });
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //连接失败--跳过
            //点击跳过--进入设备绑定
            case R.id.tv_btn_left:
                Intent intent = new Intent(context, DeviceBindActivity.class);
                intent.putExtra("guide", true);
                launchActivity(intent);
                break;
            //连接失败--重新添加
            case R.id.tv_btn_right:

                break;
            //连接中取消
            case R.id.tv_link_cancel:
                Log.e(TAG, "click cancel");
                if (!SpeechManager.getInstance().isConnected()) {
                    NetConfigBusiness.getInstance().restoreState(true);
                }
                mHandler.sendEmptyMessage(MESSAGE_NEXT);
                break;
        }
    }

    @Override
    public boolean showBottom() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetConfigBusiness.getInstance().unRegisterListener(mListener);
        mLinkingRl.stopRotationAnimation();
    }

    private NetConfigStateChangeListener mListener = new NetConfigStateChangeListener() {
        @Override
        public void onNetConfigStateChange(CommonConst.ConfigState state) {
            if (state == CommonConst.ConfigState.CONNECT_ACTUALLY_NET) {
                mHandler.sendEmptyMessage(MSG_CONNECT_NET);
            } else if (state == CommonConst.ConfigState.CONNECT_BOX_SUCCESS) {
                mHandler.sendEmptyMessage(MSG_CONNECT_SUCCESS);
                //            } else if (state == CommonConst.ConfigState.SOCKET_CONNECT_TIME_OUT) {

            }
        }

        @Override
        public void onNetConfigException(CommonConst.ConfigError errorType) {
            switch (errorType) {
                case AP_TIME_OUT://ap创建失败
                    Log.e(TAG, "AP_TIME_OUT");
                    mLinkingRl.stopRotationAnimation();
                    DialogManager.getInstance().showExceptionDialog(
                            AppUtil.getCurrentActivity(context), "添加失败",
                            context.getString(R.string.str_box_added_failure), "下一步", true, "",
                            true, PreferenceUtil.isInGuide(context),
                            new BoxLinkExceptionDialog.BtnClickListener() {
                                @Override
                                public void onTopBtnClick() {
                                    NetConfigBusiness.getInstance().restoreState(true);
                                    mHandler.sendEmptyMessage(MESSAGE_NEXT);
                                }

                                @Override
                                public void onBottomBtnClick() {
                                }
                            });
                    //                    dismiss();
                    break;
                case CONNECT_WIFI_FAILED://重连wifi失败
                    Log.e(TAG, "CONNECT_WIFI_FAILED");
                    mLinkingRl.stopRotationAnimation();
                    DialogManager.getInstance().showExceptionDialog(
                            AppUtil.getCurrentActivity(context),
                            context.getString(R.string.str_box_add_time_out_title),
                            context.getString(R.string.str_box_wifi_timeout),
                            PreferenceUtil.isInGuide(context) ? null : "去检查", true, "下一步", false,
                            PreferenceUtil.isInGuide(context),
                            new BoxLinkExceptionDialog.BtnClickListener() {
                                @Override
                                public void onTopBtnClick() {
                                    NetConfigBusiness.getInstance().restoreState(false);
                                    mHandler.sendEmptyMessage(MSG_SHOW_WIFI_LIST);
                                }

                                @Override
                                public void onBottomBtnClick() {
                                    NetConfigBusiness.getInstance().restoreState(false);
                                    mHandler.sendEmptyMessage(MESSAGE_NEXT);
                                }
                            });
                    break;
                case ILLEGAL_HOME_ID://homeId不匹配
                    Log.e(TAG, "home id illegal");
                    mLinkingRl.stopRotationAnimation();
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
                    mLinkingRl.stopRotationAnimation();
                    break;
                case UDP_RECEIVE_TIMEOUT://公共ap||二次联网 接收udp失败
                    mLinkingRl.stopRotationAnimation();
                    Log.e(TAG, "udp receive timeout");
                    DialogManager.getInstance().showExceptionDialog(context,
                            context.getString(R.string.str_box_add_time_out_title),
                            context.getString(R.string.str_box_udp_time_out), "重新添加", true, "下一步",
                            false, PreferenceUtil.isInGuide(context),
                            new BoxLinkExceptionDialog.BtnClickListener() {
                                @Override
                                public void onTopBtnClick() {
                                    NetConfigBusiness.getInstance().restoreState(true);
                                    mHandler.sendEmptyMessage(MESSAGE_RELINK);
                                }

                                @Override
                                public void onBottomBtnClick() {
                                    NetConfigBusiness.getInstance().restoreState(true);
                                    mHandler.sendEmptyMessage(MESSAGE_NEXT);
                                }
                            });
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


}
