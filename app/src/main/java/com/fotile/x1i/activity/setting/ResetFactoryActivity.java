package com.fotile.x1i.activity.setting;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.dl7.downloaderlib.model.DownloadStatus;
import com.fotile.ota.service.OtaDownLoadServer;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.dailog.CommonDialog;
import com.fotile.x1i.dailog.FullScreenDialog;
import com.fotile.x1i.dailog.ResetFactoryDialog;
import com.fotile.x1i.listener.OnDialogListener;
import com.fotile.x1i.manager.DeviceReportManager;

import butterknife.BindView;

/**
 * 文件名称：ResetFactoryActivity
 * 创建时间：2019/4/25 13:47
 * 文件作者：yaohx
 * 功能描述：恢复出厂
 */
public class ResetFactoryActivity extends BaseActivity implements View.OnClickListener, OnDialogListener {

    @BindView(R.id.txt_reset)
    TextView txtReset;

    private OtaDownLoadServer otaDownLoadServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        bindServer();
    }

    private void initView() {
        txtReset.setOnClickListener(this);
    }

    private void bindServer() {
        Intent intent = new Intent(context, OtaDownLoadServer.class);
        context.getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    //获取下载状态
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取服务对象
            OtaDownLoadServer.OtaBinder otaBinder = (OtaDownLoadServer.OtaBinder) service;
            otaDownLoadServer = otaBinder.getServer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_reset;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //恢复出厂设置
            case R.id.txt_reset:
                int work_state = DeviceReportManager.getInstance().work_state;
                //设备工作中，无法恢复出厂设置
                if (work_state != DeviceReportManager.WORK_INIT) {
                    FullScreenDialog dialog = new FullScreenDialog(this, FullScreenDialog.FullScreenTip.ONE_BTN);
                    //设备工作中不恢复出厂设置，不需要设置listener
                    //commonDialog.setAlertButtonClickListener(this);
                    dialog.show();
                    dialog.setMessage("设备工作中，无法恢复出厂设置");
                    dialog.setTitle("提示");
                    return;
                }

                //ota下载中
                int ota_state = otaDownLoadServer.getDownLoadState();
                if (ota_state == DownloadStatus.DOWNLOADING) {
                    FullScreenDialog dialog = new FullScreenDialog(this, FullScreenDialog.FullScreenTip.ONE_BTN);
                    //ota下载中不恢复出厂设置，不需要设置listener
                    //commonDialog.setAlertButtonClickListener(this);
                    dialog.show();
                    dialog.setMessage("系统升级中，无法恢复出厂设置");
                    dialog.setTitle("提示");
                    return;
                }

                FullScreenDialog dialog = new FullScreenDialog(this, FullScreenDialog.FullScreenTip.TWO_BTN);
                dialog.setOnDialogListener(this);
                dialog.show();
                dialog.setMessage("用户数据将清零，是否确认恢复出厂设置");
                dialog.setTitle("提示");
                break;
        }
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    public void onLeftClick(Object... objects) {

    }

    @Override
    public void onRightClick(Object... objects) {
        ResetFactoryDialog dialog = new ResetFactoryDialog(context);
        dialog.show();
    }
}
