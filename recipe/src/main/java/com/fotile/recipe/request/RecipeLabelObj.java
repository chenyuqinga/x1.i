package com.fotile.recipe.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeLabelObj {
    @SerializedName("properties.label")
    @Expose
    private Regex recipeLabel;

    public Regex getRecipeLabel() {
        return recipeLabel;
    }

    public void setRecipeLabel(Regex recipeLabel) {
        this.recipeLabel = recipeLabel;
    }
}
