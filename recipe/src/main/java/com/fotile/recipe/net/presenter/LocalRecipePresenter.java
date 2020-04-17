package com.fotile.recipe.net.presenter;

import android.content.Context;

import com.fotile.common.util.Tool;
import com.fotile.recipe.base.BaseRecipePresenter;
import com.fotile.recipe.bean.recipe.DRecipe;
import com.fotile.recipe.bean.recipe.DRecipeCategory;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.bean.recipe.RecipeCategory;
import com.fotile.recipe.bean.reponse.Error;
import com.fotile.recipe.bean.reponse.Response;
import com.fotile.recipe.net.modle.DataManager;
import com.fotile.recipe.net.view.BannerRecipeView;
import com.fotile.recipe.net.view.FavoriteRecipeView;
import com.fotile.recipe.net.view.LocalRecipeView;
import com.fotile.recipe.uitl.RecipeConstant;
import com.fotile.recipe.uitl.db.DataLocalBaseUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.fotile.recipe.uitl.RecipeConstant.LOCAL_RECIPE_FROM_NET;

/**
  * 文件名称：LocalRecipePresenter
  * 创建时间：2019/1/22 15:34
  * 文件作者：yaohx
  * 功能描述：获取本地菜谱
  */
public class LocalRecipePresenter implements BaseRecipePresenter {
    /**
     * 菜谱预置（0否，1是）
     */
    private static final int PRESET = 1;

    private DataManager dataManager;
    /**
     * 本地菜谱数据库操作类
     */
    private DataLocalBaseUtil dataLocalBaseUtil;

    private CompositeSubscription compositeSubscription;
    private Context context;
    /**
     * 本地菜谱回调view
     */
    private LocalRecipeView localRecipeView;

    /**
     * 本地菜谱分类标签列表
     */
    private List<RecipeCategory> listCategory;
    /**
     * 本地菜谱列表数据
     */
    private List<Recipe> listRecipe;

    public LocalRecipePresenter(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(CompositeSubscription compositeSubscription) {
        dataManager = new DataManager(context);
        this.compositeSubscription = compositeSubscription;
        dataLocalBaseUtil = DataLocalBaseUtil.getInstance();
    }

    @Override
    public void attachLocalRecipeView(LocalRecipeView recipeView) {
        this.localRecipeView = recipeView;
    }

    @Override
    public void attachBannerRecipeView(BannerRecipeView bannerView) {

    }

    @Override
    public void attachFavoriteRecipeView(FavoriteRecipeView favoriteRecipeView) {

    }

    /**
     * 根据运营位id来获取设备对应的所有分类标签
     * 从网络或者数据库获取数据
     *
     * @param name C2.i灶具-本地菜谱
     */
    public void getRecipeCategoryList(String name) {
        //去网络获取菜谱数据
        if(LOCAL_RECIPE_FROM_NET){
            //先获取设备parentId
            compositeSubscription.add(dataManager.getDeviceParentId(name)
                    .flatMap(new Func1<Response<RecipeCategory>, Observable<Response<RecipeCategory>>>() {
                        @Override
                        public Observable<Response<RecipeCategory>> call(Response<RecipeCategory> response) {
                            String parentId = "";
                            if (response.getError() == null) {
                                List<RecipeCategory> list = response.getList();
                                if (null != list && list.size() > 0) {
                                    parentId = list.get(0).getId();
                                }
                            }
//                            LogUtil.LOG_RECIPE("获取ParentId 返回parentId",parentId);
                            return dataManager.getRecipeCategoryList(parentId);
                        }
                    })
                    //指定被订阅者在io线程池中执行
                    .subscribeOn(Schedulers.io())
                    //指定订阅者在main线程中执行
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<RecipeCategory>>() {
                        @Override
                        public void onCompleted() {
                            if (null != listCategory && listCategory.size() > 0) {
                                //从网络上获取到菜谱数据，再保存到本地数据库
                                dataLocalBaseUtil.deleteRecipeCategory();
                                dataLocalBaseUtil.insertRecipeCategory(listCategory);
                            }
                            if(null != localRecipeView){
                                localRecipeView.onRecipeCategoryListSuccess(listCategory);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(null != localRecipeView){
                                localRecipeView.onRecideCategoryListError(e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onNext(Response<RecipeCategory> response) {
                            Error error = response.getError();
                            listCategory = response.getList();
                        }
                    })
            );
        }
        //从本地获取菜谱数据
        else {
            compositeSubscription.add(dataLocalBaseUtil.queryRecipeCategoryRx()
                    //指定被订阅者在io线程池中执行
                    .subscribeOn(Schedulers.io())
                    //指定订阅者在main线程中执行
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<DRecipeCategory>>() {
                        @Override
                        public void onCompleted() {
                            if(null != localRecipeView){
                                localRecipeView.onRecipeCategoryListSuccess(listCategory);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(null != localRecipeView){
                                localRecipeView.onRecideCategoryListError(e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onNext(List<DRecipeCategory> dRecipeCategories) {
                            listCategory = dataLocalBaseUtil.convertToCategory(dRecipeCategories);
//                            LogUtil.LOG_RECIPE("菜谱分类标签--DB","来自本地数据库：" + listCategory.size());
                        }
                    })
            );
        }
    }

    /**
     * 根据分类标签id获取该分类标签下的所有菜谱列表
     * 从网络或者数据库获取数据
     *
     * @param classificationSubId 分类标签id
     * @param tagName             分类标签名称
     */
    public void getRecipeListByCategoryId(String classificationSubId, final String tagName) {
        //去网络获取菜谱数据
        if(LOCAL_RECIPE_FROM_NET){
            compositeSubscription.add(dataManager.getRecipeListByLabel(classificationSubId, tagName, PRESET)
                    //doOnNext在线程池中执行，执行耗时操作
                    .doOnNext(new Action1<Response<Recipe>>() {
                        @Override
                        public void call(Response<Recipe> r) {
                            listRecipe = r.getList();
                            if (null != listRecipe && listRecipe.size() > 0) {
                                for (Recipe recipe : listRecipe) {
                                    //将本地菜谱插入数据库
                                    if (!dataLocalBaseUtil.isDBRecipe(recipe.getId())) {
                                        dataLocalBaseUtil.insert(recipe, RecipeConstant.TYPE_OTHER_RECIPE);
                                    }
                                }
                            }
                        }
                    })
                    //指定被订阅者在io线程池中执行
                    .subscribeOn(Schedulers.io())
                    //指定订阅者在main线程中执行
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<Recipe>>() {
                        @Override
                        public void onCompleted() {
                            if(null != localRecipeView){
                                localRecipeView.onRecipeListSuccess(listRecipe);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(null != localRecipeView){
                                localRecipeView.onRecipeListError(e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onNext(Response<Recipe> response) {

                        }
                    }));
        }
        //从本地获取菜谱数据
        else {
            compositeSubscription.add(dataLocalBaseUtil.queryRecipeByCategoryId(classificationSubId)
                    //指定被订阅者在io线程池中执行
                    .subscribeOn(Schedulers.io())
                    //指定订阅者在main线程中执行
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<DRecipe>>() {
                        @Override
                        public void onCompleted() {
                            if(null != localRecipeView){
                                localRecipeView.onRecipeListSuccess(listRecipe);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(null != localRecipeView){
                                localRecipeView.onRecipeListError(e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onNext(List<DRecipe> list) {
                            listRecipe = new ArrayList<Recipe>();
                            if(null != list){
                                for (DRecipe dRecipe : list) {
                                    listRecipe.add(dataLocalBaseUtil.convertToRecipe(dRecipe));
                                }
                            }
//                            LogUtil.LOG_RECIPE("菜谱list数据--DB","来自本地数据库：" + listRecipe.size());
                        }
                    })
            );
        }
    }

    /**
     * 关键字搜索菜谱列表
     * 有网情况下，在网络搜索
     * 无网情况下执行数据库搜索，无网情况下只需传参keyString，page = -1，devicesProductsId = ""
     *
     * @param keyString 搜索关键字
     * @param page 页码从0开始
     * @param devicesProductsId 设备的 PRODUCT_ID
     */
    public void getRecipeListBySearchKey(String keyString, int page, String devicesProductsId) {
        if(Tool.isNetworkAvailable(context)){
            compositeSubscription.add(dataManager.getRecipeListBySearchKey(keyString, page,devicesProductsId)
                    //指定被订阅者在io线程池中执行
                    .subscribeOn(Schedulers.io())
                    //指定订阅者在main线程中执行
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<Recipe>>() {
                        @Override
                        public void onCompleted() {
                            if(null != localRecipeView){
                                localRecipeView.onRecipeListSuccess(listRecipe);
                            }
                        }
                        @Override
                        public void onError(Throwable e) {
                            if(null != localRecipeView){
                                localRecipeView.onRecipeListError(e.getLocalizedMessage());
                            }
                        }
                        @Override
                        public void onNext(Response<Recipe> response) {
                            listRecipe = response.getList();
                        }
                    }));
        }
        //无网情况下执行数据库搜索
        else {
            compositeSubscription.add(dataLocalBaseUtil.queryRecipeBySearchKey(keyString)
                    //指定被订阅者在io线程池中执行
                    .subscribeOn(Schedulers.io())
                    //指定订阅者在main线程中执行
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<DRecipe>>() {
                        @Override
                        public void onCompleted() {
                            if(null != localRecipeView){
                                localRecipeView.onRecipeListSuccess(listRecipe);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(null != localRecipeView){
                                localRecipeView.onRecipeListError(e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onNext(List<DRecipe> dRecipes) {
                            listRecipe = new ArrayList<Recipe>();
                            if(null != dRecipes && dRecipes.size() > 0){
                                for (DRecipe dRecipe : dRecipes) {
                                    Recipe recipe = dataLocalBaseUtil.convertToRecipe(dRecipe);
                                    listRecipe.add(recipe);
                                }
                            }
                        }
                    })
            );
        }
    }
}
