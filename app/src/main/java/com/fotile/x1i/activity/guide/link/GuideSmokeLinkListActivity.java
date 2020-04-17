package com.fotile.x1i.activity.guide.link;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.WifiListActivity;
import com.fotile.x1i.adapter.GuideSmokeLinkListAdapter;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.server.wifilink.LinkObserverable;
import com.fotile.x1i.server.wifilink.WifiSearchObserverable;
import com.fotile.x1i.server.wifilink.StoveWifiDevice;
import com.fotile.x1i.widget.RotationLoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * 文件名称：GuideSmokeLinkListActivity
 * 创建时间：2019/5/13 15:24
 * 文件作者：yaohx
 * 功能描述：烟灶联动设置页面
 */
public class GuideSmokeLinkListActivity extends BaseActivity implements View.OnClickListener, AdapterView
        .OnItemClickListener {
    /**
     * 跳过
     */
    @BindView(R.id.txt_jump)
    TextView txtJump;
    /**
     * 下一步
     */
    @BindView(R.id.txt_next)
    TextView txtNext;
    @BindView(R.id.search_loading_view)
    RotationLoadingView searchLoadingView;
    /**
     * 未搜索到蓝牙
     */
    @BindView(R.id.txt_none_tip)
    View tvNoneTip;

    /**
     * 重新搜索
     */
    @BindView(R.id.txt_again)
    TextView txtAgain;
    /**
     * 重新搜索刷新
     */
    @BindView(R.id.rotation_loading)
    RotationLoadingView searchAgain;

    /**
     * 是否连接到灶具
     */
    private boolean isStoveLinked;

    private List<StoveWifiDevice> list = new ArrayList<StoveWifiDevice>();
    /**
     * 联动灶具的适配器
     */
    private GuideSmokeLinkListAdapter listAdapter;
    @BindView(R.id.listview)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        //开始搜索
        WifiSearchObserverable.getInstance(context).startSearch();
        //保存联动开关为打开
        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.DEVICE_LINK_SMOKE_STOVE, true);
        setRotationLoading(true);
    }

    private void initView() {
        txtJump.setOnClickListener(this);
        txtNext.setOnClickListener(this);
        txtAgain.setOnClickListener(this);
        listAdapter = new GuideSmokeLinkListAdapter(context);
        listAdapter.setList(list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

        //默认是能搜索到蓝牙的
        setResultView(true);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StoveWifiDevice wifiDevice = (StoveWifiDevice) listAdapter.getItem(position);
        if (null != wifiDevice) {
            int linkState = wifiDevice.linkState;

            // 点击处于已连接状态的灶具设备 则提示是否关闭
            if (linkState == LinkObserverable.STATE_CONNECTED) {
                return;
            }
            //点击一个正在连接的设备
            if(linkState == LinkObserverable.STATE_CONNECTING){
                return;
            }
            //点击一个没有连接的蓝牙,或者经过重连后error的蓝牙
            if (linkState == LinkObserverable.STATE_NONE || linkState == LinkObserverable.STATE_ERROR) {
                //开始连接
                LinkObserverable.getInstance(context).connect(wifiDevice);
            }
        }
    }

    @Override
    public void createSearchAction() {
        actionSearchStove = new Action1<StoveWifiDevice>() {
            @Override
            public void call(StoveWifiDevice wifiDevice) {
                if (null != wifiDevice) {
                    //搜索中
                    if (wifiDevice.searching) {
                        if (!listContain(wifiDevice)) {
                            list.add(wifiDevice);
                            listAdapter.setList(list);
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                    //搜索结束
                    else {
                        //隐藏loading
                        setRotationLoading(false);
                    }
                    //有搜索结果，显示搜索结果
                    if (list.size() != 0) {
                        setResultView(true);
                    } else {
                        setResultView(false);
                    }
                }
            }
        };
    }

    @Override
    public void createLinkAction() {
       actionLinkStove = new Action1<StoveWifiDevice>() {

           @Override
           public void call(StoveWifiDevice wifiDevice) {
               //获取在列表中对应的position
               int position = listAdapter.getPosition(wifiDevice);
               Resources resources = (Resources) context.getResources();
               ColorStateList colorStateList = (ColorStateList) resources.getColorStateList(R.color.list_color_state);
               if (position == -1) {
                   return;
               }
               int state = wifiDevice.linkState;
               //连接中
               if (state == LinkObserverable.STATE_CONNECTING) {
                   listAdapter.setLinkState(LinkObserverable.STATE_CONNECTING, position);
               }
               //连接成功
               if (state == LinkObserverable.STATE_CONNECTED) {
                   listAdapter.setLinkState(LinkObserverable.STATE_CONNECTED, position);
                   txtNext.setEnabled(true);
                   if (colorStateList != null) {
                       txtNext.setTextColor(colorStateList);
                   }
                   txtNext.setBackgroundResource(R.drawable.shape_button_select);
               }
               //连接关闭
               if (state == LinkObserverable.STATE_NONE) {
                   listAdapter.setLinkState(LinkObserverable.STATE_NONE, position);
                   txtNext.setEnabled(false);
                   if (colorStateList != null) {
                       txtNext.setTextColor(colorStateList);
                   }
                   txtNext.setBackgroundResource(R.drawable.shape_button_normal);
               }
           }
       };
    }

    /**
     * loading显示与隐藏
     *
     * @param show
     */
    private void setRotationLoading(boolean show) {
        if (show) {
            searchLoadingView.setVisibility(View.VISIBLE);
            searchLoadingView.startRotationAnimation();
        } else {
            searchLoadingView.setVisibility(View.INVISIBLE);
            searchLoadingView.stopRotationAnimation();
        }
    }

    private void setResultView(boolean hasResult) {
        if (hasResult) {
            listView.setVisibility(View.VISIBLE);
            tvNoneTip.setVisibility(View.GONE);

            //有搜过结果
            txtAgain.setVisibility(View.GONE);
            searchAgain.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            tvNoneTip.setVisibility(View.VISIBLE);

            //无搜索结果
            txtAgain.setVisibility(View.VISIBLE);
            searchAgain.setVisibility(View.VISIBLE);
            searchAgain.stopRotationAnimation();
        }
    }

    /**
     * 判断列表中是否存在该对象
     *
     * @param wifiDevice
     * @return
     */
    private synchronized boolean listContain(StoveWifiDevice wifiDevice) {
        if (null != wifiDevice) {
            String name = wifiDevice.getDeviceName();
            if (!TextUtils.isEmpty(name)) {
                for (StoveWifiDevice b : list) {
                    if (name.equals(b.getDeviceName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_guide_stove_list;
    }

    @Override
    public boolean showBottom() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击跳过进入wifi设置
            case R.id.txt_jump:
                Intent intent = new Intent(this, WifiListActivity.class);
                intent.putExtra("guide", true);
                launchActivity(intent);
                break;
            //连接成功后，可以点击执行下一步，进入wifi设置
            case R.id.txt_next:
                if (isStoveLinked) {
                    intent = new Intent(this, WifiListActivity.class);
                    intent.putExtra("guide", true);
                    launchActivity(intent);
                }
                break;
                //重新搜索
            case R.id.txt_again:
                list.clear();
                //开始搜索
                WifiSearchObserverable.getInstance(context).startSearch();
                setRotationLoading(true);
                searchAgain.startRotationAnimation();
                break;
        }
    }
}
