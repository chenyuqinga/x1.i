package com.fotile.music.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.fotile.music.R;
import com.fotile.music.widget.GlideRoundTransform;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

/**
 * 文件名称：MusicTrackRecyclerAdapter
 * 创建时间：17-9-12 下午4:48
 * 文件作者：zhangqiang
 * 功能描述：音乐的adapter
 */
public class MusicTrackRecyclerAdapter extends RecyclerView.Adapter<MusicAlbumViewHolder> {

    private Context context;
    private TrackList trackList;

    public MusicTrackRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MusicAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music_album, parent, false);
        MusicAlbumViewHolder holder = new MusicAlbumViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MusicAlbumViewHolder holder, int position) {
        holder.musicName.setText(trackList.getTracks().get(position).getTrackTitle());
        Glide.with(context).load(trackList.getTracks().get(position).getCoverUrlLarge()).asBitmap()
                .transform(new CenterCrop(context)
                        ,new GlideRoundTransform(context, 10)).into(holder.musicCover);
    }

    @Override
    public int getItemCount() {
        return trackList == null ? 0 : trackList.getTracks().size();
    }

    /**
     * 更新列表数据
     * @param trackList
     */
    public void updateList(TrackList trackList){
        this.trackList = trackList;
        notifyDataSetChanged();
    }
}
