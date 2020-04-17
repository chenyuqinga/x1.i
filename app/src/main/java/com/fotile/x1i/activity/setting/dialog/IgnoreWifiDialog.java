package com.fotile.x1i.activity.setting.dialog;

import android.app.Service;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cvte.adapter.android.net.WifiManagerAdapter;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.wifi.util.LinkWifi;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;

import butterknife.BindView;

/**
 * @author chenyqi
 * @date 2019/4/17
 * @company 杭州方太智能科技有限公司
 * @description 忽略wifi Dialog提示
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


public class IgnoreWifiDialog extends BaseDialog implements View.OnClickListener {
    @BindView(R.id.tv_ignore_cancle)
    TextView tvIgnoreCancle;

    @BindView(R.id.tv_ignore_sure)
    TextView tvIgnoreSure;

    private ScanResult scanWifi;

    private LinkWifi linkWifi;
    private WifiManager wifiManager;

    private WifiConfiguration wifiConfig;

    /**
     * 系統提供的wifi内部接口的适配器
     */
    public WifiManagerAdapter wifiManagerAdapter;

    public IgnoreWifiDialog(@NonNull Context context, ScanResult ap) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
        scanWifi = ap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_ignore_wifi;
    }

    private void initView() {
        tvIgnoreCancle.setOnClickListener(this);
        tvIgnoreSure.setOnClickListener(this);
    }

    private void initData() {
        this.linkWifi = new LinkWifi(context);
        this.wifiManager = (WifiManager) context.getSystemService(Service.WIFI_SERVICE);
        wifiManagerAdapter = new WifiManagerAdapter(context, wifiManager);
        if (null != scanWifi) {
            wifiConfig = linkWifi.IsExsits(scanWifi.SSID);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_ignore_sure) {

            if (null != wifiConfig) {
                String lastWifiName = (String) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.LAST_CONNECTED_WIFI_NAME, "");
                if (!TextUtils.isEmpty(lastWifiName) && LinkWifi.convertToQuotedString(lastWifiName).equals(wifiConfig.SSID)) {
                    PreferenceUtil.removeWifiInfo(context, wifiConfig.SSID.replace("\"", ""));
                    PreferenceUtil.remove(context, PreferenceUtil.LAST_CONNECTED_WIFI_NAME);
                    PreferenceUtil.remove(context, PreferenceUtil.LAST_CONNECTED_WIFI_PWD);
                }
                wifiManagerAdapter.forget(wifiConfig.networkId, forgetListener);
            }
            dismiss();

        } else if (v.getId() == R.id.tv_ignore_cancle) {
            dismiss();
        }
    }

    private WifiManagerAdapter.ActionListener forgetListener = new WifiManagerAdapter.ActionListener() {
        @Override
        public void onSuccess() {
            PreferenceUtil.remove(context, PreferenceUtil.LAST_CONNECTED_WIFI_NAME);
            PreferenceUtil.remove(context, PreferenceUtil.LAST_CONNECTED_WIFI_PWD);
            PreferenceUtil.removeWifiInfo(context, wifiConfig.SSID.replace("\"", ""));
        }

        @Override
        public void onFailure(int reason) {
        }
    };
}
