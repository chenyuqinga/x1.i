package com.fotile.x1i.activity.quicktool;

import android.os.Bundle;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;

import butterknife.BindView;

/**
 * 文件名称： ProjectionScreenActivity
 * 创建时间： 2019/6/13
 * 文件作者： chenyqi
 * 功能描述：
 */


public class ProjectionScreenActivity extends BaseActivity {
    @BindView(R.id.step1)
    TextView tvStep1;
    @BindView(R.id.step2)
    TextView tvStep2;
    @BindView(R.id.step3)
    TextView tvStep3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        tvStep1.setText("第一步" + "\n确保移动端设备和烟机连接同一wifi网络");
        tvStep2.setText("第二步" + "\n点击视频播放客户端的投屏按钮，即可在烟机屏幕观看视频");
        tvStep3.setText("检测不到按钮可能因为设备不支持-可到页面上方重新搜索设备");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_remote_screen;
    }

    @Override
    public boolean showBottom() {
        return true;
    }
}
