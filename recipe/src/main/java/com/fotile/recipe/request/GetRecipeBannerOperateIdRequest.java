package com.fotile.recipe.request;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 文件名称：GetRecipeBannerOperateIdRequest
 * 创建时间：17-9-21 下午4:11
 * 文件作者：zhangqiang
 * 功能描述：获取菜谱运营位ID的json
 */
public class GetRecipeBannerOperateIdRequest {

    public String createRequest(String name) {

        Query query = new Query();
        query.setName(name);

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
        @SerializedName("name")
        @Expose
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
