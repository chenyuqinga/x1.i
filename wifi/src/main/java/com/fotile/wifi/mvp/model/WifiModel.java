package com.fotile.wifi.mvp.model;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.wifi.bean.MScanWifi;
import com.fotile.wifi.util.LinkWifi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 文件名称：WifiModel
 * 创建时间：2019/6/19 15:16
 * 文件作者：yaohx
 * 功能描述：可用wifi列表获取
 */
public class WifiModel {
    /**
     * WifiManager
     */
    private WifiManager mWifiManager;
    /**
     * 搜索到的wifi列表（自定义）
     */
    private List<MScanWifi> mScanWifiList;
    /**
     * 系统搜索到的列表
     */
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfiguration;
    private LinkWifi linkWifi;
    private Context context;
    /**
     * 网络连接管理
     */
    private ConnectivityManager connectivityManager;

    /**
     * 正在连接的AP名字
     */
    private String connectingWifiName;

    public WifiModel(Context context) {
        this.context = context;
        mWifiManager = (WifiManager) context.getSystemService(Service.WIFI_SERVICE);
        linkWifi = new LinkWifi(context);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void getData() {
//        LogUtil.LOG_WIFI("------------------WifiModel结束获取数据------------------","start");
        //用户点击列表时，正在连接的wifi
        connectingWifiName = (String) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.CONNECTING_WIFI_NAME,
                "");

        if (mWifiList != null) {
            mWifiList.clear();
        }
        //获取到搜索结果列表
        List<ScanResult> temp = mWifiManager.getScanResults();
        mWifiList = new ArrayList<>();
        boolean isAdd = false;
        if (temp != null) {
            for (int i = 0; i < temp.size(); i++) {
                isAdd = false;
                for (int j = 0; j < mWifiList.size(); j++) {
                    if (mWifiList.get(j).SSID.equals(temp.get(i).SSID)) {
                        isAdd = true;
                        if (mWifiList.get(j).level < temp.get(i).level) {
                            // ssid相同且新的信号更强
                            mWifiList.remove(j);
                            mWifiList.add(temp.get(i));
                            break;
                        }
                    }
                }
                if (!isAdd) {
                    mWifiList.add(temp.get(i));
                }
            }
        }
        //这个好像没有用到
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
        //根据信号强度排序
        if (mWifiList != null) {
            sortByLevel(mWifiList);
        }
        initScanWifilist();
//        LogUtil.LOG_WIFI("------------------WifiModel结束获取数据------------------","end");
    }

    public List<MScanWifi> getMScanWifiList() {
        return mScanWifiList;
    }

    /**
     * 获取到自定义的ScanResult
     * 将 mWifiList 组合成自定义
     */
    private void initScanWifilist() {
        MScanWifi mScanwifi;
        mScanWifiList = new ArrayList<MScanWifi>();
        List<MScanWifi> scanWifiListIKCC = new ArrayList<>();
        List<MScanWifi> scanWifiLists = new ArrayList<>();

        for (int i = 0; i < mWifiList.size(); i++) {
            //总共分为四个等级
            int level = WifiManager.calculateSignalLevel(mWifiList.get(i).level, 4);
            String mwifiName = mWifiList.get(i).SSID;
            if (mwifiName == null || "".equals(mwifiName)) {
                continue;
            }
            //是否加密
            boolean boolean1 = false;
            if (mWifiList.get(i).capabilities.contains("WEP") || mWifiList.get(i).capabilities.contains("PSK") ||
                    mWifiList.get(i).capabilities.contains("EAP")) {
                boolean1 = true;
            } else {
                boolean1 = false;
            }

            mScanwifi = new MScanWifi(mWifiList.get(i), mwifiName, level, boolean1);
            //是否以前配置过该网络
            if (linkWifi.IsExsits(mwifiName) != null) {
                mScanwifi.setIsExsit(true);
            } else {
                mScanwifi.setIsExsit(false);
            }

            //设置wifi AP的连接状态
            handleWifiConnect(mScanwifi);

            //将已经连接或者正在连接的排在第一位
            if (mScanwifi.getIsConnectedNow() || mScanwifi.isConnecting()) {
                mScanWifiList.add(0, mScanwifi);
            } else {
                if (mwifiName.startsWith("FotileAP_")) {
                    scanWifiListIKCC.add(mScanwifi);
                } else {
                    scanWifiLists.add(mScanwifi);
                }
            }
        }
        mScanWifiList.addAll(scanWifiListIKCC);
        mScanWifiList.addAll(scanWifiLists);

        for(MScanWifi mScanWifi : mScanWifiList ){
            LogUtil.LOG_WIFI("搜索到的wifi状态", mScanWifi);
        }
    }


    /**
     * 对wifi列表按信号强弱排序
     */
    private void sortByLevel(List<ScanResult> list) {
        Collections.sort(list, new Comparator<ScanResult>() {

            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return rhs.level - lhs.level;
            }
        });
    }

    /**
     * 设置wifi AP的连接状态
     *
     * @param scanWifi
     */
    private void handleWifiConnect(MScanWifi scanWifi) {
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        String linkSsid = wifiInfo.getSSID();
        String scanSsid = linkWifi.convertToQuotedString(scanWifi.getWifiName());

        //如果连接中的ssid和某一个scan的ssid相同
        if (linkSsid.startsWith("\"") && linkSsid.equals(scanSsid)) {
            if (scanWifi.getIsExsit()) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    scanWifi.setIsConnectedNow(true);
                    scanWifi.setConnecting(false);
                } else {
                    scanWifi.setIsConnectedNow(false);
                    scanWifi.setConnecting(true);
                }
            }
        } else {
            scanWifi.setIsConnectedNow(false);
            scanWifi.setConnecting(false);
        }

    }
}
