package com.fotile.x1i.activity.setting;


import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.dialog.DelayTimeSelectDialog;
import com.fotile.x1i.base.BaseActivity;

import butterknife.BindView;


/**
 * @author chenyqi
 * @date 2019/4/23
 * @company 杭州方太智能科技有限公司
 * @description 延时关机页面
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
public class DelayOffActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_switch)
    Switch btnSwitch;

    @BindView(R.id.tv_delay_time)
    TextView tvDelayTime;

    @BindView(R.id.layout_delay_time)
    LinearLayout layoutDelayTime;

    @BindView(R.id.layout_delay)
    LinearLayout layoutDelay;

    /**
     * 计时器弹框
     */
    DelayTimeSelectDialog timeSelectorDialog;

    /**
     * 标识延时开关是否打开
     */
    boolean delayShutDownFlag = false;

    /**
     * 是否正在计时
     */
    private boolean isTick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    /**
     * 初始化view
     */
    private void initView() {
        layoutDelayTime.setOnClickListener(this);
        //上次开关状态
        boolean delayShutDownState = PreferenceUtil.getDelayShutSwitch(context);
        delayShutDownFlag = delayShutDownState;

        //如果延时关机上一次状态时打开的，显示上次开关状态和时间数值
        if (delayShutDownState) {
            PreferenceUtil.setDelayShutSwitch(context, true);
            layoutDelay.setVisibility(View.VISIBLE);
            btnSwitch.setChecked(delayShutDownState);
            String number = String.valueOf(PreferenceUtil.getDelayTime(context));
            tvDelayTime.setText(number + "分钟");

        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layoutDelay.setVisibility(View.VISIBLE);
                    delayShutDownFlag = true;
                    PreferenceUtil.setDelayShutSwitch(context, true);
                    String number = String.valueOf(PreferenceUtil.getDelayTime(context));
                    tvDelayTime.setText(number + "分钟");
                } else {
                    layoutDelay.setVisibility(View.GONE);
                    delayShutDownFlag = false;
                    PreferenceUtil.setDelayShutSwitch(context, false);
                }
            }
        });
    }
    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_delay_off;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //点击修改时间
            case R.id.layout_delay_time:
                timeSelectorDialog = new DelayTimeSelectDialog(context);
                timeSelectorDialog.callBack(new DelayTimeSelectDialog.BtnCallBack() {
                    @Override
                    public void onPositive(DelayTimeSelectDialog dialog, int number) {
                        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.DELAY_SHUTDOWN_TIME, number);
                        tvDelayTime.setText(number + "分钟");
                    }

                    @Override
                    public void onNegative(DelayTimeSelectDialog dialog) {

                    }
                });
                timeSelectorDialog.show();
                break;
        }
    }
}
