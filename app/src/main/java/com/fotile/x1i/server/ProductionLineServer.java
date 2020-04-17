package com.fotile.x1i.server;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.fotile.common.util.Tool;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.widget.SnakeBar;

import java.io.IOException;

import fotile.device.cookerprotocollib.helper.DeviceControl;

/**
 * 文件名称：ProductionLineServer
 * 创建时间：2018/5/16 16:59
 * 文件作者：yaohx
 * 功能描述：产线自检
 */
public class ProductionLineServer extends Service {
    MediaPlayer mediaPlayer = new MediaPlayer();

    final int WHAT_LINE_START = 1;
    final int WHAT_LINE_END = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        DeviceControl.getInstance().closeDevice(true);
        //终止屏保
        ScreenTool.getInstance().addStop();
        Tool.lightScreen(this);
        handler.sendEmptyMessage(WHAT_LINE_START);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("music")) {
            playMusic();
        } else {
            new Thread(runnable).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void playMusic() {
        if (null != mediaPlayer && !mediaPlayer.isPlaying()) {
            AssetManager assetManager = getAssets();
            try {
                AssetFileDescriptor afd = assetManager.openFd("test.mp3");
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mediaPlayer) {
            mediaPlayer.stop();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                //灯
                Thread.sleep(1000);
                DeviceControl.getInstance().setLamp(DeviceControl.LAMP_CENTER, false);
                Thread.sleep(10000);

                //开风量
                Thread.sleep(5000);
                DeviceControl.getInstance().startWindControl(0xF0, true);
                Thread.sleep(8000);
                DeviceControl.getInstance().startWindControl(0xF5, true);
                Thread.sleep(8000);
                DeviceControl.getInstance().startWindControl(0xF9, true);

                //智能巡航
                Thread.sleep(8000);
                DeviceControl.getInstance().startCruise();
                Thread.sleep(8000);

                //播放音乐
                playMusic();
                //关闭所有功能
                Thread.sleep(5000);
                DeviceControl.getInstance().closeDevice(true);
                handler.sendEmptyMessage(WHAT_LINE_END);

            } catch (Exception e) {
            }

        }
    };

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_LINE_START:
                    SnakeBar.makeMsgSnake(ProductionLineServer.this,"产线自检开始").show();
                    break;
                case WHAT_LINE_END:
                    SnakeBar.makeMsgSnake(ProductionLineServer.this,"产线自检结束").show();
                    break;
            }
            return false;
        }
    });

//    private void startDeviceLinkageActivity(){
//        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setClass(ProductionLineServer.this, DeviceLinkageActivity.class);
//        ProductionLineServer.this.startActivity(intent);
//        handler.removeMessages(WHAT_CHECK_TIMEOUT);
//        ScreenTool.getInstance().addResume();
//    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
