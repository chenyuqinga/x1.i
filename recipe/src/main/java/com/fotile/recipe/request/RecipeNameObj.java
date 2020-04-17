package com.fotile.recipe.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RecipeNameObj {
    @SerializedName("name")
    @Expose
    private Regex recipeName;

    public Regex getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(Regex recipeName) {
        this.recipeName = recipeName;
    }
}
