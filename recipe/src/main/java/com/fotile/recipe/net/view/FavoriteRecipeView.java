package com.fotile.recipe.net.view;

import com.fotile.recipe.base.BaseRecipeView;
import com.fotile.recipe.bean.recipe.Recipe;

import java.util.List;

/**
 * 项目名称：X1.I
 * 创建时间：2019/1/22 16:36
 * 文件作者：yaohx
 * 功能描述：我的收藏（我的上传）菜谱相关数据操作回调view
 */
public interface FavoriteRecipeView extends BaseRecipeView {

    /**
     * 获取收藏菜谱
     *
     * @param list
     */
    void onFavoriteListSuccess(List<Recipe> list);

    /**
     * 获取自制菜谱
     *
     * @param list
     */
    void onUploadRecipeListSuccess(List<Recipe> list);

    void onUploadRecipeListError(String e);

    /**
     * 删除自制菜谱
     */
    void onUploadRecipeDeleteSuccess();

    void onUploadRecipeDeleteError(String e);
}
