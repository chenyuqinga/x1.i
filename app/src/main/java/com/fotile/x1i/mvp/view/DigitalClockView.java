package com.fotile.x1i.mvp.view;


import com.fotile.common.base.BaseView;
import com.fotile.recipe.bean.screensaver.Weather;

/**
 * 文件名称：DigitalClockView
 * 创建时间：2017-11-07 15:32
 * 文件作者：shihuijuan
 * 功能描述：电子时钟获取天气的回调接口
 */

public interface DigitalClockView extends BaseView {
    /**
     * 获取城市信息
     *
     * @param city
     */
    void onCitySuccess(String city);

    /**
     * 获取天气信息
     *
     * @param weather
     */
    void onWeatherSuccess(Weather weather);

    /**
     * 获取信息出错
     */
    void onError();

}
