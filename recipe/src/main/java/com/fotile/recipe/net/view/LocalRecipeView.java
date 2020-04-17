package com.fotile.recipe.net.view;

import com.fotile.recipe.base.BaseRecipeView;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.bean.recipe.RecipeBanner;
import com.fotile.recipe.bean.recipe.RecipeCategory;

import java.util.List;

/**
 * 项目名称：X1.I
 * 创建时间：2018/12/10 18:04
 * 文件作者：yaohx
 * 功能描述：本地菜谱相关数据操作回调view
 */
public interface LocalRecipeView extends BaseRecipeView {
    /**
     * 运营位id来获取设备对应的所有分类标签
     *
     * @param categoryList
     */
    void onRecipeCategoryListSuccess(List<RecipeCategory> categoryList);
    void onRecideCategoryListError(String e);

    /**
     * 获取分类下的菜谱列表Success
     * 根据关键
     *
     * @param recipeList
     */
    void onRecipeListSuccess(List<Recipe> recipeList);
    void onRecipeListError(String e);

}
