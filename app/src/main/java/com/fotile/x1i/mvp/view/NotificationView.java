package com.fotile.x1i.mvp.view;


import com.fotile.message.bean.NotificationDb;
import com.fotile.x1i.base.BaseView;

import java.util.List;

/**
 * Created by wanghouyu on 17-10-11.
 */

public interface NotificationView extends BaseView{
    void onNotificationSuccess(List<NotificationDb> notificationDbList);

    void onMemorandumSuccess(List memorandumDataList);

    void onError(String result);
}
