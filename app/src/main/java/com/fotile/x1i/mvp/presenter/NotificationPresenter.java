package com.fotile.x1i.mvp.presenter;

import android.content.Context;


import com.fotile.message.bean.MemorandumDb;
import com.fotile.message.bean.NotificationDb;
import com.fotile.message.util.MessageDataBaseUtil;
import com.fotile.x1i.base.BasePresenter;
import com.fotile.x1i.base.BaseView;
import com.fotile.x1i.mvp.view.NotificationView;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by wanghouyu on 17-10-11.
 */

public class NotificationPresenter implements BasePresenter {
    private Context context;
    private NotificationView notificationView;
    private CompositeSubscription compositeSubscription;
    private MessageDataBaseUtil dataBaseUtil;
    public NotificationPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(CompositeSubscription compositeSubscription) {
        this.compositeSubscription = compositeSubscription;
        dataBaseUtil = MessageDataBaseUtil.getInstance();
    }

    @Override
    public void attachView(BaseView baseView) {
        notificationView = (NotificationView) baseView;
    }

    public void getMemorandumDataList() {

        compositeSubscription.add(dataBaseUtil.queryAllMemorandumBean().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<MemorandumDb>>() {
                    @Override
                    public void call(List<MemorandumDb> memorandumDbList) {
                        notificationView.onMemorandumSuccess(memorandumDbList);
                    }
                }));
    }

    public void getNoticDataList() {
        compositeSubscription.add(dataBaseUtil.queryAllNotificationDb().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<NotificationDb>>() {
                    @Override
                    public void call(List<NotificationDb> notificationDbList) {
                        notificationView.onNotificationSuccess(notificationDbList);
                    }
                }));
    }
}
