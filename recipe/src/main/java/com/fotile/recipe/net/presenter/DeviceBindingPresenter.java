package com.fotile.recipe.net.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;

import com.fotile.common.base.BasePresenter;
import com.fotile.common.base.BaseView;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.recipe.bean.device.DeviceSubQR;
import com.fotile.recipe.bean.token.DeviceloginToken;
import com.fotile.recipe.net.modle.DataManager;
import com.fotile.recipe.net.view.DeviceBindingView;
import com.fotile.recipe.uitl.QRCodeUtil;
import com.fotile.recipe.uitl.RecipeConstant;

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
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称：DeviceBindingPresenter
 * 创建时间： 2018/11/14
 * 文件作者：huanghuang
 * 功能描述：设备扫码订阅Presenter
 */
public class DeviceBindingPresenter implements BasePresenter {

    private final static String TAG = "设备二维码";

    private CompositeSubscription compositeSubscription;
    private DeviceBindingView deviceBindingView;
    private DataManager dataManager;
    private Context context;
    private Subscription subscription;
    private Bitmap bitmapQR;
    private Boolean isRegisterReceiver = false;

    public DeviceBindingPresenter(Context context) {
        this.context = context;
        dataManager = new DataManager(context);
    }

    @Override
    public void onCreate(CompositeSubscription compositeSubscription) {
        this.compositeSubscription = compositeSubscription;
    }

    @Override
    public void attachView(BaseView baseView) {
        deviceBindingView = (DeviceBindingView) baseView;
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
     * 反注册wifi连接上广播监听
     */
    public void unRegisterWifiReceiver() {
        if (null != wifiReceiver && isRegisterReceiver) {
            LogUtil.LOGE(TAG, "unRegisterWifiReceiver");
            context.unregisterReceiver(wifiReceiver);
            isRegisterReceiver = false;
        }
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                int netWorkState = Tool.getNetWorkState(context);
                //网络断开，停止获取二维码
                if (Tool.NETWORK_NONE == netWorkState) {
                    LogUtil.LOGE(TAG, "WIFI不可用");
                    stopRxTimer();
                    deviceBindingView.updateFailQR(bitmapQR);
                }
                //网络连接成功，开始获取二维码
                else {
                    LogUtil.LOGE(TAG, "WIFI已连接");
                    startRxTimer();
                }
            }
        }
    };

    /**
     * 获取扫描二维码
     */
    public void getDeviceSubQR() {
        String id = RecipeConstant.LAMPBLACK_PRODUCT_ID;
        String macAddress = Tool.getLocalMacAddress();
        String authorizeCode = DeviceControl.getInstance().getAuthCode();
        LogUtil.LOGE(TAG, "authorizeCode:" + authorizeCode);

        compositeSubscription.add(dataManager.getDeviceLoginToken(id, macAddress, authorizeCode)
                .flatMap(new Func1<DeviceloginToken, Observable<DeviceSubQR>>() {
                    @Override
                    public Observable<DeviceSubQR> call(DeviceloginToken devicelogin) {
                        String token = devicelogin.getAccessToken();
                        LogUtil.LOGE(TAG, "deviceLogin token:" + token);
                        return dataManager.getDeviceSubQR(DeviceControl.getInstance().getDeviceID() + "", token);
                    }
                })
                .retryWhen(new RetryWithDelay())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DeviceSubQR>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.LOGE(TAG, "SubQR   onCompleted");
                        deviceBindingView.updateSuccessQR(bitmapQR);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.LOGE(TAG, "SubQR    onError     e:" + e.toString());
                        deviceBindingView.updateFailQR(bitmapQR);
                    }

                    @Override
                    public void onNext(DeviceSubQR deviceSubQR) {
                        LogUtil.LOGE(TAG, "获取二维码接口result:" + deviceSubQR.toString());
                        String deviceId = deviceSubQR.getDeviceId();
                        String qrcode = deviceSubQR.getQrcode();
                        String businessId = deviceSubQR.getBusinessId();

                        String result = getQRCodeStr(deviceId, qrcode, businessId);
                        bitmapQR = QRCodeUtil.createQRCodeBitmap(result, 250);
                    }
                }));
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
    public void startRxTimer() {
        if (null == subscription)
            LogUtil.LOGE(TAG, "startRxTimer");
        subscription = Observable.interval(0, 8, TimeUnit.MINUTES).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.LOGE(TAG, "Timer   onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.LOGE(TAG, "Timer   onError");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        LogUtil.LOGE(TAG, "onNext    aLong:" + aLong);
                        getDeviceSubQR();
                    }
                });
    }

    /**
     * 停止计时器
     */
    public void stopRxTimer() {
        if (null != subscription && !subscription.isUnsubscribed()) {
            LogUtil.LOGE(TAG, "stopRxTimer");
            subscription.unsubscribe();
            subscription = null;
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
