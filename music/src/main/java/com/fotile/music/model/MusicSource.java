package com.fotile.music.model;

import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

public class MusicSource {

    private List<MusicBean> mMusicList;

    public MusicSource(List<MusicBean> musicList) {
        this.mMusicList = musicList;
    }

    public List<MediaMetadataCompat> getList() {
        ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
        if (mMusicList != null && !mMusicList.isEmpty()) {
            for (MusicBean musicBean : mMusicList) {
                tracks.add(buildFromBean(musicBean));
            }
        }
        return tracks;
    }

    private MediaMetadataCompat buildFromBean(MusicBean musicBean) {
        String id = String.valueOf(musicBean.getLinkUrl().hashCode());
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, musicBean.getSubTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, musicBean.getImageUrl())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicBean.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, musicBean.getLinkUrl())
                .build();
    }
}
