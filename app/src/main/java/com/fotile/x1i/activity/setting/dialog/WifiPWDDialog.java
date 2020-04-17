package com.fotile.x1i.activity.setting.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.net.wifi.ScanResult;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cvte.adapter.android.net.WifiManagerAdapter;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.wifi.util.LinkWifi;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.widget.RotationLoadingView;


import java.util.List;

import butterknife.BindView;

/**
 * @author chenyqi
 * @date 2019/4/17
 * @company 杭州方太智能科技有限公司
 * @description WifiPWDDialog
 * <p>
 * Copyright (c) 2018, FOTILE GROUP.
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * - Neither the name of the FOTILE GROUP nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL FOTILE GROUP BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


public class WifiPWDDialog extends BaseDialog implements View.OnClickListener {
    /**
     * 连接超时的消息类型
     */
    private static final int MESSAGE_CONNECT_TIMEOUT = 1;

    @BindView(R.id.edit_wifi_pwd)
    EditText editWifiPwd;

    @BindView(R.id.layout_pwd)
    LinearLayout layoutPwd;

    @BindView(R.id.tv_join)
    TextView tvJoin;

    @BindView(R.id.tv_cancel)
    TextView tvCancel;

    @BindView(R.id.layout_btn)
    LinearLayout layoutBtn;

    @BindView(R.id.loading_view)
    RotationLoadingView loadingView;

    private ScanResult wifi;
    private LinkWifi linkWifi;

    /**
     * 网络ID
     */
    private int netWorkId = -1;

    private WifiManager wifiManager;

    private InputMethodManager inputManager;

    /**
     * 系統提供的wifi内部接口的适配器
     */
    private WifiManagerAdapter wifiManagerAdapter;

    /**
     * 输入的密码
     */
    private String pwd;

    public WifiPWDDialog(@NonNull Context context, ScanResult ap) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
        wifi = ap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        registerNetworkReceiver();
        ScreenTool.getInstance().addPause();
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_input_password;
    }


    /**
     * 注册监听网络变化
     */
    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(mReceiver, filter);
    }

    private void initData() {
        linkWifi = new LinkWifi(context);
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManagerAdapter = new WifiManagerAdapter(context, wifiManager);
    }

    private void initView() {
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        tvJoin.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        editWifiPwd.addTextChangedListener(pwdTextChange);
        editWifiPwd.setOnEditorActionListener(editorActionListener);
    }

    /**
     * 广播接收，监听网络
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            handleEvent(intent);
        }
    };

    private void handleEvent(Intent intent) {
        // TODO Auto-generated method stub
        final String action = intent.getAction();
        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.State state = networkInfo.getState();
            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            if (wifiInfo != null && state == NetworkInfo.State.CONNECTED && wifiInfo.getSSID().equals(LinkWifi
                    .convertToQuotedString(wifi.SSID))) {
                PreferenceUtil.setPreferenceValue(context, PreferenceUtil.LAST_CONNECTED_WIFI_NAME, wifi.SSID);
                PreferenceUtil.setPreferenceValue(context, PreferenceUtil.LAST_CONNECTED_WIFI_PWD, pwd);
                PreferenceUtil.setPwd(context, wifi.SSID.replace("\"", ""), pwd);
                //连接成功
                hideLoadingIcon();
                dismiss();
            }

        } else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
            int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
            if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                handleConnectFailure(false);
            }
        }
    }

    private TextWatcher pwdTextChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (s.length() >= 8) {
                tvJoin.setEnabled(true);
            } else {
                tvJoin.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                connectAp();
                //隐藏软键盘
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            return false;
        }

    };

    @Override
    public void onClick(View v) {
        // 点击加入
        if (v.getId() == R.id.tv_join) {
            connectAp();
            //隐藏软键盘
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        // 点击取消
        else if (v.getId() == R.id.tv_cancel) {
            hideLoadingIcon();
            forgetWifi();
            //            connectExistAp();
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            dismiss();
        }
    }

    /**
     * 连接已保存的AP
     */
    private void connectExistAp() {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (null != existingConfigs && existingConfigs.size() > 0) {
            for (WifiConfiguration wifiConfiguration : existingConfigs) {
                if (wifiConfiguration.networkId != netWorkId) {
                    linkWifi.ConnectToNetID(wifiConfiguration.networkId);
                    break;
                }
            }
        }
    }

    /**
     * 连接wifi
     */
    private void connectAp() {
        String wifiPwd = editWifiPwd.getText().toString();
        if (TextUtils.isEmpty(wifiPwd)) {
            Toast.makeText(context, "密码不能为空！", Toast.LENGTH_SHORT).show();
        } else if (wifiPwd.length() <= 7) {
            Toast.makeText(context, "密码需要8位数以上！", Toast.LENGTH_SHORT).show();
        } else {
            pwd = wifiPwd;
            // 此处加入连接wifi代码
            int netID = linkWifi.CreateWifiInfo2(wifi, pwd);
            netWorkId = netID;
            linkWifi.ConnectToNetID(netID);
            tvJoin.setEnabled(false);
            showLoadingIcon();
            connectHandler.removeMessages(MESSAGE_CONNECT_TIMEOUT);
            connectHandler.sendEmptyMessageDelayed(MESSAGE_CONNECT_TIMEOUT, 60000);
        }

    }

    private Handler connectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            handleConnectFailure(true);
            return false;
        }
    });

    /**
     * AP连接失败的处理
     *
     * @param isTimeout
     */
    private void handleConnectFailure(boolean isTimeout) {
        connectHandler.removeMessages(MESSAGE_CONNECT_TIMEOUT);
        hideLoadingIcon();
        editWifiPwd.setText("");
        editWifiPwd.setHint("请输入密码");
        editWifiPwd.setHintTextColor(Color.parseColor("#66FFFFFF"));
        if (isTimeout) {
            Toast toast = Toast.makeText(context, "wifi连接失败", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            //Toast.makeText(context, "wifi连接失败", Toast.LENGTH_SHORT).show();
        } else {
            Toast toast = Toast.makeText(context, "wifi密码错误，请重新连接", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            //Toast.makeText(context, "wifi密码错误，请重新连接", Toast.LENGTH_SHORT).show();
        }
        tvJoin.setEnabled(false);
        forgetWifi();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        // 取消时 显示dock
        //        EventBus.getDefault().post(new DockMessage(DockMessage.DOCK_STATE_BAR_SHOW));
        //        EventBus.getDefault().post(new DockMessage(DockMessage.DOCK_BOTTOM_SHOW));
        connectHandler.removeMessages(MESSAGE_CONNECT_TIMEOUT);
        context.unregisterReceiver(mReceiver);
    }

    /**
     * 忘记已保存的wifi配置
     */
    private void forgetWifi() {
        if (netWorkId != -1) {
            wifiManagerAdapter.forget(netWorkId, forgetListener);
        }
    }

    /**
     * 忽略网络监听
     */
    WifiManagerAdapter.ActionListener forgetListener = new WifiManagerAdapter.ActionListener() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onFailure(int reason) {
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() ==  MotionEvent.ACTION_DOWN ) {
            View view = getCurrentFocus();
            if (isShouldHideKeyBord(view, ev)) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判定当前是否需要隐藏
     */
    protected boolean isShouldHideKeyBord(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
            //return !(ev.getY() > top && ev.getY() < bottom);
        }
        return false;
    }
    /**
     * 显示加载图标
     */
    private void showLoadingIcon() {
        loadingView.setVisibility(View.VISIBLE);
        loadingView.startRotationAnimation();
    }

    /**
     * 隐藏加载图标
     */
    private void hideLoadingIcon() {
        loadingView.setVisibility(View.GONE);
        loadingView.stopRotationAnimation();
    }

}
