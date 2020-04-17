package com.fotile.x1i.activity.guide.box;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.DeviceBindActivity;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.manager.SpeechManager;

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


public class GuideBoxLinkPreActivity extends BaseActivity implements OnClickListener {
    @BindView(R.id.check_box)
    CheckBox checkBox;

    @BindView(R.id.tv_tips)
    TextView tvTips;

    @BindView(R.id.tv_join)
    TextView tvJoin;

    @BindView(R.id.tv_exit)
    TextView tvExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        SpeechManager.getInstance().registerApplication(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_box_link_pre;
    }

    @Override
    public boolean showBottom() {
        return false;
    }

    /**
     * 初始化
     */
    private void initView() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Resources resources = (Resources) context.getResources();
                ColorStateList colorStateList = (ColorStateList) resources.getColorStateList(R.color.list_color_state);
                if (!isChecked) {
                    Toast.makeText(context, "请先勾选语音助手指示灯提示", Toast.LENGTH_SHORT).show();
                    tvJoin.setEnabled(false);
                    if (colorStateList != null) {
                        tvJoin.setTextColor(colorStateList);
                    }
                    tvJoin.setBackgroundResource(R.drawable.shape_button_normal);
                } else {
                    tvJoin.setEnabled(true);
                    if (colorStateList != null) {
                        tvJoin.setTextColor(colorStateList);
                    }
                    tvJoin.setBackgroundResource(R.drawable.shape_button_select);
                }
            }
        });

        tvJoin.setOnClickListener(this);
        tvExit.setOnClickListener(this);

        tvExit.setText("跳过");
        tvJoin.setText("下一步");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //下一步--进入盒子连接
            case R.id.tv_join:
                if (checkBox.isChecked()) {
                    launchActivity(GuideBoxLinkingActivity.class);
                    Toast.makeText(context, "连接语音助手", Toast.LENGTH_SHORT).show();
                }
                break;
            //点击跳过--进入设备绑定
            case R.id.tv_exit:
                Intent intent = new Intent(context, DeviceBindActivity.class);
                intent.putExtra("guide", true);
                launchActivity(intent);
                break;
        }
    }
}
