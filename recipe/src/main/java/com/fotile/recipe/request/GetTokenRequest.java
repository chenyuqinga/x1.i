package com.fotile.recipe.request;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 文件名称：GetTokenRequest
 * 创建时间：17-9-21 下午8:28
 * 文件作者：zhangqiang
 * 功能描述：获取设备的token
 */
public class GetTokenRequest {

    public String createRequest(String id, String mac, String authorizeCode) {
        Query query = new Query();
        query.setMac(mac);
        query.setAuthorizeCode(authorizeCode);
        query.setProductId(id);

        Gson gson = new Gson();
        String result = gson.toJson(query);
        return result;
    }


    private class Query {
        //json中的字段为"mac"
        @SerializedName("mac")
        @Expose
        private String mac;

        @SerializedName("authorize_code")
        @Expose
        private String authorizeCode;

        @SerializedName("product_id")
        @Expose
        private String productId;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getAuthorizeCode() {
            return authorizeCode;
        }

        public void setAuthorizeCode(String authorizeCode) {
            this.authorizeCode = authorizeCode;
        }
    }
}
