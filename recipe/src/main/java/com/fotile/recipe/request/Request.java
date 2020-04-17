package com.fotile.recipe.request;

public class Request {
    private Query query;
    private String offset;
    private String limit;
    private GetSourceRecipeListRequest.Order order;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public void setOrder(GetSourceRecipeListRequest.Order order) {
        this.order = order;
    }
}
