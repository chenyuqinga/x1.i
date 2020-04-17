package com.fotile.message.util;

import android.content.Context;
import android.content.res.Resources;


import com.fotile.message.R;
import com.fotile.message.bean.TimeBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 文件名称：MessageDateUtil
 * 创建时间：2017/8/18
 * 文件作者：wanghouyu
 * 功能描述：时间控件工具类
 */

public class MessageDateUtil {


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
        Calendar cal = Calendar.getInstance();

        try {
            Date date = getDeatilDateFormat().parse(day);
            cal.setTime(date);
        } catch (ParseException e) {

        }

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

    public static SimpleDateFormat getDeatilDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return dateFormat;
    }

    public static String showDate(Context context, TimeBean tb) {

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH) + 1;
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);
        int curWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
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
