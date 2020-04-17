package com.fotile.x1i.activity.ota;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dl7.downloaderlib.entity.FileInfo;
import com.dl7.downloaderlib.model.DownloadStatus;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.ota.bean.MyFileInfo;
import com.fotile.ota.bean.UpgradeInfo;
import com.fotile.ota.service.OtaDownLoadServer;
import com.fotile.ota.util.OtaConstant;
import com.fotile.ota.util.OtaUpgradeUtil;
import com.fotile.ota.view.ProgressView;
import com.fotile.x1i.R;
import com.fotile.x1i.dailog.CommonDialog;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.widget.SnakeBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.fotile.x1i.activity.ota.OtaCheckFragment.REQUEST_EXIT_DOWNLOADED;
import static com.fotile.x1i.activity.ota.OtaCheckFragment.REQUEST_EXIT_DOWNLOADING;
import static com.fotile.x1i.activity.ota.OtaCheckFragment.REQUEST_EXIT_UNDOWNLOAD;
import static com.fotile.x1i.manager.DeviceReportManager.WORK_INIT;

/**
 * 项目名称：Ota
 * 创建时间：2018/10/12 9:55
 * 文件作者：yaohx
 * 功能描述：Ota下载页面
 * 显示固件包的下载状态（下载完成或者下载中）
 */
public class OtaDownloadFragment extends BaseOtaFragment {
    /**************************下载中**************************/
    private View layout_downloading;
    /**
     * 下载进度条
     */
    private ProgressView progress_bar;
    /**
     * 下载版本
     */
    private TextView txt_version_target_1;
    /**************************下载中**************************/

    /**************************下载完成**************************/
    private View layout_downloaded;
    /**
     * 进度条上的提示文案
     */
    private TextView txt_down_tip;
    /**
     * 下载版本
     */
    private TextView txt_version_target_2;
    /**
     * 更新内容
     */
    private TextView txt_version_desc;
    /**
     * 立即安装
     */
    private TextView txt_install_now;
    /**
     * 稍后安装
     */
    private TextView txt_install_later;

    private OtaDownLoadServer otaDownLoadServer;
    /**************************下载完成**************************/

    final int VIEW_DOWNLOADING = 2001;
    final int VIEW_DOWNLOADED = 2002;
    final int VIEW_DOWN_ERROR = 2003;

    private UpgradeInfo upgradeInfo;
    /**
     * 下载包当前的状态
     */
    private int state;

    public OtaDownloadFragment(UpgradeInfo upgradeInfo, int state) {
        this.upgradeInfo = upgradeInfo;
        this.state = state;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initView();
        //设备工作中，无法系統升級
        int work_state = DeviceReportManager.work_state;
        if(work_state != WORK_INIT){
            CommonDialog dialog = new CommonDialog(context, CommonDialog.CommonTip.ONE_BTN);
            dialog.setTitle("提示");
            dialog.setMessage("烟机工作中，无法系统升级");
            dialog.show();
            return view;
        }
        showView(VIEW_DOWNLOADING);
        initData();
        return view;
    }

    private void initData() {
        //存在文件--已下载完成
        if (state == REQUEST_EXIT_DOWNLOADED) {
            //直接显示下载完成
            showView(VIEW_DOWNLOADED);
            return;
        }
        //存在文件--未下载或者下载中
        if (state == REQUEST_EXIT_UNDOWNLOAD || state == REQUEST_EXIT_DOWNLOADING) {
            showView(VIEW_DOWNLOADING);
        }

        EventBus.getDefault().register(this);
        //使用全局context绑定服务
        Intent intent = new Intent(context, OtaDownLoadServer.class);
        context.getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    //服务绑定完成后开始下载
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取服务对象
            OtaDownLoadServer.OtaBinder otaBinder = (OtaDownLoadServer.OtaBinder) service;
            otaDownLoadServer = otaBinder.getServer();
            //            otaDownLoadServer.startDownload(upgradeInfo, false);
            otaDownLoadServer.startDownload(upgradeInfo, false);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //下载中进度回调，事件由DownLoadService中发出
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDownloadUpdate(MyFileInfo myFileInfo) {
        FileInfo fileInfo = myFileInfo.fileInfo;
        if (null != fileInfo) {
            int state = fileInfo.getStatus();
            switch (state) {
                //开始
                case DownloadStatus.START:
                    showView(VIEW_DOWNLOADING);
                    break;
                //下载中
                case DownloadStatus.DOWNLOADING:
                    float progress = OtaUpgradeUtil.getProgress(fileInfo.getLoadBytes(), fileInfo.getTotalBytes());
                    progress_bar.setProgress(progress);
                    showView(VIEW_DOWNLOADING);
                    break;
                //完成（固件包或者mcu下载完成都会回调这里）
                case DownloadStatus.COMPLETE:
                    //校验成功
                    if(myFileInfo.md5Check){
                        progress_bar.setProgress(1.0f);
                        showView(VIEW_DOWNLOADED);
                    }
                    else{
                        change2Check();
                        SnakeBar.makeMsgSnake(context,"安装包下载错误，请重试").show();
                    }
                    break;
                //报错
                case DownloadStatus.ERROR:
                    showView(VIEW_DOWN_ERROR);
                    SnakeBar.makeMsgSnake(context, "网络断开，下载暂停，请恢复网络").show();
                    break;
            }
        }
    }

    public void showView(int situation) {
        switch (situation) {
            //报错
            case VIEW_DOWN_ERROR:
                txt_down_tip.setText("暂停下载升级包");
                break;
            //下载中
            case VIEW_DOWNLOADING:
                txt_down_tip.setText("正在下载升级包");
                layout_downloading.setVisibility(View.VISIBLE);
                layout_downloaded.setVisibility(View.GONE);
                break;
            //下载完成
            case VIEW_DOWNLOADED:
                txt_down_tip.setText("下载完成");
                layout_downloading.setVisibility(View.GONE);
                layout_downloaded.setVisibility(View.VISIBLE);
                break;

        }
    }

    private void initView() {
        layout_downloading = view.findViewById(R.id.layout_downloading);
        progress_bar = (ProgressView) view.findViewById(R.id.progress_bar);
        txt_down_tip = (TextView) view.findViewById(R.id.txt_down_tip);
        txt_version_target_1 = (TextView) view.findViewById(R.id.txt_version_target_1);

        layout_downloaded = view.findViewById(R.id.layout_downloaded);
        txt_version_target_2 = (TextView) view.findViewById(R.id.txt_version_target_2);
        txt_version_desc = (TextView) view.findViewById(R.id.txt_version_desc);
        txt_install_now = (TextView) view.findViewById(R.id.txt_install_now);
        txt_install_later = (TextView) view.findViewById(R.id.txt_install_later);

        txt_install_now.setOnClickListener(install_now_listener);
        txt_install_later.setOnClickListener(install_later_listener);

        txt_version_target_1.setText("方太智慧厨房" + upgradeInfo.name);
        txt_version_target_2.setText("方太智慧厨房" + upgradeInfo.name);
        txt_version_desc.setText(upgradeInfo.comment);
    }

    //立即安装
    View.OnClickListener install_now_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // /sdcard/***.apk
            AppUtil.upgrade(context, OtaConstant.FILE_ABSOLUTE_OTA);
        }
    };

    //稍后安装
    View.OnClickListener install_later_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().finish();
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.fragment_ota_download;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (null != otaDownLoadServer) {
            otaDownLoadServer.cancelNetTimer();
        }
    }

}
