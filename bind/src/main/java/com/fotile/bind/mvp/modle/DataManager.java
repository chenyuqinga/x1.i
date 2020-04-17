package com.fotile.bind.mvp.modle;

import android.content.Context;

import com.fotile.bind.bean.DeviceSubQR;
import com.fotile.bind.bean.DeviceloginToken;
import com.fotile.bind.bean.request.GetTokenRequest;
import com.fotile.bind.mvp.ServerUrl;
import com.fotile.bind.util.BindConstant;
import com.fotile.common.util.log.LogUtil;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;

/**
 * 项目名称：X1.I
 * 创建时间：2018/12/7 15:07
 * 文件作者：yaohx
 * 功能描述：管理网络请求入口
 */
public class DataManager {
    Context context;

    //根据设备获取设备的token
    private RetrofitService deviceTokenService;

    public DataManager(Context context) {
        deviceTokenService = RetrofitHelper.getInstance(context).getDeviceTokenService();
    }

    /**
     * 根据设备获取设备的token
     * @param id            设备的产品id
     * @param mac           mac地址
     * @param authorizeCode 认证码
     * @return
     */
    public Observable<DeviceloginToken> getDeviceLoginToken(String id, String mac, String authorizeCode) {
        GetTokenRequest request = new GetTokenRequest();
        String json = request.createRequest(id, mac, authorizeCode);
        RequestBody body = createRequestBody(json);

        String url = RetrofitHelper.getInstance(context).getDeviceTokenUrl();
        LogUtil.LOG_BIND("获取设备token的url", url + "device_login");
        LogUtil.LOG_BIND("获取设备token的body", json);
        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        //正式环境或者测试环境
        return BindConstant.TYPE_REQUEST_ONLINE == requestType ? deviceTokenService.getLoginTokenOnLine(body) :
                deviceTokenService.getLoginTokenTest(body);
    }


    /**
     * 获取扫码订阅二维码
     *
     * @param deviceId
     * @param token
     * @return
     */
    public Observable<DeviceSubQR> getDeviceSubQR(String deviceId, String token) {
        String url = RetrofitHelper.getInstance(context).getDeviceTokenUrl();
        LogUtil.LOG_BIND( "获取二维码接口地址" ,url + ServerUrl.SUB_QR);
        LogUtil.LOG_BIND("获取二维码接口deviceId" , deviceId);
        LogUtil.LOG_BIND("获取二维码接口token" , token);

        int requestType = RetrofitHelper.getInstance(context).getRequestType();
        return BindConstant.TYPE_REQUEST_ONLINE == requestType ? deviceTokenService.getDeviceSubQROnline(deviceId, token)
                : deviceTokenService.getDeviceSubQRTest(deviceId, token);
    }

    /**
     * 构建json实体
     */
    private RequestBody createRequestBody(String json) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        return body;
    }
}
