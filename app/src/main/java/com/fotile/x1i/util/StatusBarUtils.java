package com.fotile.x1i.util;

import android.content.Context;
import android.content.Intent;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2017/1/6
 *     desc  :
 * </pre>
 */
public class StatusBarUtils {

    /**yunos系统隐藏状态栏
     * @param context
     */
    public static void hideStatusBar(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.hideNaviBar");
        intent.putExtra("hide", true);
        context.sendBroadcast(intent);
    }

    public static void showStatusBar(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.hideNaviBar");
        intent.putExtra("hide", false);
        context.sendBroadcast(intent);
    }
}
