/*
 * ************************************************************
 * 文件：SpeechService.java  模块：voice  项目：X1.I
 * 作者：coiliaspp
 * 杭州方太智能科技有限公司  https://www.fotile.com
 * Copyright (c) 2019
 * ************************************************************
 */

package com.fotile.voice.net;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SpeechService {

@POST("vpa/helper/")
    Call<ResponseBody> getHelpContent(@Body RequestBody body);
}
