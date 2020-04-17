package com.fotile.recipe.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 文件名称：GetBannerRequest
 * 创建时间：17-9-22 下午2:39
 * 文件作者：zhangqiang
 * 功能描述：请求菜谱轮播json
 */
public class GetBannerRequest {

    public String createRequest(List<String> list) {


        _Id id = new _Id();
        id.set$in(list);

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
        private _Id id;

        public _Id getId() {
            return id;
        }

        public void setId(_Id id) {
            this.id = id;
        }
    }

    private static class _Id {
        private List<String> $in;

        public List<String> get$in() {
            return $in;
        }

        public void set$in(List<String> $in) {
            this.$in = $in;
        }
    }

}
