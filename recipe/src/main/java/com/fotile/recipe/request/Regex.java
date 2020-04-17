package com.fotile.recipe.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Regex {

    @SerializedName("$regex")
    @Expose
    private String  regex;

    @SerializedName("$options")
    @Expose
    private String  option;

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
