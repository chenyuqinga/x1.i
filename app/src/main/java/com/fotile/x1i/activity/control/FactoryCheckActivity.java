package com.fotile.x1i.activity.control;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fotile.x1i.adapter.FactoryCheckAdapter;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.DeviceControl;
import fotile.device.cookerprotocollib.helper.bean.WorkBean;
import rx.functions.Action1;

/**
  * 文件名称：FactoryCheckActivity
  * 创建时间：2019/7/17 10:35
  * 文件作者：yaohx
  * 功能描述：
  */
public class FactoryCheckActivity extends BaseActivity {

    /**
     * 标题
     */
    @BindView(R.id.txt_check_title)
    TextView txt_check_title;

    /**
     * 内容区域
     */
    @BindView(R.id.linear_content)
    LinearLayout linear_content;
    /**
     * 当前风机工作模式
     */
    @BindView(R.id.txt_work_mode)
    TextView txt_work_mode;

    @BindView(R.id.listview)
    ListView listView;

    FactoryCheckAdapter listAdapter;

    private List<FactoryCheckAdapter.FactoryData> list = new ArrayList<FactoryCheckAdapter.FactoryData>();

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        startTimer();
    }

    @Override
    public void createAction() {
        super.createAction();
        actionWorkBean = new Action1<WorkBean>() {
            @Override
            public void call(WorkBean workBean) {
                setViewValue(workBean);
            }
        };
    }

    private void setViewValue(WorkBean workBean) {
        String workmode = workBean.workmode;

        //自动档
        if (workmode.equals("autocruise")) {
            txt_work_mode.setText("自动档");
            linear_content.setVisibility(View.VISIBLE);
            txt_check_title.setVisibility(View.GONE);

            initData(workBean);
        }
        //空气管家
        else if (workmode.equals("airsteward")) {
            txt_work_mode.setText("空气管家");
            linear_content.setVisibility(View.VISIBLE);
            txt_check_title.setVisibility(View.GONE);

            initData(workBean);
        }
        //风量控制 - 风量控制的风机要大于0
        else if (workmode.equals("manual") && workBean.motorgear > 0) {
            txt_work_mode.setText("风量");
            linear_content.setVisibility(View.VISIBLE);
            txt_check_title.setVisibility(View.GONE);

            initData(workBean);
        } else {
            return;
        }

    }
    private void initData(WorkBean workBean) {
        list.clear();
        FactoryCheckAdapter.FactoryData fd = new FactoryCheckAdapter.FactoryData("电机转速", workBean.motorspeed);
        list.add(fd);
        fd = new FactoryCheckAdapter.FactoryData("电机电流", workBean.motorcurrent);
        list.add(fd);
        fd = new FactoryCheckAdapter.FactoryData("运转时间", workBean.runtime + "");
        list.add(fd);
        fd = new FactoryCheckAdapter.FactoryData("电动推杆电流", workBean.linearactuatorelectricity + "");
        list.add(fd);
        fd = new FactoryCheckAdapter.FactoryData("故障", workBean.errornum + "");
        list.add(fd);
        fd = new FactoryCheckAdapter.FactoryData("NTC", workBean.NTC  + "");
        list.add(fd);
        fd = new FactoryCheckAdapter.FactoryData("特殊功能", workBean.specialfunction  + "");
        list.add(fd);

        listAdapter.setList(list);
        listAdapter.notifyDataSetChanged();
    }
    private void initView() {
        listAdapter = new FactoryCheckAdapter(this);
        listAdapter.setList(list);
        listView.setAdapter(listAdapter);
    }

    private void startTimer() {
        if (null == timer) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DeviceControl.getInstance().getDeviceInfo();
            }
        }, 2 * 1000, 5 * 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    private void cancelTimer() {
        if (null != timer) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_factory_check;
    }
}
