package com.fotile.wifi.mvp.view;


import com.fotile.common.base.BaseView;
import com.fotile.wifi.bean.MScanWifi;

import java.util.List;

/**
 * 文件名称：WifiView
 * 创建时间：2017/8/15
 * 文件作者：wanghouyu
 * 功能描述：更新wifi的回調接口.
 */
public interface WifiView extends BaseView {

    /**
     * 请求成功
     *
     * @param scanWifiList 返回wifi列表
     */
    void onSuccess(List<MScanWifi> scanWifiList);

    /**
     * 请求失败
     *
     * @param result 返回的提示信息
     */
    void onError(String result);
}
