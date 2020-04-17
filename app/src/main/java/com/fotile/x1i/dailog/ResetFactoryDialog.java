package com.fotile.x1i.dailog;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ebanswers.ble.BLESerialManager;
import com.fotile.common.util.Tool;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.widget.RotationLoadingView;

import java.io.File;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.DeviceControl;

/**
 * 文件名称：ResetFactoryDialog
 * 创建时间：2019/4/28 10:56
 * 文件作者：yaohx
 * 功能描述：恢复出厂设置进度条dialog
 */
public class ResetFactoryDialog extends BaseDialog {

    @BindView(R.id.img_rotaion)
    RotationLoadingView imgRotaion;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.layout_ration)
    LinearLayout layoutRation;
    @BindView(R.id.layout_loading)
    LinearLayout layoutLoading;


    /**
     * 关闭旋转效果
     */
    final int WHAT_CLOSE_RATION = 100;

    final int WHAT_UPDATE_PROGRESS = 200;

    /**
     * 2000毫秒更新完毕
     */
    private int time_all = 2000;
    /**
     * 进度条更新的时间累加
     */
    private int time_progress = 0;
    /**
     * 进度条的进度累加
     */
    private int view_progress = 0;

    public ResetFactoryDialog(Context context) {
        super(context, R.style.FullScreenDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       initData();
    }

    private void initData(){
        ScreenTool.getInstance().addPause();
        // 发送解除设备绑定指令
        DeviceControl.getInstance().setXlinkServer(2);
        //恢复出厂暂停音乐播放
        BLESerialManager.getInstance().pauseMusic();
        //删除蓝牙模块配对记录
        BLESerialManager.getInstance().deleteAllPairInfo();
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_reset_factory;
    }

    @Override
    public void show() {
        super.show();
        layoutRation.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);
        imgRotaion.startRotationAnimation();
        handler.sendEmptyMessageDelayed(WHAT_CLOSE_RATION, time_all);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        handler.removeCallbacksAndMessages(null);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //关闭旋转效果
                case WHAT_CLOSE_RATION:
                    layoutRation.setVisibility(View.GONE);
                    layoutLoading.setVisibility(View.VISIBLE);
                    imgRotaion.stopRotationAnimation();

                    handler.sendEmptyMessageDelayed(WHAT_UPDATE_PROGRESS, 200);
                    break;
                //更新进度条
                case WHAT_UPDATE_PROGRESS:
                    //每隔多少秒执行一个更新
                    int item_time = 100;
                    //每100毫秒更新一次
                    handler.sendEmptyMessageDelayed(WHAT_UPDATE_PROGRESS, item_time);
                    time_progress += item_time;

                    view_progress = view_progress + (progressBar.getMax() * item_time / time_all);
                    progressBar.setProgress(view_progress);

                    //执行恢复出厂
                    if (time_progress > time_all) {
                        dismiss();
                        Tool.resetFactory(context);

                    }
                    break;
            }
            return false;
        }
    });

}
