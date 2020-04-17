package com.fotile.x1i.activity.quicktool.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fotile.common.util.log.LogUtil;
import com.fotile.message.bean.NotificationDb;
import com.fotile.message.util.MessageDataBaseUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseFragment;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.mvp.presenter.NotificationPresenter;
import com.fotile.x1i.mvp.view.NotificationView;

import java.util.List;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.bean.DeviceMessage;
import rx.functions.Action1;

/**
 * 文件名称：MemorFragment
 * 创建时间：2019/6/25 13:54
 * 文件作者：yaohx
 * 功能描述：备忘录 Fragment
 */
public class MemorFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    MemorRecyclerAdapter recyclerAdapter;
    /**
     * 消息通知及备忘录presenter
     */
    private NotificationPresenter notificationPresenter;
    /**
     * 消息提醒
     */
    Action1 actionMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initPresenter();
        initView();
        getData();
        return view;
    }

    /**
     * 初始化Presernter
     */
    private void initPresenter() {
        notificationPresenter = new NotificationPresenter(context);
        notificationPresenter.onCreate(compositeSubscription);
        notificationPresenter.attachView(notificationView);
    }

    /**
     * 清空列表数据
     */
    public void clearData() {
        MessageDataBaseUtil.getInstance().deleteAllMemorandum();
        getData();
    }

    @Override
    public void createAction() {
        super.createAction();
        actionMessage = new Action1<DeviceMessage>() {
            @Override
            public void call(DeviceMessage deviceMessage) {
                //获取消息
                if (deviceMessage.code == 9) {
                    getData();
                }
            }
        };
        DeviceReportManager.getInstance().addMessageAction(actionMessage);
    }

    private void getData() {
        notificationPresenter.getMemorandumDataList();
    }

    private void initView() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager
                .VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new MemorRecyclerAdapter(context);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private NotificationView notificationView = new NotificationView() {
        @Override
        public void onNotificationSuccess(List<NotificationDb> notificationList) {
        }

        @Override
        public void onMemorandumSuccess(List memorandumDataList) {
            int size = (null == memorandumDataList ? 0 : memorandumDataList.size());
            LogUtil.LOG_Notifi("获取的备忘录条数", size);
            recyclerAdapter.addItems(memorandumDataList);
        }

        @Override
        public void onError(String result) {

        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.fragment_memor;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DeviceReportManager.getInstance().removeMessageAction(actionMessage);
    }
}
