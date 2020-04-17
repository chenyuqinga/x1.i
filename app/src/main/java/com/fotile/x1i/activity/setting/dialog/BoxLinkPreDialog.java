package com.fotile.x1i.activity.setting.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fotile.voice.NetConfigBusiness;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.bean.event.BoxLinkageMissEvent;
import com.fotile.x1i.bean.event.BoxLinkingStep;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.widget.TopBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * @author chenyqi
 * @date 2019/4/19
 * @company 杭州方太智能科技有限公司
 * @description 语音盒子连接Dialog
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


public class BoxLinkPreDialog extends BaseDialog implements View.OnClickListener {
    @BindView(R.id.check_box)
    CheckBox checkBox;

    @BindView(R.id.tv_tips)
    TextView tvTips;

    @BindView(R.id.tv_join)
    TextView tvJoin;

    @BindView(R.id.tv_exit)
    TextView tvExit;

    @BindView(R.id.tv_box_notice)
    TextView mBoxNoticeTv;

    @BindView(R.id.ll_box_check_box)
    LinearLayout mBoxCheckBoxLl;
    private BoxLinkingDialog mLinkingDialog;

    public BoxLinkPreDialog(@NonNull Context context) {
        super(context, R.style.FullScreenDialog);
        Log.e("coiliaspp", "dialog construct ");
        this.context = context;
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_box_link_pre;
    }

    /**
     * 初始化
     */
    private void initView() {
        tvExit.setText("取消");
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    tvJoin.setTextColor(Color.parseColor("#FF666666"));
                    tvJoin.setBackgroundResource(R.drawable.shape_button_normal);
                } else {
                    tvJoin.setTextColor(Color.parseColor("#FFFFFFFF"));
                    tvJoin.setBackgroundResource(R.drawable.shape_button_select);
                }
            }
        });

        mBoxCheckBoxLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        });

        tvJoin.setOnClickListener(this);
        tvExit.setOnClickListener(this);
        SpannableString spannableString = new SpannableString(
                getContext().getString(R.string.str_box_connect_note_line2));
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#C8AF70"));
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(Color.parseColor("#C8AF70"));
        spannableString.setSpan(colorSpan1, 0, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(colorSpan2, 21, spannableString.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mBoxNoticeTv.setText(spannableString);
    }

    /**
     * 显示连接dialog
     */
    private void showLinkDialog(Context context, boolean isNetConnect) {
        if (mLinkingDialog != null && mLinkingDialog.isShowing()) {
            return;
        }
        //点击下一步，开始udp倒计时
        NetConfigBusiness.getInstance().startUdpTimeOutCountdown();
        dismissOutBottom();
        mLinkingDialog = new BoxLinkingDialog(context, isNetConnect);
        mLinkingDialog.show();
        dismissOutBottom();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNextStep(BoxLinkingStep step) {
        Log.e("NetConfigBusiness", "on next step: " + step);
        if (step != null) {
            dismiss();
            showLinkDialog(context, true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //加入
            case R.id.tv_join:
                if (!checkBox.isChecked()) {
                    Toast.makeText(context, "请先勾选盒子指示灯提示", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("dydy", "show linking dialog");
                    Toast.makeText(context, "连接盒子", Toast.LENGTH_SHORT).show();
                    dismiss();
                    showLinkDialog(context, false);
                }
                break;
            //退出
            case R.id.tv_exit:
                dismiss();
                NetConfigBusiness.getInstance().restoreState(true);
                break;
        }
    }

    @Override
    public void show() {
        super.show();
        TopBar.getInstance(context).hide();
        ScreenTool.getInstance().addPause();
    }

    @Override
    public void dismissOutBottom() {
        TopBar.getInstance(context).show();
        super.dismissOutBottom();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        EventBus.getDefault().post(new BoxLinkageMissEvent());
        TopBar.getInstance(context).show();
        ScreenTool.getInstance().addResetData("exit box link pre");
        EventBus.getDefault().unregister(this);
    }
}
