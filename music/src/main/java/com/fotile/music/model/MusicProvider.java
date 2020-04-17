package com.fotile.music.model;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MusicProvider {

    private static final String TAG = MusicProvider.class.getSimpleName();
    private List<MediaMetadataCompat> mMediaCompats;
    private final ConcurrentMap<String, MutableMediaMetadata> mMusicListById;

    public MusicProvider() {
        mMusicListById = new ConcurrentHashMap<>();
    }

    public void setMediaCompatList(List<MediaMetadataCompat> mediaCompatList) {
        mMediaCompats = mediaCompatList;
        for(MediaMetadataCompat item : mediaCompatList) {
            String musicId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
            mMusicListById.put(musicId, new MutableMediaMetadata(musicId, item));
        }
    }

    public void setMusicBeanList(List<MusicBean> musicList) {
//        if (mMediaCompats != null && musicList != null && musicList.size() != 0) {
            mMediaCompats = new MusicSource(musicList).getList();
            setMediaCompatList(mMediaCompats);
//        }
        /*for(MediaMetadataCompat item : mMediaCompats) {
            String musicId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
            mMusicListById.put(musicId, new MutableMediaMetadata(musicId, item));
        }*/
    }

    public List<MediaMetadataCompat> getMusicList() {
        return mMediaCompats;
    }

    /**
     * Return the MediaMetadataCompat for the given musicID.
     *
     * @param musicId The unique, non-hierarchical music ID.
     */
    public MediaMetadataCompat getMusic(String musicId) {
        Log.e(TAG, "music id: " + musicId);
        /*for (MediaMetadataCompat mediaCompat : mMediaCompats) {
            String id = mediaCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
            if (id.equals(musicId)) {
                return mediaCompat;
            }
        }
        return null;*/
        return mMusicListById.containsKey(musicId) ? mMusicListById.get(musicId).metadata : null;
    }

    /**
     * Get an iterator over a shuffled collection of all songs
     * 获取 存储了所有音乐的列表 随机打乱顺序后的迭代器
     */
    public Iterable<MediaMetadataCompat> getShuffledMusic() {
        List<MediaMetadataCompat> shuffled = new ArrayList<>(mMusicListById.size());
        for (MutableMediaMetadata mutableMetadata : mMusicListById.values()) {
            shuffled.add(mutableMetadata.metadata);
        }
        Collections.shuffle(shuffled);//打乱列表的顺序
        return shuffled;
    }

    public List<MediaBrowserCompat.MediaItem> getChildren() {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        for (MediaMetadataCompat metadata : mMediaCompats) {
            mediaItems.add(new MediaBrowserCompat.MediaItem(metadata.getDescription(),
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return mediaItems;
    }

    public synchronized void updateDuration(String musicId, long duration) {
        Log.e(TAG, "update duration, get music");
        MediaMetadataCompat metadata = getMusic(musicId);
        Log.e(TAG, "update duration, musicId: " + musicId + " metadata: " + metadata);
        metadata = new MediaMetadataCompat.Builder(metadata)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .build();

        MutableMediaMetadata mutableMetadata = mMusicListById.get(musicId);
        if (mutableMetadata == null) {
            throw new IllegalStateException(
                    "Unexpected error: Inconsistent data structures in " + "MusicProvider");
        }
        mutableMetadata.metadata = metadata;
    }

}
