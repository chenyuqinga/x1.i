package com.fotile.x1i.activity.ota;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dl7.downloaderlib.model.DownloadStatus;
import com.fotile.common.util.Tool;
import com.fotile.ota.R;
import com.fotile.ota.service.OtaDownLoadServer;
import com.fotile.x1i.base.BaseActivity;

/**
 * 项目名称：Ota
 * 创建时间：2018/10/11 14:02
 * 文件作者：yaohx
 * 功能描述：系统升级页面
 */
public class OtaMainActivity extends BaseActivity {

    /**
     * 记录onResume方法回调，是否是从wife回来
     */
    public boolean resume_from_wife = false;
    /**
     * 执行了onSaveInstanceState之后，Fragment执行commit状态不会被保存
     * 所以，当activity在后台时，不执行commit
     */
    public boolean isForeground = true;

    private OtaDownLoadServer otaDownLoadServer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindServer();
        initData();
    }

    private void bindServer() {
        Intent intent = new Intent(context, OtaDownLoadServer.class);
        context.getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    //获取下载OtaDownLoadServer
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
    protected void onResume() {
        super.onResume();
        //如果从wife回来，需要刷新页面
        if (resume_from_wife) {
            initData();
            resume_from_wife = false;
        }
    }

    private void initData() {
        FragmentManager ft = getSupportFragmentManager();
        FragmentTransaction transaction = ft.beginTransaction();
        //网络可用
        if (Tool.isNetworkAvailable(this)) {
            transaction.replace(R.id.frame, new OtaCheckFragment());
            transaction.commit();
        }
        //网络不可用
        else {
            transaction.replace(R.id.frame, new OtaNetErrorFragment());
            transaction.commit();
        }
    }

    /**
     * Dock栏返回键被点击
     */
    public void onBackClick(){
        isForeground = false;
        int download_state = otaDownLoadServer.getDownLoadState();
        if(download_state == DownloadStatus.DOWNLOADING){
            Toast.makeText(context,"后台持续下载中",Toast.LENGTH_SHORT).show();
//            SnakeBar.makeMsgSnake(context,"后台持续下载中").show();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_ota_main;
    }

    @Override
    public boolean showBottom() {
        return true;
    }

}
