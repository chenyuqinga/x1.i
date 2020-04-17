package com.fotile.recipe.net.view;

import com.fotile.recipe.base.BaseRecipeView;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.bean.recipe.RecipeBanner;

import java.util.List;

/**
 * 项目名称：X1.I
 * 创建时间：2019/1/8 15:11
 * 文件作者：yaohx
 * 功能描述：轮播位（达人秀）相关数据操作回调view
 */
public interface BannerRecipeView extends BaseRecipeView {
    /**
     * 获取轮播位（达人秀）菜谱
     * @param list
     */
    void onRecipeBannerSuccess(List<RecipeBanner> list);
    void onRecipeBannerError(String e);

    /**
     * 获取轮播位（达人秀）菜谱详情
     *
     * 菜谱loading页网络菜谱
     *
     * @param recipe
     */
    void onRecipeBannerDetailSuccess(Recipe recipe);
    void onRecipeBannerDetailError(String e);
}
