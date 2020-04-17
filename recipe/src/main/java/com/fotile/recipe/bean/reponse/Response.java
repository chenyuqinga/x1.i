package com.fotile.recipe.bean.reponse;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 文件名称：Response
 * 创建时间：2017/8/15
 * 文件作者：zhaoqingjing
 * 功能描述：返回数据json
 */
public class Response<T> {

    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("list")
    @Expose
    private List<T> list;

    @SerializedName("error")
    @Expose
    private Error error;

    @SerializedName("code")
    @Expose
    private int code;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        String gson = new Gson().toJson(this);
        return gson;
    }
}
