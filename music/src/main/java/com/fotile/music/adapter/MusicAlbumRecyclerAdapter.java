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
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

/**
 * 文件名称：MusicAlbumRecyclerAdapter
 * 创建时间：17-9-11 上午9:33
 * 文件作者：zhangqiang
 * 功能描述：专辑的adapter
 */
public class MusicAlbumRecyclerAdapter extends RecyclerView.Adapter<MusicAlbumViewHolder> {

    private Context context;

    private AlbumList albumList = new AlbumList();


    public MusicAlbumRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MusicAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_music_album, parent, false);
        return new MusicAlbumViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MusicAlbumViewHolder holder, int position) {

        holder.musicName.setText(albumList.getAlbums().get(position).getAlbumTitle());
        Glide.with(context).load(albumList.getAlbums().get(position).getCoverUrlLarge()).asBitmap()
                .transform(new CenterCrop(context), new GlideRoundTransform(context, 10)).into(
                holder.musicCover);
    }


    @Override
    public int getItemCount() {
        if (albumList == null || albumList.getAlbums() == null) {
            return 0;
        }
        return albumList.getAlbums().size();
    }

    public void setAlbumTrackList(AlbumList albumList) {
        this.albumList = albumList;

    }

}
