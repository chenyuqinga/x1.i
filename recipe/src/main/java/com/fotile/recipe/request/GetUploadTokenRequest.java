package com.fotile.recipe.request;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 文件名称：GetUploadTokenRequest
 * 创建时间：17-9-21 下午9:58
 * 文件作者：zhangqiang
 * 功能描述：請求菜谱轮播或上传的token的json
 */
public class GetUploadTokenRequest {

    public String createRequest(String id) {
        Query query = new Query();
        query.setAppId(id);
        Gson gson = new Gson();
        return gson.toJson(query);
    }


    private static class Query {
        @SerializedName("app_id")
        @Expose
        private String appId;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }
    }
}
