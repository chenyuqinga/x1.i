package com.fotile.music.manager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.fotile.music.model.CommanConst;
import com.fotile.music.model.MusicBean;
import com.fotile.music.playback.PlaybackManager;

import java.util.ArrayList;
import java.util.List;

public class MusicManager {
    private static final String TAG = MusicManager.class.getSimpleName();
    //    private static final String TAG = "lil_dy";
    private Context mContext;
    // MediaBrowserCompat 客户端
    private MediaBrowserCompat mMediaBrowser;
    // MediaControllerCompat
    private MediaControllerCompat mMediaController;
    // TransportControls
    private MediaControllerCompat.TransportControls mTransportControls;
    private boolean mNeedExecutePlay;
    private PlaybackStateCompat mLastPlaybackState;

    private static MusicManager instance = null;
    private boolean mIsDuiMode = false;
    private MediaMetadataCompat mPreMetadata;

    private MusicManager() {
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            synchronized (MusicManager.class) {
                if (instance == null) {
                    instance = new MusicManager();
                }
            }
        }
        return instance;
    }

    public boolean isDuiMode() {
        // TODO: 2019/4/1 增加模式选择
        return mIsDuiMode;
    }

    public void init(Context context, ArrayList<MusicBean> musicBeans, boolean needExecutePlay) {
        Log.e(TAG, "init");
        mNeedExecutePlay = needExecutePlay;
        this.mContext = context;
        if (musicBeans != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(CommanConst.MUSIC_BEAN_LIST, musicBeans);
            mMediaBrowser = new MediaBrowserCompat(mContext,
                    new ComponentName(mContext, MusicService.class), mConnectionCallback, bundle);
        } else {
            mMediaBrowser = new MediaBrowserCompat(mContext,
                    new ComponentName(mContext, MusicService.class), mConnectionCallback, null);
        }
        Log.e(TAG, "mMediaBrowser: " + mMediaBrowser.isConnected());
    }

    /**
     * 链接 {@link MusicService}
     */
    public void connect() {
        if (mMediaBrowser == null) {
            return;
        }
        Log.e(TAG, "connect: " + mMediaBrowser.isConnected());
        if (!mMediaBrowser.isConnected()) {
            try {
                mMediaBrowser.connect();
            } catch (Exception e) {
                Log.e(TAG, "connect failed : \n" + e.getMessage());
            }
        }
    }

    /**
     * 断开链接
     */
    public void disconnect() {
        Log.e(TAG, "disconnect");
        //        if (mMediaController != null) {
        //            mMediaController.unregisterCallback(mCallback);
        //            mMediaController = null;
        //        }
        if (mMediaBrowser != null && mMediaBrowser.isConnected()) {
            mMediaBrowser.disconnect();
        }
    }

    /**
     * 链接状态
     *
     * @return
     */
    public boolean isConnected() {
        return mMediaBrowser != null && mMediaBrowser.isConnected();
    }

    /**
     * 播放
     */
    public void play() {
        if (mTransportControls != null) {
            mTransportControls.play();
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mTransportControls != null) {
            mTransportControls.pause();
        }
    }

    /**
     * 停止
     */
    public void stop() {
        if (mTransportControls != null) {
            mTransportControls.stop();
        }
        mIsDuiMode = false;
        // TODO: 2019/4/1 clear music list
    }

    /**
     * 上一首
     */
    public void skipToPrevious() {
        if (mTransportControls != null) {
            mTransportControls.skipToPrevious();
        }
    }

    /**
     * 下一首
     */
    public void skipToNext() {
        if (mTransportControls != null) {
            mTransportControls.skipToNext();
        }
    }

    /**
     * seek
     */
    public void seekTo(long pos) {
        if (mTransportControls != null) {
            mTransportControls.seekTo(pos);
        }
    }

    public void setRepeatMode(boolean isSingleLoop) {
        if (isSingleLoop) {
            mTransportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
        } else {
            mTransportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
        }
    }

    /**
     * 更新播放队列
     */
    public void updatePlayList(final ArrayList<MusicBean> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (mMediaController == null) {
            return;
        }
        //相同的列表则不刷新播放队列
        Bundle args = new Bundle();
        args.putParcelableArrayList(PlaybackManager.KEY_MUSIC_QUEUE, list);
        mMediaController.getTransportControls().sendCustomAction(
                PlaybackManager.CUSTOM_ACTION_MUSIC_UPDATE_QUNEN, args);
    }

    private MediaBrowserCompat.ConnectionCallback mConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            Log.e(TAG, "onConnected");
            String root = mMediaBrowser.getRoot();
            Log.e(TAG, "root: " + root);
            if (mNeedExecutePlay) {
                mMediaBrowser.unsubscribe(root);
                mMediaBrowser.subscribe(root, mSubscriptionCallback);
            }
            Log.e(TAG, "connectToSession");
            try {
                connectToSession(mMediaBrowser.getSessionToken());
            } catch (RemoteException e) {
                Log.e(TAG, "could not connect media controller by exception: " + e);
            }
        }
    };

    private MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            try {
                Log.e(TAG, "fragment onChildrenLoaded, parentId=" + parentId + "  count=" +
                           children.size());
                MediaBrowserCompat.MediaItem mediaItem = children.get(0);
                if (mediaItem.isPlayable()) {//可播放状态
                    MediaControllerCompat mediaController = MediaControllerCompat
                            .getMediaController((Activity) mContext);
                    if (mediaController != null) {
                        Log.e(TAG, "play from media id: " + mediaItem.getMediaId());
                        mediaController.getTransportControls().playFromMediaId(
                                mediaItem.getMediaId(), null);
                    } else {
                        Log.e(TAG, "mediaController is null");
                    }
                }
            } catch (Throwable t) {
                Log.e(TAG, "Error on childrenloaded:" + t);
            }
        }

        @Override
        public void onError(@NonNull String id) {
            Log.e(TAG, "browse fragment subscription onError, id= " + id);
        }
    };

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        mMediaController = new MediaControllerCompat(mContext, token);
        MediaControllerCompat.setMediaController((Activity) mContext, mMediaController);
        Log.e(TAG, "Media Controller: " +
                   MediaControllerCompat.getMediaController((Activity) mContext));
        mMediaController.registerCallback(mCallback);
        // 获取TransportControls
        mTransportControls = mMediaController.getTransportControls();
        onPlaybackStateChanged(mMediaController.getPlaybackState());
        MediaMetadataCompat metadata = mMediaController.getMetadata();
        if (mMediaController.getMetadata() != null) {
            onMetadataChanged(metadata);
            onProgressInit(mMediaController.getPlaybackState());
        }
    }

    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            Log.d(TAG, "onPlaybackstate changed" + state);
            MusicManager.this.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null && !TextUtils.equals(metadata.getDescription().getTitle(),
                    mPreMetadata == null ? null : mPreMetadata.getDescription().getTitle())) {
                Log.e(TAG, "metadata changed: " +
                           metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                MusicManager.this.onMetadataChanged(metadata);
                mPreMetadata = metadata;
            }
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            Log.e(TAG, "queue changed");
            MusicManager.this.onQueueChanged(queue);
        }
    };

    /**
     * 回调播放队列
     *
     * @return
     */
    public List<MediaSessionCompat.QueueItem> getQueue() {
        if (mMediaController == null) {
            return null;
        }
        return mMediaController.getQueue();
    }

    /**
     * 播放队列
     *
     * @return
     */
    public boolean hasPlayQueue() {
        List<MediaSessionCompat.QueueItem> queue = getQueue();
        return queue != null && !queue.isEmpty();
    }


    /**
     * 当前播放状态
     *
     * @return
     */
    public PlaybackStateCompat getPlaybackState() {
        if (mMediaController == null) {
            return null;
        }
        return mMediaController.getPlaybackState();
    }

    /**
     * 当前播放数据
     *
     * @return
     */
    public MediaMetadataCompat getMediaMetadata() {
        if (mMediaController == null) {
            return null;
        }
        return mMediaController.getMetadata();
    }

    // ####################################音频状态变化回调##########################################

    /**
     * 音频播放状态变化的监听者
     */
    private List<OnAudioStatusChangeListener> mAudioChangeListeners = new ArrayList<>();

    public boolean isPlaying() {
        return mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING;
    }


    /**
     *
     */
    public interface OnAudioStatusChangeListener {

        /**
         * 播放状态修改
         */
        void onPlaybackStateChanged(@NonNull PlaybackStateCompat state);

        /**
         * 当前播放歌曲信息修改
         */
        void onMetadataChanged(MediaMetadataCompat metadata);

        /**
         * 播放队列修改
         */
        void onQueueChanged(List<MediaSessionCompat.QueueItem> queue);


        void onProgressInit(PlaybackStateCompat state);
    }


    /**
     * 添加监听
     *
     * @param listener
     */
    public void addOnAudioStatusListener(OnAudioStatusChangeListener listener) {
        mAudioChangeListeners.add(listener);
        Log.e(TAG, "listener list size: " + mAudioChangeListeners.size());
        // 添加监听  会回调一次当前播放状态
        if (mMediaController != null) {
            // 回到 播放状态
            listener.onPlaybackStateChanged(getPlaybackState());
            // 回调 数据变化
            listener.onMetadataChanged(getMediaMetadata());
            // 回调 播放队列
            listener.onQueueChanged(getQueue());
            // 回调 进度条？
            listener.onProgressInit(getPlaybackState());
        }
    }

    /**
     * 移除监听
     *
     * @param listener
     */
    public void removeAudioStateListener(OnAudioStatusChangeListener listener) {
        mAudioChangeListeners.remove(listener);
    }

    public boolean hasListener(OnAudioStatusChangeListener listener) {
        return mAudioChangeListeners.contains(listener);
    }

    /**
     * 回调：播放状态变化
     *
     * @param state
     */
    private void onPlaybackStateChanged(PlaybackStateCompat state) {
        if (state != null) {
            if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                mIsDuiMode = true;
            }
            mLastPlaybackState = state;
            try {
                for (OnAudioStatusChangeListener l : mAudioChangeListeners) {
                    l.onPlaybackStateChanged(state);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 回调：音频数据变化
     *
     * @param metadata
     */
    private void onMetadataChanged(MediaMetadataCompat metadata) {
        try {
            for (OnAudioStatusChangeListener l : mAudioChangeListeners) {
                l.onMetadataChanged(metadata);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onProgressInit(@NonNull PlaybackStateCompat state) {
        try {
            for (OnAudioStatusChangeListener l : mAudioChangeListeners) {
                l.onProgressInit(state);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 回调：播放队列变化
     *
     * @param queue
     */
    private void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
        try {
            for (OnAudioStatusChangeListener l : mAudioChangeListeners) {
                l.onQueueChanged(queue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
