package com.fotile.recipe.net.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.recipe.base.BaseRecipePresenter;
import com.fotile.recipe.bean.recipe.DRecipe;
import com.fotile.recipe.bean.recipe.DRecipeCategory;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.bean.recipe.RecipeBanner;
import com.fotile.recipe.bean.recipe.RecipeBannerId;
import com.fotile.recipe.bean.reponse.Response;
import com.fotile.recipe.net.modle.DataManager;
import com.fotile.recipe.net.view.BannerRecipeView;
import com.fotile.recipe.net.view.FavoriteRecipeView;
import com.fotile.recipe.net.view.LocalRecipeView;
import com.fotile.recipe.uitl.db.DataLocalBaseUtil;
import com.fotile.recipe.uitl.db.DataNetBaseUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 项目名称：X1.I
 * 创建时间：2019/1/22 16:24
 * 文件作者：yaohx
 * 功能描述：获取轮播菜谱\达人秀
 */
public class BannerRecipePresenter implements BaseRecipePresenter {

    private DataManager dataManager;
    /**
     * 本地菜谱数据库操作类
     */
    private DataLocalBaseUtil dataLocalBaseUtil;
    /**
     * 网络菜谱数据库操作类（达人秀、轮播位、我的上传）
     */
    private DataNetBaseUtil dataNetBaseUtil;

    private CompositeSubscription compositeSubscription;
    private Context context;

    /**
     * 轮播位回调view
     */
    private BannerRecipeView bannerRecipeView;

    /**
     * 轮播菜谱
     */
    private List<RecipeBanner> listBanner = new ArrayList<>();
    /**
     * 轮播菜谱详情
     */
    private Recipe bannerRecipe;

    public BannerRecipePresenter(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(CompositeSubscription compositeSubscription) {
        dataManager = new DataManager(context);
        this.compositeSubscription = compositeSubscription;
        dataLocalBaseUtil = DataLocalBaseUtil.getInstance();
        dataNetBaseUtil = DataNetBaseUtil.getInstance();
    }

    @Override
    public void attachLocalRecipeView(LocalRecipeView recipeView) {

    }

    @Override
    public void attachBannerRecipeView(BannerRecipeView bannerView) {
        this.bannerRecipeView = bannerView;
    }

    @Override
    public void attachFavoriteRecipeView(FavoriteRecipeView favoriteRecipeView) {

    }

    /**
     * 获取菜谱达人秀
     * @param name C2.i灶具达人秀
     */
    public void getRecipeAdults(String name){
        compositeSubscription.add(dataManager.getRecipeBannerOperateId(name)
                .flatMap(new Func1<Response<RecipeBannerId>, Observable<Response<RecipeBanner>>>() {
                    @Override
                    public Observable<Response<RecipeBanner>> call(Response<RecipeBannerId> recipeBannerIdResponse) {
                        List<RecipeBannerId> list = recipeBannerIdResponse.getList();
                        List<String> stringList = new ArrayList<>();
                        if (null != list && list.size() > 0) {
                            for (int j = 0; j < list.size(); j++) {
                                List<RecipeBannerId.ContentId> item = list.get(j).getContent();
                                if (null != item && item.size() > 0) {
                                    for (int i = 0; i < item.size(); i++) {
                                        String s = item.get(i).getId();
                                        stringList.add(s);
                                    }
                                }
                            }
                        }
                        return dataManager.getRecipeBanner(stringList);
                    }
                })
                .doOnNext(new Action1<Response<RecipeBanner>>() {
                    @Override
                    public void call(Response<RecipeBanner> recipeBannerResponse) {
                        if(null != listBanner){
                            listBanner.clear();
                        }
                        listBanner = recipeBannerResponse.getList();
                        //将数据保存到本地数据库
                        if(null != listBanner && listBanner.size() > 0){
                            //删除达人秀数据
                            dataNetBaseUtil.deleteRecipeBanner(true);
                            for (int i = 0; i < listBanner.size(); i++) {
                                RecipeBanner recipeBanner = listBanner.get(i);
                                if (!dataNetBaseUtil.isDbRecipeBanner(recipeBanner.getRecipeId())) {
                                    dataNetBaseUtil.insertRecipeBanner(recipeBanner);
                                }
                            }
                        }
                    }
                })
                //指定被订阅者在io线程池中执行
                .subscribeOn(Schedulers.io())
                //指定订阅者在main线程中执行
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<RecipeBanner>>() {
                    @Override
                    public void onCompleted() {
                        if(null != bannerRecipeView){
                            bannerRecipeView.onRecipeBannerSuccess(listBanner);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(null != bannerRecipeView){
                            bannerRecipeView.onRecipeBannerError(e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onNext(Response<RecipeBanner> recipeBannerResponse) {

                    }
                })
        );
    }

    /**
     * 获取菜谱轮播位
     * 有网络时去网络获取
     * 无网络时去本地数据库获取
     * @param name C2.i灶具轮播位
     */
    public void getRecipeBanner(String name){
        if(Tool.isNetworkAvailable(context)){
            compositeSubscription.add(dataManager.getRecipeBannerOperateId(name)
                    .flatMap(new Func1<Response<RecipeBannerId>, Observable<Response<RecipeBanner>>>() {
                        @Override
                        public Observable<Response<RecipeBanner>> call(Response<RecipeBannerId> recipeBannerIdResponse) {
                            List<RecipeBannerId> list = recipeBannerIdResponse.getList();
                            List<String> stringList = new ArrayList<>();
                            if (null != list && list.size() > 0) {
                                for (int j = 0; j < list.size(); j++) {
                                    List<RecipeBannerId.ContentId> item = list.get(j).getContent();
                                    if (null != item && item.size() > 0) {
                                        for (int i = 0; i < item.size(); i++) {
                                            String s = item.get(i).getId();
                                            stringList.add(s);
                                        }
                                    }
                                }
                            }
                            return dataManager.getRecipeBanner(stringList);
                        }
                    })
                    //指定被订阅者在io线程池中执行
                    .subscribeOn(Schedulers.io())
                    //指定订阅者在main线程中执行
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<RecipeBanner>>() {
                        @Override
                        public void onCompleted() {
                            if(null != bannerRecipeView){
                                bannerRecipeView.onRecipeBannerSuccess(listBanner);

                                int size = null == listBanner ? 0 : listBanner.size();
                                LogUtil.LOG_REQUEST("网络获取轮播菜谱size", size);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(null != bannerRecipeView){
                                bannerRecipeView.onRecipeBannerError(e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onNext(Response<RecipeBanner> recipeBannerResponse) {
                            if(null != listBanner){
                                listBanner.clear();
                            }
                            listBanner = recipeBannerResponse.getList();
                        }
                    })
            );
        }
        //无网络时去本地数据库获取
        else {
            getRecipeBannerFromDB();
        }
    }

    /**
     * 获取菜谱轮播位
     * 去本地数据库获取
     */
    private void getRecipeBannerFromDB(){
        //取本地数据库中的前四个分类下的第一个菜谱
        final int count = 4;

        listBanner.clear();
        //先获取菜谱分类
        List<DRecipeCategory> list = dataLocalBaseUtil.queryRecipeCategoryList();
        //取最大前四个类别
        int maxSize = list.size() > count ? count : list.size();
        List<DRecipeCategory> result = new ArrayList<DRecipeCategory>();
        for(int k = 0; k < maxSize; k++){
            result.add(list.get(k));
        }

        Observable.from(result)
                //先获取分类下的菜谱
                .flatMap(new Func1<DRecipeCategory, Observable<List<DRecipe>>>() {
                    @Override
                    public Observable<List<DRecipe>> call(DRecipeCategory dRecipeCategory) {
                        String categoryId = dRecipeCategory.getId();
                        return dataLocalBaseUtil.queryRecipeByCategoryId(categoryId);
                    }
                })
                //指定被订阅者在io线程池中执行
                .subscribeOn(Schedulers.io())
                //指定订阅者在main线程中执行
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<DRecipe>>() {
                    @Override
                    public void onCompleted() {
                        if(null != bannerRecipeView){
                            //攒足4条数据再通知UI
                            if(listBanner.size() >= count){
                                bannerRecipeView.onRecipeBannerSuccess(listBanner);

                                int size = null == listBanner ? 0 : listBanner.size();
                                LogUtil.LOG_REQUEST("数据库获取轮播菜谱size", size);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(null != bannerRecipeView){
                            bannerRecipeView.onRecipeBannerError(e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onNext(List<DRecipe> dRecipes) {
                        if(null != dRecipes && dRecipes.size() > 0){
                            //取每个分类下的第一个菜谱
                            DRecipe dRecipe = dRecipes.get(0);
                            RecipeBanner recipeBanner = dataLocalBaseUtil.convertToRecipeBanner(dRecipe);
                            listBanner.add(recipeBanner);
                        }
                    }
                });
    }

    /**
     * 获取轮播（达人秀）菜谱详情
     * 轮播位（达人秀）recipeBanner来自网络，则去网络获取详情
     * 轮播位（达人秀）recipeBanner来自数据库，则去数据库获取详情
     * @param recipeBanner
     */
    public void getRecipeBannerDetail(final RecipeBanner recipeBanner){
        bannerRecipe = null;
        if(null != recipeBanner){
            LogUtil.LOG_REQUEST("getRecipeBannerDetail",recipeBanner);
            String moudle = recipeBanner.getModule();
            String api = recipeBanner.getApi();
            String recipeId = recipeBanner.getRecipeId();
            //轮播菜谱来自网络运营位
            if(!TextUtils.isEmpty(moudle) && !TextUtils.isEmpty(api)){
                compositeSubscription.add(dataManager.getRecipeBannerDetail(moudle, api, recipeId)
                        //指定被订阅者在io线程池中执行
                        .subscribeOn(Schedulers.io())
                        //指定订阅者在main线程中执行
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Response<Recipe>>() {
                            @Override
                            public void onCompleted() {
                                if(null != bannerRecipeView){
                                    bannerRecipeView.onRecipeBannerDetailSuccess(bannerRecipe);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if(null != bannerRecipeView){
                                    bannerRecipeView.onRecipeBannerDetailError(e.getLocalizedMessage());
                                }
                            }

                            @Override
                            public void onNext(Response<Recipe> recipeResponse) {
                                List<Recipe> list = recipeResponse.getList();
                                if(null != list && list.size() > 0){
                                    bannerRecipe = list.get(0);
                                }
                            }
                        })
                );
            }
            //轮播菜谱来自本地数据库
            else {
                Recipe recipe = dataLocalBaseUtil.queryRecipeById(recipeId);
                if(null != bannerRecipeView){
                    if(null != recipe){
                        bannerRecipeView.onRecipeBannerDetailSuccess(recipe);
                    }
                    else {
                        bannerRecipeView.onRecipeBannerDetailError("数据库中无该菜谱数据");
                    }
                }
            }
        }
    }


    /**
     * 返回菜谱详情数据json
     */
    private Response<Recipe> recipeResponse;

    /**
     * 根据菜谱ID获取菜谱-给菜谱播放loading页面调用
     * @param recipeId 菜谱id
     * @param token 灶具上传菜谱token
     * @param deviceId 灶具deviceId
     */
    public void getRecipeById(String recipeId, String token ,String deviceId) {
        bannerRecipe = null;
        compositeSubscription.add(dataManager.getRecipeById(recipeId, token,deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Recipe>>() {
                    @Override
                    public void onCompleted() {
                        if(null != bannerRecipeView){
                            bannerRecipeView.onRecipeBannerDetailSuccess(bannerRecipe);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(null != bannerRecipeView){
                            bannerRecipeView.onRecipeBannerDetailError(e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onNext(Response<Recipe> response) {
                        List<Recipe> list = response.getList();
                        if(null != list && list.size() > 0){
                            bannerRecipe = list.get(0);
                        }
                    }
                }));
    }
}
