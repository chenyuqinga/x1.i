package com.fotile.music.manager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.fotile.music.R;
import com.fotile.music.model.CommanConst;
import com.fotile.music.model.MusicBean;
import com.fotile.music.model.MusicProvider;
import com.fotile.music.playback.LocalPlayback;
import com.fotile.music.playback.PlaybackManager;
import com.fotile.music.playback.QueueManager;
import com.fotile.music.utils.MediaIDHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaBrowserServiceCompat implements PlaybackManager.PlaybackServiceCallback {

    private static final String TAG = MusicService.class.getSimpleName();

    private MediaSessionCompat mMediaSession;
    private MusicProvider mMusicProvider;
    private PlaybackManager mPlaybackManager;
    private QueueManager mQueueManager;
    private final DelayedStopHandler mDelayedStopHandler = new DelayedStopHandler(this);
    // Delay stopSelf by using a handler.
    private static final int STOP_DELAY = 30000;

    @Override
    public void onCreate() {
        Log.e(TAG, "music service on create");
        super.onCreate();
        mMusicProvider = new MusicProvider();
        //QueueManager提供四个回调接口
        mQueueManager = new QueueManager(mMusicProvider, new QueueManager.MetadataUpdateListener() {
            @Override
            public void onMetadataChanged(MediaMetadataCompat metadata) {
                mMediaSession.setMetadata(metadata);
                Log.e(TAG, "set Metadata");
            }

            @Override
            public void onMetadataRetrieveError() {
                mPlaybackManager.updatePlaybackState(getString(R.string.error_no_metadata));
            }

            @Override
            public void onQueueUpdated(List<MediaSessionCompat.QueueItem> newQueue) {
                mMediaSession.setQueue(newQueue);
            }
        });
        LocalPlayback playback = new LocalPlayback(this, mMusicProvider);
        Log.e(TAG, "local play back: " + playback);
        mPlaybackManager = new PlaybackManager(this, mQueueManager, playback, mMusicProvider);
        Log.e(TAG, "mPlaybackManager: " + mPlaybackManager);

        // Create a new MediaSession.
        mMediaSession = new MediaSessionCompat(getApplicationContext(), "MusicService");
        Log.e(TAG, "mMediaSession: " + mMediaSession);

        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                               MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setCallback(mPlaybackManager.getMediaSessionCallback());

        //        Context context = getApplicationContext();
        //        Intent intent = new Intent(context, MusicPlayActivity.class);
        //        PendingIntent pi = PendingIntent.getActivity(context, 99 /*request code*/, intent,
        //                PendingIntent.FLAG_UPDATE_CURRENT);
        //        mMediaSession.setSessionActivity(pi);

        setSessionToken(mMediaSession.getSessionToken());
        //                queueManager.initData();
        Log.e(TAG, "service on create over");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Reset the delay handler to enqueue a message to stop the service if
        // nothing is playing.
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        return START_STICKY;
    }

    /*
     * Handle case when user swipes the app away from the recents apps list by
     * stopping the service (and any ongoing playback).
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    /**
     * (non-Javadoc)
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        // Service is being killed, so make sure we release our resources
        mPlaybackManager.handleStopRequest(null);

        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mMediaSession.release();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid,
            Bundle rootHints) {
        Log.e(TAG,
                "OnGetRoot: clientPackageName=" + clientPackageName + "; clientUid=" + clientUid +
                " ; rootHints=" + rootHints);
        if (rootHints != null) {
            ArrayList<MusicBean> musicList = rootHints.getParcelableArrayList(
                    CommanConst.MUSIC_BEAN_LIST);
            if (musicList != null && musicList.size() != 0) {
                mMusicProvider.setMusicBeanList(musicList);
                mQueueManager.initData();
            }
        }
        return new BrowserRoot(MediaIDHelper.MEDIA_ID_ROOT, null);
    }

    @Override
    public void onLoadChildren(@NonNull final String parentMediaId,
            @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "OnLoadChildren: parentMediaId=" + parentMediaId);
        Log.e(TAG, "onLoadChildren, parentMediaId: " + parentMediaId + ", result: " + result);
        if (MediaIDHelper.MEDIA_ID_EMPTY_ROOT.equals(parentMediaId)) {
            result.sendResult(new ArrayList<MediaBrowserCompat.MediaItem>());
        } else {
            List<MediaBrowserCompat.MediaItem> children = mMusicProvider.getChildren();
            Log.e(TAG, "children: " + children);
            result.sendResult(children);
        }
    }

    @Override
    public void onPlaybackStart() {
        mMediaSession.setActive(true);

        mDelayedStopHandler.removeCallbacksAndMessages(null);

        // The service needs to continue running even after the bound client (usually a
        // MediaController) disconnects, otherwise the music playback will stop.
        // Calling startService(Intent) will keep the service running until it is explicitly killed.
        //即使绑定的客户端（通常是指MediaController）断开连接了，Service也需要继续运行，否则音乐将会停止播放。
        //调用startService(Intent)将保持Service持续运行直到明确要将服务杀掉为止
        startService(new Intent(getApplicationContext(), MusicService.class));
    }

    @Override
    public void onNotificationRequired() {

    }

    @Override
    public void onPlaybackStop() {
        mMediaSession.setActive(false);
        // Reset the delayed stop handler, so after STOP_DELAY it will be executed again,
        // potentially stopping the service.
        //重置延迟停止的Handler，所以收到 STOP_DELAY 消息后将再次执行
        //有可能会停止Service
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        stopForeground(true);
    }

    @Override
    public void onPlaybackStateUpdated(PlaybackStateCompat newState) {
        mMediaSession.setPlaybackState(newState);
    }

    /**
     * A simple handler that stops the service if playback is not active (playing)
     * 当playback不在活跃状态时停止服务
     */
    private static class DelayedStopHandler extends Handler {
        private final WeakReference<MusicService> mWeakReference;

        private DelayedStopHandler(MusicService service) {
            mWeakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicService service = mWeakReference.get();
            if (service != null && service.mPlaybackManager.getPlayback() != null) {
                if (service.mPlaybackManager.getPlayback().isPlaying()) {
                    Log.d(TAG, "Ignoring delayed stop since the media player is in use.");
                    return;
                }
                Log.d(TAG, "Stopping service with delay handler.");
                service.stopSelf();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "on bind");
        return super.onBind(intent);
    }
}
