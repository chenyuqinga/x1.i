package com.fotile.x1i.activity.guide;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ebanswers.sdk.util.sleep.ScreenOffManager;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.MainActivity;
import com.fotile.x1i.activity.guide.link.GuideSmokeLinkAskActivity;
import com.fotile.x1i.base.BaseActivity;

import butterknife.BindView;

public class GuideWelcomeActiivty extends BaseActivity implements View.OnClickListener {
    /**
     * 开始设置
     */
    @BindView(R.id.tv_start_setting)
    TextView tvStartSetting;
    /**
     * 厂测专用
     */
    @BindView(R.id.img_factory_test)
    ImageView imgFactoryTest;
    /**
     * 点击次数
     */
    final static int COUNTS = 4;
    /**
     * 规定有效时间
     */
    final static long DURATION = 4 * 1000;
    long[] serialHits = new long[COUNTS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvStartSetting.setOnClickListener(this);
        imgFactoryTest.setOnClickListener(this);

        //在此处激活设备
        ScreenOffManager screenOffManager = new ScreenOffManager();
        screenOffManager.sleep(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide_welcome;
    }

    @Override
    public boolean showBottom() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_factory_test:
                System.arraycopy(serialHits, 1, serialHits, 0, serialHits.length - 1);
                //实现左移，然后最后一个位置更新距离开机的时间，如果最后一个时间和最开始时间小于DURATION，即连续5次点击
                serialHits[serialHits.length - 1] = SystemClock.uptimeMillis();
                if (serialHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    launchActivity(MainActivity.class);
                }
                break;
            //开始设置
            case R.id.tv_start_setting:
                launchActivity(GuideSmokeLinkAskActivity.class);
                break;
        }
    }
}
