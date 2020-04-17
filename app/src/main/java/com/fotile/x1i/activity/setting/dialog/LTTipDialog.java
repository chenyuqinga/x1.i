package com.fotile.x1i.activity.setting.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;

import butterknife.BindView;

/**
 * 文件名称：LTTipDialog
 * 创建时间：2019/5/13 13:39
 * 文件作者：yaohx
 * 功能描述：乐投提示dialog
 */
public class LTTipDialog extends BaseDialog implements View.OnClickListener {

    @BindView(R.id.txt_confirm)
    TextView txtConfirm;

    public LTTipDialog(Context context) {
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
        return R.layout.dialog_lt_tip;
    }


    private void initView() {
        txtConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //确认
            case R.id.txt_confirm:
                dismiss();
                break;
        }
    }
}
