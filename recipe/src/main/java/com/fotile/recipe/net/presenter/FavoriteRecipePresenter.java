package com.fotile.recipe.net.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.recipe.base.BaseRecipePresenter;
import com.fotile.recipe.bean.recipe.DRecipe;
import com.fotile.recipe.bean.recipe.DRecipeBanner;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.bean.recipe.RecipeBanner;
import com.fotile.recipe.bean.reponse.Response;
import com.fotile.recipe.net.modle.DataManager;
import com.fotile.recipe.net.view.BannerRecipeView;
import com.fotile.recipe.net.view.FavoriteRecipeView;
import com.fotile.recipe.net.view.LocalRecipeView;
import com.fotile.recipe.uitl.RecipeConstant;
import com.fotile.recipe.uitl.db.DataFavoriteUtil;
import com.fotile.recipe.uitl.db.DataNetBaseUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 项目名称：X1.I
 * 创建时间：2019/1/22 15:35
 * 文件作者：yaohx
 * 功能描述：获取我的收藏（我的上传）菜谱
 */
public class FavoriteRecipePresenter implements BaseRecipePresenter {

    private CompositeSubscription compositeSubscription;
    private Context context;

    /**
     * 收藏菜谱回调view
     */
    private FavoriteRecipeView favoriteRecipeView;

    private DataFavoriteUtil dataFavoriteUtil;
    private DataNetBaseUtil dataNetBaseUtil;

    private DataManager dataManager;

    /**
     * 请求上传菜谱数据的页数编号
     */
    private int requestPage = 0;

    public FavoriteRecipePresenter(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(CompositeSubscription compositeSubscription) {
        this.compositeSubscription = compositeSubscription;
        dataManager = new DataManager(context);
        dataFavoriteUtil = DataFavoriteUtil.getInstance();
        dataNetBaseUtil = DataNetBaseUtil.getInstance();
    }

    @Override
    public void attachLocalRecipeView(LocalRecipeView recipeView) {

    }

    @Override
    public void attachBannerRecipeView(BannerRecipeView bannerView) {

    }

    @Override
    public void attachFavoriteRecipeView(FavoriteRecipeView favoriteRecipeView) {
        this.favoriteRecipeView = favoriteRecipeView;
    }

    /**
     * 重置请求页数
     */
    public void resetRequestPage() {
        requestPage = 0;
    }

    /**
     * 收藏菜谱
     *
     * @param recipe
     * @return false    已经收藏过了
     * true     收藏成功
     */
    public boolean addFavoriteRecipe(Recipe recipe) {
        //如果菜谱已经收藏过了
        if (isFavoriteRecipe(recipe.getId())) {
            return false;
        }
        //如果没有收藏过
        else {
            //如果菜谱已经在轮播菜谱（达人秀）数据库中存在
            if (dataNetBaseUtil.isDbRecipeBanner(recipe.getId())) {
                DRecipeBanner dRecipeBanner = dataNetBaseUtil.queryRecipeBanner(recipe.getId());
                if (null != dRecipeBanner) {
                    RecipeBanner recipeBanner = dataNetBaseUtil.convertRecipeBanner(dRecipeBanner);
                    //将轮播菜谱保存到轮播菜谱数据库
                    if (!dataFavoriteUtil.isDbRecipeBanner(recipe.getId())) {
                        dataFavoriteUtil.insertRecipeBanner(recipeBanner);
                    }
                }
            }
            //再保存菜谱详情数据
            dataFavoriteUtil.insert(recipe, RecipeConstant.TYPE_OTHER_RECIPE);
            return true;
        }
    }

    /**
     * 删除收藏的菜谱
     *
     * @param id
     */
    public void deleteFacoriteRecipe(String id) {
        dataFavoriteUtil.deleteByRecipeId(id);
    }


    /**
     * 从数据库中获取收藏列表
     */
    public void getFavoriteRecipeList() {
        compositeSubscription.add(dataFavoriteUtil.queryAll()
                //指定被订阅者在io线程池中执行
                .subscribeOn(Schedulers.io())
                //指定订阅者在main线程中执行
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<DRecipe>>() {
                    @Override
                    public void call(List<DRecipe> dRecipes) {
                        List<Recipe> list = new ArrayList<>();
                        if (null != dRecipes) {
                            for (DRecipe dRecipe : dRecipes) {
                                Recipe recipe = dataFavoriteUtil.convertToRecipe(dRecipe);
                                list.add(recipe);
                            }
                        }
                        if (null != favoriteRecipeView) {
                            favoriteRecipeView.onFavoriteListSuccess(list);
                        }
                    }
                }));
    }

    /**
     * 菜谱的收藏状态
     *
     * @param id
     */
    public boolean isFavoriteRecipe(String id) {
        return dataFavoriteUtil.isDBRecipe(id);
    }

    /**
     * 获取上传菜谱，如果有获取到菜谱列表，先将数据保存在数据库中再通知activity
     */
    public void getUploadRecipeList(Context context) {
        //灶具的mac地址
        String deviceId = (String) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.STOVE_DEVICE_ID, "");
        if (!TextUtils.isEmpty(deviceId)) {
            try {
                deviceId = Long.parseLong(deviceId, 16) + "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //设备自制菜谱token
        String token = PreferenceUtil.getStoveUploadRecipeToken(context);

        //从网络获取自制菜谱数据
        if (Tool.isNetworkAvailable(context) && !TextUtils.isEmpty(deviceId) && !TextUtils.isEmpty(token)) {
            compositeSubscription.add(dataManager.getUploadRecipe(requestPage, token, deviceId)
                    .doOnNext(new Action1<Response<Recipe>>() {
                        @Override
                        public void call(Response<Recipe> response) {
                            List<Recipe> list = response.getList();
                            //将自制菜谱保存到本地数据库
                            if (null != list && list.size() > 0) {
                                if (requestPage == 0) {
                                    dataNetBaseUtil.deleteUploadRecipe();
                                }
                                for (Recipe recipe : list) {
                                    if (!dataNetBaseUtil.isDBRecipe(recipe.getId())) {
                                        dataNetBaseUtil.insert(recipe, RecipeConstant.TYPE_OTHER_RECIPE);
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

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != favoriteRecipeView) {
                                favoriteRecipeView.onUploadRecipeListError(e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onNext(Response<Recipe> response) {
                            List<Recipe> list = response.getList();
                            if (null != favoriteRecipeView) {
                                favoriteRecipeView.onUploadRecipeListSuccess(list);
                            }
                        }
                    })
            );
        }
        //从本地获取自制菜谱数据
        else {
            compositeSubscription.add(dataNetBaseUtil.queryUploadRecipe()
                    //指定被订阅者在io线程池中执行
                    .subscribeOn(Schedulers.io())
                    //指定订阅者在main线程中执行
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<DRecipe>>() {
                        @Override
                        public void call(List<DRecipe> dRecipes) {
                            List<Recipe> recipeList = new ArrayList<>();
                            if (dRecipes != null && dRecipes.size() > 0) {
                                for (DRecipe dRecipe : dRecipes) {
                                    recipeList.add(dataNetBaseUtil.convertToRecipe(dRecipe));
                                }
                            }
                            if (null != favoriteRecipeView) {
                                favoriteRecipeView.onUploadRecipeListSuccess(recipeList);
                            }
                        }
                    })
            );
        }
    }

    /**
     * 删除自制菜谱
     * 先删除网络自制菜谱
     * 删除成功后再去删除本地数据库自制菜谱
     *
     * @param recipeId
     */
    public void deleteUploadRecipe(final String recipeId) {
        //灶具的mac地址
        String deviceId = (String) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.STOVE_DEVICE_ID, "");
        if (!TextUtils.isEmpty(deviceId)) {
            try {
                deviceId = Long.parseLong(deviceId, 16) + "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //设备自制菜谱token
        String token = PreferenceUtil.getStoveUploadRecipeToken(context);

        if (!TextUtils.isEmpty(deviceId) && !TextUtils.isEmpty(token)) {
            compositeSubscription.add(dataManager.deleteUploadRecipe(recipeId, token, deviceId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.LOGE("---deletedefeat", e);
                            if (null != favoriteRecipeView) {
                                favoriteRecipeView.onUploadRecipeDeleteError(e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onNext(Response response) {
                            //网络自制菜谱删除成功
                            if (null != response && response.getCode() == 0) {
                                dataNetBaseUtil.deleteByRecipeId(recipeId);
                                if (null != favoriteRecipeView) {
                                    favoriteRecipeView.onUploadRecipeDeleteSuccess();
                                    LogUtil.LOGE("---deletesuccess", "success");
                                }
                            } else {
                                if (null != favoriteRecipeView) {
                                    String msg = "网络自制菜谱删除失败：" + response.getError().getMsg();
                                    favoriteRecipeView.onUploadRecipeDeleteError(msg);
                                    LogUtil.LOGE("---deletedefeat", "网络自制菜谱删除失败：");
                                }
                            }
                        }
                    }));
        } else {
            if (null != favoriteRecipeView) {
                favoriteRecipeView.onUploadRecipeDeleteError("自制菜谱token为空");
                LogUtil.LOGE("---deletedefeat", "自制菜谱token为空");
            }
        }
    }

    /**
     * 根据id查询收藏
     *
     * @param id
     */
    public Recipe getRecipeById(String id) {
        return dataFavoriteUtil.queryRecipeById(id);
    }

    /**
     * 根据id删除收藏菜谱
     *
     * @param id
     */
    public void deleteByRecipeId(String id) {
        dataFavoriteUtil.deleteByRecipeId(id);
    }

}
