package com.fotile.x1i.activity.screensaver;

import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.fotile.x1i.R;
import com.fotile.x1i.widget.BottomView;
import com.fotile.x1i.widget.TopBar;


/**
 * 文件名称：VideoViewActivity
 * 创建时间：2019/7/9 10:23
 * 文件作者：yaohx
 * 功能描述：方太画报
 */
public class VideoViewActivity extends BaseClockActivity {

    VideoView videoView;

    ImageView img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        initData();

        BottomView.getInstance(this).hide();
        TopBar.getInstance(this).hide();
    }

    private void initData() {
        videoView = (VideoView) findViewById(R.id.video_view);
        img = (ImageView) findViewById(R.id.img);

        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.play));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.play));
                videoView.start();
            }
        });
        videoView.start();

        img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                videoView.pause();
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView = null;
    }
}
