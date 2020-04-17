package com.fotile.x1i.activity.quicktool.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.fotile.x1i.R;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.listener.OnDialogListener;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 文件名称：TickTimeSelectDialog
 * 创建时间：2019/5/27 18:22
 * 文件作者：yaohx
 * 功能描述：定时器选择时间dialog
 */
public class TickTimeSelectDialog extends BaseDialog implements View.OnClickListener {

    @BindView(R.id.wheel_view)
    WheelView wheelView;

    @BindView(R.id.txt_confirm)
    TextView tvConfirm;

    @BindView(R.id.txt_cancel)
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


    public TickTimeSelectDialog(Context context) {
        super(context, R.style.FullScreenDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        initTimeData();
        wheelView.setShowLine(false);
        wheelView.setMoveRange(10);
        numberPickerAdapter = new ArrayWheelAdapter(timeDataList);
        wheelView.setAdapter(numberPickerAdapter);
        wheelView.setCurrentItem(currentPositon);
        wheelView.setOnItemSelectedListener(timeSelectedListener);

        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击确认
            case R.id.txt_confirm:
                dismiss();
                //开始倒计时
                String str = timeDataList.get(currentPositon);
                int minute = Integer.parseInt(str);
                if (null != onDialogListener) {
                    onDialogListener.onRightClick(minute);
                }
                break;
            //点击取消
            case R.id.txt_cancel:
                dismiss();
                break;
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

    /**
     * 初始化选择时间的数据
     */
    private void initTimeData() {
        for (int i = 1; i < TIME_MAX; i++) {
            if (i < 10) {
                timeDataList.add("0" + i);
            } else {
                timeDataList.add("" + i);
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_timer_select;
    }


}
