package com.fotile.recipe.base;


import com.fotile.recipe.net.view.BannerRecipeView;
import com.fotile.recipe.net.view.FavoriteRecipeView;
import com.fotile.recipe.net.view.LocalRecipeView;

import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称：BaseMusicPresenter
 * 创建时间：2017/8/7 14:40
 * 文件作者：yaohx
 * 功能描述：
 */
public interface BaseRecipePresenter {
    /**
     * 完成Presenter的一些初始化工作
     *
     * @param compositeSubscription
     */
    void onCreate(CompositeSubscription compositeSubscription);

    void attachLocalRecipeView(LocalRecipeView localRecipeView);

    void attachBannerRecipeView(BannerRecipeView bannerRecipeView);

    void attachFavoriteRecipeView(FavoriteRecipeView favoriteRecipeView);

}
