package com.fotile.recipe.net.view;

import android.graphics.Bitmap;

import com.fotile.common.base.BaseView;


/**
 * 文件名称：DeviceBindingView
 * 创建时间： 2018/11/14
 * 文件作者：huanghuang
 * 功能描述：设备绑定View
 */
public interface DeviceBindingView extends BaseView {

    /**
     * 二维码更新成功
     *
     * @param bitmap
     */
    void updateSuccessQR(Bitmap bitmap);

    /**
     * 二维码更新失败
     *
     * @param bitmap
     */
    void updateFailQR(Bitmap bitmap);


    /**
     * 出厂模式显示效果
     */
    void onRestoreFactory();


}
