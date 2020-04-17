package com.fotile.recipe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.recipe.bean.token.DeviceloginToken;
import com.fotile.recipe.bean.token.UploadRecipeToken;
import com.fotile.recipe.net.ServerUrl;
import com.fotile.recipe.net.modle.DataManager;
import com.fotile.recipe.net.modle.RetrofitHelper;
import com.fotile.recipe.uitl.RecipeConstant;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.fotile.common.util.PreferenceUtil.STOVE_AUTHCODE;
import static com.fotile.common.util.PreferenceUtil.STOVE_DEVICE_ID;
import static com.fotile.common.util.PreferenceUtil.STOVE_MAC;

/**
 * 文件名称：RecipesTokenRequest
 * 创建时间：17-9-25 下午6:53
 * 文件作者：zhangqiang
 * 功能描述：用于定时请求Token的服务
 */
public class RecipesTokenRequestService extends Service {

    private static final String TAG = "RecipesTokenRequestService";
    /**
     * 灶具mac地址
     */
    private String stoveAddress;
    private String stoveAuthorizeCode;

    final String TEST_STOVE_ADDRESS = "047863a0239b";
    final String TEST_STOVE_AUTHCODE = "554d4457526d684e424d725750307570";
    final String TEST_STOVE_DEVICEID = "1afe8283";

    /**
     * Rxjava
     */
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    /**
     * 管理网络请求入口
     */
    private DataManager dataManager;

    /**
     * 登录设备的id与token
     */
    private DeviceloginToken deviceloginToken;
    /**
     * 我的上传菜谱token
     */
    private UploadRecipeToken uploadRecipeToken;

    /**
     * 每小時请求一次token
     */
    private int TIME = 60 * 60 * 1000;

    private Context context;
    /**
     * 记录上一次的网络状态
     */
    private NetworkInfo.State lastState = null;

    private static final int UPDATE_TOKEN_UPLOADRECIPE = 2001;

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        registerWifiReceiver();
    }

    private void initData() {
        //启动服务时，重置token
        PreferenceUtil.setStoveUploadRecipeToken(this, "");
        context = this;
        dataManager = new DataManager(this);
        getStoveInfo();
        // 每小时更新一次我的上传菜谱token
        handler.postDelayed(runnable, 10000);
    }

    /**
     * 获取灶具mac地址和authcode
     */
    private void getStoveInfo() {
        //灶具mac地址
        stoveAddress = (String) PreferenceUtil.getPreferenceValue(this, STOVE_MAC, "");
        //灶具authorcode
        //authorizeCode = Tool.parseAscii(CookerProtocol.getAuthCode());
        stoveAuthorizeCode = Tool.convertHexToASCII((String) PreferenceUtil.getPreferenceValue(this,
                STOVE_AUTHCODE, ""));

    }

    //2小時请求一次token
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                //获取我的上传菜谱token
                getStoveUploadRecipeToken();
                handler.postDelayed(this, TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //网络连接状态改变
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != info) {
                    NetworkInfo.State state = info.getState();
                    if (state == NetworkInfo.State.CONNECTED && (null == lastState || lastState != state)){
                        handler.sendEmptyMessageDelayed(UPDATE_TOKEN_UPLOADRECIPE, 1000);
                    }
                    lastState = state;
                }
            }
            //灶具蓝牙状态改变
            else if (RecipeConstant.ACTION_UPDATE_STOVE_INFO.equals(action)) {
                getStoveInfo();
                handler.sendEmptyMessageDelayed(UPDATE_TOKEN_UPLOADRECIPE, 1000);
            }
        }
    };

    /**
     * 注册wifi连接上广播监听
     */
    private void registerWifiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        //设置接收广播的类型
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //蓝牙连接状态改变
        intentFilter.addAction(RecipeConstant.ACTION_UPDATE_STOVE_INFO);
        //调用Context的registerReceiver（）方法进行动态注册
        registerReceiver(wifiReceiver, intentFilter);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //更新我的上传菜谱token
                case UPDATE_TOKEN_UPLOADRECIPE:
                    getStoveUploadRecipeToken();
                    break;
            }
            return false;
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    /**
     * 先获取灶具登录请求的token
     * 再获取我的上传菜谱token
     */
    private void getStoveUploadRecipeToken() {
        if (!Tool.isNetworkAvailable(this)){
            return;
        }
        if(TextUtils.isEmpty(stoveAddress) || TextUtils.isEmpty(stoveAuthorizeCode)){
            return;
        }

        //先获取灶具登录请求的token
        compositeSubscription.add(dataManager.getDeviceLoginToken(RecipeConstant.STOVE_PRODUCT_ID, stoveAddress, stoveAuthorizeCode)
                        .flatMap(new Func1<DeviceloginToken, Observable<UploadRecipeToken>>() {
                            @Override
                            public Observable<UploadRecipeToken> call(DeviceloginToken deviceloginToken) {
                                //请求上传菜谱token的APP_ID
                                int requestType = RetrofitHelper.getInstance(RecipesTokenRequestService.this).getRequestType();
                                String appId = RecipeConstant.TYPE_REQUEST_ONLINE == requestType ? ServerUrl.MYUP_TOKEN_APP_ID : ServerUrl.MYUP_TOKEN_APP_ID_TEST;
                                //灶具deviceId
                                String deviceId = (String) PreferenceUtil.getPreferenceValue(context, STOVE_DEVICE_ID,"");
                                RecipesTokenRequestService.this.deviceloginToken = deviceloginToken;
                                //获取设备token之后再去获取上传菜谱的token
                                return dataManager.getUploadRecipeToken(appId, deviceloginToken.getAccessToken(), deviceId);
                            }
                        })
                        //指定被订阅者在io线程池中执行
                        .subscribeOn(Schedulers.io())
                        //指定订阅者在main线程中执行
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<UploadRecipeToken>() {
                            @Override
                            public void onCompleted() {
                                //if (MYUP_TOKEN_APP_ID.equals(applyToken.getAppId())) {
                                String myUpToken = uploadRecipeToken.getAccessToken();
                                PreferenceUtil.setStoveUploadRecipeToken(RecipesTokenRequestService.this, myUpToken);
                                //} else if (BANNER_APP_ID.equals(applyToken.getAppId())) {
                                //bannerToken = applyToken.getAccessToken();
                                //}
                                //((BaseApplication) getApplication()).setBannerToken(bannerToken);
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtil.LOG_REQUEST("获取菜谱token返回数据error",e);
//                        requestToken();
                            }

                            @Override
                            public void onNext(UploadRecipeToken uploadRecipeToken) {
                                RecipesTokenRequestService.this.uploadRecipeToken = uploadRecipeToken;
                                LogUtil.LOG_REQUEST("获取菜谱token返回数据success", uploadRecipeToken);
                            }
                        })
        );
    }
}
