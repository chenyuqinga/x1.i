package com.fotile.x1i.dailog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.fotile.x1i.R;
import com.fotile.x1i.activity.setting.WifiListActivity;
import com.fotile.x1i.base.BaseDialog;

import java.util.List;

/**
 * 文件名称：NetWorkErrorDialog
 * 创建时间：2018/12/6 14:05
 * 文件作者：chenyqi
 * 功能描述：网络异常提示
 */


public class NetWorkErrorDialog extends BaseDialog {
    /**
     * 去看看
     */
    TextView tv_Go;
    /**
     * 取消
     */
    TextView tv_Cancle;
    private Context context;

    public NetWorkErrorDialog(Context context) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_net_error;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.dialog_net_error, null);
        setContentView(view);
        tv_Go = (TextView) findViewById(R.id.tv_Go);
        tv_Cancle = (TextView) findViewById(R.id.tv_Cancle);
        tv_Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WifiListActivity.class);
                context.startActivity(intent);
                dismiss();
            }
        });
        tv_Cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    /**
     * 注册监听网络状态变化
     */
    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(networkReceiver, filter);
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ((WifiManager.NETWORK_STATE_CHANGED_ACTION).equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.State state = networkInfo.getState();
                if (state == NetworkInfo.State.CONNECTED) {
                    dismiss();
                }
            }
        }
    };

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu,
            int deviceId) {

    }
}
