package com.fotile.x1i.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.fotile.common.util.log.LogUtil;
import com.fotile.wifi.bean.MScanWifi;
import com.fotile.wifi.util.LinkWifi;
import com.fotile.x1i.R;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.util.Constant;
import com.fotile.x1i.widget.RotationLoadingView;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名称：WifiListAdapter
 * 创建时间：2017/8/15
 * 文件作者：wanghouyu
 * 功能描述：wifi列表适配器
 */
public class WifiListAdapter extends BaseAdapter {
    protected Context context;
    protected List<MScanWifi> datas = new ArrayList<>();
    private LinkWifi linkWifi;

    public WifiListAdapter(Context context) {
        this.context = context;
        this.linkWifi = new LinkWifi(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public MScanWifi getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final WifiListViewHolder viewHolder = getViewHolder(position, convertView,
                parent);
        convert(viewHolder, getItem(position));

        return viewHolder.getConvertView();

    }

    /**
     * 更新列表数据
     *
     * @param list
     */
    public void updateDataList(List<MScanWifi> list) {
        datas = list;
        notifyDataSetChanged();
    }

    /**
     * 重置连接状态,在关闭wifi开关时调用
     */
    public void resetConnectStatus() {
        if (datas != null && datas.size() > 0) {
            MScanWifi firstItem = datas.get(0);
            firstItem.setIsConnectedNow(false);
            firstItem.setConnecting(false);
            notifyDataSetChanged();
        }
    }

    /**
     * 布局里面的View常用也就那么几种：ImageView,TextView,Button,CheckBox等等
     * viewholder中封装常用的方法
     *
     * @param helper
     * @param item
     */
    private void convert(WifiListViewHolder helper, final MScanWifi item) {
        String wifename = item.getWifiName();
        //wifename 大厨管家
        if (wifename.contains(Constant.IKCC_NAME) && wifename.length() == 13) {
            helper.setText(R.id.txt_wifi_name,  "大厨管家-"+ wifename.substring(wifename.length() - 4));
        } else {
            helper.setText(R.id.txt_wifi_name, wifename);
        }
        TextView wifiName = helper.getView(R.id.txt_wifi_name);
        wifiName.setTextColor(Color.parseColor("#FFFFFF"));

        //信号强度
        helper.setImageResource(R.id.img_wifi, AppUtil.getResourceById("wifi_" + item.getLevel() + "_default"));

        //是否有密码
        if (item.getIsLock()) {
            helper.getView(R.id.img_lock).setVisibility(View.VISIBLE);
            helper.setImageResource(R.id.img_lock, R.mipmap.icon_wireless_lock);
        } else {
            helper.getView(R.id.img_lock).setVisibility(View.GONE);
        }

        //连接状态
        ImageView imgStatus = helper.getView(R.id.img_ok);
        RotationLoadingView rotationView = helper.getView(R.id.img_wifi_connecting);
        //正在连接
        if (item.getIsConnectedNow() || item.isConnecting()) {
            if (item.isConnecting()) {
                rotationView.setVisibility(View.VISIBLE);
                rotationView.startRotationAnimation();
                imgStatus.setVisibility(View.GONE);
            } else {
                rotationView.setVisibility(View.GONE);
                rotationView.stopRotationAnimation();
                imgStatus.setBackgroundResource(R.mipmap.icon_wireless_ok);
                imgStatus.setVisibility(View.VISIBLE);
            }
            helper.setImageResource(R.id.img_wifi, AppUtil.getResourceById("wifi_" + item.getLevel() + "_default"));

//            // 顶部状态栏显示wifi强度
//            EventBus.getDefault().post(new WifiLevelMessage(item.getLevel()));
            helper.setImageResource(R.id.img_lock, R.mipmap.icon_wireless_lock);
            wifiName.setTextColor(Color.parseColor("#C8AF70"));
        } else {
            rotationView.setVisibility(View.GONE);
            rotationView.stopRotationAnimation();
            imgStatus.setVisibility(View.GONE);
        }
    }

    /**
     * 将某一个ssid的连接状态更新为已连接
     *
     * @param ssid
     */
    public void notifyItemConnected(String ssid) {
        if (null != datas) {
            for (int k = 0; k < datas.size(); k++) {
                MScanWifi mScanWifi = datas.get(k);
                String wifiName = LinkWifi.convertToQuotedString(mScanWifi.getWifiName());
                if (wifiName.equals(ssid)) {
                    mScanWifi.setConnecting(false);
                    mScanWifi.setIsConnectedNow(true);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    /**
     * getViewHolder
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    private WifiListViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
        return WifiListViewHolder.get(context, convertView, parent, R.layout.layout_wireless_item, position);
    }
}
