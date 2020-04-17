package com.fotile.x1i.activity.quicktool.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import com.ebanswers.ble.BLESerialManager;
import com.fotile.common.util.PreferenceUtil;
import com.fotile.common.util.log.LogUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.adapter.BlueToothLinkListAdapter;
import com.fotile.x1i.base.BaseActivity;
import com.fotile.x1i.dailog.CommonDialog;
import com.fotile.x1i.dailog.FullScreenDialog;
import com.fotile.x1i.listener.OnDialogListener;

import com.fotile.x1i.server.BleServer;
import com.fotile.x1i.widget.RotationLoadingView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import fotile.ble.bean.BleDevice;
import fotile.ble.observer.link.ILinkObserverable;
import fotile.ble.observer.link.LinkFactory;
import fotile.ble.observer.search.SearchObserverable;
import fotile.ble.util.BleAction;
import rx.functions.Action1;

/**
 * 文件名称：BleListActiivty
 * 创建时间：2019/7/24 16:26
 * 文件作者：yaohx
 * 功能描述：安卓自研开发板蓝牙搜索
 */
public class BleListActiivty1 extends BaseActivity implements CompoundButton.OnCheckedChangeListener, AdapterView
        .OnItemClickListener, OnClickListener {

    @BindView(R.id.btn_switch)
    Switch btnSwitch;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.rotation_loading)
    RotationLoadingView rotationLoadingView;
    @BindView(R.id.img_cover)
    ImageView imgCover;

    /**
     * 蓝牙搜索回调
     */
    private Action1 actionSearch;
    LinkAction linkAction = new LinkAction();

    /**
     * 蓝牙搜索结果列表
     */
    List<BleDevice> list = new ArrayList<>();

    private BlueToothLinkListAdapter listAdapter;
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
    /**
     * 打开系统蓝牙开关
     */
    private Timer openTimer;
    /**
     * 打开系统蓝牙开关后，开始执行蓝牙搜索
     */
    private final int WHAT_START_SEARCH = 1005;
    /**
     * 蓝牙灶具搜索观察者
     */
    private SearchObserverable searchObserverable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        createBleSearchAction();
        addDefaultLinkingDevice();
        initData();
    }

    private void initView() {
        imgCover.setOnClickListener(this);
        //listview
        listAdapter = new BlueToothLinkListAdapter(context);
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

        handler.sendEmptyMessageDelayed(WHAT_BLE_ADD_DEFAULT, 2000);
    }

    public void createBleSearchAction() {
        actionSearch = new Action1<BleDevice>() {
            @Override
            public synchronized void call(BleDevice bleDevice) {
                int linkStatus = bleDevice.linkStatus;
                LogUtil.LOG_TOOTH("BleSearchAction",linkStatus);
                //搜索中
                if (bleDevice.searchStatus == BleDevice.SEARCHING) {
                    showRotation(true);
                    //开关未打开时不接受搜索结果
                    if (!PreferenceUtil.getDeviceLinkSwitch(context)) {
                        return;
                    }
                    BluetoothDevice bluetoothDevice = bleDevice.bluetoothDevice;
                    if ( !listContain(bleDevice)) {
                            list.add(bleDevice);
                            listAdapter.setList(list);
                            listAdapter.notifyDataSetChanged();
                        LogUtil.LOG_TOOTH("BleSearchAction--搜索中",linkStatus+"---"+!listContain(bleDevice)+"----"+list);
                    }

                }
                //已连接
                else if (linkStatus == ILinkObserverable.STATE_CONNECTED) {
                    cancelOpenTimer();
                    showRotation(false);
                    closePairDialog();
                    listAdapter.setList(list);
                    listAdapter.notifyDataSetChanged();
//                    addDefaultLinkedDevice(bleDevice);
//                    LogUtil.LOG_TOOTH("BleSearchAction--已连接",linkStatus+"---"+ addDefaultLinkedDevice(bleDevice));
                    //更换item显示
                    int position = listAdapter.getPosition(bleDevice.getAddress());
                    if (position != -1) {
                        listAdapter.notifyItemLinkState(ILinkObserverable.STATE_CONNECTED, position);
//                        //连接成功后，取消搜索，高丹提出的需求
//                        BleAction.getInstance().stopDeviceSearch(BleServer.class);
                    }
                }
                //未连接或连接错误
                else if (linkStatus  == ILinkObserverable.STATE_NONE || linkStatus == ILinkObserverable.STATE_ERROR){
                    LogUtil.LOG_TOOTH("BleSearchAction--未连接或连接错误",linkStatus);
                    showRotation(false);

                    listAdapter.setList(list);
                    listAdapter.notifyDataSetChanged();
                    //隐藏配对码dialog
                    if (null != pairDialog) {
                        pairDialog.dismiss();
                    }
                    //显示配对失败
//                   showPairErrorDialog();
                }
                //配对正在连接中
                else if(linkStatus  == ILinkObserverable.STATE_CONNECTING){
                    LogUtil.LOG_TOOTH("BleSearchAction--连接中",linkStatus);
                }
                //搜索结束
                if (bleDevice.searchStatus == BleDevice.SEARCHOVER) {
                    LogUtil.LOG_TOOTH("BleSearchAction","搜索结束");
                    //搜索结束，停止转圈
                    showRotation(false);
                }
                listAdapter.notifyItemLinkState(linkStatus, getPosition(bleDevice));
            }};
        searchObserverable = SearchObserverable.getInstance(context);
        //添加搜索回调
        searchObserverable.addSearchObserver(actionSearch);
    }

    /**
     * 处理正在连接中的 Ble 设备
     */
    private void addDefaultLinkingDevice() {
        //上次开关状态
        boolean stoveLinkState = PreferenceUtil.getDeviceLinkSwitch(context);
        if (stoveLinkState) {
            //            //先搜索两秒钟后才添加已经连接成功的 BT/Ble（2019/12/10新增需求 连接成功修改为不搜索）
            //            mHandler.sendEmptyMessageDelayed(WHAT_ADD_DEFAULT_LINKED, 2000);
            //给正在连接的ble设备添加LinkAction回调，因为正在连接的ble设备不会广播，搜索不到
            //搜索不到正在连接的ble设备会导致添加不了 LinkAction
            ILinkObserverable linkObserverable = LinkFactory.getLinkingObserverable();
            if (null != linkObserverable) {
                linkObserverable.addLinkObserver(linkAction);
            }
        }
    }

            /**
             * 判断列表中是否存在该对象
             *
             * @param bleDevice
             * @return
             */
            private synchronized boolean listContain(BleDevice bleDevice) {
                if (null != bleDevice && null != bleDevice.bluetoothDevice) {
                    String name = bleDevice.getName();
                    if (!TextUtils.isEmpty(name)) {
                        for (BleDevice b : list) {
                            if (name.equals(b.getName())) {
                                return true;
                            }
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
        //退出页面停止搜索，院长2019/12/10新增需求
        BleAction.getInstance().stopDeviceSearch(BleServer.class);
        //移除action
        searchObserverable.removeSearchObserver(actionSearch);
        LinkFactory.removeLinkeAction(linkAction);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                BleDevice bleDevice = LinkFactory.getLinkDevice();
                //如果有连接的设备
                if (bleDevice != null) {
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
        //保存开关值
        PreferenceUtil.setBleMusicSwitch(context, true);
        //打开系统蓝牙开关
        BluetoothAdapter.getDefaultAdapter().enable();
        listView.setVisibility(View.VISIBLE);
        //清空list
        list.clear();
        listAdapter.setList(list);
        listAdapter.notifyDataSetChanged();
        //如果没有已连接设备，才执行搜索
        boolean linked = addDefaultLinkedDevice();
        if(!linked){
            //显示loading
            showRotation(true);
            //打开蓝牙开关需要一个过程
            cancelOpenTimer();
            openTimer = new Timer();
            openTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    boolean open = BluetoothAdapter.getDefaultAdapter().isEnabled();
                    LogUtil.LOG_TOOTH("系统蓝牙enable", open);
                    if (open) {
                        //开始蓝牙搜索
                        handler.sendEmptyMessageDelayed(WHAT_START_SEARCH, 800);
                        //取消定时器
                        cancelOpenTimer();
                    }
                }
            }, 0, 1000);
        }
    }
    /**
     * 取消定时器
     */
    private synchronized void cancelOpenTimer() {
        if (null != openTimer) {
            openTimer.cancel();
            openTimer.purge();
            openTimer = null;
        }
    }
    private void closeSwitch() {
        showRotation(false);
        //取消定时器
        cancelOpenTimer();
        //关闭系统蓝牙开关
        BluetoothAdapter.getDefaultAdapter().disable();
        //保存开关值
        PreferenceUtil.setBleMusicSwitch(context, false);
        //清空list
        list.clear();
        listAdapter.setList(list);
        listAdapter.notifyDataSetChanged();
        listView.setVisibility(View.GONE);
        //关闭蓝牙搜索，防止搜索到重连设备发起重连
        BleAction.getInstance().stopDeviceSearch(BleServer.class);
        //关闭所有蓝牙连接
        LinkFactory.disConnectAllDevice();
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
            BleDevice bleDevice = (BleDevice) listAdapter.getItem(position);
            if (null != bleDevice) {
                LogUtil.LOG_TOOTH("蓝牙列表item状态---hasDeviceLinking", bleDevice.getName() + "：" + listAdapter.hasDeviceLinking());
                //如果有设备正在连接
                if (listAdapter.hasDeviceLinking()) {
                    return;
                }
                int linkState = listAdapter.getLinkState(position);
                LogUtil.LOG_TOOTH("蓝牙列表item状态---linkState", bleDevice.getName() + "：" + linkState);

                // 点击处于已连接状态的灶具蓝牙 则提示是否关闭
                if (linkState == ILinkObserverable.STATE_CONNECTED) {
                    LogUtil.LOG_TOOTH("蓝牙列表item状态---点击已连接蓝牙", bleDevice.getName() + "：" + linkState);
                    showDisconnectDialog(bleDevice, 1);
                    return;
                }
                //点击一个没有连接的蓝牙,或者经过重连后error的蓝牙
                if (linkState == ILinkObserverable.STATE_NONE || linkState == ILinkObserverable.STATE_ERROR) {
                    //开始连接
                    BleAction.getInstance().startDeviceLink(bleDevice, BleServer.class);
                    //显示配对码
//                    showPairDialog();
//                    //移除上一次的超时
//                    handler.removeMessages(WHAT_BLE_LINK_TIMEOUT);
//                    //连接超时后将item状态更新为 normal
//                    Message message = new Message();
//                    message.what = WHAT_BLE_LINK_TIMEOUT;
//                    message.obj = position;
//                    handler.sendMessageDelayed(message, 30 * 1000);
                }
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
    private void showDisconnectDialog(final BleDevice bleDevice, final int type) {
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
                showRotation(false);
//                //取消定时器
//                cancelOpenTimer();
//                //清空list
//                list.clear();
//                //关闭蓝牙搜索，防止搜索到重连设备发起重连
//                BleAction.getInstance().stopDeviceSearch(BleServer.class);
                //断开连接
                BleAction.getInstance().closeDeviceLink(bleDevice,BleServer.class);
                //关闭所有蓝牙连接
                LinkFactory.disConnectAllDevice();
                listAdapter.setList(list);
                listAdapter.notifyDataSetChanged();
                //Switch开关，点击断开连接时，将开关置位false
                if (type == 2) {
                    closeSwitch();
                    btnSwitch.setChecked(false);
                }
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
//                //蓝牙连接超时
//                case WHAT_BLE_LINK_TIMEOUT:
//                    if (TextUtils.isEmpty(BLESerialManager.connected_mac)) {
//                        int position = (int) msg.obj;
//                        LogUtil.LOG_TOOTH("蓝牙连接超时", "item positon:" + position);
//                        //隐藏配对码dialog
//                        if (null != pairDialog) {
//                            pairDialog.dismiss();
//                        }
//                        showPairErrorDialog();
//                    }
//                    break;
                //开启蓝牙 搜索灶具
                case WHAT_START_SEARCH:
                    //开始搜索蓝牙
                    BleAction.getInstance().startDeviceSearch(BleServer.class, context);
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
    /**
     * Link 回调Action1
     */
    class LinkAction implements Action1<BleDevice> {
        @Override
        public synchronized void call(BleDevice bleDevice) {
                    int state = bleDevice.linkStatus;
                     LogUtil.LOG_TOOTH("----BleListActivity回调",state);
                    //连接中
                    if (state == ILinkObserverable.STATE_CONNECTING) {
                        notifyBleList(bleDevice);
                    }
                    //连接成功
                    if (state == ILinkObserverable.STATE_CONNECTED) {
                        notifyBleList(bleDevice);
                        //连接成功，dismiss提示框
                        closePairDialog();
                        addDefaultLinkedDevice(bleDevice);
                    }
                    //连接手动关闭-或者关闭灶具蓝牙
                    if (state == ILinkObserverable.STATE_NONE) {
                        notifyBleList(bleDevice);
                    }
                    //数据连接失败-传输失败
                    if (state == ILinkObserverable.STATE_ERROR) {
                        notifyBleList(bleDevice);
                    }
                    listAdapter.notifyItemLinkState(state, getPosition(bleDevice));
                }
            }
    /**
     * 在连接成功后，添加已经连接成功的蓝牙
     * 场景：用户重启蓝牙自连，还未连接成功时，用户进入到设备联动页面执行搜索
     * （搜索时，ble外围设备不广播，所以搜不到设备，列表中不会显示正在连接的设备）
     * 所以需要在连接成功后主动将ble设备添加到列表中
     */
    private boolean addDefaultLinkedDevice(BleDevice bleDevice) {
        String name = null == bleDevice ? "null" : bleDevice.getName();
        LogUtil.LOG_TOOTH("addDefaultLinkedDevice 连接成功后添加", name);
        if (null != bleDevice) {
            if (!listContain(bleDevice)) {
                list.add(bleDevice);
                listAdapter.setList(list);
                listAdapter.notifyDataSetChanged();
                //连接成功后，取消搜索，高丹提出的需求
                BleAction.getInstance().stopDeviceSearch(BleServer.class);
                //手动添加的对象有可能没有被搜索到，导致没有添加LinkAction
                ILinkObserverable linkObserverable = LinkFactory.getLinkObserverable(bleDevice, context);
                linkObserverable.addLinkObserver(linkAction);
                return true;
            }
        }
        return false;
    }
    /**
     * 获取Index信息
     *
     * @param bleDevice
     * @return
     */
    public int getPosition(BleDevice bleDevice) {
        String mac = bleDevice.getAddress();
        for (int k = 0; k < list.size(); k++) {
            BleDevice target = list.get(k);
            if (mac.equals(target.getAddress())) {
                return k;
            }
        }
        return -1;
    }
    /**
     * 更新列表
     *
     * @param bleDevice
     */
    private void notifyBleList(BleDevice bleDevice) {
        String target_address = bleDevice.getAddress();
        for (BleDevice device : list) {
            if (device.getAddress().equals(target_address)) {
                device.linkStatus = bleDevice.linkStatus;
                break;
            }
        }
        listAdapter.setList(list);
        LogUtil.LOG_TOOTH("----BleListActivity更新列表",target_address+"----"+list);
        listAdapter.notifyDataSetChanged();
    }
    /**
     * 显示配对码
     */
    public void showPairDialog() {
        if (null == pairDialog) {
            pairDialog = new CommonDialog(context, CommonDialog.CommonTip.ONE_BTN);
            pairDialog.setMessage("配对码：0000");
        }
        pairDialog.show();
    }
    /**
     * 关闭配对码弹框
     */
    public void closePairDialog() {
        if (null != pairDialog) {
            pairDialog.dismiss();
        }
    }


    /**
     * 添加已经连接成功的蓝牙
     * @return true 有已经连接成功设备，并且添加到列表成功
     *          false 无连接成功设备
     */
    private boolean addDefaultLinkedDevice() {
        //获取正在连接的设备
        BleDevice bleDevice = LinkFactory.getLinkDevice();
        String name = null == bleDevice ? "null" : bleDevice.getName();
        LogUtil.LOG_TOOTH("addDefaultLinkedDevice 进入页面添加", name);
        if (null != bleDevice) {
            if (!listContain(bleDevice)) {
                list.add(bleDevice);
                listAdapter.setList(list);
                listAdapter.notifyDataSetChanged();
                //连接成功后，取消搜索，高丹提出的需求
                BleAction.getInstance().stopDeviceSearch(BleServer.class);
                //手动添加的对象有可能没有被搜索到，导致没有添加LinkAction
                ILinkObserverable linkObserverable = LinkFactory.getLinkObserverable(bleDevice, context);
                linkObserverable.addLinkObserver(linkAction);
                return true;
            }
        }
        return false;
    }
}
