package com.fotile.x1i.activity.quicktool.message;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.message.bean.NotificationDb;
import com.fotile.x1i.R;
import com.fotile.x1i.adapter.common.CommonViewpagerFragmentAdapter;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.base.BaseFragment;
import com.fotile.x1i.manager.DeviceReportManager;
import com.fotile.x1i.mvp.presenter.NotificationPresenter;
import com.fotile.x1i.mvp.view.NotificationView;
import com.fotile.x1i.widget.HorScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.bean.DeviceMessage;
import rx.functions.Action1;

/**
 * 文件名称： MessageActivity
 * 创建时间： 2019/6/12
 * 文件作者： chenyqi
 * 功能描述： 消息中心
 */
public class MessageActivity extends BaseActivity implements ViewPager.OnPageChangeListener, HorScrollView
        .OnHorItemClickListener, View.OnClickListener {

    @BindView(R.id.tabLayout_category)
    HorScrollView tabLayoutCategory;

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    CommonViewpagerFragmentAdapter viewpagerAdapter;

    private MemorFragment memorFragment;
    private NotificationFragment notificationFragment;

    @BindView(R.id.clear)
    TextView txtClear;
    /**
     * 消息提醒
     */
    Action1 actionMessage;
    /**
     * 消息通知及备忘录presenter
     */
    private NotificationPresenter notificationPresenter;

    @Override
    public void createAction() {
        super.createAction();
        actionMessage = new Action1<DeviceMessage>() {
            @Override
            public void call(DeviceMessage deviceMessage) {
                //获取家庭备忘
                if (deviceMessage.code == 9) {
                    notificationPresenter.getMemorandumDataList();
                }
                //获取消息
                if (deviceMessage.code == 16) {
                    notificationPresenter.getNoticDataList();
                }
                updateRedShow();
            }
        };

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        initData();
        initView();
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
     * 初始化数据
     */
    private void initData() {
        notificationPresenter.getMemorandumDataList();
        notificationPresenter.getNoticDataList();
        //添加action
        if (null != actionMessage) {
            DeviceReportManager.getInstance().addMessageAction(actionMessage);
        }
    }

    private NotificationView notificationView = new NotificationView() {
        @Override
        public void onNotificationSuccess(List<NotificationDb> notificationList) {
            int size = (null == notificationList ? 0 : notificationList.size());
            LogUtil.LOG_Notifi("获取的消息条数", size);
            if (size > 0) {
                for (NotificationDb db : notificationList) {
                    LogUtil.LOG_Notifi("消息提醒", db);
                }
            }
        }

        @Override
        public void onMemorandumSuccess(List memorandumDataList) {
            int size = (null == memorandumDataList ? 0 : memorandumDataList.size());
            LogUtil.LOG_Notifi("获取的备忘录条数", size);
        }

        @Override
        public void onError(String result) {

        }
    };

    private void initView() {
        int index = 0;
        tabLayoutCategory.addItemView("通知", index++);
        tabLayoutCategory.addItemView("备忘录", index++);
        tabLayoutCategory.setSelect(0);
        tabLayoutCategory.setOnHorItemClickListener(this);
        List<BaseFragment> list = new ArrayList<BaseFragment>();
        memorFragment = new MemorFragment();
        notificationFragment = new NotificationFragment();

        list.add(notificationFragment);
        list.add(memorFragment);
        viewpagerAdapter = new CommonViewpagerFragmentAdapter(getSupportFragmentManager());
        viewpagerAdapter.setList(list);
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
        PreferenceUtil.setHasUnreadNotification(this, false);
        txtClear.setOnClickListener(this);
        updateRedShow();
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    //HorScrollView 单个元素的点击事件
    @Override
    public void onHorItemClick(int index) {
        viewPager.setCurrentItem(index);
        if (index == 0) {
            PreferenceUtil.setHasUnreadNotification(this, false);
        } else if (index == 1) {
            PreferenceUtil.setHasUnreadMemorandum(this, false);
        }
        LogUtil.LOGE("---------onHorItemClick", index);
        updateRedShow();
    }

    /**
     * 设置通知红点是否显示
     */
    public void updateRedShow() {
        if (PreferenceUtil.hasUnreadMemorandum(this)) {
            tabLayoutCategory.showRedPoint(1);
        } else {
            tabLayoutCategory.hideRedPoint(1);
        }
        if (PreferenceUtil.hasUnreadNotification(this)) {
            tabLayoutCategory.showRedPoint(0);
        } else {
            tabLayoutCategory.hideRedPoint(0);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tabLayoutCategory.setSelect(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear:
                int position = viewPager.getCurrentItem();
                if (position == 0) {
                    notificationFragment.clearData();
                } else {
                    memorFragment.clearData();
                }
                break;
        }
    }
}
