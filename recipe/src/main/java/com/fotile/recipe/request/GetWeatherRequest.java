package com.fotile.recipe.request;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 文件名称：GetWeatherRequest
 * 创建时间：2017-11-08 09:19
 * 文件作者：shihuijuan
 * 功能描述：请求天气信息
 */

public class GetWeatherRequest {
    public String createRequest(String city) {

        Query query = new Query();
        query.setCity(city);

        Gson gson = new Gson();
        return gson.toJson(query);
    }


    private static class Query {
        @SerializedName("city")
        @Expose
        private String city;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }
}
