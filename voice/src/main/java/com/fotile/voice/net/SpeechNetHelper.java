/*
 * ************************************************************
 * 文件：SpeechNetHelper.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.net;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.fotile.voice.CommonConst;
import com.fotile.voice.bean.SpeechHelpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpeechNetHelper {

    private static final String TAG = SpeechNetHelper.class.getSimpleName();
    private SpeechContentGetListener mListener;

    public SpeechNetHelper(SpeechContentGetListener listener) {
        mListener = listener;
    }

    public void getSpeechContent() {
        JsonObject object = new JsonObject();
        object.addProperty("product_name", "欧近");
        //        object.addProperty("skill_name", "设备控制");
        final String json = new Gson().toJson(object);
        Log.e(TAG, "json: " + json);
        Call<ResponseBody> helpContent = getSpeechService().getHelpContent(createRequestBody(json));
        helpContent.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    SpeechHelpResponse speechHelpResponse = null;
                    String bodyJson = null;
                    try {
                        bodyJson = response.body().string();
                    } catch (Exception e) {
                        Log.e(TAG, "exception: " + e);
                        e.printStackTrace();
                    }
                    if (!TextUtils.isEmpty(bodyJson)) {
                        bodyJson = bodyJson.replaceAll("\\s*", "");
                    }
                    Log.e(TAG, "response.body: " + bodyJson);
                    speechHelpResponse = JSONObject.parseObject(bodyJson, SpeechHelpResponse.class);
                    Log.e(TAG, "speechHelpResponse: " + speechHelpResponse);
                    if (speechHelpResponse != null) {
                        Log.e(TAG, "net result: " + speechHelpResponse.toString());
                        if (speechHelpResponse.getCode() == 0) {
                            if (mListener != null) {
                                mListener.onContentGet(speechHelpResponse.getData());
                            }
                        } else {
                            Log.e(TAG, "get content fail!!!");
                            if (mListener != null) {
                                mListener.onGetFail();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e(TAG, "get content fail: " + throwable);
                if (mListener != null) {
                    mListener.onGetFail();
                }
            }
        });
    }

    private SpeechService getSpeechService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(CommonConst.BASE_URL).client(
                new OkHttpClient()).addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        SpeechService speechService = retrofit.create(SpeechService.class);
        return speechService;
    }

    /**
     * 构建json实体
     */
    private RequestBody createRequestBody(String json) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                json);
        return body;
    }

    public interface SpeechContentGetListener {
        void onContentGet(List<String> list);

        void onGetFail();
    }

}
