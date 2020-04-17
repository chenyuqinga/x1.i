package com.fotile.x1i.activity.guide.introduce;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;

import butterknife.BindView;

/**
 * 文件名称：GuideIntroduceActivity
 * 创建时间：2019/5/15 15:16
 * 文件作者：yaohx
 * 功能描述：功能简介
 */
public class GuideIntroduceActivity extends BaseActivity {

    /**
     * 查看
     */
    @BindView(R.id.tv_start_setting)
    TextView tvStartSetting;

   @BindView(R.id.tv_start_skip)
    TextView tvStartSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvStartSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(GuideIntroducePageActivity.class);
            }
        });
        tvStartSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch2Home();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide_introduce;
    }

//    @Override
//    public boolean showBottom() {
//        return false;
//    }


    @Override
    public boolean showBottom() {
        return false;
    }

    @Override
    public boolean showTopBar() {
        return false;
    }
}
