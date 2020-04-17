package com.fotile.x1i.activity.music;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fotile.common.util.ImageFrameHandlerUtil;
import com.fotile.music.manager.MusicManager;
import com.fotile.music.model.CommanConst;
import com.fotile.x1i.R;
import com.fotile.x1i.bean.event.FinishActivityMessage;
import com.fotile.x1i.dailog.VolumeDialog;
import com.fotile.x1i.util.AnimConstant;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


/**
 * 文件名称：MusicPlayActivity
 * 创建时间：2018/11/8 11:03
 * 文件作者：chenyqi
 * 功能描述：
 */

public class DuiMusicPlayActivity extends BaseMusicActivity implements View.OnClickListener {

    private static final String TAG = "MusicPlayActivity";

    private PlaybackStateCompat mLastPlaybackState;

    private final ScheduledExecutorService mExecutorService = Executors
            .newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;
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
     * 音乐播放动画图片集合
     */
    private int[] mMusicPlayAnim;

    /**
     * 音乐播放中的动画
     */
    private ImageFrameHandlerUtil mPlayingAnim;

    //    private Button mSetSingleLoopBtn;
    //    private Button mCloseSingleLoopBtn;
    //    private Button mReplayBtn;

    /**
     * 音量manager
     */
    private AudioManager audioManager;
    private Context context;
    private long mCurrentDuration;
    private final Handler mHandler = new Handler();
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        context = this;
        initView();
        //        initAnimation();
        initMusicData();
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();
        if (MusicManager.getInstance() != null) {
            MusicManager.getInstance().connect();
        }
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        if (MusicManager.getInstance() != null) {
            MusicManager.getInstance().removeAudioStateListener(mOnAudioStatusChangeListener);
            MusicManager.getInstance().disconnect();
        }
        stopSeekbarUpdate();
        mExecutorService.shutdown();
        super.onDestroy();
    }


    /**
     * 初始化动画
     */
    //    private void initAnimation() {
    //        animationImage = AnimationUtils.loadAnimation(context, R.anim.music_img_rotate);
    //        LinearInterpolator interpolator = new LinearInterpolator();
    //        animationImage.setInterpolator(interpolator);
    //    }
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

        tvPlayName.setSelected(true);
        tvPlayAuthor.setSelected(true);

        //        isFromHome = getIntent().getBooleanExtra("isFromHome", false);
        imgBtnPlay.setOnClickListener(this);
        imgBtnNext.setOnClickListener(this);
        imgBtnPre.setOnClickListener(this);
        imgBtnSound.setOnClickListener(this);
        //        mSetSingleLoopBtn.setOnClickListener(this);
        //        mCloseSingleLoopBtn.setOnClickListener(this);
        //        mReplayBtn.setOnClickListener(this);
        seekBarProgress.setOnSeekBarChangeListener(seekBarChangeListener);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        imgBtnPlay.setImageResource(R.mipmap.btn_music_play);

        Intent intent = getIntent();
        if (intent != null) {
            MediaMetadataCompat metadata = intent.getParcelableExtra(
                    CommanConst.EXTRA_CURRENT_MEDIA_DESCRIPTION);
            if (metadata != null) {
                updateMediaDescription(metadata);
            }
        }
        mPlayingAnim = new ImageFrameHandlerUtil(this, mIvPlayingAnim, mMusicPlayAnim, 40, true);

    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            tvPlayTime.setText(DateUtils.formatElapsedTime((mCurrentDuration * progress / 100000)));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            stopSeekbarUpdate();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            long currentPosition = (long) (mCurrentDuration * seekBar.getProgress() /
                                           (float) seekBar.getMax());
            Log.e(TAG, "seek bar progress: " + seekBar.getProgress() + ", seek bar max: " +
                       seekBar.getMax() + ", seek to " + currentPosition);
            if (MusicManager.getInstance() != null) {
                MusicManager.getInstance().seekTo(currentPosition);
            }
            scheduleSeekbarUpdate();
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgBtn_play) {
            if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                if (MusicManager.getInstance() != null) {
                    MusicManager.getInstance().pause();
                }
                Log.e(TAG, "set repeat mode " + PlaybackStateCompat.REPEAT_MODE_ONE);
            } else if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PAUSED) {
                if (MusicManager.getInstance() != null) {
                    MusicManager.getInstance().play();
                }
            }

        } else if (id == R.id.imgBtn_next) {
            if (MusicManager.getInstance() != null) {
                MusicManager.getInstance().skipToNext();
            }
            Log.e(TAG, "skipToNext");

        } else if (id == R.id.imgBtn_pre) {
            if (MusicManager.getInstance() != null) {
                MusicManager.getInstance().skipToPrevious();
            }
            Log.e(TAG, "skipToPrevious");

        } else if (id == R.id.imgBtn_Sound) {// TODO: 2019/3/18  show sound dialog
            showSoundDialog();
        } else {

        }
    }

    @Override
    public void onActivityFinish(FinishActivityMessage message) {
        super.onActivityFinish(message);
    }

    @Override
    public void finish() {
        super.finish();
        Log.e(TAG, "finish and stop dui music");
        MusicManager.getInstance().stop();
    }

    /**
     * 显示音量调节界面
     */
    private void showSoundDialog() {
        VolumeDialog volumeDialog = new VolumeDialog(context);
        volumeDialog.show();
    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    mHandler.post(mUpdateProgressTask);
                }
            }, PROGRESS_UPDATE_INITIAL_INTERVAL, PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private void initMusicData() {
        MusicManager.getInstance().addOnAudioStatusListener(mOnAudioStatusChangeListener);
        Log.e("MusicManager", "add music listener in dui music play activity");
        MusicManager.getInstance().init(this, null, false);
    }

    private MusicManager.OnAudioStatusChangeListener mOnAudioStatusChangeListener = new MusicManager.OnAudioStatusChangeListener() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            Log.e(TAG, "onPlaybackStateChanged: " + state);
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            Log.e(TAG, "onMetadataChanged: " + metadata);
            updateMediaDescription(metadata);
            updateDuration(metadata);
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            Log.e(TAG, "onQueueChanged: " + queue);

        }

        @Override
        public void onProgressInit(PlaybackStateCompat state) {
            Log.e(TAG, "onProgressInit: " + state);
            updateProgress();
            if (state != null && (state.getState() == PlaybackStateCompat.STATE_PLAYING ||
                                  state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
                scheduleSeekbarUpdate();
            }
        }
    };

    private void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            long timeDelta =
                    SystemClock.elapsedRealtime() - mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        if (mCurrentDuration != 0) {
            seekBarProgress.setProgress((int) (100 * currentPosition / mCurrentDuration));
        }
    }

    private void updateMediaDescription(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        Log.d(TAG, "updateMediaDescription called ");
        tvPlayName.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        tvPlayAuthor.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        Glide.with(context).load(metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))
                .into(imgPlayMusic);
    }


    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        Log.d(TAG, "updateDuration called ");
        Log.e(TAG, "cc" + metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        mCurrentDuration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        Log.e(TAG, "mCurrentDuration: " + mCurrentDuration);
        tvPlayTimeAll.setText(DateUtils.formatElapsedTime(mCurrentDuration / 1000));
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        mLastPlaybackState = state;

        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                imgBtnPlay.setImageResource(R.mipmap.btn_music_pause);
                //                imgPlayMusic.startAnimation(animationImage);
                mPlayingAnim.start();
                scheduleSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                imgBtnPlay.setImageResource(R.mipmap.btn_music_play);
                //                imgPlayMusic.clearAnimation();
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                imgBtnPlay.setImageResource(R.mipmap.btn_music_play);
                //                imgPlayMusic.clearAnimation();
                mPlayingAnim.stop();
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                //                mPlayPause.setVisibility(INVISIBLE);
                //                mLoading.setVisibility(VISIBLE);
                //                mLine3.setText(R.string.loading);
                stopSeekbarUpdate();
                break;
            default:
                Log.e(TAG, "Unhandled state " + state.getState());
                break;
        }

        imgBtnNext.setVisibility((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) == 0
                                 ? INVISIBLE
                                 : VISIBLE);
        imgBtnPre.setVisibility(
                (state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) == 0
                ? INVISIBLE
                : VISIBLE);
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

