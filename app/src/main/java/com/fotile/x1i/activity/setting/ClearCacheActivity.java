package com.fotile.x1i.activity.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.message.util.MessageDataBaseUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.dailog.CommonDialog;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.widget.RotationLoadingView;
import com.fotile.x1i.widget.SnakeBar;

import butterknife.BindView;

import static com.fotile.x1i.manager.DeviceReportManager.WORK_INIT;

/**
 * 文件名称：ClearCacheActivity
 * 创建时间：2019/4/24 17:03
 * 文件作者：yaohx
 * 功能描述：清除缓存
 */
public class ClearCacheActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.layout_clear_cache)
    View layout_clear_cache;
    /**
     * 旋转动图
     */
    @BindView(R.id.rotation_loading)
    RotationLoadingView rotation_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout_clear_cache.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_clear;
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //清空缓存
            case R.id.layout_clear_cache:
                //设备工作中，无法清除缓存
                int work_state = DeviceReportManager.work_state;
                if(work_state != WORK_INIT){
                    CommonDialog dialog = new CommonDialog(context, CommonDialog.CommonTip.ONE_BTN);
                    dialog.setTitle("提示");
                    dialog.setMessage("设备工作中，无法清除缓存");
                    dialog.show();
                    return;
                }
                rotation_loading.startRotationAnimation();
                // 删除所有消息
                MessageDataBaseUtil.getInstance().deleteAllMemorandum();
                MessageDataBaseUtil.getInstance().deleteAllNotification();
                //
                PreferenceUtil.setHasUnreadMemorandum(context, false);
                PreferenceUtil.setHasUnreadNotification(context, false);
                handler.sendEmptyMessageDelayed(1,3000);
                break;
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            rotation_loading.stopRotationAnimation();
            SnakeBar.makeMsgSnake(context,"清除缓存成功").show();
            return false;
        }
    });
}
