package com.fotile.x1i.activity.music;

import android.os.Bundle;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.bean.event.FinishActivityMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * 文件名称：BaseMusicActivity
 * 创建时间：2017/8/7 13:39
 * 文件作者：yaohx
 * 功能描述：所有Activity的基类
 */
public abstract class BaseMusicActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        EventBus.getDefault().register(this);
    }

    //关闭所有音乐相关界面
    @Subscribe
    public void onActivityFinish(FinishActivityMessage message) {
        if (message.to_class.getSimpleName().contains("BaseMusicActivity")) {
//            finish();
        }
    }

    /**
     * 在每一个Activity结束的时候负责解除订阅关系
     */
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean showBottom() {
        return true;
    }
}
