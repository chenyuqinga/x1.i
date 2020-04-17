package com.fotile.x1i.activity.setting.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;

import butterknife.BindView;

/**
 * @author chenyqi
 * @date 2019/4/18
 * @company 杭州方太智能科技有限公司
 * @description 是否断开烟灶联动/盒子联动Dialog提示
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


public class IgnoreLinkageDialog extends BaseDialog {
    @BindView(R.id.tv_tips1)
    TextView tvTips1;

    @BindView(R.id.tv_tips2)
    TextView tvTips2;

    @BindView(R.id.tv_btn_left)
    TextView tvBtnLeft;

    @BindView(R.id.tv_btn_right)
    TextView tvBtnRight;

    private OnClickListener onClickListener;

    public IgnoreLinkageDialog(@NonNull Context context) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_dialog_close_wifi;
    }

    /**
     * 初始化
     */
    private void initView() {

        tvBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onNegative(IgnoreLinkageDialog.this);
            }
        });

        tvBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onPositive(IgnoreLinkageDialog.this);
            }
        });

    }

    public interface OnClickListener {
        /**
         * 确定
         *
         * @param dialog
         */
        void onPositive(IgnoreLinkageDialog dialog);

        /**
         * 取消
         *
         * @param dialog
         */
        void onNegative(IgnoreLinkageDialog dialog);

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void closeSmokeStove(){
        tvTips1.setText("断开连接");
        tvTips1.setText("是否断开烟灶联动");
        tvBtnLeft.setText("取消");
        tvBtnRight.setText("断开连接");


    }

    public void addOverTimeText(){
        tvTips1.setText("添加超时");
        tvTips1.setText("连接盒子超时，请在50s内完成盒子上电操作");
        tvBtnLeft.setText("确定");
        tvBtnRight.setText("重新添加");

    }
}
