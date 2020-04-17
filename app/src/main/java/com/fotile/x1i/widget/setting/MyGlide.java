package com.fotile.x1i.widget.setting;

import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.fotile.common.util.log.LogUtil;

/**
 * Created by chenyqi on 2019/7/2.
 */

public class MyGlide implements GlideModule {
    public static final int DISK_CACHE_SIZE = 500 * 1024 * 1024;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

        String path = Environment.getExternalStorageDirectory().getPath().toString()+"/fotile";
        LogUtil.LOGE("--path",path);
        builder.setDiskCache(new DiskLruCacheFactory(path, DISK_CACHE_SIZE));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }

}
