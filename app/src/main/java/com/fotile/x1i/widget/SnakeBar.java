package com.fotile.x1i.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.fotile.x1i.R;

import java.util.LinkedList;
import java.util.Queue;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
  * 文件名称：SnakeBar
  * 创建时间：2019/5/10 17:40
  * 文件作者：yaohx
  * 功能描述：提示SnakeBar 弹框
  */
public class SnakeBar {
    public static final int LENGTH_LONG = 4000;
    public static final int LENGTH_SHORT = 2000;
    private static WindowManager windowManager;
    private static WindowManager.LayoutParams layoutParams;
    private static SnakeBar snackBar;
    private static LayoutInflater inflater;
    /**
     * 缓存队列
     */
    private static Queue<SnakeView> queue = new LinkedList<SnakeView>();
    /**
     * 是否正在显示中
     */
    private volatile boolean isShowing;

    final static int MSG_HIDE = 1001;
    final static int MSG_NEXT = 1002;

    private static SnakeBar make(Context context, String content, int duration, int resId) {
        init(context);
        SnakeView snackView = new SnakeView(content, duration, resId);
        queue.add(snackView);
        return snackBar;
    }

    private static SnakeBar make(Context context, int resId, String title, String content, int duration, int width,
            int height) {
        init(context, width, height);
//        SnakeView snackView = new SnakeView(resId, title, content, duration);
        SnakeView snackView = new SnakeView(content, duration, resId);
        queue.add(snackView);
        return snackBar;
    }

    /**
     * 无title提示
     *
     * @param context
     * @param content
     * @return
     */
    public static SnakeBar makeToastSnake(Context context, String content) {
        return make(context, -1, null, content, LENGTH_SHORT, 480, 110);
    }


    /**
     * 创建一个留言\备忘录提示
     */
    public static SnakeBar makeMsgSnake(Context context, String content) {
        return make(context, content, LENGTH_SHORT, R.mipmap.icon_snake_tip_message);
    }

    /**
     * 创建一个定时结束提示
     */
    public static SnakeBar makeTimerSnake(Context context, String content) {
        return make(context, content, LENGTH_SHORT, R.mipmap.icon_snake_tip_timer);
    }

    /**
     * 创建一个远程控制提示
     */
    public static SnakeBar makeControlSnake(Context context, String content) {
        return make(context, content, LENGTH_SHORT, R.mipmap.icon_snake_tip_control);
    }

    /**
     * 创建一个设备联动提示
     */
    public static SnakeBar makeDeviceLinkMsgSnake(Context context, String content) {
        return make(context, content, LENGTH_SHORT, R.mipmap.icon_snake_tip_link);
    }

    public static SnakeBar makeSnake(Context context, String content) {
        return make(context, content, LENGTH_SHORT, -1);
    }

    public synchronized void show() {
        if (!isShowing && queue.size() > 0) {
            Action1<String> action = new Action1<String>() {
                @Override
                public void call(String s) {
                    SnakeView snackView = queue.poll();
                    isShowing = true;
                    windowManager.addView(snackView.view, layoutParams);
                    //延迟隐藏
                    Message message = new Message();
                    message.obj = snackView;
                    message.what = MSG_HIDE;
                    handler.sendMessageDelayed(message, snackView.duration);
                }
            };

            Observable observable = Observable.just("");
            observable.subscribeOn(Schedulers.immediate())//指定被观察者执行的线程
                    .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                    .subscribe(action);
        }
    }


    Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HIDE:
                    hide((SnakeView) msg.obj);
                    break;
                //显示下一个
                case MSG_NEXT:
                    if (null != queue && queue.size() > 0) {
                        show();
                    }
                    break;

            }
            return false;
        }
    });

    private void hide(SnakeView snackView) {
        if (null != snackView) {
            windowManager.removeView(snackView.view);
            isShowing = false;
            //每一个显示之间间隔300毫秒
            handler.sendEmptyMessageDelayed(MSG_NEXT, 300);
        }
    }

    private static void init(Context context) {
        if (null == windowManager) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.height = 90;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            //            layoutParams.windowAnimations = R.style.top_snack_bar_anim_style;
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            layoutParams.gravity = Gravity.CENTER;
            //设置图片格式，效果为背景透明
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.x = 0;
            layoutParams.y = 0;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams
                    .FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        if (null == snackBar) {
            snackBar = new SnakeBar();
        }
        if (null == inflater) {
            inflater = LayoutInflater.from(context);
        }
    }

    private static void init(Context context, int width, int height) {
        if (null == windowManager) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        //更改高度需要重新设置，不然每一次都是上一次的宽度高度
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = height;
        layoutParams.width = width;
        //            layoutParams.windowAnimations = R.style.top_snack_bar_anim_style;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.gravity = Gravity.CENTER;
        //设置图片格式，效果为背景透明
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams
                .FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        if (null == snackBar) {
            snackBar = new SnakeBar();
        }
        if (null == inflater) {
            inflater = LayoutInflater.from(context);
        }
    }


    static class SnakeView {
        SnakeView(String content, int duration, int resId) {
            view = inflater.inflate(R.layout.layout_snake, null);
            //图标
            ImageView imgSnake = (ImageView) view.findViewById(R.id.img_snake);
            if (resId == -1) {
                imgSnake.setVisibility(View.GONE);
            } else {
                imgSnake.setVisibility(View.VISIBLE);
                imgSnake.setImageResource(resId);
            }

            //内容
            TextView txtSnakeContent = (TextView) view.findViewById(R.id.txt_snake_tip);
            txtSnakeContent.setText(content);
            this.duration = duration;
        }

        View view;
        int duration;
    }
}
