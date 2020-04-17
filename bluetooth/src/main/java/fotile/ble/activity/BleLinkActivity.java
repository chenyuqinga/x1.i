package fotile.ble.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fotile.ble.R;
import fotile.ble.bean.BleDevice;
import fotile.ble.observer.link.LinkFactory;
import fotile.ble.observer.link.ILinkObserverable;
import rx.functions.Action1;

import static fotile.ble.observer.link.ILinkObserverable.STATE_CONNECTED;
import static fotile.ble.observer.link.ILinkObserverable.STATE_CONNECTING;
import static fotile.ble.observer.link.ILinkObserverable.STATE_NONE;


/**
 * 项目名称：BleClient
 * 创建时间：2018/9/5 14:54
 * 文件作者：yaohx
 * 功能描述：蓝牙连接、蓝牙数据读写
 */
@Deprecated
public class BleLinkActivity extends BaseBleActivity {

//    private BleDevice bleDevice;
//    ILinkObserverable linkObserverable;
//
//    TextView txtName;
//    TextView txtStatus;
//
//    Button btnLink;
//    Button btnDisLink;
//    Button btnWrite;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ble_link);
//
//        bleDevice = getIntent().getParcelableExtra("bleDevice");
//
//        txtName = (TextView) findViewById(R.id.txt_ble_name);
//        txtStatus = (TextView) findViewById(R.id.txt_ble_status);
//        btnLink = (Button) findViewById(R.id.btn_link);
//        btnDisLink = (Button) findViewById(R.id.btn_dislink);
//        btnWrite = (Button) findViewById(R.id.btn_write);
//
//        btnLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                linkObserverable.connect();
//            }
//        });
//        btnDisLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                linkObserverable.disConnect();
//            }
//        });
//        btnWrite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                byte[] data = new byte[]{0x01, 0x02};
//                byte[] data = new byte[]{(byte) 0xf4, (byte) 0xf5, 0x00, 0x09, 0x05, 0x00, 0x01, 0x22, 0x33, 0x44,
//                        0x55, 0x00, 0x0C};
//                linkObserverable.write(data);
//            }
//        });
//        txtName.setText(bleDevice.getName());
//
//        initData();
//    }
//
//    private void initData() {
//        BleDecorator bleDecorator = new BleDecorator(bleDevice, actionLink);
//        linkObserverable = LinkFactory.createLinkObserverable(bleDevice, bleDecorator, this);
//    }
//
//    @Override
//    public void createAction() {
//        super.createAction();
//        actionLink = new Action1<BleDevice>() {
//            @Override
//            public void call(BleDevice bleDevice) {
//                if (null != bleDevice) {
//                    switch (bleDevice.linkStatus) {
//                        case STATE_NONE:
//                            txtStatus.setText("连接已断开");
//                            break;
//                        case STATE_CONNECTING:
//                            txtStatus.setText("连接中");
//                            break;
//                        case STATE_CONNECTED:
//                            txtStatus.setText("已连接");
//                            break;
//                    }
//                }
//            }
//        };
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        linkObserverable.disConnect();
//        linkObserverable.removeLinkObserver(actionLink);
//    }

}
