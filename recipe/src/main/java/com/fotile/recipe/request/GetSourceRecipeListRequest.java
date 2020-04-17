package com.fotile.recipe.request;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名称：GetSourceRecipeListRequest
 * 创建时间：2017/9/01
 * 文件作者：yujiaying
 * 功能描述：根据source获取菜谱
 */

public class GetSourceRecipeListRequest {

    /**
     * 每页限制的数据数量
     */
    private static final int ITEM_LIMIT_EACH_PAGE = 20;
    /**
     * 创建根据source获取菜谱的请求json
     * @param source 菜谱来源
     * @param page 请求第page页
     * @return
     */
    public String createRequest(String source, int page) {
        List<String> list = new ArrayList<>();
        list.add(source);

        _Id id = new _Id();
        id.set$in(list);

        Query query = new Query();
        query.setId(id);

        Order order = new Order();
        order.setCreate_time(-1);

        Request request = new Request();
        request.setQuery(query);
        request.setLimit(""+ITEM_LIMIT_EACH_PAGE);
        request.setOffset("" + page * ITEM_LIMIT_EACH_PAGE);
        request.setOrder(order);

        Gson gson = new Gson();
        return gson.toJson(request);


    }

    public static class _Id {
        private List<String> $in;

        public List<String> get$in() {
            return $in;
        }

        public void set$in(List<String> $in) {
            this.$in = $in;
        }
    }

    public static class Order {
        private int create_time;

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }
    }
}
