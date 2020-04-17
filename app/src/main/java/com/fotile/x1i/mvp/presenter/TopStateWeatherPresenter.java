package com.fotile.x1i.mvp.presenter;

import android.content.Context;

import com.fotile.common.util.log.LogUtil;
import com.fotile.recipe.bean.screensaver.City;
import com.fotile.recipe.bean.screensaver.Weather;
import com.fotile.recipe.bean.screensaver.WeatherResponse;
import com.fotile.recipe.net.modle.DataManager;
import com.fotile.x1i.base.BasePresenter;
import com.fotile.x1i.base.BaseView;
import com.fotile.x1i.mvp.view.TopStateWeatherView;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称： TopStateWeatherPresenter
 * 创建时间： 2019/6/11
 * 文件作者： chenyqi
 * 功能描述： 状态栏获取Presenter
 */



public class TopStateWeatherPresenter implements BasePresenter {
    private Context context;
    private TopStateWeatherView topStateWeatherView;
    private CompositeSubscription compositeSubscription;
    /**
     * 网络数据请求管理
     */
    private DataManager dataManager;

    public TopStateWeatherPresenter(Context context){
        this.context = context;
    }

    @Override
    public void onCreate(CompositeSubscription compositeSubscription) {
        this.compositeSubscription = compositeSubscription;
        dataManager = new DataManager(context);
    }

    @Override
    public void attachView(BaseView baseView) {
        topStateWeatherView = (TopStateWeatherView)baseView;
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
                        LogUtil.LOGE("---天气--错误城市", e.toString());
                        topStateWeatherView.onError();
                    }

                    @Override
                    public void onNext(City city) {
                        if (null != city && null != city.getDataList()) {
                            List<String> cityInfo = city.getDataList();
                            if (cityInfo.size() > 3) {
                                topStateWeatherView.onCitySuccess(cityInfo.get(2));
                            } else {
                                topStateWeatherView.onError();
                            }
                        } else {
                            topStateWeatherView.onError();
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
                        LogUtil.LOGE("---天气--错误温度", e.toString());
                        topStateWeatherView.onError();
                    }

                    @Override
                    public void onNext(WeatherResponse<Weather> response) {
                        if (null != response) {
                            List<Weather> list = response.getWeather();
                            if (null != list && list.size() > 0) {
                                topStateWeatherView.onWeatherSuccess(list.get(0));
                            } else {
                                topStateWeatherView.onError();
                            }
                        } else {
                            topStateWeatherView.onError();
                        }

                    }
                })
        );
    }

}
