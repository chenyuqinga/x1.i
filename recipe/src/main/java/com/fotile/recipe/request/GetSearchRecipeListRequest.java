package com.fotile.recipe.request;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名称：GetSearchRecipeListRequest
 * 创建时间：2017/8/7
 * 文件作者：zhaoqingjing
 * 功能描述：请求搜索的json
 */
public class GetSearchRecipeListRequest {

    public String createRequest(String key, int page, String devicesProductsId) {
        Regex recipeLabel = new Regex();
        recipeLabel.setRegex(key);
        recipeLabel.setOption("i");

        RecipeLabelObj recipeLabelObj = new RecipeLabelObj();
        recipeLabelObj.setRecipeLabel(recipeLabel);

        Regex recipeName = new Regex();
        recipeName.setRegex(key);
        recipeName.setOption("i");

        RecipeNameObj recipeNameObj = new RecipeNameObj();
        recipeNameObj.setRecipeName(recipeName);

        List<Object> list = new ArrayList();
        list.add(recipeLabelObj);
        list.add(recipeNameObj);

        Query query = new Query();
        query.setOr(list);
        query.setDevicesProductsId(devicesProductsId);

        Request request = new Request();
        request.setQuery(query);
        request.setLimit("10");
        request.setOffset("" + page * 10);

        Gson gson = new Gson();
        return gson.toJson(request);
    }
}
