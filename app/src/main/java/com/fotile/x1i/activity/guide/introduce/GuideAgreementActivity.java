package com.fotile.x1i.activity.guide.introduce;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fotile.common.util.Tool;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;


import butterknife.BindView;

/**
 * 文件名称：GuideAgreementActivity
 * 创建时间：2019/5/14 19:44
 * 文件作者：yaohx
 * 功能描述：用户协议
 */
public class GuideAgreementActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.txt_next)
    TextView txtNext;
    /**
     * 勾选协议
     */
    @BindView(R.id.check_box)
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Resources resources = (Resources) context.getResources();
                ColorStateList colorStateList = (ColorStateList) resources.getColorStateList(R.color.list_color_state);
                if (!isChecked) {
                    txtNext.setEnabled(false);
                    if (colorStateList != null) {
                        txtNext.setTextColor(colorStateList);
                    }
                    txtNext.setBackgroundResource(R.drawable.shape_button_normal);
                } else {
                    txtNext.setEnabled(true);
                    if (colorStateList != null) {
                        txtNext.setTextColor(colorStateList);
                    }
                    txtNext.setBackgroundResource(R.drawable.shape_button_select);
                    Toast.makeText(context, "请阅读并同意《方太用户使用协议》", Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtNext.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide_agreement;
    }

    @Override
    public boolean showBottom() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击下一步进入功能简介
            case R.id.txt_next:
                if (checkBox.isChecked()) {
                    launchActivity(GuideIntroduceActivity.class);
                }
                break;
        }
    }
}
