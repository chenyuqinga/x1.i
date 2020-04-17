package com.fotile.recipe.request;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 文件名称：GetRecipeRequest
 * 创建时间：17-9-28 下午2:26
 * 文件作者：zhangqiang
 * 功能描述：菜谱轮播的请求json
 */
public class GetRecipeRequest {

    public String createRequest(String id) {

        Query query = new Query();
        query.setId(id);

        Request request = new Request();
        request.setQuery(query);

        Gson gson = new Gson();
        return gson.toJson(request);
    }

    private static class Request {
        private Query query;

        public Query getQuery() {
            return query;
        }

        public void setQuery(Query query) {
            this.query = query;
        }
    }

    private static class Query {

        @SerializedName("_id")
        @Expose
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
