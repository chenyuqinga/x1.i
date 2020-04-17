package com.fotile.ota.util;

/**
  * 文件名称：OtaListener
  * 创建时间：2018/8/1 17:09
  * 文件作者：yaohx
  * 功能描述：OtaListener
  */
public interface OtaListener {


    abstract void onDownloadCompleted(String newVersion);

    abstract void onInstallNow(boolean containMcu);

    abstract void onInstallLater(boolean containMcu);
    abstract void gotoWifiActivity();
}
