package com.fotile.x1i.activity.guide;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.MainActivity;
import com.fotile.x1i.base.BaseActivity;

/**
 * 文件名称：GuideLoadingActivity
 * 创建时间：2019/5/13 14:16
 * 文件作者：yaohx
 * 功能描述：开机页
 */
public class GuideLoadingActivity extends BaseActivity {

    private static final int MESSAGE_DELAY_JUMP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        //延时跳转
        handler.sendEmptyMessageDelayed(MESSAGE_DELAY_JUMP, 4000);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //延时跳转
                case MESSAGE_DELAY_JUMP:
                    boolean first = (boolean) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.FIRST_ENTER_APP, true);
                    if (first) {
                        launchActivity(GuideWelcomeActiivty.class);
                    } else {
                        launchActivity(MainActivity.class);
                    }
                    finish();
                    break;
            }
            return false;
        }
    });

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide_loading;
    }

    @Override
    public boolean showBottom() {
        return false;
    }
}
