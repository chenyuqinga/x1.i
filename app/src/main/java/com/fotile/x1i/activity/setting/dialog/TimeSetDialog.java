package com.fotile.x1i.activity.setting.dialog;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.TimeSettingActivity;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.widget.SnakeBar;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;

/**
 * 文件名称：TimeSetDialog
 * 创建时间：2019/7/29 11:52
 * 文件作者：yaohx
 * 功能描述：设置系统时间dialog
 */
public class TimeSetDialog extends BaseDialog implements View.OnClickListener {

    /**
     * 小时的最小值
     */
    private final int HOUR_MIN = 0;
    /**
     * 24小时制小时的最大值
     */
    private final int HOUR_MAX_24 = 23;
    /**
     * 分钟的最小值
     */
    private final int MINUTE_MIN = 0;
    /**
     * 分钟的最大值
     */
    private final int MINUTE_MAX = 59;
    /**
     * 小时数据列表
     */
    private ArrayList<String> hourList = new ArrayList<>();
    /**
     * 分钟数据列表
     */
    private ArrayList<String> minuteList = new ArrayList<>();
    /**
     * 小时数据列表适配器
     */
    private ArrayWheelAdapter hourListAdapter;
    /**
     * 分钟数据列表适配器
     */
    private ArrayWheelAdapter minuteListAdapter;
    /**
     * 滚轮小时
     */
    @BindView(R.id.wheel_view_hour)
    WheelView wheelViewHour;
    /**
     * 滚轮分钟
     */
    @BindView(R.id.wheel_view_minute)
    WheelView wheelViewMinute;

    @BindView(R.id.txt_cancel)
    TextView txtCancel;
    @BindView(R.id.txt_confirm)
    TextView txtConfirm;

    private TimeSettingActivity.OnDateTimeSetListener onDateTimeSetListener;

    public TimeSetDialog(Context context) {
        super(context, R.style.FullScreenDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_time_set;
    }

    private void initView() {
        txtCancel.setOnClickListener(this);
        txtConfirm.setOnClickListener(this);
    }

    public void setOnDateTimeSetListener(TimeSettingActivity.OnDateTimeSetListener listener){
        this.onDateTimeSetListener = listener;
    }

    private void initData() {
        Time time = new Time();
        time.setToNow();

        for (int i = HOUR_MIN; i <= HOUR_MAX_24; i++) {
            hourList.add(getShowTimeStr(i));
        }
        hourListAdapter = new ArrayWheelAdapter(hourList);
        wheelViewHour.setAdapter(hourListAdapter);
        wheelViewHour.setCurrentItem(hourList.indexOf(getShowTimeStr(time.hour)));

        for (int i = MINUTE_MIN; i <= MINUTE_MAX; i++) {
            minuteList.add(getShowTimeStr(i));
        }
        minuteListAdapter = new ArrayWheelAdapter(minuteList);
        wheelViewMinute.setAdapter(minuteListAdapter);
        wheelViewMinute.setCurrentItem(minuteList.indexOf(getShowTimeStr(time.minute)));
    }


    public String getShowTimeStr(int i) {
        if (i < 10) {
            return "0" + String.valueOf(i);
        } else {
            return String.valueOf(i);
        }
    }

    /**
     * 无网络情况下设置系统时间
     */
    public String setSysDate() {
        Time time = new Time();
        time.setToNow();
        Calendar c = Calendar.getInstance();
        //年
        int year = time.year;
        c.set(Calendar.YEAR, year);
        //月
        int month = time.month;
        c.set(Calendar.MONTH, month);
        //日
        int day = time.monthDay;
        c.set(Calendar.DAY_OF_MONTH, day);

        //时
        int hour = Integer.parseInt((String) hourListAdapter.getData().get(wheelViewHour.getCurrentItem()));
        c.set(Calendar.HOUR_OF_DAY, hour);
        //分
        int minute = Integer.parseInt((String) minuteListAdapter.getData().get(wheelViewMinute.getCurrentItem()));
        c.set(Calendar.MINUTE, minute);

        String result = hour + "时" + minute + "分";

        try {
            long when = c.getTimeInMillis();
            if (when / 1000 < Integer.MAX_VALUE) {
                ((AlarmManager) context.getSystemService(context.ALARM_SERVICE)).setTime(when);
            }
            SnakeBar.makeMsgSnake(context, "设置成功").show();
            dismiss();
            return result;
        } catch (SecurityException e) {
            e.printStackTrace();

            SnakeBar.makeMsgSnake(context, "系统未定义权限").show();
            dismiss();
            return result;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //取消
            case R.id.txt_cancel:
                dismiss();
                break;
            //确认
            case R.id.txt_confirm:
                String result = setSysDate();
                if(null != onDateTimeSetListener){
                    onDateTimeSetListener.onTimeSet(result);
                }
                break;
        }
    }
}
