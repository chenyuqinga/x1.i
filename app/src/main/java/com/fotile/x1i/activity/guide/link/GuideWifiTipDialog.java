package com.fotile.x1i.activity.guide.link;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.DeviceBindActivity;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.base.BaseDialog;

import butterknife.BindView;

/**
 * 文件名称：GuideWifiTipDialog
 * 创建时间：2019/4/29 17:43
 * 文件作者：yaohx
 * 功能描述：提示用户未联网dialog
 */
public class GuideWifiTipDialog extends BaseDialog implements View.OnClickListener {
    @BindView(R.id.txt_jump)
    TextView txtJump;
    @BindView(R.id.txt_set)
    TextView txtSet;

    public GuideWifiTipDialog(Context context) {
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
        return R.layout.dialog_wifi_tip;
    }


    private void initView() {
        txtSet.setOnClickListener(this);
        txtJump.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击跳过去设置语音盒子
            case R.id.txt_jump:
                if(context instanceof BaseActivity){
                    BaseActivity activity = (BaseActivity) context;
//                    activity.launchActivity(GuideBoxLinkPreActivity.class);
                    Intent intent = new Intent(context, DeviceBindActivity.class);
                    intent.putExtra("guide", true);
                    activity.launchActivity(intent);
                }
                dismiss();
                break;
            //点击设置回到wifi列表设置页面
            case R.id.txt_set:
                dismiss();
                break;
        }
    }
}
