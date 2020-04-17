package com.fotile.recipe.request;

import com.google.gson.Gson;

/**
 * 文件名称：GetRecipeListRequest
 * 创建时间：2017/8/7
 * 文件作者：zhaoqingjing
 * 功能描述：请求菜谱json
 */

public class GetRecipeByIdRequest {

    public String createRequest(String id) {

        Query query = new Query();
        query.setRecipeId(id);

        Request request = new Request();
        request.setQuery(query);

        Gson gson = new Gson();
        return gson.toJson(request);
    }
}
