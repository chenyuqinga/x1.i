package com.fotile.recipe.request;

import com.google.gson.Gson;

/**
 * 文件名称：GetDeviceParentIdRequest
 * 创建时间：2017/8/7
 * 文件作者：zhaoqingjing
 * 功能描述：请求设备的parentid
 */
public class GetDeviceParentIdRequest {

    public String createRequest(String name) {
        Query query = new Query();
        query.setCategoryName(name);

        Request request = new Request();
        request.setQuery(query);

        Gson gson = new Gson();
        return gson.toJson(request);
    }
}
