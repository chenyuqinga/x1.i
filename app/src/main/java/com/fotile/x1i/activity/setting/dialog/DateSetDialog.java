package com.fotile.x1i.activity.setting.dialog;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.TimeSettingActivity;
import com.fotile.x1i.base.BaseDialog;
import com.fotile.x1i.widget.SnakeBar;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;

/**
 * 文件名称：DateSetDialog
 * 创建时间：2019/7/29 13:37
 * 文件作者：yaohx
 * 功能描述：系统设置日期dialog
 */
public class DateSetDialog extends BaseDialog implements View.OnClickListener {

    /**
     * 滚轮年
     */
    @BindView(R.id.wheel_view_year)
    WheelView wheelViewYear;

    /**
     * 滚轮月
     */
    @BindView(R.id.wheel_view_month)
    WheelView wheelViewMonth;

    /**
     * 滚轮天
     */
    @BindView(R.id.wheel_view_day)
    WheelView wheelViewDay;

    /**
     * 年份的最小值、最大值
     */
    private final int YEAR_MIN = 1980, YEAR_MAX = 2036;

    /**
     * 月份的最小值、最大值
     */
    private final int MONTH_MIN = 1, MONTH_MAX = 12;
    /**
     * 年份数据列表
     */
    private ArrayList<String> yearList = new ArrayList<>();
    /**
     * 月份数据列表
     */
    private ArrayList<String> monthList = new ArrayList<>();
    /**
     * 天数数据列表
     */
    private ArrayList<String> daythList = new ArrayList<>();
    /**
     * 大月(31天)天数数据列表
     */
    private ArrayList<String> bigMonthDaythList = new ArrayList<>();
    /**
     * 小月(30天)天数数据列表
     */
    private ArrayList<String> littleMonthDaythList = new ArrayList<>();
    /**
     * 二月天数数据列表
     */
    private ArrayList<String> februaryDaythList = new ArrayList<>();

    /**
     * 年、月、日数据列表适配器
     */
    private ArrayWheelAdapter yearListAdapter, monthListAdapter, daythListAdapter;

    /**
     * 二月天数的最大值
     */
    private int FEBRUARY_DAY_MAX = 28;

    /**
     * 天的最小值
     */
    private final int DAY_MIN = 1;
    /**
     * 大月天的最大值
     */
    private final int BIG_DAY_MAX = 31;
    /**
     * 小月天的最大值
     */
    private final int LITTLE_DAY_MAX = 30;

    @BindView(R.id.txt_cancel)
    TextView txtCancel;
    @BindView(R.id.txt_confirm)
    TextView txtConfirm;

    private TimeSettingActivity.OnDateTimeSetListener onDateTimeSetListener;

    public DateSetDialog(Context context) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_date_set;
    }

    private void initView() {
//        wheelViewYear.setMoveRange(20);
        txtCancel.setOnClickListener(this);
        txtConfirm.setOnClickListener(this);
    }

    public void setOnDateTimeSetListener(TimeSettingActivity.OnDateTimeSetListener listener){
        this.onDateTimeSetListener = listener;
    }

    private void initData() {
        Time time = new Time();
        time.setToNow();

        //年份的取值范围
        for (int i = YEAR_MIN; i <= YEAR_MAX; i++) {
            yearList.add(getShowTimeStr(i));
        }
        yearListAdapter = new ArrayWheelAdapter(yearList);
        wheelViewYear.setAdapter(yearListAdapter);
        wheelViewYear.setCurrentItem(yearList.indexOf(getShowTimeStr(time.year)));

        //月份的取值范围
        for (int i = MONTH_MIN; i <= MONTH_MAX; i++) {
            monthList.add(getShowTimeStr(i));
        }
        monthListAdapter = new ArrayWheelAdapter(monthList);
        wheelViewMonth.setAdapter(monthListAdapter);
        wheelViewMonth.setCurrentItem(monthList.indexOf(getShowTimeStr(time.month + 1)));

        //是闰年，求二月份的天数
        if (time.year % 4 == 0 && time.year % 100 != 0 || time.year % 400 == 0) {
            FEBRUARY_DAY_MAX = 29;
        } else {
            FEBRUARY_DAY_MAX = 28;
        }
        daythList.clear();
        for (int i = DAY_MIN; i <= BIG_DAY_MAX; i++) {
            bigMonthDaythList.add(getShowTimeStr(i));
        }
        for (int i = DAY_MIN; i <= LITTLE_DAY_MAX; i++) {
            littleMonthDaythList.add(getShowTimeStr(i));
        }
        for (int i = DAY_MIN; i <= FEBRUARY_DAY_MAX; i++) {
            februaryDaythList.add(getShowTimeStr(i));
        }
        switch (time.month + 1) {
            case 4:
            case 6:
            case 9:
            case 11:
                daythList.addAll(littleMonthDaythList);
                break;
            case 2:
                daythList.addAll(februaryDaythList);
                break;
            default:
                daythList.addAll(bigMonthDaythList);
                break;
        }
        daythListAdapter = new ArrayWheelAdapter(daythList);
        wheelViewDay.setAdapter(daythListAdapter);
        wheelViewDay.setCurrentItem(daythList.indexOf(getShowTimeStr(time.monthDay)));

        //set listener
        wheelViewMonth.setOnItemSelectedListener(monthSelectedListener);
        wheelViewYear.setOnItemSelectedListener(yearSelectedListener);
    }

    /**
     * 年份选择栏的滚动监听
     */
    OnItemSelectedListener yearSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(int index) {
            //将选择的天数记录进入stewReserveBean
            int year = Integer.parseInt((String) yearListAdapter.getItem(index));
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {//是闰年
                FEBRUARY_DAY_MAX = 29;
            } else {
                FEBRUARY_DAY_MAX = 28;
            }
            februaryDaythList.clear();
            for (int i = DAY_MIN; i <= FEBRUARY_DAY_MAX; i++) {
                februaryDaythList.add(getShowTimeStr(i));
            }

            //当前选择为2月份时
            if (wheelViewMonth.getCurrentItem() == 1) {
                daythList.clear();
                daythList.addAll(februaryDaythList);
                daythListAdapter.setData(daythList);
                wheelViewDay.setCurrentItem(0);
            }
        }
    };

    /**
     * 月份选择栏的滚动监听
     */
    OnItemSelectedListener monthSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(int index) {
            //将选择的天数记录进入stewReserveBean
            daythList.clear();
            switch (index + 1) {
                case 4:
                case 6:
                case 9:
                case 11:
                    daythList.addAll(littleMonthDaythList);
                    break;
                case 2:
                    daythList.addAll(februaryDaythList);
                    break;
                default:
                    daythList.addAll(bigMonthDaythList);
                    break;
            }
            daythListAdapter.setData(daythList);
            wheelViewDay.setCurrentItem(0);
        }
    };


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
        int year = Integer.parseInt((String) yearListAdapter.getData().get(wheelViewYear.getCurrentItem()));
        c.set(Calendar.YEAR, year);
        //月-月需要减1
        int month = Integer.parseInt((String) monthListAdapter.getData().get(wheelViewMonth.getCurrentItem())) - 1;
        c.set(Calendar.MONTH, month);
        //日
        int day = Integer.parseInt((String) daythListAdapter.getData().get(wheelViewDay.getCurrentItem()));
        c.set(Calendar.DAY_OF_MONTH, day);
        //时
        int hour = time.hour;
        c.set(Calendar.HOUR_OF_DAY, hour);
        //分
        int minute = time.minute;
        c.set(Calendar.MINUTE, minute);

        String result = year + "年" + (month + 1) + "月" + day + "日";

        //此处需要系统权限
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
                    onDateTimeSetListener.onDateSet(result);
                }
                break;
        }
    }

}
