package com.fotile.x1i.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.dialog.LTTipDialog;
import com.fotile.x1i.base.BaseActivity;

import butterknife.BindView;
/**
  * 文件名称：LTActivity
  * 创建时间：2019/5/13 11:42
  * 文件作者：yaohx
  * 功能描述：投屏
  */
public class LTActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.txt_lt_help)
    TextView txtLtHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        txtLtHelp.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_lt;
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_lt_help:
                LTTipDialog dialog = new LTTipDialog(context);
                dialog.show();
                break;
        }
    }
}
