package com.fotile.x1i.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.Tool;
import com.fotile.common.util.log.LogUtil;
import com.fotile.voice.CommonConst;
import com.fotile.voice.NetConfigBusiness;
import com.fotile.voice.NetConfigStateChangeListener;
import com.fotile.voice.bean.BoxLinkState;
import com.fotile.voice.bean.NluResult;
import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.dialog.BoxLinkPreDialog;
import com.fotile.x1i.adapter.GuideSmokeLinkListAdapter;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.bean.event.BoxLinkageMissEvent;
import com.fotile.x1i.dailog.BoxLinkExceptionDialog;
import com.fotile.x1i.dailog.CommonDialog;
import com.fotile.x1i.dailog.FullScreenDialog;
import com.fotile.x1i.listener.OnDialogListener;
import com.fotile.x1i.manager.DialogManager;
import com.fotile.x1i.manager.SpeechManager;
import com.fotile.x1i.server.wifilink.LinkAction;
import com.fotile.x1i.server.wifilink.LinkObserverable;
import com.fotile.x1i.server.wifilink.StoveWifiDevice;
import com.fotile.x1i.server.wifilink.WifiSearchObserverable;
import com.fotile.x1i.util.AppUtil;
import com.fotile.x1i.widget.RotationLoadingView;
import com.fotile.x1i.widget.SpeechFloatingLayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import fotile.device.cookerprotocollib.helper.DeviceControl;
import rx.functions.Action1;

import static com.fotile.common.util.PreferenceUtil.STOVE_LINK_DEVICE_MAC;

/**
 * @author chenyqi
 * @date 2019/4/18
 * @company 杭州方太智能科技有限公司
 * @description 设备联动页
 * <p>
 * Copyright (c) 2018, FOTILE GROUP.
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * - Neither the name of the FOTILE GROUP nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL FOTILE GROUP BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


public class DeviceLinkageActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemClickListener {
    public static final String TAG = DeviceLinkageActivity.class.getSimpleName();

    /**
     * 页面滚动控件
     */
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    /**
     * 烟灶联动开关
     */
    @BindView(R.id.btn_switch_stove)
    Switch btnSwitchStove;
    /**
     * 烟灶联动提示
     */
    @BindView(R.id.tv_smoke_stove_tips)
    TextView tvSmokeStoveTips;
    /**
     * 灶具列表
     */
    @BindView(R.id.list_view_stove)
    ListView listViewStove;
    /**
     * 打开烟灶联动时布局（包含灶具列表和未搜索到灶具提示）
     */
    @BindView(R.id.layout_list)
    RelativeLayout layoutList;
    /**
     * 没有搜索到灶具蓝牙时的布局
     */
    @BindView(R.id.layout_none_stove)
    LinearLayout layoutNoneStove;
    /**
     * 重新搜索
     */
    @BindView(R.id.tv_refresh)
    TextView tvRefresh;
    /**
     * 搜索灶具蓝牙loading
     */
    @BindView(R.id.search_loading_view)
    RotationLoadingView searchLoadingView;
    /**
     * 关闭烟灶联动时，确认dialog
     */
    CommonDialog closeDialog;
    /**
     * 联动灶具的适配器
     */
    private GuideSmokeLinkListAdapter listAdapter;

    private List<StoveWifiDevice> list = new ArrayList<StoveWifiDevice>();
    /**
     * 添加已经连接的设备
     */
    final int WHAT_MSG_ADD_LINKED_DEVICE = 1;
    /****************************************↑烟灶联动相关变量定义↑***************************************/

    /**
     * 添加语音助手
     */
    @BindView(R.id.btn_switch_voice)
    Switch mVBoxSb;

    @BindView(R.id.ll_add_box)
    LinearLayout mAddBoxLl;

    @BindView(R.id.connect_loading_view)
    RotationLoadingView mConnectLoadingRlv;

    /**
     * 语音助手添加成功后
     */
    @BindView(R.id.ll_add_ok)
    LinearLayout mAddOkLl;

    @BindView(R.id.rl_box_state)
    RelativeLayout mBoxStateRl;

    private boolean mInAdding;
    private static final int MSG_SHOW_ROTATE = 30001;
    private static final int MSG_SHOW_WIFI_LIST = 20001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(TAG, "is power on: " + PreferenceUtil.getBoxPowerState(context));
        mVBoxSb.setChecked(PreferenceUtil.getBoxPowerState(context) ||
                           SpeechManager.getInstance().isConnected());
        if (PreferenceUtil.getBoxPowerState(context) || SpeechManager.getInstance().isConnected()) {
            //开电源
            DeviceControl.getInstance().setVboxPower("on");
            //保存状态
            PreferenceUtil.setBoxPowerState(context, true);
        }
        mBoxStateRl.setVisibility((PreferenceUtil.getBoxPowerState(context) ||
                                   SpeechManager.getInstance().isConnected())
                                  ? View.VISIBLE
                                  : View.GONE);
        setRotationLoading(mConnectLoadingRlv, SpeechManager.getInstance().isConnecting());
        mAddBoxLl.setVisibility((SpeechManager.getInstance().isConnected() ||
                                 SpeechManager.getInstance().isConnecting())
                                ? View.GONE
                                : View.VISIBLE);
        mAddOkLl.setVisibility(
                SpeechManager.getInstance().isConnected() ? View.VISIBLE : View.GONE);
        mInAdding = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "on new intent");
        initView();
        mVBoxSb.setChecked(PreferenceUtil.getBoxPowerState(context) ||
                           SpeechManager.getInstance().isConnected());
        if (PreferenceUtil.getBoxPowerState(context) || SpeechManager.getInstance().isConnected()) {
            //开电源
            DeviceControl.getInstance().setVboxPower("on");
            //保存状态
            PreferenceUtil.setBoxPowerState(context, true);
        }
        mBoxStateRl.setVisibility((PreferenceUtil.getBoxPowerState(context) ||
                                   SpeechManager.getInstance().isConnected())
                                  ? View.VISIBLE
                                  : View.GONE);
        setRotationLoading(mConnectLoadingRlv, SpeechManager.getInstance().isConnecting());
        mAddBoxLl.setVisibility(
                (SpeechManager.getInstance().isConnected() ||
                 SpeechManager.getInstance().isConnecting()) ? View.GONE : View.VISIBLE);
        mAddOkLl.setVisibility(
                SpeechManager.getInstance().isConnected() ? View.VISIBLE : View.GONE);
        mInAdding = false;
    }

    /**
     * 初始化页面控件
     */
    private void initView() {
        //让ScrollView获得焦点
        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);
        scrollView.requestFocus();

        closeDialog = new CommonDialog(context, CommonDialog.CommonTip.TWO_BTN);
        closeDialog.setBtnTxt("确定关闭", "取消");
        closeDialog.setMessage("若关闭烟灶联动，\n烟灶联动相关功能将不可用");

        listAdapter = new GuideSmokeLinkListAdapter(context);
        listAdapter.setList(list);
        listViewStove.setAdapter(listAdapter);
        listViewStove.setOnItemClickListener(this);

        // 烟灶联动开关
        btnSwitchStove.setOnCheckedChangeListener(this);
        //上次开关状态
        boolean stoveLinkState = (boolean) PreferenceUtil.getPreferenceValue(context,
                PreferenceUtil.DEVICE_LINK_SMOKE_STOVE, true);
        btnSwitchStove.setChecked(stoveLinkState);
        if (stoveLinkState) {
            openSmokeStoveLink();
        }

        // 语音盒子
        mVBoxSb.setChecked(PreferenceUtil.getBoxPowerState(context));
        mVBoxSb.setOnCheckedChangeListener(this);
        mAddBoxLl.setOnClickListener(this);
    }

    private void initData() {
        EventBus.getDefault().register(this);
        //添加已经连接过的设备
        handler.sendEmptyMessageDelayed(WHAT_MSG_ADD_LINKED_DEVICE, 2000);

        NetConfigBusiness.getInstance().registerListener(mListener);
    }

    private NetConfigStateChangeListener mListener = new NetConfigStateChangeListener() {
        @Override
        public void onNetConfigStateChange(CommonConst.ConfigState state) {
            switch (state) {
                case START_TCP://ip未被屏蔽且homeId匹配
                    handler.sendEmptyMessage(MSG_SHOW_ROTATE);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNetConfigException(CommonConst.ConfigError errorType) {

        }

        @Override
        public void onDialogStateChange(CommonConst.DialogState state) {

        }

        @Override
        public void onHeard(int type, String asrResult) {

        }

        @Override
        public void onSpeak(String content) {

        }

        @Override
        public void onUnderStand(NluResult nluResult) {

        }

        @Override
        public void onWakeUp(String content) {

        }

        @Override
        public void onMusicStart(String musicList) {

        }

        @Override
        public void onMusicStop() {

        }

        @Override
        public void onRecipesGet(String recipes) {

        }

        @Override
        public void onVboxError(String error) {

        }
    };

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //添加已经连接过的设备
                case WHAT_MSG_ADD_LINKED_DEVICE:
                    addDefaultLinkedDevice();
                    break;
                case MSG_SHOW_ROTATE:
                    if (mAddBoxLl.isShown()) {
                        mAddBoxLl.setVisibility(View.GONE);
                        mAddOkLl.setVisibility(View.GONE);
                        setRotationLoading(mConnectLoadingRlv, true);
                    }
                    break;
                case MSG_SHOW_WIFI_LIST:
                    context.startActivity(new Intent(context, WifiListActivity.class));
                    break;
                default:
                    break;
            }
            return false;
        }
    });

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
                }
                //连接关闭
                if (state == LinkObserverable.STATE_NONE) {
                    listAdapter.setLinkState(LinkObserverable.STATE_NONE, position);
                }
                //连接失败
                if (state == LinkObserverable.STATE_ERROR) {
                    listAdapter.setLinkState(LinkObserverable.STATE_ERROR, position);
                    showLinkErrorDialog();
                }
            }
        };
    }

    /**
     * 连接失败后，提示
     */
    private void showLinkErrorDialog() {
        FullScreenDialog dialog = new FullScreenDialog(context,
                FullScreenDialog.FullScreenTip.ONE_BTN);
        dialog.setTitle("连接失败");
        dialog.setMessage("配对不成功，请重新连接");
        dialog.setBtnTxt("", "确定");
        dialog.show();
    }

    /**
     * 添加已经连接成功的蓝牙
     */
    private boolean addDefaultLinkedDevice() {
        StoveWifiDevice wifiDevice = LinkObserverable.getInstance(context).getLinkedStoveDevice();

        if (null != wifiDevice && wifiDevice.linkState == LinkObserverable.STATE_CONNECTED) {
            LogUtil.LOG_STOVE_LINK("已连接tcp设备addDefaultLinkedDevice",
                    null == wifiDevice ? "null" : wifiDevice.mac);
            if (!listContain(wifiDevice)) {
                list.add(wifiDevice);
                listAdapter.setList(list);
                listAdapter.notifyDataSetChanged();
                return true;
            }
        }
        return false;
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

    /**
     * 打开烟灶联动开关时执行的动作
     */
    private void openSmokeStoveLink() {
        //开启搜索
        WifiSearchObserverable.getInstance(context).startSearch();

        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.DEVICE_LINK_SMOKE_STOVE, true);
        tvSmokeStoveTips.setVisibility(View.GONE);
        layoutList.setVisibility(View.VISIBLE);

        setResultView(true);
        //显示loading
        setRotationLoading(true);
    }

    /**
     * 关闭烟灶联动开关时执行的动作
     */
    private void closeSmokeLinkStove() {
        //关闭搜索
        WifiSearchObserverable.getInstance(context).stopSearch(true);

        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.DEVICE_LINK_SMOKE_STOVE, false);
        tvSmokeStoveTips.setVisibility(View.VISIBLE);
        layoutList.setVisibility(View.GONE);

        setResultView(false);
        //隐藏loading
        setRotationLoading(false);

        //清空
        list.clear();
        listAdapter.setList(list);
        listAdapter.notifyDataSetChanged();
        //关闭连接
        LinkObserverable.getInstance(context).disConnection();
        //移除延时主动关逻辑
        LinkAction.getInstance(context).removeCloseWifiWhat();
    }

    /**
     * 设置是否有搜索结果的显示view
     *
     * @param hasResult 是否有蓝牙搜索结果
     */
    private void setResultView(boolean hasResult) {
        if (hasResult) {
            listViewStove.setVisibility(View.VISIBLE);
            layoutNoneStove.setVisibility(View.GONE);
        } else {
            listViewStove.setVisibility(View.GONE);
            layoutNoneStove.setVisibility(View.VISIBLE);
        }
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

    /**
     * loading显示与隐藏
     *
     * @param show
     */
    private void setRotationLoading(RotationLoadingView view, boolean show) {
        if (show) {
            view.setVisibility(View.VISIBLE);
            view.startRotationAnimation();
        } else {
            view.setVisibility(View.GONE);
            view.stopRotationAnimation();
        }
    }


    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_device_linkage;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //添加语音盒子
            case R.id.ll_add_box:
                if (!mInAdding) {
                    mInAdding = true;
                    showLinkageBox();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            //灶具开关
            case R.id.btn_switch_stove:
                if (isChecked) {
                    openSmokeStoveLink();
                } else {
                    closeDialog.setOnDialogListener(new OnDialogListener() {
                        //点击关闭烟灶联动
                        @Override
                        public void onLeftClick(Object... objects) {
                            closeSmokeLinkStove();
                        }

                        //点击取消
                        @Override
                        public void onRightClick(Object... objects) {
                            btnSwitchStove.setChecked(true);
                        }
                    });
                    closeDialog.show();
                }
                break;
            case R.id.btn_switch_voice:
                if (isChecked) {
                    //开电源
                    DeviceControl.getInstance().setVboxPower("on");
                    //保存状态
                    PreferenceUtil.setBoxPowerState(context, true);
                    //设置接收盒子信息
                    PreferenceUtil.setPreferenceValue(context, PreferenceUtil.RECEIVE_BOX_MSG,
                            true);
                    //若有wifi连接，则开始发送探测包
                    if (Tool.getNetWorkState(context) == Tool.NETWORK_WIFI) {
                        SpeechManager.getInstance().probeBox();
                    }
                    //更新ui
                    mBoxStateRl.setVisibility(View.VISIBLE);
                } else {
                    DialogManager.getInstance().showExceptionDialog(context, "提示",
                            "若关闭语音助手，语音功能将无法使用", "确定关闭", true, "取消", false, false,
                            new BoxLinkExceptionDialog.BtnClickListener() {
                                @Override
                                public void onTopBtnClick() {
                                    //向盒子发送断开请求
                                    NetConfigBusiness.getInstance().requestLeave(true);
                                    //设置不接收盒子信息
                                    PreferenceUtil.setPreferenceValue(context,
                                            PreferenceUtil.RECEIVE_BOX_MSG, false);
                                    //停止发送探测包
                                    NetConfigBusiness.getInstance().stopProbe();
                                    //语音盒子连接状态变化
                                    NetConfigBusiness.getInstance().setLeaveState(true);
                                    NetConfigBusiness.getInstance().restoreState(true);
                                    //语音盒子相关ui变化
                                    SpeechFloatingLayer.get(context).forceDismiss(true);
                                    //断电
                                    DeviceControl.getInstance().setVboxPower("off");
                                    //                                    NetConfigBusiness.getInstance().powerOff();
                                    //保存供电状态
                                    PreferenceUtil.setBoxPowerState(context, false);
                                    //设备联动ui变化
                                    mBoxStateRl.setVisibility(View.GONE);
                                }

                                @Override
                                public void onBottomBtnClick() {
                                    mVBoxSb.setChecked(true);
                                }
                            });
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StoveWifiDevice wifiDevice = (StoveWifiDevice) listAdapter.getItem(position);
        if (null != wifiDevice) {
            int linkState = wifiDevice.linkState;

            // 点击处于已连接状态的灶具设备 则提示是否关闭
            if (linkState == LinkObserverable.STATE_CONNECTED) {
                closeWifiLink(wifiDevice);
                return;
            }
            //点击一个正在连接的设备
            if (linkState == LinkObserverable.STATE_CONNECTING) {
                return;
            }
            //点击一个没有连接的蓝牙,或者经过重连后error的蓝牙
            if (linkState == LinkObserverable.STATE_NONE ||
                linkState == LinkObserverable.STATE_ERROR) {
                //开始连接
                LinkObserverable.getInstance(context).connect(wifiDevice);
            }
        }
    }

    /**
     * 断开某一个wifi联动，点击某一个item执行
     */
    private void closeWifiLink(StoveWifiDevice wifiDevice) {
        if (null != wifiDevice) {
            FullScreenDialog dialog = new FullScreenDialog(context,
                    FullScreenDialog.FullScreenTip.TWO_BTN);
            dialog.setTitle("断开连接");
            dialog.setMessage("是否断开烟灶联动");
            dialog.setBtnTxt("取消", "断开连接");
            dialog.setOnDialogListener(new OnDialogListener() {
                @Override
                public void onLeftClick(Object... objects) {
                }

                //点击断开连接
                @Override
                public void onRightClick(Object... objects) {
                    //断开连接
                    LinkObserverable.getInstance(context).disConnection();
                    //移除延时主动关逻辑
                    LinkAction.getInstance(context).removeCloseWifiWhat();
                    //清空mac地址
                    PreferenceUtil.setPreferenceValue(context, STOVE_LINK_DEVICE_MAC, "");
                }
            });
            dialog.show();
        }
    }

    @Subscribe
    public void onEventBoxLinkStateChanged(BoxLinkState state) {
        if (state != null) {
            setRotationLoading(mConnectLoadingRlv, false);
            mAddBoxLl.setVisibility(state.isBoxLinked() ? View.GONE : View.VISIBLE);
            mAddOkLl.setVisibility(state.isBoxLinked() ? View.VISIBLE : View.GONE);
            //若未供电且盒子已连接
            if (!PreferenceUtil.getBoxPowerState(context) && state.isBoxLinked()) {
                //打开供电开关
                DeviceControl.getInstance().setVboxPower("on");
                //保存供电状态
                PreferenceUtil.setBoxPowerState(context, true);
                mBoxStateRl.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe
    public void onEventBoxLinkageDialogMiss(BoxLinkageMissEvent event) {
        if (event != null) {
            mInAdding = false;
        }
    }

    /**
     * 显示连接dialog
     */
    private void showLinkageBox() {
        //暂时去除定时消失逻辑
        //        mHandler.sendEmptyMessageDelayed(MSG_BIND_SPEECH_END, CommonConst.ONE_MINUTE);
        //开启ap，等待用户根据提示操作盒子
        if (SpeechManager.getInstance().bindSpeech()) {
            BoxLinkPreDialog boxDialog = new BoxLinkPreDialog(context);
            boxDialog.show();
        } else {
            //ap放出失败，显示异常对话框
            DialogManager.getInstance().showExceptionDialog(AppUtil.getCurrentActivity(context),
                    context.getString(R.string.str_box_add_fail_title),
                    context.getString(R.string.str_box_wifi_timeout), "去检查", true, "确定", false,
                    PreferenceUtil.isInGuide(context),
                    new BoxLinkExceptionDialog.BtnClickListener() {
                        @Override
                        public void onTopBtnClick() {
                            NetConfigBusiness.getInstance().restoreState(false);
                            handler.sendEmptyMessage(MSG_SHOW_WIFI_LIST);
                        }

                        @Override
                        public void onBottomBtnClick() {
                            NetConfigBusiness.getInstance().restoreState(false);
                            mInAdding = false;
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mListener != null) {
            NetConfigBusiness.getInstance().unRegisterListener(mListener);
        }
    }
}
