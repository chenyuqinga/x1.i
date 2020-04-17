package com.fotile.recipe.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * 文件名称：GetRecipeListRequest
 * 创建时间：2017/8/7
 * 文件作者：zhaoqingjing
 * 功能描述：请求菜谱json
 */
public class GetRecipeListRequest {

    public String createRequest(String classificationSubId, int preset) {
        Query query = new Query();
        query.setClassificationSubId(classificationSubId);
//        query.setPreset(preset);

        Request request = new Request();
        request.setQuery(query);

        Gson gson = new Gson();
        return gson.toJson(request);
    }


    class Query {
        @SerializedName("classification.sub.id")
        private String classificationSubId;

        /**
         * 审核状态（0表示待审核，1表示已发布，2表示审核失败，3表示审核中）
         */
//        @SerializedName("status")
//        private int status;

        /**
         * 上架状态（1表示上架，2表示下架，3表示草稿）
         */
//        @SerializedName("otstatus")
//        private int otstatus;

//        /**
//         * 菜谱预置（0否，1是）
//         */
//        @SerializedName("preset")
//        private int preset;

//        public int getPreset() {
//            return preset;
//        }
//
//        public void setPreset(int preset) {
//            this.preset = preset;
//        }


        public String getClassificationSubId() {
            return classificationSubId;
        }

        public void setClassificationSubId(String classificationSubId) {
            this.classificationSubId = classificationSubId;
        }

      /*  public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getOtstatus() {
            return otstatus;
        }

        public void setOtstatus(int otstatus) {
            this.otstatus = otstatus;
        }*/
    }

    class Request {
        private Query query;

        public void setQuery(Query query) {
            this.query = query;
        }
    }
}
