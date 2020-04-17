package com.fotile.bind.mvp.view;

import android.graphics.Bitmap;


/**
 * 文件名称：DeviceBindingView
 * 创建时间： 2018/11/14
 * 文件作者：huanghuang
 * 功能描述：设备绑定View
 */
public interface DeviceBindingView {

    /**
     * 二维码更新成功
     *
     * @param bitmap
     */
    void updateSuccessQR(Bitmap bitmap);

    /**
     * 二维码更新失败
     */
    void updateFailQR(   );

}
