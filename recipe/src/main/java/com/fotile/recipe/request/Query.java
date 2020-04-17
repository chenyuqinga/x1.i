package com.fotile.recipe.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Query {
    @SerializedName("$or")
    private List<Object> or;

    @SerializedName("properties.label")

    private String propertitiesLabel;

    @SerializedName("name")
    private String categoryName;

    @SerializedName("_id")
    private String recipeId;

    @SerializedName("classification.sub.id")
    private String classificationSubId;

    @SerializedName("devices.device_id")
    private GetSourceRecipeListRequest._Id id;

    @SerializedName("devices.products.id")
    private String devicesProductsId;

    @SerializedName("create_time")
    private CreateTime createTime;

    public String getDevicesProductsId() {
        return devicesProductsId;
    }
    public void setDevicesProductsId(String devicesProductsId) {
        this.devicesProductsId = devicesProductsId;
    }

    public GetSourceRecipeListRequest._Id getId() {
        return id;
    }

    public void setId(GetSourceRecipeListRequest._Id id) {
        this.id = id;
    }

    public List<Object> getOr() {
        return or;
    }

    public void setOr(List<Object> or) {
        this.or = or;
    }

    public String getPropertitiesLabel() {
        return propertitiesLabel;
    }

    public void setPropertitiesLabel(String propertitiesLabel) {
        this.propertitiesLabel = propertitiesLabel;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getClassificationSubId() {
        return classificationSubId;
    }

    public void setClassificationSubId(String classificationSubId) {
        this.classificationSubId = classificationSubId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public CreateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(CreateTime createTime) {
        this.createTime = createTime;
    }

    class CreateTime{
        @SerializedName("$gte")
        private String time;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
