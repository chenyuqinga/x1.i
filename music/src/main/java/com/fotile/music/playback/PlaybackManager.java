/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fotile.music.playback;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.fotile.music.model.MusicBean;
import com.fotile.music.model.MusicProvider;
import com.fotile.music.model.MusicSource;
import com.fotile.music.utils.QueueHelper;

import java.util.ArrayList;

/**
 * Manage the interactions among the container service, the queue manager and the actual playback.
 * 管理service容器、队列管理者和playback实例之间的交互
 */
public class PlaybackManager implements Playback.Callback {

    private static final String TAG = PlaybackManager.class.getSimpleName();

    /**
     * key
     */
    // 音频队列数据
    public static final String KEY_MUSIC_QUEUE = "com.netease.awakeing.music.KEY_MUSIC_QUEUE";
    // 更新队列
    public static final String CUSTOM_ACTION_MUSIC_UPDATE_QUNEN = "com.netease.awakeing.music.MUSIC_QUEUE_UPDATE";
    // 音频队列的title数据
    public static final String KEY_MUSIC_QUEUE_TITLE = "com.netease.awakeing.music.KEY_MUSIC_QUEUE_TITLE";
    // 播放index，小于0表示不播
    public static final String KEY_MUSIC_QUEUE_PLAY_INDEX = "com.netease.awakeing.music.KEY_MUSIC_QUEUE_PLAY_INDEX";

    private QueueManager mQueueManager;
    private Playback mPlayback;
    private PlaybackServiceCallback mServiceCallback;
    private MediaSessionCallback mMediaSessionCallback;
    private MusicProvider mMusicProvider;
    private boolean mIsSingleLoop = false;

    public PlaybackManager(PlaybackServiceCallback serviceCallback, QueueManager queueManager,
            Playback playback, MusicProvider musicProvider) {
        mServiceCallback = serviceCallback;
        mQueueManager = queueManager;
        mMediaSessionCallback = new MediaSessionCallback();
        mPlayback = playback;
        mMusicProvider = musicProvider;
        mPlayback.setCallback(this);
    }

    public Playback getPlayback() {
        return mPlayback;
    }

    public MediaSessionCompat.Callback getMediaSessionCallback() {
        return mMediaSessionCallback;
    }

    /**
     * 处理播放音乐的请求
     */
    public void handlePlayRequest() {
        Log.e(TAG, "handlePlayRequest: mState=" + mPlayback.getState());
        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {
            mServiceCallback.onPlaybackStart();
            mPlayback.play(currentMusic);
        }
    }

    /**
     * Handle a request to pause music
     * 处理暂停音乐的请求
     */
    public void handlePauseRequest() {
        Log.d(TAG, "handlePauseRequest: mState=" + mPlayback.getState());
        if (mPlayback.isPlaying()) {
            mPlayback.pause();
            mServiceCallback.onPlaybackStop();
        }
    }

    /**
     * Handle a request to stop music
     * 处理停止音乐的请求
     *
     * @param withError Error message in case the stop has an unexpected cause. The error
     *                  message will be set in the PlaybackState and will be visible to
     *                  MediaController clients.
     */
    public void handleStopRequest(String withError) {
        Log.d(TAG, "handleStopRequest: mState=" + mPlayback.getState() + " error= " + withError);
        mPlayback.stop(true);
        mServiceCallback.onPlaybackStop();
        updatePlaybackState(withError);
    }


    /**
     * Update the current media player state, optionally showing an error message.
     * 更新当前媒体播放器的状态，可选择是否显示错误信息
     *
     * @param error 如果不为null，错误信息将呈现给用户.
     */
    public void updatePlaybackState(String error) {
        Log.d(TAG, "updatePlaybackState, playback state=" + mPlayback.getState());
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        if (mPlayback != null && mPlayback.isConnected()) {
            position = mPlayback.getCurrentStreamPosition();
        }

        //noinspection ResourceType
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder().setActions(
                getAvailableActions());

        int state = mPlayback.getState();

        // If there is an error message, send it to the playback state:
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            stateBuilder.setErrorMessage(error);
            state = PlaybackStateCompat.STATE_ERROR;
        }
        //noinspection ResourceType
        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());

        // Set the activeQueueItemId if the current index is valid.
        //如果当前索引是有效的
        Log.e(TAG, "get current music when updatePlaybackState");
        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {
            stateBuilder.setActiveQueueItemId(currentMusic.getQueueId());
        }

        mServiceCallback.onPlaybackStateUpdated(stateBuilder.build());

        if (state == PlaybackStateCompat.STATE_PLAYING ||
            state == PlaybackStateCompat.STATE_PAUSED) {
            mServiceCallback.onNotificationRequired();
        }
    }

    //获取所有可用的动作命令
    private long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_PLAY_PAUSE |
                       PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                       PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                       PlaybackStateCompat.ACTION_PREPARE |
                       PlaybackStateCompat.ACTION_SET_REPEAT_MODE;
        if (mPlayback.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        } else {
            actions |= PlaybackStateCompat.ACTION_PLAY;
        }
        return actions;
    }

    /**
     * Implementation of the Playback.Callback interface
     */
    @Override
    public void onCompletion() {
        if (!mIsSingleLoop) {
            // The media player finished playing the current song, so we go ahead
            // and start the next.
            //当音乐播放器播完了当前歌曲，则继续播放下一首
            if (mQueueManager.skipQueuePosition(1)) {
                handlePlayRequest();
                mQueueManager.updateMetadata();
            } else {
                // If skipping was not possible, we stop and release the resources:
                //若不可能跳到下一首音乐进行播放，则停止并释放资源
                handleStopRequest(null);
            }
        } else {
            mPlayback.seekTo(0);
        }
    }

    @Override
    public void onPlaybackStatusChanged(int state) {
        updatePlaybackState(null);
    }

    @Override
    public void onError(String error) {
        updatePlaybackState(error);
    }

    @Override
    public void onDurationGet(long duration) {
        String currentId = mQueueManager.getCurrentMusic().getDescription().getMediaId();
        mMusicProvider.updateDuration(currentId, duration);
        mQueueManager.updateMetadata();
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        //点击播放按钮时触发
        //通过MediaControllerCompat.getTransportControls().play()触发
        @Override
        public void onPlay() {
            Log.d(TAG, "play");
            if (mQueueManager.getCurrentMusic() == null) {
                mQueueManager.setRandomQueue();
            }
            handlePlayRequest();
        }

        @Override
        public void onPrepare() {
            super.onPrepare();
        }

        //设置到指定进度时触发
        //通过MediaControllerCompat.getTransportControls().seekTo(position)触发
        @Override
        public void onSeekTo(long position) {
            Log.e(TAG, "onSeekTo: " + position);
            mPlayback.seekTo((int) position);
        }

        //播放指定媒体数据时触发
        //通过MediaControllerCompat.getTransportControls().playFromMediaId(mediaItem.getMediaId(), null)触发
        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            Log.d(TAG, "playFromMediaId mediaId:" + mediaId + "  extras=" + extras);
            mQueueManager.setQueueFromMusic(mediaId);
            handlePlayRequest();
        }

        //暂停时触发
        //通过MediaControllerCompat.getTransportControls().pause()触发
        @Override
        public void onPause() {
            Log.d(TAG, "pause. current state=" + mPlayback.getState());
            handlePauseRequest();
        }

        //停止播放时触发
        //通过MediaControllerCompat.getTransportControls().stop()触发
        @Override
        public void onStop() {
            Log.d(TAG, "stop. current state=" + mPlayback.getState());
            handleStopRequest(null);
        }

        //跳到下一首时触发
        //通过MediaControllerCompat.getTransportControls().skipToNext()触发
        @Override
        public void onSkipToNext() {
            Log.d(TAG, "skipToNext");
            if (mQueueManager.skipQueuePosition(1)) {
                handlePlayRequest();
            } else {
                handleStopRequest("Cannot skip");
            }
            mQueueManager.updateMetadata();
        }

        //跳到上一首时触发
        //通过MediaControllerCompat.getTransportControls().skipToPrevious()触发
        @Override
        public void onSkipToPrevious() {
            if (mQueueManager.skipQueuePosition(-1)) {
                handlePlayRequest();
            } else {
                handleStopRequest("Cannot skip");
            }
            mQueueManager.updateMetadata();
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            // TODO: 2019/3/12
            super.onSetRepeatMode(repeatMode);
            if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
                mIsSingleLoop = true;
            } else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
                mIsSingleLoop = false;
            }
            Log.e(TAG, "get repeat mode: " + repeatMode);
        }

        /**
         * 更新播放队列
         *
         * @param extras
         */
        private void updateMusicQueue(Bundle extras) {
            //
            if (extras == null) {
                return;
            }
            extras.setClassLoader(MediaDescriptionCompat.class.getClassLoader());
            // 列表数据
//            ArrayList<MediaMetadataCompat> list = extras.getParcelableArrayList(KEY_MUSIC_QUEUE);
            ArrayList<MusicBean> list = extras.getParcelableArrayList(KEY_MUSIC_QUEUE);
            if (list == null) {
                return;
            }
            MusicSource musicSource = new MusicSource(list);
            // 更新内容提供者
            mMusicProvider.setMediaCompatList(musicSource.getList());
            // 设置播放队列
            mQueueManager.setCurrentQueue(QueueHelper.convertToQueue(musicSource.getList()));
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            // 更新播放队列
            if (CUSTOM_ACTION_MUSIC_UPDATE_QUNEN.equals(action)) {
                updateMusicQueue(extras);
            }
        }
    }


    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onNotificationRequired();

        void onPlaybackStop();

        void onPlaybackStateUpdated(PlaybackStateCompat newState);
    }
}
