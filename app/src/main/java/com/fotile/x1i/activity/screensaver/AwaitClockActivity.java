package com.fotile.x1i.activity.screensaver;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;


import com.fotile.common.util.PreferenceUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.widget.MyQAnalogClock;

import org.greenrobot.eventbus.EventBus;

/**
 * 文件名称：AwaitClockActivity
 * 创建时间：2019/3/12 17:15
 * 文件作者：chenyqi
 * 功能描述：
 */
public class AwaitClockActivity extends BaseClockActivity {
    private LinearLayout awaitClockLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_await_clock);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View
                .SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE);
        int awaitClockStyle = PreferenceUtil.getScreenSaverType(this);
        MyQAnalogClock view = new MyQAnalogClock(this, awaitClockStyle);
        awaitClockLinearLayout = (LinearLayout) findViewById(R.id.await_clock);
        awaitClockLinearLayout.addView(view);
        awaitClockLinearLayout.setOnClickListener(this);

    }

    public int getLayoutId() {
        return R.layout.layout_await_clock;
    }

}
