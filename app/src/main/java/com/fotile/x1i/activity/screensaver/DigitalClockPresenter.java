package com.fotile.x1i.activity.screensaver;

import android.content.Context;

import com.fotile.common.base.BasePresenter;
import com.fotile.common.base.BaseView;
import com.fotile.recipe.bean.screensaver.City;
import com.fotile.recipe.bean.screensaver.Weather;
import com.fotile.recipe.bean.screensaver.WeatherResponse;
import com.fotile.recipe.net.modle.DataManager;
import com.fotile.x1i.mvp.view.DigitalClockView;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称：DigitalClockPresenter
 * 创建时间：2017-11-07 15:30
 * 文件作者：shihuijuan
 * 功能描述：电子时钟Presenter
 */

public class DigitalClockPresenter implements BasePresenter {

    private Context context;
    private DigitalClockView digitalClockView;
    private CompositeSubscription compositeSubscription;
    /**
     * 网络数据请求管理
     */
    private DataManager dataManager;

    public DigitalClockPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(CompositeSubscription compositeSubscription) {
        this.compositeSubscription = compositeSubscription;
        dataManager = new DataManager(context);
    }

    @Override
    public void attachView(BaseView baseView) {
        digitalClockView = (DigitalClockView) baseView;
    }

    /**
     * 获取城市信息
     *
     * @param ipAddress
     */
    public void getCityInfo(String ipAddress) {
        compositeSubscription.add(dataManager.getCity(ipAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<City>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        digitalClockView.onError();
                    }

                    @Override
                    public void onNext(City city) {
                        if (null != city && null != city.getDataList()) {
                            List<String> cityInfo = city.getDataList();
                            if (cityInfo.size() > 3) {
                                digitalClockView.onCitySuccess(cityInfo.get(2));
                            } else {
                                digitalClockView.onError();
                            }
                        } else {
                            digitalClockView.onError();
                        }
                    }
                })
        );
    }

    /**
     * 获取天气信息
     *
     * @param city
     */
    public void getWeatherInfo(String city) {
        compositeSubscription.add(dataManager.getWeatherInfo(city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WeatherResponse<Weather>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        digitalClockView.onError();
                    }

                    @Override
                    public void onNext(WeatherResponse<Weather> response) {
                        if (null != response) {
                            List<Weather> list = response.getWeather();
                            if (null != list && list.size() > 0) {
                                digitalClockView.onWeatherSuccess(list.get(0));
                            } else {
                                digitalClockView.onError();
                            }
                        } else {
                            digitalClockView.onError();
                        }

                    }
                })
        );
    }
}
