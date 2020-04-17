package com.fotile.x1i.activity.music;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fotile.common.util.ImageFrameHandlerUtil;
import com.fotile.common.util.Tool;
import com.fotile.x1i.R;
import com.fotile.x1i.dailog.NetWorkErrorDialog;
import com.fotile.x1i.dailog.VolumeDialog;
import com.fotile.x1i.server.screensaver.ScreenTool;
import com.fotile.x1i.util.AnimConstant;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.album.SubordinatedAlbum;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件名称：MusicPlayActivity
 * 创建时间：2018/11/8 11:03
 * 文件作者：chenyqi
 * 功能描述：
 */

public class MusicPlayActivity extends BaseMusicActivity {

    private static final String TAG = "MusicPlayActivity";
    /**
     * 播放时音乐名称
     */
    TextView tvPlayName;

    TextView tvPlayAuthor;
    /**
     * 播放
     */
    ImageView imgBtnPlay;
    /**
     * 下一首
     */
    ImageView imgBtnNext;

    /**
     * 上一首
     */
    ImageView imgBtnPre;

    /**
     * 音量调节
     */
    ImageView imgBtnSound;
    /**
     * 当前播放进度时长
     */
    TextView tvPlayTime;
    /**
     * 总共播放时长
     */
    TextView tvPlayTimeAll;
    /**
     * 播放进度SeekBar
     */
    SeekBar seekBarProgress;

    /**
     * 播放时音乐图像Img
     */
    ImageView imgPlayMusic;

    /**
     * 音乐播放时的动画
     */
    ImageView mIvPlayingAnim;

    /**
     * 音量manager
     */
    private AudioManager audioManager;
    /**
     * 喜马拉雅播放器
     */
    private XmPlayerManager xmPlayerManager;

    /**
     * 喜马拉雅
     */
    private CommonRequest xmCommonRequest;

    /**
     * 播放界面图片旋转动画
     */
    private Animation animationImage;
    /**
     * 更新播放进度
     */
    private boolean updateProgress = true;
    private Context context;

    /**
     * 是否是从主页跳转
     */
    private boolean isFromHome;

    /**
     * 网络异常提示
     */
    private NetWorkErrorDialog netWorkErrorDialog;

    /**
     * 音乐名称
     */
    String mTitle = null;

    /**
     * 音乐作者
     */
    String mName = null;

    /**
     * 封面url
     */
    String mCoverUrl = null;

    /**
     * 音乐播放动画图片集合
     */
    private int[] mMusicPlayAnim;

    /**
     * 音乐播放中的动画
     */
    private ImageFrameHandlerUtil mPlayingAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initView();
        initXm();
        //        initAnimation();
        if (isFromHome) {
            // TODO: 2019/6/4 更新页面信息是否应该放在onResume里？
            initMusicInfo();
        }
    }


    /**
     * 初始化动画
     */
    private void initAnimation() {
        animationImage = AnimationUtils.loadAnimation(context, R.anim.music_img_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();
        animationImage.setInterpolator(interpolator);
    }

    /**
     * 初始化喜马拉雅播放器
     */
    private void initXm() {

        xmCommonRequest = CommonRequest.getInstanse();
        xmPlayerManager = XmPlayerManager.getInstance(context);
        xmPlayerManager.setBreakpointResume(false);
        //        updatePreButton();
        updateButtonStatus();
        xmPlayerManager.addPlayerStatusListener(playerStatusListener);
        xmPlayerManager.addOnConnectedListerner(new XmPlayerManager.IConnectListener() {
            @Override
            public void onConnected() {
                xmPlayerManager.removeOnConnectedListerner(this);

                xmCommonRequest.setDefaultPagesize(100);
                xmPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
                Log.e(TAG, "播放器初始化成功");
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_play;
    }

    private void initView() {
        mMusicPlayAnim = getResIds(AnimConstant.ANIM_MUSIC_PLAY);
        tvPlayName = (TextView) findViewById(R.id.tv_play_name);
        tvPlayAuthor = (TextView) findViewById(R.id.tv_play_author);
        imgBtnPlay = (ImageView) findViewById(R.id.imgBtn_play);
        imgBtnNext = (ImageView) findViewById(R.id.imgBtn_next);
        imgBtnPre = (ImageView) findViewById(R.id.imgBtn_pre);
        tvPlayTime = (TextView) findViewById(R.id.tv_playTime);
        tvPlayTimeAll = (TextView) findViewById(R.id.tv_playTimeAll);
        seekBarProgress = (SeekBar) findViewById(R.id.sBar_progress);
        imgPlayMusic = (ImageView) findViewById(R.id.img_play_music);
        imgBtnSound = (ImageView) findViewById(R.id.imgBtn_Sound);
        mIvPlayingAnim = (ImageView) findViewById(R.id.iv_playing_anim);
        mPlayingAnim = new ImageFrameHandlerUtil(this, mIvPlayingAnim, mMusicPlayAnim, 40, false);

        isFromHome = getIntent().getBooleanExtra("isFromHome", false);
        imgBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //音乐播放与暂停
                if (xmPlayerManager.isPlaying()) {
                    xmPlayerManager.pause();
                } else {
                    xmPlayerManager.play();
                }
            }
        });
        imgBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tool.fastclick()) {
                    Log.e(TAG, "click to next");
                    xmPlayerManager.playNext();
                }
            }
        });
        imgBtnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tool.fastclick()) {
                    Log.e(TAG, "click to pre");
                    xmPlayerManager.playPre();
                    xmCommonRequest.setDefaultPagesize(200);
                }
            }
        });
        //调节音量
        imgBtnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tool.fastclick()) {
                    showSoundDialog();
                }
            }
        });
        seekBarProgress.setOnSeekBarChangeListener(new SeekBarChangeListener());
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        imgBtnPlay.setImageResource(R.mipmap.img_music_start);
        tvPlayName.setSelected(true);
        tvPlayAuthor.setSelected(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (xmPlayerManager.isPlaying()) {
            mPlayingAnim.setLoop(true);
            mPlayingAnim.start();
        }
    }

    /**
     * 显示音量调节界面
     */
    private void showSoundDialog() {
        VolumeDialog volumeDialog = new VolumeDialog(context);
        volumeDialog.show();
    }


    /**
     * 实现监听播放进度SeekBar的类
     */
    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            xmPlayerManager.seekToByPercent(seekBar.getProgress() / (float) seekBar.getMax());
            updateProgress = true;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            updateProgress = false;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }
    }


    @Override
    protected void onDestroy() {
        if (xmPlayerManager != null) {
            xmPlayerManager.removePlayerStatusListener(playerStatusListener);
        }
        if (null != netWorkErrorDialog && netWorkErrorDialog.isShowing()) {
            netWorkErrorDialog.dismiss();
        }
        super.onDestroy();
    }

    private IXmPlayerStatusListener playerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundPrepared() {
            Log.e(TAG, "onSoundPrepared");
            seekBarProgress.setEnabled(true);
        }

        /**
         *切歌
         * @param laModel 上一首model,可能为空
         * @param curModel 下一首model
         */
        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            Log.e(TAG, "onSoundSwitch index:" + (curModel == null ? null : curModel.toString()));
            //            updatePreButton();
            PlayableModel model = xmPlayerManager.getCurrSound();
            if (model != null) {
                String title = null;
                String name = null;
                String coverUrl = null;
                int time = 0;
                if (model instanceof Track) {
                    Track info = (Track) model;
                    title = info.getTrackTitle();
                    name = info.getAnnouncer().getNickname();
                    coverUrl = info.getCoverUrlLarge();
                    time = info.getDuration();
                    Log.e(TAG, "music_track: " + info);
                }
                Log.e(TAG, "music_title_switch: " + title);
                tvPlayName.setText(title);
                //切换歌曲时，时间改问题，默认时间00
                tvPlayTime.setText(formatMusicTime(0));
                tvPlayTimeAll.setText(formatMusicTime(time * 1000));
                if (name != null) {
                    tvPlayAuthor.setText(name);
                } else {
                    tvPlayAuthor.setText(getResources().getString(R.string.unknown_author));
                }
                Glide.with(context).load(coverUrl).into(imgPlayMusic);
            }
            updateButtonStatus();
            ScreenTool.getInstance().addResetData("music_switch");
        }

        /**
         * 播放停止
         */
        @Override
        public void onPlayStop() {
            Log.e(TAG, "onPlayStop");
            imgBtnPlay.setImageResource(R.mipmap.img_music_start);
            mPlayingAnim.stop();
        }

        /**
         * 播放开始
         */
        @Override
        public void onPlayStart() {
            Log.e(TAG, "onPlayStart");
            imgBtnPlay.setImageResource(R.mipmap.img_music_pause);
            mPlayingAnim.setLoop(true);
            mPlayingAnim.start();
            //音乐播放开始，重置屏保
            //            ScreenTool.getInstance().addResetData("音乐播放开始");
        }


        /**
         * 播放进度回调
         * @param currPos
         * @param duration
         */
        @Override
        public void onPlayProgress(int currPos, int duration) {
            Log.e(TAG, "currPos: " + currPos + ", duration: " + duration);
            String coverUrl = "";
            String newTitle = null;
            String newName = null;
            PlayableModel info = xmPlayerManager.getCurrSound();
            if (info != null) {
                if (info instanceof Track) {
                    newTitle = ((Track) info).getTrackTitle();
                    newName = ((Track) info).getAnnouncer().getNickname();
                    coverUrl = ((Track) info).getCoverUrlLarge();
                }
            }

            if (!TextUtils.equals(mTitle, newTitle)) {
                mTitle = newTitle;
                Log.e(TAG, "music_title_progress: " + mTitle);
                tvPlayName.setText(mTitle);
            }
            if (!TextUtils.equals(mName, newName)) {
                mName = newName;
                if (mName != null) {
                    tvPlayAuthor.setText(mName);
                } else {
                    tvPlayAuthor.setText(getResources().getString(R.string.unknown_author));
                }
            }
            if (!TextUtils.equals(mCoverUrl, coverUrl)) {
                mCoverUrl = coverUrl;
                Log.e(TAG, "cover url: " + mCoverUrl);
                Glide.with(context).load(mCoverUrl).into(imgPlayMusic);
            }
            tvPlayTime.setText(formatMusicTime(currPos));
            tvPlayTimeAll.setText(formatMusicTime(duration));
            if (updateProgress && duration != 0) {
                seekBarProgress.setProgress((int) (100 * currPos / (float) duration));
            }
        }

        /**
         * 播放停止
         */
        @Override
        public void onPlayPause() {
            Log.e(TAG, "onPlayPause");
            mPlayingAnim.pause();
            imgBtnPlay.setImageResource(R.mipmap.img_music_start);
        }

        /**
         * 播放停止
         */
        @Override
        public void onSoundPlayComplete() {
            Log.e(TAG, "onSoundPlayComplete");
            imgBtnPlay.setImageResource(R.mipmap.img_music_start);
        }

        /**
         * 播放错误
         */
        @Override
        public boolean onError(XmPlayerException exception) {
            Log.e(TAG, "onError " + exception.getMessage());
            imgBtnPlay.setImageResource(R.mipmap.img_music_start);
            if (!Tool.isNetworkAvailable(context) || (!TextUtils.isEmpty(exception.getMessage()) &&
                                                      exception.getMessage().contains(
                                                              "what = 8, extra = -1004"))) {
                if (null == netWorkErrorDialog) {
                    netWorkErrorDialog = new NetWorkErrorDialog(context);
                }
                netWorkErrorDialog.show();

            }
            return false;
        }

        /**
         * 缓冲进度回调
         */
        @Override
        public void onBufferProgress(int position) {
            seekBarProgress.setSecondaryProgress(position);
        }

        /**
         * 开始缓冲
         */
        public void onBufferingStart() {
            seekBarProgress.setEnabled(false);

        }

        /**
         * 结束缓冲
         */
        public void onBufferingStop() {
            seekBarProgress.setEnabled(true);
        }

    };

    /**
     * 更新上下按钮的状态
     */
    private void updateButtonStatus() {
        imgBtnPre.setEnabled(xmPlayerManager.hasPreSound());
        imgBtnNext.setEnabled(xmPlayerManager.hasNextSound());
    }

    @Override
    public void finish() {
        super.finish();
        if (isFromHome) {
            startMusicTrackActivity();
        }
        xmPlayerManager.stop();

    }

    /**
     * 跳转至专辑列表页面
     */
    private void startMusicTrackActivity() {
        //获取当前音乐所属专辑并跳转至专辑列表页面
        PlayableModel model = xmPlayerManager.getCurrSound();
        String albumTitle = "";
        String albumId = "";
        if (model != null) {
            if (model instanceof Track) {
                Track info = (Track) model;
                SubordinatedAlbum album = info.getAlbum();
                if (album != null) {
                    albumId = albumId + album.getAlbumId();
                    albumTitle = album.getAlbumTitle();
                }
            }
            Intent intent = new Intent(this, MusicTrackActivity.class);
            intent.putExtra("title", albumTitle);
            intent.putExtra("albumId", albumId);
            intent.putExtra("isFromHome", true);
            startActivity(intent);
        }
    }

    private void initMusicInfo() {
        xmPlayerManager.seekTo(xmPlayerManager.getPlayCurrPositon());
        imgBtnPlay.setImageResource(
                xmPlayerManager.isPlaying() ? R.mipmap.img_music_pause : R.mipmap.img_music_start);
        PlayableModel model = xmPlayerManager.getCurrSound();
        if (model != null) {
            String title = null;
            String name = null;
            String coverUrl = null;
            if (model instanceof Track) {
                Track info = (Track) model;
                title = info.getTrackTitle();
                name = info.getAnnouncer().getNickname();
                coverUrl = info.getCoverUrlLarge();
            }
            Log.e(TAG, "music_title_init: " + title);
            tvPlayName.setText(title);
            if (name != null) {
                tvPlayAuthor.setText(name);
            } else {
                tvPlayAuthor.setText(getResources().getString(R.string.unknown_author));
            }
            Glide.with(context).load(coverUrl).into(imgPlayMusic);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "status: " + xmPlayerManager.getPlayerStatus());
        if (xmPlayerManager.getPlayerStatus() == 7) {
            if (null == netWorkErrorDialog) {
                netWorkErrorDialog = new NetWorkErrorDialog(context);
            }
            netWorkErrorDialog.show();
        }
    }

    /**
     * HH:mm:ss
     */
    private static String formatMusicTime(long ms) {
        int ONE_HOUR = 3600000;
        SimpleDateFormat format = null;
        if (ms < ONE_HOUR) {
            format = new SimpleDateFormat("mm:ss");
        } else {
            format = new SimpleDateFormat("HH:mm:ss");
        }
        Date date = new Date(ms);
        return format.format(date);
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    public int[] getResIds(String[] imgNames) {
        String packageName = this.getPackageName();
        int[] resIds = new int[imgNames.length];
        try {
            for (int i = 0; i <= imgNames.length - 1; i++) {
                resIds[i] = this.getResources().getIdentifier(imgNames[i], "mipmap", packageName);
                //                Log.w(TAG, "res: " + resIds[i] + ", " +
                //                           mContext.getResources().openRawResource(resIds[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resIds;
    }
}

