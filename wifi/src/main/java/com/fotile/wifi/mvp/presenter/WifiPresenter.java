package com.fotile.wifi.mvp.presenter;

import android.content.Context;

import com.fotile.common.base.BasePresenter;
import com.fotile.common.base.BaseView;
import com.fotile.wifi.bean.MScanWifi;
import com.fotile.wifi.mvp.model.WifiModel;
import com.fotile.wifi.mvp.view.WifiView;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称：WifiPresenter
 * 创建时间：2019/6/19 15:14
 * 文件作者：yaohx
 * 功能描述：用于请求搜索到的wifi列表
 */
public class WifiPresenter implements BasePresenter {
    private WifiModel model;
    private WifiView wifiView;

    public WifiPresenter(Context context) {
        model = new WifiModel(context);
    }

    @Override
    public void onCreate(CompositeSubscription compositeSubscription) {
    }

    @Override
    public void attachView(BaseView wifiView) {
        this.wifiView = (WifiView) wifiView;
    }

    public void getWifiData() {
        model.getData();
        List<MScanWifi> scanWifiList = model.getMScanWifiList();
        if (scanWifiList != null) {
            wifiView.onSuccess(scanWifiList);
        } else {
            wifiView.onError("get list is null");
        }
    }
}