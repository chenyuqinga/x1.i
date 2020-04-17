package com.fotile.common.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.widget.ImageView;

import com.mrwang.imageframe.ImageFrameHandler;

/**
 * 文件名称：ImageFrameHandlerUtil
 * 创建时间：2018/4/13 19:30
 * 文件作者：yaohx
 * 功能描述：内存优化类
 */
public class ImageFrameHandlerUtil {
    private static final String TAG = ImageFrameHandlerUtil.class.getSimpleName();
    private ImageView imageView;
    private ImageFrameHandler build;
    private ImgFramePlayFinishListener mListener;

    /**
     * 默认使用的是80毫秒每帧
     *
     * @param context
     * @param m
     * @param resId
     */
    public ImageFrameHandlerUtil(Context context, ImageView m, int[] resId, int duration) {
        this.imageView = m;
        int fps = 1000 / duration;

        build = new ImageFrameHandler.ResourceHandlerBuilder(context.getResources(), resId).setFps(fps)// 设置fps(每秒播放多少帧)
                .setLoop(true)// 设置是否循环播放
                .openLruCache(true)// 设置是否开启LRU缓存,如果不循环,建议不开启,如果循环,建议开启,不过多次测试性能相差并不大
                .setOnImageLoaderListener(new ImageFrameHandler.OnImageLoadListener() {
                    //创建一个监听器 监听解析成功 以及完成解析
                    @Override
                    public void onImageLoad(BitmapDrawable drawable) {
                        //给你的View设置上图片
                        ViewCompat.setBackground(imageView, drawable);
                    }

                    @Override
                    public void onPlayFinish() {
                        if (mListener != null) {
                            mListener.onPlayFinish();
                        }

                    }
                }).build();// 构建一个Handler 来处理
    }

    public ImageFrameHandlerUtil(Context context, final ImageView m, int[] resId, int interval,
            boolean isLoop) {
        int fps = 1000 / interval;
        build = new ImageFrameHandler.ResourceHandlerBuilder(context.getResources(), resId).setFps(
                fps)// 设置fps(每秒播放多少帧)
                .setLoop(isLoop)// 设置是否循环播放
                .openLruCache(true)// 设置是否开启LRU缓存,如果不循环,建议不开启,如果循环,建议开启,不过多次测试性能相差并不大
                .setOnImageLoaderListener(new ImageFrameHandler.OnImageLoadListener() {
                    //创建一个监听器 监听解析成功 以及完成解析
                    @Override
                    public void onImageLoad(BitmapDrawable drawable) {
                        //给你的View设置上图片
                        ViewCompat.setBackground(m, drawable);
                    }

                    @Override
                    public void onPlayFinish() {
                        if (mListener != null) {
                            mListener.onPlayFinish();
                        }
                    }
                })//只有在Loop为false时设置的listener才有效
                .build();// 构建一个Handler 来处理
    }

    public void start() {
        Log.e(TAG, "start: " + build);
        if (null != build) {
            build.start();
        }
    }

    public void stop() {
        Log.e(TAG, "stop: " + build);
        if (null != build) {
            build.stop();
        }
    }

    public void stop(boolean quitLooper) {
        Log.e(TAG, "stop: " + build);
        if (null != build) {
            build.stop(quitLooper);
        }
    }


    public void pause() {
        Log.e(TAG, "pause: " + build);
        if (null != build) {
            build.pause();
        }
    }

    public void setInterval(int interval) {
        int fps = 1000 / interval;
        if (null != build) {
            build.setFps(fps);
        }
    }

    public void setLoop(boolean isLoop) {
        if (null != build) {
            build.setLoop(isLoop);
        }
    }

    public void setPlayFinishListener(ImgFramePlayFinishListener listener) {
        this.mListener = listener;
    }

    public interface ImgFramePlayFinishListener {
        void onPlayFinish();
    }

}
