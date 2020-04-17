package com.fotile.x1i.mvp.view;

import com.fotile.recipe.bean.screensaver.Weather;
import com.fotile.x1i.base.BaseView;

/**
 * 文件名称： TopStateWeatherView
 * 创建时间： 2019/6/11
 * 文件作者： chenyqi
 * 功能描述： 状态栏获取天气的回调接口
 */



public interface TopStateWeatherView extends BaseView{
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
