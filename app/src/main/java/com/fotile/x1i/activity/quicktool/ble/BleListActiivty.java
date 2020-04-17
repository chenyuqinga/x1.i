package com.fotile.x1i.activity.quicktool.ble;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

import com.ebanswers.ble.BLEDevice;
import com.ebanswers.ble.BLESerialManager;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.adapter.BleMusicListAdapter;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.dailog.CommonDialog;
import com.fotile.x1i.dailog.FullScreenDialog;
import com.fotile.x1i.listener.OnDialogListener;
import com.fotile.x1i.server.blemusic.BLELinkObserverable;
import com.fotile.x1i.server.blemusic.BLESearchObserverable;
import com.fotile.x1i.widget.RotationLoadingView;

import java.util.ArrayList;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * 文件名称：BleListActiivty
 * 创建时间：2019/7/24 16:26
 * 文件作者：yaohx
 * 功能描述：蓝牙音箱
 */
public class BleListActiivty extends BaseActivity implements CompoundButton.OnCheckedChangeListener, AdapterView
        .OnItemClickListener, OnClickListener {

    @BindView(R.id.btn_switch)
    Switch btnSwitch;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.rotation_loading)
    RotationLoadingView rotationLoadingView;
    @BindView(R.id.img_cover)
    ImageView imgCover;

    Action1<BLEDevice> searchAction;
    Action1<String> linkAction;
    /**
     * 搜索到的设备列表
     */
    private ArrayList<BLEDevice> list = new ArrayList<BLEDevice>();

    private BleMusicListAdapter listAdapter;
    /**
     * 配对码
     */
    private CommonDialog pairDialog;
    /**
     * ble连接超时
     */
    final int WHAT_BLE_LINK_TIMEOUT = 1002;
    /**
     * 添加已经存在的连接设备
     */
    final int WHAT_BLE_ADD_DEFAULT = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        createBleSearchAction();
        createBleLinkAction();
        initData();
    }

    private void initView() {
        imgCover.setOnClickListener(this);
        //listview
        listAdapter = new BleMusicListAdapter(context);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

        boolean open = PreferenceUtil.getBleMusicSwitch(context);
        btnSwitch.setChecked(open);
        //如果上一次保存的是开
        if (open) {
            //Switch打开时，cover显示，用于接受点击事件
            imgCover.setVisibility(View.VISIBLE);
            openSwitch();
        }
        btnSwitch.setOnCheckedChangeListener(this);

    }

    private void initData() {
//        BLESerialManager.getInstance().setBleVisible();//设置可见值后, 已连接的蓝牙会断开

        BLESearchObserverable.getInstance(context).addSearchAction(searchAction);
        BLELinkObserverable.getInstance().addLinkAction(linkAction);

        handler.sendEmptyMessageDelayed(WHAT_BLE_ADD_DEFAULT, 2000);
    }

    public void createBleSearchAction() {
        searchAction = new Action1<BLEDevice>() {
            @Override
            public void call(BLEDevice bleDevice) {
                if (null != bleDevice) {
                    //搜索中
                    if (bleDevice.isSearching()) {
                        LogUtil.LOG_BLE_MUSIC("搜索到的蓝牙音箱", bleDevice);
                        showRotation(true);

                        if (!containsDevice(bleDevice)) {
                            list.add(bleDevice);
                            listAdapter.setList(list);
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                    //搜索结束
                    else {
                        showRotation(false);
                    }
                }
            }
        };
    }

    public void createBleLinkAction() {
        linkAction = new Action1<String>() {
            @Override
            public void call(String connected_mac) {
                //移除上一次的超时
                handler.removeMessages(WHAT_BLE_LINK_TIMEOUT);
                //连接成功
                if (!TextUtils.isEmpty(connected_mac)) {
                    listAdapter.setLinkState(BLEDevice.STATUS_CONNECTED, connected_mac);
                    //隐藏配对码dialog
                    if (null != pairDialog) {
                        pairDialog.dismiss();
                    }
                }
                //板子ble模块断开连接
                else {
                    listAdapter.setLinkState(BLEDevice.STATUS_NORMAL, connected_mac);
                }
            }
        };
    }

    /**
     * list中是否包含相同mac的ble
     *
     * @return
     */
    private synchronized boolean containsDevice(BLEDevice bleDevice) {
        if (null != list) {
            String tagMac = bleDevice.getMac_address();
            for (BLEDevice b : list) {
                if (TextUtils.equals(tagMac, b.getMac_address())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        //移除连接超时handler
        handler.removeCallbacksAndMessages(null);
        //停止搜索
        BLESearchObserverable.getInstance(context).removeSearchAction(searchAction);
        BLESearchObserverable.getInstance(context).stopSearch(false);
        //移除连接action
        BLELinkObserverable.getInstance().removeLinkAction(linkAction);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //防止清理数据或回复出厂设置之后,设备显示断开,但是手机已连接
        BLESerialManager.getInstance().enableBle(PreferenceUtil.getBleMusicSwitch(context));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_ble_list;
    }

    @Override
    public boolean showBottom() {
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            //Switch打开时，cover显示，用于接受点击事件
            imgCover.setVisibility(View.VISIBLE);
            openSwitch();
        } else {
            //Switch关闭时，cover隐藏，用于打开Switch开关
            imgCover.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击询问是否关闭ble蓝牙
            case R.id.img_cover:
                String connectMac = BLESerialManager.connected_mac;
                //如果有连接的设备
                if (!TextUtils.isEmpty(connectMac)) {
                    BLEDevice bleDevice = listAdapter.getLinkiedDevice();
                    showDisconnectDialog(bleDevice, 2);
                } else {
                    closeSwitch();
                    btnSwitch.setChecked(false);
                }
                break;
        }
    }

    /**
     * 开启蓝牙搜索
     */
    private void openSwitch() {
        showRotation(true);
        //保存开关值
        PreferenceUtil.setBleMusicSwitch(context, true);
        //打开系统蓝牙开关
        BluetoothAdapter.getDefaultAdapter().enable();
        //清空list
        list.clear();
        listAdapter.setList(list);
        listAdapter.notifyDataSetChanged();

        //开始搜索
        BLESearchObserverable.getInstance(context).startSearch();
    }

    private void closeSwitch() {
        showRotation(false);
        //保存开关值
        PreferenceUtil.setBleMusicSwitch(context, false);
        //关闭系统蓝牙开关
        BluetoothAdapter.getDefaultAdapter().disable();
        //清空list
        list.clear();
        listAdapter.setList(list);
        listAdapter.notifyDataSetChanged();

        //结束搜索
        BLESearchObserverable.getInstance(context).stopSearch(true);
    }

    private void showRotation(boolean isshow) {
        if (isshow) {
            rotationLoadingView.startRotationAnimation();
            rotationLoadingView.setVisibility(View.VISIBLE);
        } else {
            rotationLoadingView.stopRotationAnimation();
            rotationLoadingView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0 && position <= list.size() - 1) {
            if (listAdapter.hasDeviceLinking()) {
                return;
            }
            BLEDevice device = list.get(position);
            String mac = device.getMac_address();

            //点击的设备是已连接状态
            if (TextUtils.equals(mac, BLESerialManager.connected_mac) || device.getStatus() == BLEDevice
                    .STATUS_CONNECTED) {
                LogUtil.LOG_BLE_MUSIC("蓝牙音箱已连接到", BLESerialManager.connected_name);
                showDisconnectDialog(device, 1);
            } else {
                //开始连接
                BLELinkObserverable.getInstance().connect(device);
                //显示配对码
                showPairDialog();
                //将点击的item设置为 connecting
                listAdapter.setLinkState(BLEDevice.STATUS_CONNECTING, position);
                //移除上一次的超时
                handler.removeMessages(WHAT_BLE_LINK_TIMEOUT);
                //连接超时后将item状态更新为 normal
                Message message = new Message();
                message.what = WHAT_BLE_LINK_TIMEOUT;
                message.obj = position;
                handler.sendMessageDelayed(message, 30 * 1000);
            }
        }
    }

    /**
     * 断开连接dialog
     *
     * @param bleDevice
     * @param type     type == 1 点击item事件
     *                  type == 2 点击Switch开关事件
     */
    private void showDisconnectDialog(final BLEDevice bleDevice, final int type) {
        FullScreenDialog dialog = new FullScreenDialog(context, FullScreenDialog.FullScreenTip.TWO_BTN);
        dialog.setTitle("断开连接");
        dialog.setMessage("是否断开蓝牙连接");
        dialog.setBtnTxt("取消", "断开连接");
        dialog.show();
        dialog.setOnDialogListener(new OnDialogListener() {
            @Override
            public void onLeftClick(Object... objects) {
            }

            //点击断开连接
            @Override
            public void onRightClick(Object... objects) {
                BLELinkObserverable.getInstance().disConnect(bleDevice);
                closeSwitch();
                //Switch开关，点击断开连接时，将开关置位false
                if (type == 2) {
                    btnSwitch.setChecked(false);
                }
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //蓝牙连接超时
                case WHAT_BLE_LINK_TIMEOUT:
                    if (TextUtils.isEmpty(BLESerialManager.connected_mac)) {
                        int position = (int) msg.obj;
                        LogUtil.LOG_BLE_MUSIC("蓝牙音箱连接超时", "item positon:" + position);
                        //更新item
                        listAdapter.setLinkState(BLEDevice.STATUS_NORMAL, position);

                        //隐藏配对码dialog
                        if (null != pairDialog) {
                            pairDialog.dismiss();
                        }
                        showPairErrorDialog();
                    }
                    break;
                //添加已经连接的设备
                case WHAT_BLE_ADD_DEFAULT:
                    addDefaultLinkDevice();
                    break;
            }
            return false;
        }
    });

    /**
     * 配对失败
     */
    private void showPairErrorDialog() {
        FullScreenDialog dialog = new FullScreenDialog(context, FullScreenDialog.FullScreenTip.ONE_BTN);
        dialog.setTitle("连接失败");
        dialog.setMessage("配对不成功，请重新连接");
        dialog.show();
    }

    private void addDefaultLinkDevice() {
        String mac = BLESerialManager.connected_mac;
        String name = BLESerialManager.connected_name;

        LogUtil.LOG_BLE_MUSIC("addDefaultLinkDevice", name);
        if (!TextUtils.isEmpty(mac)) {
            BLEDevice bleDevice = new BLEDevice(mac, name);
            bleDevice.setStatus(BLEDevice.STATUS_CONNECTED);
            //如果列表中没有相同的mac
            if (!containsDevice(bleDevice)) {
                list.add(bleDevice);
                listAdapter.setList(list);
                listAdapter.notifyDataSetChanged();
            }
            //如果列表中已经有该mac
            else {
                listAdapter.setLinkState(BLEDevice.STATUS_CONNECTED, mac);
            }
        }
    }

    /**
     * 配对码
     */
    public void showPairDialog() {
        if (null == pairDialog) {
            pairDialog = new CommonDialog(context, CommonDialog.CommonTip.ONE_BTN);
            pairDialog.setMessage("配对码：0000");
        }
        pairDialog.show();
    }
}
