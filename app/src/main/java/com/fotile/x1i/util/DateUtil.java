package com.fotile.x1i.util;

import android.content.Context;
import android.content.res.Resources;

import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.message.bean.MemorandumDb;
import com.fotile.message.bean.TimeBean;
import com.fotile.recipe.bean.screensaver.DigitalTime;
import com.fotile.x1i.R;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 文件名称：DateUtil
 * 创建时间：2017/8/18
 * 文件作者：wanghouyu
 * 功能描述：时间控件工具类
 */

public class DateUtil {


    /**
     * 获取当前时间
     *
     * @return HH:mm
     */
    public static String getCurrentTime() {
        long sysTime = System.currentTimeMillis();
        Date date = new Date(sysTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(date);
        return time;
    }

    /**
     * 获取12小时制时间
     *
     * @return
     */
    public static DigitalTime getDigitalTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm", Locale.CHINA);
        long timeMillis = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);
        int amOrPm = cal.get(Calendar.AM_PM);
        String time = dateFormatter.format(cal.getTime());
        DigitalTime result = new DigitalTime();
        result.setTime(time);
        result.setAmOrPm(amOrPm == 0 ? "AM" : "PM");
        return result;
    }

    /**
     * 十以下加零
     *
     * @param str
     * @return
     */
    public static String thanTen(int str) {

        String string = null;

        if (str < 10) {
            string = "0" + str;
        } else {

            string = "" + str;

        }
        return string;
    }

    public static TimeBean getTimeBeanFormatFromDetail(String day) {
        int year = Integer.parseInt(day.substring(day.indexOf("date") + 7, day.indexOf("date") + 11));
        int month = Integer.parseInt(day.substring(day.indexOf("date") + 12, day.indexOf("date") + 14));
        int dayOfMonth = Integer.parseInt(day.substring(day.indexOf("date") + 15, day.indexOf("date") + 17));
        int hour = Integer.parseInt(day.substring(day.indexOf("time") + 7, day.indexOf("time") + 9));
        int minute = Integer.parseInt(day.substring(day.indexOf("time") + 10, day.indexOf("time") + 12));
        int second = Integer.parseInt(day.substring(day.indexOf("time") + 13, day.indexOf("time") + 15));
        int dayOfYear = getDayOfYear(year, month, dayOfMonth);
        int dayOfWeek = getDayOfWeek(year, month, dayOfMonth);
        LogUtil.LOGE("-----时间getTimeBeanFormat", year + "---" + month + "---" + dayOfMonth + "---" + hour + "---" + minute + "---" + second + "---" + dayOfYear + "---" + dayOfWeek);
        return new TimeBean(year, month, dayOfMonth, 0, dayOfWeek, dayOfYear, hour, minute, second);
    }

    public static TimeBean getTimeBeanFormatFromDetailMemor(MemorandumDb memorandumDb) {
        LogUtil.LOGE("-----时间getTimeBeanFormatMemor", memorandumDb.getDate() + memorandumDb.getTime());
        String date = memorandumDb.getDate();
        String time = memorandumDb.getTime();
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));
        int second = Integer.parseInt(time.substring(6, 8));
        int dayOfYear = getDayOfYear(year, month, dayOfMonth);
        int dayOfWeek = getDayOfWeek(year, month, dayOfMonth);
        LogUtil.LOGE("-----时间getTimeBeanFormatMemor", year + "---" + month + "---" + dayOfMonth + "---" + hour + "---" + minute + "---" + second + "---" + dayOfYear + "---" + dayOfWeek);
        return new TimeBean(year, month, dayOfMonth, 0, dayOfWeek, dayOfYear, hour, minute, second);
    }

    public static SimpleDateFormat getDeatilDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return dateFormat;
    }

    /**
     * 一年中的第几天
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @return
     */
    public static int getDayOfYear(int year, int month, int dayOfMonth) {
        int leap;
        int sum = 0;
        //先计算某月以前月份的总天数
        switch (month) {
            case 1:
                sum = 0;
                break;
            case 2:
                sum = 31;
                break;
            case 3:
                sum = 59;
                break;
            case 4:
                sum = 90;
                break;
            case 5:
                sum = 120;
                break;
            case 6:
                sum = 151;
                break;
            case 7:
                sum = 181;
                break;
            case 8:
                sum = 212;
                break;
            case 9:
                sum = 243;
                break;
            case 10:
                sum = 273;
                break;
            case 11:
                sum = 304;
                break;
            case 12:
                sum = 334;
                break;
            default:
                System.out.println("data error");
                break;
        }
        //再加上某天的天数
        sum = sum + dayOfMonth;
        //判断是不是闰年
        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))
            leap = 1;
        else
            leap = 0;
        //如果是闰年且月份大于2,总天数应该加一天
        if (leap == 1 && month > 2)
            sum++;
        return sum;
    }

    //星期几，返回值-1即为当前星期
    public static int getDayOfWeek(int year, int month, int dayOfMonth) {
        // 计算这一年的元旦(XXXX年1月1号)是星期几
        int week = 1;//默认周一
        int d = year + (year - 1) / 4 - (year - 1) / 100 + (year - 1) / 400;
        d = d % 7; // d=0 Sunday, d=1 Monday
        int sum = getDayOfYear(year, month, dayOfMonth);
        int x = (sum + d - 1) % 7; // 计算这一天是星期几
        switch (x) {
            case 0:
                week = 1;
                break;
            case 1:
                week = 2;
                break;
            case 2:
                week = 3;
                break;
            case 3:
                week = 4;
                break;
            case 4:
                week = 5;
                break;
            case 5:
                week = 6;
                break;
            case 6:
                week = 7;
                break;
            default:
                break;
        }
        return week;
    }

    public static String showDate(Context context, TimeBean tb) {
        long sysTime = System.currentTimeMillis();
        LogUtil.LOGE("-----时间sysTime", sysTime);
        Date date = new Date(sysTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 一年中第D");
        String time = dateFormat.format(date);
        int curYear = Integer.parseInt(time.substring(0, time.indexOf("年")));
        int curMonth = Integer.parseInt(time.substring(5, time.indexOf("月")));
        int curDay = Integer.parseInt(time.substring(8, time.indexOf("日")));
        int dayOfYear = Integer.parseInt(time.substring(time.indexOf("第") + 1));
        String day = tb.getDateFormat();
        Resources resources = context.getResources();
        if (tb.getYear() == curYear && tb.getMonth() == curMonth && tb.getDay() == curDay) {
            return resources.getString(R.string.message_today);
        } else if (isYestday(day)) {
            return resources.getString(R.string.message_yesterday);
        } else if ((dayOfYear - tb.getDayOfYear() < 7 && tb.getYear() == curYear) || (curYear - tb.getYear() == 1 &&
                tb.getDay() - curDay > 24 && tb.getMonth() - curMonth == 11)) {
            String[] weekDays = {resources.getString(R.string.sunday), resources.getString(R.string.monday),
                    resources.getString(R.string.tuesday), resources.getString(R.string.wednesday), resources
                    .getString(R.string.thursday), resources.getString(R.string.friday), resources.getString(R.string
                    .saturday)};
            return weekDays[tb.getDayOfWeek() - 1];
        } else {
            return tb.getMessageShowDateFormat();
        }
    }

    /**
     * 判断是否为昨天(
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean isYestday(String day) {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        try {
            date = getDateFormat().parse(day);
        } catch (ParseException e) {

        }
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    public static SimpleDateFormat getDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat;
    }

    /**
     * 获取消息通知天数
     *
     * @param dayList
     * @return
     */
    public static List<String> getIntDateList(List<String> dayList) {

        List<String> strList = new ArrayList<>();
        for (String day : dayList) {
            if (!strList.contains(day)) {
                strList.add(day);
            }
        }
        return strList;
    }

    /**
     * 判断是否需要删除Date (
     *
     * @param dayList 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return
     * @throws ParseException
     */
    public static List<String> needDeleteDateList(List<String> dayList) {

        List<String> strList = new ArrayList<>();
        for (String day : dayList) {
            Calendar curCalendar = Calendar.getInstance();
            Date predate = new Date(System.currentTimeMillis());
            curCalendar.setTime(predate);

            Calendar preCalendar = Calendar.getInstance();
            try {
                Date date = getDateFormat().parse(day);
                preCalendar.setTime(date);
            } catch (ParseException e) {

            }
            if (preCalendar.get(Calendar.YEAR) == (curCalendar.get(Calendar.YEAR))) {
                int diffDay = curCalendar.get(Calendar.DAY_OF_YEAR) - preCalendar.get(Calendar.DAY_OF_YEAR);
                if (diffDay > 30) {
                    strList.add(day);
                }
            }
        }
        return strList;

    }

    public static List<String> needDeleteDateList(String deleteday, List<String> dayList) {

        List<String> strList = new ArrayList<>();
        for (String day : dayList) {
            Calendar curCalendar = Calendar.getInstance();
            Calendar preCalendar = Calendar.getInstance();
            try {
                Date predate = getDateFormat().parse(deleteday);
                curCalendar.setTime(predate);
                Date date = getDateFormat().parse(day);
                preCalendar.setTime(date);
            } catch (ParseException e) {

            }
            if (preCalendar.get(Calendar.YEAR) == (curCalendar.get(Calendar.YEAR))) {
                int diffDay = curCalendar.get(Calendar.DAY_OF_YEAR) - preCalendar.get(Calendar.DAY_OF_YEAR);
                if (diffDay == 0) {
                    strList.add(day);
                }
            }
        }
        return strList;
    }

    /**
     * 将秒转换为00分00秒格式
     *
     * @return 00分00秒
     */
    public static String changSecend2Minute(long secend) {
        String result = "";
        if (secend <= 0) {
            return "0秒";
        }
        long minute = 0;
        // 整除分钟余下的秒数
        long left_secend = 0;

        minute = secend / 60;
        left_secend = secend % 60;

        String minute_str = "";
        if (minute > 0) {
            minute_str = minute + "分";
        }

        String secend_str = left_secend + "秒";
        if (left_secend < 10) {
            secend_str = "0" + left_secend + "秒";
        }

        result = minute_str + secend_str;

        //        BigDecimal bigDecimal1 = new BigDecimal(secend);
        //        BigDecimal bigDecimal2 = new BigDecimal(60);

        return result;
    }

    public static TimeBean getCurrentTimeToBean() {

        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        return new TimeBean(year, month, dayOfMonth, weekOfYear, dayOfWeek, dayOfYear, hour, minute, second);
    }
}
