package com.fotile.x1i.activity.ota;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dl7.downloaderlib.model.DownloadStatus;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.common.util.Tool;
import com.fotile.ota.R;
import com.fotile.ota.bean.UpgradeInfo;
import com.fotile.ota.service.OtaDownLoadServer;
import com.fotile.ota.util.OtaConstant;
import com.fotile.ota.util.OtaUpgradeUtil;
import com.fotile.ota.view.OtaLoadingView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.fotile.common.util.PreferenceUtil.OTA_AVAILABLE;


/**
 * 项目名称：Ota
 * 创建时间：2018/10/11 14:04
 * 文件作者：yaohx
 * 功能描述：ota检测界面
 * 检测中
 * 检测结果-无版本更新
 * 检测结果-有版本更新
 */
public class OtaCheckFragment extends BaseOtaFragment {
    /******************************************************************************/
    /**
     * 检测结果-无版本更新
     */
    private View layout_checked_none;
    /**
     * 本地版本号
     */
    private TextView txt_version_local;
    /******************************************************************************/
    /**
     * 检测结果-有版本更新
     */
    private View layout_checked_exist;
    /**
     * 目标版本
     */
    private TextView txt_version_target;
    /**
     * 版本描述
     */
    private TextView txt_version_desc;
    /**
     * 开始下载
     */
    private TextView txt_version_download;
    /******************************************************************************/
    /**
     * 版本检测中
     */
    private View layout_checking;
    private OtaLoadingView img_loading;
    /******************************************************************************/

    /**
     * 校验包名
     */
    private String check_package_name;
    /**
     * 校验本地版本号
     */
    private String check_version_code;
    /**
     * 校验本地mac
     */
    private String check_mac_address;
    private UpgradeInfo upgradeInfo;

    //请求中
    private static final int REQUEST_CHECKING = 1000;
    //获取超时
    private static final int REQUEST_TIMEOUT = 1001;
    //服务器无新的升级包
    public static final int REQUEST_NO_PACKAGE = 1002;
    //获取数据异常
    public static final int REQUEST_ERROR = 1003;
    //有新的升级包-未下载
    public static final int REQUEST_EXIT_UNDOWNLOAD = 1004;
    //有新的升级包-已下载
    public static final int REQUEST_EXIT_DOWNLOADED = 1005;
    //有新的升级包-下载中
    public static final int REQUEST_EXIT_DOWNLOADING = 1006;

    private OtaDownLoadServer otaDownLoadServer;
    /**
     * 下载状态
     */
    private int download_state = DownloadStatus.NORMAL;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initView();
        showView(REQUEST_CHECKING);
        initData();
        bindServer();
        return view;
    }

    private void initData() {
        //用于匹配OTA服务器的包名
        String key = "ota_check_package";
        check_package_name = Tool.getPkgMetaValue(context, key);
        //校验匹配版本号
        check_version_code = Tool.getLocalSysVersion(context);
        //校验mac地址
        check_mac_address = Tool.getLocalMacAddress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getUpgradeInfo();
            }
        }).start();
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
            download_state = otaDownLoadServer.getDownLoadState();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 根据请求返回数据，显示view
     *
     * @param situation
     */
    public void showView(int situation) {
        switch (situation) {
            //请求中
            case REQUEST_CHECKING:
                layout_checking.setVisibility(View.VISIBLE);
                layout_checked_none.setVisibility(View.GONE);
                layout_checked_exist.setVisibility(View.GONE);
                img_loading.startRotationAnimation();
                break;
            //请求超时
            case REQUEST_TIMEOUT:
                change2NetError();
                break;
            //请求错误
            case REQUEST_ERROR:
                layout_checking.setVisibility(View.GONE);
                layout_checked_none.setVisibility(View.VISIBLE);
                layout_checked_exist.setVisibility(View.GONE);
                img_loading.stopRotationAnimation();
                break;
            //服务器无新的升级包
            case REQUEST_NO_PACKAGE:
                layout_checking.setVisibility(View.GONE);
                layout_checked_none.setVisibility(View.VISIBLE);
                layout_checked_exist.setVisibility(View.GONE);
                img_loading.stopRotationAnimation();
                break;
            //存在下载url-未下载
            case REQUEST_EXIT_UNDOWNLOAD:
                layout_checking.setVisibility(View.GONE);
                layout_checked_none.setVisibility(View.GONE);
                layout_checked_exist.setVisibility(View.VISIBLE);
                img_loading.stopRotationAnimation();

                txt_version_target.setText("方太智慧厨房" + upgradeInfo.name);
                txt_version_desc.setText(upgradeInfo.comment);
                break;
            //存在下载url-已下载
            case REQUEST_EXIT_DOWNLOADED:
                change2Download(upgradeInfo, REQUEST_EXIT_DOWNLOADED);
                break;
            //存在下载url-正在下载
            case REQUEST_EXIT_DOWNLOADING:
                change2Download(upgradeInfo, REQUEST_EXIT_DOWNLOADING);
                break;
        }
    }

    public void initView() {
        layout_checked_none = view.findViewById(R.id.layout_checked_none);
        txt_version_local = (TextView) view.findViewById(R.id.txt_version_local);
        layout_checked_exist = view.findViewById(R.id.layout_checked_exist);
        txt_version_target = (TextView) view.findViewById(R.id.txt_version_target);
        txt_version_desc = (TextView) view.findViewById(R.id.txt_version_desc);
        txt_version_download = (TextView) view.findViewById(R.id.txt_version_download);
        layout_checking = view.findViewById(R.id.layout_checking);
        img_loading = (OtaLoadingView) view.findViewById(R.id.img_loading);

        txt_version_local.setText("检测到当前系统已是最新版本\n方太智慧厨房" + Tool.getVersionName(context));

        txt_version_download.setOnClickListener(version_download_listener);
        img_loading.startRotationAnimation();
    }

    //版本下载
    View.OnClickListener version_download_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Tool.fastclick()) {
                if (null != upgradeInfo) {
                    //存在下载文件-未下载
                    change2Download(upgradeInfo, REQUEST_EXIT_UNDOWNLOAD);
                }
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.fragment_ota_check;
    }


    /**
     * 获取OTA服务器上的固件的信息
     */
    private void getUpgradeInfo() {
        OtaUpgradeUtil otaUpgradeUtil = new OtaUpgradeUtil();
        String reqUrl = otaUpgradeUtil.buildUrl(check_package_name, check_version_code, check_mac_address, context);
        //测试使用
        if (OtaConstant.OTA_URL_TEST) {
            reqUrl = OtaConstant.test_url;
        }

        LogUtil.LOGOta("请求Ota包信息url", reqUrl);
        String content = "";
        String miwen = "";
        String mingwen = "";
        try {
            content = otaUpgradeUtil.httpGet(reqUrl);
            //没有可更新的包
            if (content == null || content.equals("{}")) {
                checkhandler.sendEmptyMessage(REQUEST_NO_PACKAGE);
//                OtaTool.RedTips = 0;
//                OtaTool.delectFile();
                return;
            }
            JSONObject jo = new JSONObject(content);
            miwen = jo.getString("message");
            mingwen = otaUpgradeUtil.Decrypt(miwen, OtaConstant.PASSWORD);
            LogUtil.LOGOta("请求Ota包信息返回数据", mingwen);
        } catch (IOException e) {
            e.printStackTrace();
            checkhandler.sendEmptyMessage(REQUEST_TIMEOUT);
            return;
        } catch (JSONException e) {
            e.printStackTrace();
            checkhandler.sendEmptyMessage(REQUEST_ERROR);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            checkhandler.sendEmptyMessage(REQUEST_ERROR);
            return;
        }
        Gson parser = new Gson();
        upgradeInfo = parser.fromJson(mingwen, UpgradeInfo.class);

        //下载中
        if (download_state == DownloadStatus.DOWNLOADING) {
            checkhandler.sendEmptyMessage(REQUEST_EXIT_DOWNLOADING);
        } else {
            //检测ota下载包是否在本地已经存在
            if (OtaUpgradeUtil.exitOtaFile(upgradeInfo)) {
                checkhandler.sendEmptyMessage(REQUEST_EXIT_DOWNLOADED);
            } else {
                checkhandler.sendEmptyMessage(REQUEST_EXIT_UNDOWNLOAD);
            }
        }

//        //如果mcu下载包以前没有下载过
//        if (!TextUtils.isEmpty(upgradeInfo.ex_url) && !OtaUpgradeUtil.exitMcuFile(upgradeInfo)) {
//            Intent intent = new Intent(context, OtaDownLoadServer.class);
//            intent.putExtra("upgradeInfo", upgradeInfo);
//            context.startService(intent);
//        }

//        这个是获取网络文件大小，应该让后台改，实在不行在这里改一下
//        mInfo.size = String.valueOf( OtaTool.getFileLength(mInfo.url));
//        File file = new File(OtaConstant.FILE_ABSOLUTE_OTA);
//        有本地文件并且MD5校验合法
//        if (file.exists() && OtaTool.checkDownloadFileMd5(upgradeInfo)) {
//            OtaTool.RedTips = 1;
//        return;
//        } else {
//            file.delete();
//            OtaTool.RedTips = 2;
//            checkhandler.sendEmptyMessage(REQUEST_ERROR);
//        }
    }

    /**
     * 固件包升级信息回调
     * 进入该回调，mInfo有值了
     */
    private Handler checkhandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                //超时
                case REQUEST_TIMEOUT:
                    showView(REQUEST_TIMEOUT);
                    break;
                //无下载包
                case REQUEST_NO_PACKAGE:
                    showView(REQUEST_NO_PACKAGE);
                    PreferenceUtil.setPreferenceValue(context,OTA_AVAILABLE,false);
                    break;
                //存在下载url-未下载
                case REQUEST_EXIT_UNDOWNLOAD:
                    showView(REQUEST_EXIT_UNDOWNLOAD);
                    PreferenceUtil.setPreferenceValue(context,OTA_AVAILABLE,true);
                    break;
                //存在下载url-已下载
                case REQUEST_EXIT_DOWNLOADED:
                    showView(REQUEST_EXIT_DOWNLOADED);
                    PreferenceUtil.setPreferenceValue(context,OTA_AVAILABLE,true);
                    break;
                //存在下载url-下载中
                case REQUEST_EXIT_DOWNLOADING:
                    showView(REQUEST_EXIT_DOWNLOADING);
                    PreferenceUtil.setPreferenceValue(context,OTA_AVAILABLE,true);
                    break;
                //请求错误
                case REQUEST_ERROR:
                    showView(REQUEST_ERROR);
                    break;
            }
        }
    };


}
