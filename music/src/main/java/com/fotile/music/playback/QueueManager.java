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

import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.fotile.music.model.MusicProvider;
import com.fotile.music.utils.QueueHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueManager {
    private static final String TAG = QueueManager.class.getSimpleName();

    private MusicProvider mMusicProvider;
    private MetadataUpdateListener mListener;

    // "Now playing" queue:
    //当前播放队列
    private List<MediaSessionCompat.QueueItem> mPlayingQueue;
    private int mCurrentIndex;

    /**
     * @param musicProvider 数据源提供者
     * @param listener      播放数据更新的回调接口
     */
    public QueueManager(@NonNull MusicProvider musicProvider,
            @NonNull MetadataUpdateListener listener) {
        this.mMusicProvider = musicProvider;
        this.mListener = listener;

        //mPlayingQueue是线程安全的
        mPlayingQueue = Collections.synchronizedList(new ArrayList<MediaSessionCompat.QueueItem>());
        Log.e(TAG, "set mPlayingQueue in constructor: " + mPlayingQueue);
        mCurrentIndex = 0;
    }

    public void initData() {
        List<MediaSessionCompat.QueueItem> queueItems = QueueHelper.convertToQueue(
                mMusicProvider.getMusicList());
        Log.e(TAG, "setCurrentQueue when init data");
        setCurrentQueue(queueItems, null);
        updateMetadata();
    }

    /**
     * 按照传入的数量跳到队列该数量的位置后（若值为负数则向前跳）开始播放音乐
     *
     * @param amount
     * @return
     */
    public boolean skipQueuePosition(int amount) {
        int index = mCurrentIndex + amount;
        if (index < 0) {
            // 如果索引值跳到了第一首歌的索引之前，则会从第一首歌开始播放
            index = 0;
        } else {
            // 通过取余的方式，当索引跳过了最后一首音乐，则回到队列开始处继续计算最终的索引值
            //（通过这种方式实现了队列的循环）
            index %= mPlayingQueue.size();
        }
        if (!QueueHelper.isIndexPlayable(index, mPlayingQueue)) {
            Log.e(TAG, "Cannot increment queue index by " + amount + ". Current=" + mCurrentIndex +
                       " queue length= " + mPlayingQueue.size());
            return false;
        }
        mCurrentIndex = index;
        return true;
    }

    /**
     * 设置播放队列为随机队列
     */
    public void setRandomQueue() {
        setCurrentQueue(QueueHelper.getRandomQueue(mMusicProvider));
        updateMetadata();
    }

    /**
     * 通过mCurrentIndex获取当前播放的音乐
     *
     * @return
     */
    public MediaSessionCompat.QueueItem getCurrentMusic() {
        if (!QueueHelper.isIndexPlayable(mCurrentIndex, mPlayingQueue)) {
            Log.e(TAG, "current music is null, index: " + mCurrentIndex + ", queue: " + mPlayingQueue);
            return null;
        }
        return mPlayingQueue.get(mCurrentIndex);
    }

    protected void setCurrentQueue(List<MediaSessionCompat.QueueItem> newQueue) {
        Log.e(TAG, "setCurrentQueue when setCurrentQueue");
        setCurrentQueue(newQueue, null);
    }

    public void setQueueFromMusic(String mediaId) {
        Log.e(TAG, "setQueueFromMusic: " + mediaId);
        List<MediaSessionCompat.QueueItem> queueItems = QueueHelper.convertToQueue(
                mMusicProvider.getMusicList());
        Log.e(TAG, "queue items: " + queueItems);
        setCurrentQueue(queueItems, mediaId);
        updateMetadata();
    }

    /**
     * 设置当前播放队列
     *
     * @param newQueue       新的播放队列
     * @param initialMediaId 初始的mediaId
     */
    protected void setCurrentQueue(List<MediaSessionCompat.QueueItem> newQueue,
            String initialMediaId) {
        mPlayingQueue = newQueue;
        Log.e(TAG, "set mPlayingQueue by set: " + mPlayingQueue);
        int index = 0;
        if (initialMediaId != null) {
            index = QueueHelper.getMusicIndexOnQueue(mPlayingQueue, initialMediaId);
        }
        mCurrentIndex = Math.max(index, 0);
        mListener.onQueueUpdated(newQueue);
    }

    /**
     * 更新媒体数据
     */
    public void updateMetadata() {
        Log.e(TAG, "updateMetadata");
        MediaSessionCompat.QueueItem currentMusic = getCurrentMusic();
        if (currentMusic == null) {
            mListener.onMetadataRetrieveError();
            return;
        }
        final String musicId = currentMusic.getDescription().getMediaId();
        MediaMetadataCompat metadata = mMusicProvider.getMusic(musicId);
        if (metadata == null) {
            throw new IllegalArgumentException("Invalid musicId " + musicId);
        }

        mListener.onMetadataChanged(metadata);
    }

    public interface MetadataUpdateListener {
        void onMetadataChanged(MediaMetadataCompat metadata);//媒体数据变更时调用

        void onMetadataRetrieveError();//媒体数据检索失败时调用

        void onQueueUpdated(List<MediaSessionCompat.QueueItem> newQueue);//当前播放队列变更时调用
    }
}
