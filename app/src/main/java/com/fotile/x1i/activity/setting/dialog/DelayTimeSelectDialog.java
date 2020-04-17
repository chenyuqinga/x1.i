package com.fotile.x1i.activity.setting.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;


import java.util.ArrayList;

import butterknife.BindView;

/**
  * 文件名称：DelayTimeSelectDialog
  * 创建时间：2019/7/29 11:47
  * 文件作者：yaohx
  * 功能描述：烟灶联动时间选择器
  */
public class DelayTimeSelectDialog extends BaseDialog {

    @BindView(R.id.wheel_view)
    WheelView wheelView;

    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    @BindView(R.id.tv_cancel)
    TextView tvCancel;

    /**
     * 设置时间的最大值
     */
    private final int TIME_MAX = 100;
    /**
     * 当前选择的位置
     */
    private int currentPositon = 0;

    /**
     * 可选时间列表
     */
    ArrayList<String> timeDataList = new ArrayList<>();

    /**
     * 可选时间列表适配器
     */
    private ArrayWheelAdapter<String> numberPickerAdapter;

    private BtnCallBack btnCallBack;

    public DelayTimeSelectDialog(Context context) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_delay_time;
    }

    private void initView() {
        initTimeData();
        wheelView.setShowLine(false);
        wheelView.setMoveRange(10);
        numberPickerAdapter = new ArrayWheelAdapter(timeDataList);
        wheelView.setAdapter(numberPickerAdapter);
        wheelView.setCurrentItem(currentPositon);
        wheelView.setOnItemSelectedListener(timeSelectedListener);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                String number = timeDataList.get(currentPositon);
                btnCallBack.onPositive(DelayTimeSelectDialog.this, Integer.parseInt(number));
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                btnCallBack.onNegative(DelayTimeSelectDialog.this);
            }
        });

    }

    /**
     * 初始化选择时间的数据
     */
    private void initTimeData() {
        int time = PreferenceUtil.getDelayTime(context);
        //从1分钟到99分钟
        for (int i = 1; i < TIME_MAX; i++) {
            if (i == time) {
                currentPositon = i - 1;
            }
            if (i < 10) {
                timeDataList.add("0" + i);
            } else {
                timeDataList.add("" + i);
            }
        }
    }

    /**
     * 时间选择栏的滚动监听
     */

    OnItemSelectedListener timeSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(int index) {
            currentPositon = index;
        }
    };

    public void callBack(BtnCallBack btnCallBack) {
        this.btnCallBack = btnCallBack;
    }

    public interface BtnCallBack {
        /**
         * 点击确定
         *
         * @param dialog
         * @param number
         */
        void onPositive(DelayTimeSelectDialog dialog, int number);

        /**
         * 点击取消
         *
         * @param dialog
         */
        void onNegative(DelayTimeSelectDialog dialog);
    }
}
