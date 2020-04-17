package com.fotile.x1i.activity.recipe;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.fotile.common.util.log.LogUtil;
import com.fotile.recipe.bean.recipe.AIngredient;
import com.fotile.recipe.bean.recipe.BIngredient;
import com.fotile.recipe.bean.recipe.CIngredient;
import com.fotile.recipe.bean.recipe.CookingStep;
import com.fotile.recipe.bean.recipe.DIngredient;
import com.fotile.recipe.bean.recipe.MajorIngredient;
import com.fotile.recipe.bean.recipe.MinorIngredient;
import com.fotile.recipe.bean.recipe.Properties;
import com.fotile.recipe.bean.recipe.ReadyStep;
import com.fotile.recipe.bean.recipe.Recipe;
import com.fotile.recipe.net.presenter.FavoriteRecipePresenter;
import com.fotile.recipe.uitl.RecipeConstant;
import com.fotile.recipe.uitl.db.DataFavoriteUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.adapter.recipe.MajorIngredientsListViewAdapter;
import com.fotile.x1i.adapter.recipe.MinorIngredientsListViewAdapter;
import com.fotile.x1i.adapter.recipe.RecipeCookStepListViewAdapter;
import com.fotile.x1i.adapter.recipe.RecipeReadyStepListViewAdapter;
import com.fotile.x1i.adapter.recipe.TypeAIngredientsListViewAdapter;
import com.fotile.x1i.adapter.recipe.TypeBIngredientsListViewAdapter;
import com.fotile.x1i.adapter.recipe.TypeCIngredientsListViewAdapter;
import com.fotile.x1i.adapter.recipe.TypeDIngredientsListViewAdapter;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.widget.MyListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class RecipesDetailsActivity extends BaseActivity implements View.OnClickListener {
    private Recipe recipe;
    //菜谱详情Part 1
    @BindView(R.id.img_recipe_bg)
    ImageView imgRecipeBg;
    @BindView(R.id.img_recipe_details)
    ImageView imageRecipeDetails;
    @BindView(R.id.recipe_name)
    TextView recipeName;
    @BindView(R.id.tv_details_description)
    TextView tvDetailsDescription;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_difficulty)
    TextView tvDifficulty;
    @BindView(R.id.tv_recipe_source)
    TextView tvRecipeSource;
    @BindView(R.id.img_recipe_source)
    ImageView imgRecipeSource;
    //菜谱详情Part 2：材料清单
    @BindView(R.id.listView_major_ingredients)
    MyListView listViewMajorIngredients;
    @BindView(R.id.listView_minor_ingredients)
    MyListView listViewMinorIngredients;
    @BindView(R.id.listView_typeA_ingredients)
    MyListView listViewTypeAIngredients;
    @BindView(R.id.listView_typeB_ingredients)
    MyListView listViewTypeBIngredients;
    @BindView(R.id.listView_typeC_ingredients)
    MyListView listViewTypeCIngredients;
    @BindView(R.id.listView_typeD_ingredients)
    MyListView listViewTypeDIngredients;
    @BindView(R.id.tv_material_Major)
    TextView tvMaterialMajor;
    @BindView(R.id.tv_material_Minor)
    TextView tvMaterialMinor;
    @BindView(R.id.tv_material_typeA)
    TextView tvMaterialTypeA;
    @BindView(R.id.tv_material_typeB)
    TextView tvMaterialTypeB;
    @BindView(R.id.tv_material_typeC)
    TextView tvMaterialTypeC;
    @BindView(R.id.tv_material_typeD)
    TextView tvMaterialTypeD;
    //菜谱详情Part 3：准备步骤
    @BindView(R.id.listView_ready_step)
    MyListView listViewReadyStep;
    //菜谱详情Part 3：烹饪步骤
    @BindView(R.id.listView_cook_step)
    MyListView listViewCookStep;
    @BindView(R.id.img_recipe_collect)
    ImageView imgRecipeCollect;
    @BindView(R.id.tv_recipe_collect)
    TextView tvRecipeCollect;
    @BindView(R.id.layout_collect_recipe)
    RelativeLayout layoutCollectRecipe;
    /**
     * 图片背景大小参数
     */
    private static final String PARAM_IMAGE_BACKGROUND = "?x-oss-process=image/resize,w_1280,h_800";
    /**
     * 图片大小参数
     */
    private static final String PARAM_IMAGE_INNER = "?x-oss-process=image/resize,w_440,h_480";
    /**
     * 图片大小参数
     */
    private static final String PARAM_IMAGE = "?x-oss-process=image/resize,w_590,h_393";
    /**
     * 是否为收藏菜谱的标识
     */
    private boolean isFavoriteRecipe = false;

    private FavoriteRecipePresenter favoriteRecipePresenter;

    List<View> viewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        recipe = bundle.getParcelable("recipe");
        //        imgRecipeCollect.setOnClickListener(this);
        //        tvRecipeCollect.setOnClickListener(this);
        layoutCollectRecipe.setOnClickListener(this);
        initFavoriteData();
        setData();
    }

    private void initFavoriteData() {
        favoriteRecipePresenter = new FavoriteRecipePresenter(this);
        favoriteRecipePresenter.onCreate(compositeSubscription);
        favoriteRecipePresenter.attachFavoriteRecipeView(null);
        isFavoriteRecipe = favoriteRecipePresenter.isFavoriteRecipe(recipe.getId());
        updateFavoriteStatus(isFavoriteRecipe);
    }

    private void setData() {
        if (recipe == null) {
            return;
        }
        setDataPart1();

        setDataPart2();

        setDataPart3();

        setDataPart4();


    }

    //菜谱详情Part 1数据获取
    private void setDataPart1() {
        if (recipe.getImages() != null && !recipe.getImages().isEmpty()) {
            Glide.with(context).load(recipe.getImages().get(0) + PARAM_IMAGE_INNER).thumbnail(RecipeConstant
                    .GLIDE_THUMBNAIL_RECIPE_DETAIL).bitmapTransform(new MultiTransformation(new CenterCrop(context),
                    new RoundedCornersTransformation(context, 10, 0, RoundedCornersTransformation.CornerType.ALL)))
                    .into(imageRecipeDetails);
            Glide.with(context).load(recipe.getImages().get(0) + PARAM_IMAGE_BACKGROUND)
                    .bitmapTransform(new BlurTransformation(context)).crossFade().into(imgRecipeBg);
        }
        recipeName.setText(TextUtils.isEmpty(recipe.getName()) ? "" : recipe.getName());
        tvDetailsDescription.setText(TextUtils.isEmpty(recipe.getInstructions()) ? "" : recipe.getInstructions());
        LogUtil.LOG_RECIPE("description", TextUtils.isEmpty(recipe.getInstructions()));
        Properties recipeProperties = recipe.getProperties();
        LogUtil.LOG_RECIPE("properties", recipeProperties.toString());
        if (null != recipeProperties) {
            tvTime.setText((TextUtils.isEmpty(recipeProperties.getCookingTime()) ? "" : recipeProperties.getCookingTime()) +
                    (TextUtils.isEmpty(recipeProperties.getTimeUnit()) ? "" : recipeProperties.getTimeUnit()));
            tvDifficulty.setText((TextUtils.isEmpty(recipeProperties.getDifficulty()) ? "" : recipeProperties.getDifficulty()));
        }

        if (RecipeConstant.RECIPE_SOURCE_OFFICIAL.equals(recipe.getSource())) {
            tvRecipeSource.setText(context.getString(R.string.source_official));
            imgRecipeSource.setImageResource(R.mipmap.img_recipe_source_official);
        } else if (RecipeConstant.RECIPE_SOURCE_UPLOAD.equals(recipe.getSource())) {
            tvRecipeSource.setText(context.getString(R.string.source_upload));
            imgRecipeSource.setImageResource(R.mipmap.img_recipe_source_user);
        } else if (RecipeConstant.RECIPE_SOURCE_ADULT.equals(recipe.getSource())) {
            tvRecipeSource.setText(context.getString(R.string.source_adult));
            imgRecipeSource.setImageResource(R.mipmap.img_recipe_source_official);
        }
    }

    //菜谱详情Part 2食谱材料清单数据获取
    private void setDataPart2() {
        List<MajorIngredient> majorIngredient = recipe.getMajorIngredients();
        List<MinorIngredient> minorIngredient = recipe.getMinorIngredients();
        List<AIngredient> typeAIngredient = recipe.getTypeAIngredients();
        List<BIngredient> typeBIngredient = recipe.getTypeBIngredients();
        List<CIngredient> typeCIngredient = recipe.getTypeCIngredients();
        List<DIngredient> typeDIngredient = recipe.getTypeDIngredients();
        //加载菜谱主料清单
        if (majorIngredient != null && !majorIngredient.isEmpty()) {
            listViewMajorIngredients.setVisibility(View.VISIBLE);
            tvMaterialMajor.setVisibility(View.VISIBLE);
            MajorIngredientsListViewAdapter majorListViewAdapter = new MajorIngredientsListViewAdapter(this, majorIngredient);
            listViewMajorIngredients.setAdapter(majorListViewAdapter);
        }
        //加载菜谱辅料清单
        if (minorIngredient != null && !minorIngredient.isEmpty()) {
            LogUtil.LOG_RECIPE("minorIngredient", minorIngredient == null || (minorIngredient != null && minorIngredient.isEmpty()));
            listViewMinorIngredients.setVisibility(View.VISIBLE);
            tvMaterialMinor.setVisibility(View.VISIBLE);
            MinorIngredientsListViewAdapter minorlListViewAdapter = new MinorIngredientsListViewAdapter(this, minorIngredient);
            listViewMinorIngredients.setAdapter(minorlListViewAdapter);
        }
        //加载菜谱A料清单
        if (typeAIngredient != null && !typeAIngredient.isEmpty()) {

            listViewTypeAIngredients.setVisibility(View.VISIBLE);
            tvMaterialTypeA.setVisibility(View.VISIBLE);
            TypeAIngredientsListViewAdapter aListViewAdapter = new TypeAIngredientsListViewAdapter(this, typeAIngredient);
            listViewTypeAIngredients.setAdapter(aListViewAdapter);
        }
        //加载菜谱B料清单
        if (typeBIngredient != null && typeBIngredient.isEmpty()) {


            listViewTypeBIngredients.setVisibility(View.VISIBLE);
            tvMaterialTypeB.setVisibility(View.VISIBLE);
            TypeBIngredientsListViewAdapter bListViewAdapter = new TypeBIngredientsListViewAdapter(this, typeBIngredient);
            listViewTypeBIngredients.setAdapter(bListViewAdapter);
        }
        //加载菜谱C料清单
        if (typeCIngredient != null && !typeCIngredient.isEmpty()) {

            listViewTypeCIngredients.setVisibility(View.VISIBLE);
            tvMaterialTypeC.setVisibility(View.VISIBLE);
            TypeCIngredientsListViewAdapter cListViewAdapter = new TypeCIngredientsListViewAdapter(this, typeCIngredient);
            listViewTypeCIngredients.setAdapter(cListViewAdapter);
        }
        //加载菜谱D料清单
        if (typeDIngredient != null && !typeDIngredient.isEmpty()) {

            listViewTypeDIngredients.setVisibility(View.VISIBLE);
            tvMaterialTypeD.setVisibility(View.VISIBLE);
            TypeDIngredientsListViewAdapter dListViewAdapter = new TypeDIngredientsListViewAdapter(this, typeDIngredient);
            listViewTypeDIngredients.setAdapter(dListViewAdapter);
        }

    }

    //菜谱详情Part 3准备步骤数据获取
    private void setDataPart3() {
        List<ReadyStep> readyStepList = recipe.getReadySteps();
        int readyStepCount = (null != readyStepList) ? readyStepList.size() : 0;
        if (readyStepList != null && !readyStepList.isEmpty()) {

            listViewReadyStep.setVisibility(View.VISIBLE);
            RecipeReadyStepListViewAdapter recipeReadyStepListViewAdapter = new RecipeReadyStepListViewAdapter(this, readyStepList, recipe);
            listViewReadyStep.setAdapter(recipeReadyStepListViewAdapter);
        }
    }

    //菜谱详情Part 4烹饪步骤数据获取
    private void setDataPart4() {
        //菜谱详情Part 2烹饪步骤数据获取
        List<CookingStep> cookingStepList = recipe.getCookingSteps();
        int cookingStepCount = (null != cookingStepList) ? cookingStepList.size() : 0;
        if (cookingStepList != null && !cookingStepList.isEmpty()) {

            listViewCookStep.setVisibility(View.VISIBLE);
            RecipeCookStepListViewAdapter recipeCookStepListViewAdapter = new RecipeCookStepListViewAdapter(this, cookingStepList, recipe);
            listViewCookStep.setAdapter(recipeCookStepListViewAdapter);
        }
    }

    private String getCookStepTitle(int index, int totalCount) {
        StringBuilder builder = new StringBuilder();
        builder.append(index + 1);
        builder.append(" / ");
        builder.append(totalCount);
        return builder.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_collect_recipe:
                boolean favorite = favoriteRecipePresenter.isFavoriteRecipe(recipe.getId());
                //如果已经收藏过，删除收藏
                if (favorite) {
                    //删除收藏
                    favoriteRecipePresenter.deleteFacoriteRecipe(recipe.getId());
                    updateFavoriteStatus(false);
                } else {
                    //菜谱收藏达到上限
                    if (DataFavoriteUtil.getInstance().getMyFavoriteSize() >= RecipeConstant.MAX_MY_FAVORITE_VALUE) {


                    }
                    //收藏
                    if (DataFavoriteUtil.getInstance().getMyFavoriteSize() < RecipeConstant.MAX_MY_FAVORITE_VALUE) {
                        if (!favoriteRecipePresenter.isFavoriteRecipe(recipe.getId())) {
                            favoriteRecipePresenter.addFavoriteRecipe(recipe);
                            updateFavoriteStatus(true);
                        }
                    }
                }
                break;
        }
    }

    /**
     * 更新收藏状态
     *
     * @param isFavorite
     */
    public void updateFavoriteStatus(boolean isFavorite) {
        imgRecipeCollect.setImageResource(isFavorite ? R.mipmap.img_collect_recipe : R.mipmap.img_uncollect_recipe);
        tvRecipeCollect.setText(isFavorite ? getString(R.string.collect_recipe) : getString(R.string
                .cancel_collect_recipe));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_recipe_detail;
    }

    @Override
    public boolean showBottom() {
        return true;
    }
}
