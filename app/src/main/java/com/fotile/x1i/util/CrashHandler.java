package com.fotile.x1i.util;

import android.content.Context;
import android.os.Environment;


import com.fotile.common.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件名称：CrashHandler
 * 创建时间：2018/1/15 15:40
 * 文件作者：yaohx
 * 功能描述：异常处理 CrashHandler
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    //文件路径
    private final String PATH = Constant.FILE_CACHE_FOLDER + "/crash";
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private static CrashHandler mCrashHandler = new CrashHandler();
    private Context mContext;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return mCrashHandler;
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultCrashHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
//            //关闭蓝牙
//            BlueToothTool.getInstance(mContext).closeBlueTooth();
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//
//            }
//            //重启app
//            Process.killProcess(Process.myPid());
//            System.exit(1);
//            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("com.fotile.c2i");
//            mContext.startActivity(intent);
        }
    }

    /**
     * 自定义错误处理,收集错误信息
     *
     * @param ex
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        writeToSDcard(ex);
        return true;
    }

    //将异常写入文件
    private void writeToSDcard(Throwable ex) {
        //如果没有SD卡，直接返回
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }

        long currenttime = System.currentTimeMillis();
        String time_day = new SimpleDateFormat("yyyy-MM-dd").format(new Date(currenttime));
        //crash目录
        String folder = PATH + "/" + time_day;
        FileUtil.createFolder(folder);

        //创建crash文件
        String time = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date(currenttime));
        String filename = "crash-" + time + ".txt";
        File exfile = new File(folder + "/" + filename);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(exfile)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pw.println(time);
        ex.printStackTrace(pw);
        pw.close();

    }
}
