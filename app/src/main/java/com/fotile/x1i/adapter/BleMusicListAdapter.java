package com.fotile.x1i.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ebanswers.ble.BLEDevice;
import com.fotile.x1i.R;
import com.fotile.x1i.widget.RotationLoadingView;

import java.util.ArrayList;
import java.util.List;


/**
 * 文件名称：BleMusicListAdapter
 * 创建时间：2019/7/24 17:37
 * 文件作者：yaohx
 * 功能描述：蓝牙音箱列表adapter
 */
public class BleMusicListAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private List<BLEDevice> list;
    private int color_normal = Color.parseColor("#ffffff");
    private int color_linked = Color.parseColor("#e1c79b");

    public BleMusicListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<BLEDevice> list) {
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
        BLEDevice bleDevice = list.get(position);
        //设备名称
        String deviceName = bleDevice.getName();
        holder.txt_device_name.setText(deviceName);
        //连接状态
        int state = bleDevice.getStatus();
        //未连接 //连接错误
        if (state == BLEDevice.STATUS_NORMAL) {
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
        if (state == BLEDevice.STATUS_CONNECTING) {
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
        if (state == BLEDevice.STATUS_CONNECTED) {
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
     */
    public int getPosition(BLEDevice bleDevice) {
        int position = -1;
        if (null != list && list.size() != 0 && null != bleDevice) {
            for (int k = 0; k < list.size(); k++) {
                String mac = list.get(k).getMac_address();
                if (mac.equals(bleDevice.getMac_address())) {
                    return k;
                }
            }
        }
        return position;
    }

    /**
     * 将所有的item重置为normal状态--点击连接其他的蓝牙时，将上一次的item断开
     */
    private void setAllLinkStateNormal() {
        if (null != list) {
            for (BLEDevice data : list) {
                data.setStatus(BLEDevice.STATUS_NORMAL);
            }
        }
    }

    /**
     * 设置连接状态
     *
     * @param state
     * @param item_index
     */
    public void setLinkState(int state, int item_index) {
        if (null != list && list.size() != 0) {
            if (item_index >= 0 && item_index <= list.size() - 1) {
                //先将所有的item设置为normal
                setAllLinkStateNormal();

                BLEDevice bleDevice = list.get(item_index);
                if (state != bleDevice.getStatus()) {
                    bleDevice.setStatus(state);
                }
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 更新某一个mac对应的item状态，connected_mac为空更新所有item为normal
     *
     * @param state
     * @param connected_mac 如果为空，标识板子ble模块断开连接
     */
    public void setLinkState(int state, String connected_mac) {
        if (!TextUtils.isEmpty(connected_mac)) {
            if (null != list && list.size() != 0) {
                int item_index = -1;
                for (int k = 0; k < list.size(); k++) {
                    BLEDevice bleDevice = list.get(k);
                    if (TextUtils.equals(bleDevice.getMac_address(), connected_mac)) {
                        item_index = k;
                        break;
                    }
                }
                setLinkState(state, item_index);
            }
        } else {
            setAllLinkStateNormal();
            notifyDataSetChanged();
        }
    }

    /**
     * 是否有设备正在执行连接
     *
     * @return
     */
    public boolean hasDeviceLinking() {
        boolean result = false;
        for (BLEDevice data : list) {
            if (data.getStatus() == BLEDevice.STATUS_CONNECTING) {
                result = true;
            }
        }
        return result;
    }

    public BLEDevice getLinkiedDevice() {
        BLEDevice result = null;
        for (BLEDevice data : list) {
            if (data.getStatus() == BLEDevice.STATUS_CONNECTED) {
                result = data;
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
