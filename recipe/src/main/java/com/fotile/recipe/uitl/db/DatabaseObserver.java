package com.fotile.recipe.uitl.db;

/**
 * 文件名称：DatabaseObserver
 * 创建时间：2017-09-15 18:57
 * 文件作者：shihuijuan
 * 功能描述：监听数据库变化的接口
 */

public interface DatabaseObserver {
    /**
     * 数据变化回调方法
     */
    void onChange();
}
