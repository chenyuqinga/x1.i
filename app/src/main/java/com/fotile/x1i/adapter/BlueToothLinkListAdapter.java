package com.fotile.x1i.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.fotile.x1i.R;
import com.fotile.x1i.widget.RotationLoadingView;

import java.util.List;

import fotile.ble.bean.BleDevice;
import fotile.ble.observer.link.ILinkObserverable;

/**
 * 项目名称：Z1.5
 * 创建时间：2018/10/22 17:44
 * 文件作者：yaohx
 * 功能描述：蓝牙列表的listAdapter
 */
public class BlueToothLinkListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<BleDevice> list;

    public BlueToothLinkListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    private int color_normal = Color.parseColor("#ffffff");
    private int color_linked = Color.parseColor("#e1c79b");

    public void setList(List<BleDevice> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return null == list ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.item_guide_link_list, null);
            holder.txt_device_name = (TextView) convertView.findViewById(R.id.txt_device_name);
            holder.txt_link_status = (TextView) convertView.findViewById(R.id.txt_link_status);
            holder.img_loading = (RotationLoadingView) convertView.findViewById(R.id.img_loading);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BleDevice bleDevice = list.get(position);
        //设备名称
        String deviceName = bleDevice.getName();
        //如果名称为空用mac代替
        if (TextUtils.isEmpty(deviceName)) {
            deviceName = bleDevice.getAddress();
        }
        holder.txt_device_name.setText(deviceName);
        //连接状态
        int state = bleDevice.linkStatus;
        //未连接 //连接错误
        if (state == ILinkObserverable.STATE_NONE || state == ILinkObserverable.STATE_ERROR) {
            //名称
            holder.txt_device_name.setTextColor(color_normal);
            //连接状态
            holder.txt_link_status.setText("未连接");
            holder.txt_link_status.setTextColor(color_normal);
            holder.txt_link_status.setAlpha(0.6f);
            //转圈
            holder.img_loading.setVisibility(View.GONE);
            holder.img_loading.stopRotationAnimation();
        }
        //连接中
        if (state == ILinkObserverable.STATE_CONNECTING) {
            //名称
            holder.txt_device_name.setTextColor(color_linked);
            //连接状态
            holder.txt_link_status.setText("");
            holder.txt_link_status.setTextColor(color_linked);
            holder.txt_link_status.setAlpha(0.6f);
            //转圈
            holder.img_loading.setVisibility(View.VISIBLE);
            holder.img_loading.startRotationAnimation();
        }
        //已连接
        if (state == ILinkObserverable.STATE_CONNECTED) {
            //名称
            holder.txt_device_name.setTextColor(color_linked);
            //连接状态
            holder.txt_link_status.setText("已连接");
            holder.txt_link_status.setTextColor(color_linked);
            holder.txt_link_status.setAlpha(1f);
            //转圈
            holder.img_loading.setVisibility(View.GONE);
            holder.img_loading.stopRotationAnimation();
        }
        return convertView;
    }

    /**
     * 获取其对应的position
     *
     * @param targetAddress
     * @return
     */
    public int getPosition(String targetAddress) {
        int position = -1;
        if (null != list && list.size() != 0) {
            for (int k = 0; k < list.size(); k++) {
                String address = list.get(k).getAddress();
                if (address.equals(targetAddress)) {
                    return k;
                }
            }
        }
        return position;
    }

    /**
     * 设置连接状态
     *
     * @param state
     * @param item_index
     */
    public void notifyItemLinkState(int state, int item_index) {
        if (null != list && list.size() != 0) {
            if (item_index >= 0 && item_index <= list.size() - 1) {
                BleDevice bleDevice = list.get(item_index);
                if (state != bleDevice.linkStatus) {
                    bleDevice.linkStatus = state;
                    notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 获取当前index的连接状态
     *
     * @param index
     * @return
     */
    public int getLinkState(int index) {
        BleDevice bleDevice = list.get(index);
        if (null != bleDevice) {
            return bleDevice.linkStatus;
        }
        return -1;
    }

    /**
     * 是否有设备正在执行连接
     *
     * @return
     */
    public boolean hasDeviceLinking() {
        boolean result = false;
        for (BleDevice data : list) {
            if (data.linkStatus == ILinkObserverable.STATE_CONNECTING) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Holder
     */
    public final class ViewHolder {
        /**
         * 可用设备名称
         */
        public TextView txt_device_name;
        /**
         * 连接状态文字
         */
        public TextView txt_link_status;
        /**
         * 正在加载图标控件
         */
        public RotationLoadingView img_loading;
    }
}
