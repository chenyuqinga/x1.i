package fotile.ble.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fotile.ble.ListAdapter;
import fotile.ble.R;
import fotile.ble.bean.BleDevice;
import fotile.ble.observer.link.LinkFactory;
import fotile.ble.observer.search.SearchObserverable;
import rx.functions.Action1;

import static fotile.ble.observer.link.ILinkObserverable.STATE_CONNECTED;
import static fotile.ble.observer.link.ILinkObserverable.STATE_CONNECTING;
import static fotile.ble.observer.link.ILinkObserverable.STATE_ERROR;
import static fotile.ble.observer.link.ILinkObserverable.STATE_NONE;
import static fotile.ble.observer.search.SearchObserverable.SEARCH_TIME_NORMAL;

/**
 * 文件名称：BleListActivity
 * 创建时间：2018/9/4 11:18
 * 文件作者：yaohx
 * 功能描述：蓝牙搜索列表
 */
@Deprecated
public class BleListActivity extends BaseBleActivity implements AdapterView.OnItemClickListener {

//    ListView listView;
//    List<BleDevice> list = new ArrayList<BleDevice>();
//
//    SearchObserverable searchObserverable;
//    ListAdapter listAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ble_list);
//        listView = (ListView) findViewById(R.id.listview);
//
//        listAdapter = new ListAdapter(this);
//        listAdapter.setList(list);
//        listView.setAdapter(listAdapter);
//        listView.setOnItemClickListener(this);
//        initData();
//    }
//
//    private void initData() {
//        searchObserverable = SearchObserverable.getInstance(this);
//        searchObserverable.addSearchObserver(actionSearch);
//        searchObserverable.startBleSearch(SEARCH_TIME_NORMAL);
//    }
//
//    @Override
//    public void createAction() {
//        super.createAction();
//        actionSearch = new Action1<BleDevice>() {
//            @Override
//            public void call(BleDevice bleDevice) {
//                BluetoothDevice bluetoothDevice = bleDevice.bluetoothDevice;
//                if (null != bluetoothDevice && !listContain(bleDevice)) {
//                    list.add(bleDevice);
//                    listAdapter.setList(list);
//                    listAdapter.notifyDataSetChanged();
//                }
//            }
//        };
//
//    }
//
//    private boolean listContain(BleDevice bleDevice) {
//        if (null != bleDevice && null != bleDevice.bluetoothDevice) {
//            String name = bleDevice.getName();
//            if (!TextUtils.isEmpty(name)) {
//                for (BleDevice b : list) {
//                    if (name.equals(b.getName())) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        BleDevice bleDevice = list.get(position);
//        if (null != bleDevice) {
//            Intent intent = new Intent(this, BleLinkActivity.class);
//            intent.putExtra("bleDevice", bleDevice);
//            startActivity(intent);
//        }
    }
//
//    public void notifyBleList(BleDevice bleDevice) {
//        String target_address = bleDevice.getAddress();
//        for (BleDevice device : list) {
//            if (device.getAddress().equals(target_address)) {
//                device.linkStatus = bleDevice.linkStatus;
//                break;
//            }
//        }
//        listAdapter.setList(list);
//        listAdapter.notifyDataSetChanged();
//
//    }
//
//    //Link 回调Action1
//    class LinkAction implements Action1<BleDevice> {
//        @Override
//        public void call(BleDevice bleDevice) {
//            int state = bleDevice.linkStatus;
//            //连接中
//            if (state == STATE_CONNECTING) {
//                notifyBleList(bleDevice);
//            }
//            //连接成功
//            if (state == STATE_CONNECTED) {
//                notifyBleList(bleDevice);
//            }
//            //连接手动关闭-或者关闭灶具蓝牙
//            if (state == STATE_NONE) {
//                notifyBleList(bleDevice);
//            }
//            //数据连接失败-传输失败
//            if (state == STATE_ERROR) {
//                notifyBleList(bleDevice);
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //停止搜索
//        searchObserverable.stopBleSearch();
//        searchObserverable.removeSearchObserver(actionSearch);
//        //关闭所有蓝牙连接
//        LinkFactory.disConnectAllDevice();
//    }
}
