package com.fotile.bind.mvp.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;

import com.fotile.bind.mvp.modle.DataManager;
import com.fotile.bind.mvp.view.DeviceBindingView;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import fotile.device.cookerprotocollib.helper.DeviceControl;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称：DeviceBindingPresenter
 * 创建时间： 2018/11/14
 * 文件作者：huanghuang
 * 功能描述：设备扫码订阅Presenter
 */
public class DeviceBindingPresenter {

    private CompositeSubscription compositeSubscription;
    private DeviceBindingView deviceBindingView;
    private DataManager dataManager;
    private Context context;
    private Subscription subscription;
    private Bitmap bitmapQR;
    /**
     * 是否在定时获取数据
     */
    private Boolean isRegisterReceiver = false;
    /**
     * 产品id
     */
    private String productId;
    /**
     * 认证码
     */
    private String authorizeCode;
    /**
     * 设备id
     */
    private String deviceId;

    public DeviceBindingPresenter(Context context, DeviceBindingView deviceBindingView) {
        this.context = context;
        this.deviceBindingView = deviceBindingView;
        dataManager = new DataManager(context);
        this.compositeSubscription = new CompositeSubscription();
    }

    /**
     * 停止获取二维码
     */
    public void stop() {
        unRegisterWifiReceiver();
        //停止计时
        stopRxTimer();
    }

    /**
     * 注册wifi连接上广播监听
     *
     * @return 之前是否注册过
     */
    public boolean registerWifiReceiver() {
        if (isRegisterReceiver) {
            return true;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(wifiReceiver, filter);
        isRegisterReceiver = true;
        return false;
    }

    /**
     * 反注册wifi连接上广播监听-在view或者activity destory时调用
     */
    private void unRegisterWifiReceiver() {
        if (null != wifiReceiver) {
            context.unregisterReceiver(wifiReceiver);
        }
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                int netWorkState = Tool.getNetWorkState(context);
                //网络断开，停止获取二维码
                if (Tool.NETWORK_NONE == netWorkState) {
                    LogUtil.LOG_BIND("WIFI不可用", netWorkState);
                    stopRxTimer();
                    deviceBindingView.updateFailQR();
                }
                //网络连接成功，开始获取二维码
                else {
                    LogUtil.LOG_BIND("WIFI已连接", netWorkState);
                    startRxTimer();
                }
            }
        }
    };

    public void setData(String productId, String authorizeCode, final String deviceId) {
        this.productId = productId;
        this.authorizeCode = authorizeCode;
        this.deviceId = deviceId;

        LogUtil.LOG_BIND("productId", productId);
        LogUtil.LOG_BIND("authorizeCode", authorizeCode);
        LogUtil.LOG_BIND("deviceId", deviceId);
    }

    /**
     * 获取扫描二维码
     */
    public void getDeviceSubQR() {
        if (!Tool.isNetworkAvailable(context)) {
            deviceBindingView.updateFailQR();
        } else {
            DeviceControl.getQRCode();
        }

//        String macAddress = Tool.getLocalMacAddress();
//        if (compositeSubscription == null) {
//            compositeSubscription = new CompositeSubscription();
//        }
//        compositeSubscription.add(dataManager.getDeviceLoginToken(productId, macAddress, authorizeCode)
//                .flatMap(new Func1<DeviceloginToken, Observable<DeviceSubQR>>() {
//                    @Override
//                    public Observable<DeviceSubQR> call(DeviceloginToken devicelogin) {
//                        String token = devicelogin.getAccessToken();
//                        LogUtil.LOG_BIND("deviceLogin token", token);
//                        return dataManager.getDeviceSubQR(deviceId + "", token);
//
//                    }
//                })
//                //                .retryWhen(new RetryWithDelay())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<DeviceSubQR>() {
//                    @Override
//                    public void onCompleted() {
//                        //                        LogUtil.LOG_BIND("getDeviceSubQR", "onCompleted");
//                        deviceBindingView.updateSuccessQR(bitmapQR);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtil.LOG_BIND("getDeviceSubQR", "onError:" + e.toString());
//                        deviceBindingView.updateFailQR();
//                    }
//
//                    @Override
//                    public void onNext(DeviceSubQR deviceSubQR) {
//                        LogUtil.LOG_BIND("getDeviceSubQR", "onNext二维码:" + deviceSubQR.toString());
//                        String deviceId = deviceSubQR.getDeviceId();
//                        String qrcode = deviceSubQR.getQrcode();
//                        String businessId = deviceSubQR.getBusinessId();
//
//                        String result = getQRCodeStr(deviceId, qrcode, businessId);
//                        bitmapQR = QRCodeUtil.createQRCodeBitmap(result, 250);
//                    }
//                }));
    }

    private String getQRCodeStr(String deviceId, String qrcode, String businessId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("qrcode", qrcode);
            jsonObject.put("businessId", businessId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 每隔8分钟刷新一次
     */
    private void startRxTimer() {
        subscription = Observable.interval(0, 8, TimeUnit.MINUTES).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        //                        LogUtil.LOG_BIND("startRxTimer", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        //                        LogUtil.LOG_BIND("startRxTimer", "onError:" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Long aLong) {
                        //                        LogUtil.LOG_BIND("startRxTimer", "onNext");
                        getDeviceSubQR();
                    }
                });
    }

    /**
     * 停止计时器
     */
    private void stopRxTimer() {
        if (null != subscription && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
        if (null != compositeSubscription && compositeSubscription.hasSubscriptions()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }
    }

    /**
     * 连接失败自动重连机制
     */
    public class RetryWithDelay implements Func1<Observable<? extends Throwable>, Observable<?>> {
        @Override
        public Observable<?> call(Observable<? extends Throwable> attempts) {
            return attempts.zipWith(Observable.range(1, 3), new Func2<Throwable, Integer, Integer>() {
                @Override
                public Integer call(Throwable throwable, Integer i) {
                    return i;
                }
            }).flatMap(new Func1<Integer, Observable<? extends Long>>() {
                @Override
                public Observable<? extends Long> call(Integer retryCount) {
                    return Observable.timer(2, TimeUnit.SECONDS);
                }
            });
        }
    }
}
