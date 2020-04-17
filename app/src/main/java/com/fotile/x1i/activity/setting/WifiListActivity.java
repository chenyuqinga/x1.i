package com.fotile.x1i.activity.setting;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.voice.wifi.WifiAPManager;
import com.fotile.wifi.bean.MScanWifi;
import com.fotile.wifi.mvp.presenter.WifiPresenter;
import com.fotile.wifi.mvp.view.WifiView;
import com.fotile.wifi.util.LinkWifi;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.guide.link.GuideWifiTipDialog;
import com.fotile.x1i.activity.setting.dialog.IgnoreWifiDialog;
import com.fotile.x1i.activity.setting.dialog.WifiPWDDialog;
import com.fotile.x1i.adapter.WifiListAdapter;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.util.Constant;
import com.fotile.x1i.widget.RotationLoadingView;
import com.fotile.x1i.widget.setting.WifiListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author： yaohx
 * @data： 2019/4/17 13:55
 * @company： 杭州方太智能科技有限公司
 * @description： 无线设置页
 * <p>
 */
public class WifiListActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    /**
     * wifi开关
     */
    @BindView(R.id.btn_switch)
    Switch btnSwitch;
    /**
     * wifi显示列表
     */
    @BindView(R.id.list_wifi)
    WifiListView listWifi;
    /**
     * 旋转动图
     */
    @BindView(R.id.rotation_loading)
    RotationLoadingView rotationRefresh;
    /**
     * wifi管理器
     */
    private WifiManager wifiManager;
    /**
     * wifi列表容器
     */
    private List<MScanWifi> scanViewWifiList = new ArrayList<>();
    /**
     * wifi扫描scanner
     */
    private Scanner scanner;
    /**
     * 用于wifi状态改变的filter
     */
    private IntentFilter filter;
    /**
     * 连接的wifi
     */
    private LinkWifi linkWifi;

    /**
     * wifi的数据处理类
     */
    private WifiPresenter presenter;

    /**
     * wifi列表适配器
     */
    private WifiListAdapter wifiListAdapter;
    private MScanWifi lastFirstItem;
    /**
     * 大厨管家密码
     */
    private String hexPsw;

    /**
     * 设置页需要显示的标题
     */
    @BindView(R.id.txt_wifi_setting_title)
    TextView txtWifiSettingTitle;

    /**
     * 引导页需要显示
     */
    @BindView(R.id.txt_wifi_guide_title)
    TextView txtWifiGuideTitle;

    @BindView(R.id.layout_bottom_view)
    LinearLayout layoutBottomView;
    /**
     * 跳过
     */
    @BindView(R.id.txt_jump)
    TextView txtJump;
    /**
     * 下一步
     */
    @BindView(R.id.txt_next)
    TextView txtNext;

    boolean fromGuide = false;
    /**
     * 显示wifi图标
     */
    private final int WIFI_IS_VISIBLE = 0x01;
    /**
     * 隐藏wifi图标
     */
    private final int WIFI_IS_INVISIBLE = 0x02;

    private boolean isNext = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initApp();
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initPresenter();
        registerListener();
        registerBroadcast();
    }

    private void initData() {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Service.WIFI_SERVICE);
        scanner = new Scanner(this);
        linkWifi = new LinkWifi(context);
    }

    private void initPresenter() {
        presenter = new WifiPresenter(context);
        presenter.attachView(wifiView);
    }

    private void initApp() {
        if (getIntent().hasExtra("guide")) {
            fromGuide = true;
        }
    }

    /**
     * 初始化view
     */
    private void initView() {
        //来自引导页
        if (fromGuide) {
            txtWifiSettingTitle.setVisibility(View.GONE);
            txtWifiGuideTitle.setVisibility(View.VISIBLE);
            layoutBottomView.setVisibility(View.VISIBLE);
        }
        //来自设置页
        else {
            txtWifiSettingTitle.setVisibility(View.VISIBLE);
            txtWifiGuideTitle.setVisibility(View.GONE);
            layoutBottomView.setVisibility(View.GONE);
        }

        wifiListAdapter = new WifiListAdapter(context);
        listWifi.setAdapter(wifiListAdapter);
        txtJump.setOnClickListener(this);
        txtNext.setOnClickListener(this);
        rotationRefresh.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击跳过提示用户未联网
            case R.id.txt_jump:
                GuideWifiTipDialog dialog = new GuideWifiTipDialog(context);
                dialog.show();
                break;
            //点击下一步进入语音盒子设置
            case R.id.txt_next:
                if (isNext) {
                    Log.e("ccc", "launch box link pre");
                    Intent intent = new Intent(context, DeviceBindActivity.class);
                    intent.putExtra("guide", true);
                    launchActivity(intent);
                    //                    launchActivity(GuideBoxLinkPreActivity.class);
                }
                break;
            //点击刷新列表
            case R.id.rotation_loading:
                updateWifiList();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        scanner.forceScan();
        if (!wifiListAdapter.isEmpty()) {
            listWifi.setSelection(0);
        }
    }

    /**
     * 停止刷新列表
     */
    private void hideLoadingIcon() {
        rotationRefresh.setVisibility(View.GONE);
        rotationRefresh.stopRotationAnimation();
    }

    /**
     * 开始刷新列表
     */
    private void showLoadingIcon() {
        rotationRefresh.setVisibility(View.VISIBLE);
        rotationRefresh.startRotationAnimation();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wireless;
    }

    /**
     * 监听事件设置
     */
    public void registerListener() {
        listWifi.setOnItemClickListener(this);

        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (WifiAPManager.getInstance(WifiListActivity.this).isAPEnable()) {
                        WifiAPManager.getInstance(WifiListActivity.this).closeWifiAp();
                    }
                    if (!wifiManager.isWifiEnabled()) {
                        wifiManager.setWifiEnabled(true);
                    }
                    //开始搜索
                    wifiManager.startScan();
                    //打开开关时，获取一次列表
                    updateWifiList();
                    showLoadingIcon();
                    listWifi.setVisibility(View.VISIBLE);
                } else {
                    if (wifiManager.isWifiEnabled()) {
                        wifiManager.setWifiEnabled(false);

                    }
                    listWifi.setVisibility(View.GONE);
                    hideLoadingIcon();
                }
            }
        });
    }

    /**
     * 注册广播
     */
    public void registerBroadcast() {
        filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        context.registerReceiver(wifiReceiver, filter);
    }

    /**
     * 注销
     */
    public void unregisterBroadcast() {
        context.unregisterReceiver(wifiReceiver);
    }

    /**
     * 广播接收，监听网络
     */
    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            handleEvent(context, intent);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unregisterBroadcast();
        scanner.pause();
        scanner.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean showBottom() {
        if (fromGuide) {
            return false;
        }
        return true;
    }

    /**
     * 处理事件
     *
     * @param context
     * @param intent
     */
    public void handleEvent(Context context, Intent intent) {
        Resources resources = (Resources) context.getResources();
        ColorStateList colorStateList = (ColorStateList) resources.getColorStateList(R.color.list_color_state);
        // TODO Auto-generated method stub
        final String action = intent.getAction();
        LogUtil.LOG_WIFI("wifi相关广播",action);
        //wifi状态改变
        if ((WifiManager.WIFI_STATE_CHANGED_ACTION).equals(action)) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            updateWifiStateChanged(wifiState);
        }
        //搜索到可用的wifi
        else if ((WifiManager.SCAN_RESULTS_AVAILABLE_ACTION).equals(action)) {
            updateWifiList();
        }
        //网络状态改变
        else if ((WifiManager.NETWORK_STATE_CHANGED_ACTION).equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.State state = networkInfo.getState();
            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);

            LogUtil.LOG_WIFI("==========wifiInfo", (null == wifiInfo ?"null" : wifiInfo.getSSID()));

            LogUtil.LOG_WIFI("==========state",state);

            //如果连接成功，及时更新列表显示
            if (state == NetworkInfo.State.CONNECTED) {
                if (null != wifiListAdapter && null != wifiInfo) {
                    wifiListAdapter.notifyItemConnected(wifiInfo.getSSID());
                }
                updateWifiList();
            }
            if (state == NetworkInfo.State.DISCONNECTED || state == NetworkInfo.State.CONNECTING) {
                if (state == NetworkInfo.State.CONNECTING) {
                    PreferenceUtil.remove(context, PreferenceUtil.CONNECTING_WIFI_NAME);
                }
                //如果有wifi连接状态改变，去更新列表
                updateWifiList();
            }

            if (wifiInfo != null && state == NetworkInfo.State.CONNECTED && wifiInfo.getSSID().replace("\"", "")
                    .startsWith(Constant.IKCC_NAME)) {
                PreferenceUtil.setPreferenceValue(context, PreferenceUtil.LAST_CONNECTED_WIFI_NAME, wifiInfo.getSSID
                        ().replace("\"", ""));
                PreferenceUtil.setPreferenceValue(context, PreferenceUtil.LAST_CONNECTED_WIFI_PWD, hexPsw);
                PreferenceUtil.setPwd(context, wifiInfo.getSSID().replace("\"", ""), hexPsw);
            }

            //切换ui显示
            if (wifiInfo != null && state == NetworkInfo.State.CONNECTED) {
                isNext = true;
                txtNext.setEnabled(true);
                if (colorStateList != null) {
                    txtNext.setTextColor(colorStateList);
                }
                txtNext.setBackgroundResource(R.drawable.shape_button_select);
            } else {
                isNext = false;
                txtNext.setEnabled(false);
                if (colorStateList != null) {
                    txtNext.setTextColor(colorStateList);
                }
                txtNext.setBackgroundResource(R.drawable.shape_button_normal);
            }
        }
    }

    /**
     * 更新WiFi列表UI
     */
    public void updateWifiList() {
        final int wifiState = wifiManager.getWifiState();
        switch (wifiState) {
            // wifi处于开启状态
            case WifiManager.WIFI_STATE_ENABLED:
                presenter.getWifiData();
                break;

            default:
                break;
        }
    }

    private void updateWifiStateChanged(int state) {
        switch (state) {

            //正在打开WiFi
            case WifiManager.WIFI_STATE_ENABLING:
                btnSwitch.setEnabled(false);
                break;

            //WiFi已经打开
            case WifiManager.WIFI_STATE_ENABLED:
                btnSwitch.setEnabled(true);
                btnSwitch.setChecked(true);
                listWifi.setVisibility(View.VISIBLE);
                scanner.resume();
                showLoadingIcon();
                break;

            //正在关闭WiFi
            case WifiManager.WIFI_STATE_DISABLING:
                btnSwitch.setEnabled(false);
                break;

            //WiFi已经关闭
            case WifiManager.WIFI_STATE_DISABLED:
                btnSwitch.setEnabled(true);
                btnSwitch.setChecked(false);
                listWifi.setVisibility(View.GONE);
                wifiListAdapter.resetConnectStatus();
                listWifi.updateSelection(true);
                break;

            default:
                btnSwitch.setEnabled(true);
                break;
        }
    }

    private WifiView wifiView = new WifiView() {
        @Override
        public void onSuccess(List<MScanWifi> scanWifiList) {
            hideLoadingIcon();
            if (scanWifiList != null && scanWifiList.size() > 0) {
                MScanWifi firstItem = scanWifiList.get(0);
                if (null != lastFirstItem && !lastFirstItem.getWifiName().equals(firstItem.getWifiName())) {
                    listWifi.updateSelection(true);
                }

                if (listWifi.isCanRefresh()) {
                    scanViewWifiList = scanWifiList;
                    wifiListAdapter.updateDataList(scanWifiList);
                    lastFirstItem = firstItem;
                }
            }
        }

        @Override
        public void onError(String result) {
            hideLoadingIcon();
        }
    };

    /**
     * 显示输入密码框
     *
     * @param ap
     */
    private void showPasswordDialog(ScanResult ap) {
        WifiPWDDialog dialog = new WifiPWDDialog(context, ap);
        dialog.show();
    }

    /**
     * 显示忽略网络密码框
     *
     * @param ap
     */
    private void showIgnoreDialog(ScanResult ap) {
        IgnoreWifiDialog dialog = new IgnoreWifiDialog(context, ap);
        dialog.show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final ScanResult wifi = scanViewWifiList.get(position).scanResult;
        //是否配置过这个ssid网络
        WifiConfiguration wifiConfig = linkWifi.IsExsits(wifi.SSID);

        //点击的wifi已连接--执行断开
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSSID().equals(LinkWifi.convertToQuotedString(wifi.SSID))) {
            showIgnoreDialog(wifi);
            return;
        }
        //点击的wifi连接过--执行连接（不用输入密码）
        if (wifiConfig != null) {
            linkWifi.setMaxPriority(wifiConfig);
            linkWifi.ConnectToNetID(wifiConfig.networkId);
            PreferenceUtil.setPreferenceValue(context, PreferenceUtil.CONNECTING_WIFI_NAME, wifi.SSID);
            updateWifiList();
            return;
        }
        //执行连接（输入密码）
        if (scanViewWifiList.get(position).getIsLock()) {
            String name = (String) PreferenceUtil.getPreferenceValue(context, PreferenceUtil
                    .LAST_CONNECTED_WIFI_NAME, "");
            String pwd = (String) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.LAST_CONNECTED_WIFI_PWD,
                    "");
            if (wifi.SSID.startsWith("FotileAP_") && wifi.SSID.length() == 13) {
                //计算大厨管家密码
                String mac = wifi.SSID.substring(9, wifi.SSID.length());
                int psw = Integer.parseInt("20185555", 16) ^ Integer.parseInt(mac, 16);
                hexPsw = Integer.toHexString(psw);
                // 此处加入连接wifi代码
                LinkWifi linkWifi = new LinkWifi(context);
                int netID = linkWifi.CreateWifiInfo2(wifi, hexPsw);
                linkWifi.ConnectToNetID(netID);
            }
//            //如果本地保存的ssid和点击的wifi ssid相同
//            else if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd) && name.equals(wifi.SSID)) {
//                int netID = linkWifi.CreateWifiInfo2(wifi, pwd);
//                linkWifi.ConnectToNetID(netID);
//            }
            else {
                showPasswordDialog(wifi);
            }
        } else {
            int netID = linkWifi.CreateWifiInfo2(wifi, "");
            linkWifi.ConnectToNetID(netID);
        }

    }

    /**
     * 这个类使用startScan()方法开始扫描wifi
     * WiFi扫描结束时系统会发送该广播，
     * 用户可以监听该广播通过调用WifiManager
     * 的getScanResults方法来获取到扫描结果
     *
     * @author why
     */
    public static class Scanner extends Handler {
        private final WeakReference<WifiListActivity> weakReference;

        public Scanner(WifiListActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }
        void pause() {
            removeMessages(0);
        }
        void forceScan() {
            removeMessages(0);
            sendEmptyMessage(0);
        }

        @Override
        public void handleMessage(Message message) {
            if (null != weakReference.get() && null != weakReference.get().wifiManager) {
                weakReference.get().wifiManager.startScan();
            }
            //每隔一段时间搜索一次
            sendEmptyMessageDelayed(0, 30 * 1000);
        }
    }
}
