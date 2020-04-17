package com.fotile.x1i.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.fotile.x1i.R;

import java.util.List;

/**
 * 文件名称：FactoryCheckAdapter
 * 创建时间：2017/12/4 16:22
 * 文件作者：yaohx
 * 功能描述：工厂自检模式Adapter
 */
public class FactoryCheckAdapter extends BaseAdapter {

    private List<FactoryData> list;
    private LayoutInflater inflater;

    public FactoryCheckAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<FactoryData> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return null == list ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_check_factory, null);
        TextView txt_work_mode_tag = (TextView) view.findViewById(R.id.txt_work_mode_tag);
        TextView txt_work_mode_value = (TextView) view.findViewById(R.id.txt_work_mode_value);

        FactoryData factoryData = list.get(position);
        txt_work_mode_tag.setText(factoryData.tag);
        txt_work_mode_value.setText(factoryData.value);

        return view;
    }

    public static class FactoryData {
        public String tag;
        public String value;

        public FactoryData(String tag, String value) {
            this.tag = tag;
            this.value = value;
        }
    }
}
