package com.fotile.recipe.request;

import com.google.gson.Gson;

/**
 * 文件名称：GetRecipeCategoryListRequest
 * 创建时间：2017/8/7
 * 文件作者：zhaoqingjing
 * 功能描述：请求菜谱分类json
 */
public class GetRecipeCategoryListRequest {

    public String createRequest(String pid) {
        ParentId parentId = new ParentId();
        parentId.set$eq(pid);

        Query query = new Query();
        query.setParent_id(parentId);

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
        private ParentId parent_id;

        public ParentId getParent_id() {
            return parent_id;
        }

        public void setParent_id(ParentId parent_id) {
            this.parent_id = parent_id;
        }
    }

    private static class ParentId {
        private String $eq;

        public String get$eq() {
            return $eq;
        }

        public void set$eq(String $eq) {
            this.$eq = $eq;
        }
    }
}
