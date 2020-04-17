package com.fotile.x1i.base;


import rx.subscriptions.CompositeSubscription;

/**
  * @author：       yaohx
  * @data：         2019/4/15 16:22
  * @company：      杭州方太智能科技有限公司
  * @description： MVP模式中的Presenter
  */
public interface BasePresenter {
    /**
     * 完成Presenter的一些初始化工作
     *
     * @param compositeSubscription
     */
    void onCreate(CompositeSubscription compositeSubscription);


    /**
     * 传递BaseView
     *
     * @param baseView
     */
    void attachView(BaseView baseView);
}
