package fotile.ble.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import fotile.ble.R;
import fotile.ble.service.ExampleLinkServer;
@Deprecated
public class BleMainActivity extends BaseBleActivity {

//    Button button;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ble_main);
//
//        Intent intent = new Intent(this, ExampleLinkServer.class);
//        startService(intent);
//
//        button = (Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkBLEFeature();
//            }
//        });
//    }
//
//    /**
//     * 检查BLE是否起作用
//     */
//    private void checkBLEFeature() {
//        //判断是否支持蓝牙4.0
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, "不支持Ble", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        //获取蓝牙适配器
//        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        //判断是否支持蓝牙
//        if (bluetoothAdapter == null) {
//            //不支持
//            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            //判断是否已经打开
//            if (!bluetoothAdapter.isEnabled()) {
//                bluetoothAdapter.enable();
//                Toast.makeText(this, "请打开蓝牙", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Intent intent = new Intent(this, BleListActivity.class);
//            startActivity(intent);
//        }
//    }

}
