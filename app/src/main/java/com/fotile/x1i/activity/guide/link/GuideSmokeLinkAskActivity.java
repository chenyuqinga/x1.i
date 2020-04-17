package com.fotile.x1i.activity.guide.link;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.WifiListActivity;
import com.fotile.x1i.base.BaseActivity;

import butterknife.BindView;


/**
 * 文件名称：GuideSmokeLinkAskActivity
 * 创建时间：2019/5/13 14:51
 * 文件作者：yaohx
 * 功能描述：烟灶联动--灶具询问
 */
public class GuideSmokeLinkAskActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_no)
    TextView txtNo;

    @BindView(R.id.tv_yes)
    TextView txtYes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        txtNo.setOnClickListener(this);
        txtYes.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide_stove_ask;
    }

    @Override
    public boolean showBottom() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击否，跳转到wifi设置
            case R.id.tv_no:
                Intent intent = new Intent(this,WifiListActivity.class);
                intent.putExtra("guide",true);
                launchActivity(intent);
                break;
            //点击是，跳转到灶具列表页
            case R.id.tv_yes:
                launchActivity(GuideSmokeLinkListActivity.class);
                break;
        }
    }
}
