package com.fotile.x1i.util;

import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by caixd on 2016/11/7.
 */

public class CmdUtil {


    /**
     * 通过root 修改系统时间
     *
     * @param ServiceTime
     */
    public static void timeSynchronization(Date ServiceTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.kkmmss", Locale.getDefault());
        String datetime = sdf.format(ServiceTime);
        String command = "date -s  \"" + datetime + "\"";
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "settings put global auto_time 0"});
            Runtime.getRuntime().exec(new String[]{"su", "-c", command});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过root 自动设置时间
     *
     * @param isAuto
     */
    public static void autoSetTime(boolean isAuto) {
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "settings put global auto_time " + (isAuto ? 1 : 0)});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 杀死systemui, 以屏蔽系统通知
     */
    public static void killSystemUI(){
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "service call activity 42 s16 com.android.systemui"}); // WAS
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 开启systemui, 以显示状态栏
     */
    public static void startSystemUI(){
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "am startservice -n com.android.systemui/.SystemUIService"});
            Log.d("CmdUtil", "startSystemUI: "+proc.toString());
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    /**
//     * 给固件发送广播修改时间
//     *
//     * @param year
//     * @param month
//     * @param day
//     * @param hour
//     * @param minute
//     */
//    public static void sendSetTimeBroadcast(int year, int month, int day, int hour, int minute) {
//        Intent intent = new Intent();
//        intent.setAction("com.android.system.settime");
//        intent.putExtra("year", year);
//        intent.putExtra("month", month);
//        intent.putExtra("day", day);
//        intent.putExtra("hour", hour);
//        intent.putExtra("minute", minute);
//        CoreApplication.getInstance().sendBroadcast(intent);
//    }

//    /**
//     * 给固件发送广播自动设置时间
//     *
//     * @param isAuto
//     */
//    public static void sendAutoSetTime(boolean isAuto) {
//        Intent intent = new Intent();
//        intent.setAction("com.android.system.autotime");
//        intent.putExtra("enable", isAuto ? 1 : 0);
//        CoreApplication.getInstance().sendBroadcast(intent);
//    }

    public static void oneKeySetWifi(){
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "setup -p 3"});
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("CmdUtil", "oneKeySetWifi: "+e.getMessage());
        }
    }

}
