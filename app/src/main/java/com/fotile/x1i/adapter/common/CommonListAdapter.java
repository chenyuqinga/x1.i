package com.fotile.x1i.adapter.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CommonListAdapter extends BaseAdapter {
    public Context context;
    public LayoutInflater inflater;

    public CommonListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
}
